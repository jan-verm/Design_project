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
import models.Reply;
import models.Video;
import models.VideoReference;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Juta
 */
public class CommentControllerTest {
    
    private VideoController videoController;
    private CourseNotesController courseNotesController;
    private CommentController commentController;
    private int userId;
    private int courseId = 1;

    @Before
    public void setUp() throws ClassicDatabaseException {
        MockUpDAO mockup = new MockUpDAO();
        CoursesMockUpDAO coursesMockup = new CoursesMockUpDAO();
        commentController = new CommentController(mockup, mockup, mockup, mockup, coursesMockup);
        videoController = new VideoController(mockup, coursesMockup, coursesMockup);
        courseNotesController = new CourseNotesController(mockup, coursesMockup, coursesMockup, "/var/www/classic/resources/courses");
        userId = coursesMockup.addUser(new User("username", Role.ADMIN, "password"));
    }
    
    /**
     * Test of getVideoComments method, of class CommentController.
     * @throws databaseaccess.exceptions.ClassicDatabaseException
     * @throws databaseaccess.exceptions.ClassicNotFoundException
     */
    @Test
    public void testGetVideoComments() throws ClassicDatabaseException, ClassicNotFoundException, ClassicUnauthorizedException {
        Video video = new Video("title", "url", 20);
        int videoId = videoController.addVideo(courseId, video, userId);
        Comment comment = new Comment( "body", false);
        VideoReference videoRef = new VideoReference(video, 20);
        List<VideoReference> refs = new ArrayList<>();
        refs.add(videoRef);
        int commentId = commentController.addVideoComment(videoId, comment, refs, userId);
        List<Comment> expResult = new ArrayList<>();
        expResult.add(comment);
        
        List<Comment> result = commentController.getVideoComments(videoId);
        assertEquals(expResult.get(0).getBody(), result.get(0).getBody());
        assertEquals(expResult.get(0).getUser().getUsername(), result.get(0).getUser().getUsername());
        assertEquals(commentId, result.get(0).getId());
    }
    
     /**
     * Test of getVideoComments method, of class CommentController.
     * @throws databaseaccess.exceptions.ClassicDatabaseException
     * @throws databaseaccess.exceptions.ClassicNotFoundException
     */
    @Test
    public void testGetVideoCommentsEmpty() throws ClassicDatabaseException, ClassicNotFoundException, ClassicUnauthorizedException {
        Video video = new Video("title", "url", 20);
        int videoId = videoController.addVideo(courseId, video, userId);
        List<Comment> result = commentController.getVideoComments(videoId);
        
        assertEquals(true, result.isEmpty());
    }
    
     /**
     * Test of getVideoComments method, of class CommentController.
     * @throws databaseaccess.exceptions.ClassicDatabaseException
     * @throws databaseaccess.exceptions.ClassicNotFoundException
     */
    @Test(expected=ClassicNotFoundException.class)
    public void testGetVideoCommentsNotFoundException() throws ClassicDatabaseException, ClassicNotFoundException {
        commentController.getVideoComments(0);
    }
    
    /**
     * Test of getCourseNotesComments method, of class CommentController.
     * @throws databaseaccess.exceptions.ClassicDatabaseException
     * @throws databaseaccess.exceptions.ClassicNotFoundException
     */
    @Test
    public void testGetCourseNotesComments() throws ClassicDatabaseException, ClassicNotFoundException, ClassicUnauthorizedException {
        CourseNotes cn = new CourseNotes("title", "url");
        int cnId = courseNotesController.addCourseNotes(courseId, cn, userId);
        Comment comment = new Comment( "body", false);
        List<Location> locations = new ArrayList<>();
        locations.add(new Location(20, 1, 2, 3, 4));
        CourseNotesReference cnRef = new CourseNotesReference(cn, locations);
        List<CourseNotesReference> refs = new ArrayList<>();
        refs.add(cnRef);
        int commentId = commentController.addCourseNotesComment(cnId, comment, refs, userId);
        List<Comment> expResult = new ArrayList<>();
        expResult.add(comment);
        
        List<Comment> result = commentController.getCourseNotesComments(cnId);
        assertEquals(expResult.get(0).getBody(), result.get(0).getBody());
        assertEquals(expResult.get(0).getUser().getUsername(), result.get(0).getUser().getUsername());
        assertEquals(commentId, result.get(0).getId());
    }
    
