package com.example.transactions.config;

import com.example.transactions.controllers.AccountController;
import com.example.transactions.controllers.TransactionController;
import com.example.transactions.exceptions.ConstraintViolationExceptionMapper;
import com.example.transactions.exceptions.GlobalExceptionMapper;
import io.swagger.v3.jaxrs2.integration.resources.OpenApiResource;
import jakarta.ws.rs.ApplicationPath;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * Jersey configuration class.
 * Acts as the registry for all JAX-RS resources (controllers) and
 * providers (exception mappers, filters).
 */
@Component
@ApplicationPath("/api")
public class JerseyConfig extends ResourceConfig {

    public JerseyConfig(Set<Class<?>> classes) {
        super(classes);
    }

    public JerseyConfig() {

        // Register REST Endpoints
        register(AccountController.class);
        register(TransactionController.class);

        // Register Global Exception Handling Mapper
        register(GlobalExceptionMapper.class);
        register(ConstraintViolationExceptionMapper.class);
        register(OpenApiResource.class);
    }
}