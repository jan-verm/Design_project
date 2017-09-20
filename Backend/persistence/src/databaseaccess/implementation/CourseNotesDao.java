/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package databaseaccess.implementation;

import courses.User;
import databaseaccess.exceptions.ClassicDatabaseException;
import databaseaccess.exceptions.ClassicNotFoundException;
import databaseaccess.interfaces.ICourseNotesDao;
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

/**
 * Data Access Object to work with coursenotes in the Classic database.
 * @author Jorsi Grammens
 */
public class CourseNotesDao extends ClassicDatabaseConnection implements ICourseNotesDao {

    private static final String COURSENOTESID_COLUMN = "coursenotesID";
    private static final String TITLE_COLUMN = "title";
    private static final String URL_COLUMN = "url";
    private static final String PARENTID_COLUMN = "parentID";
    
    private CourseDao courseDao;
    
    /**
     * Constructs an instance of <code>CourseNotesDao</code>.
     * @param propertiesFileName Propreties file that contains information about the database connection.
     * @throws ClassicDatabaseException
     */
    public CourseNotesDao(String propertiesFileName) throws ClassicDatabaseException {
        super(propertiesFileName);
        courseDao = new CourseDao(propertiesFileName);
    }

    @Override
    public int addCourseNotes(int parentID, CourseNotes courseNotes) throws ClassicDatabaseException, ClassicNotFoundException {
        String sql = "INSERT INTO \"Coursenotes\" (title,url,\"parentID\") "
                + "VALUES (?,?,?) RETURNING *;";
        try (Connection con = getConnection(); PreparedStatement stmt = con.prepareStatement(sql)) {
            if (!courseDao.isCourse(parentID)) {
                throw new ClassicNotFoundException(ClassicNotFoundException.COURSE_OBJECT,parentID);
            }
            stmt.setString(1, courseNotes.getTitle());
            stmt.setString(2, courseNotes.getUrl());
            stmt.setInt(3, parentID);
            ResultSet rs = stmt.executeQuery();
            rs.next();
            int id = rs.getInt(COURSENOTESID_COLUMN);
            courseNotes.setId(id);
            return id;
        } catch (SQLException ex) {
            String message = "A problem occured while trying to add coursenotes to the Classic database";
            Logger.getLogger(CourseNotesDao.class.getName()).log(Level.SEVERE, message, ex);
            throw new ClassicDatabaseException(message, ex);
        }
    }

