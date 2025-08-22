package de.ichmann.bistro_web_app.controller;

import de.ichmann.bistro_web_app.api.model.ProductAPIDTO;
import de.ichmann.bistro_web_app.api.service.ProductsApi;
import de.ichmann.bistro_web_app.controller.mapper.ProductMapper;
import de.ichmann.bistro_web_app.service.ProductService;
import de.ichmann.bistro_web_app.data.internal.ProductDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ProductController implements ProductsApi {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @Override
    public ResponseEntity<List<ProductAPIDTO>> productsGet() {
        List<ProductDTO> productDTOList = productService.getProducts();
        return ResponseEntity.ok(ProductMapper.toProductAPIDTOList(productDTOList));
    }

    @Override
    public ResponseEntity<ProductAPIDTO> productsIdGet(Long id) {
        ProductDTO productDTO = productService.getProductById(id);
        return ResponseEntity.ok(ProductMapper.toProductAPIDTO(productDTO));
    }
}
