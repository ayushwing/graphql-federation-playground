package com.ayushwing.federation.review.resolver;

import com.ayushwing.federation.review.model.Product;
import com.ayushwing.federation.review.model.Review;
import com.ayushwing.federation.review.model.User;
import com.ayushwing.federation.review.repository.ReviewRepository;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsData;
import com.netflix.graphql.dgs.DgsEntityFetcher;

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
     * Entity fetchers for the federation stubs declared in this subgraph.
     * Apollo Router sends _entities queries here to resolve Product.reviews
     * and User.reviews. We echo the key map back — DGS uses it as the
     * source for the field resolvers below.
     */
    @DgsEntityFetcher(name = "Product")
    public Product fetchProductEntity(Map<String, Object> values) {
        return new Product((String) values.get("id"));
    }

    @DgsEntityFetcher(name = "User")
    public User fetchUserEntity(Map<String, Object> values) {
        return new User((String) values.get("id"));
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
        Product product = env.getSource();
        DataLoader<String, List<Review>> dataLoader = env.getDataLoader("reviewsByProduct");
        return dataLoader.load(product.id());
    }

    @DgsData(parentType = "Product", field = "averageRating")
    public Double averageRating(DataFetchingEnvironment env) {
        Product product = env.getSource();
        List<Review> reviews = reviewRepository.findByProductId(product.id());
        if (reviews.isEmpty()) return null;
        OptionalDouble avg = reviews.stream().mapToInt(Review::getRating).average();
        return avg.isPresent() ? Math.round(avg.getAsDouble() * 10.0) / 10.0 : null;
    }

    @DgsData(parentType = "Product", field = "reviewCount")
    public int reviewCount(DataFetchingEnvironment env) {
        Product product = env.getSource();
        return reviewRepository.findByProductId(product.id()).size();
    }

    /**
     * Resolves {@code User.reviews} — all reviews written by this User.
     */
    @DgsData(parentType = "User", field = "reviews")
    public List<Review> userReviews(DataFetchingEnvironment env) {
        User user = env.getSource();
        return reviewRepository.findByAuthorId(user.id());
    }
}
