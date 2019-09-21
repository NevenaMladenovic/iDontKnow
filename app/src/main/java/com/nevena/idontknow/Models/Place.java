package com.nevena.idontknow.Models;

public class Place
{

    private String name;
    private String thumbnailUrl;
    private String address;
    private String workingHours;
    private double rate;
    private String placetID;


    private double latitude;
    private double longitude;

    public Place()
    {
//        this.name = "";
//        this.thumbnailUrl = "";
//        this.address = "";
//        this.workingHours = "";
//        this.rate = 0;
    }

    public Place(String name, String thumbnailUrl, String address, String workingHours, double rate)
    {
        this.name = name;
        this.thumbnailUrl = thumbnailUrl;
        this.address = address;
        this.workingHours = workingHours;
        this.rate = rate;
    }

    public Place(String name, String thumbnailUrl, String address, String workingHours, double rate, double latitude, double longitude)
    {
        this.name = name;
        this.thumbnailUrl = thumbnailUrl;
        this.address = address;
        this.workingHours = workingHours;
        this.rate = rate;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Place(Place place)
    {
        this.name = place.name;
        this.thumbnailUrl = place.thumbnailUrl;
        this.address = place.address;
        this.workingHours = place.workingHours;
        this.rate = place.rate;
    }

    public Place(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getThumbnailUrl()
    {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl)
    {
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getAddress()
    {
        return address;
    }

    public void setAddress(String address)
    {
        this.address = address;
    }

    public String getWorkingHours()
    {
        return workingHours;
    }

    public void setWorkingHours(String workingHours)
    {
        this.workingHours = workingHours;
    }

    public double getRate()
    {
        return rate;
    }

    public void setRate(double rate)
    {
        this.rate = rate;
    }

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