    /**
     * Test of getCourseNotesComments method, of class CommentController.
     * @throws databaseaccess.exceptions.ClassicDatabaseException
     * @throws databaseaccess.exceptions.ClassicNotFoundException
     */
    @Test
    public void testGetCourseNotesCommentsIsEmpty() throws ClassicDatabaseException, ClassicNotFoundException, ClassicUnauthorizedException {
        CourseNotes cn = new CourseNotes("title", "url");
        int cnId = courseNotesController.addCourseNotes(courseId, cn, userId); 
        List<Comment> result = commentController.getCourseNotesComments(cnId);
        
        assertEquals(true, result.isEmpty());
    }
    
    /**
     * Test of getCourseNotesComments method, of class CommentController.
     * @throws databaseaccess.exceptions.ClassicDatabaseException
     * @throws databaseaccess.exceptions.ClassicNotFoundException
     */
    @Test(expected=ClassicNotFoundException.class)
    public void testGetCourseNotesCommentsNotFoundException() throws ClassicDatabaseException, ClassicNotFoundException {
        commentController.getCourseNotesComments(0);
    }

    /**
     * Test of addComment and getComment method, of class CommentController.
     * @throws databaseaccess.exceptions.ClassicDatabaseException
     * @throws databaseaccess.exceptions.ClassicNotFoundException
     */
    @Test
    public void testAddAndGetCourseNotesComment() throws ClassicDatabaseException, ClassicNotFoundException, ClassicUnauthorizedException {
        CourseNotes cn = new CourseNotes("title", "url");
        int cnId = courseNotesController.addCourseNotes(courseId, cn, userId);
        Comment comment = new Comment( "body", false);
        List<Location> locations = new ArrayList<>();
        locations.add(new Location(20, 1, 2, 3, 4));
        CourseNotesReference expResult = new CourseNotesReference(cn, locations);
        List<CourseNotesReference> refs = new ArrayList<>();
        refs.add(expResult);
        
        int commentId = commentController.addCourseNotesComment(cnId, comment, refs, userId);
        Comment result = commentController.getComment(commentId);
        
        assertEquals(comment.getUser().getUsername(), result.getUser().getUsername());
        assertEquals(comment.getBody(), result.getBody());
        assertEquals(commentId, result.getId());
    }
    
    /**
     * Test of addComment and getComment method, of class CommentController.
     * @throws databaseaccess.exceptions.ClassicDatabaseException
     * @throws databaseaccess.exceptions.ClassicNotFoundException
     */
    @Test(expected=ClassicNotFoundException.class)
    public void testAddCourseNotesCommentNotFoundException() throws ClassicDatabaseException, ClassicNotFoundException {
        CourseNotes cn = new CourseNotes("title", "url");
        Comment comment = new Comment( "body", false);
        List<Location> locations = new ArrayList<>();
        locations.add(new Location(20, 1, 2, 3, 4));
        CourseNotesReference expResult = new CourseNotesReference(cn, locations);
        List<CourseNotesReference> refs = new ArrayList<>();
        refs.add(expResult);
        
        commentController.addCourseNotesComment(0, comment, refs, userId);
    }
    
    /**
     * Test of addComment and getComment method, of class CommentController.
     * @throws databaseaccess.exceptions.ClassicDatabaseException
     * @throws databaseaccess.exceptions.ClassicNotFoundException
     */
    @Test(expected=ClassicNotFoundException.class)
    public void testGetCommentNotFoundException() throws ClassicDatabaseException, ClassicNotFoundException {
        commentController.getComment(0);
    }
    
    /**
     * Test of getComment method, of class CommentController.
     * @throws databaseaccess.exceptions.ClassicDatabaseException
     * @throws databaseaccess.exceptions.ClassicNotFoundException
     */
    @Test
    public void testAddVAndGetideoComment() throws ClassicDatabaseException, ClassicNotFoundException, ClassicUnauthorizedException {
        Video video = new Video("title", "url", 20);
        int videoId = videoController.addVideo(courseId, video, userId);
        Comment comment = new Comment( "body", false);
        VideoReference videoRef = new VideoReference(video, 20);List<VideoReference> refs = new ArrayList<>();
        refs.add(videoRef);
        
        int commentId = commentController.addVideoComment(videoId, comment, refs, userId);
        Comment result = commentController.getComment(commentId);
        
        assertEquals(comment.getBody(), result.getBody());
        assertEquals(comment.getUser().getUsername(), result.getUser().getUsername());
        assertEquals(commentId, result.getId());
    }
    
