package com.nevena.idontknow.Models;

public class User
{
    private String name, surname, nickname;
    private String email, userID;

    //private int rang;

    public User()
    {
        this.name="";
        this.surname="";
        this.nickname="";
        this.email="";

    }

    public User(String name, String surname, String nickname, String email)
    {
        this.name = name;
        this.surname=surname;
        this.nickname=nickname;
        this.email = email;

    }

    public User(String name, String surname, String nickname, String email, String userID)
    {
        this.name = name;
        this.surname=surname;
        this.nickname=nickname;
        this.email = email;
        this.userID = userID;

    }

    public User(User u)
    {
        this.name = u.getName();
        this.surname= u.getSurname();
        this.nickname= u.getNickname();
        this.email = u.getEmail();
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


}
