package com.example.shalehatbooking.model;

public class Message {
private String userEmail , messageBody,nameUser ;
private Long date;

    public Message() {

    }

    public Message(String userEmail, String messageBody,String nameUser, Long date) {
        this.userEmail = userEmail;
        this.messageBody = messageBody;
        this.date = date;
        this.nameUser = nameUser;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public String getMessageBody() {
        return messageBody;
    }

    public Long getDate() {
        return date;
    }

    public String getNameUser() {
        return nameUser;
    }

    public void setNameUser(String nameUser) {
        this.nameUser = nameUser;
    }
}
