package de.ichmann.bistro_web_app.repository;

import de.ichmann.bistro_web_app.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, Long> {


    Optional<ProductEntity> findByExternalId(String s);


}
