package parser;

import java.util.List;

import models.Comment;
import models.CourseNotesReference;
import models.VideoReference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Jan Vermeulen
 */
public class CommentParser {

	private static final String REPLIES = "replies";
	private static final String COMMENT_ID = "commentId";
	private static final String SELF_COURSE_NOTES_REFERENCES = "selfCourseNotesReferences";
	private static final String SELF_VIDEO_REFERENCES = "selfVideoReferences";
	private static final String UPVOTES = "upvotes";
	private static final String APPROVED = "approved";
	private static final String CREATION_TIME = "creationTime";
	private static final String BODY = "body";
	private static final String QUESTION = "question";
	private static final String USERNAME = "username";

	/**
	 * JSON comment to comment object
	 * 
	 * @param JSON String
	 * @return comment object
	 */
	public static Comment to(String json) throws JSONException {
		JSONObject root = new JSONObject(json);
		boolean question = root.getBoolean(QUESTION);
		String body = root.getString(BODY);
		
		Comment a =  new Comment(body, question);
		
		if(root.has(CREATION_TIME)) {
			a.setCreationTime(root.getInt(CREATION_TIME));
		}
		
		if(root.has(APPROVED)) {
			a.setApproved(root.getBoolean(APPROVED));
		}
		
		if(root.has(UPVOTES)) {
			a.setUpvotes(root.getInt(UPVOTES));
		}
		
		return a;
	}
	
	/**
	 * Retrieve self references of a video.
	 * 
	 * @param JSON String
	 * @return comment object
	 */
	public static JSONArray getSelfVideoRefs(String json) throws JSONException {
		JSONObject root = new JSONObject(json);
		return root.getJSONArray(SELF_VIDEO_REFERENCES);
	}
	
	/**
	 * Retrieve self references of coursenotes.
	 * 
	 * @param JSON String
	 * @return comment object
	 */
	public static JSONArray getSelfCourseNotesReferences(String json) throws JSONException {
		JSONObject root = new JSONObject(json);
		return root.getJSONArray(SELF_COURSE_NOTES_REFERENCES);
	}
	
	/**
	 * Comment object to JSON comment
	 * 
	 * @param comment object
	 * @return JSON String
	 */
	public static JSONObject from(Comment comment, List<VideoReference> videoRefs, List<CourseNotesReference> cnRefs) throws JSONException {
		JSONObject root = new JSONObject();
		root.put(COMMENT_ID, comment.getId());
                if (comment.getUser() != null) {
                    root.put(USERNAME, comment.getUser().getUsername());
                }
		root.put(BODY, comment.getBody());
		root.put(QUESTION, comment.isQuestion());
		root.put(CREATION_TIME, comment.getCreationTime());
		root.put(APPROVED, comment.isApproved());
		root.put(UPVOTES, comment.getUpvotes());
		root.put(REPLIES, ReplyParser.fromList(comment.getChildren()));
		
		if(videoRefs != null) {
			root.put(SELF_VIDEO_REFERENCES, VideoReferenceParser.fromList(videoRefs));
		}
		if(cnRefs != null) {
			root.put(SELF_COURSE_NOTES_REFERENCES, CourseNotesReferenceParser.fromList(cnRefs));
		}
		
		return root;
	}
	
	/**
	 * List of Comment objects to JSON comment list
	 * 
	 * @param comment object
	 * @return JSON String
	 */
	public static JSONArray fromList(List<Comment> CommentList, List<List<VideoReference>> selfVideoRefsList, List<List<CourseNotesReference>> selfCnRefsList) throws JSONException {
		JSONArray root = new JSONArray();
		for(int i=0; i<CommentList.size(); i++) {
			Comment comment = CommentList.get(i);
			List<VideoReference> selfVideoRefs = null;
			List<CourseNotesReference> selfCnRefs = null;
			if(selfVideoRefsList != null) {
				selfVideoRefs = selfVideoRefsList.get(i);
			}
			if(selfCnRefsList != null) {
				selfCnRefs = selfCnRefsList.get(i);
			}
			
			JSONObject listElem = from(comment, selfVideoRefs, selfCnRefs);
			root.put(listElem);
		}
		return root;
	}
	
}
