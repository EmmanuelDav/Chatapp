package com.rad5.chatapp.FCM;

import androidx.annotation.NonNull;

public class FirebaseMessage {
    public String to;
    public Data mData;

    public FirebaseMessage(String to, Data data) {
        this.to = to;
        mData = data;
    }

    public  FirebaseMessage(){}

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public Data getData() {
        return mData;
    }

    public void setData(Data data) {
        mData = data;
    }

    @Override
    public String toString() {
        return "FirebaseMessage{" +
                "to='" + to + '\'' +
                ", mData=" + mData +
                '}';
    }
}
