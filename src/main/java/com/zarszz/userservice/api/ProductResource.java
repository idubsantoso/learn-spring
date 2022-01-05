package com.zarszz.userservice.api;

import java.net.URI;
import java.util.List;

import com.zarszz.userservice.domain.Product;
import com.zarszz.userservice.service.ProductServiceImpl;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ProductResource {
    private final ProductServiceImpl productService;

    @GetMapping("/products")
    ResponseEntity<List<Product>> getProducts() {
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/products").toUriString());
        return ResponseEntity.created(uri).body(this.productService.getProducts());
    }

    @PostMapping("/products")
    ResponseEntity<Product> create(@RequestBody Product product) {
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/products").toUriString());
        return ResponseEntity.created(uri).body(this.productService.save(product));
    }
}
