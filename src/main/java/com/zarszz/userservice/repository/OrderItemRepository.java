package com.zarszz.userservice.repository;

import com.zarszz.userservice.domain.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    Long deleteByOrderId(Long orderId);
}
