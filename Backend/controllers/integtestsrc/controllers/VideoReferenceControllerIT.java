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
import databaseaccess.implementation.CommentDao;
import databaseaccess.implementation.CourseDao;
import databaseaccess.implementation.CourseNotesDao;
import databaseaccess.implementation.CourseNotesReferenceDao;
import databaseaccess.implementation.LectureDao;
import databaseaccess.implementation.ReplyDao;
import databaseaccess.implementation.UserDao;
import databaseaccess.implementation.VideoDao;
import databaseaccess.implementation.VideoReferenceDao;
import databaseaccess.interfaces.ICommentDao;
import databaseaccess.interfaces.ICourseDao;
import databaseaccess.interfaces.ICourseNotesDao;
import databaseaccess.interfaces.ICourseNotesReferenceDao;
import databaseaccess.interfaces.ILectureDao;
import databaseaccess.interfaces.IReplyDao;
import databaseaccess.interfaces.IUserDao;
import databaseaccess.interfaces.IVideoDao;
import databaseaccess.interfaces.IVideoReferenceDao;
import java.util.ArrayList;
import java.util.List;
import models.Comment;
import models.CourseNotes;
import models.CourseNotesReference;
import models.Location;
import models.Video;
import models.VideoReference;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Juta
 */
public class VideoReferenceControllerIT {
    
    private VideoController videoController;
    private CommentController commentController;
    private VideoReferenceController videoReferenceController;
    private final String TEST_DATABASE_CONFIG = "testdbconfig.properties";
    private IVideoDao videodao;
    private ICommentDao commentdao;
    private CourseNotesController courseNotesController;
    private ICourseNotesDao notesdao;
    
    //courses 
    private CourseController courseControler;
    private ICourseDao coursedao;
    private ILectureDao lecturedao;
    private UserController userController;
    private IUserDao userdao;
    private ICourseNotesReferenceDao coursenotesrefdao;
    private IVideoReferenceDao videorefdao;
    private int courseId = 0;
    private int userId;

    @Before
    public void setUp() throws ClassicDatabaseException, ClassicNotFoundException {
        commentdao = new CommentDao(TEST_DATABASE_CONFIG);
        videodao = new VideoDao(TEST_DATABASE_CONFIG);
        notesdao = new CourseNotesDao(TEST_DATABASE_CONFIG);
        videorefdao = new VideoReferenceDao(TEST_DATABASE_CONFIG);
        coursenotesrefdao = new CourseNotesReferenceDao(TEST_DATABASE_CONFIG);
        IReplyDao replydao = new ReplyDao(TEST_DATABASE_CONFIG);
        coursedao = new CourseDao(TEST_DATABASE_CONFIG);
        lecturedao = new LectureDao(TEST_DATABASE_CONFIG);
        userdao = new UserDao(TEST_DATABASE_CONFIG);
        
        courseControler = new CourseController(coursedao, lecturedao, videodao, notesdao, userdao, "/var/www/classic/resources/courses");
        userController = new UserController(userdao);
        
        videoReferenceController = new VideoReferenceController(videorefdao, commentdao, userdao);
        videoController = new VideoController(videodao, coursedao, userdao);
        commentController = new CommentController(commentdao, replydao, videorefdao, coursenotesrefdao, userdao);
        courseNotesController = new CourseNotesController(notesdao, coursedao, userdao, "/var/www/classic/resources/courses");
        
        User u = new User("prof", Role.TEACHER,"password");
        userId = userController.addUser(u);
        
        Course c = new Course("course");
        courseId = courseControler.addCourse(c, userId);
    }
    
    @After
    public void tearDown() throws ClassicDatabaseException {
        videodao.cleanTable();
        commentdao.cleanTable();
        notesdao.cleanTable();
        coursedao.cleanTable();
        userdao.cleanTable();
    }

