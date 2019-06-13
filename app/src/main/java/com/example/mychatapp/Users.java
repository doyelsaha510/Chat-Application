package com.example.mychatapp;

public class Users {
    public String name;
    public String image;
    public String status;
    public String id;
    private String token;


    public Users(String name, String image, String status, String id,String token) {
        this.name = name;
        this.image = image;
        this.status = status;
        this.id = id;
        this.token=token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Users() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


}
