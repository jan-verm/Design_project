package endpoint;

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

import config.TestConfig;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = { TestConfig.class })
@IntegrationTest
@WebAppConfiguration
@TestPropertySource(locations="classpath:test.properties")
public class VideoEndpointTest {

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
    
    @Test
    public void testGetVideo() throws Exception {
    	mock.perform(get("/course/1/lecture/0/video/1"))
	        .andExpect(status().isOk())
	        .andExpect(jsonPath("$.videoId").value(1))
	        .andExpect(jsonPath("$.name").value("title"))
	        .andExpect(jsonPath("$.url").value("url"))
	        .andExpect(jsonPath("$.duration").value(999));
    }
    
    @Test()
    public void testGetVideoNotFoundException() throws Exception {
    	mock.perform(get("/course/1/lecture/0/video/0"))
	        .andExpect(status().is4xxClientError());
    }
    
    @Test
    public void testGetVideos() throws Exception {
    	mock.perform(get("/course/1/lecture/0/video"))
	        .andExpect(status().isOk())
	        .andExpect(jsonPath("$[0].videoId").value(1))
	        .andExpect(jsonPath("$[0].name").value("title"))
	        .andExpect(jsonPath("$[0].url").value("url"))
	        .andExpect(jsonPath("$[0].duration").value(999));
    }
    
    @Test
    public void testUpdateVideo() throws Exception {
    	String requestBody = "{\"name\": \"string_edit\",\"url\": \"string\",\"duration\": 0}";
    	mock.perform(patch("/course/1/lecture/0/video/1")
        		.content(requestBody))
        		.andExpect(status().isOk());
    }
    
    @Test()
    public void testUpdateVideoNotFoundException() throws Exception {
    	String requestBody = "{\"name\": \"string_edit\",\"url\": \"string\",\"duration\": 0}";
    	mock.perform(patch("/course/1/lecture/0/video/0")
        	.content(requestBody))
	        .andExpect(status().is4xxClientError());
    }
    
    @Test()
    public void testUpdateVideoBadRequest() throws Exception {
    	String requestBody = "{\"name\": \"string_edit\"\"url\": \"string\",\"duration\": 0}";
    	mock.perform(patch("/course/1/lecture/0/video/1")
        	.content(requestBody))
	        .andExpect(status().isBadRequest());
    }
    
    @Test
    public void testPostVideo() throws Exception {
    	String requestBody = "{\"name\": \"string\",\"url\": \"string\",\"duration\": 0}";
    	
    	mock.perform(post("/course/1/lecture/0/video")
    		.content(requestBody))
    		.andExpect(status().isOk())
    		.andExpect(jsonPath("$.videoId").value(1));
    }
    
    @Test
    public void testPostLectureVideo() throws Exception {
    	String requestBody = "{\"name\": \"string\",\"url\": \"string\",\"duration\": 0}";
    	
    	mock.perform(post("/course/1/lecture/1/video")
    		.content(requestBody))
    		.andExpect(status().isOk())
    		.andExpect(jsonPath("$.videoId").value(1));
    }
    
    @Test()
    public void testPostVideoBadRequest() throws Exception {
    	String requestBody = "{\"name\": \"string_edit\"\"url\": \"string\",\"duration\": 0}";
    	mock.perform(post("/course/1/lecture/0/video")
    		.content(requestBody))
	        .andExpect(status().isBadRequest());
    }
    
    @Test
    public void testDeleteVideo() throws Exception {
    	mock.perform(delete("/course/1/lecture/0/video/1"))
    		.andExpect(status().isOk());
    }
    
    @Test
    public void testDeleteVideoNotFoundException() throws Exception {
    	mock.perform(delete("/course/1/lecture/0/video/0"))
    		.andExpect(status().is4xxClientError());
    }
    
}
