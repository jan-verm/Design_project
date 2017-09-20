/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package databaseaccess.implementation;

import databaseaccess.exceptions.ClassicDatabaseException;
import databaseaccess.exceptions.ClassicNotFoundException;
import databaseaccess.interfaces.IVideoReferenceDao;
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
import models.VideoReference;

/**
 * Data Access Object to work with videoreferences in the Classic database.
 * @author Jorsi Grammens
 */
public class VideoReferenceDao extends ClassicDatabaseConnection implements IVideoReferenceDao {

    private static final String VCID_COLUMN = "vcID";
    private static final String VIDEOID_COLUMN = "videoID";
    private static final String VIDEOTITLE_COLUMN = "title";
    private static final String VIDEOURL_COLUMN = "url";
    private static final String VIDEODURATION_COLUMN = "duration";
    private static final String TIMESTAMP_COLUMN = "timestamp";
    private static final String VISIBLE_COLUMN = "visible";

    private CommentDao commentDao;
    private VideoDao videoDao;

    /**
     * Constructs an instance of <code>VideoReferenceDao</code>.
     * @param propertiesFileName Propreties file that contains information about the database connection.
     * @throws ClassicDatabaseException
     */
    public VideoReferenceDao(String propertiesFileName) throws ClassicDatabaseException {
        super(propertiesFileName);
        commentDao = new CommentDao(propertiesFileName);
        videoDao = new VideoDao(propertiesFileName);
    }
    
