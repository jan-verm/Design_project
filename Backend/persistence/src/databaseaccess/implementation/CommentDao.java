/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package databaseaccess.implementation;

import courses.User;
import databaseaccess.exceptions.ClassicDatabaseException;
import databaseaccess.exceptions.ClassicNotFoundException;
import databaseaccess.exceptions.ClassicUnauthorizedException;
import databaseaccess.interfaces.ICommentDao;
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

/**
 * Data Access Object to work with comments in the Classic database.
 * @author Jorsi Grammens
 */
public class CommentDao extends ClassicDatabaseConnection implements ICommentDao {

    private static final String COMMENTID_COLUMN = "commentID";
    private static final String USERID_COLUMN = "userID";
    private static final String BODY_COLUMN = "body";
    private static final String CREATIONTIME_COLUMN = "creationtime";
    private static final String APPROVED_COLUMN = "approved";
    private static final String QUESTION_COLUMN = "question";
    private static final String VOTES_COLUMN = "votes";
    private static final String VALUE_COLUMN = "value";

    private UserDao userDao;
    CourseNotesDao courseNotesDao;
    VideoDao videoDao;

    /**
     * Constructs an instance of <code>CommentDao</code>.
     * @param propertiesFileName Propreties file that contains information about the database connection.
     * @throws ClassicDatabaseException
     */
    public CommentDao(String propertiesFileName) throws ClassicDatabaseException {
        super(propertiesFileName);
        userDao = new UserDao(propertiesFileName);
        courseNotesDao = new CourseNotesDao(propertiesFileName);
        videoDao = new VideoDao(propertiesFileName);
    }

    @Override
    public int addComment(Comment comment) throws ClassicDatabaseException {
        String sql = "INSERT INTO \"Comment\" (\"body\",\"userID\",\"creationtime\",\"approved\",\"question\") "
                + "VALUES (?,?,?,?,?) RETURNING *;";
        try (Connection con = getConnection(); PreparedStatement stmtComment = con.prepareStatement(sql)) {
            int i = 1;
            stmtComment.setString(i++, comment.getBody());
            stmtComment.setInt(i++, comment.getUser().getId());
            stmtComment.setLong(i++, comment.getCreationTime());
            stmtComment.setBoolean(i++, comment.isApproved());
            stmtComment.setBoolean(i++, comment.isQuestion());
            ResultSet rsComment = stmtComment.executeQuery();
            rsComment.next();
            int commentID = rsComment.getInt(COMMENTID_COLUMN);
            comment.setId(commentID);
            return commentID;
        } catch (SQLException ex) {
            String message = "A problem occured while trying to add a comment to the Classic database";
            Logger.getLogger(CommentDao.class.getName()).log(Level.SEVERE, message, ex);
            throw new ClassicDatabaseException(message, ex);
        }
    }

