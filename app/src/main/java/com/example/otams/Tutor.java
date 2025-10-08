package com.example.otams;

public class Tutor extends User{
    private String degree;
    private String course;

    public Tutor(String role, String firstName, String lastName, String email, String password, String phoneNum, String degree, String course) {
        super(role, firstName, lastName, email, password, phoneNum);

        this.degree = degree;
        this.course = course;
    }

    public String getDegree(){
        return degree;
    }
    public void setDegree(String degree){
        this.degree = degree;
    }

    public String getCourse(){
        return course;
    }
    public void setCourse(String course){
        this.course = course;

    }
}