package com.lab3.demo.Service;

import com.lab3.demo.Model.Machine;
import com.lab3.demo.Model.Observable;
import com.lab3.demo.Model.Product;
import com.lab3.demo.Model.ProductsQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class service {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    public service(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }
    public void sendMessageToClients(String destination, String message) {
        messagingTemplate.convertAndSend(destination, message);
    }
    private void notifyStatusUpdate(String message) {
        messagingTemplate.convertAndSend("/topic/status", message);
}


    ArrayList<Thread> threads = new ArrayList<>();;
    ArrayList<Machine> machines = new ArrayList<>(); ;
    ArrayList<ProductsQueue> queues  = new ArrayList<>(); ;
    int numproducts = 5 ;

    public ArrayList<ProductsQueue> getQueues() {
        return queues;
    }

    public ArrayList<Machine> getMachines() {
        return machines;
    }

    public service(){

    }

    public void addQueueTosystem(int id) {
        ProductsQueue q = new ProductsQueue();
        q.setId(id);
        queues.add(q);
        if(id == 0){
            for (int i = 0; i < 5; i++) {
                Product p = new Product(i);
                q.addtoQueue(p);

            }

        }
    }

    public void addMachineTosystem(int id) {
        Machine m = new Machine(id);
        machines.add(m);
        System.out.println("Machine added ");
    }

    public void connectMachineToQueue(int fromid , int toid){
        for(Machine m : machines){
            if(m.getId() == fromid){
                for(ProductsQueue q : queues){
                    if(q.getId() == toid) {
                        m.setSuccessorQueue(q);
                    }
                }
            }
        }
    }

    public void connectQueueToMachine(int fromid , int toid){
        for(ProductsQueue q : queues){
            if(q.getId() == fromid){
                for(Machine m : machines){
                    if(m.getId() == toid){
                        q.addObservableMachine(m);
                    }
                }
            }
        }
        for(Machine m : machines){
            if(m.getId() == toid){
                for(ProductsQueue q : queues){
                    if(q.getId() == fromid) {
                        m.setObserver(q);
                    }
                }
            }
        }
    }

    public void getnumProducts (int num){
        this.numproducts = num;
    }

    public void simulate(){
        int threadPoolSize = 4;
        System.out.println("Creating thread pool with size: " + threadPoolSize);
        ExecutorService executorService = Executors.newFixedThreadPool(threadPoolSize);
        for (Machine machine: this.machines){
            Thread thread = new Thread(machine);
            threads.add(thread);
        }

        for (Thread thread: this.threads){
            thread.start();
        }
        executorService.shutdown();
        notifyStatusUpdate("Simulation started...");
    }

    public String getStatus() {
        return "offf";
    }
}
class Test {
    public static void main(String[] args) {
        service sservice = new service();

        // Step 1: Add machines and queues
        sservice.addMachineTosystem(1);
        System.out.println(sservice.machines.get(0).getProcessingTime());
        sservice.addQueueTosystem(2);  // Queue 2
        sservice.addMachineTosystem(3);
        System.out.println(sservice.machines.get(1).getProcessingTime());
        sservice.addQueueTosystem(4);  // Queue 4 (Successor for both machines)

        // Step 2: Connect machines and queues
        // Queue 2 will be connected to both Machine 1 and Machine 3
        sservice.connectQueueToMachine(2, 1); // Machine 1 gets products from Queue 2
        sservice.connectQueueToMachine(2, 3); // Machine 3 gets products from Queue 2

        // Both Machine 1 and Machine 3 will add products to Queue 4 after processing
        sservice.connectMachineToQueue(1, 4); // Machine 1 adds to Queue 4
        sservice.connectMachineToQueue(3, 4); // Machine 3 adds to Queue 4

        // Step 3: Add products to Queue 2
        Product p1 = new Product(5); // Product with ID 5
        Product p2 = new Product(6); // Product with ID 6
        sservice.queues.get(0).addtoQueue(p1); // Add to Queue 2
        sservice.queues.get(0).addtoQueue(p2); // Add to Queue 2

        // Step 4: Simulate the system
        sservice.simulate();

        ExecutorService executorService = Executors.newSingleThreadExecutor();

        // Monitor the system state at intervals
        executorService.submit(() -> {
            int checkCount = 0;
            while (checkCount < 25) { // Check 10 times before stopping
                try {
                    Thread.sleep(1000); // Wait for 1 second between checks
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }

                // Monitor queues and machines
                System.out.println("Final state of Queue 2:");
                System.out.println("Queue 2 is empty: " + sservice.queues.get(0).getQueueProducts().isEmpty());

                System.out.println("Final state of Queue 4:");
                System.out.println("Queue 4 is empty: " + sservice.queues.get(1).getQueueProducts().isEmpty());

                // Monitor each machine and its successor queue
                for (Machine machine : sservice.machines) {
                    System.out.println("Machine " + machine.getId() + " is ready: " + machine.isReady());
                    if (machine.getSuccessorQueue() != null) {
                        System.out.println("Successor queue for Machine " + machine.getId() + " contains products:");
                        while (!machine.getSuccessorQueue().getQueueProducts().isEmpty()) {
                            Product processedProduct = machine.getSuccessorQueue().getproduct();
                            System.out.println(processedProduct); // Print processed products
                        }
                    }
                }
                checkCount++;
            }
            // Shutdown the executor after completing the task
            executorService.shutdown();
        });
    }
}


