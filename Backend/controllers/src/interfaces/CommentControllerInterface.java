/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package interfaces;

import databaseaccess.exceptions.ClassicDatabaseException;
import databaseaccess.exceptions.ClassicNotFoundException;
import databaseaccess.exceptions.ClassicUnauthorizedException;
import java.util.List;
import models.Comment;
import models.CourseNotesReference;
import models.Reply;
import models.VideoReference;

/**
 *
 * @author Juta
 */
public interface CommentControllerInterface {

    /**
     * Method to add a comment
     * @param videoId the id of the video
     * @param comment the comment to add
     * @param selfVideoRef video reference for the video where the comment is created
     * @return the id of the created comment
     * @throws databaseaccess.exceptions.ClassicDatabaseException
     * @throws databaseaccess.exceptions.ClassicNotFoundException
     */
    public int addVideoComment(int videoId, Comment comment, List<VideoReference> selfVideoRefs, int userId)  
            throws ClassicDatabaseException, ClassicNotFoundException;
    
    /**
     * Method to add a comment
     * @param courseNotesId the id of the video
     * @param comment the comment to add
     * @param selfCourseNotesRef course notes reference for the course notes where the comment is created
     * @return the id of the created comment
     * @throws databaseaccess.exceptions.ClassicDatabaseException
     * @throws databaseaccess.exceptions.ClassicNotFoundException
     */
    public int addCourseNotesComment(int courseNotesId, Comment comment, List<CourseNotesReference> selfCourseNotesRefs, int userId)
            throws ClassicDatabaseException, ClassicNotFoundException;
    
    /**
     * Method to get a comment
     * @param commentId the id of the comment
     * @return the comment
     * @throws databaseaccess.exceptions.ClassicDatabaseException
     * @throws databaseaccess.exceptions.ClassicNotFoundException
     */
    public Comment getComment(int commentId) throws ClassicDatabaseException,ClassicNotFoundException;
    /**
     * Method to get all video comments
     * @param videoId the id of the video
     * @return all the comments in that video
     * @throws databaseaccess.exceptions.ClassicDatabaseException
     * @throws databaseaccess.exceptions.ClassicNotFoundException
     */
    public List<Comment> getVideoComments(int videoId) throws ClassicDatabaseException,ClassicNotFoundException;
    
    /**
     * Method to get all course notes comments
     * @param courseNotesId id of the course notes
     * @return all the comments in these course notes
     * @throws ClassicDatabaseException
     * @throws ClassicNotFoundException
     */
    public List<Comment> getCourseNotesComments(int courseNotesId) throws ClassicDatabaseException,ClassicNotFoundException;
    
    /**
     * Method to get update a comment
     * @param commentId the id of the comment
     * @param comment the updated comment object
     * @throws databaseaccess.exceptions.ClassicDatabaseException
     * @throws databaseaccess.exceptions.ClassicNotFoundException
     */
    public void updateComment(int commentId, Comment comment, int userId)
            throws ClassicDatabaseException,ClassicNotFoundException, ClassicUnauthorizedException;
    
    /**
     * Method to vote a comment
     * @param commentId the id of the comment
     * @param upvote true if upvote, false if downvote
     * @param userid id of the user that votes
     * @throws ClassicDatabaseException
     * @throws ClassicNotFoundException
     */
    public void voteComment(int commentId, boolean upvote, int userid)throws ClassicDatabaseException,ClassicNotFoundException;
    
    /**
     * Method to approve a comment
     * @param commentId the id of the comment
     * @param approved true if approve, false if unapprove
     * @param userid id of the user that approves
     * @throws ClassicDatabaseException
     * @throws ClassicNotFoundException
     */
    public void setApprovedComment(int commentId, boolean approved, int userid) throws ClassicDatabaseException,ClassicNotFoundException;
    
    /**
     * Method to delete a comment
     * @param commentId the id of the comment
     * @throws databaseaccess.exceptions.ClassicDatabaseException
     * @throws databaseaccess.exceptions.ClassicNotFoundException
     */
    public void deleteComment(int commentId, int userId)
            throws ClassicDatabaseException,ClassicNotFoundException, ClassicUnauthorizedException;
    
    /**
     * Method to get all replies on a comment
     * @param commentId the id of the comment
     * @return all the replies for this comment
     * @throws ClassicDatabaseException
     * @throws ClassicNotFoundException
     */
    public List<Reply> getReplies(int commentId) throws ClassicDatabaseException, ClassicNotFoundException;
    
    /**
     * Method to get a reply
     * @param replyId id of the reply
     * @return reply object
     * @throws ClassicDatabaseException
     * @throws ClassicNotFoundException
     */
    public Reply getReply(int replyId) throws ClassicDatabaseException, ClassicNotFoundException;
    
    /**
     * Method to add a reply on a comment
     * @param commentId id of the comment
     * @param reply the reply to add
     * @return id of the reply
     * @throws ClassicDatabaseException
     * @throws ClassicNotFoundException
     */
    public int addReplytoComment(int commentId, Reply reply, int userId) throws ClassicDatabaseException,ClassicNotFoundException;
    
    /**
     * Method to add a reply on a reply
     * @param replyId id of the reply
     * @param reply the reply to add
     * @return id of the reply
     * @throws ClassicDatabaseException
     * @throws ClassicNotFoundException
     */
    public int addReplytoReply(int replyId, Reply reply, int userId) throws ClassicDatabaseException,ClassicNotFoundException;
    
    /**
     * Method to update a reply
     * @param replyId id of the reply
     * @param reply the updated reply
     * @throws ClassicDatabaseException
     * @throws ClassicNotFoundException
     */
    public void updateReply(int replyId, Reply reply, int userId)
            throws ClassicDatabaseException,ClassicNotFoundException, ClassicUnauthorizedException;
    
    /**
     * Method to vote a reply
     * @param replyId id of the reply
     * @param upvote true if upvote, false if downvote
     * @param userid id of the user that approves
     * @throws ClassicDatabaseException
     * @throws ClassicNotFoundException
     */
    public void voteReply(int replyId, boolean upvote, int userid) throws ClassicDatabaseException, ClassicNotFoundException;
    
    /**
     * Method to approve a reply
     * @param replyId id of the reply
     * @param approved true if approve, false if unapprove
     * @param userid id of the user that approves
     * @throws ClassicDatabaseException
     * @throws ClassicNotFoundException
     */
    public void setApprovedReply(int replyId, boolean approved, int userid) throws ClassicDatabaseException,ClassicNotFoundException;
    
    /**
     * Deletes a reply
     * @param replyId id of the reply
     * @throws ClassicDatabaseException
     * @throws ClassicNotFoundException
     */
    public void deleteReply(int replyId, int userId) throws
            ClassicDatabaseException,ClassicNotFoundException, ClassicUnauthorizedException;

}
