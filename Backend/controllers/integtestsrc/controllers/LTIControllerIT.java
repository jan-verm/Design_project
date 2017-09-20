package controllers;

import databaseaccess.exceptions.ClassicDatabaseException;
import databaseaccess.exceptions.ClassicNotFoundException;
import databaseaccess.implementation.LtiDao;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class LTIControllerIT {
	
    private LTIController ltiController;
    private final String TEST_DATABASE_CONFIG = "testdbconfig.properties";
    private LtiDao dao;

    @Before
    public void setUp() throws ClassicDatabaseException {
        dao = new LtiDao(TEST_DATABASE_CONFIG);
        ltiController = new LTIController(dao);
    }
    
    @After
    public void tearDown() throws ClassicDatabaseException {
        dao.cleanTable();
    }


    /**
     * Test if exception is thrown when key is already in database.
     * @throws java.lang.Exception
     */
    @Test(expected=ClassicDatabaseException.class)
    public void testGenerateDup() throws Exception {
        ltiController.generateKeySecretPair("testclassic");
        ltiController.generateKeySecretPair("testclassic");
    }
    
    /**
     * Test generation of new key-secret pair.
     * @throws java.lang.Exception
     */
    @Test
    public void testGenerate() throws Exception {
        String secret = ltiController.generateKeySecretPair("testclassic2");
        assertNotNull(secret);
    }
    
    /**
     * Test if exception is thrown when key is deleted but key is not in the database.
     * @throws java.lang.Exception
     */
    @Test(expected=ClassicNotFoundException.class)
    public void testDeleteNotFound() throws Exception {
        ltiController.deleteKeySecretPair("testclassic");
    }
    
    /**
     * Test if exception is thrown when key is deleted and database error.
     * @throws java.lang.Exception
     */
    @Test(expected=ClassicNotFoundException.class)
    public void testDeleteError() throws Exception {
        ltiController.deleteKeySecretPair("testclassic2");
    }
    
    /**
     * Test if delete is successfull.
     * @throws java.lang.Exception
     */
    @Test
    public void testDelete() throws Exception {
        String secret = ltiController.generateKeySecretPair("succes");
        ltiController.deleteKeySecretPair("succes");
    }
    
    /**
     * Test if exception is thrown when key is searched but key is not in the database.
     * @throws java.lang.Exception
     */
    @Test(expected=ClassicNotFoundException.class)
    public void testGetNotFound() throws Exception {
        ltiController.getSecret("testclassic");
    }
    
    /**
     * Test if exception is thrown when key is searched and database error.
     * @throws java.lang.Exception
     */
    @Test(expected=ClassicNotFoundException.class)
    public void testGetError() throws Exception {
        ltiController.getSecret("testclassic2");
    }
    
    /**
     * Test get secret.
     * @throws java.lang.Exception
     */
    @Test
    public void testGet() throws Exception {
        String secret = ltiController.generateKeySecretPair("testclassic2");
        String result = ltiController.getSecret("testclassic2");
        assertEquals(secret, result);
    }
    
    /**
     * Test time difference calculation.
     * @throws java.lang.Exception
     */
    @Test
    public void testDifference() throws Exception {
        Long diff = ltiController.getTimeDifference(System.currentTimeMillis()/1000);
        assertTrue(diff<=10);
    }
    
    /**
     * Test of generateOAuthSignature method, of class LTIEndpoint.
     */
    @Test
    public void generateOAuthSignature() throws Exception {
        Map<String,List<String>> form = new HashMap<>();
        List<String> oauth_version = new LinkedList<>(Arrays.asList("1.0"));
        List<String> lti_message_type = new LinkedList<>(Arrays.asList("basic-lti-launch-request"));
        List<String> lti_version = new LinkedList<>(Arrays.asList("LTI-1p0"));
        List<String> oauth_consumer_key = new LinkedList<>(Arrays.asList("testclassic"));
        List<String> resource_link_id = new LinkedList<>(Arrays.asList("1"));
        List<String> oauth_callback = new LinkedList<>(Arrays.asList("about:blank"));
        List<String> oauth_timestamp = new LinkedList<>(Arrays.asList("1462546654"));
        List<String> oauth_nonce = new LinkedList<>(Arrays.asList("d24c99c8275b4a360084037e43cc29e9"));
        List<String> oauth_signature = new LinkedList<>(Arrays.asList("lDEd/Ae49WfoBXYGtuHBFl5g9GA="));
        List<String> ext_user_username = new LinkedList<>(Arrays.asList("admin"));
        List<String> roles = new LinkedList<>(Arrays.asList("Instructor,urn:lti:sysrole:ims/lis/Administrator,urn:lti:instrole:ims/lis/Administrator"));
        form.put("oauth_version", oauth_version);
        form.put("lti_message_type", lti_message_type);
        form.put("lti_version", lti_version);
        form.put("oauth_consumer_key", oauth_consumer_key);
        form.put("resource_link_id", resource_link_id);
        form.put("oauth_callback", oauth_callback);
        form.put("oauth_timestamp", oauth_timestamp);
        form.put("oauth_nonce", oauth_nonce);
        form.put("oauth_signature", oauth_signature);
        form.put("ext_user_username", ext_user_username);
        form.put("roles", roles);
        String secret = "testclassic";
        String HTTPMethod = "POST";
        String url = "http://student-dp8.intec.ugent.be/development/api/lti";
        String signature = ltiController.generateOAuthSignature(secret, (Map)form, HTTPMethod, url);
        assertEquals("lDEd/Ae49WfoBXYGtuHBFl5g9GA=", signature);
        
    }
    
}

