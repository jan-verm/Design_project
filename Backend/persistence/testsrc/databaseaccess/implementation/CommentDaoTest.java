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
public class CommentDaoTest {
    
    CourseDao courseDao;
    private CommentDao commentDao;
    private VideoDao videoDao;
    private CourseNotesDao courseNotesDao;
    private VideoReferenceDao videoReferenceDao;
    private CourseNotesReferenceDao courseNotesReferenceDao;
    private UserDao userDao;
    
    private CommentDao fakedao;
    
    private User user;
    
    public CommentDaoTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() throws ClassicDatabaseException {
        commentDao = new CommentDao(CommentDao.TEST_DATABASE_CONFIG);
        videoDao = new VideoDao(VideoDao.TEST_DATABASE_CONFIG);
        courseNotesDao = new CourseNotesDao(CourseNotesDao.TEST_DATABASE_CONFIG);
        videoReferenceDao = new VideoReferenceDao(VideoReferenceDao.TEST_DATABASE_CONFIG);
        courseNotesReferenceDao = new CourseNotesReferenceDao(CourseNotesReferenceDao.TEST_DATABASE_CONFIG);
        courseDao = new CourseDao(CourseDao.TEST_DATABASE_CONFIG);
        userDao = new UserDao(UserDao.TEST_DATABASE_CONFIG);
        fakedao = new CommentDao("fakedbconfig.properties");
        
        user = new User("commenttestuser", Role.NONE,"password");
        userDao.addUser(user);
    }
    
    @After
    public void tearDown() throws ClassicDatabaseException {
        commentDao.cleanTable();
        videoDao.cleanTable();
        courseNotesDao.cleanTable();
        videoReferenceDao.cleanTable();
        courseNotesReferenceDao.cleanTable();
        courseDao.cleanTable();
        userDao.cleanTable();
    }

    /**
     * Test of addComment method, of class CommentDao.
     * @throws java.lang.Exception
     */
    @Test
    public void testAddComment() throws Exception {
        Comment expResult = new Comment("testAddCourseNotesBody", false);
        expResult.setUser(user);
        int expResultID = commentDao.addComment(expResult);
        Comment result = commentDao.getComment(expResultID);
        assertEquals(expResultID, result.getId());
        assertEquals(expResult.getBody(), result.getBody());
        assertEquals(expResult.getUser().getId(), result.getUser().getId());
        assertEquals(expResult.getCreationTime(), result.getCreationTime());
        assertEquals(expResult.isApproved(), result.isApproved());
        assertEquals(expResult.getUpvotes(), result.getUpvotes());         
        assertEquals(expResult.isQuestion(), result.isQuestion());  
    }
    
    @Test(expected=ClassicDatabaseException.class)
    public void testAddCommentDBException() throws ClassicDatabaseException {
        Comment expResult = new Comment("testAddCourseNotesBody", false);
        expResult.setUser(user);
        fakedao.addComment(expResult);
    }

    /**
     * Test of deleteComment method, of class CommentDao.
     * @throws java.lang.Exception
     */
    @Test(expected = ClassicNotFoundException.class)
    public void testDeleteComment() throws Exception {
        Comment expResult = new Comment("testDeleteCommentBody", false);
        expResult.setUser(user);
        int expResultID = commentDao.addComment(expResult);
        Comment result = commentDao.getComment(expResultID);
        assertEquals(expResultID, result.getId());
        assertEquals(expResult.getBody(), result.getBody());
        assertEquals(expResult.getUser().getId(), result.getUser().getId());
        assertEquals(expResult.getCreationTime(), result.getCreationTime());
        assertEquals(expResult.isApproved(), result.isApproved());
        assertEquals(expResult.getUpvotes(), result.getUpvotes());         
        assertEquals(expResult.isQuestion(), result.isQuestion());  
        commentDao.deleteComment(expResultID);
        Comment comment = commentDao.getComment(expResultID);
    }
    
    @Test(expected=ClassicDatabaseException.class)
    public void testDeleteCommentDBException() throws ClassicDatabaseException, ClassicNotFoundException {
        fakedao.deleteComment(0);
    }
    
    @Test(expected=ClassicNotFoundException.class)
    public void testDeleteCommentNotFoundException() throws ClassicDatabaseException, ClassicNotFoundException {
        commentDao.deleteComment(0);
    }

