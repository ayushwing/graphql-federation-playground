package com.ayushwing.federation.product.resolver;

import com.ayushwing.federation.product.model.Product;
import com.ayushwing.federation.product.repository.ProductRepository;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsData;

import graphql.schema.DataFetchingEnvironment;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Field-level resolvers for types that reference federation entities.
 *
 * <p>{@code Product.createdBy} returns a stub User map containing just the
 * {@code id} key. Apollo Router uses that key to fetch the full User from
 * the user-subgraph via the {@code _entities} query.
 *
 * <p>{@code User.products} resolves all products created by a given User — this
 * field is defined in this subgraph's schema as an extension to the User type.
 */
@DgsComponent
public class ProductFieldResolver {

    private final ProductRepository productRepository;

    public ProductFieldResolver(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    /**
     * Resolves {@code Product.createdBy} as a federation stub pointing to the
     * user-subgraph entity. Apollo Router replaces this with the full User.
     */
    @DgsData(parentType = "Product", field = "createdBy")
    public Map<String, Object> createdBy(DataFetchingEnvironment env) {
        Product product = env.getSource();
        // Return a representation map — Apollo Router uses __typename + @key fields
        // to route the _entities query to the correct subgraph.
        Map<String, Object> userRef = new HashMap<>();
        userRef.put("__typename", "User");
        userRef.put("id", product.getCreatedById());
        return userRef;
    }

    /**
     * Resolves {@code User.products} — all products created by the given User.
     * The User is a federation stub carrying only its {@code id}.
     */
    @DgsData(parentType = "User", field = "products")
    public List<Product> userProducts(DataFetchingEnvironment env) {
        Map<String, Object> user = env.getSource();
        String userId = (String) user.get("id");
        return productRepository.findByCreatedById(userId);
    }
}
