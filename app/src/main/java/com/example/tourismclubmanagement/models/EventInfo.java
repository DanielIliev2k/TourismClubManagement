package com.example.tourismclubmanagement.models;

import java.util.Date;

public class EventInfo {

    private String id;
    private String eventName;
    private String location;
    private Date departureTime;
    private String duration;
    private String equipment;
    private Boolean participation;
    private String notes;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public Boolean getParticipation() {
        return participation;
    }

    public void setParticipation(Boolean participation) {
        this.participation = participation;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public EventInfo(String id, String eventName, String location, Date departureTime, String duration, String equipment, Boolean participation, String notes) {
        this.id = id;
        this.eventName = eventName;
        this.location = location;
        this.departureTime = departureTime;
        this.duration = duration;
        this.equipment = equipment;
        this.participation = participation;
        this.notes = notes;
    }

    public EventInfo() {
    }
}
