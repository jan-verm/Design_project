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
import databaseaccess.interfaces.ICommentDao;
import databaseaccess.interfaces.ICourseNotesReferenceDao;
import databaseaccess.interfaces.IReplyDao;
import databaseaccess.interfaces.IUserDao;
import databaseaccess.interfaces.IVideoReferenceDao;
import databaseaccess.exceptions.ClassicUnauthorizedException;
import java.util.List;
import models.Comment;
import models.VideoReference;
import interfaces.CommentControllerInterface;
import java.util.Date;
import models.CourseNotesReference;
import models.Reply;

/**
 *
 * @author Juta
 */
public class CommentController implements CommentControllerInterface {

    private final IVideoReferenceDao videoReferenceDao;
    private final ICourseNotesReferenceDao courseNotesReferenceDao;
    private final ICommentDao dao;
    private final IReplyDao replydao;
    private final IUserDao userdao;
    
    /**
     * create a comment controller
     * @param dao class that interacts with the db for comments
     * @param replydao class that interacts with the db for replies
     * @param videodao class that interacts with the db for videos
     * @param courseNotesReferenceDao class that interacts with the db for course notes references
     * @param userdao class that interacts with the db for users
     */
    public CommentController(ICommentDao dao, IReplyDao replydao, IVideoReferenceDao videodao, ICourseNotesReferenceDao courseNotesReferenceDao, IUserDao userdao){
        this.dao = dao;
        this.replydao = replydao;
        this.videoReferenceDao = videodao;
        this.courseNotesReferenceDao = courseNotesReferenceDao;
        this.userdao = userdao;
    }
    
    private User getUser(int userId) throws ClassicDatabaseException, ClassicNotFoundException{
        return userdao.getUser(userId);
    }
    
    private void checkOwnershipComment(int userId, int commentId) throws ClassicDatabaseException, ClassicNotFoundException, ClassicUnauthorizedException {
        User user = getUser(userId);
        User owner = dao.getOwner(commentId);
        if (user.getRole() != Role.ADMIN && user.getId() != owner.getId()) {
            throw new ClassicUnauthorizedException("user with id " + userId + " is not allowed to perform this action");
        }
    }
    
    private void checkOwnershipReply(int userId, int replyId) throws ClassicDatabaseException, ClassicNotFoundException, ClassicUnauthorizedException {
        User user = getUser(userId);
        User owner = replydao.getOwner(replyId);
        if (user.getRole() != Role.ADMIN && user.getId() != owner.getId()) {
            throw new ClassicUnauthorizedException("user with id " + userId + " is not allowed to perform this action");
        }
    }
    
    @Override
    public int addVideoComment(int videoId, Comment comment, List<VideoReference> selfVideoRefs, int userId) throws ClassicDatabaseException, ClassicNotFoundException {
        long seconds = new Date().getTime();
        comment.setCreationTime(seconds);
        comment.setUser(getUser(userId));
        
        int commentId = dao.addComment(comment);
        for(VideoReference selfVideoRef : selfVideoRefs) {
        	videoReferenceDao.addVideoReference(videoId, commentId, selfVideoRef);
        }
        return commentId;
    }
    
    @Override
    public int addCourseNotesComment(int courseNotesId, Comment comment, List<CourseNotesReference> selfCourseNotesRefs, int userId) throws ClassicDatabaseException, ClassicNotFoundException {
        long seconds = new Date().getTime();
        comment.setCreationTime(seconds);
        comment.setUser(getUser(userId));
        int commentId = dao.addComment(comment);
        
        for(CourseNotesReference selfCourseNotesRef : selfCourseNotesRefs) {
            courseNotesReferenceDao.addCourseNotesReference(courseNotesId, commentId, selfCourseNotesRef);
        }
        return commentId;
    }

    @Override
    public Comment getComment(int commentId) throws ClassicDatabaseException,ClassicNotFoundException {
        Comment comment = dao.getComment(commentId);
        comment.setChildren(getReplies(commentId));
        return comment;
    }
    
