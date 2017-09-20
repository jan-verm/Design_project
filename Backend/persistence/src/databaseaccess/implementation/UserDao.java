/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package databaseaccess.implementation;

import courses.Course;
import courses.Role;
import courses.User;
import databaseaccess.exceptions.ClassicDatabaseException;
import databaseaccess.exceptions.ClassicNotFoundException;
import databaseaccess.exceptions.ClassicNameTakenException;
import databaseaccess.interfaces.IUserDao;
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
 * Data Access Object to work with users in the Classic database.
 * @author Jorsi Grammens
 */
public class UserDao extends ClassicDatabaseConnection implements IUserDao {

    private static final String USERID_COLUMN = "userID";
    private static final String USERNAME_COLUMN = "username";
    private static final String ROLE_COLUMN = "role";
    private static final String PASS_COLUMN = "password";
    private static final String LECTUREID_COLUMN = "lectureID";
    private static final String TITLE_COLUMN = "title";     

    /**
     * Constructs an instance of <code>UserDao</code>.
     * @param propertiesFileName Propreties file that contains information about the database connection.
     * @throws ClassicDatabaseException
     */
    public UserDao(String propertiesFileName) throws ClassicDatabaseException {
        super(propertiesFileName);
    }

    @Override
    public int addUser(User user) throws ClassicDatabaseException {
        String sqlInsertUser = "INSERT INTO \"User\" (username,role,password) "
                + "VALUES (?,?,?) RETURNING *;";
        try (Connection con = getConnection(); PreparedStatement stmt = con.prepareStatement(sqlInsertUser)) {
            if (isTaken(user.getUsername())) {
                throw new ClassicNameTakenException("The username " + user.getUsername()+ " is taken.");
            }
            int i = 1;
            stmt.setString(i++, user.getUsername());
            stmt.setInt(i++, user.getRole().getValue());
            stmt.setString(i++, user.getPassword());
            ResultSet rs = stmt.executeQuery();
            rs.next();
            int id = rs.getInt(USERID_COLUMN);
            user.setId(id);
            return id;
        } catch (SQLException ex) {
            String message = "A problem occured while trying to add a user to the Classic database";
            Logger.getLogger(UserDao.class.getName()).log(Level.SEVERE, message, ex);
            throw new ClassicDatabaseException(message, ex);
        }
    }

