/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package databaseaccess.implementation;

import databaseaccess.exceptions.ClassicDatabaseException;
import databaseaccess.exceptions.ClassicNotFoundException;
import databaseaccess.interfaces.ILocationDao;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import models.Location;

/**
 * Data Access Object to work with locations in the Classic database.
 * @author Jorsi Grammens
 */
public class LocationDao extends ClassicDatabaseConnection implements ILocationDao {

    private static final String LOCATIONID_COLUMN = "locationID";
    private static final String PAGENUMBER_COLUMN = "pagenumber";
    private static final String X1_COLUMN = "x1";
    private static final String Y1_COLUMN = "y1";
    private static final String X2_COLUMN = "x2";
    private static final String Y2_COLUMN = "y2";
    
    CourseNotesReferenceDao courseNotesReferenceDao;
    
    /**
     * Constructs an instance of <code>LocationDao</code>.
     * @param propertiesFileName Propreties file that contains information about the database connection.
     * @throws ClassicDatabaseException
     */
    public LocationDao(String propertiesFileName) throws ClassicDatabaseException {
        super(propertiesFileName);
    }

    @Override
    public int addLocation(int refID, Location location) throws ClassicDatabaseException {
        String sql = "INSERT INTO \"Location\" (\"ccID\",\"pagenumber\",\"x1\",\"y1\",\"x2\",\"y2\") "
                + "VALUES (?,?,?,?,?,?) RETURNING *;";
        try (Connection con = getConnection(); PreparedStatement stmt = con.prepareStatement(sql)) {
            int i = 1;
            stmt.setInt(i++, refID);
            stmt.setInt(i++, location.getPagenumber());
            stmt.setDouble(i++, location.getX1());
            stmt.setDouble(i++, location.getY1());
            stmt.setDouble(i++, location.getX2());
            stmt.setDouble(i++, location.getY2());
            ResultSet rs = stmt.executeQuery();
            rs.next();
            int id = rs.getInt(LOCATIONID_COLUMN);
            location.setId(id);
            return id;
        } catch (SQLException ex) {
            String message = "A problem occured while trying to add a location to the Classic database";
            Logger.getLogger(LocationDao.class.getName()).log(Level.SEVERE, message, ex);
            throw new ClassicDatabaseException(message, ex);
        }
    }
    
