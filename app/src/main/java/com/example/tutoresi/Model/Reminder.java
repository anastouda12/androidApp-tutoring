package com.example.tutoresi.Model;

import java.util.Date;

/**
 * Class reminder of a tutoring
 */
public class Reminder {

    private String course;
    private Date date;
    private String location;


    public Reminder(String course, Date date, String location){
        this.course = course;
        this.date  = date;
        this.location = location;
    }

    public Reminder(){

    }


    public Date getDate() {
        return date;
    }

    public String getCourse() {
        return course;
    }


    public String getLocation() {
        return location;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public void setLocation(String location) {
        this.location = location;
    }

}
