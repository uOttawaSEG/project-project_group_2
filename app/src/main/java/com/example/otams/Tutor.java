package com.example.otams;

/**
 * Represents a tutor user in the system.
 * Inherits from the User class and adds information about the tutor's degree and courses.
 */
public class Tutor extends User {
    private String degree;
    private String course;

    /**
     * Constructs a new Tutor object.
     *
     * @param role      The role of the user (should be "Tutor").
     * @param firstName The first name of the tutor.
     * @param lastName  The last name of the tutor.
     * @param email     The email address of the tutor.
     * @param password  The password for the tutor's account.
     * @param phoneNum  The phone number of the tutor.
     * @param degree    The highest degree obtained by the tutor.
     * @param course    The course(s) the tutor can teach.
     */
    public Tutor(String role, String firstName, String lastName, String email, String password, String phoneNum, String degree, String course) {
        super(role, firstName, lastName, email, password, phoneNum);

        this.degree = degree;
        this.course = course;
    }

    /**
     * Gets the degree of the tutor.
     *
     * @return The tutor's degree.
     */
    public String getDegree() {
        return degree;
    }

    /**
     * Sets the degree of the tutor.
     *
     * @param degree The new degree.
     */
    public void setDegree(String degree) {
        this.degree = degree;
    }

    /**
     * Gets the course(s) the tutor teaches.
     *
     * @return The tutor's course(s).
     */
    public String getCourse() {
        return course;
    }

    /**
     * Sets the course(s) the tutor teaches.
     *
     * @param course The new course(s).
     */
    public void setCourse(String course) {
        this.course = course;
    }
}
