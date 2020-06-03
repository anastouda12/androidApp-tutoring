package com.example.tutoresi.Data;

import androidx.lifecycle.MutableLiveData;

import com.example.tutoresi.Model.Course;


public class CourseRepository {

    private FirebaseSource firebaseSource;
    private static CourseRepository instance = getInstance();;

    private CourseRepository(){
        firebaseSource = FirebaseSource.getInstance();
    }

    public static CourseRepository getInstance(){
        if(instance==null){
            instance = new CourseRepository();
        }
        return instance;
    }

    public MutableLiveData<Integer> addCourse(Course course){
        return firebaseSource.addCourse(course);
    }

    public MutableLiveData<Boolean> addTutoring(String courseId, String description){
        return firebaseSource.addTutoring(courseId,description);
    }

    public MutableLiveData<Boolean> courseHasTutors(String courseId){
        return firebaseSource.courseHasTutors(courseId);
    }

    public MutableLiveData<Boolean> removeCourse(String courseId){
        return firebaseSource.removeCourse(courseId);
    }

}
