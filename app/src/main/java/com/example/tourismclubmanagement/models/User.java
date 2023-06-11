package com.example.tourismclubmanagement.models;

import java.io.Serializable;
import java.util.List;

public class User implements Serializable {
    private String id;
    private List<String> groups;
    private String email;
    private String name;
    private Integer age;
    private String hometown;
    private Boolean firstLogin;

    public User() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setGroups(List<String> groups) {
        this.groups = groups;
    }
    public Boolean getFirstLogin() {
        return firstLogin;
    }

    public void setFirstLogin(Boolean firstLogin) {
        this.firstLogin = firstLogin;
    }

    public String getId() {
        return id;
    }

    public List<String> getGroups() {
        return groups;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getHometown() {
        return hometown;
    }

    public void setHometown(String hometown) {
        this.hometown = hometown;
    }

}