    /**
     * Test of addVideoReference method, of class VideoReferenceController.
     * @throws databaseaccess.exceptions.ClassicDatabaseException
     * @throws databaseaccess.exceptions.ClassicNotFoundException
     */
    @Test
    public void testAddVideoReference() throws ClassicDatabaseException, ClassicNotFoundException, ClassicUnauthorizedException{
        Video cn = new Video("title", "url", 999);
        int cnId = videoController.addVideo(courseId, cn, userId);
        Comment comment = new Comment("body", false);
        VideoReference expResult = new VideoReference(cn, 20);
        
        
        List<VideoReference> refs = new ArrayList<>();
        refs.add(expResult);
        int commentId = commentController.addVideoComment(cnId, comment, refs, userId);
        
        VideoReference expResult2 = new VideoReference(cn, 10);
        videoReferenceController.addVideoReference(cnId, commentId, expResult2, userId);
        
        List<VideoReference> result = videoReferenceController.getSelfVideoReferences(cnId, commentId);
        
        assertEquals(cnId, result.get(0).getVideo().getId());
        assertEquals(cnId, result.get(1).getVideo().getId());
    }
    
    /**
     * Test of getParents method, of class VideoReferenceController.
     * @throws databaseaccess.exceptions.ClassicDatabaseException
     * @throws databaseaccess.exceptions.ClassicNotFoundException
     */
    @Test
    public void testGetParents() throws ClassicDatabaseException, ClassicNotFoundException, ClassicUnauthorizedException{
        Video cn = new Video("title", "url", 999);
        int cnId = videoController.addVideo(courseId, cn, userId);
        Comment comment = new Comment("body", false);
        VideoReference expResult = new VideoReference(cn, 20);
        
        
        List<VideoReference> refs = new ArrayList<>();
        refs.add(expResult);
        int commentId = commentController.addVideoComment(cnId, comment, refs, userId);
        
        VideoReference expResult2 = new VideoReference(cn, 10);
        videoReferenceController.addVideoReference(cnId, commentId, expResult2, userId);
        
        int[] result = videoReferenceController.getParents(cnId);
        
        assertEquals(courseId, result[0]);
        assertEquals(0, result[1]);
    }
    
    /**
     * Test of addVideoReference method, of class VideoReferenceController.
     * @throws databaseaccess.exceptions.ClassicDatabaseException
     * @throws databaseaccess.exceptions.ClassicNotFoundException
     */
    @Test(expected=ClassicNotFoundException.class)
    public void testAddVideoReference_VideoNotFoundException() throws ClassicDatabaseException, ClassicNotFoundException, ClassicUnauthorizedException {
        Video cn = new Video("title", "url", 999);
        int cnId = videoController.addVideo(courseId, cn, userId);
        Comment comment = new Comment("body", false);

        VideoReference expResult = new VideoReference(cn, 20);
        List<VideoReference> refs = new ArrayList<>();
        refs.add(expResult);
        int commentId = commentController.addVideoComment(cnId, comment, refs, userId);
        VideoReference expResult2 = new VideoReference(cn, 10);
        
        videoReferenceController.addVideoReference(0, commentId, expResult2, userId);
    }
    
    /**
     * Test of addVideoReference method, of class VideoReferenceController.
     * @throws databaseaccess.exceptions.ClassicDatabaseException
     * @throws databaseaccess.exceptions.ClassicNotFoundException
     */
    @Test(expected=ClassicNotFoundException.class)
    public void testAddVideoReference_CommentNotFoundException() throws ClassicDatabaseException, ClassicNotFoundException, ClassicUnauthorizedException {
        Video cn = new Video("title", "url", 999);
        int cnId = videoController.addVideo(courseId, cn, userId);
        Comment comment = new Comment("body", false);
        
        VideoReference expResult = new VideoReference(cn, 20);
        List<VideoReference> refs = new ArrayList<>();
        refs.add(expResult);
        int commentId = commentController.addVideoComment(cnId, comment, refs, userId);
        VideoReference expResult2 = new VideoReference(cn, 10);
        
        videoReferenceController.addVideoReference(cnId, 0, expResult2, userId);
    }

