package com.zarszz.userservice.persistence.service;

import com.zarszz.userservice.domain.Order;
import com.zarszz.userservice.requests.v1.order.OrderDto;

import java.util.List;
import java.util.NoSuchElementException;

public interface OrderService {
    Order create(OrderDto createOrderDto) throws NoSuchElementException;
    List<Order> get();
    Order getById(Long id) throws NoSuchElementException;
    void update(OrderDto updateOrderDto , Long id) throws NoSuchElementException;
    void delete(Long id) throws NoSuchElementException;
}
