package com.lab3.demo.Model;

public interface Observer {
    void update(Observable observable);
    String getId();
    int size();
}
