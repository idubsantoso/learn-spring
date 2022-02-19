package com.zarszz.userservice.persistence.service;

import java.util.List;
import java.util.NoSuchElementException;

import com.zarszz.userservice.domain.Product;

public interface ProductService {
    Product save(Product product);
    Product get(Long id) throws NoSuchElementException;
    Product update(Long id, Product product) throws Exception;
    List<Product> getProducts();
}
