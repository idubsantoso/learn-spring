package com.zarszz.userservice.service;

import com.zarszz.userservice.domain.Order;
import com.zarszz.userservice.domain.Payment;
import org.springframework.dao.DuplicateKeyException;

import java.util.List;
import java.util.NoSuchElementException;

public interface PaymentService {
    Payment create(Long orderId) throws DuplicateKeyException;
    List<Payment> get();
    Payment getById(Long id) throws NoSuchElementException;
    void update(Order order, Long id) throws NoSuchElementException;
    void delete(Long id) throws NoSuchElementException;
    void cancel(Long id) throws NoSuchElementException;
    void proceed(Long id) throws NoSuchElementException;
}
