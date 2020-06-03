package com.example.tutoresi.Data;

import androidx.lifecycle.MutableLiveData;

import com.example.tutoresi.Model.Course;
import com.example.tutoresi.Model.Tutoring;

import java.util.List;


/**
 * Course repository
 */
public class CourseRepository {

    private FirebaseSource firebaseSource;
    private static CourseRepository instance = null;

    /**
     * Constructor of course repository
     */
    private CourseRepository(){
        firebaseSource = FirebaseSource.getInstance();
    }

    /**
     * Get instance of repository course
     * @return instance of repository course
     */
    public static CourseRepository getInstance(){
        if(instance==null){
            instance = new CourseRepository();
        }
        return instance;
    }

    /**
     * Add a course in DB.
     * @param course course to ADD
     * @return Code representing the succes or fail of the action
     * (CODE 1 = Course added successful, CODE -4 = Course already exist, CODE -5 = course addition fail.
     */
    public MutableLiveData<Integer> addCourse(Course course){
        return firebaseSource.addCourse(course);
    }

    /**
     * Add tutoring to a course
     * @param courseId course to add tutoring
     * @param description description of the tutoring
     * @return true if the tutoring was added
     */
    public MutableLiveData<Boolean> addTutoring(String courseId, String description){
        return firebaseSource.addTutoring(courseId,description);
    }

    /**
     * Checks if a course has tutoring
     * @param courseId course to checks
     * @return true if the course has tutoring
     */
    public MutableLiveData<Boolean> courseHasTutors(String courseId){
        return firebaseSource.courseHasTutors(courseId);
    }

    /**
     * Remove course from DB.
     * @param courseId course to remove.
     * @return true if the course was removed successfully
     */
    public MutableLiveData<Boolean> removeCourse(String courseId){
        return firebaseSource.removeCourse(courseId);
    }

    /**
     * Get the list of the courses
     * @return list of courses
     */
    public MutableLiveData<List<Course>> getCourses(){
        return firebaseSource.getCourses();
    }

    /**
     * Get all tutoring of a course given
     * @param courseID course to get tutoring
     * @return list of tutoring.
     */
    public MutableLiveData<List<Tutoring>> getTutoringOfCourse(String courseID){
        return firebaseSource.getTutoringOfCourse(courseID);
    }

}
