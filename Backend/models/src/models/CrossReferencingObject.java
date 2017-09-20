package models;

import java.util.ArrayList;
import java.util.List;

public abstract class CrossReferencingObject {

	private List<CommentReference> commentRefs;
	private List<VideoReference> videoRefs;
	private List<CourseNotesReference> courseNotesRefs;
	
	public CrossReferencingObject() {
		this.commentRefs = new ArrayList<>();
		this.videoRefs = new ArrayList<>();
		this.courseNotesRefs = new ArrayList<>();
	}
	
	public void addReference(CommentReference annotationRef) {
		commentRefs.add(annotationRef);
	}
	
	public void addReference(VideoReference videoRef) {
		videoRefs.add(videoRef);
	}
	
	public void addReference(CourseNotesReference courseNotesRef) {
		courseNotesRefs.add(courseNotesRef);
	}

	public List<CommentReference> getCommentRefs() {
		return commentRefs;
	}

	public List<VideoReference> getVideoRefs() {
		return videoRefs;
	}

	public List<CourseNotesReference> getCourseNotesRefs() {
		return courseNotesRefs;
	}
	
}
