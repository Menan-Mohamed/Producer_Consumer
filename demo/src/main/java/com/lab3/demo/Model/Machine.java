package com.lab3.demo.Model;

import java.util.Random;


public class Machine implements Observable, Runnable {
    private int id;
    private int processingTime;
    private Observer observer;
    private boolean isReady;
    private Product currentProduct;
    private ProductsQueue successorQueue;

    public int getId() {
        return id;
    }


    public int getProcessingTime() {
        return processingTime;
    }

    public Machine(int id) {
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

    public void setReady(boolean isReady) {
        this.isReady = isReady;
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
        Product tempProduct = null;

        if (!((ProductsQueue) observer).getQueueProducts().isEmpty() && isReady){
            tempProduct = ((ProductsQueue) observer).getproduct();
            isReady = false;
        }
        currentProduct = tempProduct;
    }

    @Override
    public void run() {
        while (true) {

            if (isReady) {
                notifyObservers();
            }
            if (currentProduct != null) {
                processProduct(currentProduct);
                successorQueue.addtoQueue(currentProduct);
                currentProduct = null;
                isReady = true;
            }
        }
    }
}