    /**
     * Test of getVideoReference method, of class VideoReferenceController.
     * @throws databaseaccess.exceptions.ClassicDatabaseException
     * @throws databaseaccess.exceptions.ClassicNotFoundException
     */
    @Test
    public void testGetSelfVideoReferences() throws ClassicDatabaseException, ClassicNotFoundException, ClassicUnauthorizedException {
        Video cn = new Video("title", "url", 999);
        int cnId = videoController.addVideo(courseId, cn, userId);
        Comment comment = new Comment("body", false);
        VideoReference expResult = new VideoReference(cn, 20);
        
        List<VideoReference> refs = new ArrayList<>();
        refs.add(expResult);
        int commentId = commentController.addVideoComment(cnId, comment, refs, userId);
        
        List<VideoReference> result = videoReferenceController.getSelfVideoReferences(cnId, commentId);
        
        assertEquals(cnId, result.get(0).getVideo().getId());
        assertEquals(expResult.getTimestamp(), result.get(0).getTimestamp());
    }
    
    /**
     * Test of getVideoReference method, of class VideoReferenceController.
     * @throws databaseaccess.exceptions.ClassicDatabaseException
     * @throws databaseaccess.exceptions.ClassicNotFoundException
     */
    @Test(expected=ClassicNotFoundException.class)
    public void testGetSelfVideoReferences_VideoNotFoundException() throws ClassicDatabaseException, ClassicNotFoundException, ClassicUnauthorizedException {
        Video cn = new Video("title", "url", 999);
        int cnId = videoController.addVideo(courseId, cn, userId);
        Comment comment = new Comment("body", false);
        
        VideoReference expResult = new VideoReference(cn, 20);
        List<VideoReference> refs = new ArrayList<>();
        refs.add(expResult);
        int commentId = commentController.addVideoComment(cnId, comment, refs, userId);
        
        videoReferenceController.getSelfVideoReferences(0, commentId);
    }
    
    /**
     * Test of getVideoReference method, of class VideoReferenceController.
     * @throws databaseaccess.exceptions.ClassicDatabaseException
     * @throws databaseaccess.exceptions.ClassicNotFoundException
     */
    @Test(expected=ClassicNotFoundException.class)
    public void testGetSelfVideoReferences_CommentNotFoundException() throws ClassicDatabaseException, ClassicNotFoundException, ClassicUnauthorizedException {
        Video cn = new Video("title", "url", 999);
        int cnId = videoController.addVideo(courseId, cn, userId);
        Comment comment = new Comment("body", false);
        
        VideoReference expResult = new VideoReference(cn, 20);
        List<VideoReference> refs = new ArrayList<>();
        refs.add(expResult);
        commentController.addVideoComment(cnId, comment, refs, userId);
        
        videoReferenceController.getSelfVideoReferences(cnId, 0);
    }

    /**
     * Test of getVideoReferences method, of class VideoReferenceController.
     * @throws databaseaccess.exceptions.ClassicDatabaseException
     * @throws databaseaccess.exceptions.ClassicNotFoundException
     */
    @Test
    public void testGetVideoReferences() throws ClassicDatabaseException, ClassicNotFoundException, ClassicUnauthorizedException {
        Video notes = new Video("title", "url", 999);
        int cnId = videoController.addVideo(courseId, notes, userId);
        Comment comment = new Comment("body", false);
        VideoReference expResult = new VideoReference(notes, 20);
        
        List<VideoReference> refs = new ArrayList<>();
        refs.add(expResult);
        int commentId = commentController.addVideoComment(cnId, comment, refs, userId);
        
        Video notes2 = new Video("title2", "url2", 888);
        int videoId2 = videoController.addVideo(courseId, notes2, userId);
        VideoReference expResult2 = new VideoReference(notes2, 10);
        
        videoReferenceController.addVideoReference(videoId2, commentId, expResult2, userId);
        
        List<VideoReference> result = videoReferenceController.getVideoReferences(commentId);
        
        assertEquals(cnId, result.get(0).getVideo().getId());
        assertEquals(expResult.getTimestamp(), result.get(0).getTimestamp()); 
        
        assertEquals(videoId2, result.get(1).getVideo().getId());
        assertEquals(expResult2.getTimestamp(), result.get(1).getTimestamp()); 
    }
    
