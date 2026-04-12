package com.ayushwing.federation.review.dataloader;

import com.ayushwing.federation.review.model.Review;
import com.ayushwing.federation.review.repository.ReviewRepository;

import com.netflix.graphql.dgs.DgsDataLoader;

import org.dataloader.BatchLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * DataLoader that batch-fetches reviews for multiple products in one shot.
 *
 * <p>Without a DataLoader, resolving {@code reviews} on each Product in a list
 * would fire N individual queries — one per product. The DataLoader collects all
 * product IDs requested in a single GraphQL execution tick, issues ONE
 * {@code findByProductIds} call to the repository, then fans the results back out.
 *
 * <p>Registered as a Spring bean by DGS via {@link DgsDataLoader}. The name
 * {@code "reviewsByProduct"} is used to look it up in resolvers.
 */
@DgsDataLoader(name = "reviewsByProduct")
public class ReviewsByProductDataLoader implements BatchLoader<String, List<Review>> {

    private static final Logger log = LoggerFactory.getLogger(ReviewsByProductDataLoader.class);

    private final ReviewRepository reviewRepository;
    private final Executor executor = Executors.newCachedThreadPool();

    public ReviewsByProductDataLoader(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    /**
     * Receives all product IDs requested in this execution tick and returns
     * a future that resolves with one list of reviews per product ID,
     * in the same order as the input keys.
     */
    @Override
    public CompletionStage<List<List<Review>>> load(List<String> productIds) {
        return CompletableFuture.supplyAsync(() -> {
            log.debug("DataLoader batch: fetching reviews for {} products: {}",
                    productIds.size(), productIds);

            Map<String, List<Review>> byProduct =
                    reviewRepository.findByProductIds(Set.copyOf(productIds));

            // Return results in the same order as input keys
            return productIds.stream()
                    .map(id -> byProduct.getOrDefault(id, List.of()))
                    .toList();
        }, executor);
    }
}
