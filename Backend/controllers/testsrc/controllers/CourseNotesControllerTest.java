/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import courses.Role;
import courses.User;
import dao.CoursesMockUpDAO;
import dao.MockUpDAO;
import databaseaccess.exceptions.ClassicDatabaseException;
import databaseaccess.exceptions.ClassicNotFoundException;
import databaseaccess.exceptions.ClassicUnauthorizedException;
import java.util.List;
import models.CourseNotes;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Juta
 */
public class CourseNotesControllerTest {
    
    private CourseNotesController courseNotesController;
    
    @Before
    public void setUp() throws ClassicDatabaseException {
        MockUpDAO mockup = new MockUpDAO();
        CoursesMockUpDAO coursesMockup = new CoursesMockUpDAO();
        courseNotesController = new CourseNotesController(mockup, coursesMockup, coursesMockup, "/var/www/classic/resources/courses");
        userId = coursesMockup.addUser(new User("username", Role.ADMIN, "password"));
    }
    
    private int userId;
    private int courseId = 1;

    /**
     * Test of addCourseNotes method, of class CourseNotesController.
     * @throws databaseaccess.exceptions.ClassicDatabaseException
     * @throws databaseaccess.exceptions.ClassicNotFoundException
     */
    @Test
    public void testAddCourseNotes() throws ClassicDatabaseException, ClassicNotFoundException, ClassicUnauthorizedException {
        CourseNotes notes = new CourseNotes("title", "url");
        int courseNotesId = courseNotesController.addCourseNotes(courseId, notes, userId);
        CourseNotes result = courseNotesController.getCourseNotes(courseNotesId);
        assertEquals(courseNotesId, result.getId());
    }

    /**
     * Test of getAllCourseNotes method, of class CourseNotesController.
     * @throws databaseaccess.exceptions.ClassicDatabaseException
     * @throws databaseaccess.exceptions.ClassicNotFoundException
     */
    @Test
    public void testGetCourseNotes() throws ClassicDatabaseException, ClassicNotFoundException, ClassicUnauthorizedException {
        CourseNotes notes = new CourseNotes("title", "url");
        int courseNotesId = courseNotesController.addCourseNotes(courseId, notes, userId);
        CourseNotes result = courseNotesController.getCourseNotes(courseNotesId);
        
        assertEquals(notes.getTitle(), result.getTitle());
        assertEquals(notes.getUrl(), result.getUrl());
        assertEquals(courseNotesId, result.getId());  
    }
    
    /**
     * Test of getAllCourseNotes method, of class CourseNotesController.
     * @throws databaseaccess.exceptions.ClassicDatabaseException
     * @throws databaseaccess.exceptions.ClassicNotFoundException
     */
    @Test(expected=ClassicNotFoundException.class)
    public void testGetCourseNotesNotFoundException() throws ClassicDatabaseException, ClassicNotFoundException {
        courseNotesController.getCourseNotes(0);
    }

    /**
     * Test of getAllCourseNotes method, of class CourseNotesController.
     * @throws databaseaccess.exceptions.ClassicDatabaseException
     */
    @Test
    public void testGetAllCourseNotes() throws ClassicDatabaseException, ClassicNotFoundException, ClassicUnauthorizedException {
        CourseNotes notes = new CourseNotes("title", "url");
        int courseNotesId = courseNotesController.addCourseNotes(courseId, notes, userId);
        CourseNotes notes2 = new CourseNotes("title2", "url2");
        int courseNotesId2 = courseNotesController.addCourseNotes(courseId, notes2, userId);
        
        List<CourseNotes> result = courseNotesController.getAllCourseNotes(courseId);
        
        assertEquals(notes.getTitle(), result.get(0).getTitle());
        assertEquals(notes.getUrl(), result.get(0).getUrl());
        assertEquals(courseNotesId, result.get(0).getId());
        
        assertEquals(notes2.getTitle(), result.get(1).getTitle());
        assertEquals(notes2.getUrl(), result.get(1).getUrl());
        assertEquals(courseNotesId2, result.get(1).getId());
    }
    
    /**
     * Test of getAllCourseNotes method, of class CourseNotesController.
     * @throws databaseaccess.exceptions.ClassicDatabaseException
     */
    @Test
    public void testGetAllCourseNotesIsEmpty() throws ClassicDatabaseException, ClassicNotFoundException {
        List<CourseNotes> result = courseNotesController.getAllCourseNotes(courseId);
        
        assertEquals(true, result.isEmpty());
    }

    /**
     * Test of updateCourseNotes method, of class CourseNotesController.
     * @throws databaseaccess.exceptions.ClassicDatabaseException
     * @throws databaseaccess.exceptions.ClassicNotFoundException
     */
    @Test
    public void testUpdateCourseNotes() throws ClassicDatabaseException, ClassicNotFoundException, ClassicUnauthorizedException {
        CourseNotes notes = new CourseNotes("title", "url");
        int courseNotesId = courseNotesController.addCourseNotes(courseId, notes, userId);
        notes = new CourseNotes("title2", "url2");
        courseNotesController.updateCourseNotes(courseNotesId, notes, userId);
        CourseNotes result = courseNotesController.getCourseNotes(courseNotesId);
        
        assertEquals(notes.getTitle(), result.getTitle());
        assertEquals(notes.getUrl(), result.getUrl());
        assertEquals(courseNotesId, result.getId()); 
    }
    
    /**
     * Test of updateCourseNotes method, of class CourseNotesController.
     * @throws databaseaccess.exceptions.ClassicDatabaseException
     * @throws databaseaccess.exceptions.ClassicNotFoundException
     */
    @Test(expected=ClassicNotFoundException.class)
    public void testUpdateCourseNotesNotFoundException() throws ClassicDatabaseException, ClassicNotFoundException, ClassicUnauthorizedException {
        CourseNotes notes = new CourseNotes("title", "url");
        courseNotesController.addCourseNotes(courseId, notes, userId);
        notes = new CourseNotes("title2", "url2");
        
        courseNotesController.updateCourseNotes(0, notes, userId);
    }

    /**
     * Test of deleteCourseNotes method, of class CourseNotesController.
     * @throws databaseaccess.exceptions.ClassicDatabaseException
     * @throws databaseaccess.exceptions.ClassicNotFoundException
     */
    @Test(expected=ClassicNotFoundException.class)
    public void testDeleteCourseNotes() throws ClassicDatabaseException, ClassicNotFoundException, ClassicUnauthorizedException {
        CourseNotes notes = new CourseNotes("title", "url");
        int courseNotesId = courseNotesController.addCourseNotes(courseId, notes, userId);
        courseNotesController.deleteCourseNotes(courseNotesId, userId);
        
        courseNotesController.getCourseNotes(courseNotesId);
    }
    
    /**
     * Test of deleteCourseNotes method, of class CourseNotesController.
     * @throws databaseaccess.exceptions.ClassicDatabaseException
     * @throws databaseaccess.exceptions.ClassicNotFoundException
     */
    @Test(expected=ClassicNotFoundException.class)
    public void testDeleteCourseNotesNotFoundException() throws ClassicDatabaseException, ClassicNotFoundException, ClassicUnauthorizedException {
        courseNotesController.deleteCourseNotes(0, userId);
    }
}
