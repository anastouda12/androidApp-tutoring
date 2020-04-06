package com.example.tutoresi.Model;

public class Tutoring {

    private User author;
    private String descriptionTutoring;


    public Tutoring(){
        // Necessary for firebase
    }

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
