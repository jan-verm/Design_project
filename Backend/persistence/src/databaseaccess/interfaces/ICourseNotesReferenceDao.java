/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package databaseaccess.interfaces;

import databaseaccess.exceptions.ClassicDatabaseException;
import databaseaccess.exceptions.ClassicNotFoundException;
import java.util.List;
import models.CourseNotesReference;

/**
 * Database access object for coursenotesreferences.
 * @author jorsi
 */
public interface ICourseNotesReferenceDao {
    
    /**
     * Add a reference between a comment and coursenotes to the database.
     * @param courseNotesID ID of the courseNotes.
     * @param commentID ID of the comment.
     * @param courseNotesRef
     * @return Id of the coursenotesreference
     * @throws databaseaccess.exceptions.ClassicDatabaseException
     * @throws databaseaccess.exceptions.ClassicNotFoundException
     */
    public int addCourseNotesReference(int courseNotesID, int commentID, CourseNotesReference courseNotesRef) throws ClassicDatabaseException,ClassicNotFoundException;
   
    /**
     * Delete a reference between a comment and coursenotes to the database.
     * @param refID ID of the reference
     * @throws databaseaccess.exceptions.ClassicDatabaseException
     * @throws databaseaccess.exceptions.ClassicNotFoundException
     */
    public void deleteCourseNotesReference(int refID) throws ClassicDatabaseException,ClassicNotFoundException;
    
    /**
     * Get a videoReference with given id
     * @param refID
     * @return Specified videoReference
     * @throws ClassicDatabaseException
     * @throws ClassicNotFoundException
     */
    public CourseNotesReference getCourseNotesReference(int refID) throws ClassicDatabaseException,ClassicNotFoundException;
    
    /**
     * Get references between a comment and coursenotes to the database.
     * @param courseNotesID ID of the courseNotes.
     * @param commentID ID of the comment.
     * @return List of coursenotesreference.
     * @throws databaseaccess.exceptions.ClassicDatabaseException
     * @throws databaseaccess.exceptions.ClassicNotFoundException
     */
    public List<CourseNotesReference> getSelfCourseNotesReferences(int courseNotesID, int commentID) throws ClassicDatabaseException,ClassicNotFoundException;
    
    /**
     * Get all reference from a comment to videos from the database.
     * @param commentID ID of the comment.
     * @return List of coursenotesreference.
     * @throws databaseaccess.exceptions.ClassicDatabaseException
     * @throws databaseaccess.exceptions.ClassicNotFoundException
     */
    public List<CourseNotesReference> getCourseNotesReferences(int commentID) throws ClassicDatabaseException, ClassicNotFoundException;
    
    /**
     * Update a reference between a comment and coursenotes to the database.
     * @param courseNotesRef Edited coursenotesreference.
     * @throws databaseaccess.exceptions.ClassicDatabaseException
     * @throws databaseaccess.exceptions.ClassicNotFoundException
     */
    public void updateCourseNotesReference(CourseNotesReference courseNotesRef) throws ClassicDatabaseException,ClassicNotFoundException;

    /**
     * Deletes all coursenotesreference entries in the database
     * @throws databaseaccess.exceptions.ClassicDatabaseException
     */
    public void cleanTable() throws ClassicDatabaseException;
    
    /**
     * 
     * @param courseNotesID id of the course notes
     * @return int array with {courseID, lectureID}
     * @throws ClassicNotFoundException
     * @throws ClassicDatabaseException 
     */
    public int[] getCourseAndLectureForCourseNotes(int courseNotesID) throws ClassicNotFoundException, ClassicDatabaseException;
}
