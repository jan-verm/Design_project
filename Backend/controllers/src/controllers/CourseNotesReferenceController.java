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
import databaseaccess.interfaces.ICourseNotesReferenceDao;
import databaseaccess.interfaces.IUserDao;
import interfaces.CourseNotesReferenceControllerInterface;
import java.util.List;
import models.CourseNotesReference;

/**
 *
 * @author Juta
 */
public class CourseNotesReferenceController implements CourseNotesReferenceControllerInterface {
    
    private final ICourseNotesReferenceDao dao;
    private final ICommentDao commentdao;
    private final IUserDao userdao;
    
    /**
     * create a course notes reference controller
     * @param dao class that interacts with the db for course notes references
     * @param commentdao class that interacts with the db for comments
     * @param userdao class that interacts with the db for users
     */
    public CourseNotesReferenceController(ICourseNotesReferenceDao dao, ICommentDao commentdao, IUserDao userdao){
        this.dao = dao;
        this.commentdao = commentdao;
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
        User owner = commentdao.getOwner(commentId);
        if (user.getRole() != Role.ADMIN && user.getId() != owner.getId()) {
            throw new ClassicUnauthorizedException("user with id " + userId + " is not allowed to perform this action");
        }
    }

    @Override
    public int addCourseNotesReference(int courseNotesId, int commentId, CourseNotesReference courseNotesRef, int userId) throws ClassicDatabaseException, ClassicNotFoundException, ClassicUnauthorizedException {
        checkOwnershipComment(userId, commentId);
        return dao.addCourseNotesReference(courseNotesId, commentId, courseNotesRef);
    }

    @Override
    public List<CourseNotesReference> getSelfCourseNotesReferences(int courseNotesId, int commentId) throws ClassicDatabaseException, ClassicNotFoundException {
        return dao.getSelfCourseNotesReferences(courseNotesId, commentId);
    }

    @Override
    public List<CourseNotesReference> getCourseNotesReferences(int commentId) throws ClassicDatabaseException, ClassicNotFoundException {
        return dao.getCourseNotesReferences(commentId);
    }

    @Override
    public void updateCourseNotesReference(int commentId, int referenceId, CourseNotesReference courseNotesRef, int userId) throws ClassicDatabaseException, ClassicNotFoundException, ClassicUnauthorizedException {
        checkOwnershipComment(userId, commentId);
        courseNotesRef.setRefId(referenceId);
        dao.updateCourseNotesReference(courseNotesRef);
    }

    @Override
    public void deleteCourseNotesReference(int commentId, int referenceId, int userId) throws ClassicDatabaseException, ClassicNotFoundException, ClassicUnauthorizedException {
        checkOwnershipComment(userId, commentId);
        dao.deleteCourseNotesReference(referenceId);
    }

    @Override
    public CourseNotesReference getCourseNotesReference(int refId) throws ClassicDatabaseException, ClassicNotFoundException {
        return dao.getCourseNotesReference(refId);
    }

    @Override
    public int[] getParents(int videoID) throws ClassicDatabaseException, ClassicNotFoundException {
        return dao.getCourseAndLectureForCourseNotes(videoID);
    }
}
