package com.ayushwing.federation.review.model;

/**
 * Review entity — owned by this subgraph.
 *
 * <p>{@code authorId} and {@code productId} are federation reference keys.
 * The full {@code User} and {@code Product} objects are resolved via entity
 * fetchers in their respective subgraphs.
 */
public class Review {

    private String id;
    private int rating;
    private String comment;
    private String authorId;    // federation ref → user-subgraph
    private String productId;   // federation ref → product-subgraph
    private String createdAt;

    public Review() {}

    public Review(String id, int rating, String comment,
                  String authorId, String productId, String createdAt) {
        this.id = id;
        this.rating = rating;
        this.comment = comment;
        this.authorId = authorId;
        this.productId = productId;
        this.createdAt = createdAt;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public String getAuthorId() { return authorId; }
    public void setAuthorId(String authorId) { this.authorId = authorId; }

    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}
