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
import java.util.ArrayList;
import java.util.List;
import models.Comment;
import models.CourseNotes;
import models.CourseNotesReference;
import models.Location;
import models.Video;
import models.VideoReference;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Juta
 */
public class CourseNotesReferenceControllerTest {
    
    private CourseNotesController courseNotesController;
    private CommentController commentController;
    private CourseNotesReferenceController courseNotesReferenceController;
    private VideoController videoController;

    @Before
    public void setUp() throws ClassicDatabaseException {
        MockUpDAO mockup = new MockUpDAO();
        CoursesMockUpDAO coursesMockup = new CoursesMockUpDAO();
        commentController = new CommentController(mockup, mockup, mockup, mockup, coursesMockup);
        videoController = new VideoController(mockup, coursesMockup, coursesMockup);
        courseNotesController = new CourseNotesController(mockup, coursesMockup, coursesMockup, "/var/www/classic/resources/courses");
        courseNotesReferenceController = new CourseNotesReferenceController(mockup, mockup, coursesMockup);
        userId = coursesMockup.addUser(new User("username", Role.ADMIN, "password"));
    }
    
    private int userId;
    private int courseId = 1;

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