    /**
     * Test of getComment method, of class CommentDao.
     */
    @Test
    public void testGetComment() throws Exception {
        Comment expResult = new Comment("testGetCommentBody", false);
        expResult.setUser(user);
        int expResultID = commentDao.addComment(expResult);
        Comment result = commentDao.getComment(expResultID);
        assertEquals(expResultID, result.getId());
        assertEquals(expResult.getBody(), result.getBody());
        assertEquals(expResult.getUser().getId(), result.getUser().getId());
        assertEquals(expResult.getCreationTime(), result.getCreationTime());
        assertEquals(expResult.isApproved(), result.isApproved());
        assertEquals(expResult.getUpvotes(), result.getUpvotes());      
        assertEquals(expResult.isQuestion(), result.isQuestion());  
    }
    
    @Test(expected=ClassicDatabaseException.class)
    public void testGetCommentDBException() throws ClassicDatabaseException, ClassicNotFoundException {
        fakedao.getComment(0);
    }
    
    @Test(expected=ClassicNotFoundException.class)
    public void testGetCommentNotFoundException() throws ClassicDatabaseException, ClassicNotFoundException {
        commentDao.getComment(0);
    }

    /**
     * Test of getVideoComments method, of class CommentDao.
     * @throws java.lang.Exception
     */
    @Test
    public void testGetVideoComments() throws Exception {
        Course course = new Course("test");
        course.setOwner(user);
        int parentID = courseDao.addCourse(course);
        Video video = new Video("getVideo", "getVideoUrl", 100);
        int videoID = videoDao.addVideo(parentID, video);
        Comment expResult = new Comment("testGetVideoCommentsBody", true);
        expResult.setUser(user);
        int expResultID = commentDao.addComment(expResult);        
        VideoReference videoReference = new VideoReference(video, 50);
        videoReference.setVisible(true);
        videoReferenceDao.addVideoReference(videoID, expResultID, videoReference);        
        List<Comment> results = commentDao.getVideoComments(videoID);
        Comment result = null;
        for (Comment r : results) {
            if (r.getId() == expResultID) {
                result = r;
            }
        }
        assertEquals(expResultID, result.getId());
        assertEquals(expResult.getBody(), result.getBody());
        assertEquals(expResult.getUser().getId(), result.getUser().getId());
        assertEquals(expResult.getCreationTime(), result.getCreationTime());
        assertEquals(expResult.isApproved(), result.isApproved());
        assertEquals(expResult.getUpvotes(), result.getUpvotes());               
        assertEquals(expResult.isQuestion(), result.isQuestion());   
    }
    
    @Test(expected=ClassicDatabaseException.class)
    public void testGetVideoCommentsDBException() throws ClassicDatabaseException, ClassicNotFoundException {
        fakedao.getVideoComments(0);
    }
    
    @Test(expected=ClassicNotFoundException.class)
    public void testGetVideoCommentsNotFoundException() throws ClassicDatabaseException, ClassicNotFoundException {
        commentDao.getVideoComments(0);
    }

    /**
     * Test of getCourseNotesComments method, of class CommentDao.
     * @throws java.lang.Exception
     */
    @Test
    public void testGetCourseNotesComments() throws Exception {
        Course course = new Course("test");
        course.setOwner(user);
        int parentID = courseDao.addCourse(course);
        CourseNotes courseNotes = new CourseNotes("getCourseNotes", "getCourseNotesUrl");
        int coursenotesID = courseNotesDao.addCourseNotes(parentID, courseNotes);
        Comment expResult = new Comment("testGetCourseNotesCommentsBody", true);
        expResult.setUser(user);
        int expResultID = commentDao.addComment(expResult);        
        List<Location> locations = new ArrayList<>();
        locations.add(new Location(5, 10, 20, 15, 5));
        CourseNotesReference courseNotesReference = new CourseNotesReference(courseNotes,locations);
        courseNotesReference.setVisible(true);
        courseNotesReferenceDao.addCourseNotesReference(coursenotesID, expResultID, courseNotesReference);        
        List<Comment> results = commentDao.getCourseNotesComments(coursenotesID);
        Comment result = null;
        for (Comment r : results) {
            if (r.getId() == expResultID) {
                result = r;
            }
        }
        assertEquals(expResultID, result.getId());
        assertEquals(expResult.getBody(), result.getBody());
        assertEquals(expResult.getUser().getId(), result.getUser().getId());
        assertEquals(expResult.getCreationTime(), result.getCreationTime());
        assertEquals(expResult.isApproved(), result.isApproved());
        assertEquals(expResult.getUpvotes(), result.getUpvotes());          
        assertEquals(expResult.isQuestion(), result.isQuestion());   
    }
    
