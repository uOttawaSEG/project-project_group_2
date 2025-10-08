package com.example.otams;

import java.io.Serializable;
public class User implements Serializable{

    private String role;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String phoneNum;

    public User(String role, String firstName, String lastName, String email, String password, String phoneNum) {
        this.role = role;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.phoneNum = phoneNum;
    }

    public String getRole(){
        return role;
    }
    public void setRole(String role){
        this.role = role;
    }
    public String getFirstName(){
        return firstName;
    }
    public void setFirstName(String firstName){
        this.firstName = firstName;
    }

    public String getLastName(){
        return lastName;
    }
    public void setLastName(String lastName){
        this.lastName = lastName;
    }

    public String getEmail(){
        return email;
    }
    public void setEmail(){
        this.email = email;
    }

    public String getPassword(){
        return password;
    }
    public void setPassword(String password){
        this.password = password;
    }

    public String getPhoneNum(){
        return phoneNum;
    }
    public void setPhoneNum(String phoneNum){
        this.phoneNum = phoneNum;
    }
}
