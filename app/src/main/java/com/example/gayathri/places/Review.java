package com.example.gayathri.places;

public class Review {
    private String name, time_created, rating, photo_url, text, user_url;
    public Review(){

    }

    public Review(String name, String time_created, String rating, String photo_url, String text, String user_url) {
        this.name = name;
        this.time_created = time_created;
        this.rating = rating;
        this.photo_url = photo_url;
        this.text = text;
        this.user_url = user_url;
    }

    public String getUser_url() {
        return user_url;
    }

    public void setUser_url(String user_url) {
        this.user_url = user_url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTime_created() {
        return time_created;
    }

    public void setTime_created(String time_created) {
        this.time_created = time_created;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getPhoto_url() {
        return photo_url;
    }

    public void setPhoto_url(String photo_url) {
        this.photo_url = photo_url;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
