package com.zarszz.userservice.persistence.service;

import com.midtrans.httpclient.error.MidtransError;
import com.zarszz.userservice.domain.Order;
import com.zarszz.userservice.domain.Payment;
import com.zarszz.userservice.requests.v1.order.CreatePaymentDto;
import org.springframework.dao.DuplicateKeyException;

import javax.mail.MessagingException;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

public interface PaymentService {
    void create(Long orderId, CreatePaymentDto paymentDto) throws DuplicateKeyException, MidtransError;
    void save(Payment payment);
    List<Payment> get();
    Payment getById(Long id) throws NoSuchElementException;
    void update(Order order, Long id) throws NoSuchElementException;
    void delete(Long id) throws NoSuchElementException;
    void cancel(Long id) throws NoSuchElementException;
    void proceed(Map<String, Object> response) throws NoSuchElementException, MidtransError, MessagingException;
}
