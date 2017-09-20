/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import static org.junit.Assert.assertEquals;

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

/**
 *
 * @author Juta
 */
public class CourseNotesReferenceControllerIT {
    
    private CommentController commentController;
    private CourseNotesReferenceController courseNotesReferenceController;
    private CourseNotesController courseNotesController;
    private VideoController videoController;
    private final String TEST_DATABASE_CONFIG = "testdbconfig.properties";
    private ICourseNotesDao notesdao;
    private IVideoDao videodao;
    private ICommentDao commentdao;
    
    //courses 
    private CourseController courseControler;
    private ICourseDao coursedao;
    private ILectureDao lecturedao;
    private UserController userController;
    private IUserDao userdao;
    private ICourseNotesReferenceDao notesrefdao;
    private IVideoReferenceDao videorefdao;
    private IReplyDao replydao;
    private int courseId = 0;
    private int userId;

    @Before
    public void setUp() throws ClassicDatabaseException, ClassicNotFoundException {
        commentdao = new CommentDao(TEST_DATABASE_CONFIG);
        notesdao = new CourseNotesDao(TEST_DATABASE_CONFIG);
        videodao = new VideoDao(TEST_DATABASE_CONFIG);
        userdao = new UserDao(TEST_DATABASE_CONFIG);
        notesrefdao = new CourseNotesReferenceDao(TEST_DATABASE_CONFIG);
        coursedao = new CourseDao(TEST_DATABASE_CONFIG);
        lecturedao = new LectureDao(TEST_DATABASE_CONFIG);
        videorefdao = new VideoReferenceDao(TEST_DATABASE_CONFIG);
        replydao = new ReplyDao(TEST_DATABASE_CONFIG);
        
        courseControler = new CourseController(coursedao, lecturedao, videodao, notesdao, userdao, "/var/www/classic/resources/courses");
        userController = new UserController(userdao);
        courseNotesReferenceController = new CourseNotesReferenceController(notesrefdao, commentdao, userdao);
        courseNotesController = new CourseNotesController(notesdao, coursedao, userdao, "/var/www/classic/resources/courses");
        commentController = new CommentController(commentdao, replydao, videorefdao, notesrefdao, userdao);
        videoController = new VideoController(videodao, coursedao, userdao);
        
        User u = new User("prof", Role.TEACHER,"password");
        userId = userController.addUser(u);
        Course c = new Course("course");
        courseId = courseControler.addCourse(c, userId);
    }
    
    @After
    public void tearDown() throws ClassicDatabaseException {
        notesdao.cleanTable();
        videodao.cleanTable();
        commentdao.cleanTable();
        coursedao.cleanTable();
        userdao.cleanTable();
    }

    /**
     * Test of addCourseNotesReference method, of class CourseNotesReferenceController.
     * @throws databaseaccess.exceptions.ClassicDatabaseException
     * @throws databaseaccess.exceptions.ClassicNotFoundException
     */
    @Test
    public void testAddCourseNotesReference() throws ClassicDatabaseException, ClassicNotFoundException, ClassicUnauthorizedException{
        CourseNotes cn = new CourseNotes("title", "url");
        int cnId = courseNotesController.addCourseNotes(courseId, cn, userId);
        Comment comment = new Comment("body", false);
        List<Location> locations = new ArrayList<>();
        locations.add(new Location(20, 1, 2, 3, 4));
        CourseNotesReference expResult = new CourseNotesReference(cn, locations);
        
        List<CourseNotesReference> refs = new ArrayList<>();
        refs.add(expResult);
        int commentId = commentController.addCourseNotesComment(cnId, comment, refs, userId);
        
        locations = new ArrayList<>();
        locations.add(new Location(10, 2, 3, 4, 5));
        CourseNotesReference expResult2 = new CourseNotesReference(cn, locations);
        courseNotesReferenceController.addCourseNotesReference(cnId, commentId, expResult2, userId);
        
        List<CourseNotesReference> result = courseNotesReferenceController.getSelfCourseNotesReferences(cnId, commentId);
        
        assertEquals(cnId, result.get(0).getCourseNotes().getId());
        assertEquals(cnId, result.get(1).getCourseNotes().getId());
    }
    
