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
import models.Video;

/**
 * Database access object for videos.
 * @author Jorsi Grammens
 */
public interface IVideoDao {
    
    /**
     * Adds a video to the database.
     * @param parentID Id of the course or lecture the video belongs to.
     * @param video Video to add.
     * @return ID of the added video.
     * @throws databaseaccess.exceptions.ClassicDatabaseException
     * @throws databaseaccess.exceptions.ClassicNotFoundException
     */
    public int addVideo(int parentID, Video video) throws ClassicDatabaseException,ClassicNotFoundException; 
    
    /**
     * Delete video from the video.
     * @param videoID Id of the video to delete.
     * @throws databaseaccess.exceptions.ClassicDatabaseException
     * @throws databaseaccess.exceptions.ClassicNotFoundException
     */
    public void deleteVideo(int videoID) throws ClassicDatabaseException,ClassicNotFoundException; 
    
     /**
     * Get a specified video from the database.
     * @param videoID ID of the video to get.
     * @return Specified video.
     * @throws databaseaccess.exceptions.ClassicDatabaseException
     * @throws databaseaccess.exceptions.ClassicNotFoundException
     */
    public Video getVideo(int videoID) throws ClassicDatabaseException,ClassicNotFoundException;  
    
    /**
     * Get all video from the database.
     * @return List of videos
     * @throws databaseaccess.exceptions.ClassicDatabaseException
     */
    public List<Video> getVideos() throws ClassicDatabaseException;
               
    /**
     * Get all videos from a specific course or lecture from the database.
     * @param parentID Id of the course or lecture
     * @return List of videos
     * @throws databaseaccess.exceptions.ClassicDatabaseException
     * @throws databaseaccess.exceptions.ClassicNotFoundException
     */
    public List<Video> getVideos(int parentID) throws ClassicDatabaseException, ClassicNotFoundException;
    
    /**
     * Get the owner of the video.
     * @param videoID Id of the video for which the owner is wanted.
     * @return User object that represents the owner of the course.
     * @throws ClassicDatabaseException
     * @throws ClassicNotFoundException
     */
    public User getOwner(int videoID) throws ClassicDatabaseException, ClassicNotFoundException;
    
    /**
     * Update a specified video in the database.
     * @param video Edited video.
     * @throws databaseaccess.exceptions.ClassicDatabaseException
     * @throws databaseaccess.exceptions.ClassicNotFoundException
     */
    public void updateVideo(Video video) throws ClassicDatabaseException,ClassicNotFoundException; 
    
    /**
     * Deletes all video entries in the database
     * @throws databaseaccess.exceptions.ClassicDatabaseException
     */
    public void cleanTable() throws ClassicDatabaseException;
}
