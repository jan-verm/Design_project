/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package endpoint;

import config.ITConfig;
import org.json.JSONObject;
import org.junit.After;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import org.springframework.web.context.WebApplicationContext;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;


/**
 *
 * @author Juta
 */


@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = { ITConfig.class })
@IntegrationTest
@WebAppConfiguration
@TestPropertySource(locations="classpath:test.properties")
public class ReplyEndpointIT {
    
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

    /**
     * Test of postCommentReply method, of class ReplyEndpoint.
     */
    @Test
    public void testPostCommentReply() throws Exception {
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
        
        // add reply
        requestBody = 
    		"{\"user\":\"user\",\"body\":\"body\"}";
        
        String replyresponse = mock.perform(post("/course/" + courseId + "/lecture/0/comment/" + commentid + "/children")
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.replyId").exists())
                .andReturn().getResponse().getContentAsString();
        
        JSONObject replyjson = new JSONObject(replyresponse);
        int replyid = replyjson.getInt("replyId");
        
        //get comment
        mock.perform(get("/course/" + courseId + "/lecture/0/video/" + id + "/comment/" + commentid))
	        .andExpect(status().isOk())
	        .andExpect(jsonPath("$.commentId").value(commentid))
	        .andExpect(jsonPath("$.username").value("admin"))
	        .andExpect(jsonPath("$.body").value("body"))
	        .andExpect(jsonPath("$.creationTime").exists())
	        .andExpect(jsonPath("$.selfVideoReferences.[0].videoId").value(id))
	        .andExpect(jsonPath("$.selfVideoReferences.[0].timestamp").value(12))
                .andExpect(jsonPath("$.replies.[0].replyId").value(replyid))
                .andExpect(jsonPath("$.replies.[0].creationTime").exists())
                .andExpect(jsonPath("$.replies.[0].user").value("admin"))
                .andExpect(jsonPath("$.replies.[0].body").value("body"));
        
        //delete all
        mock.perform(delete("/course/" + courseId + "/lecture/0/video/" + id + "/comment/" + commentid))
        	.andExpect(status().isOk());
        
        mock.perform(delete("/course/" + courseId + "/lecture/0/video/"+id))
    		.andExpect(status().isOk());
    }
    
    @Test
    public void testPostCommentReplies() throws Exception {
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
        
        // add reply
        requestBody = 
    		"{\"user\":\"user\",\"body\":\"body\"}";
        
        String replyresponse = mock.perform(post("/course/" + courseId + "/lecture/0/comment/" + commentid + "/children")
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.replyId").exists())
                .andReturn().getResponse().getContentAsString();
        
        JSONObject replyjson = new JSONObject(replyresponse);
        int replyid = replyjson.getInt("replyId");
        
        // add second reply
        requestBody = 
    		"{\"body\":\"body2\"}";
        
        replyresponse = mock.perform(post("/course/" + courseId + "/lecture/0/comment/" + commentid + "/children")
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.replyId").exists())
                .andReturn().getResponse().getContentAsString();
        
        replyjson = new JSONObject(replyresponse);
        int replyid2 = replyjson.getInt("replyId");
        
        //get comment
        mock.perform(get("/course/" + courseId + "/lecture/0/video/" + id + "/comment/" + commentid))
	        .andExpect(status().isOk())
	        .andExpect(jsonPath("$.commentId").value(commentid))
	        .andExpect(jsonPath("$.username").value("admin"))
	        .andExpect(jsonPath("$.body").value("body"))
	        .andExpect(jsonPath("$.creationTime").exists())
	        .andExpect(jsonPath("$.selfVideoReferences.[0].videoId").value(id))
	        .andExpect(jsonPath("$.selfVideoReferences.[0].timestamp").value(12))
                .andExpect(jsonPath("$.replies.[0].creationTime").exists())
                .andExpect(jsonPath("$.replies.[0].user").value("admin"))
                .andExpect(jsonPath("$.replies.[0].body").value("body"))
                .andExpect(jsonPath("$.replies.[1].replyId").value(replyid2))
                .andExpect(jsonPath("$.replies.[1].creationTime").exists())
                .andExpect(jsonPath("$.replies.[1].user").value("admin"))
                .andExpect(jsonPath("$.replies.[1].body").value("body2"));
        
        //delete all
        mock.perform(delete("/course/" + courseId + "/lecture/0/video/" + id + "/comment/" + commentid))
        	.andExpect(status().isOk());
        
        mock.perform(delete("/course/" + courseId + "/lecture/0/video/"+id))
    		.andExpect(status().isOk());
    }
    
    @Test
    public void testPostReplytoCommentNotFoundException() throws Exception {
        String requestBody = 
            "{\"user\":\"user\",\"body\":\"body\"}";
        
        mock.perform(post("/course/1/lecture/0/comment/0/children")
                .content(requestBody))
                .andExpect(status().is4xxClientError());
    }
    
    @Test
    public void testPostReplytoCommentBadRequest() throws Exception {
        String requestBody = 
            "{\"user\":\"user\"\"body\":\"body\"}";
        
        mock.perform(post("/course/1/lecture/0/comment/1/children")
                .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    /**
     * Test of postReplyReply method, of class ReplyEndpoint.
     */
    @Test
    public void testPostReplyReply() throws Exception {
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
        
        // add reply
        requestBody = 
    		"{\"user\":\"user\",\"body\":\"body\"}";
        
        String replyresponse = mock.perform(post("/course/" + courseId + "/lecture/0/comment/" + commentid + "/children")
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.replyId").exists())
                .andReturn().getResponse().getContentAsString();
        
        JSONObject replyjson = new JSONObject(replyresponse);
        int replyid = replyjson.getInt("replyId");
        
        // add reply to reply
        requestBody = 
    		"{\"body\":\"body2\"}";
        
        replyresponse = mock.perform(post("/course/" + courseId + "/lecture/0/reply/" + replyid + "/children")
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.replyId").exists())
                .andReturn().getResponse().getContentAsString();
        
        replyjson = new JSONObject(replyresponse);
        int replyid2 = replyjson.getInt("replyId");
        
        //get comment
        mock.perform(get("/course/" + courseId + "/lecture/0/video/" + id + "/comment/" + commentid))
	        .andExpect(status().isOk())
	        .andExpect(jsonPath("$.commentId").value(commentid))
	        .andExpect(jsonPath("$.username").value("admin"))
	        .andExpect(jsonPath("$.body").value("body"))
	        .andExpect(jsonPath("$.creationTime").exists())
	        .andExpect(jsonPath("$.selfVideoReferences.[0].videoId").value(id))
	        .andExpect(jsonPath("$.selfVideoReferences.[0].timestamp").value(12))
                .andExpect(jsonPath("$.replies.[0].replyId").value(replyid))
                .andExpect(jsonPath("$.replies.[0].creationTime").exists())
                .andExpect(jsonPath("$.replies.[0].user").value("admin"))
                .andExpect(jsonPath("$.replies.[0].body").value("body"))
                .andExpect(jsonPath("$.replies.[0].replies.[0].replyId").value(replyid2))
                .andExpect(jsonPath("$.replies.[0].replies.[0].creationTime").exists())
                .andExpect(jsonPath("$.replies.[0].replies.[0].user").value("admin"))
                .andExpect(jsonPath("$.replies.[0].replies.[0].body").value("body2"));
        
        //delete all
        mock.perform(delete("/course/" + courseId + "/lecture/0/video/" + id + "/comment/" + commentid))
        	.andExpect(status().isOk());
        
        mock.perform(delete("/course/" + courseId + "/lecture/0/video/"+id))
    		.andExpect(status().isOk());
    }
    
    @Test
    public void testPostReplytoReplyNotFoundException() throws Exception {
        String requestBody = 
            "{\"user\":\"user\",\"body\":\"body\"}";
        
        mock.perform(post("/course/1/lecture/0/reply/0/children")
                .content(requestBody))
                .andExpect(status().is4xxClientError());
    }
    
    @Test
    public void testPostReplytoReplyBadRequest() throws Exception {
        String requestBody = 
            "{\"user\":\"user\"\"body\":\"body\"}";
        
        mock.perform(post("/course/1/lecture/0/reply/1/children")
                .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    /**
     * Test of patchReply method, of class ReplyEndpoint.
     */
    @Test
    public void testPatchReply() throws Exception {
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
        
        // add reply
        requestBody = 
    		"{\"body\":\"body\"}";
        
        String replyresponse = mock.perform(post("/course/" + courseId + "/lecture/0/comment/" + commentid + "/children")
                .content(requestBody))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        
        JSONObject replyjson = new JSONObject(replyresponse);
        int replyid = replyjson.getInt("replyId");
        
        //update reply
        requestBody = 
            "{\"body\":\"newbody\"}";
    	
    	response = mock.perform(patch("/course/" + courseId + "/lecture/0/reply/" + replyid)
    		.content(requestBody))
    		//.andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        System.out.println(response);
        
        //get comment
        mock.perform(get("/course/" + courseId + "/lecture/0/video/" + id + "/comment/" + commentid))
	        .andExpect(status().isOk())
	        .andExpect(jsonPath("$.commentId").value(commentid))
	        .andExpect(jsonPath("$.username").value("admin"))
	        .andExpect(jsonPath("$.body").value("body"))
	        .andExpect(jsonPath("$.creationTime").exists())
	        .andExpect(jsonPath("$.selfVideoReferences.[0].videoId").value(id))
	        .andExpect(jsonPath("$.selfVideoReferences.[0].timestamp").value(12))
                .andExpect(jsonPath("$.replies.[0].replyId").value(replyid))
                .andExpect(jsonPath("$.replies.[0].creationTime").exists())
                .andExpect(jsonPath("$.replies.[0].user").value("admin"))
                .andExpect(jsonPath("$.replies.[0].body").value("newbody"));
        
        //delete all
        mock.perform(delete("/course/" + courseId + "/lecture/0/video/" + id + "/comment/" + commentid))
        	.andExpect(status().isOk());
        
        mock.perform(delete("/course/" + courseId + "/lecture/0/video/"+id))
    		.andExpect(status().isOk());
    }
    
    @Test
    public void testPatchReplyNotFoundException() throws Exception {
        String requestBody = 
            "{\"user\":\"user\",\"body\":\"new_body\"}";
        
        mock.perform(patch("/course/1/lecture/0/reply/0")
                .content(requestBody))
                .andExpect(status().is4xxClientError());
    }
    
    @Test
    public void testPatchReplyBadRequest() throws Exception {
        String requestBody = 
            "{\"user\":\"user\"\"body\":\"new_body\"}";
        
        mock.perform(patch("/course/1/lecture/0/reply/1")
                .content(requestBody))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    public void testDeleteReply() throws Exception {
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
        
        // add reply
        requestBody = 
    		"{\"user\":\"user\",\"body\":\"body\"}";
        
        String replyresponse = mock.perform(post("/course/" + courseId + "/lecture/0/comment/" + commentid + "/children")
                .content(requestBody))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        
        JSONObject replyjson = new JSONObject(replyresponse);
        int replyid = replyjson.getInt("replyId");
         
      //delete all
        mock.perform(delete("/course/" + courseId + "/lecture/0/reply/" + replyid))
        .andExpect(status().isOk());
        
        mock.perform(delete("/course/" + courseId + "/lecture/0/video/" + id + "/comment/" + commentid))
        	.andExpect(status().isOk());
        
        mock.perform(delete("/course/" + courseId + "/lecture/0/video/"+id))
    		.andExpect(status().isOk());
    }
    
    @Test
    public void testDeleteReplyNotFoundException() throws Exception {
        mock.perform(delete("/course/1/lecture/0/reply/0"))
                .andExpect(status().is4xxClientError());
    }
}
