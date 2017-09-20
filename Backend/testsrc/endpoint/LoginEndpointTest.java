/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package endpoint;

import config.TestConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;
import org.springframework.web.context.WebApplicationContext;

/**
 *
 * @author Juta
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = { TestConfig.class })
@IntegrationTest
@WebAppConfiguration
@TestPropertySource(locations="classpath:test.properties")
public class LoginEndpointTest {
    
    private MockMvc mock;
	
    @Autowired
    private WebApplicationContext webApplicationContext;

    @Before
    public void setup() throws Exception {
        mock = webAppContextSetup(webApplicationContext).build();
    }

    /**
     * Test of postCredentials method, of class LoginEndpoint.
     */
    @Test
    public void testPostCredentials() throws Exception {
        String requestBody = "{\"username\": \"admin\", \"password\": \"admin\"}";
    	
        mock.perform(post("/login")
    		.content(requestBody))
    		.andExpect(status().isOk());
    }
    
    /**
     * Test bad credentials.
     */
    @Test
    public void testPostBadCredentials() throws Exception {
        String requestBody = "{\"username\": \"admin\", \"password\": \"very_secret_lti_user_password\"}";
    	
        mock.perform(post("/login")
    		.content(requestBody))
    		.andExpect(status().is4xxClientError());
        
        requestBody = "{\"username\": \"admin\", \"password\": \"invallid\"}";
    	
        mock.perform(post("/login")
    		.content(requestBody))
    		.andExpect(status().is4xxClientError());
    }

    /**
     * Test of checkCredentials method, of class LoginEndpoint.
     */
    @Test
    public void testCheckCredentials() throws Exception {
        String requestBody = "{\"username\": \"admin\", \"password\": \"admin\"}";
    	
        mock.perform(post("/login")
    		.content(requestBody))
    		.andExpect(status().isOk());
        
        mock.perform(get("/login"))
	        .andExpect(status().isOk())
	        .andExpect(jsonPath("$.username").value("admin"))
	        .andExpect(jsonPath("$.role").value("ADMIN"))
	        .andExpect(jsonPath("$.password").doesNotExist());
    }

    /**
     * Test of logout method, of class LoginEndpoint.
     */
    @Test
    public void testLogout() throws Exception {
        String requestBody = "{\"username\": \"admin\", \"password\": \"admin\"}";
    	
        mock.perform(post("/login")
    		.content(requestBody))
    		.andExpect(status().isOk());
        
        mock.perform(post("/logout"))
        		.andExpect(status().isOk());
    }
    
}
