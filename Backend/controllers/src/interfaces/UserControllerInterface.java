/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package interfaces;

import courses.User;
import databaseaccess.exceptions.ClassicDatabaseException;
import databaseaccess.exceptions.ClassicNotFoundException;

import java.util.List;

/**
 *
 * @author Juta
 */
public interface UserControllerInterface {

    /**
     * add user
     * @param user user object
     * @return user id
     * @throws ClassicDatabaseException
     */
    public int addUser(User user) throws ClassicDatabaseException;
    
    /**
     * Allow a # for an lti user.
     * @param user user object
     * @return user_id
     * @throws ClassicDatabaseException
     */
    public int addLTIUser(User user) throws ClassicDatabaseException;
    
    /**
     * get user based on username
     * @param username name of the user
     * @return user object
     * @throws ClassicDatabaseException
     * @throws ClassicNotFoundException
     */
    public User getUser(String username) throws ClassicDatabaseException, ClassicNotFoundException;
    
    /**
     * get user based on userid
     * @param userid id of the user
     * @return user object
     * @throws ClassicDatabaseException
     * @throws ClassicNotFoundException
     */
    public User getUser(int userid) throws ClassicDatabaseException, ClassicNotFoundException;
    
    /**
     * Subscribe to a course
     * @param userId id of the user that wants to subscribe
     * @param courseId id of the course the user wants to subscribe to
     * @throws ClassicDatabaseException
     * @throws ClassicNotFoundException
     */
    public void subscribeCourse(int userId, int courseId) throws ClassicDatabaseException, ClassicNotFoundException;
    
    /**
     *
     * Unsubscribe from a course
     * @param userId id of the user that wants to unsubscribe
     * @param courseId id of the course the user wants to unsubscribe from
     * @throws ClassicDatabaseException
     * @throws ClassicNotFoundException
     */
    public void unSubscribeCourse(int userId, int courseId) throws ClassicDatabaseException, ClassicNotFoundException;
    
    /**
     * get all users
     * @return list of users
     * @throws ClassicDatabaseException
     * @throws ClassicNotFoundException 
     */
    public List<User> getUsers() throws ClassicDatabaseException, ClassicNotFoundException;
    
    /**
     * update a user
     * @param updateUserid the users id to update
     * @param user the updated user
     * @param authenticatedUserId the id of the user that is logged in
     * @throws ClassicDatabaseException
     * @throws ClassicNotFoundException
     */
    public void updateUser(int updateUserid, User user, int authenticatedUserId) throws ClassicDatabaseException, ClassicNotFoundException;
    
    /**
     * delete a user
     * @param deleteUserid the id of the user that needs to be deleted
     * @param authenticatedUserId the id of the user that is logged in
     * @throws ClassicDatabaseException
     * @throws ClassicNotFoundException
     */
    public void deleteUser(int deleteUserid, int authenticatedUserId) throws ClassicDatabaseException, ClassicNotFoundException;
}
