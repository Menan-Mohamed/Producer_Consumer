package com.lab3.demo.Model;

import java.util.ArrayList;

public class Memento {

    ArrayList<Product> productarr ;
    ArrayList<Integer> rate ;

    public Memento() {
        this.productarr = new ArrayList<>();
        this.rate = new ArrayList<>();
    }

    public synchronized void addToMemento (Product p){
        productarr.add(p);
    }

    public ArrayList<Product> getProductarr() {
        return productarr;
    }

    public ArrayList<Integer> getRate() {
        return rate;
    }

    public synchronized void addRate (int t){
        rate.add(t);
    }
    public synchronized int size(){
        return productarr.size();
    }
}
