package endpoint;

import config.TestConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = { TestConfig.class })
@IntegrationTest
@WebAppConfiguration
@TestPropertySource(locations="classpath:test.properties")
public class LTIEndpointTest {
    
    private MockMvc mock;
	
    @Autowired
    private WebApplicationContext webApplicationContext;

    @Before
    public void setup() throws Exception {
        mock = webAppContextSetup(webApplicationContext).build();
    }

    /**
     * Test of lti method, of class LTIEndpoint.
     */
    public void testLtiSuccess() throws Exception {
        // Test for success.
        mock.perform(post("/lti")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("oauth_version", "1.0")
                .param("lti_message_type", "basic-lti-launch-request")
    		.param("lti_version", "LTI-1p0")
                .param("oauth_consumer_key", "testclassic")
                .param("resource_link_id", "1")
                .param("oauth_callback", "about:blank")
                .param("oauth_timestamp", "1462546654")
                .param("oauth_nonce", "d24c99c8275b4a360084037e43cc29e9")
                .param("oauth_signature", "lDEd/Ae49WfoBXYGtuHBFl5g9GA=")
                .param("ext_user_username", "admin")
                .param("roles", "Instructor,urn:lti:sysrole:ims/lis/Administrator,urn:lti:instrole:ims/lis/Administrator"))
                .andExpect(status().is3xxRedirection());
    }
    
    @Test
    public void testLtiUnsucReqValidation() throws Exception {
        // Test for unsuccessfull request validation.
        mock.perform(post("/lti")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().is4xxClientError());
        
        mock.perform(post("/lti")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("oauth_version", "2.0") // different
                .param("lti_message_type", "wrong") // different
    		.param("lti_version", "wrong") // different
                .param("oauth_consumer_key", "") // different
                .param("resource_link_id", "") // different
                .param("oauth_callback", "wrong") // different
                .param("oauth_timestamp", "") // different
                .param("oauth_nonce", "") // different
                .param("oauth_signature", "lDEd/Ae49WfoBXYGtuHBFl5g9GA=")
                .param("ext_user_username", "admin")
                .param("roles", "Instructor,urn:lti:sysrole:ims/lis/Administrator,urn:lti:instrole:ims/lis/Administrator"))
                .andExpect(status().is4xxClientError());
    }
    
    @Test
    public void testLtiTooOld() throws Exception {
        // Test for too old timestamp.
        mock.perform(post("/lti")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("oauth_version", "1.0")
                .param("lti_message_type", "basic-lti-launch-request")
    		.param("lti_version", "LTI-1p0")
                .param("oauth_consumer_key", "testclassic")
                .param("resource_link_id", "1")
                .param("oauth_callback", "about:blank")
                .param("oauth_timestamp", "1562556654") // different
                .param("oauth_nonce", "d24c99c8275b4a360084037e43cc29e9")
                .param("oauth_signature", "lDEd/Ae49WfoBXYGtuHBFl5g9GA=")
                .param("ext_user_username", "admin")
                .param("roles", "Instructor,urn:lti:sysrole:ims/lis/Administrator,urn:lti:instrole:ims/lis/Administrator"))
                .andExpect(status().is4xxClientError());
    }
    
    @Test
    public void testLtiDuplicateNonce() throws Exception {
        // Try success test.
        testLtiSuccess();
        // Test for duplicate nonce.
        mock.perform(post("/lti")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("oauth_version", "1.0")
                .param("lti_message_type", "basic-lti-launch-request")
    		.param("lti_version", "LTI-1p0")
                .param("oauth_consumer_key", "testclassic")
                .param("resource_link_id", "1")
                .param("oauth_callback", "about:blank")
                .param("oauth_timestamp", "1462546654")
                .param("oauth_nonce", "d24c99c8275b4a360084037e43cc29e9")
                .param("oauth_signature", "lDEd/Ae49WfoBXYGtuHBFl5g9GA=")
                .param("ext_user_username", "admin")
                .param("roles", "Instructor,urn:lti:sysrole:ims/lis/Administrator,urn:lti:instrole:ims/lis/Administrator"))
                .andExpect(status().is4xxClientError());
    }
        
