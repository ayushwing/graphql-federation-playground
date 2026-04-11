package com.ayushwing.federation.review.resolver;

import com.ayushwing.federation.review.model.Review;
import com.ayushwing.federation.review.repository.ReviewRepository;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsData;

import graphql.schema.DataFetchingEnvironment;

import org.dataloader.DataLoader;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.OptionalDouble;
import java.util.concurrent.CompletableFuture;

/**
 * Field-level resolvers for Review, Product (extended), and User (extended).
 *
 * <p>The {@code Product.reviews} field uses the {@link com.ayushwing.federation.review.dataloader.ReviewsByProductDataLoader}
 * to batch all product review fetches in a single execution into one repository call,
 * preventing N+1 queries when a list of products is resolved.
 */
@DgsComponent
public class ReviewFieldResolver {

    private final ReviewRepository reviewRepository;

    public ReviewFieldResolver(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    /**
     * Resolves {@code Review.author} as a federation stub.
     * Apollo Router fetches the full User from user-subgraph via _entities.
     */
    @DgsData(parentType = "Review", field = "author")
    public Map<String, Object> author(DataFetchingEnvironment env) {
        Review review = env.getSource();
        Map<String, Object> ref = new HashMap<>();
        ref.put("__typename", "User");
        ref.put("id", review.getAuthorId());
        return ref;
    }

    /**
     * Resolves {@code Review.product} as a federation stub.
     * Apollo Router fetches the full Product from product-subgraph via _entities.
     */
    @DgsData(parentType = "Review", field = "product")
    public Map<String, Object> product(DataFetchingEnvironment env) {
        Review review = env.getSource();
        Map<String, Object> ref = new HashMap<>();
        ref.put("__typename", "Product");
        ref.put("id", review.getProductId());
        return ref;
    }

    /**
     * Resolves {@code Product.reviews} using the DataLoader to batch-fetch.
     *
     * <p>When the gateway asks for reviews on a list of products, DGS calls
     * this method once per product but the DataLoader coalesces all those calls
     * into a single {@code findByProductIds} call to the repository.
     */
    @DgsData(parentType = "Product", field = "reviews")
    public CompletableFuture<List<Review>> productReviews(DataFetchingEnvironment env) {
        Map<String, Object> product = env.getSource();
        String productId = (String) product.get("id");

        DataLoader<String, List<Review>> dataLoader =
                env.getDataLoader("reviewsByProduct");
        return dataLoader.load(productId);
    }

    @DgsData(parentType = "Product", field = "averageRating")
    public Double averageRating(DataFetchingEnvironment env) {
        Map<String, Object> product = env.getSource();
        String productId = (String) product.get("id");
        List<Review> reviews = reviewRepository.findByProductId(productId);
        if (reviews.isEmpty()) return null;
        OptionalDouble avg = reviews.stream().mapToInt(Review::getRating).average();
        return avg.isPresent() ? Math.round(avg.getAsDouble() * 10.0) / 10.0 : null;
    }

    @DgsData(parentType = "Product", field = "reviewCount")
    public int reviewCount(DataFetchingEnvironment env) {
        Map<String, Object> product = env.getSource();
        String productId = (String) product.get("id");
        return reviewRepository.findByProductId(productId).size();
    }

    /**
     * Resolves {@code User.reviews} — all reviews written by this User.
     */
    @DgsData(parentType = "User", field = "reviews")
    public List<Review> userReviews(DataFetchingEnvironment env) {
        Map<String, Object> user = env.getSource();
        String userId = (String) user.get("id");
        return reviewRepository.findByAuthorId(userId);
    }
}
