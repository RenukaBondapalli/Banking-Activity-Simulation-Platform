package com.example.bank.app;

import com.example.bank.controller.AccountController;
import com.example.bank.controller.CustomerController;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;

import jakarta.ws.rs.ApplicationPath;

@ApplicationPath("/api")
public class App extends ResourceConfig {
    
    public App() {
        register(AccountController.class);
        register(CustomerController.class);
        
        packages("com.example.bank.controller");
        
        property(ServerProperties.TRACING, "ALL");
        
        System.out.println("Application is running...");
    }
}