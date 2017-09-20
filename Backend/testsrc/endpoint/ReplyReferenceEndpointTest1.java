package endpoint;

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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = { TestConfig.class })
@IntegrationTest
@WebAppConfiguration
@TestPropertySource(locations="classpath:test.properties")
public class ReplyReferenceEndpointTest1 {

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
    public void testGetVideoReferences() throws Exception {
    	mock.perform(get("/reply/1/videoref"))
    			.andExpect(status().isOk())
    			.andExpect(jsonPath("$[0].videoId").value(1))
    			.andExpect(jsonPath("$[0].timestamp").value(12))
    			.andExpect(jsonPath("$[0].refId").value(0));
    }
    
    @Test
    public void testGetVideoReferencesNotFoundException() throws Exception {
    	mock.perform(get("/reply/0/videoref"))
            .andExpect(status().is4xxClientError());
    }
	
	@Test
	public void testGetCourseNotesReferences() throws Exception {
		mock.perform(get("/reply/1/coursenotesref"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$[0].courseNotesId").value(123))
			.andExpect(jsonPath("$[0].locations[0].pageNumber").value(20))
			.andExpect(jsonPath("$[0].locations[0].x1").value(1))
			.andExpect(jsonPath("$[0].locations[0].x2").value(2))
			.andExpect(jsonPath("$[0].locations[0].y1").value(3))
			.andExpect(jsonPath("$[0].locations[0].y2").value(4));
	}
        
    @Test
    public void testGetCourseNotesReferencesNotFoundException() throws Exception {
    	mock.perform(get("/reply/0/coursenotesref"))
            .andExpect(status().is4xxClientError());
    }
    
    @Test
    public void testPostVideoReference() throws Exception {
    	String requestBody = "{\"videoId\": 1,\"timestamp\": 12,\"duration\": 999}";
        
        mock.perform(post("/reply/1/videoref")
        	.content(requestBody))
        	.andExpect(status().isOk());
    }
    
    @Test
    public void testPostVideoReferenceNotFoundException() throws Exception {
    	String requestBody = "{\"videoId\": 1,\"timestamp\": 12,\"duration\": 999}";
        
        mock.perform(post("/reply/0/videoref")
        	.content(requestBody))
        	.andExpect(status().is4xxClientError());
    }
    
    @Test
    public void testPostVideoReferenceBadRequest() throws Exception {
    	String requestBody = "{\"videoId\": 1\"timestamp\": 12,\"duration\": 999}";
        
        mock.perform(post("/reply/1/videoref")
        	.content(requestBody))
        	.andExpect(status().isBadRequest());
    }
    
    @Test
    public void testPostCourseNotesReference() throws Exception {
    	String requestBody = "{ \"courseNotesId\": 123, \"locations\" : [ {\"pageNumber\": 20, \"x1\": 1, \"y1\": 2, \"x2\": 3, \"y2\": 4} ] }";
        
        mock.perform(post("/reply/1/coursenotesref")
        	.content(requestBody))
        	.andExpect(status().isOk());
    }
    
    @Test
    public void testPostCourseNotesReferenceNotFoundException() throws Exception {
    	String requestBody = "{ \"courseNotesId\": 123, \"locations\" : [ {\"pageNumber\": 20, \"x1\": 1, \"y1\": 2, \"x2\": 3, \"y2\": 4} ] }";
        
        mock.perform(post("/reply/0/coursenotesref")
        	.content(requestBody))
        	.andExpect(status().is4xxClientError());
    }
    
    @Test
    public void testPostCourseNotesReferenceBadRequest() throws Exception {
    	String requestBody = "{ \"courseNotesId\": 123 \"locations\" : [ {\"pageNumber\": 20, \"x1\": 1, \"y1\": 2, \"x2\": 3, \"y2\": 4} ] }";
        
        mock.perform(post("/reply/1/coursenotesref")
        	.content(requestBody))
        	.andExpect(status().isBadRequest());
    }
    
    @Test
    public void testPatchVideoReference() throws Exception {
    	String requestBody = "{\"videoId\": 1,\"timestamp\": 15,\"duration\": 999}";
        	
        mock.perform(patch("/reply/1/videoref/1")
        	.content(requestBody))
        	.andExpect(status().isOk());
    }
    
    @Test
    public void testPatchVideoReferenceNotFoundException() throws Exception {
    	String requestBody = "{\"videoId\": 1,\"timestamp\": 15,\"duration\": 999}";
        	
        mock.perform(patch("/reply/1/videoref/0")
        	.content(requestBody))
        	.andExpect(status().is4xxClientError());
    }
    
    @Test
    public void testPatchVideoReferenceBadRequest() throws Exception {
    	String requestBody = "{\"videoId\": 1\"timestamp\": 15,\"duration\": 999}";
        	
        mock.perform(patch("/reply/1/videoref/1")
        	.content(requestBody))
        	.andExpect(status().isBadRequest());
    }
    
    @Test
    public void testPatchCourseNotesReference() throws Exception {
    	String requestBody = "{ \"courseNotesId\": 123, \"locations\" : [{\"pageNumber\": 21, \"x1\": 1, \"y1\": 2, \"x2\": 3, \"y2\": 4}] }";
        	
    	mock.perform(patch("/reply/1/coursenotesref/1")
        	.content(requestBody))
        	.andExpect(status().isOk());
    }
    
    @Test
    public void testPatchCourseNotesReferenceNotFoundException() throws Exception {
    	String requestBody = "{ \"courseNotesId\": 123, \"locations\" : [{\"pageNumber\": 21, \"x1\": 1, \"y1\": 2, \"x2\": 3, \"y2\": 4}] }";
        	
    	mock.perform(patch("/reply/1/coursenotesref/0")
        	.content(requestBody))
        	.andExpect(status().is4xxClientError());
    }
    @Test
    public void testPatchCourseNotesReferenceBadRequest() throws Exception {
    	String requestBody = "{ \"courseNotesId\": 123 \"locations\" : [{\"pageNumber\": 21, \"x1\": 1, \"y1\": 2, \"x2\": 3, \"y2\": 4}] }";
        	
    	mock.perform(patch("/reply/1/coursenotesref/1")
        	.content(requestBody))
        	.andExpect(status().isBadRequest());
    }
    
    
    @Test
    public void testDeleteVideoReference() throws Exception {
    	mock.perform(delete("/reply/1/videoref/1"))
        	.andExpect(status().isOk());
    }
    
    @Test
    public void testDeleteVideoReferenceNotFoundException() throws Exception {
    	mock.perform(delete("/reply/1/videoref/0"))
        	.andExpect(status().is4xxClientError());
    }
    
    @Test
    public void testDeleteCourseNotesReference() throws Exception {
    	mock.perform(delete("/reply/1/coursenotesref/1"))
        	.andExpect(status().isOk());
    }
	
    @Test
    public void testDeleteCourseNotesReferenceNotFoundException() throws Exception {
    	mock.perform(delete("/reply/1/coursenotesref/0"))
        	.andExpect(status().is4xxClientError());
    }
}
