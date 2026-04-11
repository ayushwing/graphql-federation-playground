package com.ayushwing.federation.product.repository;

import com.ayushwing.federation.product.model.Product;
import com.ayushwing.federation.product.model.ProductFilter;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * In-memory product store seeded with demo data across several categories.
 */
@Repository
public class ProductRepository {

    private final Map<String, Product> store = new ConcurrentHashMap<>();

    public ProductRepository() {
        seed("p1", "Laptop Pro 16", "High-performance laptop for developers", 1499.99, "electronics", true, "u1", "2024-01-15T10:00:00Z");
        seed("p2", "Wireless Headphones", "Noise-cancelling over-ear headphones", 89.99, "electronics", true, "u2", "2024-01-20T11:00:00Z");
        seed("p3", "Mechanical Keyboard", "TKL mechanical keyboard with Cherry MX switches", 149.99, "electronics", true, "u1", "2024-02-01T09:00:00Z");
        seed("p4", "Standing Desk", "Electric height-adjustable standing desk", 649.00, "furniture", true, "u3", "2024-02-10T14:00:00Z");
        seed("p5", "Ergonomic Chair", "Lumbar-support office chair", 499.00, "furniture", false, "u3", "2024-02-15T16:00:00Z");
        seed("p6", "4K Monitor", "27-inch 4K IPS display", 399.99, "electronics", true, "u2", "2024-03-01T08:00:00Z");
        seed("p7", "USB-C Hub", "7-in-1 USB-C multiport adapter", 49.99, "electronics", true, "u4", "2024-03-10T10:30:00Z");
        seed("p8", "Bookshelf", "Solid oak 5-shelf bookcase", 229.00, "furniture", true, "u4", "2024-03-20T12:00:00Z");
    }

    private void seed(String id, String name, String description, double price,
                      String category, boolean inStock, String createdById, String createdAt) {
        store.put(id, new Product(id, name, description, price, category, inStock, createdById, createdAt));
    }

    public Optional<Product> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }

    public List<Product> findAll() {
        return new ArrayList<>(store.values());
    }

    public List<Product> findByCreatedById(String userId) {
        return store.values().stream()
                .filter(p -> userId.equals(p.getCreatedById()))
                .collect(Collectors.toList());
    }

    public List<Product> findByCategory(String category) {
        return store.values().stream()
                .filter(p -> category.equalsIgnoreCase(p.getCategory()))
                .collect(Collectors.toList());
    }

    /**
     * Returns products matching all non-null filter criteria (AND semantics).
     */
    public List<Product> findByFilter(ProductFilter filter) {
        return store.values().stream()
                .filter(p -> filter.getCategory() == null
                        || filter.getCategory().equalsIgnoreCase(p.getCategory()))
                .filter(p -> filter.getMinPrice() == null
                        || p.getPrice() >= filter.getMinPrice())
                .filter(p -> filter.getMaxPrice() == null
                        || p.getPrice() <= filter.getMaxPrice())
                .filter(p -> filter.getInStock() == null
                        || p.isInStock() == filter.getInStock())
                .filter(p -> filter.getNameContains() == null
                        || p.getName().toLowerCase().contains(filter.getNameContains().toLowerCase()))
                .collect(Collectors.toList());
    }

    public Product save(Product product) {
        store.put(product.getId(), product);
        return product;
    }
}
