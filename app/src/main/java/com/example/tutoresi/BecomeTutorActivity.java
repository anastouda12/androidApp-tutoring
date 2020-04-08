package com.example.tutoresi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

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

                if (TextUtils.isEmpty(courseTitle)) {
                    mCourseTitle.setError("ID du cours requis.");
                    return;
                }
                if (courseTitle.length() < 4 || courseTitle.length() > 6) {
                    mCourseTitle.setError("L'ID du cours doit être composé entre 4 et 6 caracteres");
                    return;
                }
                if (TextUtils.isEmpty(descriptionCourse)) {
                    mDescriptionCourse.setError("Une description du cours est requise.");
                    return;
                }
                if (TextUtils.isEmpty(descriptionTutoring)) {
                    mDescriptionTutoring.setError("Une description de tutorat est requise.");
                    return;
                }

                if (descriptionTutoring.length() < 4 || descriptionTutoring.length() > 25) {
                    mDescriptionTutoring.setError("Une description de tutorat est requise entre 4 est 25 caractères.");
                    return;
                }

                if (TextUtils.isEmpty(courseLibelle)) {
                    mCourseLibelle.setError("Un libelle de cours est requis.");
                    return;
                }
                addCourseTutoring(courseTitle,courseLibelle, descriptionCourse, descriptionTutoring);
            }
        });
    }

    private void addCourseTutoring(String courseTitle,String libelle, String descCourse, String descTutoring){
        Course course = new Course(courseTitle,libelle, descCourse);
        courseViewModel.addCourse(course);
        courseViewModel.addTutoring(course,descTutoring);
        finish();
    }
}
