/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package models;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Juta
 */
public class VideoTest {

    /**
     * Test of getTitle method, of class Video.
     */
    @Test
    public void testGetTitle() {
        String title = "title";
        Video instance = new Video(title, "", 0);
        String result = instance.getTitle();
        assertEquals(title, result);
    }

    /**
     * Test of setTitle method, of class Video.
     */
    @Test
    public void testSetTitle() {
        String title = "title";
        Video instance = new Video("", "", 0);
        instance.setTitle(title);
        String result = instance.getTitle();
        assertEquals(title, result);
    }

    /**
     * Test of getUrl method, of class Video.
     */
    @Test
    public void testGetUrl() {
        String url = "url";
        Video instance = new Video("", "url", 0);
        String result = instance.getUrl();
        assertEquals(url, result);
    }

    /**
     * Test of setUrl method, of class Video.
     */
    @Test
    public void testSetUrl() {
        String url = "url";
        Video instance = new Video("", "", 0);
        instance.setUrl(url);
        String result = instance.getUrl();
        assertEquals(url, result);
    }

    /**
     * Test of getDuration method, of class Video.
     */
    @Test
    public void testGetDuration() {
        int expResult = 20;
        Video instance = new Video("", "", expResult);
        int result = instance.getDuration();
        assertEquals(expResult, result);
    }

    /**
     * Test of setDuration method, of class Video.
     */
    @Test
    public void testSetDuration() {
        int duration = 20;
        Video instance = new Video("", "", 0);
        instance.setDuration(duration);
        int result = instance.getDuration();
        assertEquals(duration, result);
    }

    @Test
    public void testGetId() {
        int id = 5;
        Video instance = new Video("", "", 0);
        instance.setId(id);
        int result = instance.getId();
        assertEquals(id, result);
    }

    @Test
    public void testSetId() {
        int id = 5;
        Video instance = new Video("", "", 0);
        instance.setId(id);
        int result = instance.getId();
        assertEquals(id, result);
    }
}