    @Test
    public void testLtiKeyNotFound() throws Exception {
        // Test for key not found.
        mock.perform(post("/lti")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("oauth_version", "1.0")
                .param("lti_message_type", "basic-lti-launch-request")
    		.param("lti_version", "LTI-1p0")
                .param("oauth_consumer_key", "notfound") // different
                .param("resource_link_id", "1")
                .param("oauth_callback", "about:blank")
                .param("oauth_timestamp", "1462546654")
                .param("oauth_nonce", "d24c99c8375b4a360084037e43cc29e9") // different
                .param("oauth_signature", "lDEd/Ae49WfoBXYGtuHBFl5g9GA=")
                .param("ext_user_username", "admin")
                .param("roles", "Instructor,urn:lti:sysrole:ims/lis/Administrator,urn:lti:instrole:ims/lis/Administrator"))
                .andExpect(status().is4xxClientError());
    }
    
    @Test
    public void testLtiSecretUnsuccessfull() throws Exception {
        // Testcase when generation of secret is unsuccessfull.
        mock.perform(post("/lti")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("oauth_version", "1.0")
                .param("lti_message_type", "basic-lti-launch-request")
    		.param("lti_version", "LTI-1p0")
                .param("oauth_consumer_key", "usuccessfull")
                .param("resource_link_id", "1")
                .param("oauth_callback", "about:blank")
                .param("oauth_timestamp", "1462546654")
                .param("oauth_nonce", "d24c99c8375b4a360004037e43cc29e9") // different
                .param("oauth_signature", "lDEd/Ae49WfoBXYGtuABFl5g9GA=") // different
                .param("ext_user_username", "admin")
                .param("roles", "Instructor,urn:lti:sysrole:ims/lis/Administrator,urn:lti:instrole:ims/lis/Administrator"))
                .andExpect(status().is4xxClientError());
    }
        
    @Test
    public void testLtiSecretMismatch() throws Exception {
        // Testcase when secrets do not match.
        mock.perform(post("/lti")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("oauth_version", "1.0")
                .param("lti_message_type", "basic-lti-launch-request")
    		.param("lti_version", "LTI-1p0")
                .param("oauth_consumer_key", "testclassic")
                .param("resource_link_id", "1")
                .param("oauth_callback", "about:blank")
                .param("oauth_timestamp", "1462546654")
                .param("oauth_nonce", "d24c99c8375b4a360004037e43cc29e9") // different
                .param("oauth_signature", "lDEd/Ae49WfoBXYGtuABFl5g9GA=") // different
                .param("ext_user_username", "admin")
                .param("roles", "Instructor,urn:lti:sysrole:ims/lis/Administrator,urn:lti:instrole:ims/lis/Administrator"))
                .andExpect(status().is4xxClientError());
    }
        
