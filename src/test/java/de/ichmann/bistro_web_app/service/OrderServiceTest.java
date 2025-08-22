package de.ichmann.bistro_web_app.service;

import de.ichmann.bistro_web_app.data.internal.OrderDTO;
import de.ichmann.bistro_web_app.data.internal.OrderItemDTO;
import de.ichmann.bistro_web_app.data.internal.ProductDTO;
import de.ichmann.bistro_web_app.entity.OrderEntity;
import de.ichmann.bistro_web_app.entity.OrderItemEntity;
import de.ichmann.bistro_web_app.entity.ProductEntity;
import de.ichmann.bistro_web_app.repository.OrderRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;

public class OrderServiceTest {

    private final OrderRepository orderRepository = Mockito.mock(OrderRepository.class);
    private final ProductService productService = Mockito.mock(ProductService.class);

    public OrderService createSUT(boolean enableDiscount, float discount, LocalTime startTimeDiscount, LocalTime endTimeDiscount) {
        return new OrderService(
                orderRepository,
                productService,
                enableDiscount,
                discount,
                startTimeDiscount,
                endTimeDiscount
        );
    }

    @Test
    public void verifyGetOrderById() {
        //given
        final long orderId = 1L;
        final LocalDateTime orderTime = LocalDateTime.now();
        final OrderEntity orderEntity = OrderEntity.builder()
                .id(orderId)
                .customerName("customerName")
                .tableName("tableName")
                .orderTime(orderTime)
                .items(givenOrderItemEntityList(orderId))
                .build();

        Mockito.when(orderRepository.findById(orderId)).thenReturn(Optional.of(orderEntity));


        //when
        OrderDTO result = createSUT(false, 0.0f, LocalTime.of(0, 0), LocalTime.of(0, 0))
                .getOrderById(orderId);

        //then
        InOrder inOrder = Mockito.inOrder(orderRepository, productService);
        inOrder.verify(orderRepository).findById(orderId);
        inOrder.verifyNoMoreInteractions();

        Assertions.assertNotNull(result);
        Assertions.assertEquals("customerName", result.customerName());
        Assertions.assertEquals("tableName", result.table());
        Assertions.assertEquals(orderTime, result.orderTime());
        Assertions.assertEquals(2, result.orderItems().size());
        Assertions.assertEquals(2, result.orderItems().get(0).quantity());
        Assertions.assertEquals(1, result.orderItems().get(1).quantity());
        Assertions.assertEquals(2L, result.orderItems().get(0).productDTO().id());
        Assertions.assertEquals(3L, result.orderItems().get(1).productDTO().id());
        Assertions.assertEquals(70.0f, result.originalPrice());
        Assertions.assertEquals(0.0f, result.discount());
        Assertions.assertEquals(70.0f, result.totalPrice());
    }

    @Test
    // Verify that the discount is applied correctly when enabled - discount is 50% from 12 to 18. OrderTime is 15
    public void verifyGetOrderById_ActiveDiscount() {
        //given
        final long orderId = 1L;
        final LocalDateTime orderTime = LocalTime.of(15, 0).atDate(LocalDateTime.now().toLocalDate());
        final OrderEntity orderEntity = OrderEntity.builder()
                .id(orderId)
                .customerName("customerName")
                .tableName("tableName")
                .orderTime(orderTime)
                .items(givenOrderItemEntityList(orderId))
                .build();

        Mockito.when(orderRepository.findById(orderId)).thenReturn(Optional.of(orderEntity));


        //when
        OrderDTO result = createSUT(
                true,
                0.5f,
                LocalTime.of(12, 0),
                LocalTime.of(18, 0))
                .getOrderById(orderId);

        //then
        InOrder inOrder = Mockito.inOrder(orderRepository, productService);
        inOrder.verify(orderRepository).findById(orderId);
        inOrder.verifyNoMoreInteractions();

        Assertions.assertNotNull(result);
        Assertions.assertEquals("customerName", result.customerName());
        Assertions.assertEquals("tableName", result.table());
        Assertions.assertEquals(orderTime, result.orderTime());
        Assertions.assertEquals(2, result.orderItems().size());
        Assertions.assertEquals(2, result.orderItems().get(0).quantity());
        Assertions.assertEquals(1, result.orderItems().get(1).quantity());
        Assertions.assertEquals(2L, result.orderItems().get(0).productDTO().id());
        Assertions.assertEquals(3L, result.orderItems().get(1).productDTO().id());
        Assertions.assertEquals(70.0f, result.originalPrice());
        Assertions.assertEquals(0.50f, result.discount());
        Assertions.assertEquals(35.0f, result.totalPrice());
    }

