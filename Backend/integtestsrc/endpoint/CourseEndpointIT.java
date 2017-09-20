package endpoint;

import config.ITConfig;
import org.json.JSONArray;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import org.json.JSONObject;
import static org.junit.Assert.assertEquals;

import org.junit.After;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = { ITConfig.class })
@IntegrationTest
@WebAppConfiguration
@TestPropertySource(locations="classpath:test.properties")
public class CourseEndpointIT {
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
        
        // login
        requestBody = "{\"username\": \"admin\", \"password\": \"admin\"}";
    	
        mock.perform(post("/login")
    		.content(requestBody))
    		.andExpect(status().isOk());
    }
    
    @After
    public void tearDown() throws Exception { 
    	// delete user
    	mock.perform(delete("/user/" + userId))
			.andExpect(status().isOk());
        
        // logout
    	mock.perform(post("/logout"))
		.andExpect(status().isOk());
    }
    
    @Test
    public void testGetCourse() throws Exception {
        
        // add course
    	String requestBody = "{\"name\": \"course1\"}";
    	
    	String response = 
                mock.perform(post("/course")
    		.content(requestBody))
    		.andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        
        JSONObject json = new JSONObject(response);
        int courseId = json.getInt("courseId");
        
        // add lecture
        requestBody = "{\"name\": \"lecture1\"}";
        
        response = 
                mock.perform(post("/course/" + courseId + "/lecture")
    		.content(requestBody))
    		.andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();
        
        json = new JSONObject(response);
        int lectureId = json.getInt("lectureId");
        
        // add video
        requestBody = "{\"name\": \"title\",\"url\": \"url\",\"duration\": 999}";
    	
    	response = mock.perform(post("/course/" + courseId + "/lecture/0/video")
    		.content(requestBody))
    		.andExpect(status().isOk()).
                andReturn().getResponse().getContentAsString();

        json = new JSONObject(response);
        int videoid = json.getInt("videoId");
        
        // add course notes
        requestBody = "{\"name\": \"title\", \"url\": \"url\"}";
    	
    	response = mock.perform(post("/course/" + courseId + "/lecture/0/coursenotes")
    		.content(requestBody))
    		.andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        json = new JSONObject(response);
        int notesId = json.getInt("courseNotesId");
        
        // get course
    	mock.perform(get("/course/" + courseId))
	        .andExpect(status().isOk())
	        .andExpect(jsonPath("$.courseId").value(courseId))
	        .andExpect(jsonPath("$.name").value("course1"))
	        .andExpect(jsonPath("$.lectures[0].lectureId").value(lectureId))
	        .andExpect(jsonPath("$.videos[0].videoId").value(videoid))
	        .andExpect(jsonPath("$.courseNotes[0].courseNotesId").value(notesId));
        
        
        // delete all
        mock.perform(delete("/course/" + courseId + "/lecture/0/coursenotes/" + notesId))
    		.andExpect(status().isOk());
        
        mock.perform(delete("/course/" + courseId + "/lecture/0/video/"+videoid))
    		.andExpect(status().isOk());
        
        mock.perform(delete("/course/" + courseId +"/lecture/" + lectureId))
		.andExpect(status().isOk());
        
    	mock.perform(delete("/course/" + courseId))
		.andExpect(status().isOk());
    }
    
    @Test
    public void testGetCourseNotFoundException() throws Exception {
    	mock.perform(get("/course/0"))
	        .andExpect(status().is4xxClientError());
    }
    
    @Test
    public void testGetCourses() throws Exception {
        
        // add course
    	String requestBody = "{\"name\": \"course1\"}";
    	
    	String response = 
                mock.perform(post("/course")
    		.content(requestBody))
    		.andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        
        JSONObject json = new JSONObject(response);
        int courseId = json.getInt("courseId");
        
        // add second course
        requestBody = "{\"name\": \"course2\"}";
    	
    	response = 
                mock.perform(post("/course")
    		.content(requestBody))
    		.andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        
        json = new JSONObject(response);
        int courseId2 = json.getInt("courseId");
        
        mock.perform(get("/course"))
	        .andExpect(status().isOk())
	        .andExpect(jsonPath("$[0].courseId").value(courseId))
	        .andExpect(jsonPath("$[0].name").value("course1"))
                .andExpect(jsonPath("$[1].courseId").value(courseId2))
	        .andExpect(jsonPath("$[1].name").value("course2"));
        
        mock.perform(delete("/course/" + courseId))
		.andExpect(status().isOk());
        
        mock.perform(delete("/course/" + courseId2))
		.andExpect(status().isOk());
    }
    
    @Test
    public void testGetCoursesIsEmpty() throws Exception {     
    	String response = mock.perform(get("/course"))
	        .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        
        JSONArray array = new JSONArray(response);
        assertEquals(0, array.length());
    }
    
    @Test
    public void testPostCourse() throws Exception {
        
        // add course
    	String requestBody = "{\"name\": \"course1\"}";
    	
        String response = mock.perform(post("/course")
    		.content(requestBody))
    		.andExpect(status().isOk())
                .andExpect(jsonPath("$.courseId").exists())
                .andReturn().getResponse().getContentAsString();
        
        JSONObject json = new JSONObject(response);
        int courseId = json.getInt("courseId");
        
        mock.perform(delete("/course/" + courseId))
		.andExpect(status().isOk());
    }
    
    @Test
    public void testPostCourseBadRequest() throws Exception {
    	String requestBody = "{\"name\" \"course1\"}";
    	
        mock.perform(post("/course")
    		.content(requestBody))
    		.andExpect(status().isBadRequest());
    }
    
    @Test
    public void testPatchCourse() throws Exception {
        
        // add course
    	String requestBody = "{\"name\": \"course1\"}";
    	
    	String response = 
                mock.perform(post("/course")
    		.content(requestBody))
    		.andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        
        JSONObject json = new JSONObject(response);
        int courseId = json.getInt("courseId");
        
        // patch course
        requestBody = "{\"name\": \"new_course\"}";
       
        mock.perform(patch("/course/" + courseId)
                .content(requestBody))
    		.andExpect(status().isOk());
        
        // get course
    	mock.perform(get("/course/" + courseId))
	        .andExpect(status().isOk())
	        .andExpect(jsonPath("$.courseId").value(courseId))
	        .andExpect(jsonPath("$.name").value("new_course"));
        
        // delete     
    	mock.perform(delete("/course/" + courseId))
		.andExpect(status().isOk());
    }
    
    @Test
    public void testPatchCourseNotFoundException() throws Exception {
    	String requestBody = "{\"name\": \"course1\"}";
    	mock.perform(patch("/course/0")
        		.content(requestBody))
        		.andExpect(status().is4xxClientError());
    }
    
    @Test
    public void testPatchCourseBadRequest() throws Exception {
        
        // add course
    	String requestBody = "{\"name\": \"course1\"}";
    	
    	String response = 
                mock.perform(post("/course")
    		.content(requestBody))
    		.andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        
        JSONObject json = new JSONObject(response);
        int courseId = json.getInt("courseId");
        
        // patch course
        requestBody = "{\"name\" \"new_course\"}";
       
        mock.perform(patch("/course/" + courseId)
                .content(requestBody))
    		.andExpect(status().isBadRequest());
        
        // delete     
    	mock.perform(delete("/course/" + courseId))
		.andExpect(status().isOk());
    }
    
    @Test
    public void testDeleteCourseNotFoundException() throws Exception {
        mock.perform(delete("/course/0"))
    		.andExpect(status().is4xxClientError());
    }
}
