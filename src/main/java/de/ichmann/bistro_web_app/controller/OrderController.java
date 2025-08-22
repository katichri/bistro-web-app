package de.ichmann.bistro_web_app.controller;

import de.ichmann.bistro_web_app.api.model.OrderAPIDTO;
import de.ichmann.bistro_web_app.api.model.OrderRequestAPIDTO;
import de.ichmann.bistro_web_app.api.service.OrdersApi;
import de.ichmann.bistro_web_app.controller.mapper.OrderMapper;
import de.ichmann.bistro_web_app.controller.validator.OrderValidator;
import de.ichmann.bistro_web_app.service.OrderService;
import de.ichmann.bistro_web_app.data.internal.OrderDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
public class OrderController implements OrdersApi {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @Override
    public ResponseEntity<OrderAPIDTO> ordersIdGet(Long id) {
        OrderDTO orderDTO = orderService.getOrderById(id);
        return ResponseEntity.ok(OrderMapper.toOrderAPIDTO(orderDTO));
    }

    @Override
    public ResponseEntity<Void> ordersPost(OrderRequestAPIDTO orderRequestDTO) {
        OrderValidator.vaolidateOrderRequest(orderRequestDTO);
        Long id = orderService.postOrder(OrderMapper.toOrderDTO(orderRequestDTO));
        return ResponseEntity.created(URI.create("/orders/%d".formatted(id))).build();
    }
}