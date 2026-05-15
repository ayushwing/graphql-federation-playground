package com.ayushwing.federation.review.instrumentation;

import graphql.ExecutionResult;
import graphql.execution.instrumentation.InstrumentationContext;
import graphql.execution.instrumentation.InstrumentationState;
import graphql.execution.instrumentation.SimpleInstrumentationContext;
import graphql.execution.instrumentation.SimplePerformantInstrumentation;
import graphql.execution.instrumentation.parameters.InstrumentationExecutionParameters;
import graphql.execution.instrumentation.parameters.InstrumentationFieldFetchParameters;
import graphql.schema.GraphQLNamedType;
import graphql.schema.GraphQLType;
import graphql.schema.GraphQLTypeUtil;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * Times every resolver invocation and emits Micrometer metrics.
 *
 * <p>Metrics emitted:
 * <ul>
 *   <li>{@code graphql.resolver.duration} — timer per (parentType, field, status)</li>
 *   <li>{@code graphql.query.duration} — timer per (operation, status) for the whole execution</li>
 * </ul>
 *
 * <p>Logs any field resolution slower than {@code observability.graphql.slow-threshold-ms}
 * at WARN — useful for spotting N+1 patterns and slow repository calls in dev.
 */
@Component
public class ResolverTimingInstrumentation extends SimplePerformantInstrumentation {

    private static final Logger log = LoggerFactory.getLogger(ResolverTimingInstrumentation.class);

    private final MeterRegistry meterRegistry;
    private final long slowThresholdMs;

    public ResolverTimingInstrumentation(
            MeterRegistry meterRegistry,
            @Value("${observability.graphql.slow-threshold-ms:50}") long slowThresholdMs) {
        this.meterRegistry = meterRegistry;
        this.slowThresholdMs = slowThresholdMs;
    }

    @Override
    public InstrumentationContext<Object> beginFieldFetch(
            InstrumentationFieldFetchParameters parameters,
            InstrumentationState state) {
        long start = System.nanoTime();
        String parentType = unwrappedTypeName(parameters.getExecutionStepInfo().getParent().getType());
        String fieldName = parameters.getField().getName();
        return SimpleInstrumentationContext.whenCompleted((result, throwable) -> {
            long elapsedNanos = System.nanoTime() - start;
            Timer.builder("graphql.resolver.duration")
                    .description("Time spent resolving a GraphQL field")
                    .tag("parentType", parentType)
                    .tag("field", fieldName)
                    .tag("status", throwable != null ? "error" : "ok")
                    .register(meterRegistry)
                    .record(elapsedNanos, TimeUnit.NANOSECONDS);
            long elapsedMs = TimeUnit.NANOSECONDS.toMillis(elapsedNanos);
            if (elapsedMs >= slowThresholdMs) {
                log.warn("slow resolver {}.{} took {} ms (threshold={} ms)",
                        parentType, fieldName, elapsedMs, slowThresholdMs);
            }
        });
    }

    private static String unwrappedTypeName(GraphQLType type) {
        GraphQLType unwrapped = GraphQLTypeUtil.unwrapAll(type);
        return unwrapped instanceof GraphQLNamedType named ? named.getName() : "unknown";
    }

    @Override
    public InstrumentationContext<ExecutionResult> beginExecution(
            InstrumentationExecutionParameters parameters,
            InstrumentationState state) {
        long start = System.nanoTime();
        String operationName = parameters.getOperation() != null ? parameters.getOperation() : "anonymous";
        return SimpleInstrumentationContext.whenCompleted((result, throwable) -> {
            long elapsedNanos = System.nanoTime() - start;
            boolean errored = throwable != null || (result != null && !result.getErrors().isEmpty());
            Timer.builder("graphql.query.duration")
                    .description("Total GraphQL query execution time")
                    .tag("operation", operationName)
                    .tag("status", errored ? "error" : "ok")
                    .register(meterRegistry)
                    .record(elapsedNanos, TimeUnit.NANOSECONDS);
        });
    }
}
