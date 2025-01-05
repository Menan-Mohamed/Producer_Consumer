package com.lab3.demo.Model;

import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

@Component
public class ProductsQueue implements Observer{
    private String id ;
    private Queue<Product> queueProducts = new LinkedList<>();
    private ArrayList<Observable> observablesMachines = new ArrayList<>();



    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Queue<Product> getQueueProducts() {
        return queueProducts;
    }

    public void setQueueProducts(Queue<Product> queueProducts) {
        this.queueProducts = queueProducts;
    }

    public ArrayList<Observable> getObservablesMachines() {
        return observablesMachines;
    }

    public void setObservablesMachines(ArrayList<Observable> observablesMachines) {
        this.observablesMachines = observablesMachines;
    }

    public void addObservableMachine(Observable m){
        observablesMachines.add(m);
    }

    public synchronized  void addtoQueue(Product product){
        queueProducts.add(product);
        notifyAll();
    }

    public synchronized Product getproduct() {
        while (queueProducts.isEmpty()) {
            try {
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return null;
            }
        }

        return queueProducts.poll();
    }

    @Override
    public void update(Observable observable) {
        ( (Machine) observable).takeNewProduct();
    }
}
