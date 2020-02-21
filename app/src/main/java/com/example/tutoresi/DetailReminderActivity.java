package com.example.tutoresi;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import com.example.tutoresi.Model.Reminder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DetailReminderActivity extends AppCompatActivity {

    private EditText mCourse, mDate, mLocation;
    private Button mBtnAdd;
    private final Calendar myCalendar = Calendar.getInstance();
    private DatabaseReference databaseReference;
    private DatePickerDialog.OnDateSetListener date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_reminder);

        mCourse = (EditText) findViewById(R.id.input_reminder_course);
        mDate = (EditText) findViewById(R.id.input_reminder_date);
        mLocation = (EditText) findViewById(R.id.input_reminder_location);
        mBtnAdd = (Button) findViewById(R.id.btn_reminder_register);


        databaseReference = FirebaseDatabase.getInstance().getReference().child("reminders").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
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



        mBtnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String course = mCourse.getText().toString().trim();
                String date = mDate.getText().toString().trim();
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
                writeNewReminder(course,date,location);
                finish();

            }
        });

    }

    /**
     * Write new reminder on DB
     *
     */
    private void writeNewReminder(String course, String date, String location) {
        Reminder reminder = new Reminder(course,date,location);
        databaseReference.push().setValue(reminder);
    }


    private void updateLabel() {
        String myFormat = "dd/MM/yy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.FRANCE);
        mDate.setText(sdf.format(myCalendar.getTime()));
    }
}