    @Override
    public int[] getCourseAndLectureForVideo(int videoID) throws ClassicNotFoundException, ClassicDatabaseException {
        String sql = "SELECT \"lectureID\", \"courseID\" FROM \"Video\" "
                + "INNER JOIN \"Lecture\" ON \"parentID\" = \"lectureID\""
                + "WHERE \"videoID\" = ?;";
        try (Connection con = getConnection(); PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, videoID);
            ResultSet rs = stmt.executeQuery();
            if (!rs.isBeforeFirst()) {
                throw new ClassicNotFoundException("The course and lecture for video with id = " + videoID + " where not found in the Classic database");
            }
            rs.next();
            int[] result = new int[2];
            if (rs.getInt("courseId") == 0) {
                result[0] = rs.getInt("lectureID");
                result[1] = rs.getInt("courseId");
            } else {
                result[1] = rs.getInt("lectureID");
                result[0] = rs.getInt("courseId");
            }
            return result;
        } catch (SQLException ex) {
            String message = "A problem occured while trying to get a videoreference to the Classic database";
            Logger.getLogger(VideoReferenceDao.class.getName()).log(Level.SEVERE, message, ex);
            throw new ClassicDatabaseException(message, ex);
        }
    }

    @Override
    public int addVideoReference(int videoID, int commentID, VideoReference videoRef) throws ClassicDatabaseException, ClassicNotFoundException {

        String sql = "INSERT INTO \"Video_comment\" (\"videoID\",\"commentID\",\"timestamp\",\"visible\") "
                + "VALUES (?,?,?,?) RETURNING *;";
        try (Connection con = getConnection(); PreparedStatement stmt = con.prepareStatement(sql)) {
            if (!videoDao.isVideo(videoID)) {
                throw new ClassicNotFoundException(ClassicNotFoundException.VIDEO_OBJECT, videoID);
            }
            if (!commentDao.isComment(commentID)) {
                throw new ClassicNotFoundException(ClassicNotFoundException.COMMENT_OBJECT, commentID);
            }
            int i = 1;
            stmt.setInt(i++, videoID);
            stmt.setInt(i++, commentID);
            stmt.setInt(i++, videoRef.getTimestamp());
            stmt.setBoolean(i++, videoRef.isVisible()); 
            ResultSet rs = stmt.executeQuery();
            rs.next();
            int id = rs.getInt(VCID_COLUMN);
            videoRef.setRefId(id);
            return id;
        } catch (SQLException ex) {
            String message = "A problem occured while trying to add a videoreference to the Classic database";
            Logger.getLogger(VideoReferenceDao.class.getName()).log(Level.SEVERE, message, ex);
            throw new ClassicDatabaseException(message, ex);
        }
    }

    @Override
    public void deleteVideoReference(int refID) throws ClassicDatabaseException, ClassicNotFoundException {
        String sql = "DELETE FROM \"Video_comment\" WHERE \"vcID\"=?;";
        try (Connection con = getConnection(); PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, refID);
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new ClassicNotFoundException(ClassicNotFoundException.VIDEOREFERENCE_OBJECT, refID);
            }
        } catch (SQLException ex) {
            String message = "A problem occured while trying to delete a videoreference from the Classic database";
            Logger.getLogger(VideoReferenceDao.class.getName()).log(Level.SEVERE, message, ex);
            throw new ClassicDatabaseException(message, ex);
        }
    }

    @Override
    public VideoReference getVideoReference(int refID) throws ClassicDatabaseException, ClassicNotFoundException {
        String sql = "SELECT vc.\"vcID\",vc.\"videoID\",vc.\"commentID\",vc.\"timestamp\",vc.visible,v.\"title\",v.\"url\",v.\"duration\" "
                + "FROM (SELECT * FROM \"Video_comment\" WHERE \"vcID\"=?) AS vc "
                + "INNER JOIN \"Video\" AS v ON vc.\"videoID\"=v.\"videoID\";";
        try (Connection con = getConnection(); PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, refID);
            ResultSet rs = stmt.executeQuery();
            if (!rs.isBeforeFirst()) {
                throw new ClassicNotFoundException(ClassicNotFoundException.VIDEOREFERENCE_OBJECT, refID);
            }
            rs.next();
            Video video = new Video(rs.getString(VIDEOTITLE_COLUMN), rs.getString(VIDEOURL_COLUMN), rs.getInt(VIDEODURATION_COLUMN));
            video.setId(rs.getInt(VIDEOID_COLUMN));
            VideoReference videoReference = new VideoReference(video, rs.getInt(TIMESTAMP_COLUMN));
            videoReference.setRefId(rs.getInt(VCID_COLUMN));
            videoReference.setVisible(rs.getBoolean(VISIBLE_COLUMN));
            return videoReference;
        } catch (SQLException ex) {
            String message = "A problem occured while trying to get a videoreference to the Classic database";
            Logger.getLogger(VideoReferenceDao.class.getName()).log(Level.SEVERE, message, ex);
            throw new ClassicDatabaseException(message, ex);
        }
    }

    @Override
    public List<VideoReference> getSelfVideoReferences(int videoID, int commentID) throws ClassicDatabaseException, ClassicNotFoundException {
        String sql = "SELECT vc.\"vcID\",vc.\"videoID\",vc.\"commentID\",vc.\"timestamp\",vc.visible,v.\"title\",v.\"url\",v.\"duration\" "
                + "FROM (SELECT * FROM \"Video_comment\" WHERE \"videoID\"=? AND \"commentID\"=?) AS vc "
                + "INNER JOIN \"Video\" AS v ON vc.\"videoID\"=v.\"videoID\";";
        try (Connection con = getConnection(); PreparedStatement stmt = con.prepareStatement(sql)) {
            if (!videoDao.isVideo(videoID)) {
                throw new ClassicNotFoundException(ClassicNotFoundException.VIDEO_OBJECT, videoID);
            }
            if (!commentDao.isComment(commentID)) {
                throw new ClassicNotFoundException(ClassicNotFoundException.COMMENT_OBJECT, commentID);
            }
            stmt.setInt(1, videoID);
            stmt.setInt(2, commentID);
            ResultSet rs = stmt.executeQuery();

            List<VideoReference> videoReferences = new ArrayList<>();
            while (rs.next()) {
                Video video = new Video(rs.getString(VIDEOTITLE_COLUMN), rs.getString(VIDEOURL_COLUMN), rs.getInt(VIDEODURATION_COLUMN));
                video.setId(rs.getInt(VIDEOID_COLUMN));
                VideoReference videoReference = new VideoReference(video, rs.getInt(TIMESTAMP_COLUMN));
                videoReference.setRefId(rs.getInt(VCID_COLUMN));
                videoReference.setVisible(rs.getBoolean(VISIBLE_COLUMN));                
                videoReferences.add(videoReference);
            }
            return videoReferences;
        } catch (SQLException ex) {
            String message = "A problem occured while trying to get a videoreference to the Classic database";
            Logger.getLogger(VideoReferenceDao.class.getName()).log(Level.SEVERE, message, ex);
            throw new ClassicDatabaseException(message, ex);
        }
    }

    @Override
    public List<VideoReference> getVideoReferences(int commentID) throws ClassicDatabaseException, ClassicNotFoundException {
        List<VideoReference> videoReferences = new ArrayList<>();
        String sql = "SELECT \"vcID\",\"timestamp\",visible,\"Video\".\"videoID\",\"title\",\"url\",\"duration\" "
                + "FROM (SELECT * FROM \"Video_comment\" WHERE \"commentID\"=?) AS \"vc\" "
                + "INNER JOIN \"Video\" ON \"vc\".\"videoID\"=\"Video\".\"videoID\";";
        try (Connection con = getConnection(); PreparedStatement stmt = con.prepareStatement(sql)) {
            if (!commentDao.isComment(commentID)) {
                throw new ClassicNotFoundException(ClassicNotFoundException.COMMENT_OBJECT, commentID);
            }
            stmt.setInt(1, commentID);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Video video = new Video(rs.getString(VIDEOTITLE_COLUMN), rs.getString(VIDEOURL_COLUMN), rs.getInt(VIDEODURATION_COLUMN));
                video.setId(rs.getInt(VIDEOID_COLUMN));
                VideoReference videoReference = new VideoReference(video, rs.getInt(TIMESTAMP_COLUMN));
                videoReference.setRefId(rs.getInt(VCID_COLUMN));
                videoReference.setVisible(rs.getBoolean(VISIBLE_COLUMN));
                videoReferences.add(videoReference);
            }
            return videoReferences;
        } catch (SQLException ex) {
            String message = "A problem occured while trying to get a videoreference from the Classic database";
            Logger.getLogger(VideoReferenceDao.class.getName()).log(Level.SEVERE, message, ex);
            throw new ClassicDatabaseException(message, ex);
        }

    }

    protected boolean isVideoReference(int refID) throws ClassicDatabaseException {
        String sql = "SELECT EXISTS(SELECT 1 FROM \"Video_comment\" WHERE \"vcID\"=?)";
        try (Connection con = getConnection(); PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, refID);
            ResultSet rs = stmt.executeQuery();
            rs.next();
            return rs.getBoolean("exists");
        } catch (SQLException ex) {
            String message = "A problem occured while trying to check the videorefrence in the Classic database";
            Logger.getLogger(VideoReferenceDao.class.getName()).log(Level.SEVERE, message, ex);
            throw new ClassicDatabaseException(message, ex);
        }
    }

    @Override
    public void updateVideoReference(VideoReference videoRef) throws ClassicDatabaseException, ClassicNotFoundException {
        String sql = "UPDATE \"Video_comment\" "
                + "SET timestamp=?,visible=? "
                + "WHERE \"vcID\"=?;";
        try (Connection con = getConnection(); PreparedStatement stmt = con.prepareStatement(sql)) {
            int i = 1;            
            stmt.setInt(i++, videoRef.getTimestamp());
            stmt.setBoolean(i++, videoRef.isVisible());
            stmt.setInt(i++, videoRef.getRefId());
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new ClassicNotFoundException(ClassicNotFoundException.VIDEOREFERENCE_OBJECT, videoRef.getRefId());
            }
        } catch (SQLException ex) {
            String message = "A problem occured while trying to update a videoreference in the Classic database";
            Logger.getLogger(VideoReferenceDao.class.getName()).log(Level.SEVERE, message, ex);
            throw new ClassicDatabaseException(message, ex);
        }
    }

    @Override
    public void cleanTable() throws ClassicDatabaseException {
        String sql = "DELETE FROM \"Video_comment\"";
        try (Connection con = getConnection(); Statement stmt = con.createStatement()) {
            stmt.executeUpdate(sql);
        } catch (SQLException ex) {
            String message = "A problem occured while trying to clean the video_comment table in the database";
            Logger.getLogger(VideoReferenceDao.class.getName()).log(Level.SEVERE, message, ex);
            throw new ClassicDatabaseException(message, ex);
        }
    }
}
