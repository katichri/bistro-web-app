package de.ichmann.bistro_web_app.service.mapper;

import de.ichmann.bistro_web_app.data.internal.OrderDTO;
import de.ichmann.bistro_web_app.data.internal.OrderItemDTO;
import de.ichmann.bistro_web_app.entity.OrderEntity;
import de.ichmann.bistro_web_app.entity.OrderItemEntity;

import java.util.List;

public class OrderMapper {

    private OrderMapper() {
    }

    public static OrderDTO toOrderDTO(OrderEntity orderEntity,
                                      float currentDiscount,
                                      float originalPrice,
                                      float totalPrice) {
        return new OrderDTO(
                orderEntity.getId(),
                orderEntity.getCustomerName(),
                orderEntity.getTableName(),
                orderEntity.getOrderTime(),
                OrderMapper.toOrderItemDTOList(orderEntity.getItems()),
                originalPrice,
                currentDiscount,
                totalPrice
        );
    }

    private static List<OrderItemDTO> toOrderItemDTOList(List<OrderItemEntity> items) {
        return items.stream().map(OrderMapper::toOrderItemDTO).toList();
    }

    private static OrderItemDTO toOrderItemDTO(OrderItemEntity item) {
        return new OrderItemDTO(
                item.getId(),
                ProductMapper.toProductDTO(item.getProductEntity()),
                item.getQuantity());
    }


    public static OrderEntity toOrderEntity(OrderDTO orderDTO) {
        OrderEntity orderEntity = OrderEntity.builder()
                .orderTime(orderDTO.orderTime())
                .customerName(orderDTO.customerName())
                .tableName(orderDTO.table())
                .build();
        orderEntity.setItems(OrderMapper.toOrderItemEntityList(orderEntity, orderDTO.orderItems()));
        return orderEntity;
    }

    private static List<OrderItemEntity> toOrderItemEntityList(OrderEntity orderEntity, List<OrderItemDTO> orderItemDTOS) {
        return orderItemDTOS.stream()
                .map(item -> OrderMapper.toOrderItemEntity(item, orderEntity))
                .toList();
    }

    private static OrderItemEntity toOrderItemEntity(OrderItemDTO orderItemDTO, OrderEntity orderEntity) {
        return OrderItemEntity.builder()
                .productId(orderItemDTO.productDTO().id())
                .quantity(orderItemDTO.quantity())
                .orderEntity(orderEntity)
                .build();
    }
}