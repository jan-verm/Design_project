/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package databaseaccess.interfaces;

import courses.User;
import databaseaccess.exceptions.ClassicDatabaseException;
import databaseaccess.exceptions.ClassicNotFoundException;
import databaseaccess.exceptions.ClassicUnauthorizedException;
import java.util.List;
import models.Comment;

/**
 * Database access object for comments.
 * @author Jorsi Grammens
 */
public interface ICommentDao {
    
    /**
     * Add a comment to the database.
     * @param comment Comment to add.
     * @return ID of the added comment.
     * @throws databaseaccess.exceptions.ClassicDatabaseException
     */
    public int addComment(Comment comment) throws ClassicDatabaseException;
    
    /**
     * Delete a comment from the database.
     * @param commentID ID of the comment to delete.
     * @throws databaseaccess.exceptions.ClassicDatabaseException
     * @throws databaseaccess.exceptions.ClassicNotFoundException
     */
    public void deleteComment(int commentID) throws ClassicDatabaseException,ClassicNotFoundException;
    
    /**
     * Get a comment from the database.
     * @param commentID ID of a comment.
     * @return Comment with specified ID.
     * @throws databaseaccess.exceptions.ClassicDatabaseException
     * @throws databaseaccess.exceptions.ClassicNotFoundException
     */
    public Comment getComment(int commentID) throws ClassicDatabaseException,ClassicNotFoundException;  
    
    /**
     * Gets all comments on a certain video from the database.
     * @param videoID ID of a video.
     * @return List of comments on a video.
     * @throws databaseaccess.exceptions.ClassicDatabaseException
     * @throws databaseaccess.exceptions.ClassicNotFoundException
     */
    public List<Comment> getVideoComments(int videoID) throws ClassicDatabaseException, ClassicNotFoundException; 
    
    /**
     * Gets all comments on certain coursenotes from the database.
     * @param coursenotesID ID of coursenotes.
     * @return List of comments on coursenotes.
     * @throws databaseaccess.exceptions.ClassicDatabaseException
     * @throws databaseaccess.exceptions.ClassicNotFoundException
     */
    public List<Comment> getCourseNotesComments(int coursenotesID) throws ClassicDatabaseException, ClassicNotFoundException;     

    /**
     * Get the owner of the comment.
     * @param commentID Id of the comment for which the owner is wanted.
     * @return User object that represents the owner of the course.
     * @throws ClassicDatabaseException
     * @throws ClassicNotFoundException
     */
    public User getOwner(int commentID) throws ClassicDatabaseException, ClassicNotFoundException;
    
    /**
     * Update a specified comment in the database.
     * @param comment Edited comment.
     * @throws databaseaccess.exceptions.ClassicDatabaseException
     * @throws databaseaccess.exceptions.ClassicNotFoundException
     */
    public void updateComment(Comment comment) throws ClassicDatabaseException,ClassicNotFoundException;   
    
    /**
     * Deletes all comment entries in the database
     * @throws databaseaccess.exceptions.ClassicDatabaseException
     */
    public void cleanTable() throws ClassicDatabaseException;    
    
    /**
     * Upvote or downvote a comment
     * @param userID Id of the user casting the vote
     * @param commentID Id of the comment that is being voted on
     * @param upvote Boolean that indicates if the vote is an upvote(TRUE) or downvote(FALSE)
     * @throws ClassicUnauthorizedException Exception thrown if the user already voted
     * @throws ClassicDatabaseException 
     * @throws ClassicNotFoundException
     */
    public void vote(int userID ,int commentID, boolean upvote) throws ClassicUnauthorizedException,ClassicDatabaseException,ClassicNotFoundException;
    
    /**
     * Approves or unapprove a comment
     * @param commentID Id of the comment that is being approved
     * @param approve Boolean that indicates if the comment is being approved(TRUE) or unapproved(FALSE).
     * @throws ClassicDatabaseException
     * @throws ClassicNotFoundException
     */
    public void approve(int commentID, boolean approve) throws ClassicDatabaseException,ClassicNotFoundException;
}
