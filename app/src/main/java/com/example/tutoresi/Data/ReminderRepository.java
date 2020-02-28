package com.example.tutoresi.Data;

import androidx.lifecycle.MutableLiveData;

import com.example.tutoresi.Model.Reminder;
import java.util.List;

public class ReminderRepository {

    private FirebaseSource firebaseSource;
    private MutableLiveData<List<Reminder>> myReminders;


    public ReminderRepository(){
        firebaseSource = new FirebaseSource();
        myReminders = firebaseSource.getMyReminders();
    }

    public MutableLiveData<List<Reminder>> getMyReminders() {
        return myReminders;
    }

    public void addReminder(Reminder reminder){
        firebaseSource.addReminder(reminder);
    }
}
