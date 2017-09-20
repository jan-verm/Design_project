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
import models.CourseNotesReference;

/**
 *
 * @author Juta
 */
public interface CourseNotesReferenceControllerInterface {
    /**
     * add a course notes reference
     * @param courseNotesId id of the courseNotes the Comment refers to
     * @param commentId the id of the Comment
     * @param courseNotesRef reference to add
     * @return id of the reference
     * @throws databaseaccess.exceptions.ClassicDatabaseException
     * @throws databaseaccess.exceptions.ClassicNotFoundException
     */
    public int addCourseNotesReference(int courseNotesId, int commentId, CourseNotesReference courseNotesRef, int userId)
            throws ClassicDatabaseException, ClassicNotFoundException, ClassicUnauthorizedException;

    /**
     * get all course notes references that refer to the course notes itself
     * @param courseNotesId id of the course notes
     * @param commentId id of the comment
     * @return all the references that belong to this course notes in this comment
     * @throws databaseaccess.exceptions.ClassicDatabaseException
     * @throws databaseaccess.exceptions.ClassicNotFoundException
     */
    public List<CourseNotesReference> getSelfCourseNotesReferences(int courseNotesId, int commentId)  throws ClassicDatabaseException,ClassicNotFoundException;
    
    /**
     * get course notes reference
     * @param refId the id of the reference
     * @return the reference corresponding to given id
     * @throws databaseaccess.exceptions.ClassicDatabaseException
     * @throws databaseaccess.exceptions.ClassicNotFoundException
     */
    public CourseNotesReference getCourseNotesReference(int refId) throws ClassicDatabaseException,ClassicNotFoundException;
    
    /**
     * get all course notes references
     * @param commentId the id of the Comment
     * @return all the references this Comment refers to
     * @throws databaseaccess.exceptions.ClassicDatabaseException
     * @throws databaseaccess.exceptions.ClassicNotFoundException
     */
    public List<CourseNotesReference> getCourseNotesReferences(int commentId) throws ClassicDatabaseException,ClassicNotFoundException;
    
    /**
     * update course notes reference
     * @param commentId id of the comment
     * @param referenceId the id of the reference
     * @param courseNotesRef updated reference
     * @throws databaseaccess.exceptions.ClassicDatabaseException
     * @throws databaseaccess.exceptions.ClassicNotFoundException
     */
    public void updateCourseNotesReference(int commentId, int referenceId, CourseNotesReference courseNotesRef, int userId)
            throws ClassicDatabaseException,ClassicNotFoundException, ClassicUnauthorizedException;
    
    /**
     * delete course notes reference
     * @param commentId id of the comment
     * @param referenceId the id of the reference
     * @throws databaseaccess.exceptions.ClassicDatabaseException
     * @throws databaseaccess.exceptions.ClassicNotFoundException
     */
    public void deleteCourseNotesReference(int commentId, int referenceId, int userId)
            throws ClassicDatabaseException,ClassicNotFoundException, ClassicUnauthorizedException;
    
    /**
     *
     * @param videoID
     * @return int array with {courseID, lectureID}
     * @throws ClassicDatabaseException
     * @throws ClassicNotFoundException
     */
    public int[] getParents(int videoID) throws ClassicDatabaseException,ClassicNotFoundException;
}
