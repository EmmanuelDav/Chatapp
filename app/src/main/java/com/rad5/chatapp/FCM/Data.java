package com.rad5.chatapp.FCM;

public class Data {
    public String message;
    public String sendersName;

    public Data(String message, String useriid) {
        this.message = message;
        sendersName = useriid;
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
        return sendersName;
    }

    public void setuserID(String useriid) {
        sendersName = useriid;
    }

    @Override
    public String toString() {
        return "data{" +
                "message='" + message + '\'' +
                ", userid='" + sendersName + '\'' +
                '}';
    }
}
