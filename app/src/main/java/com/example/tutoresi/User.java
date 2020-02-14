package com.example.tutoresi;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class User {

    public String name, email, phone;

    public User(){
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }
    public User(String name, String email, String phone){
        this.name = name;
        this.email = email;
        this.phone = phone;
    }
}
