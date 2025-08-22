package de.ichmann.bistro_web_app.service;

import de.ichmann.bistro_web_app.data.internal.OrderDTO;
import de.ichmann.bistro_web_app.entity.OrderEntity;
import de.ichmann.bistro_web_app.repository.OrderRepository;
import de.ichmann.bistro_web_app.service.mapper.OrderMapper;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalTime;

@Slf4j
@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final ProductService productService;
    private final boolean enableDiscount;
    private final float discount;
    private final LocalTime startTimeDiscount;
    private final LocalTime endTimeDiscount;

    public OrderService(OrderRepository orderRepository,
                        ProductService productService,
                        @Value("${bistro_web_app.order.discount.enable:true}") boolean enableDiscount,
                        @Value("${bistro_web_app.order.discount.amount:0}") float discount,
                        @Value("${bistro_web_app.order.discount.start:00:00}") LocalTime startTimeDiscount,
                        @Value("${bistro_web_app.order.discount.end:00:00}") LocalTime endTimeDiscount
    ) {
        this.orderRepository = orderRepository;
        this.productService = productService;
        this.enableDiscount = enableDiscount;
        this.discount = discount;
        this.startTimeDiscount = startTimeDiscount;
        this.endTimeDiscount = endTimeDiscount;
    }


    public OrderDTO getOrderById(Long id) {
        OrderDTO orderDTO = orderRepository.findById(id)
                .map(order -> OrderMapper.toOrderDTO(
                                order,
                                getCurrentDiscount(order),
                                getTotalPrice(order, 0),
                                getTotalPrice(order, getCurrentDiscount(order))
                        )
                )
                .orElseThrow(() -> new EntityNotFoundException("Order with ID %d not found".formatted(id)));
        printBill(orderDTO);
        return orderDTO;
    }

    private void printBill(OrderDTO orderDTO) {
        StringBuilder stringBuilder = new StringBuilder("\n");
        stringBuilder.append("-------------------------\n");
        stringBuilder.append("Table Nr. %s\n".formatted(orderDTO.table()));
        stringBuilder.append("-------------------------\n");
        orderDTO.orderItems().forEach(orderItemDTO -> {
            stringBuilder.append("%s x %s: @ %.2f = %.2f\n".formatted(
                    orderItemDTO.quantity(),
                    orderItemDTO.productDTO().name(),
                    orderItemDTO.productDTO().price(),
                    orderItemDTO.productDTO().price() * orderItemDTO.quantity()
            ));
        });
        stringBuilder.append("-------------------------\n");
        stringBuilder.append("Subtotal: %.2f\n".formatted(orderDTO.originalPrice()));
        if (orderDTO.discount() > 0f) {
            stringBuilder.append("Discount: %.2f%s\n".formatted(orderDTO.discount() * 100, "%"));
        }
        stringBuilder.append("Total: %.2f\n".formatted(orderDTO.totalPrice()));
        log.info(stringBuilder.toString());
    }

    private float getCurrentDiscount(OrderEntity order) {
        if (enableDiscount
                && order.getOrderTime().toLocalTime().isAfter(startTimeDiscount)
                && order.getOrderTime().toLocalTime().isBefore(endTimeDiscount)) {
            return discount;
        }
        return 0;
    }

    private float getTotalPrice(OrderEntity order, float discount) {
        return (1f - discount) * order.getItems()
                .stream()
                .map(orderItemEntity ->
                        orderItemEntity.getProductEntity().getPrice() * orderItemEntity.getQuantity()
                )
                .reduce(0f, Float::sum);
    }


    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public Long postOrder(OrderDTO orderDTO) {
        //validate orderDTO for products existing or exception
        resolveProducts(orderDTO);
        OrderEntity orderEntity = OrderMapper.toOrderEntity(orderDTO);
        OrderEntity save = orderRepository.save(orderEntity);
        return save.getId();
    }


    private void resolveProducts(OrderDTO orderDTO) {
        orderDTO.orderItems().stream().forEach(item -> {
            if (item.productDTO() == null || item.productDTO().id() == null) {
                throw new IllegalArgumentException("Product ID must not be null in order item: %s".formatted(item));
            }
            productService.getProductById(item.productDTO().id());
        });

    }
}
