package de.ichmann.bistro_web_app.data.internal;

import java.time.LocalDateTime;
import java.util.List;

public record OrderDTO(Long id,
                       String customerName,
                       String table,
                       LocalDateTime orderTime,
                       List<OrderItemDTO> orderItems,
                       Float originalPrice,
                       Float discount,
                       Float totalPrice
) {

}
