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
import databaseaccess.interfaces.ICommentDao;
import databaseaccess.interfaces.IUserDao;
import databaseaccess.interfaces.IVideoReferenceDao;
import interfaces.VideoReferenceControllerInterface;
import java.util.List;
import models.VideoReference;

/**
 *
 * @author Juta
 */
public class VideoReferenceController implements VideoReferenceControllerInterface {
    
    private final IVideoReferenceDao dao;
    private  final ICommentDao commentDao;
    private final IUserDao userdao;
    
    /**
     * create a video reference controller
     * @param dao class that interacts with the db for video references
     * @param commentDao class that interacts with the db for comments
     * @param userdao class that interacts with the db for users
     */
    public VideoReferenceController(IVideoReferenceDao dao, ICommentDao commentDao, IUserDao userdao){
        this.dao = dao;
        this.commentDao = commentDao;
        this.userdao = userdao;
    }
    
    private User getUser(int userId) throws ClassicDatabaseException, ClassicNotFoundException{
        return userdao.getUser(userId);
    }
    
    /*
    * This also works for reply
    */
    private void checkOwnershipComment(int userId, int commentId) throws ClassicDatabaseException, ClassicNotFoundException, ClassicUnauthorizedException {
        User user = getUser(userId);
        User owner = commentDao.getOwner(commentId);
        if (user.getRole() != Role.ADMIN && user.getId() != owner.getId()) {
            throw new ClassicUnauthorizedException("user with id " + userId + " is not allowed to perform this action");
        }
    }

    @Override
    public int addVideoReference(int videoId, int commentId, VideoReference videoRef, int userId) throws ClassicDatabaseException, ClassicNotFoundException, ClassicUnauthorizedException {
        checkOwnershipComment(userId, commentId);
        return dao.addVideoReference(videoId, commentId, videoRef);
    } 

    @Override
    public List<VideoReference> getSelfVideoReferences(int videoId, int commentId) throws ClassicDatabaseException,ClassicNotFoundException {
        return dao.getSelfVideoReferences(videoId, commentId);
    }
    
    @Override
    public List<VideoReference> getVideoReferences(int commentId) throws ClassicDatabaseException,ClassicNotFoundException {
        return dao.getVideoReferences(commentId);
    }

    @Override
    public void updateVideoReference(int commentId, int referenceId, VideoReference videoRef, int userId) throws ClassicDatabaseException,ClassicNotFoundException, ClassicUnauthorizedException {
        checkOwnershipComment(userId, commentId);
        videoRef.setRefId(referenceId);
        dao.updateVideoReference(videoRef);
    }

    @Override
    public void deleteVideoReference(int commentId, int referenceId, int userId) throws ClassicDatabaseException,ClassicNotFoundException, ClassicUnauthorizedException {
        checkOwnershipComment(userId, commentId);
        dao.deleteVideoReference(referenceId);
    }

    @Override
    public VideoReference getVideoReference(int refId) throws ClassicDatabaseException, ClassicNotFoundException {
        return dao.getVideoReference(refId);
    }

    @Override
    public int[] getParents(int videoID) throws ClassicDatabaseException, ClassicNotFoundException {
        return dao.getCourseAndLectureForVideo(videoID);
    }
}
