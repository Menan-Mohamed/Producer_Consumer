package com.lab3.demo.Model;

public class RequestData {

    String color;
    String Machineid;
    String prevID;
    int prevsize;
    String nextID;

    public RequestData(String color, String machineid, String prevID, int prevsize, String nextID, int nextsize) {
        this.color = color;
        Machineid = machineid;
        this.prevID = prevID;
        this.prevsize = prevsize;
        this.nextID = nextID;
        this.nextsize = nextsize;
    }

    public String getPrevID() {
        return prevID;
    }

    public void setPrevID(String prevID) {
        this.prevID = prevID;
    }

    public String getNextID() {
        return nextID;
    }

    public void setNextID(String nextID) {
        this.nextID = nextID;
    }

    int nextsize;

    public int getPrevsize() {
        return prevsize;
    }

    public void setPrevsize(int prevsize) {
        this.prevsize = prevsize;
    }

    public int getNextsize() {
        return nextsize;
    }

    public void setNextsize(int nextsize) {
        this.nextsize = nextsize;
    }


    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getMachineid() {
        return Machineid;
    }

    public void setMachineid(String machineid) {
        Machineid = machineid;
    }


}
