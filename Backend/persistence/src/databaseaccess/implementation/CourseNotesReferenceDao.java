/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package databaseaccess.implementation;

import databaseaccess.exceptions.ClassicDatabaseException;
import databaseaccess.exceptions.ClassicNotFoundException;
import databaseaccess.interfaces.ICourseNotesReferenceDao;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import models.CourseNotes;
import models.CourseNotesReference;
import models.Location;

/**
 * Data Access Object to work with coursenotesreferences in the Classic database.
 * @author Jorsi Grammens
 */
public class CourseNotesReferenceDao extends ClassicDatabaseConnection implements ICourseNotesReferenceDao {

    private static final String CCID_COLUMN = "ccID";
    private static final String COURSENOTESID_COLUMN = "coursenotesID";
    private static final String VISIBLE_COLUMN = "visible";
    private static final String COURSENOTESTITLE_COLUMN = "title";
    private static final String COURSENOTESURL_COLUMN = "url";

    private LocationDao locationDao;
    private CourseNotesDao courseNotesDao;
    private CommentDao commentDao;

    /**
     * Constructs an instance of <code>CourseNotesReferenceDao</code>.
     * @param propertiesFileName Propreties file that contains information about the database connection.
     * @throws ClassicDatabaseException
     */
    public CourseNotesReferenceDao(String propertiesFileName) throws ClassicDatabaseException {
        super(propertiesFileName);
        locationDao = new LocationDao(propertiesFileName);
        commentDao = new CommentDao(propertiesFileName);
        courseNotesDao = new CourseNotesDao(propertiesFileName);
    }
    
    @Override
    public int[] getCourseAndLectureForCourseNotes(int courseNotesID) throws ClassicNotFoundException, ClassicDatabaseException {
        String sql = "SELECT \"lectureID\", \"courseID\" FROM \"Coursenotes\" "
                + "INNER JOIN \"Lecture\" ON \"parentID\" = \"lectureID\""
                + "WHERE \"coursenotesID\" = ?;";
        try (Connection con = getConnection(); PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, courseNotesID);
            ResultSet rs = stmt.executeQuery();
            if (!rs.isBeforeFirst()) {
                throw new ClassicNotFoundException("The course and lecture for coursenotes with id =" + courseNotesID + " where not found in the Classic database");
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
            String message = "A problem occured while trying to get the course and lecture for coursenotes from the Classic database";
            Logger.getLogger(VideoReferenceDao.class.getName()).log(Level.SEVERE, message, ex);
            throw new ClassicDatabaseException(message, ex);
        }
    }

    @Override
    public int addCourseNotesReference(int courseNotesID, int commentID, CourseNotesReference courseNotesRef) throws ClassicDatabaseException, ClassicNotFoundException {
        String sql = "INSERT INTO \"Coursenotes_comment\" (\"coursenotesID\",\"commentID\",visible) "
                + "VALUES (?,?,?) RETURNING *;";
        try (Connection con = getConnection(); PreparedStatement stmt = con.prepareStatement(sql)) {
            if (!courseNotesDao.isCourseNotes(courseNotesID)) {
                throw new ClassicNotFoundException(ClassicNotFoundException.COURSENOTES_OBJECT, courseNotesID);
            }
            if (!commentDao.isComment(commentID)) {
                throw new ClassicNotFoundException(ClassicNotFoundException.COMMENT_OBJECT, commentID);
            }
            int i = 1;
            stmt.setInt(i++, courseNotesID);
            stmt.setInt(i++, commentID);
            stmt.setBoolean(i++, courseNotesRef.isVisible());
            ResultSet rs = stmt.executeQuery();
            rs.next();
            int id = rs.getInt(CCID_COLUMN);
            courseNotesRef.setRefId(id);
            for (Location location : courseNotesRef.getLocations()) {
                locationDao.addLocation(id, location);
            }
            return id;
        } catch (SQLException ex) {
            String message = "A problem occured while trying to add a coursenotesreference to the Classic database";
            Logger.getLogger(CourseNotesReferenceDao.class.getName()).log(Level.SEVERE, message, ex);
            throw new ClassicDatabaseException(message, ex);
        }
    }

