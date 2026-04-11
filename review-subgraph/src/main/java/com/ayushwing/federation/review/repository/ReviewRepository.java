package com.ayushwing.federation.review.repository;

import com.ayushwing.federation.review.model.Review;

import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * In-memory review store. Indexed by both review ID and product ID
 * for efficient batch lookups (used by the DataLoader).
 */
@Repository
public class ReviewRepository {

    private final Map<String, Review> byId = new ConcurrentHashMap<>();
    // secondary index: productId → list of review IDs
    private final Map<String, List<String>> byProduct = new ConcurrentHashMap<>();

    public ReviewRepository() {
        seed("r1", 5, "Incredibly fast — worth every penny.", "u2", "p1");
        seed("r2", 4, "Great laptop, runs hot under load.", "u3", "p1");
        seed("r3", 5, "Best headphones I've owned.", "u1", "p2");
        seed("r4", 3, "Good sound but the ear cups wear out.", "u4", "p2");
        seed("r5", 5, "Excellent switches, perfect for coding.", "u2", "p3");
        seed("r6", 4, "Solid desk, easy to assemble.", "u1", "p4");
        seed("r7", 2, "Wobbly legs at standing height.", "u3", "p4");
        seed("r8", 5, "Sharp picture, colours are accurate.", "u4", "p6");
        seed("r9", 4, "Compact and reliable hub.", "u1", "p7");
    }

    private void seed(String id, int rating, String comment, String authorId, String productId) {
        Review review = new Review(id, rating, comment, authorId, productId,
                "2024-0" + id.charAt(1) + "-01T12:00:00Z");
        store(review);
    }

    private void store(Review review) {
        byId.put(review.getId(), review);
        byProduct.computeIfAbsent(review.getProductId(), k -> new ArrayList<>())
                 .add(review.getId());
    }

    public Optional<Review> findById(String id) {
        return Optional.ofNullable(byId.get(id));
    }

    public List<Review> findByProductId(String productId) {
        List<String> ids = byProduct.getOrDefault(productId, List.of());
        return ids.stream()
                  .map(byId::get)
                  .collect(Collectors.toList());
    }

    /**
     * Batch fetch reviews for multiple product IDs in a single call.
     * Used by the DataLoader to avoid N+1 queries.
     */
    public Map<String, List<Review>> findByProductIds(Set<String> productIds) {
        return productIds.stream()
                .collect(Collectors.toMap(
                        id -> id,
                        this::findByProductId
                ));
    }

    public List<Review> findByAuthorId(String authorId) {
        return byId.values().stream()
                .filter(r -> authorId.equals(r.getAuthorId()))
                .collect(Collectors.toList());
    }

    public Review save(Review review) {
        if (review.getId() == null) {
            review.setId(UUID.randomUUID().toString());
        }
        if (review.getCreatedAt() == null) {
            review.setCreatedAt(Instant.now().toString());
        }
        store(review);
        return review;
    }

    public boolean delete(String id) {
        Review removed = byId.remove(id);
        if (removed != null) {
            List<String> productReviews = byProduct.get(removed.getProductId());
            if (productReviews != null) {
                productReviews.remove(id);
            }
            return true;
        }
        return false;
    }
}
