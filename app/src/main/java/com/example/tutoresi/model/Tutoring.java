package com.example.tutoresi.model;

/**
 * Represents a tutoring
 */
public class Tutoring {

    private User author;
    private String descriptionTutoring;


    public Tutoring(){
        // Necessary for firebase
    }

    /**
     * Constructor of a tutoring
     * @param author author
     * @param descriptionTutoring description
     */
    public Tutoring(User author, String descriptionTutoring){
        this.author = author;
        this.descriptionTutoring = descriptionTutoring;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public String getDescriptionTutoring() {
        return descriptionTutoring;
    }

    public void setDescriptionTutoring(String descriptionTutoring) {
        this.descriptionTutoring = descriptionTutoring;
    }
}
