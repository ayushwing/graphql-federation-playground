package com.ayushwing.federation.product.model;

/**
 * Product entity — owned by this subgraph.
 *
 * <p>The {@code createdById} field stores a reference to a User by ID.
 * The actual {@code User} object is resolved via federation (Apollo Router
 * fetches it from the user-subgraph using the {@code @key} entity mechanism).
 */
public class Product {

    private String id;
    private String name;
    private String description;
    private double price;
    private String category;
    private boolean inStock;
    private String createdById;   // federation reference to User
    private String createdAt;

    public Product() {}

    public Product(String id, String name, String description, double price,
                   String category, boolean inStock, String createdById, String createdAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.category = category;
        this.inStock = inStock;
        this.createdById = createdById;
        this.createdAt = createdAt;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public boolean isInStock() { return inStock; }
    public void setInStock(boolean inStock) { this.inStock = inStock; }

    public String getCreatedById() { return createdById; }
    public void setCreatedById(String createdById) { this.createdById = createdById; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}
