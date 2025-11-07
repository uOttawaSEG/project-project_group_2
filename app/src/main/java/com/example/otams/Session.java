package com.example.otams;

import java.io.Serializable;

public class Session implements Serializable {
    private long id;
    private long slotId;
    private long tutorId;
    private long studentId;
    private String status;
    private long createdAt;
    private long scheduledAt;

    public Session() {}

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public long getSlotId() { return slotId; }
    public void setSlotId(long slotId) { this.slotId = slotId; }
    public long getTutorId() { return tutorId; }
    public void setTutorId(long tutorId) { this.tutorId = tutorId; }
    public long getStudentId() { return studentId; }
    public void setStudentId(long studentId) { this.studentId = studentId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
    public long getScheduledAt() { return scheduledAt; }
    public void setScheduledAt(long scheduledAt) { this.scheduledAt = scheduledAt; }
}
