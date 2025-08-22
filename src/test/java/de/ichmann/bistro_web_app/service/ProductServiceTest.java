package de.ichmann.bistro_web_app.service;

import de.ichmann.bistro_web_app.data.internal.ProductDTO;
import de.ichmann.bistro_web_app.entity.ProductEntity;
import de.ichmann.bistro_web_app.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.argThat;

public class ProductServiceTest {

    public static final List<ProductEntity> ALL_PRODUCTS = List.of(
            ProductEntity.builder().id(1l).name("Product 1").externalId("ext-1").price(10.0f).build(),
            ProductEntity.builder().id(2l).name("Product 2").externalId("ext-2").price(20.0f).build(),
            ProductEntity.builder().id(3l).name("Product 3").externalId("ext-3").price(30.0f).build(),
            ProductEntity.builder().id(4l).name("Product 4").externalId("ext-4").price(40.0f).build(),
            ProductEntity.builder().id(5l).name("Product 5").externalId("ext-5").price(50.0f).build(),
            ProductEntity.builder().id(6l).name("Product 6").externalId("ext-6").price(60.0f).build()
    );
    private final ProductRepository productRepository = Mockito.mock(ProductRepository.class);

    public ProductService createSUT() {
        return new ProductService(productRepository);
    }

    @Test
    public void verifyGetProducts() {
        Mockito.when(productRepository.findAll()).thenReturn(ALL_PRODUCTS);

        final List<ProductDTO> products = createSUT().getProducts();

        Mockito.verify(productRepository, Mockito.times(1)).findAll();
        Mockito.verifyNoMoreInteractions(productRepository);

        Assertions.assertEquals(ALL_PRODUCTS.size(), products.size());
        for (int i = 0; i < ALL_PRODUCTS.size(); i++) {
            Assertions.assertEquals(ALL_PRODUCTS.get(i).getId(), products.get(i).id());
            Assertions.assertEquals(ALL_PRODUCTS.get(i).getName(), products.get(i).name());
            Assertions.assertEquals(ALL_PRODUCTS.get(i).getExternalId(), products.get(i).externalId());
            Assertions.assertEquals(ALL_PRODUCTS.get(i).getPrice(), products.get(i).price());
        }
    }

    @Test
    public void verifyGetProductById() {
        final long id = 1L;
        Mockito.when(productRepository.findById(id))
                .thenReturn(Optional.of(ALL_PRODUCTS.getFirst()));

        final ProductDTO product = createSUT().getProductById(id);

        Mockito.verify(productRepository, Mockito.times(1)).findById(id);
        Mockito.verifyNoMoreInteractions(productRepository);

        Assertions.assertEquals(ALL_PRODUCTS.getFirst().getId(), product.id());
        Assertions.assertEquals(ALL_PRODUCTS.getFirst().getName(), product.name());
        Assertions.assertEquals(ALL_PRODUCTS.getFirst().getExternalId(), product.externalId());
        Assertions.assertEquals(ALL_PRODUCTS.getFirst().getPrice(), product.price());
    }


    @Test
    public void verifyGetProductById_NotFound() {
        final long id = 1L;
        Mockito.when(productRepository.findById(id))
                .thenReturn(Optional.empty());

        Assertions.assertThrows(EntityNotFoundException.class, () -> createSUT().getProductById(id));

        Mockito.verify(productRepository, Mockito.times(1)).findById(id);
        Mockito.verifyNoMoreInteractions(productRepository);

    }

    @Test
    public void verifyMerge_NewProduct() {
        final ProductDTO productToBeImported = new ProductDTO("Imported Product", "ext-123", 99.99f, null);

        Mockito.when(productRepository.findByExternalId(productToBeImported.externalId())).thenReturn(Optional.empty());

        createSUT().mergeProduct(productToBeImported);

        InOrder inOrder = Mockito.inOrder(productRepository);
        inOrder.verify(productRepository, Mockito.times(1)).findByExternalId(productToBeImported.externalId());
        inOrder.verify(productRepository).save(argThat(a -> a.getId() == null && Objects.equals(a.getExternalId(), "ext-123")));
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void verifyMerge_UpdatedProduct() {
        final ProductDTO productToBeImported = new ProductDTO("Imported Product", "ext-123", 99.99f, null);

        Mockito.when(productRepository.findByExternalId(productToBeImported.externalId()))
                .thenReturn(Optional.of(ProductEntity.builder()
                        .externalId("ext-123")
                        .name("Existing Product")
                        .price(49.99f)
                        .id(69L)
                        .build()));

        createSUT().mergeProduct(productToBeImported);

        InOrder inOrder = Mockito.inOrder(productRepository);
        inOrder.verify(productRepository, Mockito.times(1)).findByExternalId(productToBeImported.externalId());
        inOrder.verify(productRepository).save(argThat(a -> a.getId() != null && Objects.equals(a.getExternalId(), "ext-123")));
        inOrder.verifyNoMoreInteractions();
    }
}
