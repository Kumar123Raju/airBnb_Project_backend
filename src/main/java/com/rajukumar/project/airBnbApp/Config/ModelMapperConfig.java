package com.rajukumar.project.airBnbApp.Config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {

    @Bean
    ModelMapper getModelMapper(){
        return new ModelMapper();
    }
}
