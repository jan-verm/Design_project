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
import models.Video;

/**
 *
 * @author Juta
 */
public interface VideoControllerInterface {
    
    /**
     * add a video
     * @param parentId id of course or lecture this video belongs to
     * @param video the video to add
     * @return the id of the created video
     * @throws databaseaccess.exceptions.ClassicDatabaseException
     * @throws databaseaccess.exceptions.ClassicNotFoundException
     */
    public int addVideo(int parentId, Video video, int userId)
            throws ClassicDatabaseException, ClassicNotFoundException, ClassicUnauthorizedException;
    
    /**
     * Method to get a video
     * @param videoId the id of the video
     * @return The video object corresponding to the given video name
     * @throws databaseaccess.exceptions.ClassicDatabaseException
     * @throws databaseaccess.exceptions.ClassicNotFoundException
     */
    public Video getVideo(int videoId) throws ClassicDatabaseException,ClassicNotFoundException;
    
    /**
     * Method to get a list of all the videos in the database
     * @return A list of all the videos in the database
     * @throws databaseaccess.exceptions.ClassicDatabaseException
     * @throws databaseaccess.exceptions.ClassicNotFoundException
     */
    public List<Video> getAllVideos(int parentId) throws ClassicDatabaseException, ClassicNotFoundException;
    
    /**
     * update a video
     * @param videoId id of the video
     * @param video updated video
     * @throws databaseaccess.exceptions.ClassicDatabaseException
     * @throws databaseaccess.exceptions.ClassicNotFoundException
     */
    public void updateVideo(int videoId, Video video, int userId)
            throws ClassicDatabaseException,ClassicNotFoundException, ClassicUnauthorizedException;
    
    /**
     * delete a video
     * @param videoId id of the video
     * @throws databaseaccess.exceptions.ClassicDatabaseException
     * @throws databaseaccess.exceptions.ClassicNotFoundException
     */
    public void deleteVideo(int videoId, int userId)
            throws ClassicDatabaseException,ClassicNotFoundException , ClassicUnauthorizedException;
}