    /**
     * Test of addComment and getComment method, of class CommentController.
     * @throws databaseaccess.exceptions.ClassicDatabaseException
     * @throws databaseaccess.exceptions.ClassicNotFoundException
     */
    @Test(expected=ClassicNotFoundException.class)
    public void testAddVideoCommentNotFoundException() throws ClassicDatabaseException, ClassicNotFoundException {
        Video video = new Video("title", "url", 20);
        Comment comment = new Comment( "body", false);
        VideoReference videoRef = new VideoReference(video, 20);
        
        List<VideoReference> refs = new ArrayList<>();
        refs.add(videoRef);
        commentController.addVideoComment(0, comment, refs, userId);
    }
    
    /**
     * Test of updateComment method, of class CommentController.
     * @throws databaseaccess.exceptions.ClassicDatabaseException
     * @throws databaseaccess.exceptions.ClassicNotFoundException
     */
    @Test
    public void testUpdateComment() throws ClassicDatabaseException, ClassicNotFoundException, ClassicUnauthorizedException {
        Video video = new Video("title", "url", 20);
        int videoId = videoController.addVideo(courseId, video, userId);
        Comment comment = new Comment( "body", false);
        VideoReference expResult = new VideoReference(video, videoId);
        List<VideoReference> refs = new ArrayList<>();
        refs.add(expResult);
        int commentId = commentController.addVideoComment(videoId, comment, refs, userId);
        comment = new Comment("body2", true);
        commentController.updateComment(commentId, comment, userId);
        Comment result = commentController.getComment(commentId);
        
        assertEquals(comment.getUser().getUsername(), result.getUser().getUsername());
        assertEquals(comment.getBody(), result.getBody());
        assertEquals(commentId, result.getId());
    }
    
    /**
     * Test of updateComment method, of class CommentController.
     * @throws databaseaccess.exceptions.ClassicDatabaseException
     * @throws databaseaccess.exceptions.ClassicNotFoundException
     */
    @Test(expected=ClassicNotFoundException.class)
    public void testUpdateCommentNotFoundException() throws ClassicDatabaseException, ClassicNotFoundException, ClassicUnauthorizedException {
        Video video = new Video("title", "url", 20);
        int videoId = videoController.addVideo(courseId, video, userId);
        Comment comment = new Comment( "body", false);
        VideoReference expResult = new VideoReference(video, videoId);
        List<VideoReference> refs = new ArrayList<>();
        refs.add(expResult);
        commentController.addVideoComment(videoId, comment, refs, userId);
        comment = new Comment("body2", false);
        commentController.updateComment(0, comment, userId);
    }
    
    /**
     * Test of deleteComment method, of class CommentController.
     * @throws databaseaccess.exceptions.ClassicDatabaseException
     * @throws databaseaccess.exceptions.ClassicNotFoundException
     */
    @Test(expected=ClassicNotFoundException.class)
    public void testDeleteComment() throws ClassicDatabaseException, ClassicNotFoundException, ClassicUnauthorizedException {
        Video video = new Video("title", "url", 20);
        int videoId = videoController.addVideo(courseId, video, userId);
        Comment comment = new Comment( "body", false);
        VideoReference expResult = new VideoReference(video, videoId);
        List<VideoReference> refs = new ArrayList<>();
        refs.add(expResult);
        int commentId = commentController.addVideoComment(videoId, comment, refs, userId);

        commentController.deleteComment(commentId, userId);

        commentController.getComment(commentId);
    }
    
    /**
     * Test of deleteComment method, of class CommentController.
     * @throws databaseaccess.exceptions.ClassicDatabaseException
     * @throws databaseaccess.exceptions.ClassicNotFoundException
     */
    @Test(expected=ClassicNotFoundException.class)
    public void testDeleteCommentNotFoundException() throws ClassicDatabaseException, ClassicNotFoundException, ClassicUnauthorizedException {
        commentController.deleteComment(0, userId);
    }

