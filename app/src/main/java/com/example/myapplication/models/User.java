package com.example.myapplication.models;

import java.util.List;

public class User {
    private String userID;
    private String userName;
    private String email;
    private String gender;
    private String phone;
    private String avata;
    private Boolean status;

    public User() {
    }

    public User(String userID, String userName, String email, String gender, String phone, String avata, Boolean status) {
        this.userID=userID;
        this.userName = userName;
        this.email = email;
        this.gender = gender;
        this.phone = phone;
        this.avata = avata;
        this.status = status;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String fullName) {
        this.userName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGender() {
        return gender;
    }


    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAvata() {
        return avata;
    }

    public void setAvata(String avata) {
        this.avata = avata;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }
}

