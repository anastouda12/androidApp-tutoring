package com.example.tutoresi.Data;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.tutoresi.Model.Course;

import java.util.List;

public class CourseViewModel extends ViewModel {

    private CourseRepository courseRepository;
    private MutableLiveData<List<Course>> mCourses;

    public CourseViewModel(){
        courseRepository = new CourseRepository();
        mCourses = courseRepository.getMyCourses();
    }

    public MutableLiveData<List<Course>> getMyCourses() {
        return courseRepository.getMyCourses();
    }

    public void addCourse(Course course){
        courseRepository.addCourse(course);
    }

    public void addTutoring(Course course, String description){
        courseRepository.addTutoring(course, description);
    }

    public MutableLiveData<Boolean> checksAvailabilityCourseTutoring(Course course){
        return courseRepository.checksAvailabilityCourseTutoring(course);
    }
}
