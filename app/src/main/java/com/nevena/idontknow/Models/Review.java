package com.nevena.idontknow.Models;

public class Review
{
    private String comment;
    private int rate;
    private String userID;

    public Review()
    {}

    public Review(String comment, int rate, String userID)
    {
        this.comment = comment;
        this.rate = rate;
        this.userID = userID;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

}
