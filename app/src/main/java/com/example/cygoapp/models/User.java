package com.example.cygoapp.models;

import java.util.List;

public class User {

    private String uid;
    private String name;
    private String contact;
    private String email;
    private String password;
    private String nic;
    private String imgUri;
    private String bio;
    private boolean profileCreated;
    private List<String> bookedRides;
    private float rating;
    private int ratingAmount;

    public User() {
    }

    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public User(String name, String contact, String email, String nic) {
        this.name = name;
        this.contact = contact;
        this.email = email;
        this.nic = nic;
    }

    public User(String uid, String name, String contact, String email, String password, String nic, String imgUri, String bio, boolean profileCreated, List<String> bookedRides, float rating, int ratingAmount) {
        this.uid = uid;
        this.name = name;
        this.contact = contact;
        this.email = email;
        this.password = password;
        this.nic = nic;
        this.imgUri = imgUri;
        this.bio = bio;
        this.profileCreated = profileCreated;
        this.bookedRides = bookedRides;
        this.rating = rating;
        this.ratingAmount = ratingAmount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNic() {
        return nic;
    }

    public void setNic(String nic) {
        this.nic = nic;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getImgUri() {
        return imgUri;
    }

    public void setImgUri(String imgUri) {
        this.imgUri = imgUri;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public boolean isProfileCreated() {
        return profileCreated;
    }

    public void setProfileCreated(boolean profileCreated) {
        this.profileCreated = profileCreated;
    }

    public List<String> getBookedRides() {
        return bookedRides;
    }

    public void setBookedRides(List<String> bookedRides) {
        this.bookedRides = bookedRides;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public int getRatingAmount() {
        return ratingAmount;
    }

    public void setRatingAmount(int ratingAmount) {
        this.ratingAmount = ratingAmount;
    }
}