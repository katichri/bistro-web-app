// OrderItem.java
package de.ichmann.bistro_web_app.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "`order_item`")
public class OrderItemEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id", insertable = false, updatable = false)
    private ProductEntity productEntity;

    @Column(name = "product_id")
    private Long productId;

    private Integer quantity;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private OrderEntity orderEntity;
}