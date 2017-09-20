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
import models.VideoReference;

/**
 *
 * @author Juta
 */
public interface VideoReferenceControllerInterface {
    /**
     * add a video reference
     * @param videoId id of the video the comment refers to
     * @param commentId the id of the comment
     * @param videoRef reference to add
     * @return id of reference
     * @throws databaseaccess.exceptions.ClassicDatabaseException
     * @throws databaseaccess.exceptions.ClassicNotFoundException
     */
    public int addVideoReference(int videoId, int commentId, VideoReference videoRef, int userId)
            throws ClassicDatabaseException,ClassicNotFoundException, ClassicUnauthorizedException;

    /**
     * get a video reference
     * @param refId id of the reference
     * @return reference corresponding to this id
     * @throws databaseaccess.exceptions.ClassicDatabaseException
     * @throws databaseaccess.exceptions.ClassicNotFoundException
     */
    public VideoReference getVideoReference(int refId) throws ClassicDatabaseException,ClassicNotFoundException;
    
    /**
     * get all video references that refer to the given video
     * @param commentId id of the comment
     * @param videoId the id of the video
     * @return all the references that belong to this video in this comment 
     * @throws databaseaccess.exceptions.ClassicDatabaseException
     * @throws databaseaccess.exceptions.ClassicNotFoundException
     */
    public List<VideoReference> getSelfVideoReferences(int videoId, int commentId) throws ClassicDatabaseException,ClassicNotFoundException;
    
    /**
     * gat all video references in a comment
     * @param commentId the id of the comment
     * @return all the references this comment refers to
     * @throws databaseaccess.exceptions.ClassicDatabaseException
     * @throws databaseaccess.exceptions.ClassicNotFoundException
     */
    public List<VideoReference> getVideoReferences(int commentId) throws ClassicDatabaseException,ClassicNotFoundException;
    
    /**
     * update a video reference
     * @param commentId id of the comment
     * @param referenceId the id of the reference
     * @param videoRef updated reference
     * @throws databaseaccess.exceptions.ClassicDatabaseException
     * @throws databaseaccess.exceptions.ClassicNotFoundException
     */
    public void updateVideoReference(int commentId, int referenceId, VideoReference videoRef, int userId)
            throws ClassicDatabaseException,ClassicNotFoundException, ClassicUnauthorizedException;
    
    /**
     * delete a video reference
     * @param commentId id of the comment
     * @param referenceId the id of the reference
     * @throws databaseaccess.exceptions.ClassicDatabaseException
     * @throws databaseaccess.exceptions.ClassicNotFoundException
     */
    public void deleteVideoReference(int commentId, int referenceId, int userId)
            throws ClassicDatabaseException,ClassicNotFoundException, ClassicUnauthorizedException;
    
    /**
     *
     * @param courseNotesID
     * @return int array with {courseID, lectureID}
     * @throws ClassicDatabaseException
     * @throws ClassicNotFoundException
     */
    public int[] getParents(int courseNotesID) throws ClassicDatabaseException,ClassicNotFoundException;
}
