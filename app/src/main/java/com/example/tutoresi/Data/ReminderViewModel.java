package com.example.tutoresi.Data;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.tutoresi.Model.Reminder;

import java.util.List;

/**
 * ReminderViewModel
 */
public class ReminderViewModel extends ViewModel {

    private ReminderRepository reminderRepository;
    private LiveData<List<Reminder>> myReminders;

    /**
     * Constructor of reminderViewModel
     */
    public ReminderViewModel(){
        reminderRepository = ReminderRepository.getInstance();
        myReminders = reminderRepository.getRemindersOfCurrentUser();
    }

    /**
     * Add reminder of the current user in DB.
     * @param reminder reminder to add.
     * @return true if action was a success
     */
    public LiveData<Boolean> addReminder(Reminder reminder){
        return reminderRepository.addReminder(reminder);
    }

    /**
     * Get all reminders of current user
     * @return list of all reminders
     */
    public LiveData<List<Reminder>> getMyReminders(){
        return myReminders;
    }
}
