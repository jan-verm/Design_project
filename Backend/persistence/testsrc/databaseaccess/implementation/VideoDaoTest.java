/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package databaseaccess.implementation;

import courses.Course;
import courses.Role;
import courses.User;
import databaseaccess.exceptions.ClassicDatabaseException;
import databaseaccess.exceptions.ClassicNotFoundException;
import java.util.List;
import models.Video;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author jorsi
 */
public class VideoDaoTest {

    private VideoDao videoDao;
    private VideoDao fakedao;
    private CourseDao courseDao;
    private UserDao userDao;
    
    private User user;
    private Course course;
    
    public VideoDaoTest() {
    }

    @BeforeClass
    public static void setUpClass() {

    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() throws ClassicDatabaseException {       
        videoDao = new VideoDao(VideoDao.TEST_DATABASE_CONFIG);
        fakedao = new VideoDao("fakedbconfig.properties");
        courseDao = new CourseDao(CourseDao.TEST_DATABASE_CONFIG);
        userDao = new UserDao(UserDao.TEST_DATABASE_CONFIG);
        
        user = new User("testlectureuser", Role.NONE, "password");
        userDao.addUser(user);
        course = new Course("testlecture");
        course.setOwner(user);
        courseDao.addCourse(course);
    }

    @After
    public void tearDown() throws ClassicDatabaseException {
        videoDao.cleanTable();
        courseDao.cleanTable();
        userDao.cleanTable();
    }

    @Test
    public void testAddVideo() throws Exception {        
        Video expResult = new Video("testAddVideo", "testAddVideoUrl", 100);
        int expResultID = videoDao.addVideo(course.getId(), expResult);
        Video result = videoDao.getVideo(expResultID);
        assertEquals(expResultID, result.getId());
        assertEquals(expResult.getTitle(), result.getTitle());
        assertEquals(expResult.getUrl(), result.getUrl());
        assertEquals(expResult.getDuration(), result.getDuration());
    }
    
    @Test(expected=ClassicDatabaseException.class)
    public void testAddVideoDBException() throws Exception {        
        Video expResult = new Video("testAddVideo", "testAddVideoUrl", 100);
        fakedao.addVideo(0,expResult);
    }
    
    @Test(expected=ClassicNotFoundException.class)
    public void testAddVideoNFException() throws Exception {        
        Video expResult = new Video("testAddVideo", "testAddVideoUrl", 100);
        videoDao.addVideo(0,expResult);
    }

    @Test(expected = ClassicNotFoundException.class)
    public void testDeleteVideo() throws Exception {        
        Video expResult = new Video("testAddVideo", "testAddVideoUrl", 100);
        int expResultID = videoDao.addVideo(course.getId(), expResult);
        Video result = videoDao.getVideo(expResultID);
        assertEquals(expResultID, result.getId());
        assertEquals(expResult.getTitle(), result.getTitle());
        assertEquals(expResult.getUrl(), result.getUrl());
        assertEquals(expResult.getDuration(), result.getDuration());
        videoDao.deleteVideo(expResultID);
        videoDao.getVideo(expResultID);
    }
    
    @Test(expected=ClassicDatabaseException.class)
    public void testDeleteVideoDBException() throws ClassicDatabaseException, ClassicNotFoundException {
        fakedao.deleteVideo(0);
    }
    
    @Test(expected=ClassicNotFoundException.class)
    public void testDeleteVideoNotFoundException() throws ClassicDatabaseException, ClassicNotFoundException {
        videoDao.deleteVideo(0);
    }

    @Test
    public void testGetVideo() throws Exception {        
        Video expResult = new Video("testGetVideo", "testGetVideoUrl", 100);
        int expResultID = videoDao.addVideo(course.getId(), expResult);
        Video result = videoDao.getVideo(expResultID);
        assertEquals(expResultID, result.getId());
        assertEquals(expResult.getTitle(), result.getTitle());
        assertEquals(expResult.getUrl(), result.getUrl());
        assertEquals(expResult.getDuration(), result.getDuration());
    }
    
    @Test(expected=ClassicDatabaseException.class)
    public void testGetVideoDBException() throws ClassicDatabaseException, ClassicNotFoundException {
        fakedao.getVideo(0);
    }

    @Test
    public void testGetVideos() throws Exception {        
        Video expResult = new Video("testGetVideos", "testGetVideosUrl", 100);
        int expResultID = videoDao.addVideo(course.getId(), expResult);
        List<Video> results = videoDao.getVideos();
        Video result = null;
        for (Video r : results) {
            if (r.getId() == expResultID) {
                result = r;
            }
        }
        assertEquals(expResultID, result.getId());
        assertEquals(expResult.getTitle(), result.getTitle());
        assertEquals(expResult.getUrl(), result.getUrl());
        assertEquals(expResult.getDuration(), result.getDuration());
    }
    
    
    @Test(expected=ClassicDatabaseException.class)
    public void testGetVideosDBException() throws ClassicDatabaseException, ClassicNotFoundException {
        fakedao.getVideos();
    }

    @Test
    public void testGetVideos_int() throws Exception {        
        Video expResult = new Video("testGetVideos", "testGetVideosUrl", 100);
        int expResultID = videoDao.addVideo(course.getId(), expResult);
        List<Video> results = videoDao.getVideos(course.getId());
        Video result = null;
        for (Video r : results) {
            if (r.getId() == expResultID) {
                result = r;
            }
        }
        assertEquals(expResultID, result.getId());
        assertEquals(expResult.getTitle(), result.getTitle());
        assertEquals(expResult.getUrl(), result.getUrl());
        assertEquals(expResult.getDuration(), result.getDuration());
    }
    
    @Test(expected=ClassicDatabaseException.class)
    public void testGetVideos_intDBException() throws ClassicDatabaseException, ClassicNotFoundException {
        fakedao.getVideos(0);
    }
    
    @Test(expected=ClassicNotFoundException.class)
    public void testGetVideos_intNFException() throws ClassicDatabaseException, ClassicNotFoundException {
        videoDao.getVideos(0);
    }    
    
    @Test
    public void testIsVideo() throws Exception {        
        Video expResult = new Video("testGetVideo", "testGetVideoUrl", 100);
        int expResultID = videoDao.addVideo(course.getId(), expResult);        
        boolean result = videoDao.isVideo(expResultID);
        assertTrue(result);
    }

    @Test(expected = ClassicDatabaseException.class)
    public void testIsVideoDBException() throws ClassicDatabaseException, ClassicNotFoundException {
        fakedao.isVideo(0);
    }
    
    @Test
    public void testUpdateVideo() throws Exception {        
        Video expResult = new Video("testGetVideo", "testGetVideoUrl", 100);
        int expResultID = videoDao.addVideo(course.getId(), expResult);
        Video result = videoDao.getVideo(expResultID);
        assertEquals(expResultID, result.getId());
        assertEquals(expResult.getTitle(), result.getTitle());
        assertEquals(expResult.getUrl(), result.getUrl());
        assertEquals(expResult.getDuration(), result.getDuration());
        expResult.setId(expResultID);
        expResult.setTitle("testUpdateVideo");
        expResult.setUrl("testUpdateVideoUrl");
        expResult.setDuration(200);
        videoDao.updateVideo(expResult);
        Video result2 = videoDao.getVideo(expResultID);
        assertEquals(expResultID, result2.getId());
        assertEquals(expResult.getTitle(), result2.getTitle());
        assertEquals(expResult.getUrl(), result2.getUrl());
        assertEquals(expResult.getDuration(), result2.getDuration());
    }
    
    @Test(expected=ClassicDatabaseException.class)
    public void testUpdateVideoDBException() throws ClassicDatabaseException, ClassicNotFoundException {
        Video video = new Video("title", "url", 20);
        fakedao.updateVideo(video);
    }
    
    @Test(expected=ClassicNotFoundException.class)
    public void testUpdateVideoNotFoundException() throws ClassicDatabaseException, ClassicNotFoundException {
        Video video = new Video("title", "url", 20);
        videoDao.updateVideo(video);
    }

    @Test(expected = ClassicNotFoundException.class)
    public void testCleanTable() throws Exception {        
        Video expResult = new Video("testGetVideos", "testGetVideosUrl", 100);
        int expResultID = videoDao.addVideo(course.getId(),expResult);
        videoDao.cleanTable();
        videoDao.getVideo(expResultID);
    }
    
    @Test(expected=ClassicDatabaseException.class)
    public void testCleanTableDBException() throws ClassicDatabaseException, ClassicNotFoundException {
        fakedao.cleanTable();
    }
    
    @Test
    public void testGetOwner() throws Exception {        
        Video v = new Video("testAddVideo", "testAddVideoUrl", 100);        
        int vid = videoDao.addVideo(course.getId(), v);        
        
        User result = videoDao.getOwner(vid);
        
        assertEquals(user.getId(), result.getId());
        assertEquals(user.getPassword(), result.getPassword());
        assertEquals(user.getRole(), result.getRole());
        assertEquals(user.getUsername(), result.getUsername()); 
    }

     @Test(expected=ClassicDatabaseException.class)
    public void testGetOwnerDBException() throws ClassicDatabaseException, ClassicNotFoundException {
        fakedao.getOwner(0);
    }
    
    @Test(expected=ClassicNotFoundException.class)
    public void testGetOwnerNFException() throws ClassicDatabaseException, ClassicNotFoundException {
        videoDao.getOwner(0);
    }
}
