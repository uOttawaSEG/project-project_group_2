package com.example.otams;

public class RegistrationRequest {
    private int userId;
    private String role;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNum;
    private String program;
    private String degree;
    private String course;
    private String status;
    private double registrationDate;

    //constructor
    public RegistrationRequest() {}

    //setter/getter methods for each  field
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhoneNum() { return phoneNum; }
    public void setPhoneNum(String phoneNum) { this.phoneNum = phoneNum; }
    public String getProgram() { return program; }
    public void setProgram(String program) { this.program = program; }
    public String getDegree() { return degree; }
    public void setDegree(String degree) { this.degree = degree; }
    public String getCourse() { return course; }
    public void setCourse(String course) { this.course = course; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public double getRegistrationDate() { return registrationDate; }
    public void setRegistrationDate(double registrationDate) { this.registrationDate = registrationDate; }
}