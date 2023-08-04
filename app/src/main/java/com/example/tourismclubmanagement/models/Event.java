package com.example.tourismclubmanagement.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Event implements Serializable {
    private List<EventParticipant> participants;
    private EventInfo eventInfo;

    public Event(List<EventParticipant> participants, EventInfo eventInfo) {
        this.participants = participants;
        this.eventInfo = eventInfo;
    }

    public List<EventParticipant> getParticipants() {
        return participants;
    }

    public void setParticipants(List<EventParticipant> participants) {
        this.participants = participants;
    }

    public EventInfo getEventInfo() {
        return eventInfo;
    }

    public void setEventInfo(EventInfo eventInfo) {
        this.eventInfo = eventInfo;
    }

    public Event() {
    }
}
