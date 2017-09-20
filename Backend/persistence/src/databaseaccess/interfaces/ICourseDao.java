/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package databaseaccess.interfaces;

import courses.Course;
import courses.User;
import databaseaccess.exceptions.ClassicDatabaseException;
import databaseaccess.exceptions.ClassicNotFoundException;
import java.util.List;

/**
 *
 * @author jorsi
 */
public interface ICourseDao {
    
    /**
     * Add a course to the Classic database.
     * @param course The course object that is to be added.
     * @return Id of the added course 
     * @throws ClassicDatabaseException
     */
    public int addCourse(Course course) throws ClassicDatabaseException;
    
    /**
     * Delete a course from the Classic database.
     * @param courseID Id of the course that is to be deleted.
     * @throws ClassicDatabaseException
     * @throws ClassicNotFoundException
     */
    public void deleteCourse(int courseID) throws ClassicDatabaseException, ClassicNotFoundException;
    
    /**
     * Get a course from the Classic database.
     * @param courseID Id of the course to get.
     * @return Course object of the wanted course
     * @throws ClassicDatabaseException
     * @throws ClassicNotFoundException
     */
    public Course getCourse(int courseID) throws ClassicDatabaseException, ClassicNotFoundException;
    
    /**
     * Get a list of all courses in the Classic database.
     * @return List of all course objects.
     * @throws ClassicDatabaseException
     */
    public List<Course> getCourses() throws ClassicDatabaseException;
    
    /**
     * Get the owner of the course.
     * @param courseID Id of the course for which the owner is wanted.
     * @return User object that represents the owner of the course.
     * @throws ClassicDatabaseException
     * @throws ClassicNotFoundException
     */
    public User getOwner(int courseID) throws ClassicDatabaseException, ClassicNotFoundException;
    
    /**
     * Update a course in the Classic database. Used to change The course title.
     * @param course Course object that is to be updated.
     * @throws ClassicDatabaseException
     * @throws ClassicNotFoundException
     */
    public void updateCourse(Course course) throws ClassicDatabaseException, ClassicNotFoundException;
    
    /**
     * Delete all Course and Lectures from the Classic database.
     * @throws ClassicDatabaseException
     */
    public void cleanTable() throws ClassicDatabaseException;
}