    /**
     * Test of getParents method, of class CourseNotesReferenceController.
     * @throws databaseaccess.exceptions.ClassicDatabaseException
     * @throws databaseaccess.exceptions.ClassicNotFoundException
     */
    @Test
    public void testGetParents() throws ClassicDatabaseException, ClassicNotFoundException, ClassicUnauthorizedException{
        CourseNotes cn = new CourseNotes("title", "url");
        int cnId = courseNotesController.addCourseNotes(courseId, cn, userId);
        Comment comment = new Comment("body", false);
        List<Location> locations = new ArrayList<>();
        locations.add(new Location(20, 1, 2, 3, 4));
        CourseNotesReference expResult = new CourseNotesReference(cn, locations);
        
        List<CourseNotesReference> refs = new ArrayList<>();
        refs.add(expResult);
        int commentId = commentController.addCourseNotesComment(cnId, comment, refs, userId);
        
        locations = new ArrayList<>();
        locations.add(new Location(10, 2, 3, 4, 5));
        CourseNotesReference expResult2 = new CourseNotesReference(cn, locations);
        courseNotesReferenceController.addCourseNotesReference(cnId, commentId, expResult2, userId);
        
        int[] result = courseNotesReferenceController.getParents(cnId);
        
        assertEquals(courseId, result[0]);
        assertEquals(0, result[1]);
    }
    
    /**
     * Test of addCourseNotesReference method, of class CourseNotesReferenceController.
     * @throws databaseaccess.exceptions.ClassicDatabaseException
     * @throws databaseaccess.exceptions.ClassicNotFoundException
     */
    @Test(expected=ClassicNotFoundException.class)
    public void testAddCourseNotesReference_CourseNotesNotFoundException() throws ClassicDatabaseException, ClassicNotFoundException, ClassicUnauthorizedException {
        CourseNotes cn = new CourseNotes("title", "url");
        int cnId = courseNotesController.addCourseNotes(courseId, cn, userId);
        Comment comment = new Comment("body", false);
        List<Location> locations = new ArrayList<>();
        locations.add(new Location(20, 1, 2, 3, 4));
        CourseNotesReference expResult = new CourseNotesReference(cn, locations);
        
        List<CourseNotesReference> refs = new ArrayList<>();
        refs.add(expResult);
        int commentId = commentController.addCourseNotesComment(cnId, comment, refs, userId);
        
        locations = new ArrayList<>();
        locations.add(new Location(10, 2, 3, 4, 5));
        CourseNotesReference expResult2 = new CourseNotesReference(cn, locations);
        
        courseNotesReferenceController.addCourseNotesReference(0, commentId, expResult2, userId);
    }
    
    /**
     * Test of addCourseNotesReference method, of class CourseNotesReferenceController.
     * @throws databaseaccess.exceptions.ClassicDatabaseException
     * @throws databaseaccess.exceptions.ClassicNotFoundException
     */
    @Test(expected=ClassicNotFoundException.class)
    public void testAddCourseNotesReference_CommentNotFoundException() throws ClassicDatabaseException, ClassicNotFoundException, ClassicUnauthorizedException {
        CourseNotes cn = new CourseNotes("title", "url");
        int cnId = courseNotesController.addCourseNotes(courseId, cn, userId);
        Comment comment = new Comment("body", false);
        List<Location> locations = new ArrayList<>();
        locations.add(new Location(20, 1, 2, 3, 4));
        CourseNotesReference expResult = new CourseNotesReference(cn, locations);
        
        List<CourseNotesReference> refs = new ArrayList<>();
        refs.add(expResult);
        commentController.addCourseNotesComment(cnId, comment, refs, userId);
        
        locations = new ArrayList<>();
        locations.add(new Location(10, 2, 3, 4, 5));
        CourseNotesReference expResult2 = new CourseNotesReference(cn, locations);
        
        courseNotesReferenceController.addCourseNotesReference(cnId, 0, expResult2, userId);
    }

