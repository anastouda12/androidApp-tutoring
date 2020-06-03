package com.example.tutoresi;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class NewCourseActivity extends AbstractActivity {

    private EditText mCourseTitle;
    private EditText mCourseLib;
    private EditText mDescriptionCourse;
    private EditText mDescriptionTutoring;
    private Button mBtnAddCourse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_become_tutor);

        mCourseTitle = (EditText) findViewById(R.id.input_course);
        mCourseLib = (EditText) findViewById(R.id.input_libelle);
        mDescriptionCourse = (EditText) findViewById(R.id.input_description_course);
        mDescriptionTutoring = (EditText) findViewById(R.id.input_description_tutoring);
        mBtnAddCourse = (Button) findViewById(R.id.btn_addCourse);

        mBtnAddCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String courseTitle = mCourseTitle.getText().toString().trim();
                String courseLib = mCourseLib.getText().toString().trim();
                String descriptionCourse = mDescriptionCourse.getText().toString().trim();
                String descriptionTutoring = mDescriptionTutoring.getText().toString().trim();

                if (courseTitle.length() < 4 || courseTitle.length() > 6) {
                    mCourseTitle.setError(getResources().getString(R.string.course_idRequired));
                    return;
                }
                if (descriptionTutoring.length() < 4 || descriptionTutoring.length() > 100) {
                    mDescriptionTutoring.setError(getResources().getString(R.string.description_tutoringRequired));
                    return;
                }

                if(descriptionCourse.length() < 4 || descriptionCourse.length() > 30 ){
                    mDescriptionCourse.setError(getResources().getString(R.string.description_courseRequired));
                    return;
                }
                if (courseLib.length() < 4 || courseLib.length() > 25) {
                    mCourseLib.setError(getResources().getString(R.string.course_libelleRequired));
                    return;
                }
                Intent replyIntent = new Intent();
                replyIntent.putExtra("EXTRA_COURSE_TITLE",courseTitle);
                replyIntent.putExtra("EXTRA_COURSE_LIBEL",courseLib);
                replyIntent.putExtra("EXTRA_COURSE_DESC",descriptionCourse);
                replyIntent.putExtra("EXTRA_TUTORING_DESC",descriptionTutoring);
                setResult(RESULT_OK,replyIntent);
                finish();
            }
        });
    }
}
