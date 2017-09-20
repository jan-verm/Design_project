/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package databaseaccess.interfaces;

import databaseaccess.exceptions.ClassicDatabaseException;
import databaseaccess.exceptions.ClassicNotFoundException;
import java.util.List;
import models.VideoReference;

/**
 * Database access object for videoreferences
 * @author Jorsi Grammens
 */
public interface IVideoReferenceDao {
    
    /**
     * Add a reference between a comment and video to the database.
     * @param videoID ID of the video.
     * @param commentID ID of the comment.
     * @param videoRef The reference object between the video and the annotation.
     * @return Id of the videoReference
     * @throws databaseaccess.exceptions.ClassicDatabaseException
     * @throws databaseaccess.exceptions.ClassicNotFoundException
     */
    public int addVideoReference(int videoID, int commentID, VideoReference videoRef) throws ClassicDatabaseException, ClassicNotFoundException;
   
    /**
     * Delete a reference between a comment and video from the database.
     * @param refID ID of the reference
     * @throws databaseaccess.exceptions.ClassicDatabaseException
     * @throws databaseaccess.exceptions.ClassicNotFoundException
     */
    public void deleteVideoReference(int refID) throws ClassicDatabaseException,ClassicNotFoundException;
    
    /**
     * Get a videoreference specified with the refID
     * @param refID
     * @return Specified videoreference
     * @throws databaseaccess.exceptions.ClassicDatabaseException
     * @throws databaseaccess.exceptions.ClassicNotFoundException
     */
    public VideoReference getVideoReference(int refID) throws ClassicDatabaseException,ClassicNotFoundException;
    
    /**
     * Get a reference between a comment and a video from the database.
     * @param videoID ID of the video.
     * @param commentID ID of the comment.
     * @return List of videoreferences.
     * @throws databaseaccess.exceptions.ClassicDatabaseException
     * @throws databaseaccess.exceptions.ClassicNotFoundException
     */
    public List<VideoReference> getSelfVideoReferences(int videoID, int commentID) throws ClassicDatabaseException,ClassicNotFoundException;
    
    /**
     * Get all reference from a comment to videos from the database.
     * @param commentID ID of the comment.
     * @return List of videoreferences.
     * @throws databaseaccess.exceptions.ClassicDatabaseException
     * @throws databaseaccess.exceptions.ClassicNotFoundException
     */
    public List<VideoReference> getVideoReferences(int commentID) throws ClassicDatabaseException,ClassicNotFoundException;

    /**
     * Update a reference between a comment and a video in the database.
     * @param videoRef Edited videoreference.
     * @throws databaseaccess.exceptions.ClassicDatabaseException
     * @throws databaseaccess.exceptions.ClassicNotFoundException
     */
    public void updateVideoReference(VideoReference videoRef) throws ClassicDatabaseException,ClassicNotFoundException;
    
    /**
     * Deletes all videoreference entries in the database
     * @throws databaseaccess.exceptions.ClassicDatabaseException
     */
    public void cleanTable() throws ClassicDatabaseException;
    
    /**
     *
     * @param videoID id of the video
     * @return int array with {courseID, lectureID}
     * @throws ClassicNotFoundException
     * @throws ClassicDatabaseException 
     */
    public int[] getCourseAndLectureForVideo(int videoID) throws ClassicNotFoundException, ClassicDatabaseException;
}
