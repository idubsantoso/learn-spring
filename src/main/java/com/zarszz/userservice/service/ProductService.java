package com.zarszz.userservice.service;

import java.util.List;

import com.zarszz.userservice.domain.Product;

public interface ProductService {
    Product save(Product product);
    Product get(Long id) throws Exception;
    Product update(Long id, Product product) throws Exception;
    List<Product> getProducts();
}
