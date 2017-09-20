/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package models;

import java.util.ArrayList;
import java.util.List;
import org.junit.Test;

import courses.Role;
import courses.User;

import static org.junit.Assert.*;

/**
 *
 * @author jorsi
 */
public class ReplyTest {
	
	@Test
    public void testUser() {
		Reply instance = new Reply("body");
		User user = new User("username", Role.NONE, "password");
		instance.setUser(user);
		User result = instance.getUser();
		assertEquals(user, result);
	}

    @Test
    public void testGetBody() {
        String body = "replyBody";
        Reply instance = new Reply("");
        instance.setBody(body);
        assertEquals(instance.getBody(), body);  
    }

    @Test
    public void testSetBody() {
        String body = "replyBody";
        Reply instance = new Reply("");
        instance.setBody(body);
        assertEquals(instance.getBody(), body);  
    }

    @Test
    public void testGetId() {
        int id = 2;
        Reply instance = new Reply("");
        instance.setId(id);
        assertEquals(instance.getId(), id);
    }

    @Test
    public void testSetId() {
        int id = 2;
        Reply instance = new Reply("");
        instance.setId(id);
        assertEquals(instance.getId(), id);
    }

    @Test
    public void testGetCreationTime() {
        int creationTime= 300;
        Reply instance = new Reply("");
        instance.setCreationTime(creationTime);        
        assertEquals(instance.getCreationTime(), creationTime);
    }

    @Test
    public void testSetCreationTime() {
        int creationTime= 300;
        Reply instance = new Reply("");
        instance.setCreationTime(creationTime);        
        assertEquals(instance.getCreationTime(), creationTime);
    }
    
    @Test
    public void testGetChildren() {
        Reply instance = new Reply("");
        Reply reply = new Reply("");
        instance.addChild(reply);
        List<Reply> childeren = instance.getChildren();
        assertEquals(reply.getUser(), childeren.get(0).getUser());
        assertEquals(reply.getBody(), childeren.get(0).getBody());
        
    }
    
    @Test
    public void testAddChild() {
        Reply instance = new Reply("");
        Reply reply = new Reply("");
        instance.addChild(reply);
        List<Reply> childeren = instance.getChildren();
        assertEquals(reply.getUser(), childeren.get(0).getUser());
        assertEquals(reply.getBody(), childeren.get(0).getBody());
    }
    
    @Test
    public void testSetChildren() {
    	Reply instance = new Reply("");
        Reply reply = new Reply("");
        List<Reply> replies = new ArrayList<>();
        replies.add(reply);
        
        instance.setChildren(replies);
        List<Reply> childeren = instance.getChildren();
        assertEquals(reply.getUser(), childeren.get(0).getUser());
        assertEquals(reply.getBody(), childeren.get(0).getBody());
    }

    @Test
	public void testApproved() {
    	Reply instance = new Reply("");
    	assertEquals(false, instance.isApproved());
    	instance.setApproved(true);
    	assertEquals(true, instance.isApproved());
	}


    @Test
	public void testUpvotes() {
    	Reply instance = new Reply("");
    	assertEquals(0, instance.getUpvotes());
    	instance.setUpvotes(2);
    	assertEquals(2, instance.getUpvotes());
	}

    @Test
	public void testUpvote() {
    	Reply instance = new Reply("");
    	assertEquals(0, instance.getUpvotes());
    	instance.upvote();
    	assertEquals(1, instance.getUpvotes());
	}

    @Test
	public void testApprove() {
    	Reply instance = new Reply("");
    	assertEquals(false, instance.isApproved());
    	instance.approve();
    	assertEquals(true, instance.isApproved());
	}
    
}
