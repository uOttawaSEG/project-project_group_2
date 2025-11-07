package com.example.otams;

import java.io.Serializable;

public class AvailabilitySlot implements Serializable {
    private long id;
    private long tutorId;
    private String date;
    private String startTime;
    private String endTime;

    public AvailabilitySlot() {}

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public long getTutorId() { return tutorId; }
    public void setTutorId(long tutorId) { this.tutorId = tutorId; }
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }
    public String getEndTime() { return endTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }
}

