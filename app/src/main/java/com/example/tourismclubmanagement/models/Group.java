package com.example.tourismclubmanagement.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class Group implements Serializable {
    private String id;
    private ArrayList<String> userIds;
    private HashMap<String,Event> events;
    private String groupName;
    private Date dateCreated;

    public Group() {
    }

    public Group(String id,Date dateCreated) {
        this.id = id;
        this.dateCreated = dateCreated;
    }


    public Date getDateCreated() {
        return dateCreated;
    }

    public String getId() {
        return id;
    }

    public ArrayList<String> getUserIds() {
        return userIds;
    }

    public void setUserIds(ArrayList<String> userIds) {
        this.userIds = userIds;
    }

    public HashMap<String,Event> getEvents() {
        return events;
    }

    public void setEvents(HashMap<String,Event> events) {
        this.events = events;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
}
