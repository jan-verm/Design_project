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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
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
public class UserEndpointTest {
    
    private MockMvc mock;
	
    @Autowired
    private WebApplicationContext webApplicationContext;

    @Before
    public void setup() throws Exception {
        mock = webAppContextSetup(webApplicationContext).build();
        
        // login
        String requestBody = "{\"username\": \"admin\", \"password\": \"admin\"}";
    	
        mock.perform(post("/login")
    		.content(requestBody))
    		.andExpect(status().isOk());
    }

    /**
     * Test of getUsers method, of class UserEndpoint.
     */
    @Test
    public void testGetUsers() throws Exception {
        mock.perform(get("/user"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].userId").value(1))
            .andExpect(jsonPath("$[0].username").value("admin"))
            .andExpect(jsonPath("$[0].role").value("ADMIN"))
            .andExpect(jsonPath("$[0].password").doesNotExist());
    }

    /**
     * Test of postUser method, of class UserEndpoint.
     */
    @Test
    public void testPostUser() throws Exception {
        String requestBody = "{\"username\": \"admin\", \"password\": \"admin\", \"role\": \"admin\"}";
    	
        mock.perform(post("/user")
            .content(requestBody))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.userId").value(1));
    }
    
    /**
     * Test of postUser method, of class UserEndpoint.
     */
    @Test
    public void testPostUserCharacterNotAllowedException() throws Exception {
        String requestBody = "{\"username\": \"admin#\", \"password\": \"admin\", \"role\": \"admin\"}";
    	
        mock.perform(post("/user")
            .content(requestBody))
            .andExpect(status().is4xxClientError());
    }
    
    @Test
    public void testPostLtiUser() throws Exception {
        String requestBody = "{\"username\": \"admin\", \"password\": \"very_secret_lti_user_password\", \"role\": \"admin\"}";
    	
        mock.perform(post("/user")
            .content(requestBody))
            .andExpect(status().isUnauthorized());
    }

    /**
     * Test of getUser method, of class UserEndpoint.
     */
    @Test
    public void testGetUser() throws Exception {
        mock.perform(get("/user/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.userId").value(1))
            .andExpect(jsonPath("$.username").value("admin"))
            .andExpect(jsonPath("$.role").value("ADMIN"))
            .andExpect(jsonPath("$.password").doesNotExist());
    }
    
    /**
     * Test of getUserByName method, of class UserEndpoint.
     */
    @Test
    public void testGetUserByName() throws Exception {
        mock.perform(get("/userByName/admin%23testclassic"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.userId").value(1))
            .andExpect(jsonPath("$.username").value("admin#testclassic"))
            .andExpect(jsonPath("$.role").value("TEACHER"))
            .andExpect(jsonPath("$.password").doesNotExist());
    }

    /**
     * Test of patchUser method, of class UserEndpoint.
     */
    @Test
    public void testPatchUser() throws Exception {
        String requestBody = "{\"username\": \"admin\", \"password\": \"admin2\", \"role\": \"admin\"}";
    	
        mock.perform(patch("/user/1")
            .content(requestBody))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.userId").value(1));
    }

    /**
     * Test of deleteUser method, of class UserEndpoint.
     */
    @Test
    public void testDeleteUser() throws Exception {
        mock.perform(delete("/user/1"))
    		.andExpect(status().isOk());
    }
    
}
