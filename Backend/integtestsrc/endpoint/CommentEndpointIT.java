/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
@SpringApplicationConfiguration(classes = { ITConfig.class })
@IntegrationTest
@WebAppConfiguration
@TestPropertySource(locations="classpath:test.properties")
public class CommentEndpointIT {
    
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
    public void testGetVideoComments() throws Exception {
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
        
        // get comment
        mock.perform(get("/course/" + courseId + "/lecture/0/video/" + id + "/comment"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].commentId").value(commentid))
                .andExpect(jsonPath("$[0].username").value("admin"))
                .andExpect(jsonPath("$[0].body").value("body"))
                .andExpect(jsonPath("$[0].creationTime").exists())
                .andExpect(jsonPath("$[0].selfVideoReferences.[0].videoId").value(id))
                .andExpect(jsonPath("$[0].selfVideoReferences.[0]timestamp").value(12));
        
        //delete all
        mock.perform(delete("/course/" + courseId + "/lecture/0/video/" + id + "/comment/" + commentid))
        	.andExpect(status().isOk());
        
        mock.perform(delete("/course/" + courseId + "/lecture/0/video/"+id))
    		.andExpect(status().isOk());
    }
    
    @Test
    public void testGetVideoCommentsNotFoundException() throws Exception {
        mock.perform(get("/course/" + courseId + "/lecture/0/video/0/comment"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void testGetCourseNotesComments() throws Exception {
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
        
        //get comment
        mock.perform(get("/course/" + courseId + "/lecture/0/coursenotes/" + id + "/comment"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].commentId").value(commentid))
                .andExpect(jsonPath("$[0].username").value("admin"))
                .andExpect(jsonPath("$[0].creationTime").exists())
                .andExpect(jsonPath("$[0].body").value("body"))
                .andExpect(jsonPath("$[0].question").value(false))
                
                .andExpect(jsonPath("$[0].selfCourseNotesReferences.[0].courseNotesId").exists())
                .andExpect(jsonPath("$[0].selfCourseNotesReferences.[0].locations.[0].x1").value(1))
                .andExpect(jsonPath("$[0].selfCourseNotesReferences.[0].locations.[0].x2").value(3))
                .andExpect(jsonPath("$[0].selfCourseNotesReferences.[0].locations.[0].y1").value(2))
                .andExpect(jsonPath("$[0].selfCourseNotesReferences.[0].locations.[0].y2").value(4))
                .andExpect(jsonPath("$[0].selfCourseNotesReferences.[0].locations.[0].pageNumber").value(20));
                
        // delete all
        mock.perform(delete("/course/" + courseId + "/lecture/0/coursenotes/" + id + "/comment/" + commentid))
    		.andExpect(status().isOk());
        mock.perform(delete("/course/" + courseId + "/lecture/0/coursenotes/" + id))
    		.andExpect(status().isOk());
    }
    
    @Test
    public void testGetCourseNotesCommentsNotFoundException() throws Exception {
        mock.perform(get("/course/" + courseId + "/lecture/0/coursenotes/0/comment"))
                .andExpect(status().is4xxClientError());
    }
    
    @Test
    public void testGetVideoComment() throws Exception {
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
        
        //get comment
        mock.perform(get("/course/" + courseId + "/lecture/0/video/" + id + "/comment/" + commentid))
	        .andExpect(status().isOk())
	        .andExpect(jsonPath("$.commentId").value(commentid))
	        .andExpect(jsonPath("$.username").value("admin"))
	        .andExpect(jsonPath("$.body").value("body"))
	        .andExpect(jsonPath("$.creationTime").exists())
	        .andExpect(jsonPath("$.selfVideoReferences.[0].videoId").value(id))
	        .andExpect(jsonPath("$.selfVideoReferences.[0].timestamp").value(12));
        
        //delete all
        mock.perform(delete("/course/" + courseId + "/lecture/0/video/" + id + "/comment/" + commentid))
        	.andExpect(status().isOk());
        
        mock.perform(delete("/course/" + courseId + "/lecture/0/video/"+id))
    		.andExpect(status().isOk());
    }
    
    @Test
    public void testGetVideoCommentNotFoundException() throws Exception {
        mock.perform(get("/course/" + courseId + "/lecture/0/video/0/comment/0"))
	        .andExpect(status().isBadRequest());
    }

    @Test
    public void testGetCourseNotesComment() throws Exception {
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
        
        //get comment
        mock.perform(get("/course/" + courseId + "/lecture/0/coursenotes/" + id + "/comment/" + commentid))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.commentId").value(commentid))
                .andExpect(jsonPath("$.username").value("admin"))
                .andExpect(jsonPath("$.creationTime").exists())
                .andExpect(jsonPath("$.body").value("body"))
                .andExpect(jsonPath("$.question").value(false))
                
                .andExpect(jsonPath("$.selfCourseNotesReferences.[0].courseNotesId").exists())
                .andExpect(jsonPath("$.selfCourseNotesReferences.[0].locations.[0].x1").value(1))
                .andExpect(jsonPath("$.selfCourseNotesReferences.[0].locations.[0].x2").value(3))
                .andExpect(jsonPath("$.selfCourseNotesReferences.[0].locations.[0].y1").value(2))
                .andExpect(jsonPath("$.selfCourseNotesReferences.[0].locations.[0].y2").value(4))
                .andExpect(jsonPath("$.selfCourseNotesReferences.[0].locations.[0].pageNumber").value(20));
        
        // delete all
        mock.perform(delete("/course/" + courseId + "/lecture/0/coursenotes/" + id + "/comment/" + commentid))
    		.andExpect(status().isOk());
        mock.perform(delete("/course/" + courseId + "/lecture/0/coursenotes/" + id))
    		.andExpect(status().isOk());
    }
    
    @Test
    public void testGetCourseNotesCommentNotFoundException() throws Exception {
        mock.perform(get("/course/" + courseId + "/lecture/0/coursenotes/0/comment/0"))
	        .andExpect(status().isBadRequest());
    }

    @Test
    public void testPostVideoComment() throws Exception {
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
                .andExpect(jsonPath("$.commentId").exists())
                .andReturn().getResponse().getContentAsString();
        
        JSONObject commentjson = new JSONObject(commentresponse);
        int commentid = commentjson.getInt("commentId");
        
        //delete all
        mock.perform(delete("/course/" + courseId + "/lecture/0/video/" + id + "/comment/" + commentid))
        	.andExpect(status().isOk());
        
        mock.perform(delete("/course/" + courseId + "/lecture/0/video/"+id))
    		.andExpect(status().isOk());
        
    }
    
    @Test
    public void testPostVideoCommentBadRequest() throws Exception {
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
    		"{\"username\":\"user\"\"question\":\"false\",\"body\":\"body\","
    		+ "\"selfVideoReferences\":[{\"videoId\":" + id + ","
    		+ "\"timestamp\":12}]}";
    	
    	mock.perform(post("/course/" + courseId + "/lecture/0/video/" + id + "/comment")
    		.content(commentrequestBody))
    		.andExpect(status().isBadRequest());
        
        mock.perform(delete("/course/" + courseId + "/lecture/0/video/"+id))
    		.andExpect(status().isOk());
    }
    
    @Test
    public void testPostVideoCommentNotFoundException() throws Exception {
    	String commentrequestBody = 
    		"{\"username\":\"user\",\"question\":\"false\",\"body\":\"body\","
    		+ "\"selfVideoReferences\":[{\"videoId\":0,"
    		+ "\"timestamp\":12}]}";
    	
    	mock.perform(post("/course/" + courseId + "/lecture/0/video/0/comment")
    		.content(commentrequestBody))
    		.andExpect(status().isBadRequest());
    }
    
    @Test
    public void testPostCourseNotesComment() throws Exception {
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
                .andExpect(jsonPath("$.commentId").exists())
                .andReturn().getResponse().getContentAsString();
        
        JSONObject commentjson = new JSONObject(commentresponse);
        int commentid = commentjson.getInt("commentId");
        
        // delete all
        mock.perform(delete("/course/" + courseId + "/lecture/0/coursenotes/" + id + "/comment/" + commentid))
    		.andExpect(status().isOk());
        mock.perform(delete("/course/" + courseId + "/lecture/0/coursenotes/" + id))
    		.andExpect(status().isOk());
    }
    
    @Test
    public void testPostCourseNotesCommentBadRequest() throws Exception {
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
    		"{\"username\": \"user\" \"body\": "
    		+ "\"body\", \"selfCourseNotesReferences\": [{\"locations\" :[{ "
    				+ "\"pageNumber\": 20, \"x1\": 1, \"y1\": 2, \"x2\": 3, \"y2\": 4 }]}]"
    		+ ", \"question\": false }";
    	
    	mock.perform(post("/course/" + courseId + "/lecture/0/coursenotes/" + id + "/comment")
    		.content(requestBody))
    		.andExpect(status().isBadRequest());

        mock.perform(delete("/course/" + courseId + "/lecture/0/coursenotes/" + id))
    		.andExpect(status().isOk());
    }
    
    @Test
    public void testPostCourseNotesCommentNotFoundException() throws Exception {
    	String requestBody = 
    		"{\"username\": \"user\", \"body\": "
    		+ "\"body\", \"selfCourseNotesReferences\": [{\"locations\" :[{ "
    				+ "\"pageNumber\": 20, \"x1\": 1, \"y1\": 2, \"x2\": 3, \"y2\": 4 }]}]"
    		+ ", \"question\": false }";
    	
    	mock.perform(post("/course/" + courseId + "/lecture/0/coursenotes/0/comment")
    		.content(requestBody))
    		.andExpect(status().isBadRequest());
    }
    
    @Test
    public void testPatchVideoComment() throws Exception {
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
        
        //update comment
        requestBody = 
    		"{\"question\":\"true\",\"body\":\"newbody\"}";
    	
    	mock.perform(patch("/course/" + courseId + "/lecture/0/video/" + id + "/comment/" + commentid)
    		.content(requestBody))
    		.andExpect(status().isOk());
        
        //get comment
        mock.perform(get("/course/" + courseId + "/lecture/0/video/" + id + "/comment/" + commentid))
	        .andExpect(status().isOk())
	        .andExpect(jsonPath("$.commentId").value(commentid))
	        .andExpect(jsonPath("$.username").value("admin"))
	        .andExpect(jsonPath("$.body").value("newbody"))
	        .andExpect(jsonPath("$.creationTime").exists())
	        .andExpect(jsonPath("$.selfVideoReferences.[0].videoId").value(id))
	        .andExpect(jsonPath("$.selfVideoReferences.[0].timestamp").value(12));
        
        
        //delete all
        mock.perform(delete("/course/" + courseId + "/lecture/0/video/" + id + "/comment/" + commentid))
        	.andExpect(status().isOk());
        
        mock.perform(delete("/course/" + courseId + "/lecture/0/video/"+id))
    		.andExpect(status().isOk());
    }
    
    @Test
    public void testPatchVideoCommentBadRequest() throws Exception {
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
        
        //update comment
        requestBody = 
    		"{\"username\":\"newuser\"\"question\":\"true\",\"body\":\"newbody\"}";
    	
    	mock.perform(patch("/course/" + courseId + "/lecture/0/video/" + id + "/comment/" + commentid)
    		.content(requestBody))
    		.andExpect(status().isBadRequest());

        //delete all
        mock.perform(delete("/course/" + courseId + "/lecture/0/video/" + id + "/comment/" + commentid))
        	.andExpect(status().isOk());
        
        mock.perform(delete("/course/" + courseId + "/lecture/0/video/"+id))
    		.andExpect(status().isOk());
    }
    
    @Test
    public void testPatchVideoCommentNotFoundException() throws Exception {
        String requestBody = 
    		"{\"username\":\"newuser\",\"question\":\"true\",\"body\":\"newbody\"}";
    	
    	mock.perform(patch("/course/" + courseId + "/lecture/0/video/0/comment/0")
    		.content(requestBody))
    		.andExpect(status().is4xxClientError());
    }
    
    @Test
    public void testPatchCourseNotesComment() throws Exception {
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
        
        //update comment
        requestBody = 
        		"{\"question\":\"true\",\"body\":\"newbody\"}";
    	
    	mock.perform(patch("/course/" + courseId + "/lecture/0/coursenotes/" + id + "/comment/" + commentid)
    		.content(requestBody))
    		.andExpect(status().isOk());
        
        //get comment
        mock.perform(get("/course/" + courseId + "/lecture/0/coursenotes/" + id + "/comment/" + commentid))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.commentId").value(commentid))
                .andExpect(jsonPath("$.username").value("admin"))
                .andExpect(jsonPath("$.body").value("newbody"))
                .andExpect(jsonPath("$.question").value(true))
                .andExpect(jsonPath("$.creationTime").exists())
                .andExpect(jsonPath("$.selfCourseNotesReferences.[0].courseNotesId").exists())
                .andExpect(jsonPath("$.selfCourseNotesReferences.[0].locations.[0].x1").value(1))
                .andExpect(jsonPath("$.selfCourseNotesReferences.[0].locations.[0].x2").value(3))
                .andExpect(jsonPath("$.selfCourseNotesReferences.[0].locations.[0].y1").value(2))
                .andExpect(jsonPath("$.selfCourseNotesReferences.[0].locations.[0].y2").value(4))
                .andExpect(jsonPath("$.selfCourseNotesReferences.[0].locations.[0].pageNumber").value(20));
        
        // delete all
        mock.perform(delete("/course/" + courseId + "/lecture/0/coursenotes/" + id + "/comment/" + commentid))
    		.andExpect(status().isOk());
        mock.perform(delete("/course/" + courseId + "/lecture/0/coursenotes/" + id))
    		.andExpect(status().isOk());   	
    }
    
    @Test
    public void testPatchCourseNotesCommentBadRequest() throws Exception {
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
        
        //update comment
        requestBody = 
        		"{\"username\":\"newuser\"\"question\":\"true\",\"body\":\"newbody\"}";
    	
    	mock.perform(patch("/course/" + courseId + "/lecture/0/coursenotes/" + id + "/comment/" + commentid)
    		.content(requestBody))
    		.andExpect(status().isBadRequest());
        
        // delete all
        mock.perform(delete("/course/" + courseId + "/lecture/0/coursenotes/" + id + "/comment/" + commentid))
    		.andExpect(status().isOk());
        mock.perform(delete("/course/" + courseId + "/lecture/0/coursenotes/" + id))
    		.andExpect(status().isOk());   	
    }
    
    @Test
    public void testPatchCourseNotesCommentNotFoundException() throws Exception {
        String requestBody = 
        		"{\"username\":\"newuser\",\"question\":\"true\",\"body\":\"newbody\"}";
    	
    	mock.perform(patch("/course/" + courseId + "/lecture/0/coursenotes/0/comment/0")
    		.content(requestBody))
    		.andExpect(status().is4xxClientError());
    }
    
    @Test
    public void testDeleteVideoCommentNotFoundException() throws Exception {
        mock.perform(delete("/course/" + courseId + "/lecture/0/video/0/comment/0"))
    		.andExpect(status().is4xxClientError());
    }
    
    @Test
    public void testDeleteCourseNotesCommentNotFoundException() throws Exception {
        mock.perform(delete("/course/" + courseId + "/lecture/0/coursenotes/0/comment/0"))
    		.andExpect(status().is4xxClientError());
    }
}