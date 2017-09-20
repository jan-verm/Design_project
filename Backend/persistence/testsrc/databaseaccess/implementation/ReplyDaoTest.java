/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package databaseaccess.implementation;

import courses.Role;
import courses.User;
import databaseaccess.exceptions.ClassicDatabaseException;
import databaseaccess.exceptions.ClassicNotFoundException;
import java.util.List;
import models.Comment;
import models.Reply;
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
public class ReplyDaoTest {

    private CommentDao commentDao;
    private ReplyDao replyDao;
    private ReplyDao fakedao;
    private UserDao userDao;
    
    private User user;

    public ReplyDaoTest() {
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
        replyDao = new ReplyDao(ReplyDao.TEST_DATABASE_CONFIG);
        fakedao = new ReplyDao("fakedbconfig.properties");
        
        userDao = new UserDao(userDao.TEST_DATABASE_CONFIG);
        
        user = new User("testreplyuser", Role.NONE, "password");
        userDao.addUser(user);        
    }

    @After
    public void tearDown() throws ClassicDatabaseException {
        commentDao.cleanTable();
        replyDao.cleanTable();
        userDao.cleanTable();
    }

    /**
     * Test of addReply method, of class ReplyDao.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testAddReply() throws Exception {
        Comment comment = new Comment("testAddCommentBody", false);
        comment.setUser(user);
        int commentID = commentDao.addComment(comment);
        Reply expResult = new Reply("testAddReplyBody");
        expResult.setUser(user);
        int expResultID = replyDao.addReply(commentID, expResult);

        Reply result = replyDao.getReply(expResultID);
        assertEquals(expResultID, result.getId());
        assertEquals(expResult.getBody(), result.getBody());
        assertEquals(expResult.getUser().getId(), result.getUser().getId());
        assertEquals(expResult.isApproved(), result.isApproved());
        assertEquals(expResult.getUpvotes(), result.getUpvotes());
    }

    @Test(expected = ClassicDatabaseException.class)
    public void testAddReplyDBException() throws Exception {
        Reply expResult = new Reply("testAddReplyBody");
        fakedao.addReply(0, expResult);
    }
    
        @Test(expected = ClassicNotFoundException.class)
    public void testAddRepliesNFException() throws ClassicDatabaseException, ClassicNotFoundException {
        Reply expResult = new Reply("testAddReplyBody");
        replyDao.addReply(0, expResult);
    }

    /**
     * Test of deleteReply method, of class ReplyDao.
     *
     * @throws java.lang.Exception
     */
    @Test(expected = ClassicNotFoundException.class)
    public void testDeleteReply() throws Exception {
        Comment comment = new Comment("testAddCommentBody", false);
        comment.setUser(user);
        int commentID = commentDao.addComment(comment);
        Reply expResult = new Reply("testDeleteReply");
        expResult.setUser(user);
        int expResultID = replyDao.addReply(commentID, expResult);

        Reply result = replyDao.getReply(expResultID);
        assertEquals(expResultID, result.getId());
        assertEquals(expResult.getBody(), result.getBody());
        assertEquals(expResult.getUser().getId(), result.getUser().getId());
        assertEquals(expResult.isApproved(), result.isApproved());
        assertEquals(expResult.getUpvotes(), result.getUpvotes());
        replyDao.deleteReply(expResultID);
        Reply reply = replyDao.getReply(expResultID);
    }

    @Test(expected = ClassicDatabaseException.class)
    public void testDeleteReplyDBException() throws ClassicDatabaseException, ClassicNotFoundException {
        fakedao.deleteReply(0);
    }

    @Test(expected = ClassicNotFoundException.class)
    public void testDeleteReplyNotFoundException() throws ClassicDatabaseException, ClassicNotFoundException {
        replyDao.deleteReply(0);
    }

