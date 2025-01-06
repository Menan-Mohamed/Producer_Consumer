package com.lab3.demo.Model;

import com.lab3.demo.Service.WebSocketService;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;

public class Machine implements Observable, Runnable {
    private final String id;
    private final int processingTime;
    private final List<ProductsQueue> prevQueues = new ArrayList<>();
    private ProductsQueue successorQueue;
    private boolean isReady;
    private boolean resumilate = false;
    private Product currentProduct;
    private final WebSocketService webSocketService;
    private ExecutorService executorService;
    private String prevID;



    public boolean isResumilate() {
        return resumilate;
    }

    public void setResumilate(boolean resumilate) {
        this.resumilate = resumilate;
    }



    public Product getCurrentProduct() {
        return currentProduct;
    }

    public void setCurrentProduct(Product currentProduct) {
        this.currentProduct = currentProduct;
    }

    public Machine(String id, WebSocketService webSocketService) {
        this.id = id;
        this.webSocketService = webSocketService;
        this.processingTime = new Random().nextInt(10001) + 5000;
//        this.processingTime = 2000;
        this.isReady = true;
    }

    public String getId() {
        return id;
    }

    public void setObserver(ProductsQueue queue) {
        addPrevQueue(queue);
    }


    public int getProcessingTime() {
        return processingTime;
    }

    public void setExecutorService(ExecutorService executorService) {
        this.executorService = executorService;
    }

    public ProductsQueue getSuccessorQueue() {
        return successorQueue;
    }

    public void setSuccessorQueue(ProductsQueue successorQueue) {
        this.successorQueue = successorQueue;
    }

    public synchronized boolean isReady() {
        return isReady;
    }

    public synchronized void setReady(boolean isReady) {
        this.isReady = isReady;
        notifyAll();
    }

    public void addPrevQueue(ProductsQueue queue) {
        prevQueues.add(queue);
    }

    public void removePrevQueue(ProductsQueue queue) {
        prevQueues.remove(queue);
    }

    public List<ProductsQueue> getPrevQueues() {
        return prevQueues;
    }

    public void processProduct(Product product) {
        System.out.println("Machine " + id + " is processing: " + product);
        try {
            if(!resumilate){
                for(int i = 0 ; i<processingTime ; i+=10) {

                    if(resumilate){

                        break;
                    }
                    Thread.sleep(10);
                }

            }
            else{
                for(int i = 0 ; i<processingTime ; i+=10) {

                    if(!resumilate){

                        break;
                    }
                    Thread.sleep(10);
                }

            }
            RequestData data = new RequestData("gray", id, "", 0,successorQueue.getId(),successorQueue.size());
            webSocketService.sendJsonMessage(data); }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public synchronized void takeNewProduct() {
        System.out.println("Machine " + id + " attempting to take a product.");

        if (prevQueues.isEmpty()) {
            System.out.println("No queues available for Machine " + id);
            return;
        }

        Random random = new Random();
        int randomIndex = random.nextInt(prevQueues.size());



        for (int i = 0; i < prevQueues.size(); i++) {
            ProductsQueue queue = prevQueues.get((randomIndex + i) % prevQueues.size());
            synchronized (queue) {
                if (!queue.getQueueProducts().isEmpty() && isReady) {
                    currentProduct = queue.getproduct();
                    if (currentProduct != null) {
                        System.out.println("Machine " + id + " took product: " + currentProduct.getColor() + " from queue " + queue.getId());
                        isReady = false;
                        prevID = queue.getId();
                        break;
                    }
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
                    RequestData data = new RequestData(currentProduct.getColor(), id, null,0 ,successorQueue.getId(),successorQueue.size());
                    webSocketService.sendJsonMessage(data);
                    processProduct(currentProduct);
                    successorQueue.addtoQueue(currentProduct);
                    currentProduct = null;
                    setReady(true);
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

    @Override
    public synchronized void addObserver(Observer observer) {
        if (observer instanceof ProductsQueue) {
            addPrevQueue((ProductsQueue) observer);
        }
    }

    @Override
    public synchronized void notifyObservers() {
        for (ProductsQueue queue : prevQueues) {
            queue.update(this);
        }
    }
}