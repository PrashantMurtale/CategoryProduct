package com.Rest.CategoryProduct.Configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@Configuration
@EnableMethodSecurity
public class AppConfig {
    // Add any non-security related beans here
    // Example:
    // @Bean
    // public SomeService someService() {
    //     return new SomeServiceImpl();
    // }
}
