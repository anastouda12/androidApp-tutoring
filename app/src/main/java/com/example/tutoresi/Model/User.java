package com.example.tutoresi.Model;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
/**
 * Class of an user of the app
 */
public class User {

    private String name, email, phone;

    public User(){
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }
    public User(String name, String email, String phone){
        this.name = name;
        this.email = email;
        this.phone = phone;
    }

    public String getPhone() {
        return phone;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }
}