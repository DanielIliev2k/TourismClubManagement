package com.example.tourismclubmanagement.models;

import java.io.Serializable;
import java.util.List;

public class Group implements Serializable, Comparable<Group> {
    private List<UserInGroupInfo> usersInGroup;
    private List<Event> events;
    private List<ChatMessage> chat;
    private GroupInfo groupInfo;

    public Group() {
    }

    public Group(List<UserInGroupInfo> usersInGroup, List<Event> events, GroupInfo groupInfo) {
        this.usersInGroup = usersInGroup;
        this.events = events;
        this.groupInfo = groupInfo;
    }

    public List<ChatMessage> getChat() {
        return chat;
    }

    public void setChat(List<ChatMessage> chat) {
        this.chat = chat;
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

    @Override
    public int compareTo(Group group) {
       return this.groupInfo.getGroupName().compareTo(group.groupInfo.getGroupName());
    }
}
