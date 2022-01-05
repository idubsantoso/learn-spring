package com.zarszz.userservice.service;

import java.util.List;
import javax.transaction.Transactional;

import com.zarszz.userservice.config.Config;
import com.zarszz.userservice.domain.Product;
import com.zarszz.userservice.repo.ProductRepo;

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
    private final ProductRepo productRepo;

    @Override
    @CacheEvict(value = Config.ALL_PRODUCT, allEntries = true)
    public Product save(Product product) {
        return this.productRepo.save(product);
    }

    @Override
    @Cacheable(value = Config.PRODUCT_STRING, key = "#id")
    public Product get(Long id) throws Exception {
        return this.productRepo.findById(id).orElseThrow(() -> new NotFoundException(""));
    }

    @Override
    @Cacheable(value = Config.ALL_PRODUCT, keyGenerator = Config.GENERATOR_CACHE_KEY)
    public List<Product> getProducts() {
        log.info("all product fetched");
        List<Product> products = this.productRepo.findAll();
        return products;
    }

    @Override
    @Caching(evict = { @CacheEvict(value = Config.ALL_PRODUCT, allEntries = true),
        @CacheEvict(value = Config.PRODUCT_STRING, key = "#id") })
    public Product update(Long id, Product product) throws Exception {
        Product productObj = this.productRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("product not found"));

        productObj.setDescription(product.getDescription());
        productObj.setName(product.getName());
        productObj.setPrice(product.getPrice());

        return this.productRepo.save(productObj);
    }

}
