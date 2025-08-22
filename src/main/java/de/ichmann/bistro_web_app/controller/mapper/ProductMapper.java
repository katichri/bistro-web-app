package de.ichmann.bistro_web_app.controller.mapper;

import de.ichmann.bistro_web_app.api.model.ProductAPIDTO;
import de.ichmann.bistro_web_app.data.internal.ProductDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class ProductMapper {
    public static List<ProductAPIDTO> toProductAPIDTOList(List<ProductDTO> productDTOList) {
        return productDTOList.stream()
                .map(ProductMapper::toProductAPIDTO)
                .toList();
    }

    public static ProductAPIDTO toProductAPIDTO(ProductDTO productDTO) {
        return new ProductAPIDTO()
                .id(productDTO.id())
                .name(productDTO.name())
                .price(productDTO.price())
                .externalId(productDTO.externalId());
    }

}
