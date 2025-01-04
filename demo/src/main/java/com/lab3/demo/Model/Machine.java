package com.lab3.demo.Model;

import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.util.Random;



public class Machine implements Observable, Runnable {
    private String id;
    private int processingTime;
    private Observer observer;
    private boolean isReady;
    private Product currentProduct;
    private ProductsQueue successorQueue;

    public String getId() {
        return id;
    }


    public int getProcessingTime() {
        return processingTime;
    }

    public Machine(String id) {
        this.id = id;
        this.processingTime = new Random().nextInt(20001) + 5000;
        this.isReady = true;

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
            if (!((ProductsQueue) observer).getQueueProducts().isEmpty() && isReady){
                currentProduct = observedQueue.getproduct();
                isReady = false;
                break;
            }
            try {
                wait(); // Wait until a product is available
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }


    @Override
    public synchronized void run() {
        while (true) {
            System.out.println("here");

            if (isReady) {
                notifyObservers();
            }
            if (currentProduct != null) {
                processProduct(currentProduct);
                successorQueue.addtoQueue(currentProduct);
                currentProduct = null;
                isReady = true;
                notifyAll();
            }
        }
    }
}