    /**
     * Test of getVideoReferences method, of class VideoReferenceController.
     * @throws databaseaccess.exceptions.ClassicDatabaseException
     * @throws databaseaccess.exceptions.ClassicNotFoundException
     */
    @Test(expected=ClassicNotFoundException.class)
    public void testGetVideoReferences_CommentFoundException() throws ClassicDatabaseException, ClassicNotFoundException {
        videoReferenceController.getVideoReferences(0);
    }
    
    /**
     * Test of getVideoReferences method, of class VideoReferenceController.
     * @throws databaseaccess.exceptions.ClassicDatabaseException
     * @throws databaseaccess.exceptions.ClassicNotFoundException
     */
    @Test
    public void testGetVideoReferencesIsEmpty() throws ClassicDatabaseException, ClassicNotFoundException, ClassicUnauthorizedException {
        CourseNotes courseNotes = new CourseNotes("title", "url");
        int courseNotesId = courseNotesController.addCourseNotes(courseId, courseNotes, userId);
        Comment comment = new Comment("body", false);
        List<Location> locations = new ArrayList<>();
        locations.add(new Location(10, 2, 3, 4, 5));
        
        CourseNotesReference courseNotesRef = new CourseNotesReference(courseNotes, locations);
        List<CourseNotesReference> refs = new ArrayList<>();
        refs.add(courseNotesRef);
        int commentId = commentController.addCourseNotesComment(courseNotesId, comment, refs, userId);
        
        List<VideoReference> result = videoReferenceController.getVideoReferences(commentId);
        assertEquals(true, result.isEmpty());
    }

    /**
     * Test of updateVideoReference method, of class VideoReferenceController.
     * @throws databaseaccess.exceptions.ClassicDatabaseException
     * @throws databaseaccess.exceptions.ClassicNotFoundException
     */
    @Test
    public void testUpdateVideoReference() throws ClassicDatabaseException, ClassicNotFoundException, ClassicUnauthorizedException {
        Video video = new Video("title", "url", 999);
        int videoId = videoController.addVideo(courseId, video, userId);
        Comment comment = new Comment("body", false);
        VideoReference expResult = new VideoReference(video, 20);
        
        List<VideoReference> refs = new ArrayList<>();
        refs.add(expResult);
        int commentId = commentController.addVideoComment(videoId, comment, refs, userId);
        
        List<VideoReference> result = videoReferenceController.getSelfVideoReferences(videoId, commentId);
        int refId = result.get(0).getRefId();
        
        expResult.setTimestamp(30);
        videoReferenceController.updateVideoReference(commentId, refId, expResult, userId);
        
        result = videoReferenceController.getSelfVideoReferences(videoId, commentId);
        
        assertEquals(videoId, result.get(0).getVideo().getId());
        assertEquals(expResult.getTimestamp(), result.get(0).getTimestamp());
    }
    
    /**
     * Test of updateVideoReference method, of class VideoReferenceController.
     * @throws databaseaccess.exceptions.ClassicDatabaseException
     * @throws databaseaccess.exceptions.ClassicNotFoundException
     */
    @Test(expected=ClassicNotFoundException.class)
    public void testUpdateVideoReferenceNotFoundException() throws ClassicDatabaseException, ClassicNotFoundException, ClassicUnauthorizedException {
        Video notes = new Video("title", "url", 999);
        int videoId = videoController.addVideo(courseId, notes, userId);
        Comment comment = new Comment("body", false);
        VideoReference expResult = new VideoReference(notes, 20);
        
        List<VideoReference> refs = new ArrayList<>();
        refs.add(expResult);
        int commentId = commentController.addVideoComment(videoId, comment, refs, userId);
        
        List<VideoReference> result = videoReferenceController.getSelfVideoReferences(videoId, commentId);
        result.get(0).getRefId();
        
        expResult = new VideoReference(notes, 10);
        
        videoReferenceController.updateVideoReference(commentId, 0, expResult, userId);
    }

