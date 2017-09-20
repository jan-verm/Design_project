/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package endpoint;

import config.TestConfig;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.ResponseEntity;
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
public class LectureEndpointTest {
    
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
     * Test of getLectures method, of class LectureEndpoint.
     */
    @Test
    public void testGetLectures() throws Exception {
        mock.perform(get("/course/1/lecture"))
	        .andExpect(status().isOk())
	        .andExpect(jsonPath("$[0].lectureId").value(1))
	        .andExpect(jsonPath("$[0].name").value("lecture"));
    }

    /**
     * Test of getLecture method, of class LectureEndpoint.
     */
    @Test
    public void testGetLecture() throws Exception {
        mock.perform(get("/course/1/lecture/1"))
	        .andExpect(status().isOk())
	        .andExpect(jsonPath("$.lectureId").value(1))
	        .andExpect(jsonPath("$.name").value("lecture"))
	        .andExpect(jsonPath("$.videos[0].videoId").value(1))
	        .andExpect(jsonPath("$.courseNotes[0].courseNotesId").value(1));
    }

    /**
     * Test of postLecture method, of class LectureEndpoint.
     */
    @Test
    public void testPostLecture() throws Exception {
        String requestBody = "{\"name\": \"lecture1\"}";
        
        mock.perform(post("/course/1/lecture")
            .content(requestBody))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.lectureId").exists());
    }

    /**
     * Test of patchLecture method, of class LectureEndpoint.
     */
    @Test
    public void testPatchLecture() throws Exception {
        String requestBody = "{\"name\": \"new_lecture\"}";
        
        mock.perform(patch("/course/1/lecture/1")
    		.content(requestBody))
    		.andExpect(status().isOk());
    }

    /**
     * Test of deleteLecture method, of class LectureEndpoint.
     */
    @Test
    public void testDeleteLecture() throws Exception {
        mock.perform(delete("/course/1/lecture/1"))
		.andExpect(status().isOk());
    }
    
}
