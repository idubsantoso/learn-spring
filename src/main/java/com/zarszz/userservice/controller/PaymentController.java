package com.zarszz.userservice.controller;

import com.zarszz.userservice.service.PaymentServiceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/payments")
public class PaymentController {

    @Autowired
    PaymentServiceImpl paymentService;

    @GetMapping
    ResponseEntity<?> get() {
        return ResponseEntity.ok(paymentService.get());
    }

    @GetMapping("/{id}")
    ResponseEntity<?> getById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(paymentService.getById(id));
    }

    @DeleteMapping("/{id}")
    ResponseEntity<?> delete(@PathVariable("id") Long id) {
        paymentService.delete(id);
        return ResponseEntity.ok("Operation success");
    }

    @PostMapping("/{id}/cancel")
    ResponseEntity<?> cancel(@PathVariable("id") Long id) {
        paymentService.cancel(id);
        return ResponseEntity.ok("Operation success");
    }
}
