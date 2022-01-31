package com.zarszz.userservice.controller;

import com.zarszz.userservice.requests.v1.order.OrderDto;
import com.zarszz.userservice.service.OrderServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping(value = "/api/orders")
public class OrderController {

    @Autowired
    OrderServiceImpl orderService;

    @GetMapping
    ResponseEntity<?> get() {
        var orders = orderService.get();
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{id}")
    ResponseEntity<?> getById(@PathVariable("id") Long id) {
        var order = orderService.getById(id);
        return ResponseEntity.ok(order);
    }

    @PostMapping
    ResponseEntity<?> create(@RequestBody @Valid OrderDto createOrderDto) {
        var order = orderService.create(createOrderDto);
        return ResponseEntity.ok(order);
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
