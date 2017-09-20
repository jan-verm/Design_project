/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package databaseaccess.interfaces;

import databaseaccess.exceptions.ClassicDatabaseException;
import databaseaccess.exceptions.ClassicNotFoundException;

/**
 * Data Access Object for LTI. 
 * @author jorsi
 */
public interface ILtiDao {
    
    /**
     * Add a key-secret pair to the database.
     * @param key Key to be added.
     * @param secret Secret to be added.
     * @throws ClassicDatabaseException
     */
    public void addKeySecretPair(String key, String secret) throws ClassicDatabaseException;
    
    /**
     * Delete a key-secret pair to the database.
     * @param key Key of the pair to be deleted
     * @throws ClassicDatabaseException
     * @throws ClassicNotFoundException
     */
    public void deleteKeySecretPair(String key) throws ClassicDatabaseException,ClassicNotFoundException;
    
    /**
     * Get a secret from the database.
     * @param key Key of the key-secret pair.
     * @return
     * @throws ClassicDatabaseException
     * @throws ClassicNotFoundException
     */
    public String getSecret(String key) throws ClassicDatabaseException,ClassicNotFoundException;
    
    /**
     * Delete all entries from the database
     * @throws ClassicDatabaseException
     */
    public void cleanTable() throws ClassicDatabaseException;
}
