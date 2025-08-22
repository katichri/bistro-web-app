package de.ichmann.bistro_web_app.service;

import de.ichmann.bistro_web_app.api.model.ProductAPIDTO;
import de.ichmann.bistro_web_app.data.internal.ProductDTO;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

@SpringBootTest
@ActiveProfiles("test")
public class ProductServiceIT {

    @Autowired
    private ProductService productService;

    @Test
    @Transactional
    public void getFromDB() {
        ProductDTO productById = productService.getProductById(1L);
        //should be the magherita pizza
        assert productById.name().equals("Pizza Magherita");
    }

    @Test
    @Transactional
    public void getFromDB_notExisting() {
        Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> productService.getProductById(-1L)
        );
    }

    @Test
    @Transactional
    public void importDataToDB() {
        ProductDTO productToUpdate = new ProductDTO("Eis", "IAM-NEW", 9.99f, null);
        productService.mergeProduct(productToUpdate);

        //verify
        List<ProductDTO> products = productService.getProducts();

        Assertions.assertTrue(products.stream().anyMatch(product -> product.name().equals("Eis")));
    }

}
