package com.example.tutoresi.Data;

import androidx.lifecycle.MutableLiveData;

import com.example.tutoresi.Model.Reminder;
import java.util.List;

public class ReminderRepository {

    private FirebaseSource firebaseSource;


    public ReminderRepository(){
        firebaseSource = new FirebaseSource();
    }

    public MutableLiveData<Boolean> addReminder(Reminder reminder){
        return firebaseSource.addReminder(reminder);
    }
}
