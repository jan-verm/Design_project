/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package databaseaccess.implementation;

import databaseaccess.exceptions.ClassicDatabaseException;
import java.sql.Connection;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author jorsi
 */
public class ClassicDatabaseConnectionTest {
    
    public ClassicDatabaseConnectionTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }
    
    @Test(expected = ClassicDatabaseException.class)
    public void testConstructorClassNFException() throws Exception {
        ClassicDatabaseConnection instance = new VideoDao("fakedriverconfig.properties");
    }
    
    
    /**
     * Test of getConnection method, of class ClassicDatabaseConnection.
     */
    @Test
    public void testGetConnection() throws Exception {
        ClassicDatabaseConnection instance = new VideoDao(ClassicDatabaseConnection.TEST_DATABASE_CONFIG);
        Connection connection = instance.getConnection();
        assertTrue(connection.isValid(10));
        connection.close();
    }
    
}
