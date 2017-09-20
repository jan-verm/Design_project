/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package interfaces;

import courses.Course;
import courses.Lecture;
import databaseaccess.exceptions.ClassicDatabaseException;
import databaseaccess.exceptions.ClassicNotFoundException;
import databaseaccess.exceptions.ClassicUnauthorizedException;
import java.util.List;

/**
 *
 * @author Juta
 */
public interface CourseControllerInterface {
    
    //COURSES
    
    /**
     * Method to add a course
     * @param course Course object that needs to be added
     * @param profId id of the prof that creates the course
     * @return id of the created course
     * @throws ClassicDatabaseException
     * @throws ClassicNotFoundException
     */
    public int addCourse(Course course, int profId)
            throws ClassicDatabaseException, ClassicNotFoundException;
    
    /**
     * Method to get a course
     * @param courseid id of the course
     * @return Course object
     * @throws ClassicDatabaseException
     * @throws ClassicNotFoundException
     */
    public Course getCourse(int courseid) throws ClassicDatabaseException, ClassicNotFoundException;
    
    /**
     * get all courses
     * @return list of all courses
     * @throws ClassicDatabaseException
     */
    public List<Course> getCourses() throws ClassicDatabaseException;
    
    /**
     * Method to update a course
     * @param courseId id of the course
     * @param course the updated Course object
     * @throws ClassicDatabaseException
     * @throws ClassicNotFoundException
     */
    public void updateCourse(int courseId, Course course, int profId)
            throws ClassicDatabaseException, ClassicNotFoundException, ClassicUnauthorizedException;
    
    /**
     * Method to delete a course
     * @param courseId id of the course
     * @throws ClassicDatabaseException
     * @throws ClassicNotFoundException
     */
    public void deleteCourse(int courseId, int profId)
            throws ClassicDatabaseException, ClassicNotFoundException, ClassicUnauthorizedException;
    
    // LECTURES

    /**
     * Method to add a lecture
     * @param courseId id of the course
     * @param lecture Lecture object
     * @param profId id of the prof that creates the lecture
     * @return the id of the created lecture
     * @throws ClassicDatabaseException
     * @throws ClassicNotFoundException
     */
    
    public int addLecture(int courseId, Lecture lecture, int profId)
            throws ClassicDatabaseException, ClassicNotFoundException, ClassicUnauthorizedException;
    
    /**
     * Method to get a lecture
     * @param lectureId id of the lecture
     * @return the lecture
     * @throws ClassicDatabaseException
     * @throws ClassicNotFoundException
     */
    public Lecture getLecture(int lectureId) throws ClassicDatabaseException, ClassicNotFoundException;
    
    /**
     * Method to get all lectures in a course
     * @param courseId id of the course
     * @return list of lectures
     * @throws ClassicDatabaseException
     * @throws ClassicNotFoundException
     */
    public List<Lecture> getLectures(int courseId) throws ClassicDatabaseException, ClassicNotFoundException;
    
    /**
     * Method to update a lecture
     * @param lectureId id of the lecture
     * @param lecture updated lecture object
     * @throws ClassicDatabaseException
     * @throws ClassicNotFoundException
     */
    public void updateLecture(int lectureId, Lecture lecture, int profId) 
            throws ClassicDatabaseException, ClassicNotFoundException, ClassicUnauthorizedException;
     
    /**
     * Method to delete a lecture
     * @param lectureId id of the lecture
     * @throws ClassicDatabaseException
     * @throws ClassicNotFoundException
     */
    public void deleteLecture(int lectureId, int profId) 
            throws ClassicDatabaseException, ClassicNotFoundException, ClassicUnauthorizedException;
}
