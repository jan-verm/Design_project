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
import models.Reply;
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
 * @author Kevin Test from the controllers to the database
 */
public class CommentControllerIT {

    private VideoController videoController;
    private CourseNotesController courseNotesController;
    private CommentController commentController;
    private final String TEST_DATABASE_CONFIG = "testdbconfig.properties";
    private IVideoDao videodao;
    private ICourseNotesDao courseNotesdao;
    private ICommentDao commentdao;
    private int userId;
    
    //courses 
    private CourseController courseControler;
    private ICourseDao coursedao;
    private ILectureDao lecturedao;
    private UserController userController;
    private IUserDao userdao;
    private IVideoReferenceDao videorefdao;
    private ICourseNotesReferenceDao coursenotesrefdao;
    private IReplyDao replydao;
    private int courseId;
    

    @Before
    public void setUp() throws ClassicDatabaseException, ClassicNotFoundException {
        commentdao = new CommentDao(TEST_DATABASE_CONFIG);
        videodao = new VideoDao(TEST_DATABASE_CONFIG);
        courseNotesdao = new CourseNotesDao(TEST_DATABASE_CONFIG);
        coursedao = new CourseDao(TEST_DATABASE_CONFIG);
        lecturedao = new LectureDao(TEST_DATABASE_CONFIG);
        userdao = new UserDao(TEST_DATABASE_CONFIG);
        videorefdao = new VideoReferenceDao(TEST_DATABASE_CONFIG);
        coursenotesrefdao = new CourseNotesReferenceDao(TEST_DATABASE_CONFIG);
        replydao = new ReplyDao(TEST_DATABASE_CONFIG);

        commentController = new CommentController(commentdao, replydao, videorefdao, coursenotesrefdao, userdao);
        videoController = new VideoController(videodao, coursedao, userdao);
        courseNotesController = new CourseNotesController(courseNotesdao, coursedao, userdao, "/var/www/classic/resources/courses");
        courseControler = new CourseController(coursedao, lecturedao, videodao, courseNotesdao, userdao, "/var/www/classic/resources/courses");
        userController = new UserController(userdao);
        
        // courses
        User u = new User("prof", Role.ADMIN, "password");
        userId = userController.addUser(u);
        Course c = new Course("course");
        courseId = courseControler.addCourse(c, userId);
    }

    @After
    public void tearDown() throws ClassicDatabaseException {
        videodao.cleanTable();
        courseNotesdao.cleanTable();
        commentdao.cleanTable();
        coursedao.cleanTable();
        userdao.cleanTable();
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
        
        //upvote another comment
        Comment comment2 = new Comment( "body", false);
        locations = new ArrayList<>();
        locations.add(new Location(10, 1, 2, 3, 4));
        CourseNotesReference expResult2 = new CourseNotesReference(cn, locations);
        List<CourseNotesReference> refs2 = new ArrayList<>();
        refs2.add(expResult);
        
        int commentId2 = commentController.addCourseNotesComment(cnId, comment2, refs2, userId);
        commentController.voteComment(commentId2, true, userId);
        Comment result2 = commentController.getComment(commentId2);
        
        assertEquals(comment2.getUser().getUsername(), result2.getUser().getUsername());
        assertEquals(comment2.getBody(), result2.getBody());
        assertEquals(commentId2, result2.getId());
        assertEquals(1, result2.getUpvotes());
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
        assertEquals(1, result.getUpvotes());
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
    
    @Test//(expected=ClassicUnauthorizedException.class)
    public void testVoteReplyUnautorizedException2() throws ClassicDatabaseException, ClassicNotFoundException {
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
        /*commentController.voteReply(replyId, true, userId);
        try {
            commentController.voteReply(replyId, true, userId);
        } catch (ClassicUnauthorizedException e) {
            
        }*/
        
        //commentController.voteReply(replyId, false, userId);
        commentController.voteReply(replyId, true, userId);
        commentController.voteReply(replyId, false, userId);
                
        commentController.voteReply(replyId, false, userId);

        //commentController.voteReply(replyId, false, userId);

        /*Reply result = commentController.getReply(replyId);
        
        assertEquals(replyId, result.getId());
        assertEquals(reply.getUser().getUsername(), result.getUser().getUsername());
        assertEquals(reply.getBody(), result.getBody());
        assertEquals(-1, result.getUpvotes());
        assertEquals(true, result.getChildren().isEmpty());*/
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
        assertEquals(true, result.isApproved());
        assertEquals(true, result.getChildren().isEmpty());
    }
}
