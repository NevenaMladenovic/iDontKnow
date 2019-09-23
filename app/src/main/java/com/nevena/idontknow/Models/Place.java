package com.nevena.idontknow.Models;

public class Place
{

    private String name;
    private String type;
    private String thumbnailUrl;
    private String address;
    private String workingHours;
    private double rate;
    private String placetID;

    private double latitude;
    private double longitude;

    private Review review;

    public Place()
    {
//        this.name = "";
//        this.thumbnailUrl = "";
//        this.address = "";
//        this.workingHours = "";
//        this.rate = 0;
    }

    public Place(String name, String type, String thumbnailUrl, String address, String workingHours, double rate)
    {
        this.name = name;
        this.type = type;
        this.thumbnailUrl = thumbnailUrl;
        this.address = address;
        this.workingHours = workingHours;
        this.rate = rate;
    }

    public Place(String name, String type, String thumbnailUrl, String address, String workingHours, double rate, double latitude, double longitude)
    {
        this.name = name;
        this.type = type;
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
        this.type = place.type;
        this.thumbnailUrl = place.thumbnailUrl;
        this.address = place.address;
        this.workingHours = place.workingHours;
        this.rate = place.rate;
        this.latitude = place.latitude;
        this.longitude = place.longitude;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public Review getReview() {
        return review;
    }

    public void setReview(Review review) {
        this.review = review;
    }
}
