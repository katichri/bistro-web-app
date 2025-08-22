package de.ichmann.bistro_web_app.controller;

import de.ichmann.bistro_web_app.api.model.OrderItemRequestAPIDTO;
import de.ichmann.bistro_web_app.api.model.OrderRequestAPIDTO;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

@SpringBootTest
@ActiveProfiles("test")
public class OrderControllerIT {

    @Autowired
    private OrderController orderController;

    @Test
    @Transactional
    public void postOrder() {
        OrderRequestAPIDTO orderRequestAPIDTO = new OrderRequestAPIDTO()
                .tableNumber("t1")
                .customerName("customer")
                .items(
                        List.of(
                                new OrderItemRequestAPIDTO(1)
                                        .productId(1L)
                        )
                );
        ResponseEntity<Void> voidResponseEntity = orderController.ordersPost(orderRequestAPIDTO);
        assert voidResponseEntity.getStatusCode().is2xxSuccessful();
        assert voidResponseEntity.getHeaders().getLocation() != null;
        final String location = voidResponseEntity.getHeaders().getLocation().toString();
        final String[] split = location.split("/");
        final Long orderId = Long.parseLong(split[split.length - 1]);

        //get
        final ResponseEntity<?> responseEntity = orderController.ordersIdGet(orderId);
        assert responseEntity.getStatusCode().is2xxSuccessful();
        assert responseEntity.getBody() != null;
    }

}
