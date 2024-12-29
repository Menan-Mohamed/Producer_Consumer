package com.lab3.demo.Model;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class ProductsQueue implements Observer{
    private int id ;
    private Queue<Product> queueProducts = new LinkedList<>();
    private ArrayList<Observable> observablesMachines = new ArrayList<>();;


    public int getId() {
        return id;
    }

    public void setId(int id) {
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

    public void addtoQueue(Product product){
        queueProducts.add(product);
    }

    public Product getproduct(){
        return queueProducts.poll();
    }

    @Override
    public void update(Observable observable) {
        ( (Machine) observable).takeNewProduct();
    }
}
