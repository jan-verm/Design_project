/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package databaseaccess.implementation;

import courses.Course;
import courses.Lecture;
import courses.Role;
import courses.User;
import databaseaccess.exceptions.ClassicDatabaseException;
import databaseaccess.exceptions.ClassicNotFoundException;
import java.util.List;
import models.Comment;
import models.Video;
import models.VideoReference;
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
public class VideoReferenceDaoTest {

    private VideoReferenceDao videoReferenceDao;
    private VideoDao videoDao;
    private CommentDao commentDao;
    private CourseDao courseDao;
    private LectureDao lectureDao;
    private VideoReferenceDao fakedao;    
    private UserDao userDao;
    
    private User user;
    private Course course;
    private Lecture lecture;
    
    

    public VideoReferenceDaoTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() throws ClassicDatabaseException, ClassicNotFoundException {
        videoReferenceDao = new VideoReferenceDao(VideoReferenceDao.TEST_DATABASE_CONFIG);
        videoDao = new VideoDao(VideoDao.TEST_DATABASE_CONFIG);
        commentDao = new CommentDao(CommentDao.TEST_DATABASE_CONFIG);
        courseDao = new CourseDao(CourseDao.TEST_DATABASE_CONFIG);
        lectureDao = new LectureDao(LectureDao.TEST_DATABASE_CONFIG);
        
        fakedao = new VideoReferenceDao("fakedbconfig.properties");
        userDao = new UserDao(userDao.TEST_DATABASE_CONFIG);
        
        user = new User("testvideorefuser", Role.NONE, "password");
        userDao.addUser(user);
        course = new Course("testvideoref");
        course.setOwner(user);
        courseDao.addCourse(course);
        lecture = new Lecture("testvideoreflecture");
        lectureDao.addLecture(course.getId(), lecture);        
    }

    @After
    public void tearDown() throws ClassicDatabaseException {
        videoReferenceDao.cleanTable();
        videoDao.cleanTable();
        commentDao.cleanTable();
        courseDao.cleanTable();
        userDao.cleanTable();
    }
    
    @Test
    public void testGetCourseAndLectureForVideo() throws Exception {
        Video v = new Video("testAddVideoReference", "testAddVideoReferenceUrl", 200);
        int vid = videoDao.addVideo(course.getId(),v);

        Comment c = new Comment("testAddVideoReferenceBody", false);
        c.setUser(user);
        int cid = commentDao.addComment(c);

        VideoReference refTest = new VideoReference(v, 5);
        int vrid = videoReferenceDao.addVideoReference(vid, cid, refTest);
        
        int[] result = videoReferenceDao.getCourseAndLectureForVideo(vid);
        assertEquals(course.getId(), result[0]);
        assertEquals(0, result[1]);
        
        
        
        Video v2 = new Video("testAddVideoReference", "testAddVideoReferenceUrl", 200);
        int vid2 = videoDao.addVideo(lecture.getId(),v2);

        Comment c2 = new Comment("testAddVideoReferenceBody", false);
        c2.setUser(user);
        int cid2 = commentDao.addComment(c2);

        VideoReference refTest2 = new VideoReference(v2, 5);
        int vrid2 = videoReferenceDao.addVideoReference(vid2, cid2, refTest2);
        
        int[] result2 = videoReferenceDao.getCourseAndLectureForVideo(vid2);
        assertEquals(course.getId(), result2[0]);
        assertEquals(lecture.getId(), result2[1]);
    }
    
    @Test(expected=ClassicDatabaseException.class)
    public void testGetCourseAndLectureForVideoDBException() throws Exception {
        fakedao.getCourseAndLectureForVideo(0);
    }
    
    @Test(expected=ClassicNotFoundException.class)
    public void testGetCourseAndLectureForVideoNFException1() throws Exception { 
        videoReferenceDao.getCourseAndLectureForVideo(0);
    }

