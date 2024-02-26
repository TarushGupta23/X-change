package com.example.x_change.utility;

public class ChatListItem {
    private String name, lastMsg, date;

    public ChatListItem(String name, String lastMsg, String date) {
        this.name = name;
        this.lastMsg = lastMsg;
        this.date = date;
    }

    public String getName() { return name; }
    public String getLastMsg() { return lastMsg; }
    public String getDate() { return date; }

    public void setName(String name) { this.name = name; }
    public void setLastMsg(String lastMsg) { this.lastMsg = lastMsg; }
    public void setDate(String date) { this.date = date; }
}
