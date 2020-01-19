package com.rad5.chatapp.Models;

public class Chats {
    public String sender;
    public String receiver;
    public String message;
    private Boolean isSeen;

    public Chats(){}

    public Chats(String sender, String receiver, String message,Boolean isSeen) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.isSeen =  isSeen;
    }

    public Boolean isisSeen() {
        return isSeen;
    }

    public void setSeen(Boolean seen) {
        isSeen = seen;
    }

    public String getSender() {
        return sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public String getMessage() {
        return message;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
