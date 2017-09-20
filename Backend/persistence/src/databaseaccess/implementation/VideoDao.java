/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package databaseaccess.implementation;

import courses.User;
import databaseaccess.exceptions.ClassicDatabaseException;
import databaseaccess.exceptions.ClassicNotFoundException;
import databaseaccess.interfaces.IVideoDao;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import models.Video;

/**
 * Data Access Object to work with videos in the Classic database.
 * @author Jorsi Grammens
 */
public class VideoDao extends ClassicDatabaseConnection implements IVideoDao {
    
    private static final String VIDEOID_COLUMN = "videoID";
    private static final String TITLE_COLUMN = "title";
    private static final String URL_COLUMN = "url";
    private static final String DURATION_COLUMN = "duration";
    private static final String PARENTID_COLUMN = "parentID";
    
    private CourseDao courseDao;
    
    /**
     * Constructs an instance of <code>VideoDao</code>.
     * @param propertiesFileName Propreties file that contains information about the database connection.
     * @throws ClassicDatabaseException
     */
    public VideoDao(String propertiesFileName) throws ClassicDatabaseException {
        super(propertiesFileName);
        courseDao = new CourseDao(propertiesFileName);
    }

    @Override
    public int addVideo(int parentID, Video video) throws ClassicDatabaseException, ClassicNotFoundException {
        String sql = "INSERT INTO \"Video\" (title,url,duration,\"parentID\") "
                + "VALUES (?,?,?,?) RETURNING *;";
        try (Connection con = getConnection(); PreparedStatement stmt = con.prepareStatement(sql)) {
            if (!courseDao.isCourse(parentID)) {
                throw new ClassicNotFoundException(ClassicNotFoundException.COURSE_OBJECT, parentID);
            }
            stmt.setString(1, video.getTitle());
            stmt.setString(2, video.getUrl());
            stmt.setInt(3, video.getDuration());
            stmt.setInt(4, parentID);
            ResultSet rs = stmt.executeQuery();
            rs.next();
            int id = rs.getInt(VIDEOID_COLUMN);
            video.setId(id);
            return id;
        } catch (SQLException ex) {
            String message = "A problem occured while trying to add a video to the Classic database";
            Logger.getLogger(VideoDao.class.getName()).log(Level.SEVERE, message, ex);
            throw new ClassicDatabaseException(message, ex);
        }
    }

