/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package models;

import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Juta
 */
public class CommentTest {

    /**
     * Test of getBody method, of class Annotation.
     */
    @Test
    public void testGetBody() {
        String expResult = "body";
        Comment instance = new Comment(expResult, false);
        String result = instance.getBody();
        assertEquals(expResult, result);
    }

    /**
     * Test of setBody method, of class Annotation.
     */
    @Test
    public void testSetBody() {
        String body = "body";
        Comment instance = new Comment("", false);
        instance.setBody(body);
        String result = instance.getBody();
        assertEquals(body, result);
    }

    @Test
    public void testGetAndSetId() {
        int id = 5;
        Comment instance = new Comment("", false);
        instance.setId(id);
        int result = instance.getId();
        assertEquals(id, result);
    }

    @Test
    public void testIsAndSetQuestion() {
        boolean question = false;
        Comment instance = new Comment("", false);
        instance.setQuestion(question);
        boolean result = instance.isQuestion();
        assertEquals(question, result);
    }

    @Test
    public void testGetAndAddChildren() {
        Comment instance = new Comment("", false);
        Reply reply = new Reply("");
        instance.addChild(reply);
        List<Reply> childeren = instance.getChildren();
        assertEquals(reply.getUser(), childeren.get(0).getUser());
        assertEquals(reply.getBody(), childeren.get(0).getBody());
    }
    
    @Test
    public void testGetAndSetChildren() {
        Comment instance = new Comment("", false);
        Reply reply = new Reply("");
        List<Reply> replies = new ArrayList<>();
        replies.add(reply);
        
        instance.setChildren(replies);
        List<Reply> childeren = instance.getChildren();
        assertEquals(reply.getUser(), childeren.get(0).getUser());
        assertEquals(reply.getBody(), childeren.get(0).getBody());
    }

    @Test
    public void testGetAndSetCreationTime() {
        int creationTime = 300;
        Comment instance = new Comment("", false);
        instance.setCreationTime(creationTime);
        assertEquals(instance.getCreationTime(), creationTime);
    }

    @Test
    public void testGetAndSetSelfVideoReferences() {
        Video video = new Video("", "", 20);
        VideoReference videoReference = new VideoReference(video, 10);
        videoReference.setRefId(20);
        Comment instance = new Comment("", false);
        List<VideoReference> refs = new ArrayList<>();
        refs.add(videoReference);
        instance.setSelfVideoReferences(refs);
        assertEquals(videoReference.getRefId(), instance.getSelfVideoReferences().get(0).getRefId());
    }

    @Test
    public void testGetAndSetSelfCourseNotesReference() {        
        CourseNotes courseNotes = new CourseNotes("", "");
        Location location = new Location(1, 2, 3, 4, 5);
        List<Location> locs = new ArrayList<>();
        locs.add(location);
        CourseNotesReference courseNotesReference = new CourseNotesReference(courseNotes, locs);
        courseNotesReference.setRefId(20);
        List<CourseNotesReference> refs = new ArrayList<>();
        refs.add(courseNotesReference);
        Comment instance = new Comment("", false);
        instance.setSelfCourseNotesReferences(refs);
        assertEquals(courseNotesReference.getRefId(), instance.getSelfCourseNotesReferences().get(0).getRefId());
    }
    
    @Test
	public void testApproved() {
    	Comment instance = new Comment("", false);
    	assertEquals(false, instance.isApproved());
    	instance.setApproved(true);
    	assertEquals(true, instance.isApproved());
	}


    @Test
	public void testUpvotes() {
    	Comment instance = new Comment("", false);
    	assertEquals(0, instance.getUpvotes());
    	instance.setUpvotes(2);
    	assertEquals(2, instance.getUpvotes());
	}

    @Test
	public void testUpvote() {
    	Comment instance = new Comment("", false);
    	assertEquals(0, instance.getUpvotes());
    	instance.upvote();
    	assertEquals(1, instance.getUpvotes());
	}

    @Test
	public void testApprove() {
    	Comment instance = new Comment("", false);
    	assertEquals(false, instance.isApproved());
    	instance.approve();
    	assertEquals(true, instance.isApproved());
	}
}