    @Override
    public void deleteCourseNotesReference(int refID) throws ClassicDatabaseException, ClassicNotFoundException {
        String sql = "DELETE FROM \"Coursenotes_comment\" WHERE \"ccID\"=?;";
        try (Connection con = getConnection(); PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, refID);
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new ClassicNotFoundException(ClassicNotFoundException.COURSENOTESREFERENCE_OBJECT, refID);
            }
        } catch (SQLException ex) {
            String message = "A problem occured while trying to delete a coursenotesreference from the Classic database";
            Logger.getLogger(CourseNotesReferenceDao.class.getName()).log(Level.SEVERE, message, ex);
            throw new ClassicDatabaseException(message, ex);
        }
    }

    @Override
    public CourseNotesReference getCourseNotesReference(int refID) throws ClassicDatabaseException, ClassicNotFoundException {
        String sql = "SELECT cc.\"ccID\",cc.\"coursenotesID\",cc.\"commentID\",cc.visible,c.\"title\",c.\"url\" "
                + "FROM (SELECT * FROM \"Coursenotes_comment\" WHERE \"ccID\"=?) AS cc "
                + "INNER JOIN \"Coursenotes\" AS c ON cc.\"coursenotesID\"=c.\"coursenotesID\";";
        try (Connection con = getConnection(); PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, refID);
            ResultSet rs = stmt.executeQuery();
            if (!rs.isBeforeFirst()) {
                throw new ClassicNotFoundException(ClassicNotFoundException.COURSENOTESREFERENCE_OBJECT, refID);
            }
            rs.next();
            CourseNotes courseNotes = new CourseNotes(rs.getString(COURSENOTESTITLE_COLUMN), rs.getString(COURSENOTESTITLE_COLUMN));
            courseNotes.setId(rs.getInt(COURSENOTESID_COLUMN));
            List<Location> locations = locationDao.getLocations(refID);
            CourseNotesReference courseNotesReference = new CourseNotesReference(courseNotes, locations);
            courseNotesReference.setRefId(rs.getInt(CCID_COLUMN));
            courseNotesReference.setVisible(rs.getBoolean(VISIBLE_COLUMN));
            return courseNotesReference;
        } catch (SQLException ex) {
            String message = "A problem occured while trying to get a coursenotesreference to the Classic database";
            Logger.getLogger(CourseNotesReferenceDao.class.getName()).log(Level.SEVERE, message, ex);
            throw new ClassicDatabaseException(message, ex);
        }
    }

    @Override
    public List<CourseNotesReference> getSelfCourseNotesReferences(int courseNotesID, int commentID) throws ClassicDatabaseException, ClassicNotFoundException {

        String sql = "SELECT cc.\"ccID\",cc.\"coursenotesID\",cc.\"commentID\",cc.visible,c.\"title\",c.\"url\" "
                + "FROM (SELECT * FROM \"Coursenotes_comment\" WHERE \"coursenotesID\"=? AND \"commentID\"=?) AS cc "
                + "INNER JOIN \"Coursenotes\" AS c ON cc.\"coursenotesID\"=c.\"coursenotesID\";";
        try (Connection con = getConnection(); PreparedStatement stmt = con.prepareStatement(sql)) {
            if (!courseNotesDao.isCourseNotes(courseNotesID)) {
                throw new ClassicNotFoundException(ClassicNotFoundException.COURSENOTES_OBJECT, courseNotesID);
            }
            if (!commentDao.isComment(commentID)) {
                throw new ClassicNotFoundException(ClassicNotFoundException.COMMENT_OBJECT, commentID);
            }
            stmt.setInt(1, courseNotesID);
            stmt.setInt(2, commentID);
            ResultSet rs = stmt.executeQuery();
            List<CourseNotesReference> courseNotesReferences = new ArrayList<>();
            while (rs.next()) {
                CourseNotes courseNotes = new CourseNotes(rs.getString(COURSENOTESTITLE_COLUMN), rs.getString(COURSENOTESURL_COLUMN));
                int cID = rs.getInt(COURSENOTESID_COLUMN);
                courseNotes.setId(cID);
                int refID = rs.getInt(CCID_COLUMN);
                List<Location> locations = locationDao.getLocations(refID);
                CourseNotesReference courseNotesReference = new CourseNotesReference(courseNotes, locations);
                courseNotesReference.setRefId(refID);
                courseNotesReference.setVisible(rs.getBoolean(VISIBLE_COLUMN));
                courseNotesReferences.add(courseNotesReference);
            }

            return courseNotesReferences;
        } catch (SQLException ex) {
            String message = "A problem occured while trying to add a coursenotesreference to the Classic database";
            Logger.getLogger(CourseNotesReferenceDao.class.getName()).log(Level.SEVERE, message, ex);
            throw new ClassicDatabaseException(message, ex);
        }
    }

    @Override
    public List<CourseNotesReference> getCourseNotesReferences(int commentID) throws ClassicDatabaseException, ClassicNotFoundException {
        List<CourseNotesReference> courseNotesReferences = new ArrayList<>();
        String sql = "SELECT cc.\"ccID\",\"Coursenotes\".\"coursenotesID\",cc.visible,\"title\",\"url\" "
                + "FROM (SELECT * FROM \"Coursenotes_comment\" WHERE \"commentID\"=?) AS \"cc\" "
                + "INNER JOIN \"Coursenotes\" ON \"cc\".\"coursenotesID\"=\"Coursenotes\".\"coursenotesID\";";
        try (Connection con = getConnection(); PreparedStatement stmt = con.prepareStatement(sql)) {
            if (!commentDao.isComment(commentID)) {
                throw new ClassicNotFoundException(ClassicNotFoundException.COMMENT_OBJECT, commentID);
            }
            stmt.setInt(1, commentID);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                CourseNotes courseNotes = new CourseNotes(rs.getString(COURSENOTESTITLE_COLUMN), rs.getString(COURSENOTESURL_COLUMN));
                int cID = rs.getInt(COURSENOTESID_COLUMN);
                courseNotes.setId(cID);
                int refID = rs.getInt(CCID_COLUMN);
                List<Location> locations = locationDao.getLocations(refID);
                CourseNotesReference courseNotesReference = new CourseNotesReference(courseNotes, locations);
                courseNotesReference.setRefId(refID);
                courseNotesReference.setVisible(rs.getBoolean(VISIBLE_COLUMN));
                courseNotesReferences.add(courseNotesReference);
            }
            return courseNotesReferences;
        } catch (SQLException ex) {
            String message = "A problem occured while trying to get a coursenotesreferences from the Classic database";
            Logger.getLogger(CourseNotesReferenceDao.class.getName()).log(Level.SEVERE, message, ex);
            throw new ClassicDatabaseException(message, ex);
        }
    }

    protected boolean isCourseNotesReference(int refID) throws ClassicDatabaseException {
        String sql = "SELECT EXISTS(SELECT 1 FROM \"Coursenotes_comment\" WHERE \"ccID\"=?)";
        try (Connection con = getConnection(); PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, refID);
            ResultSet rs = stmt.executeQuery();
            rs.next();
            return rs.getBoolean("exists");
        } catch (SQLException ex) {
            String message = "A problem occured while trying to check the coursenotesreference with ID = " + refID + " from the Classic database";
            Logger.getLogger(CourseNotesReferenceDao.class.getName()).log(Level.SEVERE, message, ex);
            throw new ClassicDatabaseException(message, ex);
        }
    }

    @Override
    public void updateCourseNotesReference(CourseNotesReference courseNotesRef) throws ClassicDatabaseException, ClassicNotFoundException {
        if (!isCourseNotesReference(courseNotesRef.getRefId())) {
            throw new ClassicNotFoundException(ClassicNotFoundException.COURSENOTESREFERENCE_OBJECT, courseNotesRef.getRefId());
        }
        locationDao.updateLocations(courseNotesRef.getRefId(), courseNotesRef.getLocations());

    }

    @Override
    public void cleanTable() throws ClassicDatabaseException {
        String sql = "DELETE FROM \"Coursenotes_comment\"";
        try (Connection con = getConnection(); Statement stmt = con.createStatement()) {
            stmt.executeUpdate(sql);
        } catch (SQLException ex) {
            String message = "A problem occured while trying to clean the coursenotes_comment table in the database";
            Logger.getLogger(CourseNotesReferenceDao.class.getName()).log(Level.SEVERE, message, ex);
            throw new ClassicDatabaseException(message, ex);
        }
    }
}
