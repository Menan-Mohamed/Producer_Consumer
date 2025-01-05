package com.lab3.demo.Model;

import com.lab3.demo.Service.WebSocketService;

import java.util.Random;
import java.util.concurrent.ExecutorService;

public class Machine implements Observable, Runnable {
    private String id;
    private int processingTime;

    public Observer getObserver() {
        return observer;
    }

    private Observer observer;
    private boolean isReady;
    private Product currentProduct;
    private ProductsQueue successorQueue;
    private final WebSocketService webSocketService;
    ExecutorService executorService;

    public void setExecutorService(ExecutorService executorService) {
        this.executorService = executorService;
    }



    public String getId() {
        return id;
    }


    public int getProcessingTime() {
        return processingTime;
    }

    public Machine(String id, WebSocketService webSocketService) {
        this.id = id;
        this.webSocketService = webSocketService;
        //this.processingTime = new Random().nextInt(20001) + 5000;
        this.processingTime = 2000;
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
        System.out.println("Machine " + id + " attempting to take a product.");
        while (currentProduct == null) {
            ProductsQueue observedQueue = (ProductsQueue) observer;
            synchronized (observedQueue) {
                if (!observedQueue.getQueueProducts().isEmpty() && isReady) {
                    currentProduct = observedQueue.getproduct();
                    if (currentProduct != null) {
                        System.out.println("Machine " + id + " took product: " + currentProduct.getColor());
                        isReady = false;
                        break;
                    }
                }
                try {
                    observedQueue.wait(); // Wait for the queue to notify
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        }
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            synchronized (this) {
                if (isReady && currentProduct == null) {
                    takeNewProduct();
                }

                if (currentProduct != null && !isReady) {
                    System.out.println(id + " is working");
                    RequestData data = new RequestData(currentProduct.getColor(), id);
                    webSocketService.sendJsonMessage(data);
                    processProduct(currentProduct);
                    successorQueue.addtoQueue(currentProduct);
                    currentProduct = null;
                    isReady = true;
                    notifyObservers();
                    //notifyAll();
                }
            }

            // Small sleep to prevent busy waiting
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

    }
}