    @Override
    public void deleteLocation(int locationID) throws ClassicDatabaseException, ClassicNotFoundException {
        String sql = "DELETE FROM \"Location\" "
                + "WHERE \"locationID\"=?;";
        try (Connection con = getConnection(); PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, locationID);
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new ClassicNotFoundException(ClassicNotFoundException.LOCATION_OBJECT,locationID);
            }
        } catch (SQLException ex) {
            String message = "A problem occured while trying to delete a location from the Classic database";
            Logger.getLogger(LocationDao.class.getName()).log(Level.SEVERE, message, ex);
            throw new ClassicDatabaseException(message, ex);
        }
    }

    @Override
    public Location getLocation(int locationID) throws ClassicDatabaseException, ClassicNotFoundException {
        String sql = "SELECT * FROM \"Location\" WHERE \"locationID\" = ?;";
        try (Connection con = getConnection(); PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, locationID);
            ResultSet rs = stmt.executeQuery();
            if (!rs.isBeforeFirst()) {
                throw new ClassicNotFoundException(ClassicNotFoundException.LOCATION_OBJECT,locationID);
            }
            rs.next();
            Location location = new Location(rs.getDouble(X1_COLUMN), rs.getDouble(X2_COLUMN), rs.getDouble(Y1_COLUMN), rs.getDouble(Y2_COLUMN), rs.getInt(PAGENUMBER_COLUMN));
            location.setId(rs.getInt(LOCATIONID_COLUMN));
            return location;
        } catch (SQLException ex) {
            String message = "A problem occured while trying to get the location from the Classic database";
            Logger.getLogger(LocationDao.class.getName()).log(Level.SEVERE, message, ex);
            throw new ClassicDatabaseException(message, ex);
        }
    }

    @Override
    public List<Location> getLocations() throws ClassicDatabaseException {
        String sql = "SELECT * FROM \"Location\";";
        try (Connection con = getConnection(); Statement stmt = con.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            List<Location> locations = new ArrayList<>();
            while (rs.next()) {
                Location location = new Location(rs.getDouble(X1_COLUMN), rs.getDouble(X2_COLUMN), rs.getDouble(Y1_COLUMN), rs.getDouble(Y2_COLUMN), rs.getInt(PAGENUMBER_COLUMN));
                location.setId(rs.getInt(LOCATIONID_COLUMN));
                locations.add(location);
            }
            return locations;
        } catch (SQLException ex) {
            String message = "A problem occured while trying to get location from the Classic database";
            Logger.getLogger(LocationDao.class.getName()).log(Level.SEVERE, message, ex);
            throw new ClassicDatabaseException(message, ex);
        }
    }

    @Override
    public List<Location> getLocations(int refID) throws ClassicDatabaseException {
        String sql = "SELECT * FROM \"Location\" WHERE \"ccID\"=?;";
        try (Connection con = getConnection(); PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, refID);
            ResultSet rs = stmt.executeQuery();
            List<Location> locations = new ArrayList<>();
            while (rs.next()) {
                Location location = new Location(rs.getDouble(X1_COLUMN), rs.getDouble(X2_COLUMN), rs.getDouble(Y1_COLUMN), rs.getDouble(Y2_COLUMN), rs.getInt(PAGENUMBER_COLUMN));
                location.setId(rs.getInt(LOCATIONID_COLUMN));
                locations.add(location);
            }
            return locations;
        } catch (SQLException ex) {
            String message = "A problem occured while trying to get location from the Classic database";
            Logger.getLogger(LocationDao.class.getName()).log(Level.SEVERE, message, ex);
            throw new ClassicDatabaseException(message, ex);
        }
    }

    protected boolean isLocation(int locationID) throws ClassicDatabaseException {
        String sql = "SELECT EXISTS(SELECT 1 FROM \"Location\" WHERE \"locationID\"=?)";
        try (Connection con = getConnection(); PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, locationID);
            ResultSet rs = stmt.executeQuery();
            rs.next();
            return rs.getBoolean("exists");
        } catch (SQLException ex) {
            String message = "A problem occured while trying to check the location in the Classic database";
            Logger.getLogger(LocationDao.class.getName()).log(Level.SEVERE, message, ex);
            throw new ClassicDatabaseException(message, ex);
        }
    }

    @Override
    public void updateLocation(Location location) throws ClassicDatabaseException, ClassicNotFoundException {
        String sql = "UPDATE \"Location\" "
                + "SET \"pagenumber\"=?,\"x1\"=?,\"y1\"=?,\"x2\"=?,\"y2\"=? "
                + "WHERE \"locationID\"=?;";
        try (Connection con = getConnection(); PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, location.getPagenumber());
            stmt.setDouble(2, location.getX1());
            stmt.setDouble(3, location.getY1());
            stmt.setDouble(4, location.getX2());
            stmt.setDouble(5, location.getY2());
            stmt.setInt(6, location.getId());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new ClassicNotFoundException(ClassicNotFoundException.LOCATION_OBJECT, location.getId());
            }
        } catch (SQLException ex) {
            String message = "A problem occured while trying to update the location from the Classic database";
            Logger.getLogger(LocationDao.class.getName()).log(Level.SEVERE, message, ex);
            throw new ClassicDatabaseException(message, ex);
        }
    }
    
    @Override
    public void updateLocations(int refID, List<Location> location) throws ClassicDatabaseException, ClassicNotFoundException {
        String deleteSql = "DELETE FROM \"Location\" "
                + "WHERE \"ccID\"=?;";
        try (Connection con = getConnection(); PreparedStatement stmt = con.prepareStatement(deleteSql)) {
            stmt.setInt(1, refID);
            stmt.executeUpdate();
            for (Location l : location) {
                addLocation(refID, l);
            }
        } catch (SQLException ex) {
            String message = "A problem occured while trying to delete a location from the Classic database";
            Logger.getLogger(LocationDao.class.getName()).log(Level.SEVERE, message, ex);
            throw new ClassicDatabaseException(message, ex);
        }
    }

    @Override
    public void cleanTable() throws ClassicDatabaseException {
        String sql = "DELETE FROM \"Location\"";
        try (Connection con = getConnection(); Statement stmt = con.createStatement()) {
            stmt.executeUpdate(sql);
        } catch (SQLException ex) {
            String message = "A problem occured while trying to clean the location table in the database";
            Logger.getLogger(LocationDao.class.getName()).log(Level.SEVERE, message, ex);
            throw new ClassicDatabaseException(message, ex);
        }
    }
}
