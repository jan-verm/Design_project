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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;


import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import config.ITConfig;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
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
public class OwnerShipIT {
	
	private MockMvc mock;
	
	@Autowired
    private WebApplicationContext webApplicationContext;
	
    private int userId;
    private int teacherId;
    private int adminId;
    private int courseId;

    @Before
    public void setup() throws Exception {
    	mock = webAppContextSetup(webApplicationContext).build();
        
        // create prof
    	String requestBody = "{\"username\": \"teacher\", \"password\": \"teacher\", \"role\": \"teacher\"}";
    	
    	String response = 
                mock.perform(post("/user")
    		.content(requestBody))
    		.andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        
        JSONObject json = new JSONObject(response);
        teacherId = json.getInt("userId");
        
    	// create user
    	requestBody = "{\"username\": \"student\", \"password\": \"student\", \"role\": \"student\"}";
    	
    	response = 
                mock.perform(post("/user")
    		.content(requestBody))
    		.andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        
        json = new JSONObject(response);
        userId = json.getInt("userId");
        
        
        // create admin
        requestBody = "{\"username\": \"admin\", \"password\": \"admin\", \"role\": \"admin\"}";
    	
    	response = 
                mock.perform(post("/user")
    		.content(requestBody))
    		.andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        
        json = new JSONObject(response);
        adminId = json.getInt("userId");
    }
    
    @After
    public void breakDown() throws Exception {
         // login as admin
        String requestBody = "{\"username\": \"admin\", \"password\": \"admin\"}";
    	
        mock.perform(post("/login")
    		.content(requestBody))
    		.andExpect(status().isOk());
        
    	// delete all
    	mock.perform(delete("/user/" + userId))
			.andExpect(status().isOk());
        mock.perform(delete("/user/" + teacherId))
			.andExpect(status().isOk());
        mock.perform(delete("/user/" + adminId))
			.andExpect(status().isOk());
        
        // logout
    	mock.perform(post("/logout"))
		.andExpect(status().isOk());
    }

    /**
     * Test of checkCredentials method, of class LoginEndpoint.
     * @throws Exception 
     */
    @Test
    public void testCheckOwnership() throws Exception {
         // login as teacher
        String requestBody = "{\"username\": \"teacher\", \"password\": \"teacher\"}";
    	
        mock.perform(post("/login")
    		.content(requestBody))
    		.andExpect(status().isOk());
        
        // create course
        requestBody = "{\"name\": \"course1\"}";
    	
    	String response = 
            mock.perform(post("/course")
    		.content(requestBody))
    		.andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();
        
        JSONObject json = new JSONObject(response);
        courseId = json.getInt("courseId");
        
    	//add video
        requestBody = "{\"name\": \"title\",\"url\": \"url\",\"duration\": 999}";
    	
    	response = mock.perform(post("/course/" + courseId + "/lecture/0/video")
    		.content(requestBody))
    		.andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        json = new JSONObject(response);
        int id = json.getInt("videoId");
        
        //add comment
    	String commentrequestBody = 
    		"{\"username\":\"user\",\"question\":\"false\",\"body\":\"body\","
    		+ "\"selfVideoReferences\":[{\"videoId\":" + id + ","
    		+ "\"timestamp\":12}]}";
    	
    	String commentresponse = mock.perform(post("/course/" + courseId + "/lecture/0/video/" + id + "/comment")
    		.content(commentrequestBody))
    		.andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        
        JSONObject commentjson = new JSONObject(commentresponse);
        int commentid = commentjson.getInt("commentId");
        
        mock.perform(post("/logout"))
            .andExpect(status().isOk());
        
        
        // try to edit comment when unauthorised
        requestBody = "{\"username\": \"student\", \"password\": \"student\"}";
    	
        mock.perform(post("/login")
    		.content(requestBody))
    		.andExpect(status().isOk());
        
        //update comment
        requestBody = 
    		"{\"question\":\"true\",\"body\":\"newbody\"}";
    	
    	mock.perform(patch("/course/" + courseId + "/lecture/0/video/" + id + "/comment/" + commentid)
    		.content(requestBody))
    		.andExpect(status().isUnauthorized());

        mock.perform(post("/logout"))
            .andExpect(status().isOk());
        
        
        // login as teacher
        requestBody = "{\"username\": \"teacher\", \"password\": \"teacher\"}";
    	
        mock.perform(post("/login")
    		.content(requestBody))
    		.andExpect(status().isOk());
        
        //delete all
        mock.perform(delete("/course/" + courseId + "/lecture/0/video/" + id + "/comment/" + commentid))
        	.andExpect(status().isOk());
        
        mock.perform(delete("/course/" + courseId + "/lecture/0/video/"+id))
    		.andExpect(status().isOk());
        
        mock.perform(delete("/course/" + courseId))
    		.andExpect(status().isOk());
        
        mock.perform(post("/logout"))
            .andExpect(status().isOk());
    }
}
