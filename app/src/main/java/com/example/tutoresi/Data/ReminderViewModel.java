package com.example.tutoresi.Data;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.tutoresi.Model.Reminder;

import java.util.List;

public class ReminderViewModel extends ViewModel {

    private ReminderRepository reminderRepository;

    public ReminderViewModel(){
        reminderRepository = new ReminderRepository();
    }

    public void addReminder(Reminder reminder){
        reminderRepository.addReminder(reminder);
    }
}
