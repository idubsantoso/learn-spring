package com.zarszz.userservice.kernel.configs;

import com.zarszz.userservice.kernel.configs.constant.Cache;
import com.zarszz.userservice.utility.Global;

import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;

public class CacheConfig {
    protected String[] cacheNames = { Cache.ALL_USER, Cache.USER_STRING, Cache.ALL_PRODUCT };

    @Bean(Cache.GENERATOR_CACHE_KEY)
    public KeyGenerator keyGeenratorCache() {
        return (target, method, params) -> Global.generateCacheKey(target, method, params);
    }
}
