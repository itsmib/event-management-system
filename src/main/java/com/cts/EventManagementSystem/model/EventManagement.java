package com.cts.EventManagementSystem.model;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

@Entity
@Table(name = "EVENTS") // Use uppercase for Oracle table names
public class EventManagement {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "event_seq")
    @SequenceGenerator(name = "event_seq", sequenceName = "EVENT_SEQ", allocationSize = 1)
    @Column(name = "EVENTID")
    private Long eventId;

    @Column(name = "NAME", nullable = false)
    private String name;

    @Column(name = "CATEGORY", nullable = false)
    private String category;

    @Column(name = "LOCATION", nullable = false)
    private String location;

    @Column(name = "EVENTDATE", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date date;

    @Column(name = "ORGANIZERID", nullable = false)
    private Long organizerId;

    // Constructors, getters, and setters
    public EventManagement() {
    }

    public EventManagement(String name, String category, String location, Date date, Long organizerId) {
        this.name = name;
        this.category = category;
        this.location = location;
        this.date = date;
        this.organizerId = organizerId;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Long getOrganizerId() {
        return organizerId;
    }

    public void setOrganizerId(Long organizerId) {
        this.organizerId = organizerId;
    }

    @Override
    public String toString() {
        return "EventManagement{" +
                "eventId=" + eventId +
                ", name='" + name + '\'' +
                ", category='" + category + '\'' +
                ", location='" + location + '\'' +
                ", date=" + date +
                ", organizerId=" + organizerId +
                '}';
    }
}