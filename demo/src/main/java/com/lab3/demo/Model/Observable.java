package com.lab3.demo.Model;

public interface Observable {
    public void notifyObservers();
    public void addObserver(Observer observer);
    public String getId();
}
