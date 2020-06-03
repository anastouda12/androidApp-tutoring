package com.example.tutoresi.Model;

/**
 * Class reminder of a tutoring
 */
public class Reminder {

    private String course;
    private String date;
    private String location;

    /**
     * Constructor of a reminder
     * @param course
     * @param date
     * @param location
     */
    public Reminder(String course, String date, String location){
        this.course = course;
        this.date  = date;
        this.location = location;
    }

    public Reminder(){
        // Default constructor required for calls to DataSnapshot.getValue(Reminder.class)
    }


    public String getDate() {
        return date;
    }

    public String getCourse() {
        return course;
    }


    public String getLocation() {
        return location;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public void setLocation(String location) {
        this.location = location;
    }

}
