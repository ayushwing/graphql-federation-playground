package com.ayushwing.federation.product.resolver;

import com.ayushwing.federation.product.model.Product;
import com.ayushwing.federation.product.model.ProductConnection;
import com.ayushwing.federation.product.model.ProductFilter;
import com.ayushwing.federation.product.repository.ProductRepository;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsQuery;
import com.netflix.graphql.dgs.InputArgument;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * DGS query resolvers for the Product type.
 *
 * <p>Handles {@code product}, {@code products} (with pagination + filtering),
 * and {@code productsByCategory} queries.
 */
@DgsComponent
public class ProductQueryResolver {

    private static final Logger log = LoggerFactory.getLogger(ProductQueryResolver.class);

    private final ProductRepository productRepository;

    public ProductQueryResolver(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @DgsQuery
    public Product product(@InputArgument String id) {
        log.debug("Resolving product(id={})", id);
        return productRepository.findById(id).orElse(null);
    }

    /**
     * Paginated, filterable product list.
     *
     * <p>Page and size are zero-indexed (page 0 = first page).
     * Filter criteria are AND-combined — all non-null conditions must match.
     */
    @DgsQuery
    public ProductConnection products(
            @InputArgument ProductFilter filter,
            @InputArgument Integer page,
            @InputArgument Integer size) {

        int pageNum = page != null ? page : 0;
        int pageSize = size != null ? Math.min(size, 100) : 20; // cap at 100

        List<Product> all = filter != null
                ? productRepository.findByFilter(filter)
                : productRepository.findAll();

        int totalCount = all.size();
        int fromIndex = pageNum * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, totalCount);

        if (fromIndex >= totalCount) {
            return new ProductConnection(List.of(), totalCount, false, pageNum > 0);
        }

        List<Product> page_ = all.subList(fromIndex, toIndex);
        boolean hasNext = toIndex < totalCount;
        boolean hasPrev = pageNum > 0;

        log.debug("products(filter={}, page={}, size={}) → {} of {} total",
                filter, pageNum, pageSize, page_.size(), totalCount);

        return new ProductConnection(page_, totalCount, hasNext, hasPrev);
    }

    @DgsQuery
    public List<Product> productsByCategory(@InputArgument String category) {
        log.debug("Resolving productsByCategory(category={})", category);
        return productRepository.findByCategory(category);
    }
}
