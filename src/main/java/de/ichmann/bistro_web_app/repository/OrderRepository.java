package de.ichmann.bistro_web_app.repository;

import de.ichmann.bistro_web_app.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, Long> {




}
