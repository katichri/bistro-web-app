package de.ichmann.bistro_web_app.service;

import de.ichmann.bistro_web_app.data.internal.OrderDTO;
import de.ichmann.bistro_web_app.data.internal.OrderItemDTO;
import de.ichmann.bistro_web_app.data.internal.ProductDTO;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest
@ActiveProfiles("test")
public class OrderServiceIT {

    @Autowired
    private OrderService orderService;

    @Test
    @Transactional
    public void saveAndGetFromDB() {
        //Daten einspeichern und dann testen, ob sie aus der DB geladen werden können
        OrderDTO testOrder = new OrderDTO(null, "testcustomer", "table1", LocalDateTime.now(),
                List.of(new OrderItemDTO(null, new ProductDTO(null, null, null, 1L), 1)),
                null, null, null
        )/* Testdaten erzeugen */;
        Long orderId = orderService.postOrder(testOrder);

        // Beispielhafte Implementierung: Testet, ob Bestellungen aus der Datenbank geladen werden
        OrderDTO order = orderService.getOrderById(orderId);
        Assertions.assertNotNull(order);
        Assertions.assertEquals("testcustomer", order.customerName());
        Assertions.assertEquals("table1", order.table());
        Assertions.assertEquals(1, order.orderItems().size());
        Assertions.assertEquals("Pizza Magherita", order.orderItems().get(0).productDTO().name());
    }

}
