/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package databaseaccess.implementation;

import courses.Course;
import courses.User;
import databaseaccess.exceptions.ClassicDatabaseException;
import databaseaccess.exceptions.ClassicNameTakenException;
import databaseaccess.exceptions.ClassicNotFoundException;
import databaseaccess.interfaces.ICourseDao;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Data Access Object to work with courses in the Classic database.
 * @author Jorsi Grammens
 */
public class CourseDao extends ClassicDatabaseConnection implements ICourseDao {

    private UserDao userDao;
    private static final String LECTUREID_COLUMN = "lectureID";
    private static final String TITLE_COLUMN = "title";
    private static final String USERID_COLUMN = "userID";
    
    /**
     * Constructs an instance of <code>CourseDao</code>.
     * @param propertiesFileName Propreties file that contains information about the database connection.
     * @throws ClassicDatabaseException
     */
    public CourseDao(String propertiesFileName) throws ClassicDatabaseException {
        super(propertiesFileName);
        userDao = new UserDao(propertiesFileName);
    }
    
    private void checkUniqueness(String courseName) throws ClassicDatabaseException {
        String sql = "SELECT * FROM \"Lecture\" WHERE \"title\" = ?";
        try (Connection con = getConnection(); PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, courseName);
            ResultSet rs = stmt.executeQuery();
            if (rs.isBeforeFirst()) {
                 throw new ClassicNameTakenException("The course name " + courseName + " is taken.");
            }
        } catch (SQLException ex) {
            String message = "A problem occured while trying to add a course to the Classic database";
            Logger.getLogger(CourseDao.class.getName()).log(Level.SEVERE, message, ex);
            throw new ClassicDatabaseException(message, ex);
        }
    }

    @Override
    public int addCourse(Course course) throws ClassicDatabaseException {
        String sql = "INSERT INTO \"Lecture\" (\"courseID\",title,\"userID\") "
                + "VALUES (?,?,?) RETURNING *;";
        try (Connection con = getConnection(); PreparedStatement stmt = con.prepareStatement(sql)) {
            checkUniqueness(course.getName());
            int i = 1;
            stmt.setNull(i++, Types.INTEGER);
            stmt.setString(i++, course.getName());
            stmt.setInt(i++, course.getOwner().getId());
            ResultSet rs = stmt.executeQuery();
            rs.next();
            int id = rs.getInt(LECTUREID_COLUMN);
            course.setId(id);
            return id;
        } catch (SQLException ex) {
            String message = "A problem occured while trying to add a course to the Classic database";
            Logger.getLogger(CourseDao.class.getName()).log(Level.SEVERE, message, ex);
            throw new ClassicDatabaseException(message, ex);
        }
    }

    @Override
    public void deleteCourse(int courseID) throws ClassicDatabaseException, ClassicNotFoundException {
        String sql = "DELETE FROM \"Lecture\" "
                + "WHERE \"lectureID\"=?;";
        try (Connection con = getConnection(); PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, courseID);
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new ClassicNotFoundException(ClassicNotFoundException.COURSE_OBJECT,courseID);
            }
        } catch (SQLException ex) {
            String message = "A problem occured while trying to delete a course from the Classic database";
            Logger.getLogger(CourseDao.class.getName()).log(Level.SEVERE, message, ex);
            throw new ClassicDatabaseException(message, ex);
        }
    }

    @Override
    public Course getCourse(int courseID) throws ClassicDatabaseException, ClassicNotFoundException {
        String sql = "SELECT * FROM \"Lecture\" WHERE \"lectureID\" = ?";
        try (Connection con = getConnection(); PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, courseID);
            ResultSet rs = stmt.executeQuery();
            if (!rs.isBeforeFirst()) {
                throw new ClassicNotFoundException(ClassicNotFoundException.COURSE_OBJECT,courseID);
            }
            rs.next();
            Course course = new Course(rs.getString(TITLE_COLUMN));
            course.setId(rs.getInt(LECTUREID_COLUMN));
            course.setOwner(userDao.getUser(rs.getInt("userID")));
            return course;
        } catch (SQLException ex) {
            String message = "A problem occured while trying to get the course the Classic database";
            Logger.getLogger(CourseDao.class.getName()).log(Level.SEVERE, message, ex);
            throw new ClassicDatabaseException(message, ex);
        }
    }

    @Override
    public List<Course> getCourses() throws ClassicDatabaseException {
        String sql = "SELECT * FROM \"Lecture\" WHERE \"courseID\" IS NULL";
        try (Connection con = getConnection(); Statement stmt = con.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            List<Course> courses = new ArrayList<>();
            while (rs.next()) {
                Course course = new Course(rs.getString(TITLE_COLUMN));
                course.setId(rs.getInt(LECTUREID_COLUMN));
                courses.add(course);
            }
            return courses;
        } catch (SQLException ex) {
            String message = "A problem occured while trying to get courses from the Classic database";
            Logger.getLogger(CourseDao.class.getName()).log(Level.SEVERE, message, ex);
            throw new ClassicDatabaseException(message, ex);
        }
    }

    protected boolean isCourse(int courseID) throws ClassicDatabaseException {
        String sql = "SELECT EXISTS(SELECT 1 FROM \"Lecture\" WHERE \"lectureID\"=?)";
        try (Connection con = getConnection(); PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, courseID);
            ResultSet rs = stmt.executeQuery();
            rs.next();
            return rs.getBoolean("exists");
        } catch (SQLException ex) {
            String message = "A problem occured while trying to check the course in the Classic database";
            Logger.getLogger(CourseDao.class.getName()).log(Level.SEVERE, message, ex);
            throw new ClassicDatabaseException(message, ex);
        }
    }

    @Override
    public void updateCourse(Course course) throws ClassicDatabaseException, ClassicNotFoundException {
        
        String sql = "UPDATE \"Lecture\" "
                + "SET \"title\"=? "
                + "WHERE \"lectureID\"=?;";
        try (Connection con = getConnection(); PreparedStatement stmt = con.prepareStatement(sql)) {
            checkUniqueness(course.getName());
            int i = 1;
            stmt.setString(i++, course.getName());
            stmt.setInt(i++, course.getId());
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new ClassicNotFoundException(ClassicNotFoundException.COURSE_OBJECT,course.getId());
            }
        } catch (SQLException ex) {
            String message = "A problem occured while trying to update the course from the Classic database";
            Logger.getLogger(CourseDao.class.getName()).log(Level.SEVERE, message, ex);
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
            Logger.getLogger(CourseDao.class.getName()).log(Level.SEVERE, message, ex);
            throw new ClassicDatabaseException(message, ex);
        }
    }

    @Override
    public User getOwner(int courseID) throws ClassicDatabaseException, ClassicNotFoundException {
         String sql = "SELECT \"userID\" FROM \"Lecture\" WHERE \"lectureID\" = ?;";
        try (Connection con = getConnection(); PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, courseID);
            ResultSet rs = stmt.executeQuery();
            if (!rs.isBeforeFirst()) {
                throw new ClassicNotFoundException(ClassicNotFoundException.COURSE_OBJECT,courseID);
            }
            rs.next();
            User user = userDao.getUser(rs.getInt(USERID_COLUMN));            
            return user;
        } catch (SQLException ex) {
            String message = "A problem occured while trying to get the courseowner the Classic database";
            Logger.getLogger(CourseDao.class.getName()).log(Level.SEVERE, message, ex);
            throw new ClassicDatabaseException(message, ex);
        }
    }

}
