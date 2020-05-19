package com.example.tutoresi.Data;

import androidx.lifecycle.MutableLiveData;

import com.example.tutoresi.Model.Course;

import java.util.List;

public class CourseRepository {

    private FirebaseSource firebaseSource;
    private MutableLiveData<List<Course>> mCourses;

    public CourseRepository(){
        firebaseSource = new FirebaseSource();
        mCourses = firebaseSource.getMyCourses();
    }

    public MutableLiveData<List<Course>> getMyCourses() {
        return mCourses;
    }

    public void addCourse(Course course){
        firebaseSource.addCourse(course);
    }

    public void addTutoring(String courseId, String description){
        firebaseSource.addTutoring(courseId,description);
    }
    public MutableLiveData<Boolean> checksAvailabilityCourseTutoring(Course course){
        return firebaseSource.checksAvailabilityTutoringCourse(course);
    }

    public MutableLiveData<Boolean> courseHasTutors(String courseId){
        return firebaseSource.courseHasTutors(courseId);
    }

    public void removeCourse(String courseId){
        firebaseSource.removeCourse(courseId);
    }

}
