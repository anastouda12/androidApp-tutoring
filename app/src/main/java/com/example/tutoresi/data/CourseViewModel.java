package com.example.tutoresi.data;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import com.example.tutoresi.model.Course;
import com.example.tutoresi.model.Tutoring;

import java.util.List;

/**
 * CourseViewModel
 */
public class CourseViewModel extends ViewModel {

    private CourseRepository courseRepository;
    private LiveData<List<Course>> allCourses;

    /**
     * CourseViewModel constructor
     */
    public CourseViewModel(){
        courseRepository = CourseRepository.getInstance();
        allCourses = courseRepository.getCourses();
    }

    /**
     * Add a course in DB
     * @param course the course to add
     * @return Code representing the success or fail of the action
     * (CODE 1 = Course added successful, CODE -4 = Course already exist, CODE -5 = course addition fail.
     */
    public LiveData<Integer> addCourse(Course course){
        return courseRepository.addCourse(course);
    }

    /**
     * Add tutoring to a course given
     * @param courseId course to add tutoring inside it
     * @param description description of the tutoring
     * @return true if the action was successfully
     */
    public LiveData<Boolean> addTutoring(String courseId, String description){
        return courseRepository.addTutoring(courseId, description);
    }

    /**
     * checks if a course given has tutoring
     * @param courseId course to checks
     * @return true if the course has tutoring
     */
    public  LiveData<Boolean> courseHasTutors(String courseId){
        return courseRepository.courseHasTutors(courseId);
    }

    /**
     * Remove a course from DB.
     * @param courseId course to remove.
     * @return true if the course was removed
     */
    public LiveData<Boolean> removeCourse(String courseId){
        return courseRepository.removeCourse(courseId);
    }

    /**
     * Get all courses
     * @return list of all courses
     */
    public LiveData<List<Course>> getAllCourses(){
        return allCourses;
    }

    /**
     * Get all tutoring of a course
     * @param courseID course to get tutoring
     * @return list of tutoring
     */
    public LiveData<List<Tutoring>> getTutoringOfCourse(String courseID){
        return courseRepository.getTutoringOfCourse(courseID);
    }
}