    @Test
    public void testAddVideoReference() throws Exception {        
        Video v = new Video("testAddVideoReference", "testAddVideoReferenceUrl", 200);
        int vid = videoDao.addVideo(course.getId(),v);

        Comment c = new Comment("testAddVideoReferenceBody", false);
        c.setUser(user);
        int cid = commentDao.addComment(c);

        VideoReference refTest = new VideoReference(v, 5);
        int vrid = videoReferenceDao.addVideoReference(vid, cid, refTest);
        VideoReference refResult = videoReferenceDao.getVideoReference(vrid);
        
        assertEquals(refResult.getVideo().getId(), vid);
        assertEquals(refResult.getTimestamp(), refTest.getTimestamp());
    }
    
    @Test(expected=ClassicDatabaseException.class)
    public void testAddVideoReferenceDBException() throws Exception {        
        Video v = new Video("testAddVideoReference", "testAddVideoReferenceUrl", 200);
        int vid = videoDao.addVideo(course.getId(),v);

        Comment c = new Comment("testAddVideoReferenceBody", false);
        c.setUser(user);
        int cid = commentDao.addComment(c);

        VideoReference refTest = new VideoReference(v, 5);
        fakedao.addVideoReference(vid, cid, refTest);
    }
    
    @Test(expected=ClassicNotFoundException.class)
    public void testAddVideoReferenceNFException1() throws Exception {        
        Video v = new Video("testAddVideoReference", "testAddVideoReferenceUrl", 200);
        int vid = videoDao.addVideo(course.getId(),v);

        VideoReference refTest = new VideoReference(v, 5);
        int vrid = videoReferenceDao.addVideoReference(vid, 0, refTest);
    }
    
    @Test(expected=ClassicNotFoundException.class)
    public void testAddVideoReferenceNFException2() throws Exception {
        Video v = new Video("testAddVideoReference", "testAddVideoReferenceUrl", 200);
        VideoReference refTest = new VideoReference(v, 5);
        videoReferenceDao.addVideoReference(0, 0, refTest);
    }

    @Test(expected = ClassicNotFoundException.class)
    public void testDeleteVideoReference() throws Exception {        
        Video v = new Video("testDeleteVideoReference", "testDeleteVideoReferenceUrl", 150);
        int vid = videoDao.addVideo(course.getId(), v);

        Comment c = new Comment("testDeleteVideoReferenceBody", false);
        c.setUser(user);
        int cid = commentDao.addComment(c);

        VideoReference refTest = new VideoReference(v, 30);
        int vrid = videoReferenceDao.addVideoReference(vid, cid, refTest);
        VideoReference refResult = videoReferenceDao.getVideoReference(vrid);        
        assertEquals(refResult.getVideo().getId(), vid);
        assertEquals(refResult.getTimestamp(), refTest.getTimestamp());
        
        videoReferenceDao.deleteVideoReference(vrid);
        
        videoReferenceDao.getVideoReference(vrid);        
    }
    
    @Test(expected=ClassicDatabaseException.class)
    public void testDeleteVideoReferenceDBException() throws ClassicDatabaseException, ClassicNotFoundException {
        fakedao.deleteVideoReference(0);
    }
    
    @Test(expected=ClassicNotFoundException.class)
    public void testDeleteVideoReferenceNotFoundException() throws ClassicDatabaseException, ClassicNotFoundException {
        videoReferenceDao.deleteVideoReference(0);
    }

    @Test
    public void testGetVideoReference() throws Exception {        
        Video v = new Video("testGetVideoReference", "testGetVideoReferenceUrl", 200);
        int vid = videoDao.addVideo(course.getId(), v);

        Comment c = new Comment("testGetVideoReferenceBody", false);
        c.setUser(user);
        int cid = commentDao.addComment(c);

        VideoReference refTest = new VideoReference(v, 5);
        int vrid = videoReferenceDao.addVideoReference(vid, cid, refTest);
        VideoReference refResult = videoReferenceDao.getVideoReference(vrid);
        
        assertEquals(refResult.getVideo().getId(), vid);
        assertEquals(refResult.getTimestamp(), refTest.getTimestamp());
    }
    
