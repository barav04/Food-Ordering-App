package com.upgrad.FoodOrderingApp.api;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

import com.upgrad.FoodOrderingApp.api.config.AppConfig;
import com.upgrad.FoodOrderingApp.api.config.SwaggerConfiguration;
import com.upgrad.FoodOrderingApp.service.ServiceConfiguration;

/**
 * A Configuration class that can declare one or more @Bean methods and trigger auto-configuration and component scanning.
 * This class launches a Spring Application from Java main method.
 */
@SpringBootApplication
@Import(ServiceConfiguration.class)
@ComponentScan({"com.upgrad.FoodOrderingApp.api"})
public class FoodOrderingAppApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(FoodOrderingAppApiApplication.class, args);
    }
}

