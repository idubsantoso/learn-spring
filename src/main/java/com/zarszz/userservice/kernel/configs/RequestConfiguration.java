package com.zarszz.userservice.kernel.configs;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RequestConfiguration {
    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
}
