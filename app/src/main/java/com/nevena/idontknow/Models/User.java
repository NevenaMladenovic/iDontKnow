package com.nevena.idontknow.Models;

public class User
{
    private String name, surname, nickname;
    private String email, userID;
    private int poens;

    private double latitude;
    private double longitude;


    //private int rang;

    public User()
    {
//        this.name="";
//        this.surname="";
//        this.nickname="";
//        this.email="";
//        this.poens=0;
    }

    public User(String name, String surname, String nickname, String email, String userID, double latitude, double longitude)
    {
        this.name = name;
        this.surname=surname;
        this.nickname=nickname;
        this.email = email;
        this.userID = userID;
        this.poens = 0;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public User(User u)
    {
        this.name = u.getName();
        this.surname= u.getSurname();
        this.nickname= u.getNickname();
        this.email = u.getEmail();
        this.poens = u.getPoens();
        this.latitude = u.getLatitude();
        this.longitude = u.getLongitude();

    }

    public String getUserID() { return userID; }
    public void setUserID(String userID) { this.userID = userID;}

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSurname() { return surname; }
    public void setSurname(String surname) { this.surname = surname;}

    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname;}

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email;}

    public int getPoens() { return poens; }
    public void setPoens(int poens) { this.poens = poens;}

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
