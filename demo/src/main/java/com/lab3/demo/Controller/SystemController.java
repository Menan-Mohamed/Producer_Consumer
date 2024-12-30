package com.lab3.demo.Controller;

import com.lab3.demo.Model.Machine;
import com.lab3.demo.Model.Product;
import com.lab3.demo.Service.service;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class SystemController {

     service serv;

    public SystemController(service serv) {
        this.serv = serv;
    }


    @PostMapping("/addMachine")
    public void addMachine(@RequestParam int id) {
        serv.addMachineTosystem(id);
    }


    @PostMapping("/addQueue")
    public void addQueue(@RequestParam int id) {
        serv.addQueueTosystem(id);
    }


    @PostMapping("/connectMachineToQueue")
    public void connectMachineToQueue(@RequestParam int fromId, @RequestParam int toId) {
        serv.connectMachineToQueue(fromId, toId);
    }

    @PostMapping("/connectQueueToMachine")
    public void connectQueueToMachine(@RequestParam int fromId, @RequestParam int toId) {
        serv.connectQueueToMachine(fromId, toId);
    }

    @PostMapping("/addProductToQueue")
    public void addProductToQueue(@RequestParam int queueId, @RequestParam int productId) {
        Product product = new Product(productId);
        serv.getQueues().get(queueId).addtoQueue(product);
    }

    @PostMapping("/startSimulation")
    public void startSimulation() {
        serv.simulate();
    }


    @GetMapping("/status")
    public String getStatus() {
        StringBuilder status = new StringBuilder();
        for (Machine machine : serv.getMachines()) {
            status.append("Machine ").append(machine.getId()).append(" is ready: ").append(machine.isReady()).append("\n");
            status.append("Successor queue for Machine ").append(machine.getId()).append(": ");
            if (machine.getSuccessorQueue() != null) {
                status.append("Queue contains ").append(machine.getSuccessorQueue().getQueueProducts().size()).append(" products.\n");
            }
        }
        return status.toString();
    }
}
