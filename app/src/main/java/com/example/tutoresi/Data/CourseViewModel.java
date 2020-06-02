package com.example.tutoresi.Data;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import com.example.tutoresi.Model.Course;

public class CourseViewModel extends ViewModel {

    private CourseRepository courseRepository;

    public CourseViewModel(){
        courseRepository = new CourseRepository();
    }

    public LiveData<Integer> addCourse(Course course){
        return courseRepository.addCourse(course);
    }

    public LiveData<Boolean> addTutoring(String courseId, String description){
        return courseRepository.addTutoring(courseId, description);
    }

    public  LiveData<Boolean> courseHasTutors(String courseId){
        return courseRepository.courseHasTutors(courseId);
    }

    public LiveData<Boolean> removeCourse(String courseId){
        return courseRepository.removeCourse(courseId);
    }
}