    @Test(expected=ClassicDatabaseException.class)
    public void testGetVideoReferenceDBException() throws ClassicDatabaseException, ClassicNotFoundException {
        fakedao.getVideoReference(0);
    }
    
    @Test(expected=ClassicNotFoundException.class)
    public void testGetVideoReferenceNotFoundException() throws ClassicDatabaseException, ClassicNotFoundException {
        videoReferenceDao.getVideoReference(0);
    }

    @Test
    public void testGetSelfVideoReferences() throws Exception {        
        Video v = new Video("testGetVideoReference", "testGetVideoReferenceUrl", 200);
        int vid = videoDao.addVideo(course.getId(), v);

        Comment c = new Comment("testGetVideoReferenceBody", false);
        c.setUser(user);
        int cid = commentDao.addComment(c);

        VideoReference refTest = new VideoReference(v, 5);
        int vrid = videoReferenceDao.addVideoReference(vid, cid, refTest);
        List<VideoReference> refResults = videoReferenceDao.getSelfVideoReferences(vid, cid);
        
        assertEquals(refResults.get(0).getVideo().getId(), vid);
        assertEquals(refResults.get(0).getTimestamp(), refTest.getTimestamp());
    }
    
    @Test(expected=ClassicDatabaseException.class)
    public void testGetSelfVideoReferencesDBException() throws ClassicDatabaseException, ClassicNotFoundException {
        fakedao.getSelfVideoReferences(0,0);
    }
    
    @Test(expected=ClassicNotFoundException.class)
    public void testGetSelfVideoReferencesNFException1() throws Exception {
        Video v = new Video("testGetVideoReference", "testGetVideoReferenceUrl", 200);
        int vid = videoDao.addVideo(course.getId(), v);
        Comment c = new Comment("testGetVideoReferenceBody", false);
        c.setUser(user);
        int cid = commentDao.addComment(c);
        VideoReference refTest = new VideoReference(v, 5);
        int vrid = videoReferenceDao.addVideoReference(vid, cid, refTest);
        videoReferenceDao.getSelfVideoReferences(0, cid);
    }
    
    @Test(expected=ClassicNotFoundException.class)
    public void testGetSelfVideoReferencesNFException2() throws Exception {
        Video v = new Video("testGetVideoReference", "testGetVideoReferenceUrl", 200);
        int vid = videoDao.addVideo(course.getId(), v);

        Comment c = new Comment("testGetVideoReferenceBody", false);
        c.setUser(user);
        int cid = commentDao.addComment(c);

        VideoReference refTest = new VideoReference(v, 5);
        int vrid = videoReferenceDao.addVideoReference(vid, cid, refTest);
        videoReferenceDao.getSelfVideoReferences(vid, 0);
    }
    
    @Test
    public void testGetVideoReferences() throws Exception {
        Video v = new Video("testGetVideoReferences", "testGetVideoReferencesUrl", 200);
        int vid = videoDao.addVideo(course.getId(), v);

        Comment c = new Comment("testGetVideoReferencesBody", false);
        c.setUser(user);
        int cid = commentDao.addComment(c);

        VideoReference refTest = new VideoReference(v, 5);
        videoReferenceDao.addVideoReference(vid, cid, refTest);
        List<VideoReference> refResults = videoReferenceDao.getVideoReferences(cid);
        
        assertEquals(refResults.get(0).getVideo().getId(), vid);
        assertEquals(refResults.get(0).getTimestamp(), refTest.getTimestamp());
    }
    
    @Test(expected=ClassicDatabaseException.class)
    public void testGetVideoReferencesDBException() throws ClassicDatabaseException, ClassicNotFoundException {
        fakedao.getVideoReferences(0);
    }
    
