package com.example.shalehatbooking.model;

public class Favourite {
    private int id ;
    private String idChalet,idUser,imgChalet,nameChalet;
    private double price ;

    public Favourite() {
    }

    public Favourite(int id, String idChalet, String idUser, String imgChalet, String nameChalet, double price) {
        this.id = id;
        this.idChalet = idChalet;
        this.idUser = idUser;
        this.imgChalet = imgChalet;
        this.nameChalet = nameChalet;
        this.price = price;
    }

    public Favourite(String idChalet, String idUser, String imgChalet, String nameChalet, double price) {
        this.idChalet = idChalet;
        this.idUser = idUser;
        this.imgChalet = imgChalet;
        this.nameChalet = nameChalet;
        this.price = price;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIdChalet() {
        return idChalet;
    }

    public void setIdChalet(String idChalet) {
        this.idChalet = idChalet;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getImgChalet() {
        return imgChalet;
    }

    public void setImgChalet(String imgChalet) {
        this.imgChalet = imgChalet;
    }

    public String getNameChalet() {
        return nameChalet;
    }

    public void setNameChalet(String nameChalet) {
        this.nameChalet = nameChalet;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
