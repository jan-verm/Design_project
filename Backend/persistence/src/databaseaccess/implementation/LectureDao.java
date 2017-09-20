/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package databaseaccess.implementation;

import courses.Course;
import courses.Lecture;
import courses.User;
import databaseaccess.exceptions.ClassicDatabaseException;
import databaseaccess.exceptions.ClassicNotFoundException;
import databaseaccess.interfaces.ILectureDao;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Data Access Object to work with lectures in the Classic database.
 * @author Jorsi Grammens
 */
public class LectureDao extends ClassicDatabaseConnection implements ILectureDao {

    private static final String LECTUREID_COLUMN = "lectureID";
    private static final String TITLE_COLUMN = "title";
    
    private CourseDao courseDao; 
    
    /**
     * Constructs an instance of <code>LectureDao</code>.
     * @param propertiesFileName Propreties file that contains information about the database connection.
     * @throws ClassicDatabaseException
     */
    public LectureDao(String propertiesFileName) throws ClassicDatabaseException {
        super(propertiesFileName);
        courseDao = new CourseDao(propertiesFileName);
    }

    @Override
    public int addLecture(int parentID, Lecture lecture) throws ClassicDatabaseException, ClassicNotFoundException {  
        String sql = "INSERT INTO \"Lecture\" (\"userID\",\"courseID\",title) "
                + "VALUES (?,?,?) RETURNING *;";
        try (Connection con = getConnection(); PreparedStatement stmt = con.prepareStatement(sql)) {
            if (!courseDao.isCourse(parentID)) {
                throw new ClassicNotFoundException(ClassicNotFoundException.COURSE_OBJECT,parentID);
            }
            int i = 1;
            User owner = courseDao.getOwner(parentID);
            stmt.setInt(i++, owner.getId());
            stmt.setInt(i++, parentID);
            stmt.setString(i++, lecture.getName());
            ResultSet rs = stmt.executeQuery();
            rs.next();
            int id = rs.getInt(LECTUREID_COLUMN);
            lecture.setId(id);
            return id;
        } catch (SQLException ex) {
            String message = "A problem occured while trying to add a lecture to the Classic database";
            Logger.getLogger(LectureDao.class.getName()).log(Level.SEVERE, message, ex);
            throw new ClassicDatabaseException(message, ex);
        }
    }

    @Override
    public void deleteLecture(int lectureID) throws ClassicNotFoundException, ClassicDatabaseException {
        String sql = "DELETE FROM \"Lecture\" "
                + "WHERE \"lectureID\"=?;";
        try (Connection con = getConnection(); PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, lectureID);
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new ClassicNotFoundException(ClassicNotFoundException.LECTURE_OBJECT,lectureID);
            }
        } catch (SQLException ex) {
            String message = "A problem occured while trying to delete a lecture from the Classic database";
            Logger.getLogger(LectureDao.class.getName()).log(Level.SEVERE, message, ex);
            throw new ClassicDatabaseException(message, ex);
        }
    }

    @Override
    public Lecture getLecture(int lectureId) throws ClassicNotFoundException, ClassicDatabaseException {
        String sql = "SELECT * FROM \"Lecture\" WHERE \"lectureID\" = ?";
        try (Connection con = getConnection(); PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, lectureId);
            ResultSet rs = stmt.executeQuery();
            if (!rs.isBeforeFirst()) {
                throw new ClassicNotFoundException(ClassicNotFoundException.LECTURE_OBJECT,lectureId);
            }
            rs.next();
            Course course = new Course(rs.getString(TITLE_COLUMN));
            course.setId(rs.getInt(LECTUREID_COLUMN));
            return course;
        } catch (SQLException ex) {
            String message = "A problem occured while trying to get the lecture from the Classic database";
            Logger.getLogger(LectureDao.class.getName()).log(Level.SEVERE, message, ex);
            throw new ClassicDatabaseException(message, ex);
        }
    }

    @Override
    public List<Lecture> getLectures(int parentID) throws ClassicDatabaseException, ClassicNotFoundException {
        String sql = "SELECT * FROM \"Lecture\" WHERE \"courseID\"=?";
        try (Connection con = getConnection(); PreparedStatement stmt = con.prepareStatement(sql)) {
            if (!courseDao.isCourse(parentID)) {
                throw new ClassicNotFoundException(ClassicNotFoundException.COURSE_OBJECT,parentID);
            }
            stmt.setInt(1, parentID);
            ResultSet rs = stmt.executeQuery();
            List<Lecture> lectures = new ArrayList<>();
            while (rs.next()) {
                Lecture lecture = new Lecture(rs.getString(TITLE_COLUMN));
                lecture.setId(rs.getInt(LECTUREID_COLUMN));
                lectures.add(lecture);
            }
            return lectures;
        } catch (SQLException ex) {
            String message = "A problem occured while trying to get lectures from the Classic database";
            Logger.getLogger(LectureDao.class.getName()).log(Level.SEVERE, message, ex);
            throw new ClassicDatabaseException(message, ex);
        }
    }

    protected boolean isLecture(int lectureID) throws ClassicDatabaseException{
        String sql = "SELECT EXISTS(SELECT 1 FROM \"Lecture\" WHERE \"lectureID\"=?)";
        try (Connection con = getConnection(); PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, lectureID);
            ResultSet rs = stmt.executeQuery();
            rs.next();
            return rs.getBoolean("exists");
        } catch (SQLException ex) {
            String message = "A problem occured while trying to check the comment in the Classic database";
            Logger.getLogger(CourseDao.class.getName()).log(Level.SEVERE, message, ex);
            throw new ClassicDatabaseException(message, ex);
        }
    }
    
    @Override
    public void updateLecture(Lecture lecture) throws ClassicDatabaseException, ClassicNotFoundException {
        String sql = "UPDATE \"Lecture\" "
                + "SET \"title\"=? "
                + "WHERE \"lectureID\"=?;";
        try (Connection con = getConnection(); PreparedStatement stmt = con.prepareStatement(sql)) {
            int i = 1;            
            stmt.setString(i++, lecture.getName());
            stmt.setInt(i++, lecture.getId());
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new ClassicNotFoundException(ClassicNotFoundException.LECTURE_OBJECT,lecture.getId());
            }
        } catch (SQLException ex) {
            String message = "A problem occured while trying to update the lecture in the Classic database";
            Logger.getLogger(LectureDao.class.getName()).log(Level.SEVERE, message, ex);
            throw new ClassicDatabaseException(message, ex);
        }
    }

    @Override
    public void cleanTable() throws ClassicDatabaseException {
        String sql = "DELETE FROM \"Lecture\"";
        try (Connection con = getConnection(); Statement stmt = con.createStatement()) {
            stmt.executeUpdate(sql);
        } catch (SQLException ex) {
            String message = "A problem occured while trying to clean the lecture table in the database";
            Logger.getLogger(LectureDao.class.getName()).log(Level.SEVERE, message, ex);
            throw new ClassicDatabaseException(message, ex);
        }
    }

    @Override
    public User getOwner(int lectureID) throws ClassicDatabaseException, ClassicNotFoundException {        
        try {
            return courseDao.getOwner(lectureID);
        } catch (ClassicDatabaseException ex) {
            String message = "A problem occured while trying to get the lectureowner the Classic database";
            Logger.getLogger(LectureDao.class.getName()).log(Level.SEVERE, message, ex);
            throw new ClassicDatabaseException(message, ex);
        }
    }

}
