package com.rad5.chatapp.FCM;

import androidx.annotation.NonNull;

public class Data {
    public String message;
    public String Useriid;

    public Data(String message, String useriid) {
        this.message = message;
        Useriid = useriid;
    }

    public Data() {

    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String userID() {
        return Useriid;
    }

    public void setuserID(String useriid) {
        Useriid = useriid;
    }

    @Override
    public String toString() {
        return "data{" +
                "message='" + message + '\'' +
                ", userid='" + Useriid + '\'' +
                '}';
    }
}
