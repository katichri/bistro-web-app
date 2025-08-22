package de.ichmann.bistro_web_app.controller;

import de.ichmann.bistro_web_app.api.model.OrderAPIDTO;
import de.ichmann.bistro_web_app.api.model.OrderItemRequestAPIDTO;
import de.ichmann.bistro_web_app.api.model.OrderRequestAPIDTO;
import de.ichmann.bistro_web_app.data.internal.OrderDTO;
import de.ichmann.bistro_web_app.data.internal.OrderItemDTO;
import de.ichmann.bistro_web_app.data.internal.ProductDTO;
import de.ichmann.bistro_web_app.service.OrderService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OrderControllerTest {
    final OrderService orderService = mock(OrderService.class);

    public OrderController createSUT() {
        return new OrderController(orderService);
    }

    @Test
    void verifyOrdersGet() {

        long orderId = 1L;
        Long orderItemId = 2L;
        long productId = 3L;
        OrderDTO orderDTO = new OrderDTO(
                orderId,
                "mustermann",
                "table",
                LocalDateTime.now(),
                List.of(new OrderItemDTO(
                        orderItemId,
                        new ProductDTO("test", "test", 1.99f, productId),
                        2)),
                100.0f,
                10.0f,
                90.0f);

        when(orderService.getOrderById(orderId)).thenReturn(orderDTO);

        ResponseEntity<OrderAPIDTO> response = createSUT().ordersIdGet(orderId);

        Mockito.verify(orderService).getOrderById(orderId);
        Mockito.verifyNoMoreInteractions(orderService);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
    }

    @Test
    void verifyOrdersPost() {
        when(orderService.postOrder(any())).thenReturn(42L);

        OrderRequestAPIDTO requestDTO = new OrderRequestAPIDTO(
                List.of(new OrderItemRequestAPIDTO()
                        .productId(1L)
                        .amount(2)),
                "t1");

        ResponseEntity<Void> response = createSUT().ordersPost(requestDTO);

        Mockito.verify(orderService).postOrder(any(OrderDTO.class));
        Mockito.verifyNoMoreInteractions(orderService);

        assertEquals(201, response.getStatusCode().value());
        assertTrue(Objects.requireNonNull(response.getHeaders().getLocation()).toString().endsWith("/orders/42"));
    }

    @Test
    void verifyOrdersPost_InvalidAmount() {
        OrderService orderService = mock(OrderService.class);
        when(orderService.postOrder(any())).thenReturn(42L);

        OrderRequestAPIDTO requestDTO = new OrderRequestAPIDTO(
                List.of(new OrderItemRequestAPIDTO()
                        .productId(1L)
                        .amount(0)),
                "t1");

        Mockito.verifyNoInteractions(orderService);

        Assertions.assertThrows(IllegalArgumentException.class, () -> createSUT().ordersPost(requestDTO));
    }
}
