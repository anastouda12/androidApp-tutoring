package com.example.tutoresi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.tutoresi.Data.CourseViewModel;
import com.example.tutoresi.Model.Course;

public class BecomeTutorActivity extends AppCompatActivity {

    private EditText mCourseTitle;
    private EditText mCourseLibelle;
    private EditText mDescriptionCourse;
    private EditText mDescriptionTutoring;
    private Button mBtnAddCourse;
    private CourseViewModel courseViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_become_tutor);

        mCourseTitle = (EditText) findViewById(R.id.input_course);
        mCourseLibelle = (EditText) findViewById(R.id.input_libelle);
        mDescriptionCourse = (EditText) findViewById(R.id.input_description_course);
        mDescriptionTutoring = (EditText) findViewById(R.id.input_description_tutoring);
        mBtnAddCourse = (Button) findViewById(R.id.btn_addCourse);

        courseViewModel = new ViewModelProvider(this).get(CourseViewModel.class);

        mBtnAddCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String courseTitle = mCourseTitle.getText().toString().trim();
                String courseLibelle = mCourseLibelle.getText().toString().trim();
                String descriptionCourse = mDescriptionCourse.getText().toString().trim();
                String descriptionTutoring = mDescriptionTutoring.getText().toString().trim();

                if (courseTitle.length() < 4 || courseTitle.length() > 6) {
                    mCourseTitle.setError(getResources().getString(R.string.course_idRequired));
                    return;
                }
                if (descriptionTutoring.length() < 4 || descriptionTutoring.length() > 30) {
                    mDescriptionTutoring.setError(getResources().getString(R.string.description_tutoringRequired));
                    return;
                }

                if(descriptionCourse.length() < 4 || descriptionCourse.length() > 20 ){
                    mDescriptionCourse.setError(getResources().getString(R.string.description_courseRequired));
                    return;
                }
                if (courseLibelle.length() < 4 || courseLibelle.length() > 20) {
                    mCourseLibelle.setError(getResources().getString(R.string.course_libelleRequired));
                    return;
                }
                addCourseTutoring(courseTitle,courseLibelle, descriptionCourse, descriptionTutoring);
            }
        });
    }

    private void addCourseTutoring(String courseTitle,String libelle, String descCourse, String descTutoring){
        Course course = new Course(courseTitle,libelle, descCourse);
        courseViewModel.addCourse(course); // If the course dont exist already
        courseViewModel.addTutoring(course.getId(),descTutoring);
        Toast.makeText(BecomeTutorActivity.this,getApplicationContext().getString(R.string.tutoringCreateSuccessfull)+" dans "+course.getId(),Toast.LENGTH_LONG).show();
        finish();
    }
}
