package com.ayushwing.federation.product.model;

/**
 * Input filter for product list queries.
 * Maps to the {@code ProductFilter} GraphQL input type.
 */
public class ProductFilter {

    private String category;
    private Double minPrice;
    private Double maxPrice;
    private Boolean inStock;
    private String nameContains;

    public ProductFilter() {}

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public Double getMinPrice() { return minPrice; }
    public void setMinPrice(Double minPrice) { this.minPrice = minPrice; }

    public Double getMaxPrice() { return maxPrice; }
    public void setMaxPrice(Double maxPrice) { this.maxPrice = maxPrice; }

    public Boolean getInStock() { return inStock; }
    public void setInStock(Boolean inStock) { this.inStock = inStock; }

    public String getNameContains() { return nameContains; }
    public void setNameContains(String nameContains) { this.nameContains = nameContains; }
}
