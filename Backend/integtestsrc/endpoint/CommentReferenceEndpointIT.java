package endpoint;

import config.ITConfig;
import org.json.JSONObject;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;
import org.springframework.web.context.WebApplicationContext;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = { ITConfig.class })
@IntegrationTest
@WebAppConfiguration
@TestPropertySource(locations="classpath:test.properties")
public class CommentReferenceEndpointIT {

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
    public void testGetVideoReferences() throws Exception {
        //add video
        String requestBody = "{\"name\": \"title\",\"url\": \"url\",\"duration\": 999}";
    	
    	String response = mock.perform(post("/course/" + courseId + "/lecture/0/video")
    		.content(requestBody))
    		.andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        JSONObject json = new JSONObject(response);
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
        
        //add second video
        requestBody = "{\"name\": \"title2\",\"url\": \"url2\",\"duration\": 998}";
    	
    	response = mock.perform(post("/course/" + courseId + "/lecture/0/video")
    		.content(requestBody))
    		.andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        json = new JSONObject(response);
        int id2 = json.getInt("videoId");
        
        //add extra reference to second video
        requestBody = "{\"videoId\": " + id2 + ",\"timestamp\": 40}";
        
        mock.perform(post("/course/" + courseId + "/lecture/0/comment/" + commentid + "/videoref")
        	.content(requestBody))
        	.andExpect(status().isOk());
        
        // get references
        mock.perform(get("/course/" + courseId + "/lecture/0/comment/" + commentid + "/videoref"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].refId").exists())
                .andExpect(jsonPath("$[0].videoId").value(id))
                .andExpect(jsonPath("$[0].timestamp").value(12))
                .andExpect(jsonPath("$[1].refId").exists())
                .andExpect(jsonPath("$[1].videoId").value(id2))
                .andExpect(jsonPath("$[1].timestamp").value(40));
        
        //delete all
        mock.perform(delete("/course/" + courseId + "/lecture/0/video/" + id + "/comment/" + commentid))
        	.andExpect(status().isOk());
        
        mock.perform(delete("/course/" + courseId + "/lecture/0/video/"+id))
    		.andExpect(status().isOk());
        
        mock.perform(delete("/course/" + courseId + "/lecture/0/video/"+id2))
    		.andExpect(status().isOk());
    }
	
    @Test
    public void testGetCourseNotesReferences() throws Exception {
        //add course notes
        String requestBody = "{\"name\": \"title\", \"url\": \"url\"}";
    	
    	String response = mock.perform(post("/course/" + courseId + "/lecture/0/coursenotes")
    		.content(requestBody))
    		.andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        JSONObject json = new JSONObject(response);
        int id = json.getInt("courseNotesId");
        
        // add comment
    	requestBody = 
    		"{\"username\": \"user\", \"body\": "
    		+ "\"body\", \"selfCourseNotesReferences\": [{\"locations\" :[{ "
    				+ "\"pageNumber\": 20, \"x1\": 1, \"y1\": 2, \"x2\": 3, \"y2\": 4 }]}]"
    		+ ", \"question\": false }";
    	
    	String commentresponse = mock.perform(post("/course/" + courseId + "/lecture/0/coursenotes/" + id + "/comment")
    		.content(requestBody))
    		.andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        
        JSONObject commentjson = new JSONObject(commentresponse);
        int commentid = commentjson.getInt("commentId");
        
        // add second course notes
        requestBody = "{\"name\": \"title\", \"url\": \"url\"}";
    	
    	response = mock.perform(post("/course/" + courseId + "/lecture/0/coursenotes")
    		.content(requestBody))
    		.andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        json = new JSONObject(response);
        int id2 = json.getInt("courseNotesId");
        
        // add extra reference to second course notes
        requestBody = "{ \"courseNotesId\": " + id2 + ", \"locations\":[{\"pageNumber\": 10, \"x1\": 2, \"y1\": 3, \"x2\": 4, \"y2\": 5}] }";
        
        mock.perform(post("/course/" + courseId + "/lecture/0/comment/" + commentid + "/coursenotesref")
        	.content(requestBody))
        	.andExpect(status().isOk());
        
        //get references
        mock.perform(get("/course/" + courseId + "/lecture/0/comment/" + commentid + "/coursenotesref"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].courseNotesId").value(id))
                .andExpect(jsonPath("$[0].refId").exists())
                .andExpect(jsonPath("$[0].locations.[0].pageNumber").value(20))
                .andExpect(jsonPath("$[0].locations.[0].x1").value(1))
                .andExpect(jsonPath("$[0].locations.[0].x2").value(3))
                .andExpect(jsonPath("$[0].locations.[0].y1").value(2))
                .andExpect(jsonPath("$[0].locations.[0].y2").value(4))
                
                .andExpect(jsonPath("$[1].courseNotesId").value(id2))
                .andExpect(jsonPath("$[1].refId").exists())
                .andExpect(jsonPath("$[1].locations.[0].pageNumber").value(10))
                .andExpect(jsonPath("$[1].locations.[0].x1").value(2))
                .andExpect(jsonPath("$[1].locations.[0].x2").value(4))
                .andExpect(jsonPath("$[1].locations.[0].y1").value(3))
                .andExpect(jsonPath("$[1].locations.[0].y2").value(5));
        
        // delete all
        mock.perform(delete("/course/" + courseId + "/lecture/0/coursenotes/" + id + "/comment/" + commentid))
    		.andExpect(status().isOk());
        mock.perform(delete("/course/" + courseId + "/lecture/0/coursenotes/" + id))
    		.andExpect(status().isOk());
        mock.perform(delete("/course/" + courseId + "/lecture/0/coursenotes/" + id2))
    		.andExpect(status().isOk());
    }
    
    /*
    * Test that adds coursenotes with self ref and then an extra ref to a video
    */
    @Test
    public void testGetReferencesWithCrossRef_Direction1() throws Exception {
        //add course notes
        String requestBody = "{\"name\": \"title\", \"url\": \"url\"}";
    	
    	String response = mock.perform(post("/course/" + courseId + "/lecture/0/coursenotes")
    		.content(requestBody))
    		.andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        JSONObject json = new JSONObject(response);
        int id = json.getInt("courseNotesId");
        
        // add comment
    	requestBody = 
    		"{\"username\": \"user\", \"body\": "
    		+ "\"body\", \"selfCourseNotesReferences\": [{\"locations\" :[{ "
    				+ "\"pageNumber\": 20, \"x1\": 1, \"y1\": 2, \"x2\": 3, \"y2\": 4 }]}]"
    		+ ", \"question\": false }";
    	
    	String commentresponse = mock.perform(post("/course/" + courseId + "/lecture/0/coursenotes/" + id + "/comment")
    		.content(requestBody))
    		.andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        
        JSONObject commentjson = new JSONObject(commentresponse);
        int commentid = commentjson.getInt("commentId");
        
        //add video
        requestBody = "{\"name\": \"title2\",\"url\": \"url2\",\"duration\": 998}";
    	
    	response = mock.perform(post("/course/" + courseId + "/lecture/0/video")
    		.content(requestBody))
    		.andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        json = new JSONObject(response);
        int id2 = json.getInt("videoId");
        
        //add extra reference to video
        requestBody = "{\"videoId\": " + id2 + ",\"timestamp\": 40}";
        
        mock.perform(post("/course/" + courseId + "/lecture/0/comment/" + commentid + "/videoref")
        	.content(requestBody))
        	.andExpect(status().isOk());
        
        //get coursenotes references
        mock.perform(get("/course/" + courseId + "/lecture/0/comment/" + commentid + "/coursenotesref"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].courseNotesId").value(id))
                .andExpect(jsonPath("$[0].refId").exists())
                .andExpect(jsonPath("$[0].locations.[0].pageNumber").value(20))
                .andExpect(jsonPath("$[0].locations.[0].x1").value(1))
                .andExpect(jsonPath("$[0].locations.[0].x2").value(3))
                .andExpect(jsonPath("$[0].locations.[0].y1").value(2))
                .andExpect(jsonPath("$[0].locations.[0].y2").value(4));
        
        // get video references
        mock.perform(get("/course/" + courseId + "/lecture/0/comment/" + commentid + "/videoref"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].refId").exists())
                .andExpect(jsonPath("$[0].videoId").value(id2))
                .andExpect(jsonPath("$[0].timestamp").value(40));
        
        // delete all
        mock.perform(delete("/course/" + courseId + "/lecture/0/coursenotes/" + id + "/comment/" + commentid))
    		.andExpect(status().isOk());
        mock.perform(delete("/course/" + courseId + "/lecture/0/coursenotes/" + id))
    		.andExpect(status().isOk());
        mock.perform(delete("/course/" + courseId + "/lecture/0/video/" + id2))
    		.andExpect(status().isOk());
    }
    
    /*
    * Test that adds a video with self ref and then an extra ref to coursenotes
    */
    @Test
    public void testGetReferencesWithCrossRef_Direction2() throws Exception {
        //add video
        String requestBody = "{\"name\": \"title\",\"url\": \"url\",\"duration\": 999}";
    	
    	String response = mock.perform(post("/course/" + courseId + "/lecture/0/video")
    		.content(requestBody))
    		.andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        JSONObject json = new JSONObject(response);
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
        
        // add course notes
        requestBody = "{\"name\": \"title\", \"url\": \"url\"}";
    	
    	response = mock.perform(post("/course/" + courseId + "/lecture/0/coursenotes")
    		.content(requestBody))
    		.andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        json = new JSONObject(response);
        int id2 = json.getInt("courseNotesId");
        
        // add extra reference to course notes
        requestBody = "{ \"courseNotesId\": " + id2 + ", \"locations\":[{\"pageNumber\": 10, \"x1\": 2, \"y1\": 3, \"x2\": 4, \"y2\": 5}] }";
        
        mock.perform(post("/course/" + courseId + "/lecture/0/comment/" + commentid + "/coursenotesref")
        	.content(requestBody))
        	.andExpect(status().isOk());
        
        // get video references
        mock.perform(get("/course/" + courseId + "/lecture/0/comment/" + commentid + "/videoref"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].refId").exists())
                .andExpect(jsonPath("$[0].videoId").value(id))
                .andExpect(jsonPath("$[0].timestamp").value(12));
        
        //get course notes references
        mock.perform(get("/course/" + courseId + "/lecture/0/comment/" + commentid + "/coursenotesref"))
                .andExpect(jsonPath("$[0].courseNotesId").value(id2))
                .andExpect(jsonPath("$[0].refId").exists())
                .andExpect(jsonPath("$[0].locations.[0].pageNumber").value(10))
                .andExpect(jsonPath("$[0].locations.[0].x1").value(2))
                .andExpect(jsonPath("$[0].locations.[0].x2").value(4))
                .andExpect(jsonPath("$[0].locations.[0].y1").value(3))
                .andExpect(jsonPath("$[0].locations.[0].y2").value(5));
        
        //delete all
        mock.perform(delete("/course/" + courseId + "/lecture/0/video/" + id + "/comment/" + commentid))
        	.andExpect(status().isOk());
        
        mock.perform(delete("/course/" + courseId + "/lecture/0/video/"+id))
    		.andExpect(status().isOk());
        
        mock.perform(delete("/course/" + courseId + "/lecture/0/coursenotes/"+id2))
    		.andExpect(status().isOk());
    }
    
    @Test
    public void testPostVideoReference() throws Exception {
        //add video
        String requestBody = "{\"name\": \"title\",\"url\": \"url\",\"duration\": 999}";
    	
    	String response = mock.perform(post("/course/" + courseId + "/lecture/0/video")
    		.content(requestBody))
    		.andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        JSONObject json = new JSONObject(response);
        int id = json.getInt("videoId");
        
        //add comment
    	String commentrequestBody = 
    		"{\"username\":\"user\",\"question\":\"false\",\"body\":\"body\","
    		+ "\"selfVideoReferences\":[{\"videoId\":" + id + ","
    		+ "\"timestamp\":12}]}";
    	
    	String commentresponse = mock.perform(post("/course/" + courseId + "/lecture/0/video/" + id + "/comment")
    		.content(commentrequestBody))
    		.andExpect(status().isOk())
                .andExpect(jsonPath("$.selfVideoReferences.[0].refId").exists())
                .andReturn().getResponse().getContentAsString();
        
        JSONObject commentjson = new JSONObject(commentresponse);
        int commentid = commentjson.getInt("commentId");
        
        //add second video
        requestBody = "{\"name\": \"title2\",\"url\": \"url2\",\"duration\": 998}";
    	
    	response = mock.perform(post("/course/" + courseId + "/lecture/0/video")
    		.content(requestBody))
    		.andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        json = new JSONObject(response);
        int id2 = json.getInt("videoId");
        
        //add extra reference to second video
        requestBody = "{\"videoId\": " + id2 + ",\"timestamp\": 40}";
        
        response = mock.perform(post("/course/" + courseId + "/lecture/0/comment/" + commentid + "/videoref")
        	.content(requestBody))
        	.andExpect(status().isOk())
                .andExpect(jsonPath("$.refId").exists())
                .andReturn().getResponse().getContentAsString();
                
        json = new JSONObject(response);
        int refId = json.getInt("refId");
                
        // delete all
        mock.perform(delete("/course/" + courseId + "/lecture/0/comment/" + commentid + "/videoref/" + refId))
        	.andExpect(status().isOk());
        mock.perform(delete("/course/" + courseId + "/lecture/0/video/" + id + "/comment/" + commentid))
        	.andExpect(status().isOk());
        
        mock.perform(delete("/course/" + courseId + "/lecture/0/video/"+id))
    		.andExpect(status().isOk());
        
        mock.perform(delete("/course/" + courseId + "/lecture/0/video/"+id2))
    		.andExpect(status().isOk());
    }
    
    @Test
    public void testPostCourseNotesReference() throws Exception {
        //add course notes
        String requestBody = "{\"name\": \"title\", \"url\": \"url\"}";
    	
    	String response = mock.perform(post("/course/" + courseId + "/lecture/0/coursenotes")
    		.content(requestBody))
    		.andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        JSONObject json = new JSONObject(response);
        int id = json.getInt("courseNotesId");
        
        // add comment
    	requestBody = 
    		"{\"username\": \"user\", \"body\": "
    		+ "\"body\", \"selfCourseNotesReferences\": [{\"locations\" :[{ "
    				+ "\"pageNumber\": 20, \"x1\": 1, \"y1\": 2, \"x2\": 3, \"y2\": 4 }]}]"
    		+ ", \"question\": false }";
    	
    	String commentresponse = mock.perform(post("/course/" + courseId + "/lecture/0/coursenotes/" + id + "/comment")
    		.content(requestBody))
    		.andExpect(status().isOk())
                .andExpect(jsonPath("$.selfCourseNotesReferences.[0].refId").exists())
                .andReturn().getResponse().getContentAsString();
        
        JSONObject commentjson = new JSONObject(commentresponse);
        int commentid = commentjson.getInt("commentId");
        
        // add second course notes
        requestBody = "{\"name\": \"title\", \"url\": \"url\"}";
    	
    	response = mock.perform(post("/course/" + courseId + "/lecture/0/coursenotes")
    		.content(requestBody))
    		.andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        json = new JSONObject(response);
        int id2 = json.getInt("courseNotesId");
        
        // add extra reference to second course notes
        requestBody = "{ \"courseNotesId\": " + id2 + ", \"locations\":[{\"pageNumber\": 10, \"x1\": 2, \"y1\": 3, \"x2\": 4, \"y2\": 5}] }";
        
        response = mock.perform(post("/course/" + courseId + "/lecture/0/comment/" + commentid + "/coursenotesref")
        	.content(requestBody))
        	.andExpect(status().isOk())
                .andExpect(jsonPath("$..refId").exists())
                .andReturn().getResponse().getContentAsString();
        
        json = new JSONObject(response);
        int refId = json.getInt("refId");
        
        // delete all
        mock.perform(delete("/course/" + courseId + "/lecture/0/comment/" + commentid + "/coursenotesref/" + refId))
		.andExpect(status().isOk());
        mock.perform(delete("/course/" + courseId + "/lecture/0/coursenotes/" + id + "/comment/" + commentid))
    		.andExpect(status().isOk());
        mock.perform(delete("/course/" + courseId + "/lecture/0/coursenotes/" + id))
    		.andExpect(status().isOk());
        mock.perform(delete("/course/" + courseId + "/lecture/0/coursenotes/" + id2))
    		.andExpect(status().isOk());
    }
    
    @Test
    public void testPatchVideoReference() throws Exception {
        //add video
        String requestBody = "{\"name\": \"title\",\"url\": \"url\",\"duration\": 999}";
    	
    	String response = mock.perform(post("/course/" + courseId + "/lecture/0/video")
    		.content(requestBody))
    		.andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        JSONObject json = new JSONObject(response);
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
        
        int refId = commentjson.getJSONArray("selfVideoReferences").getJSONObject(0).getInt("refId");
        
        //update reference
        requestBody = "{\"videoId\": " + id + ",\"timestamp\": 15}";
        	
        mock.perform(patch("/course/" + courseId + "/lecture/0/comment/" + commentid + "/videoref/" + refId)
        	.content(requestBody))
        	.andExpect(status().isOk());
        
        // get references
        mock.perform(get("/course/" + courseId + "/lecture/0/comment/" + commentid + "/videoref"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].refId").value(refId))
                .andExpect(jsonPath("$[0].videoId").value(id))
                .andExpect(jsonPath("$[0].timestamp").value(15));
        
        //delete all
        mock.perform(delete("/course/" + courseId + "/lecture/0/video/" + id + "/comment/" + commentid))
        	.andExpect(status().isOk());
        
        mock.perform(delete("/course/" + courseId + "/lecture/0/video/"+id))
    		.andExpect(status().isOk());
    }
    
    @Test
    public void testPatchCourseNotesReference() throws Exception {
        //add course notes
        String requestBody = "{\"name\": \"title\", \"url\": \"url\"}";
    	
    	String response = mock.perform(post("/course/" + courseId + "/lecture/0/coursenotes")
    		.content(requestBody))
    		.andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        JSONObject json = new JSONObject(response);
        int id = json.getInt("courseNotesId");
        
        // add comment
    	requestBody = 
    		"{\"username\": \"user\", \"body\": "
    		+ "\"body\", \"selfCourseNotesReferences\": [{\"locations\" :[{ "
    				+ "\"pageNumber\": 20, \"x1\": 1, \"y1\": 2, \"x2\": 3, \"y2\": 4 }]}]"
    		+ ", \"question\": false }";
    	
    	String commentresponse = mock.perform(post("/course/" + courseId + "/lecture/0/coursenotes/" + id + "/comment")
    		.content(requestBody))
    		.andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        
        JSONObject commentjson = new JSONObject(commentresponse);
        int commentid = commentjson.getInt("commentId");
        
        int refId = commentjson.getJSONArray("selfCourseNotesReferences").getJSONObject(0).getInt("refId");
        
        // update reference
        requestBody = "{ \"courseNotesId\":" + id + ", \"locations\":[{\"pageNumber\": 21, \"x1\": 2, \"y1\": 3, \"x2\": 4, \"y2\": 5}] }";
        	
    	mock.perform(patch("/course/" + courseId + "/lecture/0/comment/" + commentid + "/coursenotesref/" + refId)
        	.content(requestBody))
        	.andExpect(status().isOk());
        
        //get references
        mock.perform(get("/course/" + courseId + "/lecture/0/comment/" + commentid + "/coursenotesref"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].courseNotesId").value(id))
                .andExpect(jsonPath("$[0].refId").value(refId))
                .andExpect(jsonPath("$[0].locations.[0].pageNumber").value(21))
                .andExpect(jsonPath("$[0].locations.[0].x1").value(2))
                .andExpect(jsonPath("$[0].locations.[0].x2").value(4))
                .andExpect(jsonPath("$[0].locations.[0].y1").value(3))
                .andExpect(jsonPath("$[0].locations.[0].y2").value(5));
        
        // delete all
        mock.perform(delete("/course/" + courseId + "/lecture/0/coursenotes/" + id + "/comment/" + commentid))
    		.andExpect(status().isOk());
        mock.perform(delete("/course/" + courseId + "/lecture/0/coursenotes/" + id))
    		.andExpect(status().isOk());
    }
}
