package com.example.tourismclubmanagement.models;

public class UserGroup {
    private String groupId;
    private Boolean favourite;

    public UserGroup(String groupId, Boolean favourite) {
        this.groupId = groupId;
        this.favourite = favourite;
    }

    public UserGroup() {
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public Boolean getFavourite() {
        return favourite;
    }

    public void setFavourite(Boolean favourite) {
        this.favourite = favourite;
    }
}
