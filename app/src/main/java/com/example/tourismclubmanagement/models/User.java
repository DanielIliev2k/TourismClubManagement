package com.example.tourismclubmanagement.models;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class User implements Serializable {
    private String id;
    private String username;
    private String password;
    private String groupId;
    private Date dateCreated;
    private String name;
    private Date birthDate;
    private List<String> confirmedEvents;
    private List<String> eventApplications;
    private String hometown;
    private Boolean isAdmin;

    public User() {
    }
    public User(String groupId,String id) {
        this.groupId = groupId;
        this.id = id;
        this.dateCreated = new Date();
    }

    public User(String username, String password, Boolean isAdmin,String groupId,String id) {
        this.username = username;
        this.password = password;
        this.dateCreated = new Date();
        this.isAdmin = isAdmin;
        this.groupId = groupId;
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getGroupId() {
        return groupId;
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

    public List<String> getConfirmedEvents() {
        return confirmedEvents;
    }

    public void setConfirmedEvents(List<String> confirmedEvents) {
        this.confirmedEvents = confirmedEvents;
    }

    public List<String> getEventApplications() {
        return eventApplications;
    }

    public void setEventApplications(List<String> eventApplications) {
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
