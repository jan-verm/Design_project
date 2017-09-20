/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package models;

import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Juta
 */
public class CourseNotesReferenceTest {

    /**
     * Test of getCourseNotes method, of class CourseNotesReference.
     */
    @Test
    public void testGetCourseNotes() {
        CourseNotes expResult = new CourseNotes("title", "url");
        Location loc = new Location(1, 2, 3, 4, 5);
        List<Location> locs = new ArrayList<>();
        locs.add(loc);
        CourseNotesReference instance = new CourseNotesReference(expResult, locs);
        
        CourseNotes result = instance.getCourseNotes();
        assertEquals(expResult, result);
    }  
    
    @Test
    public void testGetLocations() {
        CourseNotes cn = new CourseNotes("title", "url");
        Location expResult = new Location(1, 2, 3, 4, 5);
        List<Location> locs = new ArrayList<>();
        locs.add(expResult);
        
        CourseNotesReference instance = new CourseNotesReference(cn, locs);
        List<Location> result = instance.getLocations();
        
        assertEquals(expResult.getPagenumber(), result.get(0).getPagenumber());
        assertEquals(expResult.getX1(), result.get(0).getX1(),4);
        assertEquals(expResult.getX2(), result.get(0).getX2(),4);
        assertEquals(expResult.getY1(), result.get(0).getY1(),4);
        assertEquals(expResult.getY2(), result.get(0).getY2(),4);
    }
    
    @Test
    public void testSetLocation() {
        CourseNotes cn = new CourseNotes("title", "url");
        Location loc = new Location(1, 2, 3, 4, 5);
        List<Location> locs = new ArrayList<>();
        locs.add(loc);
        CourseNotesReference instance = new CourseNotesReference(cn, locs);

        Location expResult = new Location(2, 3, 4, 5, 6);
        locs = new ArrayList<>();
        locs.add(expResult);
        instance.setLocations(locs);
        List<Location> result = instance.getLocations();
        
        assertEquals(expResult.getPagenumber(), result.get(0).getPagenumber());
        assertEquals(expResult.getPagenumber(), result.get(0).getPagenumber());
        assertEquals(expResult.getX1(), result.get(0).getX1(),4);
        assertEquals(expResult.getX2(), result.get(0).getX2(),4);
        assertEquals(expResult.getY1(), result.get(0).getY1(),4);
        assertEquals(expResult.getY2(), result.get(0).getY2(),4);
    }
    
    @Test
    public void testGetAndSetId() {
        int id = 5;
        CourseNotes courseNotes = new CourseNotes("", "");
        Location loc = new Location(1, 2, 3, 4, 5);
        List<Location> locs = new ArrayList<>();
        locs.add(loc);
        CourseNotesReference instance = new CourseNotesReference(courseNotes, locs);
        
        instance.setRefId(id);
        int result = instance.getRefId();
        assertEquals(id, result);
    }
    
    @Test
    public void testVisible() {
    	int id = 5;
        CourseNotes courseNotes = new CourseNotes("", "");
        Location loc = new Location(1, 2, 3, 4, 5);
        List<Location> locs = new ArrayList<>();
        locs.add(loc);
        CourseNotesReference instance = new CourseNotesReference(courseNotes, locs);
        
        instance.setVisible(true);
        boolean result = instance.isVisible();
        assertEquals(true, result);
    }
}
