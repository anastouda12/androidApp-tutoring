package com.example.tutoresi;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
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

        reminderViewModel = new ReminderViewModel();
        initDatePicker();
        initTimePicker();

        mBtnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String course = mCourse.getText().toString().trim();
                String date = mDate.getText().toString().trim();
                String time = mTime.getText().toString().trim();
                String location = mLocation.getText().toString().trim();

                if(TextUtils.isEmpty(course)){
                    mCourse.setError("Cours requis");
                    return;
                }
                if(TextUtils.isEmpty(date)){
                    mDate.setError("Date requise.");
                    return;
                }
                if(TextUtils.isEmpty(location)){
                    mLocation.setError("Location requise.");
                    return;
                }
                if(TextUtils.isEmpty(time)){
                    mTime.setError("Heure requise.");
                    return;
                }
                reminderViewModel.addReminder(new Reminder(course,getDateTimeString(),location));
                Toast.makeText(DetailReminderActivity.this,R.string.reminder_added,Toast.LENGTH_LONG).show();
                finish();

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
                myCalendar.set(Calendar.MINUTE,minute);
            }
        };

        mTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int hour = myCalendar.get(Calendar.HOUR_OF_DAY);
                int minute = myCalendar.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(DetailReminderActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        mTime.setText(hourOfDay+":"+minute);
                    }
                },hour,minute,true);
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
                updateLabelDate();
            }

        };

        mDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new DatePickerDialog(DetailReminderActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

    }

    /**
     * Update label date
     */
    private void updateLabelDate() {
        String myFormat = "dd/MM/yy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.FRANCE);
        mDate.setText(sdf.format(myCalendar.getTime()));
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
