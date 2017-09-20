/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package databaseaccess.implementation;

import databaseaccess.exceptions.ClassicDatabaseException;
import databaseaccess.exceptions.ClassicNotFoundException;
import databaseaccess.interfaces.ILtiDao;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Data Access Object to work with lti key-secret pairs in the Classic database.
 * @author Jorsi Grammens
 */
public class LtiDao extends ClassicDatabaseConnection implements ILtiDao {

    private static final String SECRET_COLUMN = "secret";
    
    /**
     * Constructs an instance of <code>LtiDao</code>.
     * @param propertiesFileName Propreties file that contains information about the database connection.
     * @throws ClassicDatabaseException
     */
    public LtiDao(String propertiesFileName) throws ClassicDatabaseException {
        super(propertiesFileName);
    }

    @Override
    public void addKeySecretPair(String key, String secret) throws ClassicDatabaseException{
        String sql = "INSERT INTO \"Lti\" (\"key\",\"secret\") "
                + "VALUES (?,?);";
        try (Connection con = getConnection(); PreparedStatement stmtLti = con.prepareStatement(sql)) {
            int i = 1;
            stmtLti.setString(i++, key);
            stmtLti.setString(i++, secret);
            stmtLti.executeUpdate();
        } catch (SQLException ex) {
            String message = "A problem occured while trying to add an lti key-secret pair to the Classic database";
            Logger.getLogger(LtiDao.class.getName()).log(Level.SEVERE, message, ex);
            throw new ClassicDatabaseException(message, ex);
        }
    }

    @Override
    public void deleteKeySecretPair(String key) throws ClassicDatabaseException,ClassicNotFoundException{
        String sql = "DELETE FROM \"Lti\" "
                + "WHERE \"key\"=?;";
        try (Connection con = getConnection(); PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, key);
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new ClassicNotFoundException("The lti key-secret pair with key = \"" + key + "\" was not found in the Classic database");
            }
        } catch (SQLException ex) {
            String message = "A problem occured while trying to delete an lti key-secret pair to the Classic database";
            Logger.getLogger(LtiDao.class.getName()).log(Level.SEVERE, message, ex);
            throw new ClassicDatabaseException(message, ex);
        }
    }

    @Override
    public String getSecret(String key) throws ClassicDatabaseException,ClassicNotFoundException{
        String sql = "SELECT * FROM \"Lti\" WHERE \"key\" = ?";
        try (Connection con = getConnection(); PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, key);
            ResultSet rs = stmt.executeQuery();
            if (!rs.isBeforeFirst()) {
                throw new ClassicNotFoundException("The lti key-secret pair with key = \"" + key + "\" was not found in the Classic database");
            }
            rs.next();
            return rs.getString(SECRET_COLUMN);
        } catch (SQLException ex) {
            String message = "A problem occured while trying to get an lti key-secret pair to the Classic database";
            Logger.getLogger(LtiDao.class.getName()).log(Level.SEVERE, message, ex);
            throw new ClassicDatabaseException(message, ex);
        }
    }

    @Override
    public void cleanTable() throws ClassicDatabaseException {
       String sql = "DELETE FROM \"Lti\"";
        try (Connection con = getConnection(); Statement stmt = con.createStatement()) {
            stmt.executeUpdate(sql);
        } catch (SQLException ex) {
            String message = "A problem occured while trying to clean the lti table in the database";
            Logger.getLogger(VideoDao.class.getName()).log(Level.SEVERE, message, ex);
            throw new ClassicDatabaseException(message, ex);
        }
    }
    
}
