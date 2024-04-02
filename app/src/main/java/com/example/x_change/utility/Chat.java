package com.example.x_change.utility;

import java.util.Date;

public class Chat {
    public String sentBy, msg;
    public Date date;

    public Chat(String senderId, String msg, Date date) {
        this.sentBy = senderId;
        this.msg = msg;
        this.date = date;
    }
}
