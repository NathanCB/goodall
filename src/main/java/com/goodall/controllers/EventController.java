package com.goodall.controllers;

import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.goodall.entities.Event;
import com.goodall.entities.Glitch;
import com.goodall.entities.User;
import com.goodall.parsers.RootParser;
import com.goodall.serializers.EventSerializer;
import com.goodall.serializers.GlitchSerializer;
import com.goodall.serializers.RootSerializer;
import com.goodall.services.EventRepository;
import com.goodall.services.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.amazonaws.services.s3.AmazonS3Client;


import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

@CrossOrigin("*")
@RestController
public class EventController {
    @Value("${cloud.aws.s3.bucket}")
    String bucket;

    @Autowired
    AmazonS3Client s3;

    @Autowired
    EventRepository events;
    @Autowired
    UserRepository users;

    RootSerializer rootSerializer = new RootSerializer();
    EventSerializer eventSerializer = new EventSerializer();
    GlitchSerializer glitchSerializer = new GlitchSerializer();

    String testUrl = "https://upload.wikimedia.org/wikipedia/commons/4/47/PNG_transparency_demonstration_1.png";

    @RequestMapping(path = "/events", method = RequestMethod.GET)//public
    public Map<String, Object> displayEvents(@RequestParam(required = false) String searchBy,
                                             @RequestParam(required = false) String value,
                                             HttpServletResponse response) throws IOException {

        if (searchBy != null && value != null) {
            return searchByZipOrCity(searchBy, value, response);
        }

        Iterable<Event> showEvents = events.findAll();
        return rootSerializer.serializeMany("/events", showEvents, eventSerializer);
    }

    public Map<String, Object> searchByZipOrCity(String searchType, String val, HttpServletResponse response) throws IOException {
        ArrayList<Event> results = null;

        if (searchType != null) {
            if (searchType.equalsIgnoreCase("city")) {
                results = events.findAllByCityContainingIgnoreCase(val);
            } else if (searchType.equalsIgnoreCase("zip")) {
                results = events.findAllByZip(val);
            } else {
                // invalid search by type
                response.sendError(400, "Invalid search by type.");
            }
        } else {
            // missing search by type
            response.sendError(400, "Missing search by type");
        }

        return rootSerializer.serializeMany("/events", results, eventSerializer);
    }

    @RequestMapping(path = "/events/{id}", method = RequestMethod.GET)//public
    public Map<String, Object> viewEvent(@PathVariable String id, HttpServletResponse response) {
        Event event = events.findFirstById(id);
        return rootSerializer.serializeOne(
                "/events/{id}",
                event,
                eventSerializer);
    }

    @RequestMapping(path = "/events/upload", method = RequestMethod.POST)
    public Map<String, Object> createEvent(@RequestParam("file") MultipartFile file,
                                           @RequestParam("address") String address,
                                           @RequestParam("city") String city,
                                           @RequestParam("state") String state,
                                           @RequestParam("zip") String zip,
                                           @RequestParam("title") String title,
                                           @RequestParam("artist") String artist,
                                           @RequestParam("date") String date,
                                           @RequestParam("description") String description,
                                           @RequestParam("start-time") String startTime,
                                           @RequestParam("end-time") String endTime,
                                           HttpServletResponse response) throws IOException {
        Authentication u = SecurityContextHolder.getContext().getAuthentication();
        User user = users.findFirstByUsername(u.getName());

        // create event
        Event event = new Event(address, city, state, zip, title, artist, date,
                description, startTime, endTime, user, "");  // Add all parms, + default image url

        if (file != null) {
            //set the photourl field
            event.setPhotoUrl("https://s3.amazonaws.com/" + bucket + "/" + file.getOriginalFilename());

            // set up s3 request
            PutObjectRequest s3Req = new PutObjectRequest(
                    bucket,
                    file.getOriginalFilename(),
                    file.getInputStream(),
                    new ObjectMetadata());

            // save object to s3
            s3.putObject(s3Req);
        }

        ApiCtl findLoc = new ApiCtl();
        ApiCtl apiResult = new ApiCtl();

        String coordinates = findLoc.makeGeocodeRequest(address);
        event.setCoordinates(coordinates);

        event.setBgUrl(apiResult.getGlitchImageUrl(testUrl));
//        event.setBgUrl(findLoc.getNasaImageUrl(coordinates));

        try {
            events.save(event);
        } catch (Exception e) {
            response.sendError(400, "Unable to save event.");
        }
        return rootSerializer.serializeOne(
                "/events/upload",
                event,
                eventSerializer
        );
    }

    @RequestMapping(path = "/events/{id}", method = RequestMethod.DELETE)//private
    public void deleteEvent(@PathVariable String id, HttpServletResponse response) throws IOException {
        try {
            events.delete(id);
        } catch (Exception e) {
            response.sendError(404, "Event not found");
        }
        response.setStatus(204);
    }

    @RequestMapping(path = "/events/{id}", method = RequestMethod.PATCH)
    public Map<String, Object> updateEvent(@PathVariable String id, @RequestBody RootParser<Event> parser, HttpServletResponse response) throws IOException {
        Authentication u = SecurityContextHolder.getContext().getAuthentication();
        User user = users.findFirstByUsername(u.getName());

        Event updatedEvent = parser.getData().getEntity();
        Event dbevent = events.findFirstById(id);

        if (dbevent.getUser().getId() == user.getId()) {
            dbevent.updateTitle(updatedEvent.getTitle());
            dbevent.updateAddress(updatedEvent.getAddress());
            dbevent.updateCity(updatedEvent.getCity());
            dbevent.updateState(updatedEvent.getState());
            dbevent.updateZip(updatedEvent.getZip());
            dbevent.updateArtist(updatedEvent.getArtist());
            dbevent.updateDate(updatedEvent.getDate());
            dbevent.updateDescription(updatedEvent.getDescription());
            dbevent.updateStartTime(updatedEvent.getStartTime());
            dbevent.updateEndTime(updatedEvent.getEndTime());
            dbevent.updateBgUrl(updatedEvent.getBgUrl());
            dbevent.updateCoordinates(updatedEvent.getCoordinates());
        }

        try {
            events.save(dbevent);
        } catch (Exception e) {
            response.sendError(400, "Unable to update event.");
        }
        return rootSerializer.serializeOne(
                "/events/" + dbevent.getId(),
                dbevent,
                eventSerializer
        );
    }

    @RequestMapping(path = "events/glitch/{id}", method = RequestMethod.GET)
    public Map<String, Object> glitchPhoto(@PathVariable String id, HttpServletResponse response) throws IOException {
        Event event = events.findFirstById(id);
        Glitch glitch = new Glitch();
        if (event.getBgUrl() != null) {
            try {
                ApiCtl apiResult = new ApiCtl();
                glitch.setUrl(apiResult.getGlitchImageUrl(event.getBgUrl()));
            } catch (Exception e) {
                response.sendError(400, "Unable to glitch.");
            }
        } else {
            glitch.setUrl(ApiCtl.defaultImgUrl);
        }
        return rootSerializer.serializeOne(
                "events/glitch/" + event.getId(),
                glitch,
                glitchSerializer
        );
    }
}