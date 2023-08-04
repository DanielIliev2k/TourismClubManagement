package com.example.tourismclubmanagement.comparators;

import com.example.tourismclubmanagement.models.Event;

import java.util.Comparator;

public class EventComparator implements Comparator<Event> {
    public EventComparator() {
    }

    @Override
    public int compare(Event event1, Event event2) {


        String date1 = event1.getEventInfo().getDepartureTime().toString();
        String date2 = event2.getEventInfo().getDepartureTime().toString();

        return date2.compareToIgnoreCase(date1);
    }

}