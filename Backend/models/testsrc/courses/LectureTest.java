/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package courses;

import java.util.ArrayList;
import java.util.List;
import models.CourseNotes;
import models.Video;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Juta
 */
public class LectureTest {

    /**
     * Test of getVideos  and setVideos method, of class Lecture.
     */
    @Test
    public void testGetAndSetVideos() {
        Lecture instance = new Lecture("lecture");
        Video video = new Video("title", "url", 99);
        List<Video> expResult = new ArrayList<>();
        expResult.add(video);
        instance.setVideos(expResult);
        
        List<Video> result = instance.getVideos();
        assertEquals(expResult, result);
    }

    /**
     * Test of addVideo method, of class Lecture.
     */
    @Test
    public void testAddVideo() {
        Lecture instance = new Lecture("lecture");
        Video video = new Video("title", "url", 99);
        instance.addVideo(video);
        List<Video> expResult = new ArrayList<>();
        expResult.add(video);
        
        List<Video> result = instance.getVideos();
        assertEquals(expResult, result);
    }

    /**
     * Test of getCourseNotes and setCourseNotes method, of class Lecture.
     */
    @Test
    public void testGetAndSetCourseNotes() {
        Lecture instance = new Lecture("lecture");
        CourseNotes cn = new CourseNotes("title", "url");
        List<CourseNotes> expResult = new ArrayList<>();
        expResult.add(cn);
        instance.setCourseNotes(expResult);
        
        List<CourseNotes> result = instance.getCourseNotes();
        assertEquals(expResult, result);
    }

    /**
     * Test of addCourseNotes method, of class Lecture.
     */
    @Test
    public void testAddCourseNotes() {
        Lecture instance = new Lecture("lecture");
        CourseNotes cn = new CourseNotes("title", "url");
        List<CourseNotes> expResult = new ArrayList<>();
        expResult.add(cn);
        instance.addCourseNotes(cn);
        
        List<CourseNotes> result = instance.getCourseNotes();
        assertEquals(expResult, result);
    }

    /**
     * Test of getName method, of class Lecture.
     */
    @Test
    public void testGetName() {
        Lecture instance = new Lecture("lecture");
        String result = instance.getName();
        assertEquals("lecture", result);
    }

    /**
     * Test of setName method, of class Lecture.
     */
    @Test
    public void testSetName() {
        Lecture instance = new Lecture("lecture");
        instance.setName("new_name");
        String result = instance.getName();
        assertEquals("new_name", result);
    }

    /**
     * Test of getId and setId method, of class Lecture.
     */
    @Test
    public void testGetAndSetId() {
        Lecture instance = new Lecture("lecture");
        instance.setId(1);
        int result = instance.getId();
        assertEquals(1, result);
    }  
}
