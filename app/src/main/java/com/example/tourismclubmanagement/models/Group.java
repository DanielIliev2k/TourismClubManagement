package com.example.tourismclubmanagement.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class Group implements Serializable {
    private List<UserInGroupInfo> usersInGroup;
    private List<Event> events;
    private GroupInfo groupInfo;

    public Group() {
    }

    public Group(List<UserInGroupInfo> usersInGroup, List<Event> events, GroupInfo groupInfo) {
        this.usersInGroup = usersInGroup;
        this.events = events;
        this.groupInfo = groupInfo;
    }

    public GroupInfo getGroupInfo() {
        return groupInfo;
    }

    public void setGroupInfo(GroupInfo groupInfo) {
        this.groupInfo = groupInfo;
    }

    public List<UserInGroupInfo> getUsersInGroup() {
        return usersInGroup;
    }

    public void setUsersInGroup(List<UserInGroupInfo> usersInGroup) {
        this.usersInGroup = usersInGroup;
    }

    public List<Event> getEvents() {
        return events;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }
}
