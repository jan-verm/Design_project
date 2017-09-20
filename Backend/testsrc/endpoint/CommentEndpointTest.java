package endpoint;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
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
public class CommentEndpointTest {
    
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
    public void testGetVideoComments() throws Exception {
        mock.perform(get("/course/1/lecture/0/video/1/comment"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].commentId").value(1))
                .andExpect(jsonPath("$[0].username").value("user"))
                .andExpect(jsonPath("$[0].body").value("body"))
                .andExpect(jsonPath("$[0].creationTime").value(1))
                .andExpect(jsonPath("$[0].selfVideoReferences[0].videoId").value(1))
                .andExpect(jsonPath("$[0].selfVideoReferences[0].timestamp").value(12))
                .andExpect(jsonPath("$[0].selfVideoReferences[0].refId").value(0));
    }
    
    @Test
    public void testGetVideoCommentsNotFoundException() throws Exception {
        mock.perform(get("/course/1/lecture/0/video/0/comment"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void testGetCourseNotesComments() throws Exception {
        mock.perform(get("/course/1/lecture/0/coursenotes/1/comment"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].commentId").value(1))
                .andExpect(jsonPath("$[0].username").value("user"))
                .andExpect(jsonPath("$[0].body").value("body"))
                .andExpect(jsonPath("$[0].question").value(false))
                .andExpect(jsonPath("$[0].creationTime").value(1))
                .andExpect(jsonPath("$[0].selfCourseNotesReferences[0].courseNotesId").value(123))
                .andExpect(jsonPath("$[0].selfCourseNotesReferences[0].locations[0].x1").value(1))
                .andExpect(jsonPath("$[0].selfCourseNotesReferences[0].locations[0].x2").value(2))
                .andExpect(jsonPath("$[0].selfCourseNotesReferences[0].locations[0].y1").value(3))
                .andExpect(jsonPath("$[0].selfCourseNotesReferences[0].locations[0].y2").value(4))
                .andExpect(jsonPath("$[0].selfCourseNotesReferences[0].locations[0].pageNumber").value(20));
    }
    
    @Test
    public void testGetCourseNotesCommentsNotFoundException() throws Exception {
        mock.perform(get("/course/1/lecture/0/coursenotes/0/comment"))
                .andExpect(status().is4xxClientError());
    }
    
    @Test
    public void testGetVideoComment() throws Exception {
    	mock.perform(get("/course/1/lecture/0/video/1/comment/1"))
	        .andExpect(status().isOk())
	        .andExpect(jsonPath("$.commentId").value(1))
	        .andExpect(jsonPath("$.username").value("user"))
	        .andExpect(jsonPath("$.body").value("body"))
	        .andExpect(jsonPath("$.creationTime").value(1))
	        .andExpect(jsonPath("$.selfVideoReferences[0].videoId").value(1))
	        .andExpect(jsonPath("$.selfVideoReferences[0].timestamp").value(12))
	        .andExpect(jsonPath("$.selfVideoReferences[0].refId").value(0));
    }
    
    @Test
    public void testGetVideoCommentNotFoundException() throws Exception {
    	mock.perform(get("/course/1/lecture/0/video/1/comment/0"))
	        .andExpect(status().is4xxClientError());
    }

    @Test
    public void testGetCourseNotesComment() throws Exception {
        mock.perform(get("/course/1/lecture/0/coursenotes/1/comment/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.commentId").value(1))
                .andExpect(jsonPath("$.username").value("user"))
                .andExpect(jsonPath("$.body").value("body"))
                .andExpect(jsonPath("$.question").value(false))
                .andExpect(jsonPath("$.creationTime").value(1));
    }
    
    @Test
    public void testGetCourseNotesCommentNotFoundException() throws Exception {
    	mock.perform(get("/course/1/lecture/0/coursenotes/1/comment/0"))
	        .andExpect(status().is4xxClientError());
    }
    
    @Test
    public void testDeleteVideoComment() throws Exception {
    	mock.perform(delete("/course/1/lecture/0/video/1/comment/1"))
        	.andExpect(status().isOk());
    }
    
    @Test
    public void testDeleteVideoCommentNotFoundException() throws Exception {
    	mock.perform(delete("/course/1/lecture/0/video/1/comment/0"))
        	.andExpect(status().is4xxClientError());
    }
    
    @Test
    public void testDeleteCourseNotesComment() throws Exception {
    	mock.perform(delete("/course/1/lecture/0/coursenotes/1/comment/1"))
        	.andExpect(status().isOk());
    }
    
    @Test
    public void testDeleteCourseNotesCommentNotFoundException() throws Exception {
    	mock.perform(delete("/course/1/lecture/0/coursenotes/1/comment/0"))
        	.andExpect(status().is4xxClientError());
    }
    
    @Test
    public void testPostVideoComment() throws Exception {
    	String requestBody = 
    		"{\"commentId\":1,\"username\":\"user\",\"question\":\"false\",\"title\":\"title\",\"body\":\"body\","
    		+ "\"creationTime\":\"99\",\"selfVideoReferences\":[{\"videoId\":1,"
    		+ "\"timestamp\":12}]}";
    	
    	mock.perform(post("/course/1/lecture/0/video/1/comment")
    		.content(requestBody))
    		.andExpect(status().isOk());
    }
    
    @Test
    public void testPostVideoCommentNotFoundException() throws Exception {
    	String requestBody = 
    		"{\"commentId\":1,\"username\":\"user\",\"question\":\"false\",\"title\":\"title\",\"body\":\"body\","
    		+ "\"creationTime\":\"99\",\"selfVideoReferences\":[{\"videoId\":1,"
    		+ "\"timestamp\":12}]}";
    	
    	mock.perform(post("/course/1/lecture/0/video/0/comment")
    		.content(requestBody))
    		.andExpect(status().is4xxClientError());
    }
    
    @Test
    public void testPostVideoCommentBadRequest() throws Exception {
    	String requestBody = 
    		"{\"commentId\":1,\"username\":\"user\"\"question\":\"false\",\"title\":\"title\",\"body\":\"body\","
    		+ "\"creationTime\":\"99\",\"selfVideoReferences\":[{\"videoId\":1,"
    		+ "\"timestamp\":12}]}";
    	
    	mock.perform(post("/course/1/lecture/0/video/1/comment")
    		.content(requestBody))
    		.andExpect(status().isBadRequest());
    }
    
    @Test
    public void testPostCourseNotesComment() throws Exception {
    	String requestBody = 
    		"{ \"commentId\": 1, \"username\": \"user\", \"title\": \"title\", \"body\": "
    		+ "\"body\", \"selfCourseNotesReferences\": ["
    				+ "{\"courseNotesId\": 123, \"locations\": [{\"pageNumber\": 20, \"x1\": 1, \"y1\": 2, \"x2\": 3, \"y2\": 4}] }"
    		+ "], \"question\": false }";
    	
    	mock.perform(post("/course/1/lecture/0/coursenotes/1/comment")
    		.content(requestBody))
    		.andExpect(status().isOk());
    }
    
    @Test
    public void testPostCourseNotesCommentNotFoundException() throws Exception {
    	String requestBody = 
    		"{ \"commentId\": 1, \"username\": \"user\", \"title\": \"title\", \"body\": "
    		+ "\"body\", \"selfCourseNotesReferences\": ["
    				+ "{\"courseNotesId\": 123, \"locations\": [{\"pageNumber\": 20, \"x1\": 1, \"y1\": 2, \"x2\": 3, \"y2\": 4}] }"
    		+ "], \"question\": false }";
    	
    	mock.perform(post("/course/1/lecture/0/coursenotes/0/comment")
    		.content(requestBody))
    		.andExpect(status().is4xxClientError());
    }
    
    @Test
    public void testPostCourseNotesCommentBadRequest() throws Exception {
    	String requestBody = 
    		"{ \"commentId\": 1, \"username\": \"user\" \"title\": \"title\", \"body\": "
    		+ "\"body\", \"selfCourseNotesReferences\": ["
    				+ "{\"courseNotesId\": 123, \"locations\": [{\"pageNumber\": 20, \"x1\": 1, \"y1\": 2, \"x2\": 3, \"y2\": 4}] }"
    		+ "], \"question\": false }";
    	
    	mock.perform(post("/course/1/lecture/0/coursenotes/1/comment")
    		.content(requestBody))
    		.andExpect(status().isBadRequest());
    }
    
    @Test
    public void testPatchVideoComment() throws Exception {
    	String requestBody = 
    		"{\"commentId\":1,\"username\":\"user\",\"question\":\"false\",\"title\":\"title\",\"body\":\"body\"}";
    	
    	mock.perform(patch("/course/1/lecture/0/video/1/comment/1")
    		.content(requestBody))
    		.andExpect(status().isOk());
    }
    
    @Test
    public void testPatchVideoCommentNotFoundException() throws Exception {
    	String requestBody = 
    		"{\"commentId\":1,\"username\":\"user\",\"question\":\"false\",\"title\":\"title\",\"body\":\"body\"}";
    	
    	mock.perform(patch("/course/1/lecture/0/video/1/comment/0")
    		.content(requestBody))
    		.andExpect(status().is4xxClientError());
    }
    
    @Test
    public void testPatchVideoCommentBadRequest() throws Exception {
    	String requestBody = 
    		"{\"commentId\":1,\"username\":\"user\"\"question\":\"false\",\"title\":\"title\",\"body\":\"body\"}";
    	
    	mock.perform(patch("/course/1/lecture/0/video/1/comment/1")
    		.content(requestBody))
    		.andExpect(status().isBadRequest());
    }
    
    @Test
    public void testPatchCourseNotesComment() throws Exception {
    	String requestBody = 
        		"{\"commentId\":1,\"username\":\"user\",\"question\":\"false\",\"title\":\"title\",\"body\":\"body_edit\"}";
    	
    	mock.perform(patch("/course/1/lecture/0/coursenotes/1/comment/1")
    		.content(requestBody))
    		.andExpect(status().isOk());
    }
    
    @Test
    public void testPatchCourseNotesCommentNotFoundException() throws Exception {
    	String requestBody = 
        		"{\"commentId\":1,\"username\":\"user\",\"question\":\"false\",\"title\":\"title\",\"body\":\"body_edit\"}";
    	
    	mock.perform(patch("/course/1/lecture/0/coursenotes/1/comment/0")
    		.content(requestBody))
    		.andExpect(status().is4xxClientError());
    }
    
    @Test
    public void testPatchCourseNotesCommentBadRequest() throws Exception {
    	String requestBody = 
        		"{\"commentId\":1,\"username\":\"user\"\"question\":\"false\",\"title\":\"title\",\"body\":\"body_edit\"}";
    	
    	mock.perform(patch("/course/1/lecture/0/coursenotes/1/comment/1")
    		.content(requestBody))
    		.andExpect(status().isBadRequest());
    }
    
    @Test
    public void testPostReplytoComment() throws Exception {
        String requestBody = 
            "{\"user\":\"user\",\"body\":\"body\"}";
        
        mock.perform(post("/course/1/lecture/0/comment/1/children")
                .content(requestBody))
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
    
    @Test
    public void testPostReplytoReply() throws Exception {
        String requestBody = 
            "{\"user\":\"user\",\"body\":\"body\"}";
        
        mock.perform(post("/course/1/lecture/0/reply/1/children")
                .content(requestBody))
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
    
    @Test
    public void testPatchReply() throws Exception {
        String requestBody = 
            "{\"user\":\"user\",\"body\":\"new_body\"}";
        
        mock.perform(patch("/course/1/lecture/0/reply/1")
                .content(requestBody))
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
        mock.perform(delete("/course/1/lecture/0/reply/1"))
                .andExpect(status().isOk());
    }
    
    @Test
    public void testDeleteReplyNotFoundException() throws Exception {
        mock.perform(delete("/course/1/lecture/0/reply/0"))
                .andExpect(status().is4xxClientError());
    }
    
    /**
     * Test of approve method, of class CourseEndpoint.
     */
    @Test
    public void testApprove() throws Exception {
        mock.perform(post("/comment/1/approve"))
    		.andExpect(status().isOk());
    }
    
    /**
     * Test of unapprove method, of class CourseEndpoint.
     */
    @Test
    public void testUnapprove() throws Exception {
        mock.perform(post("/comment/1/unapprove"))
    		.andExpect(status().isOk());
    }
    
    /**
     * Test of upvote method, of class CourseEndpoint.
     */
    @Test
    public void testUpvote() throws Exception {
        mock.perform(post("/comment/1/upvote"))
    		.andExpect(status().isOk());
    }
    
    /**
     * Test of downvote method, of class CourseEndpoint.
     */
    @Test
    public void testDownvote() throws Exception {
        mock.perform(post("/comment/1/downvote"))
    		.andExpect(status().isOk());
    }
    
    /**
     * Test of approve method, of class CourseEndpoint.
     */
    @Test
    public void testApproveReply() throws Exception {
        mock.perform(post("/reply/1/approve"))
    		.andExpect(status().isOk());
    }
    
    /**
     * Test of unapprove method, of class CourseEndpoint.
     */
    @Test
    public void testUnapproveReply() throws Exception {
        mock.perform(post("/reply/1/unapprove"))
    		.andExpect(status().isOk());
    }
    
    /**
     * Test of upvote method, of class CourseEndpoint.
     */
    @Test
    public void testUpvoteReply() throws Exception {
        mock.perform(post("/reply/1/upvote"))
    		.andExpect(status().isOk());
    }
    
    /**
     * Test of downvote method, of class CourseEndpoint.
     */
    @Test
    public void testDownvoteReply() throws Exception {
        mock.perform(post("/reply/1/downvote"))
    		.andExpect(status().isOk());
    }
}