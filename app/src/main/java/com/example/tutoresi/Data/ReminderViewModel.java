package com.example.tutoresi.Data;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.tutoresi.Model.Reminder;

import java.util.List;

public class ReminderViewModel extends ViewModel {

    private ReminderRepository reminderRepository;
    private MutableLiveData<List<Reminder>> reminders;

    public ReminderViewModel(){
        reminderRepository = new ReminderRepository();
        reminders = reminderRepository.getMyReminders();
    }

    public MutableLiveData<List<Reminder>> getReminders() {
        return reminders;
    }

    public void addReminder(Reminder reminder){
        reminderRepository.addReminder(reminder);
    }
}
