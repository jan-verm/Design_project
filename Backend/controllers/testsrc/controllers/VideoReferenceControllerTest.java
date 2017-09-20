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
public class VideoReferenceControllerTest {
    
    private VideoController videoController;
    private CommentController commentController;
    private VideoReferenceController videoReferenceController;
    private CourseNotesController courseNotesController;

    @Before
    public void setUp() throws ClassicDatabaseException {
        MockUpDAO mockup = new MockUpDAO();
        CoursesMockUpDAO coursesMockup = new CoursesMockUpDAO();
        commentController = new CommentController(mockup, mockup, mockup, mockup, coursesMockup);
        videoController = new VideoController(mockup, coursesMockup, coursesMockup);
        courseNotesController = new CourseNotesController(mockup, coursesMockup, coursesMockup, "/var/www/classic/resources/courses");
        videoReferenceController = new VideoReferenceController(mockup, mockup, coursesMockup);
        userId = coursesMockup.addUser(new User("username", Role.ADMIN, "password"));
    }
    
    private int userId;
    private int courseId = 1;
    
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
