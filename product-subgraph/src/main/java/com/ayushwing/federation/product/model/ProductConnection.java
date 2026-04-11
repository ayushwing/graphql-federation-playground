package com.ayushwing.federation.product.model;

import java.util.List;

/**
 * Cursor-style pagination wrapper for product lists.
 * Mirrors the {@code ProductConnection} GraphQL type in the schema.
 */
public class ProductConnection {

    private final List<Product> nodes;
    private final int totalCount;
    private final boolean hasNextPage;
    private final boolean hasPreviousPage;

    public ProductConnection(List<Product> nodes, int totalCount,
                             boolean hasNextPage, boolean hasPreviousPage) {
        this.nodes = nodes;
        this.totalCount = totalCount;
        this.hasNextPage = hasNextPage;
        this.hasPreviousPage = hasPreviousPage;
    }

    public List<Product> getNodes() { return nodes; }
    public int getTotalCount() { return totalCount; }
    public boolean isHasNextPage() { return hasNextPage; }
    public boolean isHasPreviousPage() { return hasPreviousPage; }
}
