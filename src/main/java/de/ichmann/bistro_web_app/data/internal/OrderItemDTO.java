package de.ichmann.bistro_web_app.data.internal;

public record OrderItemDTO(Long id, ProductDTO productDTO, Integer quantity) {
}
