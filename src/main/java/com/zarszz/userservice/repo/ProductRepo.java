package com.zarszz.userservice.repo;

import com.zarszz.userservice.domain.Product;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepo extends JpaRepository<Product, Long> {}
