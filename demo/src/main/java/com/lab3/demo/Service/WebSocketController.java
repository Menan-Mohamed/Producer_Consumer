package com.lab3.demo.Service;

import com.lab3.demo.Service.service;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketController {

    private final SimpMessagingTemplate messagingTemplate;
    private final service systemService;

    public WebSocketController(SimpMessagingTemplate messagingTemplate, service systemService) {
        this.messagingTemplate = messagingTemplate;
        this.systemService = systemService;
    }


    @MessageMapping("/status")
    @SendTo("/topic/status")
    public String sendStatus() {
        return systemService.getStatus();
    }

}
