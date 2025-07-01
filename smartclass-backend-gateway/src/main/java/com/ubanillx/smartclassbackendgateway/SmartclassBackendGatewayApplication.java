package com.ubanillx.smartclassbackendgateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class SmartclassBackendGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmartclassBackendGatewayApplication.class, args);
    }

}