    @Test(expected=ClassicDatabaseException.class)
    public void testGetCourseNotesCommentsDBException() throws ClassicDatabaseException, ClassicNotFoundException {
        fakedao.getCourseNotesComments(0);
    }
    
    @Test(expected=ClassicNotFoundException.class)
    public void testGetCourseNotesCommentsNotFoundException() throws ClassicDatabaseException, ClassicNotFoundException {
        commentDao.getCourseNotesComments(0);
    }

    @Test
    public void testIsComment() throws Exception {
        Comment expResult = new Comment("testGetCommentBody", false);
        expResult.setUser(user);
        int expResultID = commentDao.addComment(expResult);            
        boolean result = commentDao.isComment(expResultID);
        assertTrue(result);
    }

    @Test(expected = ClassicDatabaseException.class)
    public void testIsCommentDBException() throws ClassicDatabaseException, ClassicNotFoundException {
        fakedao.isComment(0);
    }
    
    /**
     * Test of updateComment method, of class CommentDao.
     * @throws java.lang.Exception
     */
    @Test
    public void testUpdateComment() throws Exception {
        Comment expResult = new Comment("testAddCommentBody",false);
        expResult.setUser(user);
        int expResultID = commentDao.addComment(expResult);
        Comment result = commentDao.getComment(expResultID);
        assertEquals(expResultID, result.getId());
        assertEquals(expResult.getBody(), result.getBody());
        assertEquals(expResult.getCreationTime(), result.getCreationTime());            
        assertEquals(expResult.isQuestion(), result.isQuestion());  
        expResult.setId(expResultID); 
        
        expResult.setBody("testUpdateCommentBody");
        expResult.setQuestion(true);
        commentDao.updateComment(expResult);
        Comment result2 = commentDao.getComment(expResultID);
        assertEquals(expResultID, result2.getId());
 
        assertEquals(expResult.getBody(), result2.getBody());
        assertEquals(expResult.getCreationTime(), result2.getCreationTime());              
        assertEquals(expResult.isQuestion(), result2.isQuestion());  
    }
    
    @Test(expected=ClassicDatabaseException.class)
    public void testUpdateCommentDBException() throws ClassicDatabaseException, ClassicNotFoundException {
        fakedao.updateComment(new Comment("", true));
    }
    
    @Test(expected=ClassicNotFoundException.class)
    public void testUpdateCommentNotFoundException() throws ClassicDatabaseException, ClassicNotFoundException {
        commentDao.updateComment(new Comment("", true));
    }

    /**
     * Test of cleanTable method, of class CommentDao.
     * @throws java.lang.Exception
     */
    @Test(expected = ClassicNotFoundException.class)
    public void testCleanTable() throws Exception {
        Comment expResult = new Comment("testCleanTableBody",false);
        expResult.setUser(user);
        int expResultID = commentDao.addComment(expResult);
        commentDao.cleanTable();
        Comment results = commentDao.getComment(expResultID);
    }
    
    @Test(expected=ClassicDatabaseException.class)
    public void testCleanTableDBException() throws ClassicDatabaseException, ClassicNotFoundException {
        fakedao.cleanTable();
    }
    
    @Test
    public void testGetOwner() throws Exception {
        Comment comment = new Comment("testGetOwner", false);
        comment.setUser(user);
        int commentID = commentDao.addComment(comment);              
        
        User result = commentDao.getOwner(commentID);
        
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
        commentDao.getOwner(0);
    }
    
