/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import courses.Course;
import courses.Role;
import courses.User;
import databaseaccess.exceptions.ClassicDatabaseException;
import databaseaccess.exceptions.ClassicNotFoundException;
import databaseaccess.exceptions.ClassicUnauthorizedException;
import databaseaccess.implementation.CourseDao;
import databaseaccess.implementation.CourseNotesDao;
import databaseaccess.implementation.LectureDao;
import databaseaccess.implementation.UserDao;
import databaseaccess.implementation.VideoDao;
import databaseaccess.interfaces.ICourseDao;
import databaseaccess.interfaces.ICourseNotesDao;
import databaseaccess.interfaces.ILectureDao;
import databaseaccess.interfaces.IUserDao;
import databaseaccess.interfaces.IVideoDao;

import java.util.List;
import models.CourseNotes;
import org.junit.After;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Juta
 */
public class CourseNotesControllerIT {
    
    private CourseNotesController courseNotesController;
    private final String TEST_DATABASE_CONFIG = "testdbconfig.properties";
    private ICourseNotesDao notesDao;
    
    //courses 
    private CourseController courseControler;
    private ICourseDao coursedao;
    private ILectureDao lecturedao;
    private UserController userController;
    private IUserDao userdao;
    private IVideoDao videodao;
    private int courseId;
    private int userId;

    @Before
    public void setUp() throws ClassicDatabaseException, ClassicNotFoundException {
        notesDao = new CourseNotesDao(TEST_DATABASE_CONFIG);
        videodao = new VideoDao(TEST_DATABASE_CONFIG);
        coursedao = new CourseDao(TEST_DATABASE_CONFIG);
        lecturedao = new LectureDao(TEST_DATABASE_CONFIG);

        userdao = new UserDao(TEST_DATABASE_CONFIG);
        courseControler = new CourseController(coursedao, lecturedao, videodao, notesDao, userdao, "/var/www/classic/resources/courses");
        userController = new	 UserController(userdao);
        courseNotesController = new CourseNotesController(notesDao, coursedao, userdao, "/var/www/classic/resources/courses");
        
        User u = new User("prof", Role.TEACHER,"password");
        userId = userController.addUser(u);
        Course c = new Course("course");
        courseId = courseControler.addCourse(c, userId);
    }
    
    @After
    public void tearDown() throws ClassicDatabaseException {
        notesDao.cleanTable();
        coursedao.cleanTable();
        userdao.cleanTable();
    }

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
