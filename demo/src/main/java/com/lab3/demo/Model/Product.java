package com.lab3.demo.Model;

import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class Product {
    private String color;
    private int id;

    private final SimpMessageSendingOperations messagingTemplate;

    public Product(SimpMessageSendingOperations messagingTemplate){
        this.messagingTemplate = messagingTemplate;
        this.color = generateRandomHexColor();
        this.id= (int) (Math.random()*100);
    }

    private String generateRandomHexColor() {
        Random random = new Random();

        int red = random.nextInt(230);
        int green = random.nextInt(230);
        int blue = random.nextInt(230);

        int variation = 100;
        red = clamp(red + random.nextInt(variation * 2 + 1) - variation);
        green = clamp(green + random.nextInt(variation * 2 + 1) - variation);
        blue = clamp(blue + random.nextInt(variation * 2 + 1) - variation);

        String hexColor = String.format("#%02X%02X%02X", red, green, blue);
        return hexColor;
    }

    private static int clamp(int value) {
        return Math.max(0, Math.min(255, value));
    }

    public String getColor() {
        return color;
    }

}
