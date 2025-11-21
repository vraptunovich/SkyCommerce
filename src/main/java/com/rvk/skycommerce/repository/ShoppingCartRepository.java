package com.rvk.skycommerce.repository;

import com.rvk.skycommerce.repository.entity.ShoppingCart;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ShoppingCartRepository extends JpaRepository<ShoppingCart, Long> {

    Optional<ShoppingCart> findByClient_Id(String clientId);

    @EntityGraph(attributePaths = {"client", "items"})
    @Query("select c from ShoppingCart c where c.id = :id")
    Optional<ShoppingCart> findByIdWithItems(@Param("id") Long id);
}
