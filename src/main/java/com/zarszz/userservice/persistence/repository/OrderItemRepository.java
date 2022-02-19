package com.zarszz.userservice.persistence.repository;

import com.zarszz.userservice.domain.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    Long deleteByOrderId(Long orderId);
    List<OrderItem> findByOrderId(Long orderId);

}
