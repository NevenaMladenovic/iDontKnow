package com.nevena.idontknow.Models;

public class User
{
    private String name, surname, nickname;
    private String email, userID;
    private Integer poens;

    //private int rang;

    public User()
    {
//        this.name="";
//        this.surname="";
//        this.nickname="";
//        this.email="";
//        this.poens=0;
    }

    public User(String name, String surname, String nickname, String email, String userID)
    {
        this.name = name;
        this.surname=surname;
        this.nickname=nickname;
        this.email = email;
        this.userID = userID;
        this.poens = 0;
    }

    public User(User u)
    {
        this.name = u.getName();
        this.surname= u.getSurname();
        this.nickname= u.getNickname();
        this.email = u.getEmail();
        this.poens = Integer.valueOf(u.getPoens());

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

    public String getPoens() { return String.valueOf(poens); }
    public void setPoens(Integer poens) { this.poens = poens;}

}
