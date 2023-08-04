package com.example.tourismclubmanagement.models;

import java.io.Serializable;
import java.util.Date;

public class UserInGroupInfo  implements Serializable {
    private String id;
    private Role role;
    private Date lastLogin;

    public UserInGroupInfo(String id, Role role) {
        this.id = id;
        this.role = role;
    }

    public UserInGroupInfo() {
    }

    public Date getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(Date lastLogin) {
        this.lastLogin = lastLogin;
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
