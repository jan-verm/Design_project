package endpoint;

import config.ITConfig;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.After;
import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

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
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = { ITConfig.class })
@IntegrationTest
@WebAppConfiguration
@TestPropertySource(locations="classpath:test.properties")
public class VideoEndpointIT {

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
    public void testGetVideo() throws Exception {
        String requestBody = "{\"name\": \"title\",\"url\": \"url\",\"duration\": 999}";
    	
    	String response = mock.perform(post("/course/" + courseId + "/lecture/0/video")
    		.content(requestBody))
    		.andExpect(status().isOk()).
                andReturn().getResponse().getContentAsString();

        JSONObject json = new JSONObject(response);
        int id = json.getInt("videoId");
        
    	mock.perform(get("/course/" + courseId + "/lecture/0/video/"+id))
	        .andExpect(status().isOk())
	        .andExpect(jsonPath("$.videoId").value(id))
	        .andExpect(jsonPath("$.name").value("title"))
	        .andExpect(jsonPath("$.url").value("url"))
	        .andExpect(jsonPath("$.duration").value(999));
        
        mock.perform(delete("/course/" + courseId + "/lecture/0/video/"+id))
    		.andExpect(status().isOk());
    }
    
    @Test
    public void testGetVideoNotFoundException() throws Exception {
    	mock.perform(get("/course/" + courseId + "/lecture/0/video/0"))
	        .andExpect(status().is4xxClientError());
    }
    
    @Test
    public void testGetVideos() throws Exception {
        String requestBody = "{\"name\": \"title\",\"url\": \"url\",\"duration\": 999}";
    	
   	String response = mock.perform(post("/course/" + courseId + "/lecture/0//video")
    		.content(requestBody))
   		.andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        JSONObject json = new JSONObject(response);
        int id = json.getInt("videoId");
        
    	mock.perform(get("/course/" + courseId + "/lecture/0//video"))
	        .andExpect(status().isOk())
	        .andExpect(jsonPath("$[0].videoId").value(id))
	        .andExpect(jsonPath("$[0].name").value("title"))
	        .andExpect(jsonPath("$[0].url").value("url"))
	        .andExpect(jsonPath("$[0].duration").value(999));
        
        mock.perform(delete("/course/" + courseId + "/lecture/0//video/"+id))
   		.andExpect(status().isOk());
    }
    
    @Test
    public void testGetVideosIsEmpty() throws Exception {     
    	String response = mock.perform(get("/course/" + courseId + "/lecture/0/video"))
	        .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        
        JSONArray array = new JSONArray(response);
        assertEquals(0, array.length());
    }
    
    @Test
    public void testUpdateVideo() throws Exception {
        String requestBody = "{\"name\": \"title\",\"url\": \"url\",\"duration\": 999}";
    	
    	String response = mock.perform(post("/course/" + courseId + "/lecture/0/video")
    		.content(requestBody))
    		.andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        JSONObject json = new JSONObject(response);
        int id = json.getInt("videoId");
        
    	String updaterequestBody = "{\"name\": \"newtitle\",\"url\": \"newurl\",\"duration\": 888}";
    	mock.perform(patch("/course/" + courseId + "/lecture/0//video/"+id)
        		.content(updaterequestBody))
        		.andExpect(status().isOk());
        
        mock.perform(get("/course/" + courseId + "/lecture/0/video/"+id))
	        .andExpect(status().isOk())
	        .andExpect(jsonPath("$.videoId").value(id))
	        .andExpect(jsonPath("$.name").value("newtitle"))
	        .andExpect(jsonPath("$.url").value("newurl"))
	        .andExpect(jsonPath("$.duration").value(888));
        
        mock.perform(delete("/course/" + courseId + "/lecture/0/video/"+id))
    		.andExpect(status().isOk());
    }
    
    @Test
    public void testUpdateVideoNotFoundException() throws Exception {
    	String updaterequestBody = "{\"name\": \"newtitle\",\"url\": \"newurl\",\"duration\": 888}";
    	mock.perform(patch("/course/" + courseId + "/lecture/0/video/0")
        		.content(updaterequestBody))
        		.andExpect(status().is4xxClientError());
    }
    
    @Test
    public void testUpdateVideoBadRequest() throws Exception {
        String requestBody = "{\"name\": \"title\",\"url\": \"url\",\"duration\": 999}";
    	
    	String response = mock.perform(post("/course/" + courseId + "/lecture/0/video")
    		.content(requestBody))
    		.andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        JSONObject json = new JSONObject(response);
        int id = json.getInt("videoId");
        
    	String updaterequestBody = "{\"name\": \"newtitle\"\"url\": \"newurl\",\"duration\": 888}";
    	mock.perform(patch("/course/" + courseId + "/lecture/0//video/"+id)
        		.content(updaterequestBody))
        		.andExpect(status().isBadRequest());
        
        mock.perform(delete("/course/" + courseId + "/lecture/0/video/"+id))
    		.andExpect(status().isOk());
    }
    
    @Test
    public void testPostVideo() throws Exception {
    	String requestBody = "{\"name\": \"title\",\"url\": \"url\",\"duration\": 999}";
    	
    	String response = mock.perform(post("/course/" + courseId + "/lecture/0//video")
    		.content(requestBody))
    		.andExpect(status().isOk())
                .andExpect(jsonPath("$.videoId").exists())
                .andReturn().getResponse().getContentAsString();

        JSONObject json = new JSONObject(response);
        int id = json.getInt("videoId");
        
        mock.perform(delete("/course/" + courseId + "/lecture/0//video/"+id))
    		.andExpect(status().isOk());
    }
    
    @Test
    public void testPostVideoBadRequest() throws Exception {
    	String requestBody = "{\"name\": \"title\"\"url\": \"url\",\"duration\": 999}";
    	
    	mock.perform(post("/course/" + courseId + "/lecture/0/video")
    		.content(requestBody))
    		.andExpect(status().isBadRequest());
    }
    
    @Test
    public void testDeleteVideoNotFoundException() throws Exception {
        mock.perform(delete("/course/" + courseId + "/lecture/0/video/0"))
    		.andExpect(status().is4xxClientError());
    }
}
