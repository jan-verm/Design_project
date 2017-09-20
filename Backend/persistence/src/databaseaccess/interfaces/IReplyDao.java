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
import models.Reply;

/**
 *
 * @author jorsi
 */
public interface IReplyDao {
    /**
     * Add a reply to the database. 
     * @param parentID ID of the comment or reply to which the reply is added.
     * @param reply The reply to add
     * @return ID of the added comment.
     * @throws databaseaccess.exceptions.ClassicDatabaseException
     * @throws databaseaccess.exceptions.ClassicNotFoundException
     */
    public int addReply(int parentID, Reply reply) throws ClassicDatabaseException, ClassicNotFoundException;
    
    /**
     * Delete a reply from the database.
     * @param replyID ID of the reply to delete.
     * @throws databaseaccess.exceptions.ClassicDatabaseException
     * @throws databaseaccess.exceptions.ClassicNotFoundException
     */
    public void deleteReply(int replyID) throws ClassicDatabaseException,ClassicNotFoundException;
    
    /**
     * Gets the owner (writer) of the reply.
     * @param commentID Id of the comment.
     * @return A user object.
     * @throws ClassicDatabaseException
     * @throws ClassicNotFoundException
     */
    public User getOwner(int commentID) throws ClassicDatabaseException, ClassicNotFoundException;
    
    /**
     * Get a list replies from the database.
     * @param parentID ID of the comment or reply to which the reply is added.
     * @return List of replies to the parent.
     * @throws databaseaccess.exceptions.ClassicDatabaseException
     * @throws databaseaccess.exceptions.ClassicNotFoundException
     */
    public List<Reply> getReplies(int parentID) throws ClassicDatabaseException,ClassicNotFoundException;  
    
    /**
     * Get a reply from the database.
     * @param replyID ID of the reply.
     * @return Reply with specified ID.
     * @throws databaseaccess.exceptions.ClassicDatabaseException
     * @throws databaseaccess.exceptions.ClassicNotFoundException
     */
    public Reply getReply(int replyID) throws ClassicDatabaseException,ClassicNotFoundException;  
    
    /**
     * Update a specified reply in the database.
     * @param reply Edited reply.
     * @throws databaseaccess.exceptions.ClassicDatabaseException
     * @throws databaseaccess.exceptions.ClassicNotFoundException
     */
    public void updateReply(Reply reply) throws ClassicDatabaseException,ClassicNotFoundException;   
    
    /**
     * Deletes all reply entries in the database.
     * @throws databaseaccess.exceptions.ClassicDatabaseException
     */
    public void cleanTable() throws ClassicDatabaseException;
    
    /**
     * Add a vote on a reply.
     * @param userID Id the of the user that casts the vote.
     * @param replyID ID of the reply.
     * @param up boolean that indicates if the vote is an upvote (TRUE) or downvote (FALSE).
     * @throws ClassicUnauthorizedException
     * @throws ClassicDatabaseException
     * @throws ClassicNotFoundException
     */
    public void vote(int userID ,int replyID, boolean up) throws ClassicUnauthorizedException,ClassicDatabaseException,ClassicNotFoundException; ;
    
    /**
     * Approve or unapprove a reply.
     * @param replyID Id of the reply to approve.
     * @param approve Boolean that indicates if the reply is approved or unapproved
     * @throws ClassicDatabaseException
     * @throws ClassicNotFoundException
     */
    public void approve(int replyID, boolean approve) throws ClassicDatabaseException,ClassicNotFoundException; 
}
