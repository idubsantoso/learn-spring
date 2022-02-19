package com.zarszz.userservice.persistence.repository;

import com.zarszz.userservice.domain.Product;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {}
