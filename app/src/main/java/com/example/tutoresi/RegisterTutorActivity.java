package com.example.tutoresi;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tutoresi.Data.CourseViewModel;

public class RegisterTutorActivity extends AbstractActivity {

    private Button mBtnRegister;
    private EditText mDescriptionTutoring;
    private TextView mTitle;
    private String course_id;
    private CourseViewModel courseViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_tutor);

        mBtnRegister = (Button) findViewById(R.id.btn_registerTutor);
        mDescriptionTutoring = (EditText) findViewById(R.id.input_description_tutoring);
        mTitle = (TextView) findViewById(R.id.course_title_tutoring);
        courseViewModel = new ViewModelProvider(this).get(CourseViewModel.class);

        course_id = getIntent().getStringExtra("course_id");
        mTitle.setText(getResources().getText(R.string.become_tutor)+" "+course_id);

        mBtnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String description =  mDescriptionTutoring.getText().toString().trim();
                if (description.length() < 4 || description.length() > 100) {
                    mDescriptionTutoring.setError(getResources().getString(R.string.description_tutoringRequired));
                    return;
                }
                registerTutoring(course_id,description);
            }
        });

    }

    private void registerTutoring(String courseId, String descriptionTutoring){
        courseViewModel.addTutoring(courseId,descriptionTutoring);
        Toast.makeText(RegisterTutorActivity.this,getApplicationContext().getString(R.string.tutoringCreateSuccessfull)+" dans "+course_id,Toast.LENGTH_LONG).show();
        finish();
    }
}
