package com.zarszz.userservice.controller;

import com.midtrans.httpclient.error.MidtransError;
import com.zarszz.userservice.requests.v1.order.OrderDto;
import com.zarszz.userservice.persistence.service.OrderServiceImpl;
import com.zarszz.userservice.persistence.service.PaymentServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@CrossOrigin
@RestController
@RequestMapping(value = "/api/orders")
public class OrderController {

    @Autowired
    OrderServiceImpl orderService;

    @Autowired
    PaymentServiceImpl paymentService;

    @GetMapping
    ResponseEntity<?> get() {
        return ResponseEntity.ok(orderService.get());
    }

    @GetMapping("/{id}")
    ResponseEntity<?> getById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(orderService.getById(id));
    }

    @PostMapping
    ResponseEntity<?> create(@RequestBody @Valid OrderDto createOrderDto) {
        return ResponseEntity.ok(orderService.create(createOrderDto));
    }

    @PostMapping("/{orderId}/payment/create")
    ResponseEntity<?> proceed(@PathVariable("orderId") Long orderId) throws MidtransError {
        paymentService.create(orderId);
        return ResponseEntity.ok("ok");
    }

    @PutMapping("/{id}")
    ResponseEntity<?> update(@RequestBody @Valid OrderDto updateOrderDto, @PathVariable("id") Long id) {
        orderService.update(updateOrderDto, id);
        return ResponseEntity.ok("Operation Success");
    }

    @DeleteMapping("/{id}")
    ResponseEntity<?> delete(@PathVariable("id") Long id) {
        orderService.delete(id);
        return ResponseEntity.ok("Operation Success");
    }
}