    @Override
    public void deleteUser(int userID) throws ClassicDatabaseException, ClassicNotFoundException {
        String sql = "DELETE FROM \"User\" "
                + "WHERE \"userID\"=?;";
        try (Connection con = getConnection(); PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, userID);
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new ClassicNotFoundException(ClassicNotFoundException.USER_OBJECT, userID);
            }
        } catch (SQLException ex) {
            String message = "A problem occured while trying to delete a user from the Classic database";
            Logger.getLogger(UserDao.class.getName()).log(Level.SEVERE, message, ex);
            throw new ClassicDatabaseException(message, ex);
        }
    }

    @Override
    public User getUser(int userID) throws ClassicDatabaseException, ClassicNotFoundException {
        String sql = "SELECT * FROM \"User\" WHERE \"userID\" = ?";
        try (Connection con = getConnection(); PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, userID);
            ResultSet rs = stmt.executeQuery();
            if (!rs.isBeforeFirst()) {
                throw new ClassicNotFoundException(ClassicNotFoundException.USER_OBJECT, userID);
            }
            rs.next();
            User user = new User(rs.getString(USERNAME_COLUMN), Role.values()[rs.getInt(ROLE_COLUMN)], rs.getString(PASS_COLUMN));            
            user.setId(userID);            
            return user;
        } catch (SQLException ex) {
            String message = "A problem occured while trying to get the user from the Classic database";
            Logger.getLogger(UserDao.class.getName()).log(Level.SEVERE, message, ex);
            throw new ClassicDatabaseException(message, ex);
        }
    }

    /**
     *
     * @param username
     * @return
     * @throws ClassicDatabaseException
     * @throws ClassicNotFoundException
     */
    @Override
    public User getUser(String username) throws ClassicDatabaseException, ClassicNotFoundException {
        String sql = "SELECT * FROM \"User\" WHERE \"username\" = ?";
        try (Connection con = getConnection(); PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (!rs.isBeforeFirst()) {
                throw new ClassicNotFoundException("No user with username = \"" + username + "\" was found");
            }
            rs.next();
            User user = new User(rs.getString(USERNAME_COLUMN), Role.values()[rs.getInt(ROLE_COLUMN)], rs.getString(PASS_COLUMN));
            int userID = rs.getInt(USERID_COLUMN);
            user.setId(userID);            
            return user;
        } catch (SQLException ex) {
            String message = "A problem occured while trying to get the user from the Classic database";
            Logger.getLogger(UserDao.class.getName()).log(Level.SEVERE, message, ex);
            throw new ClassicDatabaseException(message, ex);
        }
    }

    @Override
    public List<User> getUsers(Role role) throws ClassicDatabaseException {
        String sql = "SELECT * FROM \"User\" WHERE role=?";
        try (Connection con = getConnection(); PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, role.getValue());
            ResultSet rs = stmt.executeQuery();
            List<User> users = new ArrayList<>();
            while (rs.next()) {
                User user = new User(rs.getString(USERNAME_COLUMN), Role.values()[rs.getInt(ROLE_COLUMN)], rs.getString(PASS_COLUMN));
                int userID = rs.getInt(USERID_COLUMN);
                user.setId(userID);
                users.add(user);
            }
            return users;
        } catch (SQLException ex) {
            String message = "A problem occured while trying to get users from the Classic database";
            Logger.getLogger(UserDao.class.getName()).log(Level.SEVERE, message, ex);
            throw new ClassicDatabaseException(message, ex);
        }
    }

    @Override
    public List<User> getUsers() throws ClassicDatabaseException {
        String sql = "SELECT * FROM \"User\"";
        try (Connection con = getConnection(); Statement stmt = con.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            List<User> users = new ArrayList<>();
            while (rs.next()) {
                User user = new User(rs.getString(USERNAME_COLUMN), Role.values()[rs.getInt(ROLE_COLUMN)], rs.getString(PASS_COLUMN));
                int userID = rs.getInt(USERID_COLUMN);
                user.setId(userID);                
                users.add(user);
            }
            return users;
        } catch (SQLException ex) {
            String message = "A problem occured while trying to get users from the Classic database";
            Logger.getLogger(UserDao.class.getName()).log(Level.SEVERE, message, ex);
            throw new ClassicDatabaseException(message, ex);
        } 
    }

    protected boolean isUser(int userID) throws ClassicDatabaseException {
        String sql = "SELECT EXISTS(SELECT 1 FROM \"User\" WHERE \"userID\"=?)";
        try (Connection con = getConnection(); PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, userID);
            ResultSet rs = stmt.executeQuery();
            rs.next();
            return rs.getBoolean("exists");
        } catch (SQLException ex) {
            String message = "A problem occured while trying to check the user with ID = " + userID + " from the Classic database";
            Logger.getLogger(UserDao.class.getName()).log(Level.SEVERE, message, ex);
            throw new ClassicDatabaseException(message, ex);
        }
    }

    @Override
    public void updateUser(User user) throws ClassicDatabaseException, ClassicNotFoundException {
        String sql = "UPDATE \"User\" "
                + "SET \"username\"=?,\"role\"=? "
                + "WHERE \"userID\"=?;";
        try (Connection con = getConnection(); PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, user.getUsername());
            stmt.setInt(2, user.getRole().getValue());
            stmt.setInt(3, user.getId());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new ClassicNotFoundException(ClassicNotFoundException.USER_OBJECT, user.getId());
            }
        } catch (SQLException ex) {
            String message = "A problem occured while trying to update the user in the Classic database";
            Logger.getLogger(UserDao.class.getName()).log(Level.SEVERE, message, ex);
            throw new ClassicDatabaseException(message, ex);
        }
    }

    @Override
    public void cleanTable() throws ClassicDatabaseException {
        String sql = "DELETE FROM \"User\";";
        try (Connection con = getConnection(); Statement stmt = con.createStatement()) {
            stmt.executeUpdate(sql);
        } catch (SQLException ex) {
            String message = "A problem occured while trying to clean the user table in the database";
            Logger.getLogger(UserDao.class.getName()).log(Level.SEVERE, message, ex);
            throw new ClassicDatabaseException(message, ex);
        }
    }
    
    private boolean checkSubscription(int userID, int courseID) throws ClassicDatabaseException {
        String sql = "SELECT * FROM \"Subscription\" WHERE \"userID\"=? AND \"courseID\"=?;";
        try (Connection con = getConnection(); PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, userID);
            stmt.setInt(2, courseID);
            ResultSet rs = stmt.executeQuery();
            if (rs.isBeforeFirst()) {
                 return true;
            }
            return false;
        } catch (SQLException ex) {
            String message = "A problem occured while trying to add a course to the Classic database";
            Logger.getLogger(CourseDao.class.getName()).log(Level.SEVERE, message, ex);
            throw new ClassicDatabaseException(message, ex);
        }
    }

    @Override
    public void subscribeCourse(int userID, int courseID) throws ClassicDatabaseException, ClassicNotFoundException {
        if (!checkSubscription(userID, courseID)){
            String sqlInsertUser = "INSERT INTO \"Subscription\" (\"userID\",\"courseID\") "
                    + "VALUES (?,?) RETURNING *;";
            try (Connection con = getConnection(); PreparedStatement stmt = con.prepareStatement(sqlInsertUser)) {
                if (!isUser(userID)) {
                    throw new ClassicNotFoundException(ClassicNotFoundException.USER_OBJECT, userID);
                }
                int i = 1;            
                stmt.setInt(i++, userID);
                stmt.setInt(i++, courseID);
                ResultSet rs = stmt.executeQuery();
                rs.next();            
            } catch (SQLException ex) {
                String message = "A problem occured while trying to add a subscription to the Classic database";
                Logger.getLogger(UserDao.class.getName()).log(Level.SEVERE, message, ex);
                throw new ClassicDatabaseException(message, ex);
            }
        }
    }

    @Override
    public void unsubscribeCourse(int userID, int courseID) throws ClassicDatabaseException, ClassicNotFoundException {
        String sql = "DELETE FROM \"Subscription\" WHERE \"userID\"=? AND \"courseID\"=?;";
        try (Connection con = getConnection(); PreparedStatement stmt = con.prepareStatement(sql)) {
            if (!isUser(userID)) {
                throw new ClassicNotFoundException(ClassicNotFoundException.USER_OBJECT, userID);
            }
            int i = 1;
            stmt.setInt(i++, userID);
            stmt.setInt(i++, courseID);
            stmt.executeUpdate();
        } catch (SQLException ex) {
            String message = "A problem occured while trying to clean the user table in the database";
            Logger.getLogger(UserDao.class.getName()).log(Level.SEVERE, message, ex);
            throw new ClassicDatabaseException(message, ex);
        }
    }

    @Override
    public void updateUserPassword(int userID, String newPassword) throws ClassicDatabaseException, ClassicNotFoundException {
        String sqlComment = "UPDATE \"User\" "
                + "SET \"password\"=? "
                + "WHERE \"userID\"=?;";
        try (Connection con = getConnection(); PreparedStatement stmt = con.prepareStatement(sqlComment); ) {
            if (!isUser(userID)) {
                throw new ClassicNotFoundException(ClassicNotFoundException.USER_OBJECT, userID);
            }            
            int i = 1;
            stmt.setString(i++, newPassword);
            stmt.setInt(i++, userID);
            stmt.executeUpdate();
        } catch (SQLException ex) {
            String message = "A problem occured while trying to change the password in the Classic database";
            Logger.getLogger(UserDao.class.getName()).log(Level.SEVERE, message, ex);
            throw new ClassicDatabaseException(message, ex);
        }
    }

    @Override
    public boolean isTaken(String username) throws ClassicDatabaseException {
        String sql = "SELECT EXISTS(SELECT 1 FROM \"User\" WHERE \"username\"=?)";
        try (Connection con = getConnection(); PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            rs.next();
            return rs.getBoolean("exists");
        } catch (SQLException ex) {
            String message = "A problem occured while trying to check the user in the Classic database";
            Logger.getLogger(UserDao.class.getName()).log(Level.SEVERE, message, ex);
            throw new ClassicDatabaseException(message, ex);
        }
    }

    @Override
    public List<Course> getSubscriptions(int userID) throws ClassicDatabaseException, ClassicNotFoundException {
        String sql = "SELECT \"Lecture\".\"lectureID\",\"Lecture\".\"courseID\",\"Lecture\".\"title\",\"Lecture\".\"userID\" "
                + "FROM (SELECT * FROM \"Subscription\" WHERE \"userID\"=?) AS subs "
                + "INNER JOIN \"Lecture\" ON \"Lecture\".\"lectureID\"=subs.\"courseID\";";
        try (Connection con = getConnection(); PreparedStatement stmt = con.prepareStatement(sql)) {
            int i = 1;
            stmt.setInt(i++, userID);
            ResultSet rs = stmt.executeQuery();
            List<Course> courses = new ArrayList<>();
            while (rs.next()) {
                Course course = new Course(rs.getString(TITLE_COLUMN));
                course.setOwner(getUser(rs.getInt(USERID_COLUMN)));
                course.setId(rs.getInt(LECTUREID_COLUMN));
                courses.add(course);
            }
            return courses;
        } catch (SQLException ex) {
            String message = "A problem occured while trying to get subscriptions from the Classic database";
            Logger.getLogger(UserDao.class.getName()).log(Level.SEVERE, message, ex);
            throw new ClassicDatabaseException(message, ex);
        }
    }
}
