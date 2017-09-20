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
public class CourseTest {
    
    /**
     * Test of getLectures and setLectures method, of class Course.
     */
    @Test
    public void testGetAndSetLectures() {
        Course instance = new Course("course");
        Lecture lecture = new Lecture("lecture");
        List<Lecture> expResult = new ArrayList<>();
        expResult.add(lecture);
        instance.setLectures(expResult);
        
        List<Lecture> result = instance.getLectures();
        assertEquals(expResult, result);
    }

    /**
     * Test of addLecture method, of class Course.
     */
    @Test
    public void testAddLecture() {
        Course instance = new Course("course");
        Lecture lecture = new Lecture("lecture");
        List<Lecture> expResult = new ArrayList<>();
        expResult.add(lecture);
        instance.addLecture(lecture);
        
        List<Lecture> result = instance.getLectures();
        assertEquals(expResult, result);
    }
    
    /**
     * Test of getOwner and setOwner methods, of class Course.
     */
    @Test
    public void testOwner() {
    	Course instance = new Course("course");
    	User user = new User("username", Role.ADMIN, "password");
    	instance.setOwner(user);
    	User result = instance.getOwner();
    	assertEquals(user, result);
    }
}
