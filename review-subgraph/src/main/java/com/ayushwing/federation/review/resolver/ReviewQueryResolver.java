package com.ayushwing.federation.review.resolver;

import com.ayushwing.federation.review.model.Review;
import com.ayushwing.federation.review.repository.ReviewRepository;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsQuery;
import com.netflix.graphql.dgs.InputArgument;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@DgsComponent
public class ReviewQueryResolver {

    private static final Logger log = LoggerFactory.getLogger(ReviewQueryResolver.class);

    private final ReviewRepository reviewRepository;

    public ReviewQueryResolver(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    @DgsQuery
    public List<Review> reviews(@InputArgument String productId) {
        log.debug("Resolving reviews(productId={})", productId);
        return reviewRepository.findByProductId(productId);
    }

    @DgsQuery
    public Review review(@InputArgument String id) {
        return reviewRepository.findById(id).orElse(null);
    }

    @DgsQuery
    public List<Review> myReviews() {
        // TODO: resolve author from auth context — stubbed to u1 for demo
        return reviewRepository.findByAuthorId("u1");
    }
}
