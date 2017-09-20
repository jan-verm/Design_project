package models;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class CrossReferencingObjectTest {
	@Test
    public void testAddCommentRef() {
    	Video instance = new Video("title", "url", 999);
    	Comment comment = new Comment("body", false);
    	
    	instance.addReference(new CommentReference(comment));
    	assertEquals(comment, instance.getCommentRefs().get(0).getComment());
    }
    
    @Test
    public void testAddVideoRef() {
    	Video instance = new Video("title", "url", 999);
    	Comment comment = new Comment("body", false);
    	
    	comment.addReference(new VideoReference(instance, 20));
    	assertEquals(instance, comment.getVideoRefs().get(0).getVideo());
    }
    
    @Test
    public void testAddCourseNotesRef() {
    	CourseNotes instance = new CourseNotes("title", "url");
    	Comment comment = new Comment("body", false);
    	
    	Location loc = new Location(1, 2, 3, 4, 5);
        List<Location> locs = new ArrayList<>();
        locs.add(loc);
        
    	comment.addReference(new CourseNotesReference(instance, locs));
    	assertEquals(instance, comment.getCourseNotesRefs().get(0).getCourseNotes());
    }
}
