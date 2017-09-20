/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package databaseaccess.interfaces;

import courses.Course;
import courses.Role;
import courses.User;
import databaseaccess.exceptions.ClassicDatabaseException;
import databaseaccess.exceptions.ClassicNotFoundException;
import databaseaccess.exceptions.ClassicNameTakenException;
import java.util.List;

/**
 * Data Access Object for users. 
 * @author jorsi
 */
public interface IUserDao {
    
    /**
     * Add a user to the database.
     * @param user A user.
     * @return The ID of the user.
     * @throws ClassicDatabaseException
     * @throws ClassicNameTakenException
     */
    public int addUser(User user) throws ClassicDatabaseException, ClassicNameTakenException;
    
    /**
     * Delete a user from the database.
     * @param userID the userId of the user to delete.
     * @throws ClassicDatabaseException
     * @throws ClassicNotFoundException
     */
    public void deleteUser(int userID) throws ClassicDatabaseException, ClassicNotFoundException;
    
    /**
     * Get a user from the database.
     * @param userID userID of the user to get.
     * @return The user object.
     * @throws ClassicDatabaseException
     * @throws ClassicNotFoundException
     */
    public User getUser(int userID) throws ClassicDatabaseException, ClassicNotFoundException;
    
    /**
     * Get a user from the database.
     * @param username Username of the user to get.
     * @return The user object.
     * @throws ClassicDatabaseException
     * @throws ClassicNotFoundException
     */
    public User getUser(String username) throws ClassicDatabaseException,ClassicNotFoundException;
    
    /**
     * Get all the user of a specific role.
     * @param role Role of the users to get.
     * @return List of user objects with the specified role.
     * @throws ClassicDatabaseException
     */
    public List<User> getUsers(Role role) throws ClassicDatabaseException;
    
    /**
     * Get all the users in the database.
     * @return List of all the user objects 
     * @throws ClassicDatabaseException
     */
    public List<User> getUsers() throws ClassicDatabaseException;
    
    /**
     * Update a user in the database. 
     * This method can be used to change the role of a user or the username.
     * @param user The user object to be updated.
     * @throws ClassicDatabaseException
     * @throws ClassicNotFoundException
     */
    public void updateUser(User user) throws ClassicDatabaseException, ClassicNotFoundException;
    
    /**
     * Change the password of the user.
     * @param userID Id of the user of which the password has to be changed.
     * @param newPassword The new password value.
     * @throws ClassicDatabaseException
     * @throws ClassicNotFoundException
     */
    public void updateUserPassword(int userID, String newPassword) throws ClassicDatabaseException, ClassicNotFoundException;
    
    /**
     * Check if a username is taken.
     * @param username Username to check.
     * @return A boolean that is TRUE if the specified username is taken .
     * and FALSE id the username is available.
     * @throws ClassicDatabaseException
     */
    public boolean isTaken(String username) throws ClassicDatabaseException;
    
    /**
     * Subsribe A user to a course.
     * @param userID Id of the user to subscribe.
     * @param courseID Id of the course to subscribe the user to.
     * @throws ClassicDatabaseException
     * @throws ClassicNotFoundException
     */
    public void subscribeCourse(int userID, int courseID) throws ClassicDatabaseException, ClassicNotFoundException;
    
    /**
     * Unsubscribe a user from a course.
     * @param userID Id of the user to unsubscribe.
     * @param courseID Id of the course to unsubscribe the user from.
     * @throws ClassicDatabaseException
     * @throws ClassicNotFoundException
     */
    public void unsubscribeCourse(int userID, int courseID) throws ClassicDatabaseException, ClassicNotFoundException;
    
    /**
     * Get a list of course objects to which a user is subscribed.
     * @param userID Id of the user.
     * @return List of course objects.
     * @throws ClassicDatabaseException
     * @throws ClassicNotFoundException
     */
    public List<Course> getSubscriptions(int userID) throws ClassicDatabaseException, ClassicNotFoundException;
    
    /**
     * Deletes all elements from the user table in the database.
     * @throws ClassicDatabaseException
     */
    public void cleanTable() throws ClassicDatabaseException;
}
