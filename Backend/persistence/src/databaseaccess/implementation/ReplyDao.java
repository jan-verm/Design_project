/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package databaseaccess.implementation;

import courses.User;
import databaseaccess.exceptions.ClassicDatabaseException;
import databaseaccess.exceptions.ClassicNotFoundException;
import databaseaccess.interfaces.IReplyDao;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import models.Comment;
import models.Reply;

/**
 * Data Access Object to work with replies in the Classic database.
 * @author Jorsi Grammens
 */
public class ReplyDao extends ClassicDatabaseConnection implements IReplyDao {

    private static final String COMMENTID_COLUMN = "commentID";
    private static final String USERID_COLUMN = "userID";
    private static final String BODY_COLUMN = "body";
    private static final String CREATIONTIME_COLUMN = "creationtime";
    private static final String APPROVED_COLUMN = "approved";
    private static final String VOTES_COLUMN = "votes";
    
    CommentDao commentDao;
    UserDao userDao;

    /**
     * Constructs an instance of <code>ReplyDao</code>.
     * @param propertiesFileName Propreties file that contains information about the database connection.
     * @throws ClassicDatabaseException
     */
    public ReplyDao(String propertiesFileName) throws ClassicDatabaseException {
        super(propertiesFileName);
        commentDao = new CommentDao(propertiesFileName);
        userDao = new UserDao(propertiesFileName);
    }

    @Override
    public int addReply(int parentID, Reply reply) throws ClassicDatabaseException, ClassicNotFoundException {
        String replySql = "INSERT INTO \"Comment\" (\"parentID\",\"body\",\"userID\",\"creationtime\",\"approved\",\"question\") "
                + "VALUES (?,?,?,?,?,?) RETURNING *;";
        try (Connection con = getConnection(); PreparedStatement stmtReply = con.prepareStatement(replySql)) {
            if (!commentDao.isComment(parentID)) {
                throw new ClassicNotFoundException(ClassicNotFoundException.COMMENT_OBJECT, parentID);
            }            
            int i = 1;
            stmtReply.setInt(i++, parentID);
            stmtReply.setString(i++, reply.getBody());    
            User user = userDao.getUser(reply.getUser().getUsername());
            stmtReply.setInt(i++, user.getId());
            stmtReply.setLong(i++, reply.getCreationTime());
            stmtReply.setBoolean(i++, reply.isApproved());
            stmtReply.setBoolean(i++, false);
            ResultSet rs = stmtReply.executeQuery();
            rs.next();
            int replyID = rs.getInt("commentID");
            reply.setId(replyID);
            return replyID;
        } catch (SQLException ex) {
            String message = "A problem occured while trying to add a reply to the Classic database";
            Logger.getLogger(ReplyDao.class.getName()).log(Level.SEVERE, message, ex);
            throw new ClassicDatabaseException(message, ex);
        }
    }

    @Override
    public void deleteReply(int replyID) throws ClassicDatabaseException, ClassicNotFoundException {
        try {
            commentDao.deleteComment(replyID);
        } catch (ClassicDatabaseException ex) {
            String message = "A problem occured while trying to delete a reply from the Classic database";
            Logger.getLogger(ReplyDao.class.getName()).log(Level.SEVERE, message, ex);
            throw new ClassicDatabaseException(message, ex);
        }
    }

