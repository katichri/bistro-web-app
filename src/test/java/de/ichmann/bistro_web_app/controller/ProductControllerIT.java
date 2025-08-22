package de.ichmann.bistro_web_app.controller;

import de.ichmann.bistro_web_app.api.model.ProductAPIDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

@SpringBootTest
@ActiveProfiles("test")
public class ProductControllerIT {

    @Autowired
    private ProductController productController;

    @Test
    public void verifyGetProducts() {
        // Spring integration test imports data already
        ResponseEntity<List<ProductAPIDTO>> listResponseEntity = productController.productsGet();
        assert listResponseEntity.getStatusCode().is2xxSuccessful();
        assert listResponseEntity.getBody() != null;
        assert listResponseEntity.getBody().size() == 14; // at least currently
    }

    @Test
    public void verifyGetProductsById() {
        // Spring integration test imports data already
        ResponseEntity<ProductAPIDTO> listResponseEntity = productController.productsIdGet(1L);
        assert listResponseEntity.getStatusCode().is2xxSuccessful();
        assert listResponseEntity.getBody() != null;
        assert listResponseEntity.getBody().getName().equals("Pizza Magherita");
    }

}
