package com.example.tourismclubmanagement.models;

import java.io.Serializable;
import java.util.List;

public class User implements Serializable {
    private List<UserGroup> groups;
    private UserInfo userInfo;

    public User() {
    }

    public User(List<UserGroup> groups, UserInfo userInfo) {
        this.groups = groups;
        this.userInfo = userInfo;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    public List<UserGroup> getGroups() {
        return groups;
    }
    public void setGroups(List<UserGroup> groups) {
        this.groups = groups;
    }

}