    /**
     * Test of getCourseNotesReference method, of class CourseNotesReferenceController.
     * @throws databaseaccess.exceptions.ClassicDatabaseException
     * @throws databaseaccess.exceptions.ClassicNotFoundException
     */
    @Test
    public void testGetSelfCourseNotesReferences() throws ClassicDatabaseException, ClassicNotFoundException, ClassicUnauthorizedException {
        CourseNotes cn = new CourseNotes("title", "url");
        int cnId = courseNotesController.addCourseNotes(courseId, cn, userId);
        Comment comment = new Comment("body", false);
        List<Location> locations = new ArrayList<>();
        locations.add(new Location(20, 1, 2, 3, 4));
        CourseNotesReference expResult = new CourseNotesReference(cn, locations);
        
        List<CourseNotesReference> refs = new ArrayList<>();
        refs.add(expResult);
        int commentId = commentController.addCourseNotesComment(cnId, comment, refs, userId);
        
        List<CourseNotesReference> result = courseNotesReferenceController.getSelfCourseNotesReferences(cnId, commentId);
        
        assertEquals(cnId, result.get(0).getCourseNotes().getId());
        assertEquals(expResult.getLocations().get(0).getX1(), result.get(0).getLocations().get(0).getX1(),4);
        assertEquals(expResult.getLocations().get(0).getY1(), result.get(0).getLocations().get(0).getY1(),4);
        assertEquals(expResult.getLocations().get(0).getX2(), result.get(0).getLocations().get(0).getX2(),4);
        assertEquals(expResult.getLocations().get(0).getY2(), result.get(0).getLocations().get(0).getY2(),4);
        assertEquals(expResult.getLocations().get(0).getPagenumber(), result.get(0).getLocations().get(0).getPagenumber());
    }
    
    /**
     * Test of getCourseNotesReference method, of class CourseNotesReferenceController.
     * @throws databaseaccess.exceptions.ClassicDatabaseException
     * @throws databaseaccess.exceptions.ClassicNotFoundException
     */
    @Test(expected=ClassicNotFoundException.class)
    public void testGetSelfCourseNotesReferences_CourseNotesNotFoundException() throws ClassicDatabaseException, ClassicNotFoundException, ClassicUnauthorizedException {
        CourseNotes cn = new CourseNotes("title", "url");
        int cnId = courseNotesController.addCourseNotes(courseId, cn, userId);
        Comment comment = new Comment("body", false);
        List<Location> locations = new ArrayList<>();
        locations.add(new Location(20, 1, 2, 3, 4));
        CourseNotesReference expResult = new CourseNotesReference(cn, locations);
        
        List<CourseNotesReference> refs = new ArrayList<>();
        refs.add(expResult);
        int commentId = commentController.addCourseNotesComment(cnId, comment, refs, userId);
        
        courseNotesReferenceController.getSelfCourseNotesReferences(0, commentId);
    }
    
    /**
     * Test of getCourseNotesReference method, of class CourseNotesReferenceController.
     * @throws databaseaccess.exceptions.ClassicDatabaseException
     * @throws databaseaccess.exceptions.ClassicNotFoundException
     */
    @Test(expected=ClassicNotFoundException.class)
    public void testGetSelfCourseNotesReferences_CommentNotFoundException() throws ClassicDatabaseException, ClassicNotFoundException, ClassicUnauthorizedException {
        CourseNotes cn = new CourseNotes("title", "url");
        int cnId = courseNotesController.addCourseNotes(courseId, cn, userId);
        Comment comment = new Comment("body", false);
        List<Location> locations = new ArrayList<>();
        locations.add(new Location(20, 1, 2, 3, 4));
        CourseNotesReference expResult = new CourseNotesReference(cn, locations);
        
        List<CourseNotesReference> refs = new ArrayList<>();
        refs.add(expResult);
        commentController.addCourseNotesComment(cnId, comment, refs, userId);
        
        courseNotesReferenceController.getSelfCourseNotesReferences(cnId, 0);
    }