    @Test
    public void testVote() throws Exception {
        Comment expResult = new Comment("testVote", false);
        expResult.setUser(user);
        int expResultID = commentDao.addComment(expResult);        
        
        commentDao.vote(user.getId(), expResultID, true);        
        Comment result = commentDao.getComment(expResultID);
        assertEquals(expResultID, result.getId());
        assertEquals(expResult.getBody(), result.getBody());
        assertEquals(expResult.getUser().getId(), result.getUser().getId());        
        assertEquals(1, result.getUpvotes());  
        
        commentDao.vote(user.getId(), expResultID, false);
        
        
        Comment result2 = commentDao.getComment(expResultID);
        assertEquals(expResultID, result2.getId());
        assertEquals(expResult.getBody(), result2.getBody());
        assertEquals(expResult.getUser().getId(), result2.getUser().getId());        
        assertEquals(0, result2.getUpvotes());
        
        commentDao.vote(user.getId(), expResultID, true);        
        
        commentDao.cleanTable();
        Comment expResult2 = new Comment("testVote2", false);
        expResult2.setUser(user);
        int expResultID2 = commentDao.addComment(expResult2);        
        
        commentDao.vote(user.getId(), expResultID2, false);  
        
        Comment result3 = commentDao.getComment(expResultID2);
        assertEquals(expResultID2, result3.getId());
        assertEquals(expResult2.getBody(), result3.getBody());
        assertEquals(expResult2.getUser().getId(), result3.getUser().getId());        
        assertEquals(-1, result3.getUpvotes());
        
        commentDao.vote(user.getId(), expResultID2, true);  
        
        Comment result4 = commentDao.getComment(expResultID2);
        assertEquals(expResultID2, result4.getId());
        assertEquals(expResult2.getBody(), result4.getBody());
        assertEquals(expResult2.getUser().getId(), result4.getUser().getId());        
        assertEquals(0, result4.getUpvotes());  
    }
    
    @Test(expected = ClassicUnauthorizedException.class)
    public void testVoteUnException() throws Exception {
        Comment expResult = new Comment("testVoteUn", false);
        expResult.setUser(user);
        int expResultID = commentDao.addComment(expResult);        
        
        commentDao.vote(user.getId(), expResultID, true);        
        Comment result = commentDao.getComment(expResultID);
        assertEquals(expResultID, result.getId());
        assertEquals(expResult.getBody(), result.getBody());
        assertEquals(expResult.getUser().getId(), result.getUser().getId());        
        assertEquals(1, result.getUpvotes());  
        
        commentDao.vote(user.getId(), expResultID, true);
    }
    
    @Test(expected=ClassicDatabaseException.class)
    public void testVoteDBException() throws ClassicDatabaseException, ClassicNotFoundException {
        fakedao.vote(0, 0, true);
    }
    
    @Test(expected=ClassicNotFoundException.class)
    public void testVoteNFException1() throws ClassicDatabaseException, ClassicNotFoundException {
        commentDao.vote(0, 0, true);
    }
    
    @Test(expected=ClassicNotFoundException.class)
    public void testVoteNFException2() throws ClassicDatabaseException, ClassicNotFoundException {        
        commentDao.vote(user.getId(), 0, false);
    }  

    @Test
    public void testApprove() throws Exception {
        Comment expResult = new Comment("testVote", false);
        expResult.setUser(user);
        int expResultID = commentDao.addComment(expResult);       
       
        
        commentDao.approve(expResultID, true);        
        Comment result = commentDao.getComment(expResultID);
        assertEquals(expResultID, result.getId());
        assertEquals(expResult.getBody(), result.getBody());
        assertEquals(expResult.getUser().getId(), result.getUser().getId());   
        assertTrue(result.isApproved());
        
        
        commentDao.approve(expResultID, false);        
        Comment result2 = commentDao.getComment(expResultID);
        assertEquals(expResultID, result.getId());
        assertEquals(expResult.getBody(), result2.getBody());
        assertEquals(expResult.getUser().getId(), result2.getUser().getId());        
        assertFalse(result2.isApproved());
    }
    
    @Test(expected=ClassicDatabaseException.class)
    public void testApproveDBException() throws ClassicDatabaseException, ClassicNotFoundException {
        fakedao.approve(0,true);
    }
    
    @Test(expected=ClassicNotFoundException.class)
    public void testApproveNFException1() throws ClassicDatabaseException, ClassicNotFoundException {
        commentDao.approve(0, true);
    }
}
