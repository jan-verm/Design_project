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
import models.CourseNotes;
import models.CourseNotesReference;

/**
 *
 * @author Juta
 */
public interface CourseNotesControllerInterface {
    /**
     * Add course notes
     * @param parentId id of course or lecture this video belongs to
     * @param courseNotes the courseNotes to add
     * @return the id of the created courseNotes
     * @throws databaseaccess.exceptions.ClassicDatabaseException
     * @throws databaseaccess.exceptions.ClassicNotFoundException
     */
    public int addCourseNotes(int parentId, CourseNotes courseNotes, int userId)
            throws ClassicDatabaseException, ClassicNotFoundException , ClassicUnauthorizedException;
    
    /** 
     * Method to get a courseNotes
     * @param courseNotesId the id of the courseNotes
     * @return The courseNotes object corresponding to the given courseNotes name
     * @throws databaseaccess.exceptions.ClassicDatabaseException
     * @throws databaseaccess.exceptions.ClassicNotFoundException
     */
    public CourseNotes getCourseNotes(int courseNotesId) throws ClassicDatabaseException,ClassicNotFoundException;
    
    /**
     * Method to get a list of all the courseNotess in the database
     * @return A list of all the courseNotess in the database
     * @throws databaseaccess.exceptions.ClassicDatabaseException
     * @throws databaseaccess.exceptions.ClassicNotFoundException
     */
    public List<CourseNotes> getAllCourseNotes(int parentId) throws ClassicDatabaseException, ClassicNotFoundException;
    
    /**
     * update course notes
     * @param courseNotesId id of the courseNotes
     * @param courseNotes updated courseNotes
     * @throws databaseaccess.exceptions.ClassicDatabaseException
     * @throws databaseaccess.exceptions.ClassicNotFoundException
     */
    public void updateCourseNotes(int courseNotesId, CourseNotes courseNotes, int userId)
            throws ClassicDatabaseException,ClassicNotFoundException, ClassicUnauthorizedException;
    
    /**
     * delete course notes
     * @param courseNotesId id of the courseNotes
     * @throws databaseaccess.exceptions.ClassicDatabaseException
     * @throws databaseaccess.exceptions.ClassicNotFoundException
     */
    public void deleteCourseNotes(int courseNotesId, int userId)
            throws ClassicDatabaseException,ClassicNotFoundException, ClassicUnauthorizedException;
    
    /**
     * Creates a 'converted' pdf file, which is a new pdf file
     * with all its comments added to it with iText.
     * 
     * @param courseNotes The file you want to annotate
     * @param comments The list of comments for the courseNotes file (this should already be checked)
     * @param annotations whether or not annotations should be added to the pdf
     * @param questions whether or not questions should be added to the pdf
     * @return url of the converted pdf file
     */
    public String addAllPdfComments(CourseNotes courseNotes, List<Comment> comments, boolean annotations, boolean questions);
    
    /**
     * Adds a single comment to the output pdf file 'courseNote'
     * 
     * @param outputFile the url of the new pdf, which will be returned to the user
     * @param comment the comment that iText should add
     * @param cnref the reference to be added
     */
    public void addPdfComment(String outputFile, Comment comment, CourseNotesReference cnref);
}