    /**
     * Test of getCourseNotesReferences method, of class CourseNotesReferenceController.
     * @throws databaseaccess.exceptions.ClassicDatabaseException
     * @throws databaseaccess.exceptions.ClassicNotFoundException
     */
    @Test
    public void testGetCourseNotesReferences() throws ClassicDatabaseException, ClassicNotFoundException, ClassicUnauthorizedException {
        CourseNotes notes = new CourseNotes("title", "url");
        int cnId = courseNotesController.addCourseNotes(courseId, notes, userId);
        Comment comment = new Comment("body", false);
        List<Location> locations = new ArrayList<>();
        locations.add(new Location(20, 1, 2, 3, 4));
        CourseNotesReference expResult = new CourseNotesReference(notes, locations);
        
        List<CourseNotesReference> refs = new ArrayList<>();
        refs.add(expResult);
        int commentId = commentController.addCourseNotesComment(cnId, comment, refs, userId);
        
        CourseNotes notes2 = new CourseNotes("title2", "url2");
        int courseNotesId2 = courseNotesController.addCourseNotes(courseId, notes2, userId);
        List<Location> locations2 = new ArrayList<>();
        locations2.add(new Location(10, 2, 3, 4, 5));
        CourseNotesReference expResult2 = new CourseNotesReference(notes2, locations2);
        
        courseNotesReferenceController.addCourseNotesReference(courseNotesId2, commentId, expResult2, userId);
        
        List<CourseNotesReference> result = courseNotesReferenceController.getCourseNotesReferences(commentId);
        
        assertEquals(cnId, result.get(0).getCourseNotes().getId());
        assertEquals(expResult.getLocations().get(0).getX1(), result.get(0).getLocations().get(0).getX1(), 0.0001);
        assertEquals(expResult.getLocations().get(0).getY1(), result.get(0).getLocations().get(0).getY1(), 0.0001);
        assertEquals(expResult.getLocations().get(0).getX2(), result.get(0).getLocations().get(0).getX2(), 0.0001);
        assertEquals(expResult.getLocations().get(0).getY2(), result.get(0).getLocations().get(0).getY2(), 0.0001);
        assertEquals(expResult.getLocations().get(0).getPagenumber(), result.get(0).getLocations().get(0).getPagenumber());
        
        assertEquals(courseNotesId2, result.get(1).getCourseNotes().getId());
        assertEquals(expResult2.getLocations().get(0).getX1(), result.get(1).getLocations().get(0).getX1(), 0.0001);
        assertEquals(expResult2.getLocations().get(0).getY1(), result.get(1).getLocations().get(0).getY1(), 0.0001);
        assertEquals(expResult2.getLocations().get(0).getX2(), result.get(1).getLocations().get(0).getX2(), 0.0001);
        assertEquals(expResult2.getLocations().get(0).getY2(), result.get(1).getLocations().get(0).getY2(), 0.0001);
        assertEquals(expResult2.getLocations().get(0).getPagenumber(), result.get(1).getLocations().get(0).getPagenumber());
    }
    
    /**
     * Test of getCourseNotesReferences method, of class CourseNotesReferenceController.
     * @throws databaseaccess.exceptions.ClassicDatabaseException
     * @throws databaseaccess.exceptions.ClassicNotFoundException
     */
    @Test(expected=ClassicNotFoundException.class)
    public void testGetCourseNotesReferences_CommentFoundException() throws ClassicDatabaseException, ClassicNotFoundException {
        courseNotesReferenceController.getCourseNotesReferences(0);
    }
    
    /**
     * Test of getCourseNotesReferences method, of class CourseNotesReferenceController.
     * @throws databaseaccess.exceptions.ClassicDatabaseException
     * @throws databaseaccess.exceptions.ClassicNotFoundException
     */
    @Test
    public void testGetCourseNotesReferencesIsEmpty() throws ClassicDatabaseException, ClassicNotFoundException, ClassicUnauthorizedException {
        Video video = new Video("title", "url", 20);
        int videoId = videoController.addVideo(courseId, video, userId);
        Comment comment = new Comment("body", false);
        VideoReference videoRef = new VideoReference(video, 20);
        
        List<VideoReference> refs = new ArrayList<>();
        refs.add(videoRef);
        int commentId = commentController.addVideoComment(videoId, comment, refs, userId);
        List<CourseNotesReference> result = courseNotesReferenceController.getCourseNotesReferences(commentId);
        assertEquals(true, result.isEmpty());
    }