    /**
     * Test of addReplytoComment method, of class CommentController.
     * @throws databaseaccess.exceptions.ClassicDatabaseException
     * @throws databaseaccess.exceptions.ClassicNotFoundException
     */
    @Test
    public void testAddReplytoComment() throws ClassicDatabaseException, ClassicNotFoundException, ClassicUnauthorizedException {
        CourseNotes cn = new CourseNotes("title", "url");
        int cnId = courseNotesController.addCourseNotes(courseId, cn, userId);
        Comment comment = new Comment( "body", false);
        List<Location> locations = new ArrayList<>();
        locations.add(new Location(20, 1, 2, 3, 4));
        CourseNotesReference expResult = new CourseNotesReference(cn, locations);
        
        List<CourseNotesReference> refs = new ArrayList<>();
        refs.add(expResult);
        int commentId = commentController.addCourseNotesComment(cnId, comment, refs, userId);
        Reply reply = new Reply("body2");
        int replyId = commentController.addReplytoComment(commentId, reply, userId);
        Comment result = commentController.getComment(commentId);
        
        assertEquals(comment.getUser().getUsername(), result.getUser().getUsername());
        assertEquals(comment.getBody(), result.getBody());
        assertEquals(commentId, result.getId());
        assertEquals(reply.getBody(), result.getChildren().get(0).getBody());
        assertEquals(reply.getUser().getUsername(), result.getChildren().get(0).getUser().getUsername());
        assertEquals(replyId, result.getChildren().get(0).getId());
    }
    
    /**
     * Test of addReplytoComment method, of class CommentController.
     * @throws databaseaccess.exceptions.ClassicDatabaseException
     * @throws databaseaccess.exceptions.ClassicNotFoundException
     */
    @Test(expected=ClassicNotFoundException.class)
    public void testAddReplytoCommentNotFoundException() throws ClassicDatabaseException, ClassicNotFoundException, ClassicUnauthorizedException {
        CourseNotes cn = new CourseNotes("title", "url");
        int cnId = courseNotesController.addCourseNotes(courseId, cn, userId);
        Comment comment = new Comment( "body", false);
        List<Location> locations = new ArrayList<>();
        locations.add(new Location(20, 1, 2, 3, 4));
        CourseNotesReference expResult = new CourseNotesReference(cn, locations);
        
        List<CourseNotesReference> refs = new ArrayList<>();
        refs.add(expResult);
        commentController.addCourseNotesComment(cnId, comment, refs, userId);
        Reply reply = new Reply("body2");
        
        commentController.addReplytoComment(0, reply, userId);
    }

    /**
     * Test of addReplytoReply method, of class CommentController.
     * @throws databaseaccess.exceptions.ClassicDatabaseException
     * @throws databaseaccess.exceptions.ClassicNotFoundException
     */
    @Test
    public void testAddReplytoReply() throws ClassicDatabaseException, ClassicNotFoundException, ClassicUnauthorizedException {
        CourseNotes cn = new CourseNotes("title", "url");
        int cnId = courseNotesController.addCourseNotes(courseId, cn, userId);
        Comment comment = new Comment( "body", false);
        List<Location> locations = new ArrayList<>();
        locations.add(new Location(20, 1, 2, 3, 4));
        CourseNotesReference expResult = new CourseNotesReference(cn, locations);
        
        List<CourseNotesReference> refs = new ArrayList<>();
        refs.add(expResult);
        int commentId = commentController.addCourseNotesComment(cnId, comment, refs, userId);
        
        Reply rep = new Reply("body2");
        int replyId0 = commentController.addReplytoComment(commentId, rep, userId);
        Reply reply = new Reply("body3");
        int replyId = commentController.addReplytoReply(replyId0, reply, userId);
        Comment result = commentController.getComment(commentId);
        
        assertEquals(comment.getUser().getUsername(), result.getUser().getUsername());
        assertEquals(comment.getBody(), result.getBody());
        assertEquals(commentId, result.getId());
        assertEquals(rep.getBody(), result.getChildren().get(0).getBody());
        assertEquals(rep.getUser().getUsername(), result.getChildren().get(0).getUser().getUsername());
        assertEquals(replyId0, result.getChildren().get(0).getId());
        assertEquals(reply.getBody(), result.getChildren().get(0).getChildren().get(0).getBody());
        assertEquals(reply.getUser().getUsername(), result.getChildren().get(0).getChildren().get(0).getUser().getUsername());
        assertEquals(replyId, result.getChildren().get(0).getChildren().get(0).getId());
    }
    
