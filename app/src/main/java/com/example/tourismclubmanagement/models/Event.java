package com.example.tourismclubmanagement.models;

import java.sql.Time;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Event {
    private String eventName;
    private String location;
    private Date departureTime;
    private String duration;
    private String equipment;
    private List<User> interested;
    private List<User> confirmed;
    private String notes;

    public Event(String eventName, String location, Date departureTime, String duration, String equipment, String notes) {
        this.eventName = eventName;
        this.location = location;
        this.departureTime = departureTime;
        this.duration = duration;
        this.equipment = equipment;
        this.interested = new ArrayList();
        this.confirmed = new ArrayList();
        this.notes = notes;
    }

    public Event() {
    }
    public Event(String name) {
        this.eventName = name;
    }
    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Date getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(Date departureTime) {
        this.departureTime = departureTime;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getEquipment() {
        return equipment;
    }

    public void setEquipment(String equipment) {
        this.equipment = equipment;
    }

    public List<User> getInterested() {
        return interested;
    }

    public void setInterested(List<User> interested) {
        this.interested = interested;
    }

    public List<User> getConfirmed() {
        return confirmed;
    }

    public void setConfirmed(List<User> confirmed) {
        this.confirmed = confirmed;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

}
