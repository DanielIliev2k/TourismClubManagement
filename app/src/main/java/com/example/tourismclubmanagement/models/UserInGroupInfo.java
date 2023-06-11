package com.example.tourismclubmanagement.models;

import java.io.Serializable;

public class UserInGroupInfo  implements Serializable {
    private String id;
    private Role role;

    public UserInGroupInfo(String id, Role role) {
        this.id = id;
        this.role = role;
    }

    public UserInGroupInfo() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
