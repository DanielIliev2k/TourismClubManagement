package com.example.tourismclubmanagement.models;

public class UserInfo {
    private String id;
    private String email;
    private String name;
    private Integer age;
    private String hometown;
    private Boolean firstLogin;

    public UserInfo(String id, String email, String name, Integer age, String hometown, Boolean firstLogin) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.age = age;
        this.hometown = hometown;
        this.firstLogin = firstLogin;
    }

    public UserInfo() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public Boolean getFirstLogin() {
        return firstLogin;
    }

    public void setFirstLogin(Boolean firstLogin) {
        this.firstLogin = firstLogin;
    }
}