    /**
     * Test of updateCourseNotesReference method, of class CourseNotesReferenceController.
     * @throws databaseaccess.exceptions.ClassicDatabaseException
     * @throws databaseaccess.exceptions.ClassicNotFoundException
     */
    @Test
    public void testUpdateCourseNotesReference() throws ClassicDatabaseException, ClassicNotFoundException, ClassicUnauthorizedException {
        CourseNotes notes = new CourseNotes("title", "url");
        int courseNotesId = courseNotesController.addCourseNotes(courseId, notes, userId);
        Comment comment = new Comment("body", false);
        List<Location> locations = new ArrayList<>();
        locations.add(new Location(20, 1, 2, 3, 4));
        CourseNotesReference expResult = new CourseNotesReference(notes, locations);
        
        List<CourseNotesReference> refs = new ArrayList<>();
        refs.add(expResult);
        int commentId = commentController.addCourseNotesComment(courseNotesId, comment, refs, userId);
        
        List<CourseNotesReference> result = courseNotesReferenceController.getSelfCourseNotesReferences(courseNotesId, commentId);
        int refId = result.get(0).getRefId();
        
        locations = new ArrayList<>();
        locations.add(new Location(10, 2, 3, 4, 5));
        expResult.setLocations(locations);
        courseNotesReferenceController.updateCourseNotesReference(commentId, refId, expResult, userId);
        
        result = courseNotesReferenceController.getSelfCourseNotesReferences(courseNotesId, commentId);
        
        assertEquals(courseNotesId, result.get(0).getCourseNotes().getId());
        assertEquals(expResult.getLocations().get(0).getX1(), result.get(0).getLocations().get(0).getX1(), 0.0001);
        assertEquals(expResult.getLocations().get(0).getY1(), result.get(0).getLocations().get(0).getY1(), 0.0001);
        assertEquals(expResult.getLocations().get(0).getX2(), result.get(0).getLocations().get(0).getX2(), 0.0001);
        assertEquals(expResult.getLocations().get(0).getY2(), result.get(0).getLocations().get(0).getY2(), 0.0001);
        assertEquals(expResult.getLocations().get(0).getPagenumber(), result.get(0).getLocations().get(0).getPagenumber());
    }
    
    /**
     * Test of updateCourseNotesReference method, of class CourseNotesReferenceController.
     * @throws databaseaccess.exceptions.ClassicDatabaseException
     * @throws databaseaccess.exceptions.ClassicNotFoundException
     */
    @Test(expected=ClassicNotFoundException.class)
    public void testUpdateCourseNotesReferenceNotFoundException() throws ClassicDatabaseException, ClassicNotFoundException, ClassicUnauthorizedException {
        CourseNotes notes = new CourseNotes("title", "url");
        int courseNotesId = courseNotesController.addCourseNotes(courseId, notes, userId);
        Comment comment = new Comment("body", false);
        List<Location> locations = new ArrayList<>();
        locations.add(new Location(20, 1, 2, 3, 4));
        CourseNotesReference expResult = new CourseNotesReference(notes, locations);
        
        List<CourseNotesReference> refs = new ArrayList<>();
        refs.add(expResult);
        int commentId = commentController.addCourseNotesComment(courseNotesId, comment, refs, userId);
        
        List<CourseNotesReference> result = courseNotesReferenceController.getSelfCourseNotesReferences(courseNotesId, commentId);
        int refId = result.get(0).getRefId();
        
        locations = new ArrayList<>();
        locations.add(new Location(10, 2, 3, 4, 5));
        expResult = new CourseNotesReference(notes, locations);
        
        courseNotesReferenceController.updateCourseNotesReference(commentId, 0, expResult, userId);
    }

