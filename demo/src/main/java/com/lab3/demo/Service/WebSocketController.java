package com.lab3.demo.Service;

import com.lab3.demo.Model.Product;
import com.lab3.demo.Service.service;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;

@Controller
public class WebSocketController {

    private final SimpMessagingTemplate messagingTemplate;
    private final service sservice;

    public WebSocketController(SimpMessagingTemplate messagingTemplate, service systemService) {
        this.messagingTemplate = messagingTemplate;
        this.sservice = systemService;
        System.out.println("connected");
    }

    @MessageMapping("/status")
    @SendTo("/topic/status")
    public void sendStatus(@Payload ArrayList<ArrayList<String>> id) {
//        sservice.machines.clear();
//        sservice.queues.clear();
//        sservice.threads.clear();
        for(String i: id.get(0)){
            sservice.addMachineTosystem(i);
        }
        for(String i: id.get(1)){
            sservice.addQueueTosystem(i);
        }

        for(int i = 1; i < id.get(2).size(); i++){
            sservice.connectQueueToMachine(id.get(2).get(i-1), id.get(2).get(i));
        }

        for(int i = 1; i < id.get(3).size(); i++){
            sservice.connectMachineToQueue(id.get(3).get(i-1), id.get(3).get(i));
        }
//        sservice.addMachineTosystem("1");
//        System.out.println(sservice.machines.get(0).getProcessingTime());
//        sservice.addQueueTosystem("2");  // Queue 2
//        sservice.addMachineTosystem("3");
//        System.out.println(sservice.machines.get(1).getProcessingTime());
//        sservice.addQueueTosystem("4");  // Queue 4 (Successor for both machines)
//        sservice.addMachineTosystem("5");
//
//        // Step 2: Connect machines and queues
//        // Queue 2 will be connected to both Machine 1 and Machine 3
//        sservice.connectQueueToMachine("2", "1"); // Machine 1 gets products from Queue 2
//        sservice.connectQueueToMachine("2", "3"); // Machine 3 gets products from Queue 2
//
//        // Both Machine 1 and Machine 3 will add products to Queue 4 after processing
//        sservice.connectMachineToQueue("1", "4"); // Machine 1 adds to Queue 4
//        sservice.connectMachineToQueue("3", "4"); // Machine 3 adds to Queue 4
//        sservice.connectQueueToMachine("4", "5");
//
//        // Step 3: Add products to Queue 2
        Product p1 = new Product(messagingTemplate); // Product with ID 5
        Product p2 = new Product(messagingTemplate); // Product with ID 6
        Product p3 = new Product(messagingTemplate);
        Product p4 = new Product(messagingTemplate);
        Product p5 = new Product(messagingTemplate);
        Product p6 = new Product(messagingTemplate);

        sservice.queues.get(0).addtoQueue(p1); // Add to Queue 2
        sservice.queues.get(0).addtoQueue(p2); // Add to Queue 2
        sservice.queues.get(0).addtoQueue(p3);
        sservice.queues.get(0).addtoQueue(p4);
        sservice.queues.get(0).addtoQueue(p5);
        sservice.queues.get(0).addtoQueue(p6);

        // Step 4: Simulate the system
        sservice.simulate();
    }

}
