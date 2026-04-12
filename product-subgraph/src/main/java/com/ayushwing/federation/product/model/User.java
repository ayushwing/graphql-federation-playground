package com.ayushwing.federation.product.model;

/**
 * Federation stub for the User entity owned by user-subgraph.
 *
 * This class exists solely so graphql-java's ClassNameTypeResolver can map
 * it to the "User" GraphQL type in the _Entity union. It carries only the
 * @key field (id) needed to satisfy User.products lookups in this subgraph.
 */
public record User(String id) {}
