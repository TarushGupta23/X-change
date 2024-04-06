package com.example.x_change.utility;

public class Chat {
    public String sentBy, msg, date;

    public Chat(String senderId, String msg, String date) {
        this.sentBy = senderId;
        this.msg = msg;
        this.date = date;
    }

    public Chat() {}
}
