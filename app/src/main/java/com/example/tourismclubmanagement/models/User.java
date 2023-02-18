package com.example.tourismclubmanagement.models;

import java.util.Date;
import java.util.List;

public class User {
    private String username;
    private String password;
    private Date dateCreated;
    private String name;
    private Date birthDate;
    private List<Event> eventParticipation;
    private List<Event> eventApplications;
    private String hometown;
    private Boolean isAdmin;

    public User() {
    }

    public User(String username, String password, Date dateCreated, Boolean isAdmin) {
        this.username = username;
        this.password = password;
        this.dateCreated = dateCreated;
        this.isAdmin = isAdmin;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public List<Event> getEventParticipation() {
        return eventParticipation;
    }

    public void setEventParticipation(List<Event> eventParticipation) {
        this.eventParticipation = eventParticipation;
    }

    public List<Event> getEventApplications() {
        return eventApplications;
    }

    public void setEventApplications(List<Event> eventApplications) {
        this.eventApplications = eventApplications;
    }

    public String getHometown() {
        return hometown;
    }

    public void setHometown(String hometown) {
        this.hometown = hometown;
    }

    public Boolean getAdmin() {
        return isAdmin;
    }

    public void setAdmin(Boolean admin) {
        isAdmin = admin;
    }
}
