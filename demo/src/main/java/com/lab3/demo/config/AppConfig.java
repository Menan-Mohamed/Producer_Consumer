package com.lab3.demo.config;

import com.lab3.demo.Model.Machine;
import com.lab3.demo.Model.Product;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.SimpMessagingTemplate;

@Configuration
public class AppConfig {

    @Bean
    public Machine machine(SimpMessagingTemplate messagingTemplate) {
        return new Machine("machineId", messagingTemplate);
    }

    @Bean
    public Product product(SimpMessagingTemplate messagingTemplate) {
        return new Product(messagingTemplate);
    }

}
