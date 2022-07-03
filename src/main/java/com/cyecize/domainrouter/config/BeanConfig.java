package com.cyecize.domainrouter.config;

import com.cyecize.ioc.annotations.Bean;
import com.cyecize.ioc.annotations.Service;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class BeanConfig {

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}
