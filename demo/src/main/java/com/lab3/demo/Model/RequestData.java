package com.lab3.demo.Model;

public class RequestData {

    String color;
    String Machineid;

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

    public RequestData(String color, String machineid) {
        this.color = color;
        Machineid = machineid;
    }
}
