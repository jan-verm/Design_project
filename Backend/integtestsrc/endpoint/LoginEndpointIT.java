/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package endpoint;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import java.io.UnsupportedEncodingException;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import config.ITConfig;

/**
 *
 * @author Juta
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = { ITConfig.class })
@IntegrationTest
@WebAppConfiguration
@TestPropertySource(locations="classpath:test.properties")
public class LoginEndpointIT {
	
	private MockMvc mock;
	
	@Autowired
    private WebApplicationContext webApplicationContext;
	
	private int userId;

    @Before
    public void setup() throws Exception {
    	mock = webAppContextSetup(webApplicationContext).build();
    	
    	// create user
    	String requestBody = "{\"username\": \"admin\", \"password\": \"admin\", \"role\": \"admin\"}";
    	
    	String response = 
                mock.perform(post("/user")
    		.content(requestBody))
    		.andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        
        JSONObject json = new JSONObject(response);
        userId = json.getInt("userId");
    }
    
    @After
    public void breakDown() throws Exception {
        String requestBody = "{\"username\": \"admin\", \"password\": \"admin\"}";
        
        mock.perform(post("/login")
    		.content(requestBody))
    		.andExpect(status().isOk());
        
    	// delete user
    	mock.perform(delete("/user/" + userId))
			.andExpect(status().isOk());
        
        mock.perform(post("/logout"))
		.andExpect(status().isOk());
    }

    /**
     * Test of checkCredentials method, of class LoginEndpoint.
     * @throws Exception 
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
        
        mock.perform(post("/logout"))
		.andExpect(status().isOk());
    }
    
    /**
     * Test of checkCredentials method, of class LoginEndpoint.
     * @throws Exception 
     */
    @Test
    public void testCheckBadCredentials() throws Exception {
    	String requestBody = "{\"username\": \"admin\", \"password\": \"noadmin\"}";
    	
        mock.perform(post("/login")
    		.content(requestBody))
    		.andExpect(status().is4xxClientError());
        
        mock.perform(get("/login"))
	        .andExpect(status().is4xxClientError());
        
        mock.perform(post("/logout"))
		.andExpect(status().isOk());
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
        
        mock.perform(get("/login"))
        .andExpect(status().is4xxClientError());
    }
    
}
