package de.ichmann.bistro_web_app.service;

import de.ichmann.bistro_web_app.entity.ProductEntity;
import de.ichmann.bistro_web_app.repository.ProductRepository;
import de.ichmann.bistro_web_app.data.internal.ProductDTO;
import de.ichmann.bistro_web_app.service.mapper.OrderMapper;
import de.ichmann.bistro_web_app.service.mapper.ProductMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.function.Function;

@Slf4j
@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional
    public void mergeProduct(ProductDTO productToBeImported) {

        productRepository.findByExternalId(productToBeImported.externalId())
            .ifPresentOrElse(
                existingProduct -> {
                    log.info("Merging existing product: {}", existingProduct);
                    // Update existing product logic
                    existingProduct.setName(productToBeImported.name());
                    existingProduct.setPrice(productToBeImported.price());
                    productRepository.save(existingProduct);
                },
                () -> {
                    log.info("Creating new product: {}", productToBeImported);
                    var newProduct = ProductEntity.builder()
                            .name(productToBeImported.name())
                            .externalId(productToBeImported.externalId())
                            .price(productToBeImported.price())
                            .build();
                    productRepository.save(newProduct);
                }
            );

    }

    public List<ProductDTO> getProducts() {
        log.info("Fetching all products");
        return productRepository.findAll().stream()
                .map(ProductMapper::toProductDTO)
                .toList();
    }

    public ProductDTO getProductById(Long id) {
        log.info("Fetching product by ID: {}", id);
        return  productRepository.findById(id)
                .map(ProductMapper::toProductDTO)
                .orElseThrow(()-> new EntityNotFoundException("Product not found with ID: " + id));
    }

}
