package com.rad5.chatapp.FCM;

public class FirebaseMessage {
    public String to;
    public Data data;

    public FirebaseMessage(String to, Data data) {
        this.to = to;
        this.data = data;
    }

    public  FirebaseMessage(){}

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "FirebaseMessage{" +
                "to='" + to + '\'' +
                ", data=" + data +
                '}';
    }
}
