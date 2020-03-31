package com.rad5.chatapp.Models;

public class Users {
    private String id;
    private String Username;
    private String ImageUrl;
    private String status;
    private String UserToken;

    private Users() {
    }

    public Users(String id, String username, String imageUrl, String status, String userToken) {
        this.id = id;
        Username = username;
        ImageUrl = imageUrl;
        this.status = status;
        UserToken = userToken;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public String getImageUrl() {
        return ImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        ImageUrl = imageUrl;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUserToken() {
        return UserToken;
    }

    public void setUserToken(String userToken) {
        UserToken = userToken;
    }
}