package com.example.tourismclubmanagement.models;

import java.io.Serializable;
import java.util.Date;

public class GroupInfo {
    private String id;
    private String groupName;
    private Date dateCreated;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public GroupInfo(String id, String groupName, Date dateCreated) {
        this.id = id;
        this.groupName = groupName;
        this.dateCreated = dateCreated;
    }

    public GroupInfo() {
    }
}
