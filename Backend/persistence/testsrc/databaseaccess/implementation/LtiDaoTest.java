/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package databaseaccess.implementation;

import databaseaccess.exceptions.ClassicDatabaseException;
import databaseaccess.exceptions.ClassicNotFoundException;
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
public class LtiDaoTest {
    
    LtiDao ltiDao;
    LtiDao fakedao;
    
    public LtiDaoTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() throws ClassicDatabaseException {
        ltiDao = new LtiDao(LtiDao.TEST_DATABASE_CONFIG);
        fakedao = new LtiDao("fakedbconfig.properties");
    }
    
    @After
    public void tearDown() throws ClassicDatabaseException {
        ltiDao.cleanTable();
    }

    @Test
    public void testAddKeySecretPair() throws Exception {
        String key = "testKey";
        String expResult = "testSecret";
        ltiDao.addKeySecretPair(key, expResult);
        String result = ltiDao.getSecret(key);
        assertEquals(expResult, result);
        ltiDao.deleteKeySecretPair(key);
    }
    
    @Test(expected = ClassicDatabaseException.class)
    public void testAddKeySecretPairDBException() throws Exception {
        String key = "testKey";
        String expResult = "testSecret";
        fakedao.addKeySecretPair(key, expResult);        
    }   
    

    @Test(expected = ClassicNotFoundException.class)
    public void testDeleteKeySecretPair() throws Exception {
        String key = "testKey";
        String expResult = "testSecret";
        ltiDao.addKeySecretPair(key, expResult);
        String result = ltiDao.getSecret(key);
        assertEquals(expResult, result);
        ltiDao.deleteKeySecretPair(key);
        ltiDao.getSecret(key);
    }
    
    @Test(expected = ClassicNotFoundException.class)
    public void testDeleteSecretNFException() throws Exception {
        String key = "testNFKey";        
        ltiDao.deleteKeySecretPair(key);        
    }
    
    @Test(expected = ClassicDatabaseException.class)
    public void testDeleteSecretDBException() throws Exception {              
        fakedao.deleteKeySecretPair("");        
    }


    @Test
    public void testGetSecret() throws Exception {
        String key = "testKey";
        String expResult = "testSecret";
        ltiDao.addKeySecretPair(key, expResult);
        String result = ltiDao.getSecret(key);
        assertEquals(expResult, result);
        ltiDao.deleteKeySecretPair(key);
    }
    
    @Test(expected = ClassicDatabaseException.class)
    public void testGetSecretDBException() throws Exception {
        String key = "testKey";
        fakedao.getSecret(key);        
    }
    
    @Test(expected = ClassicNotFoundException.class)
    public void testGetSecretNFException() throws Exception {
        String key = "testNFKey";        
        ltiDao.getSecret(key);        
    }
    
    @Test(expected = ClassicNotFoundException.class)
    public void testCleanTable() throws Exception {
        String key = "testKey";
        String expResult = "testSecret";
        ltiDao.addKeySecretPair(key, expResult);
        String result = ltiDao.getSecret(key);
        assertEquals(expResult, result);
        ltiDao.cleanTable();
        ltiDao.getSecret(key);
    }
    
    @Test(expected = ClassicDatabaseException.class)
    public void testCleanTableDBException() throws Exception {        
        fakedao.cleanTable();        
    }    
}
