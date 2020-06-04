package com.example.tutoresi.data;

import androidx.lifecycle.MutableLiveData;

import com.example.tutoresi.model.Reminder;

import java.util.List;

/**
 * Reminder repository
 */
public class ReminderRepository {

    private FirebaseSource firebaseSource;
    private static ReminderRepository instance = null;

    /**
     * Constructor of reminder repository
     */
    private ReminderRepository(){
        firebaseSource = FirebaseSource.getInstance();
    }

    /**
     * Get instance of reminder repository
     * @return instance of reminder repository
     */
    public static ReminderRepository getInstance() {
        if(instance == null){
            instance = new ReminderRepository();
        }
        return instance;
    }

    /**
     * Add reminder to the current user in DB.
     * @param reminder reminder to ADD
     * @return true if the action was successfully
     */
    public MutableLiveData<Boolean> addReminder(Reminder reminder){
        return firebaseSource.addReminder(reminder);
    }

    /**
     * Get all reminders of the current user
     * @return list of all reminders of current user
     */
    public MutableLiveData<List<Reminder>> getRemindersOfCurrentUser(){
        return firebaseSource.getReminderCurrentUser();
    }
}