    /**
     * Test of deleteVideoReference method, of class VideoReferenceController.
     * @throws databaseaccess.exceptions.ClassicDatabaseException
     * @throws databaseaccess.exceptions.ClassicNotFoundException
     */
    @Test
    public void testDeleteVideoReference() throws ClassicDatabaseException, ClassicNotFoundException, ClassicUnauthorizedException {
        Video video = new Video("title", "url", 999);
        int videoId = videoController.addVideo(courseId, video, userId);
        Comment comment = new Comment("body", false);
        List<Location> locations = new ArrayList<>();
        locations.add(new Location(20, 1, 2, 3, 4));
        VideoReference expResult = new VideoReference(video, 20);
        
        List<VideoReference> refs = new ArrayList<>();
        refs.add(expResult);
        int commentId = commentController.addVideoComment(videoId, comment, refs, userId);
        
        List<VideoReference> result = videoReferenceController.getSelfVideoReferences(videoId, commentId);
        int refId = result.get(0).getRefId();
        
        videoReferenceController.deleteVideoReference(commentId, refId, userId);
        result = videoReferenceController.getSelfVideoReferences(videoId, commentId);
        
        assertEquals(true, result.isEmpty());
    }
    
    /**
     * Test of deleteVideoReference method, of class VideoReferenceController.
     * @throws databaseaccess.exceptions.ClassicDatabaseException
     * @throws databaseaccess.exceptions.ClassicNotFoundException
     */
    @Test(expected=ClassicNotFoundException.class)
    public void testDeleteVideoReferenceNotFoundException() throws ClassicDatabaseException, ClassicNotFoundException, ClassicUnauthorizedException {
        videoReferenceController.deleteVideoReference(0, 0, userId);
    }
    
    /**
     * Test of getVideoReference method, of class VideoReferenceController.
     * @throws databaseaccess.exceptions.ClassicDatabaseException
     * @throws databaseaccess.exceptions.ClassicNotFoundException
     */
    @Test
    public void testGetVideoReference() throws ClassicDatabaseException, ClassicNotFoundException, ClassicUnauthorizedException {
    	Video notes = new Video("title", "url", 999);
        int cnId = videoController.addVideo(courseId, notes, userId);
        Comment comment = new Comment("body", false);
        VideoReference expResult = new VideoReference(notes, 20);
        
        List<VideoReference> refs = new ArrayList<>();
        refs.add(expResult);
        int commentId = commentController.addVideoComment(cnId, comment, refs, userId);
        
        Video notes2 = new Video("title2", "url2", 888);
        int videoId2 = videoController.addVideo(courseId, notes2, userId);
        VideoReference expResult2 = new VideoReference(notes2, 10);
        
        int refid = videoReferenceController.addVideoReference(videoId2, commentId, expResult2, userId);
        
        VideoReference result = videoReferenceController.getVideoReference(refid);
        
        assertEquals(videoId2, result.getVideo().getId());
        assertEquals(expResult2.getTimestamp(), result.getTimestamp()); 
    }
    
    /**
     * Test of getVideoReference method, of class VideoReferenceController.
     * @throws databaseaccess.exceptions.ClassicDatabaseException
     * @throws databaseaccess.exceptions.ClassicNotFoundException
     */
    @Test(expected=ClassicNotFoundException.class)
    public void testGetVideoReferenceNotFoundException() throws ClassicDatabaseException, ClassicNotFoundException {
        videoReferenceController.getVideoReference(0);
    }
}
