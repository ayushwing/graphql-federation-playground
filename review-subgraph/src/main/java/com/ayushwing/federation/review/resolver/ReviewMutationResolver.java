package com.ayushwing.federation.review.resolver;

import com.ayushwing.federation.review.model.Review;
import com.ayushwing.federation.review.repository.ReviewRepository;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsMutation;
import com.netflix.graphql.dgs.InputArgument;

import graphql.schema.DataFetchingEnvironment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.Map;

@DgsComponent
public class ReviewMutationResolver {

    private static final Logger log = LoggerFactory.getLogger(ReviewMutationResolver.class);

    private final ReviewRepository reviewRepository;

    public ReviewMutationResolver(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    @DgsMutation
    public Review createReview(@InputArgument Map<String, Object> input,
                               DataFetchingEnvironment env) {
        String productId = (String) input.get("productId");
        int rating = (int) input.get("rating");
        String comment = (String) input.get("comment");

        // TODO: extract authorId from JWT context — stubbed to u1 for demo
        String authorId = "u1";

        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }

        Review review = new Review(null, rating, comment, authorId, productId, null);
        Review saved = reviewRepository.save(review);
        log.info("Created review {} for product {} by user {}", saved.getId(), productId, authorId);
        return saved;
    }

    @DgsMutation
    public boolean deleteReview(@InputArgument String id) {
        boolean deleted = reviewRepository.delete(id);
        if (deleted) {
            log.info("Deleted review {}", id);
        }
        return deleted;
    }
}
