package com.example.otams;

import java.io.Serializable;

// Represents a single availability window a Tutor offers for booking sessions.
// Keeping original String date/time fields for minimal impact,
// Date format assumption: YYYY-MM-DD.



public class AvailabilitySlot implements Serializable {
    private long id;          // DB primary key (auto increment when persisted)
    private long tutorId;     // Foreign key referencing a Tutor user
    private String date;      // "YYYY-MM-DD"
    private String startTime; // "HH:MM" 24-hour
    private String endTime;   // "HH:MM" 24-hour

    public AvailabilitySlot() {}

    public AvailabilitySlot(long tutorId, String date, String startTime, String endTime) {
        this.tutorId = tutorId;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
    }

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

    // Returns duration in minutes or -1 if invalid.
    public int getDurationMinutes() {
        int start = parseMinutes(startTime);
        int end = parseMinutes(endTime);
        if (start < 0 || end < 0 || end <= start) return -1;
        return end - start;
    }

    // Deliverable 3 requirement: slots must be in 30-minute increments, end > start.
    public boolean isValidSlot() {
        int duration = getDurationMinutes();
        if (duration <= 0) return false;
        if (duration % 30 != 0) return false;
        // Could add overlap & past-date checks at service layer.
        return true;
    }

    private int parseMinutes(String hhmm) {
        if (hhmm == null || !hhmm.matches("\\d{2}:\\d{2}")) return -1;
        String[] parts = hhmm.split(":");
        int h = Integer.parseInt(parts[0]);
        int m = Integer.parseInt(parts[1]);
        if (h < 0 || h > 23 || m < 0 || m > 59) return -1;
        return h * 60 + m;
    }

    @Override
    public String toString() {
        return "AvailabilitySlot{" +
                "id=" + id +
                ", tutorId=" + tutorId +
                ", date='" + date + '\'' +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", duration=" + getDurationMinutes() +
                '}';
    }
}

