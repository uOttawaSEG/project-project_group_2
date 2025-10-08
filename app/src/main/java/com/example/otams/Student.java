package com.example.otams;

public class Student extends User{
    private String program;

    public Student(String role, String firstName, String lastName, String email, String password, String phoneNum, String program) {
        super(role, firstName, lastName, email, password, phoneNum);

        this.program = program;
    }

    public String getProgram(){
        return program;
    }
    public void setProgram(String program){
        this.program = program;
    }
}