    /**
     * Test of addReplytoReply method, of class CommentController.
     * @throws databaseaccess.exceptions.ClassicDatabaseException
     * @throws databaseaccess.exceptions.ClassicNotFoundException
     */
    @Test(expected=ClassicNotFoundException.class)
    public void testAddReplytoReplyNotFoundException() throws ClassicDatabaseException, ClassicNotFoundException, ClassicUnauthorizedException {
        CourseNotes cn = new CourseNotes("title", "url");
        int cnId = courseNotesController.addCourseNotes(courseId, cn, userId);
        Comment comment = new Comment( "body", false);
        List<Location> locations = new ArrayList<>();
        locations.add(new Location(20, 1, 2, 3, 4));
        CourseNotesReference expResult = new CourseNotesReference(cn, locations);
        
        List<CourseNotesReference> refs = new ArrayList<>();
        refs.add(expResult);
        int commentId = commentController.addCourseNotesComment(cnId, comment, refs, userId);
        Reply rep = new Reply("body2");
        commentController.addReplytoComment(commentId, rep, userId);
        Reply reply = new Reply("body3");
        
        commentController.addReplytoReply(0, reply, userId);
    }

    /**
     * Test of updateReply method, of class CommentController.
     * @throws databaseaccess.exceptions.ClassicDatabaseException
     * @throws databaseaccess.exceptions.ClassicNotFoundException
     */
    @Test
    public void testUpdateReply() throws ClassicDatabaseException, ClassicNotFoundException, ClassicUnauthorizedException {
        CourseNotes cn = new CourseNotes("title", "url");
        int cnId = courseNotesController.addCourseNotes(courseId, cn, userId);
        Comment comment = new Comment( "body", false);
        List<Location> locations = new ArrayList<>();
        locations.add(new Location(20, 1, 2, 3, 4));
        
        CourseNotesReference expResult = new CourseNotesReference(cn, locations);
        List<CourseNotesReference> refs = new ArrayList<>();
        refs.add(expResult);
        int commentId = commentController.addCourseNotesComment(cnId, comment, refs, userId);
        
        Reply reply = new Reply("body2");
        int replyId = commentController.addReplytoComment(commentId, reply, userId);
        reply = new Reply("body5");
        reply.setUser(comment.getUser());
        commentController.updateReply(replyId, reply, userId);
        Comment result = commentController.getComment(commentId);
        
        assertEquals(comment.getUser().getUsername(), result.getUser().getUsername());
        assertEquals(comment.getBody(), result.getBody());
        assertEquals(commentId, result.getId());
        assertEquals(reply.getBody(), result.getChildren().get(0).getBody());
        assertEquals(reply.getUser().getUsername(), result.getChildren().get(0).getUser().getUsername());
        assertEquals(replyId, result.getChildren().get(0).getId());
    }
    
    /**
     * Test of updateReply method, of class CommentController.
     * @throws databaseaccess.exceptions.ClassicDatabaseException
     * @throws databaseaccess.exceptions.ClassicNotFoundException
     */
    @Test(expected=ClassicNotFoundException.class)
    public void testUpdateReplyNotFoundException() throws ClassicDatabaseException, ClassicNotFoundException, ClassicUnauthorizedException {
        CourseNotes cn = new CourseNotes("title", "url");
        int cnId = courseNotesController.addCourseNotes(courseId, cn, userId);
        Comment comment = new Comment( "body", false);
        List<Location> locations = new ArrayList<>();
        locations.add(new Location(20, 1, 2, 3, 4));
        
        CourseNotesReference expResult = new CourseNotesReference(cn, locations);
        List<CourseNotesReference> refs = new ArrayList<>();
        refs.add(expResult);
        int commentId = commentController.addCourseNotesComment(cnId, comment, refs, userId);
        
        Reply reply = new Reply("body2");
        commentController.addReplytoComment(commentId, reply, userId);
        reply = new Reply("body5");
        
        commentController.updateReply(0, reply, userId);
    }

    /**
     * Test of deleteReply method, of class CommentController.
     * @throws databaseaccess.exceptions.ClassicDatabaseException
     * @throws databaseaccess.exceptions.ClassicNotFoundException
     */
    @Test
    public void testDeleteReply() throws ClassicDatabaseException, ClassicNotFoundException, ClassicUnauthorizedException {
        CourseNotes cn = new CourseNotes("title", "url");
        int cnId = courseNotesController.addCourseNotes(courseId, cn, userId);
        Comment comment = new Comment( "body", false);
        List<Location> locations = new ArrayList<>();
        locations.add(new Location(20, 1, 2, 3, 4));
        CourseNotesReference expResult = new CourseNotesReference(cn, locations);
        
        List<CourseNotesReference> refs = new ArrayList<>();
        refs.add(expResult);
        int commentId = commentController.addCourseNotesComment(cnId, comment, refs, userId);
        
        Reply reply = new Reply("body2");
        int replyId = commentController.addReplytoComment(commentId, reply, userId);
        commentController.deleteReply(replyId, userId);
        Comment result = commentController.getComment(commentId);
        
        assertEquals(true, result.getChildren().isEmpty());
    }
    
