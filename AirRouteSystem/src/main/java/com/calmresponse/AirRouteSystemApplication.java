package com.calmresponse;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AirRouteSystemApplication {

    public static void main(String[] args) {
        System.setProperty("java.security.egd", "file:/dev/./urandom");
        SpringApplication.run(AirRouteSystemApplication.class, args);
    }
}
