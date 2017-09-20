/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package databaseaccess.implementation;

import databaseaccess.exceptions.ClassicDatabaseException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This abstract class is used to load in the database driver and connection properties
 * @author jorsi
 */
public abstract class ClassicDatabaseConnection {
    public static final String DATABASE_CONFIG = "dbconfig.properties";
    public static final String TEST_DATABASE_CONFIG = "testdbconfig.properties";
    
    private String driver;
    private String url;
    private String database;
    private String dbuser;
    private String dbpassword;

    /**
     * Constructs an instance of <code>ClassicDatabaseConnection</code>.
     * @param propertiesFileName Propreties file that contains information about the database connection.
     * @throws ClassicDatabaseException
     */
    public ClassicDatabaseConnection(String propertiesFileName) throws ClassicDatabaseException{
        try {
            loadProperties(propertiesFileName);
            Class.forName(driver);
        } catch (IOException ex) {
            Logger.getLogger(ClassicDatabaseConnection.class.getName()).log(Level.SEVERE, null, ex);
            throw new ClassicDatabaseException("A problem occured while trying to read the properties file (" + propertiesFileName + ")",ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ClassicDatabaseConnection.class.getName()).log(Level.SEVERE, null, ex);
            throw new ClassicDatabaseException("A problem occured while trying to load the Postgresql JDBC driver",ex);
        }
    }
    
    /**
     * Gets the database connection object.
     * @return Database connection object.
     * @throws SQLException
     */
    protected Connection getConnection() throws SQLException {        
        return  DriverManager.getConnection(url + "/" + database, dbuser, dbpassword);
    }        
    
    private void loadProperties(String propertiesFileName) throws IOException {
        Properties prop = new Properties();
        InputStream input = getClass().getClassLoader().getResourceAsStream(propertiesFileName);        

        // load a properties file
        prop.load(input);

        // get the database properties        
        driver = prop.getProperty("driver");
        url = prop.getProperty("url");
        database = prop.getProperty("database");
        dbuser = prop.getProperty("dbuser");
        dbpassword = prop.getProperty("dbpassword");
    }
}
