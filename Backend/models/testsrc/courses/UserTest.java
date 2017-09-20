/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package courses;

import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Juta
 */
public class UserTest {
    /**
     * Test of getUsername method, of class User.
     */
    @Test
    public void testGetUsername() {
        User instance = new User("username", Role.NONE,"password");
        String result = instance.getUsername();
        assertEquals("username", result);
    }

    /**
     * Test of setUsername method, of class User.
     */
    @Test
    public void testSetUsername() {
        User instance = new User("username", Role.NONE,"password");
        instance.setUsername("new_name");
        String result = instance.getUsername();
        assertEquals("new_name", result);
    }

    /**
     * Test of getSubscriptions and setSubscriptions method, of class User.
     */
    @Test
    public void testGetAndSetSubscriptions() {
        User instance = new User("username", Role.NONE,"password");
        List<Course> expResult = new ArrayList<>();
        Course course = new Course("course");
        expResult.add(course);
        instance.setSubscriptions(expResult);
          
        List<Course> result = instance.getSubscriptions();
        assertEquals(expResult, result);
    }

    /**
     * Test of addSubscription method, of class User.
     */
    @Test
    public void testAddSubscription() {
        User instance = new User("username", Role.NONE,"password");
        List<Course> expResult = new ArrayList<>();
        Course course = new Course("course");
        expResult.add(course);
        instance.addSubscription(course);
          
        List<Course> result = instance.getSubscriptions();
        assertEquals(expResult, result);
    }

    /**
     * Test of getRole method, of class User.
     */
    @Test
    public void testGetRole() {
        User instance = new User("username", Role.NONE,"password");
        Role result = instance.getRole();
        assertEquals(Role.NONE, result);
    }

    /**
     * Test of setRole method, of class User.
     */
    @Test
    public void testSetRole() {
        User instance = new User("username", Role.NONE,"password");
        instance.setRole(Role.ADMIN);
        Role result = instance.getRole();
        assertEquals(Role.ADMIN, result);
    }

    /**
     * Test of getId and setId method, of class User.
     */
    @Test
    public void testGetAndSetId() {
        User instance = new User("username", Role.NONE,"password");
        instance.setId(1);
        int result = instance.getId();
        assertEquals(1, result);
    }
    
    /**
     * Test of getPassword method, of class User.
     */
    @Test
    public void testGetPassword() {
    	User instance = new User("username", Role.NONE,"password");
    	String result = instance.getPassword();
    	assertEquals("password", result);
    }
    
    /**
     * Test of setPassword method, of class User.
     */
    @Test
    public void testSetPassword() {
    	User instance = new User("username", Role.NONE,"password");
    	instance.setPassword("password2");
    	String result = instance.getPassword();
    	assertEquals("password2", result);
    }
}
