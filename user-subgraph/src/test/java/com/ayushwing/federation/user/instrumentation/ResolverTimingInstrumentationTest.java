package com.ayushwing.federation.user.instrumentation;

import com.netflix.graphql.dgs.DgsQueryExecutor;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Verifies the resolver-timing instrumentation registers timers for executed
 * queries and field fetches against the live Micrometer registry.
 */
@SpringBootTest
class ResolverTimingInstrumentationTest {

    @Autowired
    DgsQueryExecutor dgsQueryExecutor;

    @Autowired
    MeterRegistry meterRegistry;

    @Test
    void recordsQueryAndResolverTimers() {
        dgsQueryExecutor.executeAndExtractJsonPath(
                "{ user(id: \"u1\") { name } }",
                "data.user.name"
        );

        Timer queryTimer = meterRegistry.find("graphql.query.duration").timer();
        assertThat(queryTimer).isNotNull();
        assertThat(queryTimer.count()).isGreaterThanOrEqualTo(1);

        Timer userResolver = meterRegistry.find("graphql.resolver.duration")
                .tag("parentType", "Query")
                .tag("field", "user")
                .timer();
        assertThat(userResolver).isNotNull();
        assertThat(userResolver.count()).isGreaterThanOrEqualTo(1);
    }
}
