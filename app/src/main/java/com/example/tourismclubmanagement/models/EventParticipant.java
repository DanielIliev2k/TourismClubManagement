package com.example.tourismclubmanagement.models;

public class EventParticipant {
    String userId;
    String userName;
    Status status;

    public EventParticipant(String userId, String userName, Status status) {
        this.userId = userId;
        this.status = status;
        this.userName = userName;
    }

    public EventParticipant() {
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
