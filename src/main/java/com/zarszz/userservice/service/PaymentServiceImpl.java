package com.zarszz.userservice.service;


import java.util.List;
import java.util.NoSuchElementException;

import com.zarszz.userservice.domain.Order;
import com.zarszz.userservice.domain.Payment;
import com.zarszz.userservice.domain.enumData.OrderStatus;
import com.zarszz.userservice.domain.enumData.PaymentStatus;
import com.zarszz.userservice.kernel.exception.AlreadyCreatedException;
import com.zarszz.userservice.repository.PaymentRepository;
import com.zarszz.userservice.security.entity.AuthenticatedUser;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    PaymentRepository paymentRepository;

    @Autowired
    OrderServiceImpl orderService;

    @Autowired
    AuthenticatedUser authenticatedUser;

    @Override
    @Transactional(
            propagation = Propagation.REQUIRED,
            isolation = Isolation.SERIALIZABLE
    )
    public Payment create(Long orderId) throws AlreadyCreatedException {
        if (paymentRepository.findByOrderId(orderId).isPresent())
            throw new AlreadyCreatedException("Order already created");
        var order = orderService.getById(orderId);
        var payment = new Payment();

        payment.setUser(authenticatedUser.getUser());
        payment.setOrder(order);
        payment.setStatus(PaymentStatus.PENDING);
        payment.setTotal(order.getSubTotal());
        payment.setPaymentCode(RandomStringUtils.randomAlphanumeric(16).toUpperCase());

        return paymentRepository.save(payment);
    }

    @Override
    public List<Payment> get() {
        var isAdmin = authenticatedUser.getUser().getRoles().stream().anyMatch(role -> role.getName().equals("ROLE_ADMIN"));
        return isAdmin ? paymentRepository.findAll() : paymentRepository.findByUserId(authenticatedUser.getUserId());
    }

    @Override
    public Payment getById(Long id) throws NoSuchElementException {
        return paymentRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Payment not found"));
    }

    @Override
    public void update(Order order, Long id) throws NoSuchElementException {
        var payment = paymentRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Payment not found"));
        payment.setTotal(order.getSubTotal());
        paymentRepository.save(payment);
        
    }

    @Override
    public void delete(Long id) throws NoSuchElementException {
        if (!paymentRepository.existsById(id))
            throw new NoSuchElementException("Payment not found");
        paymentRepository.deleteById(id);
        
    }

    @Override
    public void cancel(Long id) throws NoSuchElementException {
        var payment = paymentRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Payment not found"));
        payment.setStatus(PaymentStatus.CANCELLED);
        payment.getOrder().setStatus(OrderStatus.CANCELLED);
        paymentRepository.save(payment);
    }

    @Override
    public void proceed(Long id) throws NoSuchElementException {
        // TODO !
        // Create proceed payment dto
        // Connect to payment gateway
        // Set transaction state
        // Update order state
    }
}
