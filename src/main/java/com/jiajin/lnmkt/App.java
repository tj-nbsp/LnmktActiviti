package com.jiajin.lnmkt;

import org.activiti.spring.boot.SecurityAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
public class App {
    
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }
    
}
