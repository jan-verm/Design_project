/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package databaseaccess.interfaces;

import databaseaccess.exceptions.ClassicDatabaseException;
import databaseaccess.exceptions.ClassicNotFoundException;
import java.util.List;
import models.Location;

/**
 *
 * @author jorsi
 */
public interface ILocationDao {
    
    /**
     * Add a location to the database.
     * @param refID Id of the coursenotesreference to which the location belongs.
     * @param location Location object to add.
     * @return Id of the location object.
     * @throws ClassicDatabaseException
     * @throws ClassicNotFoundException
     */
    public int addLocation(int refID, Location location) throws ClassicDatabaseException,ClassicNotFoundException;
    
    /**
     * Delete a location from the database.
     * @param locationID ID of the location.
     * @throws ClassicDatabaseException
     * @throws ClassicNotFoundException
     */
    public void deleteLocation(int locationID) throws ClassicDatabaseException, ClassicNotFoundException;
    
    /**
     * Get a location from the database.
     * @param locationID Id of the location.
     * @return A location object.
     * @throws ClassicDatabaseException
     * @throws ClassicNotFoundException
     */
    public Location getLocation(int locationID) throws ClassicDatabaseException, ClassicNotFoundException;
    
    /**
     * Get all the location of a coursenotesreference.
     * @param refID id of the coursenotesreference.
     * @return A list of location objects of the coursenotesreference
     * @throws ClassicDatabaseException
     * @throws ClassicNotFoundException
     */
    public List<Location> getLocations(int refID) throws ClassicDatabaseException, ClassicNotFoundException;
    
    /**
     * Get all the locations stored in the database
     * @return 
     * @throws ClassicDatabaseException
     */
    public List<Location> getLocations() throws ClassicDatabaseException;
    
    /**
     * Update a location in the database
     * @param location Update location object
     * @throws ClassicDatabaseException
     * @throws ClassicNotFoundException
     */
    public void updateLocation(Location location) throws ClassicDatabaseException, ClassicNotFoundException;
    
    /**
     * update multiple locations in the database.
     * @param refID Id of the of the coursenotesreference the locations belongs to.
     * @param location List of updated location objects.
     * @throws ClassicDatabaseException
     * @throws ClassicNotFoundException
     */
    public void updateLocations(int refID, List<Location> location) throws ClassicDatabaseException,ClassicNotFoundException;
    
    /**
     * Delete all entries from the locations table in the database
     * @throws ClassicDatabaseException
     */
    public void cleanTable() throws ClassicDatabaseException;
}
