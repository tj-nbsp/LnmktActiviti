package com.jiajin.lnmkt;

import java.util.Arrays;

import org.activiti.spring.boot.SecurityAutoConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
public class App {
    
    public static final Logger log = LoggerFactory.getLogger(App.class);
    
    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(App.class, args);
        log.info("spring started. profiles: {}", Arrays.asList(context.getEnvironment().getActiveProfiles()));
    }
    
}
