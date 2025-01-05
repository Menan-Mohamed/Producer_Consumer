package com.lab3.demo.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lab3.demo.Model.Machine;
import com.lab3.demo.Model.Observable;
import com.lab3.demo.Model.Product;
import com.lab3.demo.Model.ProductsQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class service {
    private WebSocketService webSocketService = new WebSocketService();
    private void notifyStatusUpdate(String message) {
        //messagingTemplate.convertAndSend("/topic/status", message);
    }

    public void setWebSocketService(WebSocketService webSocketService) {
        this.webSocketService = webSocketService;
    }
    public void setWebSocketSession(WebSocketSession session) {
        this.webSocketService.setSession(session);
    }


    ArrayList<Thread> threads = new ArrayList<>();

    ArrayList<Machine> machines = new ArrayList<>();

    ArrayList<ProductsQueue> queues = new ArrayList<>();

//    int numproducts = new Random().nextInt(10) + 5;
    int numproducts = 5;

    public ArrayList<ProductsQueue> getQueues() {
        return queues;
    }

    public ArrayList<Machine> getMachines() {
        return machines;
    }

    public service() {

    }

    public void addQueueTosystem(String id) {
        ProductsQueue q = new ProductsQueue();
        q.setId(id);
        queues.add(q);
        if (id.equals("0")) {
            for (int i = 0; i < numproducts; i++) {
                Product p = new Product(i);
                System.out.println("product added with color"+ p.getColor());
                q.addtoQueue(p);
            }

        }
    }

    public void addMachineTosystem(String id) {
        Machine m = new Machine(id, webSocketService);
        machines.add(m);
        System.out.println("Machine added  "+ m.getProcessingTime());

    }

    public void connectMachineToQueue(String fromid, String toid) {
        for (Machine m : machines) {
            if (m.getId().equals(fromid)) {
                for (ProductsQueue q : queues) {
                    if (q.getId().equals(toid)) {
                        m.setSuccessorQueue(q);
                    }
                }
            }
        }
    }

    public void connectQueueToMachine(String fromid, String toid) {
        for (ProductsQueue q : queues) {
            if (q.getId().equals(fromid)) {
                for (Machine m : machines) {
                    if (m.getId().equals(toid)) {
                        q.addObservableMachine(m);
                    }
                }
            }
        }
        for (Machine m : machines) {
            if (m.getId().equals(toid)) {
                for (ProductsQueue q : queues) {
                    if (q.getId().equals(fromid)) {
                        m.setObserver(q);
                    }
                }
            }
        }
    }

    public void getnumProducts(int num) {
        this.numproducts = num;
    }

    public void simulate() {
//        for(ProductsQueue m: this.queues){
//            System.out.println("succesoor for m"+m.getObservablesMachines().get(0));
//           // System.out.println("abl m"+m.getObserver());
//        }
        int threadPoolSize = 10;
        System.out.println("Creating thread pool with size: " + threadPoolSize);
        ExecutorService executorService = Executors.newFixedThreadPool(threadPoolSize);
        for (Machine machine : this.machines) {
            System.out.println(machine.getId() +"dddd");
//            Thread thread = new Thread(machine);
//            threads.add(thread);
            machine.setExecutorService(executorService);
            executorService.submit(machine);
        }

//        for (Thread thread : this.threads) {
//            thread.start();
//            System.out.println("thread is starting");
//        }
        executorService.shutdown();
        notifyStatusUpdate("Simulation started...");
    }

    public String getStatus() {
        return "offf";
    }

}
//class Test {
//    public static void main(String[] args) {
//        service sservice = new service();
//
//        // Step 1: Add machines and queues
//        sservice.addMachineTosystem(1);
//        System.out.println(sservice.machines.get(0).getProcessingTime());
//        sservice.addQueueTosystem(2);  // Queue 2
//        sservice.addMachineTosystem(3);
//        System.out.println(sservice.machines.get(1).getProcessingTime());
//        sservice.addQueueTosystem(4);  // Queue 4 (Successor for both machines)
//
//        // Step 2: Connect machines and queues
//        // Queue 2 will be connected to both Machine 1 and Machine 3
//        sservice.connectQueueToMachine(2, 1); // Machine 1 gets products from Queue 2
//        sservice.connectQueueToMachine(2, 3); // Machine 3 gets products from Queue 2
//
//        // Both Machine 1 and Machine 3 will add products to Queue 4 after processing
//        sservice.connectMachineToQueue(1, 4); // Machine 1 adds to Queue 4
//        sservice.connectMachineToQueue(3, 4); // Machine 3 adds to Queue 4
//
//        // Step 3: Add products to Queue 2
//        Product p1 = new Product(5); // Product with ID 5
//        Product p2 = new Product(6); // Product with ID 6
//        sservice.queues.get(0).addtoQueue(p1); // Add to Queue 2
//        sservice.queues.get(0).addtoQueue(p2); // Add to Queue 2
//
//        // Step 4: Simulate the system
//        sservice.simulate();
//
//        ExecutorService executorService = Executors.newSingleThreadExecutor();
//
//        // Monitor the system state at intervals
//        executorService.submit(() -> {
//            int checkCount = 0;
//            while (checkCount < 25) { // Check 10 times before stopping
//                try {
//                    Thread.sleep(1000); // Wait for 1 second between checks
//                } catch (InterruptedException e) {
//                    Thread.currentThread().interrupt();
//                }
//
//                // Monitor queues and machines
//                System.out.println("Final state of Queue 2:");
//                System.out.println("Queue 2 is empty: " + sservice.queues.get(0).getQueueProducts().isEmpty());
//
//                System.out.println("Final state of Queue 4:");
//                System.out.println("Queue 4 is empty: " + sservice.queues.get(1).getQueueProducts().isEmpty());
//
//                // Monitor each machine and its successor queue
//                for (Machine machine : sservice.machines) {
//                    System.out.println("Machine " + machine.getId() + " is ready: " + machine.isReady());
//                    if (machine.getSuccessorQueue() != null) {
//                        System.out.println("Successor queue for Machine " + machine.getId() + " contains products:");
//                        while (!machine.getSuccessorQueue().getQueueProducts().isEmpty()) {
//                            Product processedProduct = machine.getSuccessorQueue().getproduct();
//                            System.out.println(processedProduct); // Print processed products
//                        }
//                    }
//                }
//                checkCount++;
//            }
//            // Shutdown the executor after completing the task
//            executorService.shutdown();
//        });
//    }
//}
//
//
