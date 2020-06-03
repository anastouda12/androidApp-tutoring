package com.example.tutoresi.Data;

import androidx.lifecycle.MutableLiveData;

import com.example.tutoresi.Model.Reminder;

public class ReminderRepository {

    private FirebaseSource firebaseSource;
    private static ReminderRepository instance;

    private ReminderRepository(){
        firebaseSource = FirebaseSource.getInstance();
    }

    public static ReminderRepository getInstance() {
        if(instance == null){
            instance = new ReminderRepository();
        }
        return instance;
    }

    public MutableLiveData<Boolean> addReminder(Reminder reminder){
        return firebaseSource.addReminder(reminder);
    }
}
