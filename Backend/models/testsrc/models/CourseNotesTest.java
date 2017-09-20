/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package models;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Juta
 */
public class CourseNotesTest {

    /**
     * Test of getTitle method, of class CourseNotes.
     */
    @Test
    public void testGetTitle() {
        String expResult = "title";
        CourseNotes instance = new CourseNotes(expResult, "");
        String result = instance.getTitle();
        assertEquals(expResult, result);
    }

    /**
     * Test of setTitle method, of class CourseNotes.
     */
    @Test
    public void testSetTitle() {
        String title = "title";
        CourseNotes instance = new CourseNotes("", "");
        instance.setTitle(title);
        String result = instance.getTitle();
        assertEquals(title, result);
    }

    /**
     * Test of getUrl method, of class CourseNotes.
     */
    @Test
    public void testGetUrl() {
        String expResult = "url";
        CourseNotes instance = new CourseNotes("", expResult);
        String result = instance.getUrl();
        assertEquals(expResult, result);
    }

    /**
     * Test of setUrl method, of class CourseNotes.
     */
    @Test
    public void testSetUrl() {
        String url = "url";
        CourseNotes instance = new CourseNotes("", "");
        instance.setUrl(url);
        String result = instance.getUrl();
        assertEquals(url, result);
    }

    @Test
    public void testGetId() {
        int id = 5;
        CourseNotes instance = new CourseNotes("", "");
        instance.setId(id);
        int result = instance.getId();
        assertEquals(id, result);
    }

    @Test
    public void testSetId() {
        int id = 5;
        CourseNotes instance = new CourseNotes("", "");
        instance.setId(id);
        int result = instance.getId();
        assertEquals(id, result);
    }

}
