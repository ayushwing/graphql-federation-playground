package com.ayushwing.federation.review.model;

/**
 * Federation stub for the Product entity owned by product-subgraph.
 *
 * Carries only the @key field (id) needed for Product.reviews lookups.
 * The simple class name "Product" lets graphql-java resolve it to the
 * "Product" GraphQL type in the _Entity union.
 */
public record Product(String id) {}
