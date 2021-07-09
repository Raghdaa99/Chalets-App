package com.example.shalehatbooking.model;

public class Rating {

    private String rateValue,comment;

    public Rating() {
    }

    public Rating( String rateValue, String comment) {

        this.rateValue = rateValue;
        this.comment = comment;
    }

    public String getRateValue() {
        return rateValue;
    }

    public void setRateValue(String rateValue) {
        this.rateValue = rateValue;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
