package com.example.tutoresi;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class NewReminderActivity extends AbstractActivity {

    private EditText mCourse, mDate, mLocation, mTime;
    private Button mBtnAdd;
    private final Calendar myCalendar = Calendar.getInstance();
    private DatePickerDialog.OnDateSetListener date;
    private TimePickerDialog.OnTimeSetListener time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_reminder);

        mCourse = (EditText) findViewById(R.id.input_reminder_course);
        mDate = (EditText) findViewById(R.id.input_reminder_date);
        mLocation = (EditText) findViewById(R.id.input_reminder_location);
        mBtnAdd = (Button) findViewById(R.id.btn_reminder_register);
        mTime = (EditText) findViewById(R.id.input_reminder_time);

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
                Intent replyIntent = new Intent();
                replyIntent.putExtra("EXTRA_DATETIME",getDateTimeString());
                replyIntent.putExtra("EXTRA_LOC",location);
                replyIntent.putExtra("EXTRA_COURSE",course);
                replyIntent.putExtra("EXTRA_TIME_MILLI",myCalendar.getTimeInMillis());
                setResult(RESULT_OK,replyIntent);
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

                TimePickerDialog timePickerDialog = new TimePickerDialog(NewReminderActivity.this,time,hour,minute,true);
                timePickerDialog.show();
            }
        });
    }

    /**
     * Initialize listener and DatePicker
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
                DatePickerDialog dialog = new DatePickerDialog(NewReminderActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH));
                dialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                dialog.show();
            }
        });

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
