package com.example.myapplication.models;

public class Restaurant {
    private String resID;
    private String resName;
    private Integer resRate;
    private String resAvata;
    private String resType;
    private String resAddress;

    public Restaurant() {
    }

    public Restaurant(String resID, String resName, Integer resRate, String resAvata, String resType, String resAddress) {
        this.resID = resID;
        this.resName = resName;
        this.resRate = resRate;
        this.resAvata = resAvata;
        this.resType = resType;
        this.resAddress = resAddress;
    }

    public String getResID() {
        return resID;
    }

    public void setResID(String resID) {
        this.resID = resID;
    }

    public String getResName() {
        return resName;
    }

    public void setResName(String resName) {
        this.resName = resName;
    }

    public float getResRate() {
        return resRate;
    }

    public void setResRate(Integer resRate) {
        this.resRate = resRate;
    }

    public String getResAvata() {
        return resAvata;
    }

    public void setResAvata(String resAvata) {
        this.resAvata = resAvata;
    }

    public String getResType() {
        return resType;
    }

    public void setResType(String resType) {
        this.resType = resType;
    }

    public String getResAddress() {
        return resAddress;
    }

    public void setResAddress(String resAddress) {
        this.resAddress = resAddress;
    }
}
