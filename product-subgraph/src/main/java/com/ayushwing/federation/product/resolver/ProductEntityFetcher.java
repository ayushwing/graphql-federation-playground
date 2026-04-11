package com.ayushwing.federation.product.resolver;

import com.ayushwing.federation.product.model.Product;
import com.ayushwing.federation.product.repository.ProductRepository;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsEntityFetcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Federation entity fetcher for the {@code Product} type.
 *
 * <p>Called by Apollo Router when the review-subgraph (or any other subgraph)
 * references a Product by its {@code id} key in an {@code _entities} query.
 */
@DgsComponent
public class ProductEntityFetcher {

    private static final Logger log = LoggerFactory.getLogger(ProductEntityFetcher.class);

    private final ProductRepository productRepository;

    public ProductEntityFetcher(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @DgsEntityFetcher(name = "Product")
    public Product fetchProductById(Map<String, Object> values) {
        String id = (String) values.get("id");
        log.debug("Federation entity fetch: Product(id={})", id);
        return productRepository.findById(id).orElse(null);
    }
}
