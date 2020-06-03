package com.example.tutoresi.Model;

/**
 * Represents a Course
 */
public class Course {

    private String id, libelle, description;


    public Course() {
        // Default constructor required for calls to DataSnapshot.getValue(Course.class)
    }

    /**
     * Constructor of a course
     * @param id course id
     * @param libelle course libelle
     * @param description description of the course
     */
    public Course(String id, String libelle, String description) {
        this.id = id.toUpperCase();
        this.libelle = libelle;
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public String getLibelle() {
        return libelle;
    }

    public String getDescription() {
        return description;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
