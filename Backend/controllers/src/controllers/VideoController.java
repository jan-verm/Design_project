/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import courses.Role;
import courses.User;
import databaseaccess.exceptions.ClassicDatabaseException;
import databaseaccess.exceptions.ClassicNotFoundException;
import databaseaccess.exceptions.ClassicUnauthorizedException;
import databaseaccess.interfaces.ICourseDao;
import databaseaccess.interfaces.IUserDao;
import databaseaccess.interfaces.IVideoDao;
import interfaces.VideoControllerInterface;
import java.util.List;
import models.Video;

/**
 *
 * @author Juta
 */
public class VideoController implements VideoControllerInterface {
    
    private final IVideoDao dao;
    private final ICourseDao courseDao;
    private final IUserDao userdao;
    
    /**
     * create a video controller
     * @param dao class that interacts with the db for videos
     * @param courseDao class that interacts with the db for courses
     * @param userdao class that interacts with the db for users
     */
    public VideoController(IVideoDao dao, ICourseDao courseDao, IUserDao userdao){
        this.dao = dao;
        this.courseDao = courseDao;
        this.userdao = userdao;
    }
    
    private User getUser(int userId) throws ClassicDatabaseException, ClassicNotFoundException{
        return userdao.getUser(userId);
    }
    
    private void checkOwnershipCourse(int userId, int courseId) throws ClassicDatabaseException, ClassicNotFoundException, ClassicUnauthorizedException {
        User user = getUser(userId);
        User owner = courseDao.getOwner(courseId);
        if (user.getRole() != Role.ADMIN && user.getId() != owner.getId()) {
            throw new ClassicUnauthorizedException("user with id " + userId + " is not allowed to perform this action");
        }
    }
    
    private void checkOwnershipVideo(int userId, int videoId) throws ClassicDatabaseException, ClassicNotFoundException, ClassicUnauthorizedException {
        User user = getUser(userId);
        User owner = dao.getOwner(videoId);
        if (user.getRole() != Role.ADMIN && user.getId() != owner.getId()) {
            throw new ClassicUnauthorizedException("user with id " + userId + " is not allowed to perform this action");
        }
    }
    
    @Override
    public int addVideo(int parentId, Video video, int userId) throws ClassicDatabaseException, ClassicNotFoundException, ClassicUnauthorizedException {
        checkOwnershipCourse(userId, parentId);
        return dao.addVideo(parentId, video);
    }

    @Override
    public Video getVideo(int videoId) throws ClassicDatabaseException,ClassicNotFoundException {
        return dao.getVideo(videoId);
    }
    
    @Override
    public List<Video> getAllVideos(int parentId) throws ClassicDatabaseException, ClassicNotFoundException {
        return dao.getVideos(parentId);
    }

    @Override
    public void updateVideo(int videoId, Video video, int userId) throws ClassicDatabaseException,ClassicNotFoundException, ClassicUnauthorizedException {
        checkOwnershipVideo(userId, videoId);
        video.setId(videoId);
        dao.updateVideo(video);
    }

    @Override
    public void deleteVideo(int videoId, int userId) throws ClassicDatabaseException,ClassicNotFoundException, ClassicUnauthorizedException {
        checkOwnershipVideo(userId, videoId);
        dao.deleteVideo(videoId);
    }
    
}