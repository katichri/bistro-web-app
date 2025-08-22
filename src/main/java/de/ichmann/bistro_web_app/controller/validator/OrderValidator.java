package de.ichmann.bistro_web_app.controller.validator;

import de.ichmann.bistro_web_app.api.model.OrderRequestAPIDTO;
import de.ichmann.bistro_web_app.data.internal.OrderDTO;

public class OrderValidator {
    private OrderValidator() {}

    public static void vaolidateOrderRequest(OrderRequestAPIDTO orderRequestDTO) {
        orderRequestDTO.getItems().forEach(orderItemDTO -> {
            if (orderItemDTO.getAmount() <= 0) {
                throw new IllegalArgumentException("Order item amount must be greater than zero.");
            }
        });}
}
