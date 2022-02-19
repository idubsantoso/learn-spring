package com.zarszz.userservice.persistence.service;

import java.util.List;
import java.util.NoSuchElementException;

import javax.transaction.Transactional;

import com.zarszz.userservice.kernel.configs.constant.Cache;
import com.zarszz.userservice.domain.Product;
import com.zarszz.userservice.persistence.repository.ProductRepository;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepo;

    @Override
    @CacheEvict(value = Cache.ALL_PRODUCT, allEntries = true)
    public Product save(Product product) {
        return this.productRepo.save(product);
    }

    @Override
    @Cacheable(value = Cache.PRODUCT_STRING, key = "#id")
    public Product get(Long id) throws NoSuchElementException {
        return this.productRepo.findById(id).orElseThrow(() -> new NoSuchElementException("Product Not Found"));
    }

    @Override
    @Cacheable(value = Cache.ALL_PRODUCT, keyGenerator = Cache.GENERATOR_CACHE_KEY)
    public List<Product> getProducts() {
        log.info("all product fetched");
        List<Product> products = this.productRepo.findAll();
        return products;
    }

    @Override
    @Caching(evict = { @CacheEvict(value = Cache.ALL_PRODUCT, allEntries = true),
        @CacheEvict(value = Cache.PRODUCT_STRING, key = "#id") })
    public Product update(Long id, Product product) throws Exception {
        Product productObj = this.productRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("product not found"));

        productObj.setDescription(product.getDescription());
        productObj.setName(product.getName());
        productObj.setPrice(product.getPrice());

        return this.productRepo.save(productObj);
    }

}
