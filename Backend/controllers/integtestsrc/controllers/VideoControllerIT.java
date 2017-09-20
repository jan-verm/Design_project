/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import courses.Course;
import courses.Role;
import courses.User;
import databaseaccess.exceptions.ClassicDatabaseException;
import databaseaccess.exceptions.ClassicNotFoundException;
import databaseaccess.exceptions.ClassicUnauthorizedException;
import databaseaccess.implementation.CourseDao;
import databaseaccess.implementation.CourseNotesDao;
import databaseaccess.implementation.LectureDao;
import databaseaccess.implementation.UserDao;
import databaseaccess.implementation.VideoDao;
import databaseaccess.interfaces.ICourseDao;
import databaseaccess.interfaces.ICourseNotesDao;
import databaseaccess.interfaces.ILectureDao;
import databaseaccess.interfaces.IUserDao;
import databaseaccess.interfaces.IVideoDao;
import java.util.List;
import models.Video;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Kevin
 * Test from the controllers to the database
 */
 public class VideoControllerIT {
 	
    private VideoController videoController;
    private final String TEST_DATABASE_CONFIG = "testdbconfig.properties";
    private IVideoDao videodao;
    
    //courses 
    private CourseController courseControler;
    private ICourseDao coursedao;
    private ILectureDao lecturedao;
    private UserController userController;
    private IUserDao userdao;
    private int courseId;
    private int userId;

    @Before
    public void setUp() throws ClassicDatabaseException, ClassicNotFoundException {
        videodao = new VideoDao(TEST_DATABASE_CONFIG);
        ICourseNotesDao notesdao = new CourseNotesDao(TEST_DATABASE_CONFIG);
        
        coursedao = new CourseDao(TEST_DATABASE_CONFIG);
        lecturedao = new LectureDao(TEST_DATABASE_CONFIG);
        userdao = new UserDao(TEST_DATABASE_CONFIG);
        courseControler = new CourseController(coursedao, lecturedao, videodao, notesdao, userdao, "/var/www/classic/resources/courses");
        videoController = new VideoController(videodao, coursedao, userdao);
        userController = new UserController(userdao);
        
        User u = new User("prof", Role.TEACHER,"password");
        userId = userController.addUser(u);
        Course c = new Course("course");
        courseId = courseControler.addCourse(c, userId);
    }
    
    @After
    public void tearDown() throws ClassicDatabaseException {
        videodao.cleanTable();
        coursedao.cleanTable();
        userdao.cleanTable();
    }

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