package com.zarszz.userservice.kernel.configs;

import com.zarszz.userservice.config.Config;
import com.zarszz.userservice.utility.Global;

import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;

public class CacheConfig {
    protected String[] cacheNames = { Config.ALL_USER, Config.USER_STRING, Config.ALL_PRODUCT };

    @Bean(Config.GENERATOR_CACHE_KEY)
    public KeyGenerator keyGeenratorCache() {
        return (target, method, params) -> Global.generateCacheKey(target, method, params);
    }
}
