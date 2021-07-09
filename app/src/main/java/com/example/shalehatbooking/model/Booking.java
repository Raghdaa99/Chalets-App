package com.example.shalehatbooking.model;

import com.google.firebase.firestore.DocumentId;

import java.io.Serializable;

public class Booking implements Serializable {
    @DocumentId
    private String id;
    private String nameChalet,userId, chaletId,date,status,token;
    private int numberOfDays;
    private double TotalPrice;

    public Booking() {
    }

    public Booking(String id, String nameChalet, String userId, String chaletId, String date, String status, String token, int numberOfDays, double totalPrice) {
        this.id = id;
        this.nameChalet = nameChalet;
        this.userId = userId;
        this.chaletId = chaletId;
        this.date = date;
        this.status = status;
        this.token = token;
        this.numberOfDays = numberOfDays;
        TotalPrice = totalPrice;
    }

    public Booking(String nameChalet, String userId, String chaletId, String date, String status, String token, int numberOfDays, double totalPrice) {
        this.nameChalet = nameChalet;
        this.userId = userId;
        this.chaletId = chaletId;
        this.date = date;
        this.status = status;
        this.token = token;
        this.numberOfDays = numberOfDays;
        TotalPrice = totalPrice;
    }

    public String getNameChalet() {
        return nameChalet;
    }

    public void setNameChalet(String nameChalet) {
        this.nameChalet = nameChalet;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getChaletId() {
        return chaletId;
    }

    public void setChaletId(String chaletId) {
        this.chaletId = chaletId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getNumberOfDays() {
        return numberOfDays;
    }

    public void setNumberOfDays(int numberOfDays) {
        this.numberOfDays = numberOfDays;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getTotalPrice() {
        return TotalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        TotalPrice = totalPrice;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
