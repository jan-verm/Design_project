/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import courses.Role;
import courses.User;
import dao.CoursesMockUpDAO;
import dao.MockUpDAO;
import databaseaccess.exceptions.ClassicDatabaseException;
import databaseaccess.exceptions.ClassicNotFoundException;
import databaseaccess.exceptions.ClassicUnauthorizedException;
import java.util.List;
import models.Video;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Juta
 */
public class VideoControllerTest {
    
    private VideoController videoController;
    
    @Before
    public void setUp() throws ClassicDatabaseException {
        MockUpDAO mockup = new MockUpDAO();
        CoursesMockUpDAO coursesMockup = new CoursesMockUpDAO();
        videoController = new VideoController(mockup, coursesMockup, coursesMockup);
        userId = coursesMockup.addUser(new User("username", Role.ADMIN, "password"));
    }
    
    private int userId;
    private int courseId = 1;

    /**
     * Test of getVideos method, of class VideoController.
     * @throws databaseaccess.exceptions.ClassicDatabaseException
     */
    @Test
    public void testGetVideos() throws ClassicDatabaseException, ClassicNotFoundException, ClassicUnauthorizedException {
        Video video = new Video("title", "url", 20);
        int videoId = videoController.addVideo(courseId, video, userId);
        List<Video> result = videoController.getAllVideos(courseId);
        Video resultVideo = null;
        for (Video v : result) {
            if(v.getId() == videoId) resultVideo = v;
        }
        
        assertEquals(video.getTitle(), resultVideo.getTitle());
        assertEquals(video.getUrl(), resultVideo.getUrl());
        assertEquals(video.getDuration(), resultVideo.getDuration());
        assertEquals(videoId, resultVideo.getId());   
    }
    
    /**
     * Test of getVideos method, of class VideoController.
     * @throws databaseaccess.exceptions.ClassicDatabaseException
     */
    @Test
    public void testGetVideosIsEmpty() throws ClassicDatabaseException, ClassicNotFoundException {
        List<Video> result = videoController.getAllVideos(courseId);
        assertEquals(true, result.isEmpty());
    }

    /**
     * Test of getVideo method, of class VideoController.
     * @throws databaseaccess.exceptions.ClassicDatabaseException
     * @throws databaseaccess.exceptions.ClassicNotFoundException
     */
    @Test
    public void testGetVideo() throws ClassicDatabaseException, ClassicNotFoundException, ClassicUnauthorizedException {
        Video video = new Video("title", "url", 20);
        int videoId = videoController.addVideo(courseId, video, userId);
        Video result = videoController.getVideo(videoId);
        
        assertEquals(video.getTitle(), result.getTitle());
        assertEquals(video.getUrl(), result.getUrl());
        assertEquals(video.getDuration(), result.getDuration());
        assertEquals(videoId, result.getId());
    }
    
    /**
     * Test of getVideo method, of class VideoController.
     * @throws databaseaccess.exceptions.ClassicDatabaseException
     * @throws databaseaccess.exceptions.ClassicNotFoundException
     */
    @Test(expected=ClassicNotFoundException.class)
    public void testGetVideoNotFoundException() throws ClassicDatabaseException, ClassicNotFoundException {
        videoController.getVideo(0);
    }

    /**
     * Test of addVideo method, of class VideoController.
     * @throws databaseaccess.exceptions.ClassicDatabaseException
     * @throws databaseaccess.exceptions.ClassicNotFoundException
     */
    @Test
    public void testAddVideo() throws ClassicDatabaseException, ClassicNotFoundException, ClassicUnauthorizedException {
        Video video = new Video("title", "url", 20);
        int videoId = videoController.addVideo(courseId, video, userId);
        int expId = videoController.getVideo(videoId).getId();
        assertEquals(expId, videoId);
    }
    
    /**
     * Test of updateVideo method, of class VideoController.
     * @throws databaseaccess.exceptions.ClassicDatabaseException
     * @throws databaseaccess.exceptions.ClassicNotFoundException
     */
    @Test
    public void testupdateVideo() throws ClassicDatabaseException, ClassicNotFoundException, ClassicUnauthorizedException {
        Video video = new Video("title", "url", 20);
        int videoId = videoController.addVideo(courseId, video, userId);
        video = new Video("title2", "url2", 20);
        videoController.updateVideo(videoId, video, userId);
        Video result = videoController.getVideo(videoId);
        
        assertEquals(video.getTitle(), result.getTitle());
        assertEquals(video.getUrl(), result.getUrl());
        assertEquals(video.getDuration(), result.getDuration());
        assertEquals(videoId, result.getId());
    }
    
    /**
     * Test of updateVideo method, of class VideoController.
     * @throws databaseaccess.exceptions.ClassicDatabaseException
     * @throws databaseaccess.exceptions.ClassicNotFoundException
     */
    @Test(expected=ClassicNotFoundException.class)
    public void testupdateVideoNotFoundException() throws ClassicDatabaseException, ClassicNotFoundException, ClassicUnauthorizedException {
        Video video = new Video("title", "url", 20);
        videoController.addVideo(courseId, video, userId);
        video = new Video("title2", "url2", 20);
        
        videoController.updateVideo(0, video, userId);
    }
    
    /**
     * Test of deleteVideo method, of class VideoController.
     * @throws databaseaccess.exceptions.ClassicDatabaseException
     * @throws databaseaccess.exceptions.ClassicNotFoundException
     */
    @Test(expected=ClassicNotFoundException.class)
    public void testdeleteVideo() throws ClassicDatabaseException, ClassicNotFoundException, ClassicUnauthorizedException {
        Video video = new Video("title", "url", 20);
        int videoId = videoController.addVideo(courseId, video, userId);
        videoController.deleteVideo(videoId, userId);
        
        videoController.getVideo(videoId);
    }
    
    /**
     * Test of deleteVideo method, of class VideoController.
     * @throws databaseaccess.exceptions.ClassicDatabaseException
     * @throws databaseaccess.exceptions.ClassicNotFoundException
     */
    @Test(expected=ClassicNotFoundException.class)
    public void testdeleteVideoNotFoundException() throws ClassicDatabaseException, ClassicNotFoundException, ClassicUnauthorizedException {
        videoController.deleteVideo(0, 1);
    }
}
