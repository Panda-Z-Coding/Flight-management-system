package com.calmresponse;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement//开启事务注解
public class AirRouteSystemApplication {

    public static void main(String[] args) {
        System.setProperty("java.security.egd", "file:/dev/./urandom");
        SpringApplication.run(AirRouteSystemApplication.class, args);
    }
}
