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
public class CourseEndpointTest {
    
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
     * Test of getCourses method, of class CourseEndpoint.
     */
    @Test
    public void testGetCourses() throws Exception {
        mock.perform(get("/course"))
	        .andExpect(status().isOk())
	        .andExpect(jsonPath("$[0].courseId").value(1))
	        .andExpect(jsonPath("$[0].name").value("course"));
    }

    /**
     * Test of getCourse method, of class CourseEndpoint.
     */
    @Test
    public void testGetCourse() throws Exception {
        mock.perform(get("/course/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.courseId").value(1))
            .andExpect(jsonPath("$.name").value("course"))
            .andExpect(jsonPath("$.lectures[0].lectureId").value(1))
            .andExpect(jsonPath("$.videos[0].videoId").value(1))
            .andExpect(jsonPath("$.courseNotes[0].courseNotesId").value(1));
    }

    /**
     * Test of postCourse method, of class CourseEndpoint.
     */
    @Test
    public void testPostCourse() throws Exception {
        String requestBody = "{\"name\": \"course1\"}";
    	
        mock.perform(post("/course")
    		.content(requestBody))
    		.andExpect(status().isOk())
                .andExpect(jsonPath("$.courseId").exists());
    }
    
    /**
     * Test of subscribe method, of class CourseEndpoint.
     */
    @Test
    public void testSubscribeCourse() throws Exception {
        mock.perform(post("/course/1/subscribe"))
    		.andExpect(status().isOk());
    }
    
    /**
     * Test of unsubscribe method, of class CourseEndpoint.
     */
    @Test
    public void testUnsubscribeCourse() throws Exception {
        mock.perform(post("/course/1/unsubscribe"))
    		.andExpect(status().isOk());
    }

    /**
     * Test of patchCourse method, of class CourseEndpoint.
     */
    @Test
    public void testPatchCourse() throws Exception {
        String requestBody = "{\"name\": \"new_course\"}";
       
        mock.perform(patch("/course/1")
            .content(requestBody))
            .andExpect(status().isOk());
    }

    /**
     * Test of deleteCourse method, of class CourseEndpoint.
     */
    @Test
    public void testDeleteCourse() throws Exception {
        mock.perform(delete("/course/1"))
	.andExpect(status().isOk());
    }
    
}