    /**
     * Test of deleteReply method, of class CommentController.
     * @throws databaseaccess.exceptions.ClassicDatabaseException
     * @throws databaseaccess.exceptions.ClassicNotFoundException
     */
    @Test(expected=ClassicNotFoundException.class)
    public void testDeleteReplyNotFoundException() throws ClassicDatabaseException, ClassicNotFoundException, ClassicUnauthorizedException {
        commentController.deleteReply(0, userId);
    }
    
    /**
     * Test of getReply method, of class CommentController.
     * @throws databaseaccess.exceptions.ClassicDatabaseException
     * @throws databaseaccess.exceptions.ClassicNotFoundException
     */
    @Test
    public void testGetReply() throws ClassicDatabaseException, ClassicNotFoundException, ClassicUnauthorizedException {
        CourseNotes cn = new CourseNotes("title", "url");
        int cnId = courseNotesController.addCourseNotes(courseId, cn, userId);
        Comment comment = new Comment( "body", false);
        List<Location> locations = new ArrayList<>();
        locations.add(new Location(20, 1, 2, 3, 4));
        CourseNotesReference expResult = new CourseNotesReference(cn, locations);
        
        List<CourseNotesReference> refs = new ArrayList<>();
        refs.add(expResult);
        int commentId = commentController.addCourseNotesComment(cnId, comment, refs, userId);
        
        Reply reply = new Reply("body2");
        int replyId = commentController.addReplytoComment(commentId, reply, userId);
        
        Reply result = commentController.getReply(replyId);
        
        assertEquals(replyId, result.getId());
        assertEquals(reply.getUser().getUsername(), result.getUser().getUsername());
        assertEquals(reply.getBody(), result.getBody());
        assertEquals(true, result.getChildren().isEmpty());
    }
    
    /**
     * Test of getReply method, of class CommentController.
     * @throws databaseaccess.exceptions.ClassicDatabaseException
     * @throws databaseaccess.exceptions.ClassicNotFoundException
     */
    @Test(expected=ClassicNotFoundException.class)
    public void testGetReplyNotFoundException() throws ClassicDatabaseException, ClassicNotFoundException {
        commentController.getReply(0);
    }
    
    @Test
    public void testVoteComment() throws ClassicDatabaseException, ClassicNotFoundException {
    	CourseNotes cn = new CourseNotes("title", "url");
        int cnId = courseNotesController.addCourseNotes(courseId, cn, userId);
        Comment comment = new Comment( "body", false);
        List<Location> locations = new ArrayList<>();
        locations.add(new Location(20, 1, 2, 3, 4));
        CourseNotesReference expResult = new CourseNotesReference(cn, locations);
        List<CourseNotesReference> refs = new ArrayList<>();
        refs.add(expResult);
        
        int commentId = commentController.addCourseNotesComment(cnId, comment, refs, userId);
        commentController.voteComment(commentId, true, userId);
        Comment result = commentController.getComment(commentId);
        
        assertEquals(comment.getUser().getUsername(), result.getUser().getUsername());
        assertEquals(comment.getBody(), result.getBody());
        assertEquals(commentId, result.getId());
        assertEquals(1, result.getUpvotes());
    }
    
    @Test(expected=ClassicUnauthorizedException.class)
    public void testVoteCommentUnauthorizedException() throws ClassicDatabaseException, ClassicNotFoundException {
    	CourseNotes cn = new CourseNotes("title", "url");
        int cnId = courseNotesController.addCourseNotes(courseId, cn, userId);
        Comment comment = new Comment( "body", false);
        List<Location> locations = new ArrayList<>();
        locations.add(new Location(20, 1, 2, 3, 4));
        CourseNotesReference expResult = new CourseNotesReference(cn, locations);
        List<CourseNotesReference> refs = new ArrayList<>();
        refs.add(expResult);
        
        int commentId = commentController.addCourseNotesComment(cnId, comment, refs, userId);
        commentController.voteComment(commentId, true, userId);
        commentController.voteComment(commentId, true, userId);
    }

