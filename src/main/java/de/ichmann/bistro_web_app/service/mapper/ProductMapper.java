package de.ichmann.bistro_web_app.service.mapper;

import de.ichmann.bistro_web_app.data.internal.ProductDTO;
import de.ichmann.bistro_web_app.entity.ProductEntity;

public class ProductMapper {
    private ProductMapper() {
    }

    public static ProductDTO toProductDTO(ProductEntity product) {
        return new ProductDTO(
                product.getName(),
                product.getExternalId(),
                product.getPrice(),
                product.getId()
        );
    }

}
