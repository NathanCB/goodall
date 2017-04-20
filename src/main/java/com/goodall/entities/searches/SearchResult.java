package com.goodall.entities.searches;

import com.goodall.entities.Event;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SearchResult {
    ArrayList<Event> filteredEvents;

    public ArrayList<Event> getFilteredEvents() {
        return filteredEvents;
    }

    public void setFilteredEvents(ArrayList<Event> filteredEvents) {
        this.filteredEvents = filteredEvents;
    }
}