    /**
     * Test of getReplies method, of class ReplyDao.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testGetReplies() throws Exception {
        Comment comment = new Comment("testAddCommentBody", false);
        comment.setUser(user);
        int commentID = commentDao.addComment(comment);
        Reply expResult = new Reply("testGetRepliesBody");
        expResult.setUser(user);
        int expResultID = replyDao.addReply(commentID, expResult);

        List<Reply> results = replyDao.getReplies(commentID);
        Reply result = null;
        for (Reply reply : results) {
            if (reply.getId() == expResultID) {
                result = reply;
            }
        }
        assertEquals(expResultID, result.getId());
        assertEquals(expResult.getBody(), result.getBody());
        assertEquals(expResult.getUser().getId(), result.getUser().getId());
        assertEquals(expResult.isApproved(), result.isApproved());
        assertEquals(expResult.getUpvotes(), result.getUpvotes());
    }

    @Test(expected = ClassicDatabaseException.class)
    public void testGetRepliesDBException() throws ClassicDatabaseException, ClassicNotFoundException {
        fakedao.getReplies(0);
    }
    
    @Test(expected = ClassicNotFoundException.class)
    public void testGetRepliesNFException() throws ClassicDatabaseException, ClassicNotFoundException {
        replyDao.getReplies(0);
    }

    /**
     * Test of getReply method, of class ReplyDao.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testGetReply() throws Exception {
        Comment comment = new Comment("testAddCommentBody", false);
        comment.setUser(user);
        int commentID = commentDao.addComment(comment);
        Reply expResult = new Reply("testAddReplyBody");
        expResult.setUser(user);
        int expResultID = replyDao.addReply(commentID, expResult);

        Reply result = replyDao.getReply(expResultID);
        assertEquals(expResultID, result.getId());
        assertEquals(expResult.getBody(), result.getBody());
        assertEquals(expResult.getUser().getId(), result.getUser().getId());
        assertEquals(expResult.isApproved(), result.isApproved());
        assertEquals(expResult.getUpvotes(), result.getUpvotes());
    }

    @Test(expected = ClassicDatabaseException.class)
    public void testGetReplyDBException() throws ClassicDatabaseException, ClassicNotFoundException {
        fakedao.getReply(0);
    }

    @Test(expected = ClassicNotFoundException.class)
    public void testGetReplyNotFoundException() throws ClassicDatabaseException, ClassicNotFoundException {
        replyDao.getReply(0);
    }

    /**
     * Test of updateReply method, of class ReplyDao.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testUpdateReply() throws Exception {
        Comment comment = new Comment("testAddCommentBody", false);
        comment.setUser(user);
        int commentID = commentDao.addComment(comment);
        Reply expResult = new Reply("testAddReplyBody");
        expResult.setUser(user);
        int expResultID = replyDao.addReply(commentID, expResult);

        Reply result = replyDao.getReply(expResultID);
        assertEquals(expResultID, result.getId());
        assertEquals(expResult.getBody(), result.getBody());
        assertEquals(expResult.getUser().getId(), result.getUser().getId());
        assertEquals(expResult.isApproved(), result.isApproved());
        assertEquals(expResult.getUpvotes(), result.getUpvotes());

        expResult.setBody("testUpdateReply");        
        replyDao.updateReply(expResult);
        Reply result2 = replyDao.getReply(expResultID);
        assertEquals(expResultID, result2.getId());
        assertEquals(expResult.getBody(), result2.getBody());
    }

    @Test(expected = ClassicDatabaseException.class)
    public void testUpdateReplyDBException() throws ClassicDatabaseException, ClassicNotFoundException {
        fakedao.updateReply(new Reply(""));
    }

    @Test(expected = ClassicNotFoundException.class)
    public void testUpdateReplyNotFoundException() throws ClassicDatabaseException, ClassicNotFoundException {
        replyDao.updateReply(new Reply(""));
    }

    @Test
    public void testIsReply() throws Exception {
        Comment comment = new Comment("testAddCommentBody", false);
        comment.setUser(user);
        int commentID = commentDao.addComment(comment);
        Reply expResult = new Reply("testAddReplyBody");
        expResult.setUser(user);
        int expResultID = replyDao.addReply(commentID, expResult);

        boolean result = replyDao.isReply(expResultID);
        assertTrue(result);
    }

    @Test(expected = ClassicDatabaseException.class)
    public void testIsReplyDBException() throws ClassicDatabaseException, ClassicNotFoundException {
        fakedao.isReply(0);
    }
        
    /**
     * Test of cleanTable method, of class ReplyDao.
     *
     * @throws java.lang.Exception
     */
    @Test(expected = ClassicNotFoundException.class)
    public void testCleanTable() throws Exception {
        Comment comment = new Comment("testAddCommentBody", false);
        comment.setUser(user);
        int commentID = commentDao.addComment(comment);
        Reply expResult = new Reply("testDeleteReply");
        expResult.setUser(user);
        int expResultID = replyDao.addReply(commentID, expResult);

        Reply result = replyDao.getReply(expResultID);
        assertEquals(expResultID, result.getId());
        assertEquals(expResult.getBody(), result.getBody());
        assertEquals(expResult.getUser().getId(), result.getUser().getId());
        assertEquals(expResult.isApproved(), result.isApproved());
        assertEquals(expResult.getUpvotes(), result.getUpvotes());
        replyDao.cleanTable();
        Reply reply = replyDao.getReply(expResultID);
    }    
    
