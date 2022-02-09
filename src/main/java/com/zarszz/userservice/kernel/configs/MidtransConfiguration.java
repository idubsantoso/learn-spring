package com.zarszz.userservice.kernel.configs;

import com.midtrans.service.MidtransCoreApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.midtrans.Config;
import com.midtrans.ConfigFactory;
import com.midtrans.service.MidtransSnapApi;

import org.springframework.beans.factory.annotation.Value;

@Configuration
public class MidtransConfiguration {
    @Value("${midtrans.server.key}")
    private String serverKey;

    @Value("${midtrans.server.key}")
    private String clientKey;

    @Value("${midtrans.server.environment}")
    private String environment;

    @Bean
    MidtransSnapApi midtransSnapApi() {
        var isProduction = environment.equals("production");
        return new ConfigFactory(new Config(serverKey, clientKey, isProduction)).getSnapApi();
    }

    @Bean
    MidtransCoreApi midtransCoreApi() {
        var isProduction = environment.equals("production");
        return new ConfigFactory(new Config(serverKey, clientKey, isProduction)).getCoreApi();
    }
}