    @Test(expected=ClassicNotFoundException.class)
    public void testGetVideoReferencesNotFoundException() throws ClassicDatabaseException, ClassicNotFoundException {
        videoReferenceDao.getVideoReferences(0);
    }

    @Test
    public void testIsVideoReference() throws Exception {
        Video v = new Video("testGetVideoReferences", "testGetVideoReferencesUrl", 200);
        int vid = videoDao.addVideo(course.getId(), v);

        Comment c = new Comment("testGetVideoReferencesBody", false);
        c.setUser(user);
        int cid = commentDao.addComment(c);

        VideoReference refTest = new VideoReference(v, 5);
        int refID = videoReferenceDao.addVideoReference(vid, cid, refTest);
        boolean result = videoReferenceDao.isVideoReference(refID);
        assertTrue(result);
    }

    @Test(expected = ClassicDatabaseException.class)
    public void testIsVideoReferenceDBException() throws ClassicDatabaseException, ClassicNotFoundException {
        fakedao.isVideoReference(0);
    }
    
    @Test
    public void testUpdateVideoReference() throws Exception {
        Video v = new Video("testUpdateVideoReference", "testUpdateVideoReferenceUrl", 200);
        int vid = videoDao.addVideo(course.getId(),v);

        Comment c = new Comment("testUpdateVideoReferenceBody", false);
        c.setUser(user);
        int cid = commentDao.addComment(c);

        VideoReference refTest = new VideoReference(v, 100);
        int vrid = videoReferenceDao.addVideoReference(vid, cid, refTest);        
        VideoReference refResult = videoReferenceDao.getVideoReference(vrid);        
        assertEquals(refResult.getVideo().getId(), vid);
        assertEquals(refResult.getTimestamp(), refTest.getTimestamp());
       
        refTest.setRefId(vrid);
        refTest.setTimestamp(50);
        videoReferenceDao.updateVideoReference(refTest);
        VideoReference updateResult = videoReferenceDao.getVideoReference(vrid);        
        assertEquals(updateResult.getVideo().getId(), vid);
        assertEquals(updateResult.getTimestamp(), refTest.getTimestamp());
    }
    
    @Test(expected=ClassicDatabaseException.class)
    public void testUpdateVideoReferenceDBException() throws ClassicDatabaseException, ClassicNotFoundException {
        Video v = new Video("testAddVideoReference", "testAddVideoReferenceUrl", 200);
        VideoReference videoRef = new VideoReference(v, 5);
        fakedao.updateVideoReference(videoRef);
    }
    
    @Test(expected=ClassicNotFoundException.class)
    public void testUpdateVideoReferenceNotFoundException() throws ClassicDatabaseException, ClassicNotFoundException {
        Video v = new Video("testAddVideoReference", "testAddVideoReferenceUrl", 200);
        VideoReference videoRef = new VideoReference(v, 5);
        videoReferenceDao.updateVideoReference(videoRef);
    }

    @Test(expected = ClassicNotFoundException.class)
    public void testCleanTable() throws Exception {
        Video v = new Video("testUpdateVideoReference", "testUpdateVideoReferenceUrl", 200);
        int vid = videoDao.addVideo(course.getId(),v);

        Comment c = new Comment("testUpdateVideoReferenceBody", false);
        c.setUser(user);
        int cid = commentDao.addComment(c);

        VideoReference refTest = new VideoReference(v, 100);
        int vrid = videoReferenceDao.addVideoReference(vid, cid, refTest);        
        VideoReference refResult = videoReferenceDao.getVideoReference(vrid);         
        assertEquals(refResult.getVideo().getId(), vid);
        assertEquals(refResult.getTimestamp(), refTest.getTimestamp());
        
        videoReferenceDao.cleanTable();
        
        VideoReference videoReference = videoReferenceDao.getVideoReference(vrid); 
    }
    
    @Test(expected=ClassicDatabaseException.class)
    public void testCleanTableDBException() throws ClassicDatabaseException, ClassicNotFoundException {
        fakedao.cleanTable();
    }
}
