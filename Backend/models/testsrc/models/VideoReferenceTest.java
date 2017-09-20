/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package models;

import org.junit.Test;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Juta
 */
public class VideoReferenceTest {

    /**
     * Test of getVideo method, of class VideoReference.
     */
    @Test
    public void testGetVideo() {
        Video expResult = new Video("title", "url", 20);
        VideoReference instance = new VideoReference(expResult, 0);
        Video result = instance.getVideo();
        assertEquals(expResult, result);
    }

    /**
     * Test of getTimestamp method, of class VideoReference.
     */
    @Test
    public void testGetTimestamp() {
        Video v = new Video("title", "url", 20);
        int expResult = 20;
        VideoReference instance = new VideoReference(v, expResult);
        int result = instance.getTimestamp();
        assertEquals(expResult, result);
    }
    
    @Test
    public void testSetTimestamp() {
        Video v = new Video("title", "url", 20);
        int expResult = 20;
        VideoReference instance = new VideoReference(v, 0);
        instance.setTimestamp(expResult);
        int result = instance.getTimestamp();
        assertEquals(expResult, result);
    }
    
    @Test
    public void testGetId() {
        int id = 5;
        Video video = new Video("", "", 20);
        VideoReference instance = new VideoReference(video, 5);
        instance.setRefId(id);
        int result = instance.getRefId();
        assertEquals(id, result);
    }
    
    @Test
    public void testSetId() {
        int id = 5;
        Video video = new Video("", "", 20);
        VideoReference instance = new VideoReference(video, 5);
        instance.setRefId(id);
        int result = instance.getRefId();
        assertEquals(id, result);
    }
    
    @Test
    public void testVisible() {
    	int id = 5;
        Video video = new Video("", "", 20);
        VideoReference instance = new VideoReference(video, 5);
        
        instance.setVisible(true);
        boolean result = instance.isVisible();
        assertEquals(true, result);
    }
}
