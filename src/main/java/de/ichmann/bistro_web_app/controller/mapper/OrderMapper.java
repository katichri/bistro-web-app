package de.ichmann.bistro_web_app.controller.mapper;

import de.ichmann.bistro_web_app.api.model.OrderAPIDTO;
import de.ichmann.bistro_web_app.api.model.OrderItemAPIDTO;
import de.ichmann.bistro_web_app.api.model.OrderItemRequestAPIDTO;
import de.ichmann.bistro_web_app.api.model.OrderRequestAPIDTO;
import de.ichmann.bistro_web_app.data.internal.OrderDTO;
import de.ichmann.bistro_web_app.data.internal.OrderItemDTO;
import de.ichmann.bistro_web_app.data.internal.ProductDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;

public class OrderMapper {
    public static OrderAPIDTO toOrderAPIDTO(OrderDTO orderDTO) {
        return new OrderAPIDTO()
                .id(orderDTO.id())
                .customerName(orderDTO.customerName())
                .tableNumber(orderDTO.table())
                .orderTime(orderDTO.orderTime())
                .totalPrice(orderDTO.totalPrice())
                .originalPrice(orderDTO.originalPrice())
                .items(OrderMapper.toOrderItemAPIDTOList(orderDTO.orderItems()));
    }

    private static List<OrderItemAPIDTO> toOrderItemAPIDTOList(List<OrderItemDTO> orderItemDTOS){
        return orderItemDTOS.stream().map(OrderMapper::toOrderItemAPIDTO).toList();
    }

    private static OrderItemAPIDTO toOrderItemAPIDTO(OrderItemDTO orderItemDTO) {
        return new OrderItemAPIDTO()
                .amount(orderItemDTO.quantity())
                .product(ProductMapper.toProductAPIDTO(orderItemDTO.productDTO()));
    }

    public static OrderDTO toOrderDTO(OrderRequestAPIDTO orderRequestDTO) {
        return new OrderDTO(null,
                orderRequestDTO.getCustomerName(),
                orderRequestDTO.getTableNumber(),
                LocalDateTime.now(),
                OrderMapper.toOrderItemRequestDTOList(orderRequestDTO.getItems()),
                null, // Original price will be calculated later
                null, // Discount will be calculated later
                null // Total price will be calculated later
                );
    }

    private static List<OrderItemDTO> toOrderItemRequestDTOList(@NotNull @Valid List<OrderItemRequestAPIDTO> items) {
        return items.stream()
                .map(OrderMapper::toOrderItemDTO)
                .toList();
    }

    private static OrderItemDTO toOrderItemDTO(OrderItemRequestAPIDTO item) {
        return new OrderItemDTO(null,
                new ProductDTO(null, null, null, item.getProductId()),
                item.getAmount());
    }
}
