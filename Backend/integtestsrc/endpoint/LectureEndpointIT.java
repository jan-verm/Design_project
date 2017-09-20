package endpoint;

import config.ITConfig;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.After;
import static org.junit.Assert.assertEquals;
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

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = { ITConfig.class })
@IntegrationTest
@WebAppConfiguration
@TestPropertySource(locations="classpath:test.properties")
public class LectureEndpointIT {
    private MockMvc mock;
	
    @Autowired
    private WebApplicationContext webApplicationContext;
    
    private int courseId;
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
        
        // create course
        requestBody = "{\"name\": \"course1\"}";
    	
    	response = 
            mock.perform(post("/course")
    		.content(requestBody))
    		.andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();
        
        json = new JSONObject(response);
        courseId = json.getInt("courseId");
    }
    
    @After
    public void tearDown() throws Exception {
    	//delete course
        mock.perform(delete("/course/" + courseId))
    		.andExpect(status().isOk());
        
        // delete user
    	mock.perform(delete("/user/" + userId))
			.andExpect(status().isOk());
        
        // logout
    	mock.perform(post("/logout"))
		.andExpect(status().isOk());
    }
    
    @Test
    public void testGetLecture() throws Exception {
        // add lecture
        String requestBody = "{\"name\": \"lecture1\"}";
        
        String response = 
                mock.perform(post("/course/" + courseId + "/lecture")
    		.content(requestBody))
    		.andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        
        JSONObject json = new JSONObject(response);
        int lectureId = json.getInt("lectureId");
        
        // add video
        requestBody = "{\"name\": \"title\",\"url\": \"url\",\"duration\": 999}";
    	
    	response = mock.perform(post("/course/" + courseId + "/lecture/" + lectureId + "/video")
    		.content(requestBody))
    		.andExpect(status().isOk()).
                andReturn().getResponse().getContentAsString();

        json = new JSONObject(response);
        int videoid = json.getInt("videoId");
        
        // add course notes
        requestBody = "{\"name\": \"title\", \"url\": \"url\"}";
    	
    	response = mock.perform(post("/course/" + courseId + "/lecture/" + lectureId + "/coursenotes")
    		.content(requestBody))
    		.andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        json = new JSONObject(response);
        int notesId = json.getInt("courseNotesId");
        
        // get lecture
    	mock.perform(get("/course/" + courseId + "/lecture/" + lectureId))
	        .andExpect(status().isOk())
	        .andExpect(jsonPath("$.lectureId").value(lectureId))
	        .andExpect(jsonPath("$.name").value("lecture1"))
	        .andExpect(jsonPath("$.videos[0].videoId").value(videoid))
	        .andExpect(jsonPath("$.courseNotes[0].courseNotesId").value(notesId));
        
        
        // delete all
        mock.perform(delete("/course/" + courseId + "/lecture/" + lectureId + "/coursenotes/" + notesId))
    		.andExpect(status().isOk());
        
        mock.perform(delete("/course/" + courseId + "/lecture/" + lectureId + "/video/"+videoid))
    		.andExpect(status().isOk());
        
        mock.perform(delete("/course/" + courseId +"/lecture/" + lectureId))
		.andExpect(status().isOk());
    }
    
    @Test
    public void testGetLectureNotFoundException() throws Exception {
        // get course
    	mock.perform(get("/course/" + courseId + "/lecture/0"))
	        .andExpect(status().is4xxClientError());
    }
    
    @Test
    public void testGetLectures() throws Exception {
        // add lecture
        String requestBody = "{\"name\": \"lecture1\"}";
        
        String response = 
                mock.perform(post("/course/" + courseId + "/lecture")
    		.content(requestBody))
    		.andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        
        JSONObject json = new JSONObject(response);
        int lectureId = json.getInt("lectureId");
        
        // add second lecture
        requestBody = "{\"name\": \"lecture2\"}";
        
        response = 
                mock.perform(post("/course/" + courseId + "/lecture")
    		.content(requestBody))
    		.andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        
        json = new JSONObject(response);
        int lectureId2 = json.getInt("lectureId");
        
        // get lectures
    	mock.perform(get("/course/" + courseId + "/lecture"))
	        .andExpect(status().isOk())
	        .andExpect(jsonPath("$[0].lectureId").value(lectureId))
	        .andExpect(jsonPath("$[0].name").value("lecture1"))
                .andExpect(jsonPath("$[1].lectureId").value(lectureId2))
	        .andExpect(jsonPath("$[1].name").value("lecture2"));
        
        mock.perform(delete("/course/" + courseId +"/lecture/" + lectureId))
		.andExpect(status().isOk());
        
        mock.perform(delete("/course/" + courseId +"/lecture/" + lectureId2))
		.andExpect(status().isOk());
    }
    
    @Test
    public void testGetLecturesIsEmpty() throws Exception {
        String response = mock.perform(get("/course/" + courseId + "/lecture"))
	        .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        
        JSONArray array = new JSONArray(response);
        assertEquals(0, array.length());
    }
    
    @Test
    public void testPostLecture() throws Exception {
        // add lecture
        String requestBody = "{\"name\": \"lecture1\"}";
        
        String response = 
                mock.perform(post("/course/" + courseId + "/lecture")
    		.content(requestBody))
    		.andExpect(status().isOk())
                .andExpect(jsonPath("$.lectureId").exists())
                .andReturn().getResponse().getContentAsString();
        
        JSONObject json = new JSONObject(response);
        int lectureId = json.getInt("lectureId");

        mock.perform(delete("/course/" + courseId +"/lecture/" + lectureId))
		.andExpect(status().isOk());
    }
    
    @Test
    public void testPostLectureNotFoundException() throws Exception {
        // add lecture
        String requestBody = "{\"name\": \"lecture1\"}";
        
        mock.perform(post("/course/0/lecture")
    		.content(requestBody))
    		.andExpect(status().is4xxClientError());
    }
    
    @Test
    public void testPostLectureBadRequest() throws Exception {
        // add lecture
        String requestBody = "{\"name\" \"lecture1\"}";
        
        mock.perform(post("/course/" + courseId + "/lecture")
    		.content(requestBody))
    		.andExpect(status().isBadRequest());
    }
    
    @Test
    public void testPatchLecture() throws Exception {
        // add lecture
        String requestBody = "{\"name\": \"lecture1\"}";
        
        String response = 
                mock.perform(post("/course/" + courseId + "/lecture")
    		.content(requestBody))
    		.andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        
        JSONObject json = new JSONObject(response);
        int lectureId = json.getInt("lectureId");

        // patch lecture
        requestBody = "{\"name\": \"new_lecture\"}";
        
        mock.perform(patch("/course/" + courseId + "/lecture/" + lectureId)
    		.content(requestBody))
    		.andExpect(status().isOk());

        // get lecture
    	mock.perform(get("/course/" + courseId + "/lecture/" + lectureId))
	        .andExpect(status().isOk())
	        .andExpect(jsonPath("$.lectureId").value(lectureId))
	        .andExpect(jsonPath("$.name").value("new_lecture"));
        
        
        // delete
        mock.perform(delete("/course/" + courseId +"/lecture/" + lectureId))
		.andExpect(status().isOk());
    }
    
    @Test
    public void testPatchLectureNotFoundException() throws Exception {
        String requestBody = "{\"name\": \"new_lecture\"}";
        
        mock.perform(patch("/course/" + courseId + "/lecture/0")
    		.content(requestBody))
    		.andExpect(status().is4xxClientError());
    }
    
    @Test
    public void testPatchLectureBadRequest() throws Exception {
        // add lecture
        String requestBody = "{\"name\": \"lecture1\"}";
        
        String response = 
                mock.perform(post("/course/" + courseId + "/lecture")
    		.content(requestBody))
    		.andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        
        JSONObject json = new JSONObject(response);
        int lectureId = json.getInt("lectureId");
        
        //patch lecture
        requestBody = "{\"name\" \"new_lecture\"}";
        
        mock.perform(patch("/course/" + courseId + "/lecture/0")
    		.content(requestBody))
    		.andExpect(status().isBadRequest());
        
        // delete
        mock.perform(delete("/course/" + courseId +"/lecture/" + lectureId))
		.andExpect(status().isOk());
    }
    
    @Test
    public void testDeleteLectureNotFoundException() throws Exception {
        mock.perform(delete("/course/" + courseId +"/lecture/0"))
		.andExpect(status().is4xxClientError());
    }
}