    @Override
    public List<Comment> getVideoComments(int videoId) throws ClassicDatabaseException,ClassicNotFoundException {
        List<Comment> comments = dao.getVideoComments(videoId);
        // add self references
        for (Comment comment : comments) {
            List<VideoReference> refs = videoReferenceDao.getSelfVideoReferences(videoId, comment.getId());
            comment.setSelfVideoReferences(refs);
            comment.setChildren(getReplies(comment.getId()));
        }
        return comments;
    }
    
    @Override
    public List<Comment> getCourseNotesComments(int courseNotesId) throws ClassicDatabaseException, ClassicNotFoundException {
        List<Comment> comments = dao.getCourseNotesComments(courseNotesId);
        // add self references
        for (Comment comment : comments) {
            List<CourseNotesReference> refs = courseNotesReferenceDao.getSelfCourseNotesReferences(courseNotesId, comment.getId());
            comment.setSelfCourseNotesReferences(refs);
            comment.setChildren(getReplies(comment.getId()));
        }
        return comments;
    }

    @Override
    public void updateComment(int commentId, Comment comment, int userId) throws ClassicDatabaseException,ClassicNotFoundException, ClassicUnauthorizedException {
        checkOwnershipComment(userId, commentId);
        
        comment.setId(commentId);
        comment.setUser(getUser(userId));
        dao.updateComment(comment);
    }

    @Override
    public void deleteComment(int commentId, int userId) throws ClassicDatabaseException,ClassicNotFoundException, ClassicUnauthorizedException {
        checkOwnershipComment(userId, commentId);
        
        dao.deleteComment(commentId);
    }
    
    @Override
    public List<Reply> getReplies(int commentId) throws ClassicDatabaseException, ClassicNotFoundException {
        List<Reply> replies = replydao.getReplies(commentId);
        for (Reply r : replies) {
            r.setChildren(getReplies(r.getId()));
        }
        return replies;
    }
    
    @Override
    public int addReplytoComment(int commentId, Reply reply, int userId) throws ClassicDatabaseException, ClassicNotFoundException {
        long seconds = new Date().getTime();
        reply.setCreationTime(seconds);
        reply.setUser(getUser(userId));
        return replydao.addReply(commentId, reply);
    }

    @Override
    public int addReplytoReply(int replyId, Reply reply, int userId) throws ClassicDatabaseException, ClassicNotFoundException {
        long seconds = new Date().getTime();
        reply.setCreationTime(seconds);
        reply.setUser(getUser(userId));
        return replydao.addReply(replyId, reply);
    }

    @Override
    public void updateReply(int replyId, Reply reply, int userId) throws ClassicDatabaseException, ClassicNotFoundException, ClassicUnauthorizedException {
        checkOwnershipReply(userId, replyId);
        
        reply.setId(replyId);
        replydao.updateReply(reply);
    }

    @Override
    public void deleteReply(int replyId, int userId) throws ClassicDatabaseException, ClassicNotFoundException, ClassicUnauthorizedException {
        checkOwnershipReply(userId, replyId);
        replydao.deleteReply(replyId);
    }

    @Override
    public Reply getReply(int replyId) throws ClassicDatabaseException, ClassicNotFoundException {
        return replydao.getReply(replyId);
    }

    @Override
    public void voteComment(int commentId, boolean upvote, int userid) throws ClassicDatabaseException, ClassicNotFoundException {
        dao.vote(userid, commentId, upvote);
    }

    @Override
    public void setApprovedComment(int commentId, boolean approved, int userid) throws ClassicDatabaseException, ClassicNotFoundException {
        dao.approve(commentId, approved);
    }

    @Override
    public void voteReply(int replyId, boolean upvote, int userid) throws ClassicDatabaseException, ClassicNotFoundException {
        replydao.vote(userid, replyId, upvote);
    }

    @Override
    public void setApprovedReply(int replyId, boolean approved, int userid) throws ClassicDatabaseException, ClassicNotFoundException {
        replydao.approve(replyId, approved);
    }
}
