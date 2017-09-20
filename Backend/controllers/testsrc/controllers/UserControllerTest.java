/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import courses.Course;
import courses.Role;
import courses.User;
import dao.CoursesMockUpDAO;
import dao.MockUpDAO;
import databaseaccess.exceptions.ClassicDatabaseException;
import databaseaccess.exceptions.ClassicNotFoundException;
import databaseaccess.exceptions.ClassicUnauthorizedException;
import exceptions.ClassicCharacterNotAllowedException;

import java.util.List;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Juta
 */
public class UserControllerTest {
	
	private UserController userController;
	private CourseController courseController;

    @Before
    public void setUp() throws ClassicDatabaseException {
        CoursesMockUpDAO coursemockup = new CoursesMockUpDAO();
        userController = new UserController(coursemockup);
        MockUpDAO mockup = new MockUpDAO();
        courseController = new CourseController(coursemockup, coursemockup, mockup, mockup, coursemockup, "/var/www/classic/resources/courses");
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
        userController.subscribeCourse(id, courseId);
        
        User result = userController.getUser(id);
        
        assertEquals(user.getUsername(), result.getUsername());
        assertEquals(user.getPassword(), result.getPassword());
        assertEquals(user.getRole(), result.getRole());
        assertEquals(id, result.getId());
        assertEquals(course.getName(), user.getSubscriptions().get(0).getName());
       
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
     * Test of updateUser method, of class UserController.
     */
    @Test(expected=ClassicUnauthorizedException.class)
    public void testUpdateUserUnAuthorizedException() throws Exception {
    	User user = new User("username", Role.STUDENT, "password");
        int id = userController.addUser(user);
        User user2 = new User("username2", Role.STUDENT, "password");
        int id2 = userController.addUser(user2);
        
        user = new User("username", Role.STUDENT, "password52");
        userController.updateUser(id, user, id2);
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
