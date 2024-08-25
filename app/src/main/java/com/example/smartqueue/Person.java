package com.example.smartqueue;

public class Person {

    // params
    private String email;
    private String phone;
    private String firstname;
    private String lastname;
    private String password;

    // constructor full
    public Person(String email, String phone, String firstname, String lastname, String password) {
        this.email = email;
        this.phone = phone;
        this.firstname = firstname;
        this.lastname = lastname;
        this.password = password;
    }

    // constructor with phone only
    public Person(String phone) {
        this.phone = phone;
    }

    // getters
    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public String getPassword() {
        return password;
    }

    // setters
    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // empty constructor
    public Person(){};
}