    @Test(expected = ClassicDatabaseException.class)
    public void testCleanTableDBException() throws ClassicDatabaseException, ClassicNotFoundException {
        fakedao.cleanTable();
    }

    @Test
    public void testGetOwner() throws Exception {
        Comment comment = new Comment("testAddCommentBody", false);
        comment.setUser(user);
        int commentID = commentDao.addComment(comment);
        Reply reply = new Reply("testDeleteReply");
        reply.setUser(user);
        int replyID = replyDao.addReply(commentID, reply);        
        
        User result = replyDao.getOwner(replyID);
        
        assertEquals(user.getId(), result.getId());
        assertEquals(user.getPassword(), result.getPassword());
        assertEquals(user.getRole(), result.getRole());
        assertEquals(user.getUsername(), result.getUsername()); 
    }

    @Test
    public void testVote() throws Exception {
        Comment comment = new Comment("testVote", false);
        comment.setUser(user);
        int commentID = commentDao.addComment(comment);
        Reply expResult = new Reply("testVote");
        expResult.setUser(user);
        int expResultID = replyDao.addReply(commentID, expResult);
        
        replyDao.vote(user.getId(), expResultID, true);        
        Reply result = replyDao.getReply(expResultID);
        assertEquals(expResultID, result.getId());
        assertEquals(expResult.getBody(), result.getBody());
        assertEquals(expResult.getUser().getId(), result.getUser().getId());        
        assertEquals(1, result.getUpvotes());  
        
        replyDao.vote(user.getId(), expResultID, false);
        
        Reply result2 = replyDao.getReply(expResultID);
        assertEquals(expResultID, result.getId());
        assertEquals(expResult.getBody(), result2.getBody());
        assertEquals(expResult.getUser().getId(), result2.getUser().getId());        
        assertEquals(0, result2.getUpvotes());
    }
    
    

    @Test
    public void testApprove() throws Exception {
        Comment comment = new Comment("testVote", false);
        comment.setUser(user);
        int commentID = commentDao.addComment(comment);
        Reply expResult = new Reply("testVote");
        expResult.setUser(user);
        int expResultID = replyDao.addReply(commentID, expResult);
        
        replyDao.approve(expResultID, true);        
        Reply result = replyDao.getReply(expResultID);
        assertEquals(expResultID, result.getId());
        assertEquals(expResult.getBody(), result.getBody());
        assertEquals(expResult.getUser().getId(), result.getUser().getId());   
        assertTrue(result.isApproved());
        
        
        replyDao.approve(expResultID, false);        
        Reply result2 = replyDao.getReply(expResultID);
        assertEquals(expResultID, result.getId());
        assertEquals(expResult.getBody(), result2.getBody());
        assertEquals(expResult.getUser().getId(), result2.getUser().getId());        
        assertFalse(result2.isApproved());
    }

}
