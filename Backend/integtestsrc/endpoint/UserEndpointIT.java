/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package endpoint;

import config.ITConfig;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 *
 * @author Juta
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = { ITConfig.class })
@IntegrationTest
@WebAppConfiguration
@TestPropertySource(locations="classpath:test.properties")
public class UserEndpointIT {
	
    private MockMvc mock;
	
    @Autowired
    private WebApplicationContext webApplicationContext;

    @Before
    public void setup() throws Exception {
        mock = webAppContextSetup(webApplicationContext).build();
    }

    /**
     * Test of getUsers method, of class UserEndpoint.
     */
    @Test
    public void testGetUsers() throws Exception {
        // add user
        String requestBody = "{\"username\": \"admin\", \"password\": \"admin\", \"role\": \"admin\"}";
    	
    	String response = 
                mock.perform(post("/user")
    		.content(requestBody))
    		.andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        
        JSONObject json = new JSONObject(response);
        int userId = json.getInt("userId");
        
        //add second user
        requestBody = "{\"username\": \"student\", \"password\": \"student\", \"role\": \"student\"}";
    	
    	response = 
                mock.perform(post("/user")
    		.content(requestBody))
    		.andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        
        json = new JSONObject(response);
        int userId2 = json.getInt("userId");
        
        // get users
        mock.perform(get("/user"))
	        .andExpect(status().isOk())
	        .andExpect(jsonPath("$[0].userId").value(userId))
	        .andExpect(jsonPath("$[0].username").value("admin"))
                .andExpect(jsonPath("$[0].role").value("ADMIN"))
                .andExpect(jsonPath("$[0].password").doesNotExist())
                .andExpect(jsonPath("$[1].userId").value(userId2))
	        .andExpect(jsonPath("$[1].username").value("student"))
                .andExpect(jsonPath("$[1].password").doesNotExist())
                .andExpect(jsonPath("$[1].role").value("STUDENT"));
        
        requestBody = "{\"username\": \"admin\", \"password\": \"admin\"}";
        
        mock.perform(post("/login")
    		.content(requestBody))
    		.andExpect(status().isOk());
        
        //delete
        mock.perform(delete("/user/" + userId2))
    		.andExpect(status().isOk());

        mock.perform(delete("/user/" + userId))
    		.andExpect(status().isOk());

        // logout
    	mock.perform(post("/logout"))
		.andExpect(status().isOk());
    }

    /**
     * Test of getUser method, of class UserEndpoint.
     */
    @Test
    public void testPostUser() throws Exception {
        // add user
        String requestBody = "{\"username\": \"admin\", \"password\": \"admin\", \"role\": \"admin\"}";
    	
    	String response = 
                mock.perform(post("/user")
    		.content(requestBody))
    		.andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        
        JSONObject json = new JSONObject(response);
        int userId = json.getInt("userId");
        
        assertNotEquals(0, userId);
        
        requestBody = "{\"username\": \"admin\", \"password\": \"admin\"}";
        
        mock.perform(post("/login")
    		.content(requestBody))
    		.andExpect(status().isOk());
        
        //delete
        mock.perform(delete("/user/" + userId))
    		.andExpect(status().isOk());
        
        // logout
    	mock.perform(post("/logout"))
		.andExpect(status().isOk());
    }

    /**
     * Test of getUser method, of class UserEndpoint.
     */
    @Test
    public void testGetUser() throws Exception {
        // add user
        String requestBody = "{\"username\": \"admin\", \"password\": \"admin\", \"role\": \"admin\"}";
    	
    	String response = 
            mock.perform(post("/user")
            .content(requestBody))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();
        
        JSONObject json = new JSONObject(response);
        int userId = json.getInt("userId");
        
        // get user
        mock.perform(get("/user/" + userId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.userId").value(userId))
            .andExpect(jsonPath("$.username").value("admin"))
            .andExpect(jsonPath("$.role").value("ADMIN"))
            .andExpect(jsonPath("$.password").doesNotExist());
        
        requestBody = "{\"username\": \"admin\", \"password\": \"admin\"}";
        
        mock.perform(post("/login")
    		.content(requestBody))
    		.andExpect(status().isOk());
        
        //delete
        mock.perform(delete("/user/" + userId))
    		.andExpect(status().isOk());
        
        // logout
    	mock.perform(post("/logout"))
		.andExpect(status().isOk());
    }

}
