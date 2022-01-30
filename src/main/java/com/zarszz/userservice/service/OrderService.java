package com.zarszz.userservice.service;

import com.zarszz.userservice.domain.Order;
import com.zarszz.userservice.requests.v1.order.CreateOrderDto;
import com.zarszz.userservice.requests.v1.order.UpdateOrderDto;

import java.util.List;
import java.util.NoSuchElementException;

public interface OrderService {
    Order create(CreateOrderDto createOrderDto) throws NoSuchElementException;
    List<Order> get();
    Order getById(Long id) throws NoSuchElementException;
    void update(UpdateOrderDto updateOrderDto , Long id) throws NoSuchElementException;
    void delete(Long id) throws NoSuchElementException;
}
