package com.example.tutoresi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.tutoresi.Data.ReminderViewModel;
import com.example.tutoresi.Model.Reminder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class DetailReminderActivity extends AppCompatActivity {

    private EditText mCourse, mDate, mLocation, mTime;
    private Button mBtnAdd;
    private final Calendar myCalendar = Calendar.getInstance();
    private DatePickerDialog.OnDateSetListener date;
    private TimePickerDialog.OnTimeSetListener time;
    private ReminderViewModel reminderViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_reminder);

        mCourse = (EditText) findViewById(R.id.input_reminder_course);
        mDate = (EditText) findViewById(R.id.input_reminder_date);
        mLocation = (EditText) findViewById(R.id.input_reminder_location);
        mBtnAdd = (Button) findViewById(R.id.btn_reminder_register);
        mTime = (EditText) findViewById(R.id.input_reminder_time);

        reminderViewModel = new ViewModelProvider(this).get(ReminderViewModel.class);
        initDatePicker();
        initTimePicker();
        createNotificationChannel();

        mBtnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String course = mCourse.getText().toString().trim();
                String date = mDate.getText().toString().trim();
                String time = mTime.getText().toString().trim();
                String location = mLocation.getText().toString().trim();

                if(TextUtils.isEmpty(course)){
                    mCourse.setError(getApplicationContext().getString(R.string.reminderCourseRequired));
                    return;
                }
                if(TextUtils.isEmpty(date)){
                    mDate.setError(getApplicationContext().getString(R.string.reminderDateRequired));
                    return;
                }
                if(TextUtils.isEmpty(location)){
                    mLocation.setError(getApplicationContext().getString(R.string.reminderLocationRequired));
                    return;
                }
                if(TextUtils.isEmpty(time)){
                    mTime.setError(getApplicationContext().getString(R.string.remonderTimeRequired));
                    return;
                }
                System.out.println(myCalendar.getTime());
                reminderViewModel.addReminder(new Reminder(course,getDateTimeString(),location));
                registerReminder(course,location);
            }
        });

    }

    /**
     * Register the reminder in DB
     * @param course course title
     * @param location location title
     */
    private void registerReminder(String course, String location){
        reminderViewModel.addReminder(new Reminder(course,getDateTimeString(),location)).observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if(aBoolean){
                    Toast.makeText(DetailReminderActivity.this,R.string.reminder_added,Toast.LENGTH_LONG).show();
                    setAlarm();
                    finish();
                }
            }
        });

    }


    /**
     * Initialize listener and timepicker
     */
    private void initTimePicker(){
        time = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    myCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    myCalendar.set(Calendar.MINUTE, minute);
                    myCalendar.set(Calendar.SECOND,0);
                    mTime.setText(hourOfDay+":"+minute); // update label time
            }
        };

        mTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int hour = myCalendar.get(Calendar.HOUR_OF_DAY);
                int minute = myCalendar.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(DetailReminderActivity.this,time,hour,minute,true);
                timePickerDialog.show();
            }
        });
    }

    /**
     * Initialize listener and datepicker
     */
    private void initDatePicker(){
        date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                // Update label date
                String myFormat = "dd/MM/yy";
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.FRANCE);
                mDate.setText(sdf.format(myCalendar.getTime()));
            }

        };

        mDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                DatePickerDialog dialog = new DatePickerDialog(DetailReminderActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH));
                dialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                dialog.show();
            }
        });

    }

    private void createNotificationChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence name = "TutorESI Rappel canal";
            String description = getApplicationContext().getString(R.string.channelNotificationReminder);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("notifyTutorEsi",name,importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void setAlarm(){
        Intent intent = new Intent(DetailReminderActivity.this,ReminderBroadcast.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(DetailReminderActivity.this,0, intent,0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP,myCalendar.getTimeInMillis(),pendingIntent);
    }


    /**
     * Get time format dd/MM/yy HH:mm in string
     * @return time
     */
    private String getDateTimeString(){
        String myFormat = "dd/MM/yy HH:mm";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.FRANCE);
        return sdf.format(myCalendar.getTime());
    }


}