    @Override
    public void deleteVideo(int videoID) throws ClassicDatabaseException, ClassicNotFoundException {
        String sql = "DELETE FROM \"Video\" "
                + "WHERE \"videoID\"=?;";
        try (Connection con = getConnection(); PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, videoID);
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new ClassicNotFoundException(ClassicNotFoundException.VIDEO_OBJECT, videoID);
            }
        } catch (SQLException ex) {
            String message = "A problem occured while trying to delete a video from the Classic database";
            Logger.getLogger(VideoDao.class.getName()).log(Level.SEVERE, message, ex);
            throw new ClassicDatabaseException(message, ex);
        }
    }

    @Override
    public Video getVideo(int videoID) throws ClassicDatabaseException, ClassicNotFoundException {
        String sql = "SELECT * FROM \"Video\" WHERE \"videoID\" = ?";
        try (Connection con = getConnection(); PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, videoID);
            ResultSet rs = stmt.executeQuery();
            if (!rs.isBeforeFirst()) {
                throw new ClassicNotFoundException(ClassicNotFoundException.VIDEO_OBJECT, videoID);
            }
            rs.next();            
            Video video = new Video(rs.getString(TITLE_COLUMN), rs.getString(URL_COLUMN), rs.getInt(DURATION_COLUMN));
            video.setId(rs.getInt(VIDEOID_COLUMN));
            return video;
        } catch (SQLException ex) {
            String message = "A problem occured while trying to get the video from the Classic database";
            Logger.getLogger(VideoDao.class.getName()).log(Level.SEVERE, message, ex);
            throw new ClassicDatabaseException(message, ex);
        }
    }

    @Override
    public List<Video> getVideos() throws ClassicDatabaseException {
        String sql = "SELECT * FROM \"Video\"";
        try (Connection con = getConnection(); Statement stmt = con.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            List<Video> videos = new ArrayList<>();
            while (rs.next()) {                
                Video video = new Video(rs.getString(TITLE_COLUMN), rs.getString(URL_COLUMN), rs.getInt(DURATION_COLUMN));
                video.setId(rs.getInt(VIDEOID_COLUMN));
                videos.add(video);
            }
            return videos;
        } catch (SQLException ex) {
            String message = "A problem occured while trying to get videos from the Classic database";
            Logger.getLogger(VideoDao.class.getName()).log(Level.SEVERE, message, ex);
            throw new ClassicDatabaseException(message, ex);
        }
    }

    @Override
    public List<Video> getVideos(int parentID) throws ClassicDatabaseException, ClassicNotFoundException {
        String sql = "SELECT * FROM \"Video\" WHERE \"parentID\"=?";
        try (Connection con = getConnection(); PreparedStatement stmt = con.prepareStatement(sql)) {
            if (!courseDao.isCourse(parentID)) {
                throw new ClassicNotFoundException(ClassicNotFoundException.COURSE_OBJECT, parentID);
            }
            int i = 1;
            stmt.setInt(i++, parentID);
            ResultSet rs = stmt.executeQuery();
            List<Video> videos = new ArrayList<>();
            while (rs.next()) {                
                Video video = new Video(rs.getString(TITLE_COLUMN), rs.getString(URL_COLUMN), rs.getInt(DURATION_COLUMN));
                video.setId(rs.getInt(VIDEOID_COLUMN));
                videos.add(video);
            }
            return videos;
        } catch (SQLException ex) {
            String message = "A problem occured while trying to get videos from the Classic database";
            Logger.getLogger(VideoDao.class.getName()).log(Level.SEVERE, message, ex);
            throw new ClassicDatabaseException(message, ex);
        }
    }
    
    protected boolean isVideo(int videoID) throws ClassicDatabaseException {
        String sql = "SELECT EXISTS(SELECT 1 FROM \"Video\" WHERE \"videoID\"=?)";
        try (Connection con = getConnection(); PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, videoID);
            ResultSet rs = stmt.executeQuery();
            rs.next();
            return rs.getBoolean("exists");
        } catch (SQLException ex) {
            String message = "A problem occured while trying to check the video with ID = " + videoID + " from the Classic database";
            Logger.getLogger(VideoDao.class.getName()).log(Level.SEVERE, message, ex);
            throw new ClassicDatabaseException(message, ex);
        }
    }

    @Override
    public void updateVideo(Video video) throws ClassicDatabaseException, ClassicNotFoundException {
        String sql = "UPDATE \"Video\" "
                + "SET \"title\"=?,\"url\"=?,\"duration\"=? "
                + "WHERE \"videoID\"=?;";
        try (Connection con = getConnection(); PreparedStatement stmt = con.prepareStatement(sql)) { 
            int i = 1;
            stmt.setString(i++, video.getTitle());
            stmt.setString(i++, video.getUrl());
            stmt.setInt(i++, video.getDuration());            
            stmt.setInt(i++, video.getId());
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new ClassicNotFoundException(ClassicNotFoundException.VIDEO_OBJECT, video.getId());
            }
        } catch (SQLException ex) {
            String message = "A problem occured while trying to update the video from the Classic database";
            Logger.getLogger(VideoDao.class.getName()).log(Level.SEVERE, message, ex);
            throw new ClassicDatabaseException(message, ex);
        }
    }

    @Override
    public void cleanTable() throws ClassicDatabaseException {
        String sql = "DELETE FROM \"Video\"";
        try (Connection con = getConnection(); Statement stmt = con.createStatement()) {
            stmt.executeUpdate(sql);
        } catch (SQLException ex) {
            String message = "A problem occured while trying to clean the video table in the database";
            Logger.getLogger(VideoDao.class.getName()).log(Level.SEVERE, message, ex);
            throw new ClassicDatabaseException(message, ex);
        }
    }

    @Override
    public User getOwner(int videoID) throws ClassicDatabaseException, ClassicNotFoundException {
        String sql = "SELECT \"parentID\" FROM \"Video\" WHERE \"videoID\" = ?";
        try (Connection con = getConnection(); PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, videoID);
            ResultSet rs = stmt.executeQuery();
            if (!rs.isBeforeFirst()) {
                throw new ClassicNotFoundException(ClassicNotFoundException.VIDEO_OBJECT,videoID);
            }
            rs.next();
            int parentID = rs.getInt(PARENTID_COLUMN);            
            return courseDao.getOwner(parentID);
        } catch (SQLException ex) {
            String message = "A problem occured while trying to get the video owner from the Classic database";
            Logger.getLogger(CourseNotesDao.class.getName()).log(Level.SEVERE, message, ex);
            throw new ClassicDatabaseException(message, ex);
        }
    }
}
