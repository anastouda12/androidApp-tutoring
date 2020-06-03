package com.example.tutoresi.Data;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.tutoresi.Model.Reminder;

public class ReminderViewModel extends ViewModel {

    private ReminderRepository reminderRepository;

    public ReminderViewModel(){
        reminderRepository = ReminderRepository.getInstance();
    }

    public LiveData<Boolean> addReminder(Reminder reminder){
        return reminderRepository.addReminder(reminder);
    }
}
