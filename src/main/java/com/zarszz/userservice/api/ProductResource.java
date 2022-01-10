package com.zarszz.userservice.api;

import java.net.URI;
import java.util.List;
import javax.validation.Valid;

import com.zarszz.userservice.domain.Product;
import com.zarszz.userservice.service.ProductServiceImpl;

import com.zarszz.userservice.requests.v1.product.CreateProductDto;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

    @Autowired
    private final ProductServiceImpl productService;

    @Autowired
    private ModelMapper modelMapper;

    @GetMapping("/products")
    ResponseEntity<List<Product>> getProducts() {
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/products").toUriString());
        return ResponseEntity.created(uri).body(this.productService.getProducts());
    }

    @PostMapping("/products")
    ResponseEntity<Product> create(@Valid @RequestBody CreateProductDto productDto) {
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/products").toUriString());
        Product product = convertToEntity(productDto);
        return ResponseEntity.created(uri).body(this.productService.save(product));
    }

    @GetMapping("/products/{id}")
    ResponseEntity<Product> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(this.productService.get(id));
    }

    private Product convertToEntity(CreateProductDto productDto) {
        return modelMapper.map(productDto, Product.class);
    }
}
