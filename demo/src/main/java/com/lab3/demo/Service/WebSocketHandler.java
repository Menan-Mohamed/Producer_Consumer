package com.lab3.demo.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lab3.demo.Model.Machine;
import com.lab3.demo.Model.Product;
import com.lab3.demo.Model.ProductsQueue;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class WebSocketHandler extends TextWebSocketHandler {

    private  service simulatorService;

    // Inject the service using constructor injection
    public WebSocketHandler() {

    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
        String incomingMessage = message.getPayload();
        System.out.println(incomingMessage);
        if (incomingMessage.equals("true")) {
            if (simulatorService != null) {
                simulatorService.getExecutorService().shutdown();
                simulatorService.setResimulateFlag(true);
                simulatorService.getQueues().forEach(queue -> queue.getQueueProducts().clear());
                simulatorService.simulate();
                session.sendMessage(new TextMessage("Resimulation toggled and started!"));
            } else {
                session.sendMessage(new TextMessage("Simulator service is not initialized!"));
            }
            return;
        }


        simulatorService = new service();

        simulatorService.setWebSocketSession(session);

        simulatorService.getQueues().forEach(queue -> queue.getQueueProducts().clear());
        System.out.println("Received message: " + incomingMessage);

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> messageMap = objectMapper.readValue(incomingMessage, Map.class);

        System.out.println("Parsed message map: " + messageMap);
        try {
            if (messageMap.containsKey("nodes")) {
                List<Map<String, Object>> nodes = (List<Map<String, Object>>) messageMap.get("nodes");

                for (Map<String, Object> node : nodes) {
                    String id = (String) node.get("id");
                    String type = (String) node.get("type");

                    if ("mNode".equals(type)) {

                        simulatorService.addMachineTosystem(id);
                        System.out.println("Added machine: " + id );
                    } else if ("qNode".equals(type)) {
                        simulatorService.addQueueTosystem(id);
                        System.out.println("Added queue: " + id);
                    }
                }
            }
            System.out.println(simulatorService.getQueues());
            System.out.println(simulatorService.getMachines());
            if (messageMap.containsKey("edges")) {
                List<Map<String, Object>> edges = (List<Map<String, Object>>) messageMap.get("edges");

                for (Map<String, Object> edge : edges) {
                    String sourceId = (String) edge.get("source");
                    String targetId = (String) edge.get("target");
                    List<Machine>machines=simulatorService.getMachines();
                    List<ProductsQueue> queues = simulatorService.getQueues();
                    boolean isMachine= false;
                    for(Machine m:machines){
                        if(m.getId().equals(sourceId)){
                            isMachine = true;
                            break;
                        }

                    }
                    // Call the method to connect machines to queues
                    if(isMachine){
                    simulatorService.connectMachineToQueue(sourceId, targetId);
                    }else{
                        simulatorService.connectQueueToMachine(sourceId, targetId);
                    }
                }

                System.out.println("Processed edges:");
                System.out.println(edges);
            }
            simulatorService.simulate();
            session.sendMessage(new TextMessage("Simulation started!"));

        } catch (RuntimeException e) {
            session.sendMessage(new TextMessage("Simulation failed!"));
        }

    }
}