    @Override
    public void deleteCourseNotes(int courseNotesID) throws ClassicDatabaseException, ClassicNotFoundException {
        String sql = "DELETE FROM \"Coursenotes\" "
                + "WHERE \"coursenotesID\"=?;";
        try (Connection con = getConnection(); PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, courseNotesID);
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new ClassicNotFoundException(ClassicNotFoundException.COURSENOTES_OBJECT,courseNotesID);
            }
        } catch (SQLException ex) {
            String message = "A problem occured while trying to delete courseNotes from the Classic database";
            Logger.getLogger(CourseNotesDao.class.getName()).log(Level.SEVERE, message, ex);
            throw new ClassicDatabaseException(message, ex);
        }
    }
    
    @Override
    public List<CourseNotes> getAllCourseNotes() throws ClassicDatabaseException {
        String sql = "SELECT * FROM \"Coursenotes\"";
        try (Connection con = getConnection(); Statement stmt = con.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            List<CourseNotes> courseNotes = new ArrayList<>();
            while (rs.next()) {
                CourseNotes courseNote = new CourseNotes(rs.getString(TITLE_COLUMN), rs.getString(URL_COLUMN));
                courseNote.setId(rs.getInt(COURSENOTESID_COLUMN));
                courseNotes.add(courseNote);
            }
            return courseNotes;
        } catch (SQLException ex) {
            String message = "A problem occured while trying to get coursenotes from the Classic database";
            Logger.getLogger(CourseNotesDao.class.getName()).log(Level.SEVERE, message, ex);
            throw new ClassicDatabaseException(message, ex);
        }
    }

    @Override
    public List<CourseNotes> getParentCourseNotes(int parentID) throws ClassicDatabaseException, ClassicNotFoundException {
        String sql = "SELECT * FROM \"Coursenotes\" WHERE \"parentID\"=?";
        try (Connection con = getConnection(); PreparedStatement stmt = con.prepareStatement(sql)) {
            if (!courseDao.isCourse(parentID)) {
                throw new ClassicNotFoundException(ClassicNotFoundException.COURSE_OBJECT,parentID);
            }
            int i = 1;
            stmt.setInt(i++, parentID);
            ResultSet rs = stmt.executeQuery();
            List<CourseNotes> courseNotes = new ArrayList<>();
            while (rs.next()) {
                CourseNotes courseNote = new CourseNotes(rs.getString(TITLE_COLUMN), rs.getString(URL_COLUMN));
                courseNote.setId(rs.getInt(COURSENOTESID_COLUMN));
                courseNotes.add(courseNote);
            }
            return courseNotes;
        } catch (SQLException ex) {
            String message = "A problem occured while trying to get coursenotes from the Classic database";
            Logger.getLogger(CourseNotesDao.class.getName()).log(Level.SEVERE, message, ex);
            throw new ClassicDatabaseException(message, ex);
        }
    }


    @Override
    public CourseNotes getCourseNotes(int courseNotesID) throws ClassicDatabaseException, ClassicNotFoundException {
        String sql = "SELECT * FROM \"Coursenotes\" WHERE \"coursenotesID\" = ?";
        try (Connection con = getConnection(); PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, courseNotesID);
            ResultSet rs = stmt.executeQuery();
            if (!rs.isBeforeFirst()) {
                throw new ClassicNotFoundException(ClassicNotFoundException.COURSENOTES_OBJECT,courseNotesID);
            }
            rs.next();
            CourseNotes courseNotes = new CourseNotes(rs.getString(TITLE_COLUMN), rs.getString(URL_COLUMN));
            courseNotes.setId(rs.getInt(COURSENOTESID_COLUMN));
            return courseNotes;
        } catch (SQLException ex) {
            String message = "A problem occured while trying to get the coursenotes from the Classic database";
            Logger.getLogger(CourseNotesDao.class.getName()).log(Level.SEVERE, message, ex);
            throw new ClassicDatabaseException(message, ex);
        }
    }

    protected boolean isCourseNotes(int courseNotesID) throws ClassicDatabaseException {
        String sql = "SELECT EXISTS(SELECT 1 FROM \"Coursenotes\" WHERE \"coursenotesID\"=?)";
        try (Connection con = getConnection(); PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, courseNotesID);
            ResultSet rs = stmt.executeQuery();
            rs.next();
            return rs.getBoolean("exists");
        } catch (SQLException ex) {
            String message = "A problem occured while trying to check the coursenotes in the Classic database";
            Logger.getLogger(CourseNotesDao.class.getName()).log(Level.SEVERE, message, ex);
            throw new ClassicDatabaseException(message, ex);
        }
    }

    @Override
    public void updateCourseNotes(CourseNotes courseNotes) throws ClassicDatabaseException, ClassicNotFoundException {
        String sql = "UPDATE \"Coursenotes\" "
                + "SET \"title\"=?,\"url\"=? "
                + "WHERE \"coursenotesID\"=?;";
        try (Connection con = getConnection(); PreparedStatement stmt = con.prepareStatement(sql)) {
            int i = 1;
            stmt.setString(i++, courseNotes.getTitle());
            stmt.setString(i++, courseNotes.getUrl());            
            stmt.setInt(i++, courseNotes.getId());
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new ClassicNotFoundException(ClassicNotFoundException.COURSENOTES_OBJECT,courseNotes.getId());
            }
        } catch (SQLException ex) {
            String message = "A problem occured while trying to update the coursenotes with in the Classic database";
            Logger.getLogger(CourseNotesDao.class.getName()).log(Level.SEVERE, message, ex);
            throw new ClassicDatabaseException(message, ex);
        }
    }

    @Override
    public void cleanTable() throws ClassicDatabaseException {
        String sql = "DELETE FROM \"Coursenotes\"";
        try (Connection con = getConnection(); Statement stmt = con.createStatement()) {
            stmt.executeUpdate(sql);
        } catch (SQLException ex) {
            String message = "A problem occured while trying to clean the coursenotes table in the database";
            Logger.getLogger(CourseNotesDao.class.getName()).log(Level.SEVERE, message, ex);
            throw new ClassicDatabaseException(message, ex);
        }
    }        

    @Override
    public User getOwner(int coursenotesID) throws ClassicDatabaseException, ClassicNotFoundException {
        String sql = "SELECT \"parentID\" FROM \"Coursenotes\" WHERE \"coursenotesID\" = ?";
        try (Connection con = getConnection(); PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, coursenotesID);
            ResultSet rs = stmt.executeQuery();
            if (!rs.isBeforeFirst()) {
                throw new ClassicNotFoundException(ClassicNotFoundException.COURSENOTES_OBJECT,coursenotesID);
            }
            rs.next();
            int parentID = rs.getInt(PARENTID_COLUMN);            
            return courseDao.getOwner(parentID);
        } catch (SQLException ex) {
            String message = "A problem occured while trying to get the coursenotes owner from the Classic database";
            Logger.getLogger(CourseNotesDao.class.getName()).log(Level.SEVERE, message, ex);
            throw new ClassicDatabaseException(message, ex);
        }
    }
}
