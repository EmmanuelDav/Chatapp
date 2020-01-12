package com.rad5.chatapp.Models;

public class Users {
    private String id;
    private String Username;
    private String ImageUrl;
    private String status;

    private Users(){}

    public Users(String id, String username, String imageUrl,String status) {
        this.id = id;
        Username = username;
        ImageUrl = imageUrl;
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public String getUsername() {
        return Username;
    }

    public String getImageUrl() {
        return ImageUrl;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public void setImageUrl(String imageUrl) {
        ImageUrl = imageUrl;
    }
}
