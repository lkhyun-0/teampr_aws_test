package com.aws.carepoint;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
public class CarepointApplication {

    public static void main(String[] args) {
        SpringApplication.run(CarepointApplication.class, args);
    }

}
