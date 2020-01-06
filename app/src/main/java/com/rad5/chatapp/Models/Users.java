package com.rad5.chatapp.Models;

public class Users {
    private String id;
    private String Username;
    private String ImageUrl;

    private Users(){}

    public Users(String id, String username, String imageUrl) {
        this.id = id;
        Username = username;
        ImageUrl = imageUrl;
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