    @Override
    public List<Reply> getReplies(int parentID) throws ClassicDatabaseException, ClassicNotFoundException {
        List<Reply> replies = new ArrayList<>();
        String sql = "SELECT rep.\"commentID\",rep.\"userID\",\"body\",\"creationtime\",\"approved\",\"question\",SUM(value) AS votes FROM "
                + "(SELECT * FROM \"Comment\" WHERE \"parentID\"=?) AS rep "
                + "LEFT JOIN \"Vote\" ON rep.\"commentID\"=\"Vote\".\"commentID\" "
                + "GROUP BY rep.\"commentID\",rep.\"userID\",\"body\",\"creationtime\",\"approved\",\"question\";";
        try (Connection con = getConnection(); PreparedStatement stmt = con.prepareStatement(sql)) {
            if (!commentDao.isComment(parentID)) {
                throw new ClassicNotFoundException(ClassicNotFoundException.COMMENT_OBJECT, parentID);
            }
            stmt.setInt(1, parentID);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                User user = userDao.getUser(rs.getInt(USERID_COLUMN));
                Reply r = new Reply(rs.getString(BODY_COLUMN));
                r.setUser(user);
                r.setId(rs.getInt(COMMENTID_COLUMN));
                r.setCreationTime(rs.getLong(CREATIONTIME_COLUMN));
                r.setApproved(rs.getBoolean(APPROVED_COLUMN));
                r.setUpvotes(rs.getInt(VOTES_COLUMN));
                replies.add(r);
            }
            return replies;
        } catch (SQLException ex) {
            String message = "A problem occured while trying to get the replies for the comment from the Classic database";
            Logger.getLogger(ReplyDao.class.getName()).log(Level.SEVERE, message, ex);
            throw new ClassicDatabaseException(message, ex);
        }
    }

    @Override
    public Reply getReply(int replyID) throws ClassicDatabaseException, ClassicNotFoundException {
        try {
            Comment comment = commentDao.getComment(replyID);
            Reply reply = new Reply(comment.getBody());
            reply.setUser(comment.getUser());
            reply.setCreationTime(comment.getCreationTime());
            reply.setId(comment.getId());
            reply.setApproved(comment.isApproved());
            reply.setUpvotes(comment.getUpvotes());
            return reply;
        } catch (ClassicDatabaseException ex) {
            String message = "A problem occured while trying to get the reply from the Classic database";
            Logger.getLogger(ReplyDao.class.getName()).log(Level.SEVERE, message, ex);
            throw new ClassicDatabaseException(message, ex);
        } catch (ClassicNotFoundException ex) {
            String message = "No reply found with ID = " + replyID;
            Logger.getLogger(ReplyDao.class.getName()).log(Level.SEVERE, message, ex);
            throw new ClassicNotFoundException(message, ex);
        }
    }

    protected boolean isReply(int replyID) throws ClassicDatabaseException {
        String sql = "SELECT EXISTS(SELECT 1 FROM \"Comment\" WHERE \"commentID\"=?)";
        try (Connection con = getConnection(); PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, replyID);
            ResultSet rs = stmt.executeQuery();
            rs.next();
            return rs.getBoolean("exists");
        } catch (SQLException ex) {
            String message = "A problem occured while trying to check the reply in the Classic database";
            Logger.getLogger(ReplyDao.class.getName()).log(Level.SEVERE, message, ex);
            throw new ClassicDatabaseException(message, ex);
        }
    }

    @Override
    public void updateReply(Reply reply) throws ClassicDatabaseException, ClassicNotFoundException {
        try {
            Comment tempComment = new Comment(reply.getBody(), false);
            tempComment.setUser(reply.getUser());
            tempComment.setCreationTime(reply.getCreationTime());
            tempComment.setId(reply.getId());
            tempComment.setApproved(reply.isApproved());
            tempComment.setUpvotes(reply.getUpvotes());
            commentDao.updateComment(tempComment);
        } catch (ClassicDatabaseException ex) {
            String message = "A problem occured while trying to update the reply from the Classic database";
            Logger.getLogger(ReplyDao.class.getName()).log(Level.SEVERE, message, ex);
            throw new ClassicDatabaseException(message, ex);
        } catch (ClassicNotFoundException ex) {
            String message = "No reply updated, reply with ID = " + reply.getId() + " was not found";
            Logger.getLogger(ReplyDao.class.getName()).log(Level.SEVERE, message, ex);
            throw new ClassicNotFoundException(message, ex);
        }
    }

    @Override
    public void cleanTable() throws ClassicDatabaseException {        
        
        String sql = "DELETE FROM \"Vote\"";
        try (Connection con = getConnection(); Statement stmt = con.createStatement()) {
            commentDao.cleanTable();
            stmt.executeUpdate(sql);
        } catch (SQLException ex) {
            String message = "A problem occured while trying to clean the reply table in the database";
            Logger.getLogger(CommentDao.class.getName()).log(Level.SEVERE, message, ex);
            throw new ClassicDatabaseException(message, ex);
        }
    }

        @Override
    public User getOwner(int replyID) throws ClassicDatabaseException, ClassicNotFoundException {
        return commentDao.getOwner(replyID);
    }

    @Override
    public void vote(int userID, int replyID, boolean upvote) throws ClassicDatabaseException, ClassicNotFoundException {
        commentDao.vote(userID, replyID, upvote);
    }

    @Override
    public void approve(int replyID, boolean approve) throws ClassicDatabaseException, ClassicNotFoundException {
        commentDao.approve(replyID, approve);
    }
}
