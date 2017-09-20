/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package databaseaccess.interfaces;

import courses.User;
import databaseaccess.exceptions.ClassicDatabaseException;
import databaseaccess.exceptions.ClassicNotFoundException;
import java.util.List;
import models.CourseNotes;


/**
 * Database access object for coursenotes.
 * @author Jorsi Grammens
 */
public interface ICourseNotesDao {
    
    /**
     * Adds coursenotes to the database.
     * @param parentID Id of the course or lecture the coursenote belongs to.
     * @param courseNotes Coursenotes to add.
     * @return ID of the added coursenotes.
     * @throws databaseaccess.exceptions.ClassicDatabaseException
     * @throws ClassicNotFoundException
     */
    public int addCourseNotes(int parentID, CourseNotes courseNotes) throws ClassicDatabaseException, ClassicNotFoundException;
    
    /**
     * Delete coursenotes from the database.
     * @param courseNotesID ID of the coursenotes to delete. 
     * @throws databaseaccess.exceptions.ClassicDatabaseException 
     * @throws databaseaccess.exceptions.ClassicNotFoundException 
     */
    public void deleteCourseNotes(int courseNotesID) throws ClassicDatabaseException,ClassicNotFoundException;
    
    /**
     * Get the coursenotes of a specific course or lecture from the database.
     * @param parentID Id of the course or lecture
     * @return List of coursenotes.
     * @throws databaseaccess.exceptions.ClassicDatabaseException
     * @throws databaseaccess.exceptions.ClassicNotFoundException
     */
    public List<CourseNotes> getParentCourseNotes(int parentID) throws ClassicDatabaseException, ClassicNotFoundException;
    
    /**
     * Get the coursenotes from the database.
     * @return List of coursenotes.
     * @throws databaseaccess.exceptions.ClassicDatabaseException
     */
    public List<CourseNotes> getAllCourseNotes() throws ClassicDatabaseException;
    
    /**
     * Get specified coursenotes from the database.
     * @param courseNotesID ID of the coursenotes to get.
     * @return Coursenotes with the specified ID.
     * @throws databaseaccess.exceptions.ClassicDatabaseException
     * @throws databaseaccess.exceptions.ClassicNotFoundException
     */
    public CourseNotes getCourseNotes(int courseNotesID) throws ClassicDatabaseException,ClassicNotFoundException;
    
    /**
     * Get the owner of the coursenotes from the Classic database.
     * @param courseNotesID Id of the coursenotes of which the owner is wanted
     * @return User object that represents the wanted owner.
     * @throws ClassicDatabaseException
     * @throws ClassicNotFoundException
     */
    public User getOwner(int courseNotesID) throws ClassicDatabaseException, ClassicNotFoundException;
    
    /**
     * Update specified coursenotes in the database.
     * @param courseNotes Edited coursenotes.
     * @throws databaseaccess.exceptions.ClassicDatabaseException
     * @throws databaseaccess.exceptions.ClassicNotFoundException
     */
    public void updateCourseNotes(CourseNotes courseNotes) throws ClassicDatabaseException,ClassicNotFoundException;

    /**
     * Deletes all coursenotes entries in the database
     * @throws databaseaccess.exceptions.ClassicDatabaseException
     */
    public void cleanTable() throws ClassicDatabaseException;
}
