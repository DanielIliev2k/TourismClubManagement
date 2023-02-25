package com.example.tourismclubmanagement.models;

import java.util.Date;
import java.util.List;

public class Group {
    private String id;
    private List<String> userIds;
    private List<Event> events;
    private String groupName;
    private Date dateCreated;

    public Group() {
    }

    public Group(String id) {
        this.id = id;
        this.dateCreated = new Date();
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public String getId() {
        return id;
    }

    public List<String> getUserIds() {
        return userIds;
    }

    public void setUserIds(List<String> userIds) {
        this.userIds = userIds;
    }

    public List<Event> getEvents() {
        return events;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
}