    @Override
    public void deleteComment(int commentID) throws ClassicDatabaseException, ClassicNotFoundException {
        String sql = "DELETE FROM \"Comment\" "
                + "WHERE \"commentID\"=?;";
        try (Connection con = getConnection(); PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, commentID);
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new ClassicNotFoundException(ClassicNotFoundException.COMMENT_OBJECT, commentID);
            }
        } catch (SQLException ex) {
            String message = "A problem occured while trying to delete a comment from the Classic database";
            Logger.getLogger(CommentDao.class.getName()).log(Level.SEVERE, message, ex);
            throw new ClassicDatabaseException(message, ex);
        }
    }

    @Override
    public Comment getComment(int commentID) throws ClassicDatabaseException, ClassicNotFoundException {
        String sql = "SELECT com.\"commentID\",com.\"userID\",\"body\",\"creationtime\",\"approved\",\"question\",SUM(value) AS votes "
                + "FROM (SELECT * FROM \"Comment\" WHERE \"commentID\"=?) AS com "
                + "LEFT JOIN \"Vote\" ON com.\"commentID\"=\"Vote\".\"commentID\" "
                + "GROUP BY com.\"commentID\",com.\"userID\",\"body\",\"creationtime\",\"approved\",\"question\";";
        try (Connection con = getConnection(); PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, commentID);
            ResultSet rs = stmt.executeQuery();
            if (!rs.isBeforeFirst()) {
                throw new ClassicNotFoundException(ClassicNotFoundException.COMMENT_OBJECT, commentID);
            }
            rs.next();
            User user = userDao.getUser(rs.getInt(USERID_COLUMN));
            Comment comment = new Comment(rs.getString(BODY_COLUMN), rs.getBoolean(QUESTION_COLUMN));
            comment.setUser(user);
            comment.setCreationTime(rs.getLong(CREATIONTIME_COLUMN));
            comment.setId(rs.getInt(COMMENTID_COLUMN));
            comment.setApproved(rs.getBoolean(APPROVED_COLUMN));
            comment.setUpvotes(rs.getInt(VOTES_COLUMN));
            return comment;
        } catch (SQLException ex) {
            String message = "A problem occured while trying to get the comment from the Classic database";
            Logger.getLogger(CommentDao.class.getName()).log(Level.SEVERE, message, ex);
            throw new ClassicDatabaseException(message, ex);
        }

    }

    @Override
    public List<Comment> getVideoComments(int videoID) throws ClassicDatabaseException, ClassicNotFoundException {
        List<Comment> comments = new ArrayList<>();
        String sql = "SELECT com.\"commentID\",com.\"userID\",\"body\",\"creationtime\",\"approved\",\"question\",SUM(value) AS votes FROM "
                + "(SELECT \"Comment\".\"commentID\",\"body\",\"userID\",\"creationtime\",\"approved\",\"question\" "
                + "FROM (SELECT DISTINCT \"videoID\",\"commentID\" FROM \"Video_comment\" WHERE \"videoID\"=? AND visible=TRUE) AS vc "
                + "INNER JOIN \"Comment\" ON vc.\"commentID\"=\"Comment\".\"commentID\") AS com "
                + "LEFT JOIN \"Vote\" ON com.\"commentID\"=\"Vote\".\"commentID\" "
                + "GROUP BY com.\"commentID\",com.\"userID\",\"body\",\"creationtime\",\"approved\",\"question\";";
        try (Connection con = getConnection(); PreparedStatement stmt = con.prepareStatement(sql)) {
            if (!videoDao.isVideo(videoID)) {
                throw new ClassicNotFoundException(ClassicNotFoundException.VIDEO_OBJECT,videoID);
            }
            stmt.setInt(1, videoID);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                User user = userDao.getUser(rs.getInt(USERID_COLUMN));
                Comment c = new Comment(rs.getString(BODY_COLUMN), rs.getBoolean(QUESTION_COLUMN));
                c.setId(rs.getInt(COMMENTID_COLUMN));
                c.setCreationTime(rs.getLong(CREATIONTIME_COLUMN));
                c.setApproved(rs.getBoolean(APPROVED_COLUMN));
                c.setUpvotes(rs.getInt(VOTES_COLUMN));
                c.setUser(user);
                comments.add(c);
            }
            return comments;
        } catch (SQLException ex) {
            String message = "A problem occured while trying to get the comments for the video from the Classic database";
            Logger.getLogger(CommentDao.class.getName()).log(Level.SEVERE, message, ex);
            throw new ClassicDatabaseException(message, ex);
        }
    }

    @Override
    public List<Comment> getCourseNotesComments(int coursenotesID) throws ClassicDatabaseException, ClassicNotFoundException {
        List<Comment> comments = new ArrayList<>();
        String sql = "SELECT com.\"commentID\",com.\"userID\",\"body\",\"creationtime\",\"approved\",\"question\",SUM(value) AS votes FROM "
                + "(SELECT \"Comment\".\"commentID\",\"body\",\"userID\",\"creationtime\",\"approved\",\"question\" "
                + "FROM (SELECT DISTINCT \"coursenotesID\",\"commentID\" FROM \"Coursenotes_comment\" WHERE \"coursenotesID\"=? AND visible=TRUE) AS cc "
                + "INNER JOIN \"Comment\" ON cc.\"commentID\"=\"Comment\".\"commentID\") AS com "
                + "LEFT JOIN \"Vote\" ON com.\"commentID\"=\"Vote\".\"commentID\" "
                + "GROUP BY com.\"commentID\",com.\"userID\",\"body\",\"creationtime\",\"approved\",\"question\";";
        try (Connection con = getConnection(); PreparedStatement stmt = con.prepareStatement(sql)) {
            if (!courseNotesDao.isCourseNotes(coursenotesID)) {
                throw new ClassicNotFoundException(ClassicNotFoundException.COURSENOTES_OBJECT, coursenotesID);
            }
            stmt.setInt(1, coursenotesID);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                User user = userDao.getUser(rs.getInt(USERID_COLUMN));
                Comment c = new Comment(rs.getString(BODY_COLUMN), rs.getBoolean(QUESTION_COLUMN));
                c.setId(rs.getInt(COMMENTID_COLUMN));
                c.setCreationTime(rs.getLong(CREATIONTIME_COLUMN));
                c.setApproved(rs.getBoolean(APPROVED_COLUMN));
                c.setUpvotes(rs.getInt(VOTES_COLUMN));
                c.setUser(user);
                comments.add(c);
            }
            return comments;
        } catch (SQLException ex) {
            String message = "A problem occured while trying to get the comments for the coursenotes from the Classic database";
            Logger.getLogger(CommentDao.class.getName()).log(Level.SEVERE, message, ex);
            throw new ClassicDatabaseException(message, ex);
        } 
    }

    protected boolean isComment(int commentID) throws ClassicDatabaseException {
        String sql = "SELECT EXISTS(SELECT 1 FROM \"Comment\" WHERE \"commentID\"=?)";
        try (Connection con = getConnection(); PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, commentID);
            ResultSet rs = stmt.executeQuery();
            rs.next();
            return rs.getBoolean("exists");
        } catch (SQLException ex) {
            String message = "A problem occured while trying to check the comment with ID = " + commentID + " from the Classic database";
            Logger.getLogger(CommentDao.class.getName()).log(Level.SEVERE, message, ex);
            throw new ClassicDatabaseException(message, ex);
        }
    }

    @Override
    public void updateComment(Comment comment) throws ClassicDatabaseException, ClassicNotFoundException {
        String sqlComment = "UPDATE \"Comment\" "
                + "SET \"body\"=?,\"question\"=? "
                + "WHERE \"commentID\"=?;";        
        try (Connection con = getConnection(); PreparedStatement stmt = con.prepareStatement(sqlComment);) {
            int i = 1;
            stmt.setString(i++, comment.getBody());
            stmt.setBoolean(i++, comment.isQuestion());
            stmt.setInt(i++, comment.getId());
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new ClassicNotFoundException(ClassicNotFoundException.COMMENT_OBJECT, comment.getId());
            }
        } catch (SQLException ex) {
            String message = "A problem occured while trying to update the comment from the Classic database";
            Logger.getLogger(CommentDao.class.getName()).log(Level.SEVERE, message, ex);
            throw new ClassicDatabaseException(message, ex);
        }
    }

    @Override
    public void cleanTable() throws ClassicDatabaseException {
        String sql = "DELETE FROM \"Comment\"";
        try (Connection con = getConnection(); Statement stmt = con.createStatement()) {
            stmt.executeUpdate(sql);
        } catch (SQLException ex) {
            String message = "A problem occured while trying to clean the comment table in the database";
            Logger.getLogger(CommentDao.class.getName()).log(Level.SEVERE, message, ex);
            throw new ClassicDatabaseException(message, ex);
        }
    }

    @Override
    public User getOwner(int commentID) throws ClassicDatabaseException, ClassicNotFoundException {
        String sql = "SELECT \"userID\" FROM \"Comment\" WHERE \"commentID\" = ?;";
        try (Connection con = getConnection(); PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, commentID);
            ResultSet rs = stmt.executeQuery();
            if (!rs.isBeforeFirst()) {
                throw new ClassicNotFoundException(ClassicNotFoundException.COMMENT_OBJECT,commentID);
            }
            rs.next();
            return userDao.getUser(rs.getInt(USERID_COLUMN));   
        } catch (SQLException ex) {
            String message = "A problem occured while trying to get the lectureowner the Classic database";
            Logger.getLogger(CommentDao.class.getName()).log(Level.SEVERE, message, ex);
            throw new ClassicDatabaseException(message, ex);
        }
    }

    @Override
    public void vote(int userID, int commentID, boolean upvote) throws ClassicDatabaseException, ClassicNotFoundException {
        String sqlInsert = "INSERT INTO \"Vote\" (\"commentID\",\"userID\",\"value\") "
                + "VALUES (?,?,?)";
        String sqlCheck = "SELECT * FROM \"Vote\" WHERE \"userID\"=? AND \"commentID\"=?";
        String sqlUpdate = "UPDATE \"Vote\" SET value=? WHERE \"userID\"=? AND \"commentID\"=?";
        try (Connection con = getConnection(); PreparedStatement stmtInsert = con.prepareStatement(sqlInsert); PreparedStatement stmtcheck = con.prepareStatement(sqlCheck);PreparedStatement stmtupdate = con.prepareStatement(sqlUpdate)) {
            if (!userDao.isUser(userID)) {
                throw new ClassicNotFoundException(ClassicNotFoundException.USER_OBJECT, userID);
            }
            if (!isComment(commentID)) {
                throw new ClassicNotFoundException(ClassicNotFoundException.REPLY_OBJECT, commentID);
            }
            int j = 1;
            int k = 1;
            stmtcheck.setInt(j++, userID);
            stmtcheck.setInt(j++, commentID);
            ResultSet rs = stmtcheck.executeQuery();
            if (rs.isBeforeFirst()) {
                rs.next();
                int voteValue = rs.getInt(VALUE_COLUMN);
                if ((voteValue == 1 && upvote) || (voteValue == -1 && !upvote)) {
                    throw new ClassicUnauthorizedException("User already voted");
                } else 
                if (upvote) {
                    stmtupdate.setInt(k++, voteValue+1);
                } else if (!upvote) {
                    
                    stmtupdate.setInt(k++, voteValue-1);
                }
                stmtupdate.setInt(k++, userID);
                stmtupdate.setInt(k++, commentID);
                stmtupdate.executeUpdate();
            }else{                
                int i = 1;
                stmtInsert.setInt(i++, commentID);
                stmtInsert.setInt(i++, userID);
                if (upvote) {
                    stmtInsert.setInt(i++, 1);
                } else{
                    stmtInsert.setInt(i++, -1);
                }
                stmtInsert.executeUpdate();
            }
        } catch (SQLException ex) {
            String message = "A problem occured while trying to add a vote to the Classic database";
            Logger.getLogger(CommentDao.class.getName()).log(Level.SEVERE, message, ex);
            throw new ClassicDatabaseException(message, ex);
        }
    }

    @Override
    public void approve(int commentID, boolean approve) throws ClassicDatabaseException, ClassicNotFoundException {
        String sqlComment = "UPDATE \"Comment\" "
                + "SET \"approved\"=? "
                + "WHERE \"commentID\"=?;";
        try (Connection con = getConnection(); PreparedStatement stmt = con.prepareStatement(sqlComment); ) {
            if (!isComment(commentID)) {
                throw new ClassicNotFoundException(ClassicNotFoundException.COMMENT_OBJECT, commentID);
            }            
            int i = 1;
            stmt.setBoolean(i++, approve);
            stmt.setInt(i++, commentID);
            stmt.executeUpdate();
        } catch (SQLException ex) {
            String message = "A problem occured while trying to approve the comment from the Classic database";
            Logger.getLogger(CommentDao.class.getName()).log(Level.SEVERE, message, ex);
            throw new ClassicDatabaseException(message, ex);
        }
    }
}
