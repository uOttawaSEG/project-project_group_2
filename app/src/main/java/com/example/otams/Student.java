package com.example.otams;

/**
 * Represents a student user in the system.
 * Inherits from the User class and adds a program of study.
 */
public class Student extends User {
    private String program;

    /**
     * Constructs a new Student object.
     *
     * @param role      The role of the user (should be "Student").
     * @param firstName The first name of the student.
     * @param lastName  The last name of the student.
     * @param email     The email address of the student.
     * @param password  The password for the student's account.
     * @param phoneNum  The phone number of the student.
     * @param program   The program of study for the student.
     */
    public Student(String role, String firstName, String lastName, String email, String password, String phoneNum, String program) {
        super(role, firstName, lastName, email, password, phoneNum);
        this.program = program;
    }

    /**
     * Gets the program of study for the student.
     *
     * @return The student's program of study.
     */
    public String getProgram() {
        return program;
    }

    /**
     * Sets the program of study for the student.
     *
     * @param program The new program of study.
     */
    public void setProgram(String program) {
        this.program = program;
    }
}
