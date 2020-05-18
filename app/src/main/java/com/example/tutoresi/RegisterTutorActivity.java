package com.example.tutoresi;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class RegisterTutorActivity extends AppCompatActivity {

    private Button mBtnRegister;
    private EditText mDescriptionTutoring;
    private TextView mTitle;
    private String courseTutoring;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_tutor);

        mBtnRegister = (Button) findViewById(R.id.btn_registerTutor);
        mDescriptionTutoring = (EditText) findViewById(R.id.input_description_tutoring);
        mTitle = (TextView) findViewById(R.id.course_title_tutoring);
        courseTutoring = getIntent().getStringExtra("course_id");

        mTitle.setText(getResources().getText(R.string.become_tutor)+" "+courseTutoring);

        mBtnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String description =  mDescriptionTutoring.getText().toString().trim();
                if(TextUtils.isEmpty(description)){
                    mDescriptionTutoring.setError("Une description de tutorat est requise.");
                    return;
                }
                // S'inscrire dans le tutorat
            }
        });

    }
}