    @Test
    public void testSetApprovedComment() throws ClassicDatabaseException, ClassicNotFoundException {
    	CourseNotes cn = new CourseNotes("title", "url");
        int cnId = courseNotesController.addCourseNotes(courseId, cn, userId);
        Comment comment = new Comment( "body", false);
        List<Location> locations = new ArrayList<>();
        locations.add(new Location(20, 1, 2, 3, 4));
        CourseNotesReference expResult = new CourseNotesReference(cn, locations);
        List<CourseNotesReference> refs = new ArrayList<>();
        refs.add(expResult);
        
        int commentId = commentController.addCourseNotesComment(cnId, comment, refs, userId);
        commentController.setApprovedComment(commentId, true, userId);
        Comment result = commentController.getComment(commentId);
        
        assertEquals(comment.getUser().getUsername(), result.getUser().getUsername());
        assertEquals(comment.getBody(), result.getBody());
        assertEquals(commentId, result.getId());
        assertEquals(true, result.isApproved());
    }

    @Test
    public void testVoteReply() throws ClassicDatabaseException, ClassicNotFoundException {
    	CourseNotes cn = new CourseNotes("title", "url");
        int cnId = courseNotesController.addCourseNotes(courseId, cn, userId);
        Comment comment = new Comment( "body", false);
        List<Location> locations = new ArrayList<>();
        locations.add(new Location(20, 1, 2, 3, 4));
        CourseNotesReference expResult = new CourseNotesReference(cn, locations);
        
        List<CourseNotesReference> refs = new ArrayList<>();
        refs.add(expResult);
        int commentId = commentController.addCourseNotesComment(cnId, comment, refs, userId);
        
        Reply reply = new Reply("body2");
        int replyId = commentController.addReplytoComment(commentId, reply, userId);
        commentController.voteReply(replyId, true, userId);
        
        Reply result = commentController.getReply(replyId);
        
        assertEquals(replyId, result.getId());
        assertEquals(reply.getUser().getUsername(), result.getUser().getUsername());
        assertEquals(reply.getBody(), result.getBody());
        assertEquals(1, reply.getUpvotes());
        assertEquals(true, result.getChildren().isEmpty());
    }
    
    @Test(expected=ClassicUnauthorizedException.class)
    public void testVoteReplyUnautorizedException() throws ClassicDatabaseException, ClassicNotFoundException {
    	CourseNotes cn = new CourseNotes("title", "url");
        int cnId = courseNotesController.addCourseNotes(courseId, cn, userId);
        Comment comment = new Comment( "body", false);
        List<Location> locations = new ArrayList<>();
        locations.add(new Location(20, 1, 2, 3, 4));
        CourseNotesReference expResult = new CourseNotesReference(cn, locations);
        
        List<CourseNotesReference> refs = new ArrayList<>();
        refs.add(expResult);
        int commentId = commentController.addCourseNotesComment(cnId, comment, refs, userId);
        
        Reply reply = new Reply("body2");
        int replyId = commentController.addReplytoComment(commentId, reply, userId);
        commentController.voteReply(replyId, true, userId);
        commentController.voteReply(replyId, true, userId);
    }

    @Test
    public void testSetApprovedReply() throws ClassicDatabaseException, ClassicNotFoundException {
    	CourseNotes cn = new CourseNotes("title", "url");
        int cnId = courseNotesController.addCourseNotes(courseId, cn, userId);
        Comment comment = new Comment( "body", false);
        List<Location> locations = new ArrayList<>();
        locations.add(new Location(20, 1, 2, 3, 4));
        CourseNotesReference expResult = new CourseNotesReference(cn, locations);
        
        List<CourseNotesReference> refs = new ArrayList<>();
        refs.add(expResult);
        int commentId = commentController.addCourseNotesComment(cnId, comment, refs, userId);
        
        Reply reply = new Reply("body2");
        int replyId = commentController.addReplytoComment(commentId, reply, userId);
        commentController.setApprovedReply(replyId, true, userId);
        
        Reply result = commentController.getReply(replyId);
        
        assertEquals(replyId, result.getId());
        assertEquals(reply.getUser().getUsername(), result.getUser().getUsername());
        assertEquals(reply.getBody(), result.getBody());
        assertEquals(true, reply.isApproved());
        assertEquals(true, result.getChildren().isEmpty());
    }
}
