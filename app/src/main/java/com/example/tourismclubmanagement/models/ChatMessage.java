package com.example.tourismclubmanagement.models;

import java.sql.Time;
import java.util.Date;

public class ChatMessage {
    private String sender;
    private Date date;
    private String message;

    public ChatMessage(String sender, Date date, String message) {
        this.sender = sender;
        this.date = date;
        this.message = message;
    }

    public ChatMessage() {
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