    @Test
    public void testLtiUserNotPresentYet() throws Exception {
        // Test when lti user is not yet present in the system role TEACHER.
        mock.perform(post("/lti")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("oauth_version", "1.0")
                .param("lti_message_type", "basic-lti-launch-request")
    		.param("lti_version", "LTI-1p0")
                .param("oauth_consumer_key", "testclassic")
                .param("resource_link_id", "1")
                .param("oauth_callback", "about:blank")
                .param("oauth_timestamp", "1462546654")
                .param("oauth_nonce", "124c99c8375b4a360004037e43cc29e9") // different
                .param("oauth_signature", "lDEd/Ae49WfoBXYGtuHBFl5g9GA=") // different
                .param("ext_user_username", "Driek") // different
                .param("roles", "Instructor,urn:lti:sysrole:ims/lis/Administrator,urn:lti:instrole:ims/lis/Administrator"))
                .andExpect(status().is3xxRedirection());
        
        // Role STUDENT.
        mock.perform(post("/lti")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("oauth_version", "1.0")
                .param("lti_message_type", "basic-lti-launch-request")
    		.param("lti_version", "LTI-1p0")
                .param("oauth_consumer_key", "testclassic")
                .param("resource_link_id", "1")
                .param("oauth_callback", "about:blank")
                .param("oauth_timestamp", "1462546654")
                .param("oauth_nonce", "124c99c8375b4a360004037e43cc2910") // different
                .param("oauth_signature", "lDEd/Ae49WfoBXYGtuHBFl5g9GA=") // different
                .param("ext_user_username", "student") // different
                .param("roles", "Learner"))
                .andExpect(status().is3xxRedirection());
        
        // Role INSTRUCTOR.
        mock.perform(post("/lti")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("oauth_version", "1.0")
                .param("lti_message_type", "basic-lti-launch-request")
    		.param("lti_version", "LTI-1p0")
                .param("oauth_consumer_key", "testclassic")
                .param("resource_link_id", "1")
                .param("oauth_callback", "about:blank")
                .param("oauth_timestamp", "1462546654")
                .param("oauth_nonce", "124c99c8375b4a360004037e43dd2910") // different
                .param("oauth_signature", "lDEd/Ae49WfoBXYGtuHBFl5g9GA=") // different
                .param("ext_user_username", "instructor") // different
                .param("roles", "urn:lti:instrole:ims/lis/Instructor"))
                .andExpect(status().is3xxRedirection());
        
        // Role MENTOR.
        mock.perform(post("/lti")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("oauth_version", "1.0")
                .param("lti_message_type", "basic-lti-launch-request")
    		.param("lti_version", "LTI-1p0")
                .param("oauth_consumer_key", "testclassic")
                .param("resource_link_id", "1")
                .param("oauth_callback", "about:blank")
                .param("oauth_timestamp", "1462546654")
                .param("oauth_nonce", "124c99c8375b4a360004037e43ee2910") // different
                .param("oauth_signature", "lDEd/Ae49WfoBXYGtuHBFl5g9GA=") // different
                .param("ext_user_username", "mentor") // different
                .param("roles", "urn:lti:instrole:ims/lis/Mentor"))
                .andExpect(status().is3xxRedirection());
        
        // Role TEACHINGASSISTANT.
        mock.perform(post("/lti")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("oauth_version", "1.0")
                .param("lti_message_type", "basic-lti-launch-request")
    		.param("lti_version", "LTI-1p0")
                .param("oauth_consumer_key", "testclassic")
                .param("resource_link_id", "1")
                .param("oauth_callback", "about:blank")
                .param("oauth_timestamp", "1462546654")
                .param("oauth_nonce", "124c99c8375b4a360004037e43ff2910") // different
                .param("oauth_signature", "lDEd/Ae49WfoBXYGtuHBFl5g9GA=") // different
                .param("ext_user_username", "assistant") // different
                .param("roles", "urn:lti:role:ims/lis/TeachingAssistant"))
                .andExpect(status().is3xxRedirection());
    }
    
    /**
     * Test of newLtiKeyPair method, of class LTIEndpoint.
     */
    @Test
    public void testNewLtiKeyPair() throws Exception {
        // login
        String loginBody = "{\"username\": \"admin\", \"password\": \"admin\"}";
        mock.perform(post("/login")
                .content(loginBody))
                .andExpect(status().isOk());
        
        String requestBody = "{\"key\": \"testclassic\"}";
    	
        mock.perform(post("/lti/keypair")
    		.content(requestBody))
    		.andExpect(status().isOk())
                .andExpect(jsonPath("$.secret").value("testclassic"))
                .andExpect(jsonPath("$.key").value("testclassic"));
    }
    
    /**
     * Test of deleteLtiKeyPair method, of class LTIEndpoint.
     */
    @Test
    public void testDeleteLtiKeyPair() throws Exception {
        // login
        String loginBody = "{\"username\": \"admin\", \"password\": \"admin\"}";
        mock.perform(post("/login")
                .content(loginBody))
                .andExpect(status().isOk());
        
        String requestBody = "{\"key\": \"testclassic\"}";
    	mock.perform(delete("/lti/keypair")
                .content(requestBody))
    		.andExpect(status().isOk());
    }
}
