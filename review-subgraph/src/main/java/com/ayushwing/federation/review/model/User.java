package com.ayushwing.federation.review.model;

/**
 * Federation stub for the User entity owned by user-subgraph.
 *
 * Carries only the @key field (id) needed for User.reviews lookups.
 * The simple class name "User" lets graphql-java resolve it to the
 * "User" GraphQL type in the _Entity union.
 */
public record User(String id) {}