    /**
     * Test of deleteCourseNotesReference method, of class CourseNotesReferenceController.
     * @throws databaseaccess.exceptions.ClassicDatabaseException
     * @throws databaseaccess.exceptions.ClassicNotFoundException
     */
    @Test
    public void testDeleteCourseNotesReference() throws ClassicDatabaseException, ClassicNotFoundException, ClassicUnauthorizedException {
        CourseNotes notes = new CourseNotes("title", "url");
        int courseNotesId = courseNotesController.addCourseNotes(courseId, notes, userId);
        Comment comment = new Comment("body", false);
        List<Location> locations = new ArrayList<>();
        locations.add(new Location(20, 1, 2, 3, 4));
        CourseNotesReference expResult = new CourseNotesReference(notes, locations);
        
        List<CourseNotesReference> refs = new ArrayList<>();
        refs.add(expResult);
        int commentId = commentController.addCourseNotesComment(courseNotesId, comment, refs, userId);
        
        List<CourseNotesReference> result = courseNotesReferenceController.getSelfCourseNotesReferences(courseNotesId, commentId);
        int refId = result.get(0).getRefId();
        
        courseNotesReferenceController.deleteCourseNotesReference(commentId, refId, userId);
        result = courseNotesReferenceController.getSelfCourseNotesReferences(courseNotesId, commentId);
        
        assertEquals(true, result.isEmpty());
    }
    
    /**
     * Test of deleteCourseNotesReference method, of class CourseNotesReferenceController.
     * @throws databaseaccess.exceptions.ClassicDatabaseException
     * @throws databaseaccess.exceptions.ClassicNotFoundException
     */
    @Test(expected=ClassicNotFoundException.class)
    public void testDeleteCourseNotesReferenceNotFoundException() throws ClassicDatabaseException, ClassicNotFoundException, ClassicUnauthorizedException {
        courseNotesReferenceController.deleteCourseNotesReference(0, 0, userId);
    }
    
    /**
     * Test of getCourseNotesReference method, of class CourseNotesReferenceController.
     * @throws databaseaccess.exceptions.ClassicDatabaseException
     * @throws databaseaccess.exceptions.ClassicNotFoundException
     */
    @Test
    public void testGetCourseNotesReference() throws ClassicDatabaseException, ClassicNotFoundException, ClassicUnauthorizedException {
    	CourseNotes notes = new CourseNotes("title", "url");
        int cnId = courseNotesController.addCourseNotes(courseId, notes, userId);
        Comment comment = new Comment("body", false);
        List<Location> locations = new ArrayList<>();
        locations.add(new Location(20, 1, 2, 3, 4));
        CourseNotesReference expResult = new CourseNotesReference(notes, locations);
        
        List<CourseNotesReference> refs = new ArrayList<>();
        refs.add(expResult);
        int commentId = commentController.addCourseNotesComment(cnId, comment, refs, userId);
        
        CourseNotes notes2 = new CourseNotes("title2", "url2");
        int courseNotesId2 = courseNotesController.addCourseNotes(courseId, notes2, userId);
        List<Location> locations2 = new ArrayList<>();
        locations2.add(new Location(10, 2, 3, 4, 5));
        CourseNotesReference expResult2 = new CourseNotesReference(notes2, locations2);
        
        int refid = courseNotesReferenceController.addCourseNotesReference(courseNotesId2, commentId, expResult2, userId);
        
        CourseNotesReference result = courseNotesReferenceController.getCourseNotesReference(refid);
        
        assertEquals(courseNotesId2, result.getCourseNotes().getId());
        assertEquals(expResult2.getLocations().get(0).getX1(), result.getLocations().get(0).getX1(), 0.0001);
        assertEquals(expResult2.getLocations().get(0).getY1(), result.getLocations().get(0).getY1(), 0.0001);
        assertEquals(expResult2.getLocations().get(0).getX2(), result.getLocations().get(0).getX2(), 0.0001);
        assertEquals(expResult2.getLocations().get(0).getY2(), result.getLocations().get(0).getY2(), 0.0001);
        assertEquals(expResult2.getLocations().get(0).getPagenumber(), result.getLocations().get(0).getPagenumber()); 
    }
    
    /**
     * Test of getCourseNotesReference method, of class CourseNotesReferenceController.
     * @throws databaseaccess.exceptions.ClassicDatabaseException
     * @throws databaseaccess.exceptions.ClassicNotFoundException
     */
    @Test(expected=ClassicNotFoundException.class)
    public void testGetCourseNotesReferenceNotFoundException() throws ClassicDatabaseException, ClassicNotFoundException {
    	courseNotesReferenceController.getCourseNotesReference(0);
    }
}
