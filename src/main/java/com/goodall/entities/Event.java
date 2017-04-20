package com.goodall.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "events")
public class Event implements HasId {
    static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    String id;

    @Column
    String address;

    @Column
    String city;

    @Column
    String state;

    @Column
    String zip;

    @Column
    String title;

    @Column
    String artist;

    @Column
    String date;

    @Column(columnDefinition = "text")
    String description;

    @Column
    @JsonProperty("start-time")
    String startTime;

    @Column
    @JsonProperty("end-time")
    String endTime;

    @Column
    String coordinates;

    @Column
    @JsonProperty("bg-url")
    String bgUrl;

    @ManyToOne
    User user;

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    @Column
    @JsonProperty("photo-url")
    String photoUrl;

    public Event() {
    }

    public Event(String address, String city, String state, String zip, String title, String artist,
                 String date, String description, String startTime, String endTime, User user, String photoUrl) {
        this.address = address;
        this.city = city;
        this.state = state;
        this.zip = zip;
        this.title = title;
        this.artist = artist;
        this.date = date;
        this.description = description;
        this.startTime = startTime;
        this.endTime = endTime;
        this.user = user;
        this.photoUrl = photoUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void updateTitle(String title){
        if(title != null && !title.isEmpty()){
            this.title = title;
        }
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void updateDescription(String description){
        if(description != null && !description.isEmpty()){
            this.description = description;
        }
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public void updateStartTime(String startTime){
        if(startTime != null && !startTime.isEmpty()){
            this.startTime = startTime;
        }
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public void updateEndTime(String endTime){
        if(endTime != null && !endTime.isEmpty()){
            this.endTime = endTime;
        }
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public void updateArtist(String artist){
        if(artist != null && !artist.isEmpty()){
            this.artist = artist;
        }
    }

    public User getUser() {
        return user;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void updateDate(String date){
        if(date != null && !date.isEmpty()){
            this.date = date;
        }
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void updateUser(User user){
        if(user != null && user.toString().isEmpty()){
            this.user = user;
        }
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void updateAddress(String address){
        if(address != null && !address.isEmpty()){
            this.address = address;
        }
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void updateCity(String city){
        if(city != null && !city.isEmpty()){
            this.city = city;
        }
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void updateState(String state){
        if(state != null && !state.isEmpty()){
            this.state = state;
        }
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public void updateZip(String zip){
        if(zip != null && !zip.isEmpty()){
            this.zip = zip;
        }
    }

    public String getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(String coordinates) {
        this.coordinates = coordinates;
    }

    public void updateCoordinates(String coordinates) {
        if(coordinates != null && !coordinates.isEmpty() ) {
            this.coordinates = coordinates;
        }
    }

    public String getBgUrl() {
        return bgUrl;
    }

    public void setBgUrl(String bgUrl) {
        this.bgUrl = bgUrl;
    }

    public void updateBgUrl(String bgUrl) {
        if(bgUrl != null && !bgUrl.isEmpty() ) {
            this.bgUrl = bgUrl;
        }
    }
}