    @Test
    // Verify that the error is thrown when the order is not found
    public void verifyGetOrderById_OrderNotFound() {
        //given
        final long orderId = 1L;
        final LocalDateTime orderTime = LocalTime.of(15, 0).atDate(LocalDateTime.now().toLocalDate());
        Mockito.when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        //when
        Assertions.assertThrows(EntityNotFoundException.class, () -> createSUT(
                true,
                0.5f,
                LocalTime.of(12, 0),
                LocalTime.of(18, 0))
                .getOrderById(orderId));

        //then
        InOrder inOrder = Mockito.inOrder(orderRepository, productService);
        inOrder.verify(orderRepository).findById(orderId);
        inOrder.verifyNoMoreInteractions();

    }

    @Test
    public void verifyPostOrder() {
        // given
        final ProductDTO waschlappen = new ProductDTO("Waschlappen", "Product 2", 20.0f, 2L);
        final ProductDTO esspapier = new ProductDTO("Esspapier", "Product 3", 30.0f, 3L);
        final OrderDTO orderDTO = new OrderDTO(null, "customerName",
                "tableName",
                LocalDateTime.now(),
                List.of(
                        new OrderItemDTO(null,
                                waschlappen,
                                2),
                        new OrderItemDTO(null,
                                esspapier,
                                1)
                ),
                null,
                null,
                null
        );

        Mockito.when(productService.getProductById(2L)).thenReturn(waschlappen);
        Mockito.when(productService.getProductById(3L)).thenReturn(esspapier);
        Mockito.when(orderRepository.save(any(OrderEntity.class)))
                .thenAnswer(invocation -> {
                    OrderEntity orderEntity = invocation.getArgument(0);
                    orderEntity.setId(1L); // Simulate the ID being set by the repository
                    return orderEntity;
                });

        //when
        final Long orderId = createSUT(false, 0.0f, null, null)
                .postOrder(orderDTO);

        InOrder inOrder = Mockito.inOrder(orderRepository, productService);
        inOrder.verify(productService).getProductById(2L);
        inOrder.verify(productService).getProductById(3L);
        inOrder.verify(orderRepository).save(any(OrderEntity.class));
        inOrder.verifyNoMoreInteractions();

        Assertions.assertNotNull(orderId);
        Assertions.assertEquals(1L, orderId);
    }

    @Test
    // Verify that the order creation fails when the product ID is null
    public void verifyProductUnset() {

        final ProductDTO waschlappen = new ProductDTO("Waschlappen", "Product 2", 20.0f, null);
        final ProductDTO esspapier = new ProductDTO("Esspapier", "Product 3", 30.0f, null);
        final OrderDTO orderDTO = new OrderDTO(null, "customerName",
                "tableName",
                LocalDateTime.now(),
                List.of(
                        new OrderItemDTO(null,
                                waschlappen,
                                2),
                        new OrderItemDTO(null,
                                esspapier,
                                1)
                ),
                null,
                null,
                null
        );

        //given
        Assertions.assertThrows(IllegalArgumentException.class, () ->
                createSUT(false, 0.0f, null, null)
                        .postOrder(orderDTO));

        InOrder inOrder = Mockito.inOrder(orderRepository, productService);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void verifyPostOrder_ProductNotFound() {
        // given
        final ProductDTO waschlappen = new ProductDTO("Waschlappen", "Product 2", 20.0f, 2L);
        final ProductDTO esspapier = new ProductDTO("Esspapier", "Product 3", 30.0f, 3L);
        final OrderDTO orderDTO = new OrderDTO(null, "customerName",
                "tableName",
                LocalDateTime.now(),
                List.of(
                        new OrderItemDTO(null,
                                waschlappen,
                                2),
                        new OrderItemDTO(null,
                                esspapier,
                                1)
                ),
                null,
                null,
                null
        );

        Mockito.when(productService.getProductById(2L)).thenThrow(EntityNotFoundException.class);

        //when
        Assertions.assertThrows(EntityNotFoundException.class, () ->
                createSUT(false, 0.0f, null, null)
                        .postOrder(orderDTO));

        InOrder inOrder = Mockito.inOrder(orderRepository, productService);
        inOrder.verify(productService).getProductById(2L);
        inOrder.verifyNoMoreInteractions();

    }

    private List<OrderItemEntity> givenOrderItemEntityList(long orderId) {
        return List.of(
                OrderItemEntity.builder()
                        .id(1L)
                        .productEntity(givenProduct(2)) // Mock or create a ProductEntity as needed
                        .quantity(2)
                        .orderEntity(OrderEntity.builder().id(orderId).build())
                        .build(),

                OrderItemEntity.builder()
                        .id(2L)
                        .productEntity(givenProduct(3)) // Mock or create a ProductEntity as needed
                        .quantity(1)
                        .orderEntity(OrderEntity.builder().id(orderId).build())
                        .build()
        );

    }

    private ProductEntity givenProduct(int i) {
        return ProductEntity.builder()
                .id((long) i)
                .name("Product " + i)
                .price(10.0f * i) // Example price
                .externalId("ext-" + i) // Example external ID
                .build();
    }

}
