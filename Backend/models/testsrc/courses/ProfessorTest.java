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
public class ProfessorTest {

    /**
     * Test of getManagedCourses and setManagedCourses method, of class Professor.
     */
    @Test
    public void testGetAndSetManagedCourses() {
        Professor instance = new Professor("prof","password");
        List<Course> expResult = new ArrayList<>();
        Course course = new Course("course");
        expResult.add(course);
        instance.setManagedCourses(expResult);
        
        List<Course> result = instance.getManagedCourses();
        assertEquals(expResult, result);
    }

    /**
     * Test of setManagedCourses method, of class Professor.
     */
    @Test
    public void testSetManagedCourses() {
        Professor instance = new Professor("prof","password");
        List<Course> expResult = new ArrayList<>();
        Course course = new Course("course");
        expResult.add(course);
        instance.addManagedCourse(course);
        
        List<Course> result = instance.getManagedCourses();
        assertEquals(expResult, result);
    }
}
