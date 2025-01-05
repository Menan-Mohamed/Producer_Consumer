package com.lab3.demo.Model;

import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Component;
import java.lang.String;

import java.util.Random;

@Component
public class Machine implements Observable, Runnable {
    private String id;
    private int processingTime;
    private Observer observer;
    private boolean isReady;
    private Product currentProduct;
    private ProductsQueue successorQueue;
    private String color;

    private final SimpMessageSendingOperations messagingTemplate;

    public Machine(String id, SimpMessageSendingOperations messagingTemplate) {
        this.id = id;
        this.processingTime = new Random().nextInt(20001) + 5000;
        this.isReady = true;
        this.currentProduct = new Product(messagingTemplate);
        this.messagingTemplate = messagingTemplate;
        this.color = currentProduct.getColor();
    }

    public String getId() {
        return id;
    }

    public int getProcessingTime() {
        return processingTime;
    }

    @Override
    public void addObserver(Observer observer) {
        this.observer = observer;
    }

    public void setObserver(Observer observer) {
        this.observer = observer;
    }

    @Override
    public void notifyObservers() {
        observer.update(this);
    }

    public ProductsQueue getSuccessorQueue() {
        return successorQueue;
    }

    public boolean isReady() {
        return isReady;
    }

    public synchronized void setReady(boolean isReady) {
        this.isReady = isReady;
        notifyAll();
    }

    public void setSuccessorQueue(ProductsQueue successorQueue) {
        this.successorQueue = successorQueue;
    }

    public void processProduct(Product product) {
        System.out.println("Machine " + id + " is processing: " + product);
        try {
            Thread.sleep(processingTime);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public synchronized void takeNewProduct() {
        while (currentProduct == null && observer != null) {
            ProductsQueue observedQueue = (ProductsQueue) observer;
            if (!observedQueue.getQueueProducts().isEmpty() && isReady) {
                currentProduct = observedQueue.getproduct();
                this.color = currentProduct.getColor();
                isReady = false;
                break;
            }
            try {
                wait(); // Wait until a product is available
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        TempMachine tempMachine = new TempMachine();
        tempMachine.color = this.color;
        tempMachine.id = this.id;
        messagingTemplate.convertAndSend("/topic/status", tempMachine);

    }

    @Override
    public synchronized void run() {
        while (true) {
            System.out.println("Machine " + id + " is ready: " + isReady);

            if (isReady) {
                notifyObservers();
            }
            if (currentProduct != null) {
                processProduct(currentProduct);
                this.color = currentProduct.getColor();
                if (successorQueue != null) {
                    successorQueue.addtoQueue(currentProduct);
                }

                isReady = true;
                notifyAll();
            }
            TempMachine tempMachine = new TempMachine();
            tempMachine.color = this.color;
            tempMachine.id = this.id;
            messagingTemplate.convertAndSend("/topic/status", tempMachine);
        }
    }
}

class TempMachine{
    public String color;
    public String id;

}
