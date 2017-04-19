package com.goodall.services;

import com.goodall.entities.Event;
import org.springframework.data.repository.CrudRepository;

import java.util.ArrayList;

public interface EventRepository extends CrudRepository<Event, String>{
    Event findFirstById(String id);
    ArrayList<Event> findAllByZip(String zip);
    ArrayList<Event> findAllByCity(String city);

}
