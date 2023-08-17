package com.example.tourismclubmanagement.comparators;

import com.example.tourismclubmanagement.models.Event;

import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Locale;

public class EventComparator implements Comparator<Event> {
    public EventComparator() {
    }

    @Override
    public int compare(Event event1, Event event2) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd", Locale.ENGLISH);
        String date1 = sdf.format(event1.getEventInfo().getDepartureTime());
        String date2 = sdf.format(event2.getEventInfo().getDepartureTime());

        return date1.compareToIgnoreCase(date2);
    }

}