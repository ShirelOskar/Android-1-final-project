package com.example.smartqueue;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Appointment {
    private String date;
    private String time;
    private String hairDesign;
    private String id;
    private String userId;
    private String firstName; // New field for first name
    private String lastName;  // New field for last name
    private String fullName;

    private String phone;
    // Default constructor required for calls to DataSnapshot.getValue(Appointment.class)
    public Appointment() {
    }

    // Constructor to initialize all fields
    public Appointment(String id, String date, String time, String hairDesign, String userId, String fullName, String firstName, String lastName , String phone) {
        this.date = date;
        this.time = time;
        this.hairDesign = hairDesign;
        this.id = id;
        this.userId = userId;
        this.fullName = fullName;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
    }

    // Getters and Setters for all fields
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getHairDesign() {
        return hairDesign;
    }

    public void setHairDesign(String hairDesign) {
        this.hairDesign = hairDesign;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    public String getFullName() {
        return fullName;
    }
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Appointment that = (Appointment) o;

        return id != null ? id.equals(that.id) : that.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

//    public Date getDateAsDate() {
//        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.US);
//        try {
//            return dateFormat.parse(this.date); // Assuming the date field is a string in the format "dd.MM.yyyy"
//        } catch (ParseException e) {
//            e.printStackTrace();
//            return null;
//        }
//    }

}
