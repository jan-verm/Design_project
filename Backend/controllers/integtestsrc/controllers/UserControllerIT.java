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
import databaseaccess.implementation.CourseDao;
import databaseaccess.implementation.CourseNotesDao;
import databaseaccess.implementation.UserDao;
import databaseaccess.implementation.VideoDao;
import databaseaccess.interfaces.ICourseDao;
import databaseaccess.interfaces.ICourseNotesDao;
import databaseaccess.interfaces.ILectureDao;
import databaseaccess.interfaces.IUserDao;
import databaseaccess.interfaces.IVideoDao;
import exceptions.ClassicCharacterNotAllowedException;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Juta
 */
public class UserControllerIT {
	
    private final String TEST_DATABASE_CONFIG = "testdbconfig.properties";
    private UserController userController;
    private CourseController courseController;
    private IVideoDao videodao;
    private ICourseNotesDao courseNotesdao;
    private ICourseDao coursedao;
    private ILectureDao lecturedao;
    private IUserDao userdao;

    @Before
    public void setUp() throws ClassicDatabaseException {
        videodao = new VideoDao(TEST_DATABASE_CONFIG);
        courseNotesdao = new CourseNotesDao(TEST_DATABASE_CONFIG);
        userdao = new UserDao(TEST_DATABASE_CONFIG);
        coursedao = new CourseDao(TEST_DATABASE_CONFIG);
        courseController = new CourseController(coursedao, lecturedao, videodao, courseNotesdao, userdao, "/var/www/classic/resources/courses");
        userController = new UserController(userdao);
    }
    
    @After
    public void tearDown() throws ClassicDatabaseException {
        videodao.cleanTable();
        courseNotesdao.cleanTable();
        coursedao.cleanTable();
        userdao.cleanTable();
    }

    /**
     * Test of addUser method, of class UserController.
     */
    @Test
    public void testAddUser() throws Exception {
        User user = new User("username", Role.STUDENT, "password");
        int id = userController.addUser(user);
        assertNotEquals(0, id);
    }
    
    /**
     * Test of addUser method, of class UserController.
     */
    @Test(expected=ClassicCharacterNotAllowedException.class)
    public void testAddUserCharacterNotAllowedException() throws Exception {
        User user = new User("username#", Role.STUDENT, "password");
        userController.addUser(user);
    }
    
    /**
     * Test of addLTIUser method, of class UserController.
     */
    @Test
    public void testAddLTIUser() throws Exception {
        User user = new User("consumer#username", Role.STUDENT, "lti_never_hashed");
        int id = userController.addLTIUser(user);
        assertNotEquals(0, id);
    }

    /**
     * Test of getUser method, of class UserController.
     */
    @Test
    public void testGetUser_String() throws Exception {
    	User user = new User("username", Role.STUDENT, "password");
        int id = userController.addUser(user);
        User result = userController.getUser("username");
        
        assertEquals(user.getUsername(), result.getUsername());
        assertEquals(user.getPassword(), result.getPassword());
        assertEquals(user.getRole(), result.getRole());
        assertEquals(id, result.getId());
    }

    /**
     * Test of getUser method, of class UserController.
     */
    @Test
    public void testGetUser_int() throws Exception {
    	User user = new User("username", Role.STUDENT, "password");
        int id = userController.addUser(user);
        User result = userController.getUser(id);
        
        assertEquals(user.getUsername(), result.getUsername());
        assertEquals(user.getPassword(), result.getPassword());
        assertEquals(user.getRole(), result.getRole());
        assertEquals(id, result.getId());
    }

    /**
     * Test of subscribeCourse method, of class UserController.
     */
    @Test
    public void testSubscribeCourse() throws Exception {
    	User user = new User("username", Role.TEACHER, "password");
        int id = userController.addUser(user);
        Course course = new Course("course");
        int courseId = courseController.addCourse(course, id);
        
        User user2 = new User("username2", Role.STUDENT, "password");
        int id2 = userController.addUser(user2);
        userController.subscribeCourse(id2, courseId);
        
        User result = userController.getUser(id2);
        
        assertEquals(user2.getUsername(), result.getUsername());
        assertEquals(user2.getPassword(), result.getPassword());
        assertEquals(user2.getRole(), result.getRole());
        assertEquals(id2, result.getId());
        assertEquals(course.getName(), result.getSubscriptions().get(0).getName());
       
    }

    /**
     * Test of unSubscribeCourse method, of class UserController.
     */
    @Test
    public void testUnSubscribeCourse() throws Exception {
    	User user = new User("username", Role.TEACHER, "password");
        int id = userController.addUser(user);
        Course course = new Course("course");
        int courseId = courseController.addCourse(course, id);
        userController.subscribeCourse(id, courseId);
        userController.unSubscribeCourse(id, courseId);
        
        User result = userController.getUser(id);
        
        assertEquals(user.getUsername(), result.getUsername());
        assertEquals(user.getPassword(), result.getPassword());
        assertEquals(user.getRole(), result.getRole());
        assertEquals(id, result.getId());
        assertEquals(true, user.getSubscriptions().isEmpty());
    }

    /**
     * Test of getUsers method, of class UserController.
     */
    @Test
    public void testGetUsers() throws Exception {
    	User user = new User("username", Role.STUDENT, "password");
        int id = userController.addUser(user);
        User user2 = new User("username2", Role.TEACHER, "password2");
        int id2 = userController.addUser(user2);
        List<User> result = userController.getUsers();
        
        assertEquals(user.getUsername(), result.get(0).getUsername());
        assertEquals(user.getPassword(), result.get(0).getPassword());
        assertEquals(user.getRole(), result.get(0).getRole());
        assertEquals(id, result.get(0).getId());
        
        assertEquals(user2.getUsername(), result.get(1).getUsername());
        assertEquals(user2.getPassword(), result.get(1).getPassword());
        assertEquals(user2.getRole(), result.get(1).getRole());
        assertEquals(id2, result.get(1).getId());
    }

    /**
     * Test of updateUser method, of class UserController.
     */
    @Test
    public void testUpdateUser() throws Exception {
    	User user = new User("username", Role.STUDENT, "password");
        int id = userController.addUser(user);
        user = new User("username2", Role.TEACHER, "password");
        userController.updateUser(id, user, id);
        User result = userController.getUser(id);
        
        assertEquals(user.getUsername(), result.getUsername());
        assertEquals(user.getPassword(), result.getPassword());
        assertEquals(user.getRole(), result.getRole());
        assertEquals(id, result.getId());
    }

    /**
     * Test of deleteUser method, of class UserController.
     */
    @Test(expected=ClassicNotFoundException.class)
    public void testDeleteUser() throws Exception {
    	User user = new User("username", Role.STUDENT, "password");
        int id = userController.addUser(user);
        userController.deleteUser(id, id);
        
        userController.getUser(id);
    }
    
}
