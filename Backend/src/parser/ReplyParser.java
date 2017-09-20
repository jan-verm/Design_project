package parser;

import java.util.List;

import models.Reply;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Jan Vermeulen
 */
public class ReplyParser {

	private static final String REPLIES = "replies";
	private static final String UPVOTES = "upvotes";
	private static final String APPROVED = "approved";
	private static final String CREATION_TIME = "creationTime";
	private static final String REPLY_ID = "replyId";
	private static final String BODY = "body";
	private static final String USER = "user";
	private static final String SELF_COURSE_NOTES_REFERENCES = "selfCourseNotesReferences";
	private static final String SELF_VIDEO_REFERENCES = "selfVideoReferences";

	/**
	 * JSON Reply to Reply object
	 * 
	 * @param JSON String
	 * @return Reply object
	 */
	public static Reply to(String json) throws JSONException {
		JSONObject root = new JSONObject(json);
		String body = root.getString(BODY);

		Reply response = new Reply(body);

		if(root.has(REPLY_ID)) {
			response.setId(root.getInt(REPLY_ID));
		}

		if(root.has(CREATION_TIME)) {
			response.setCreationTime(root.getInt(CREATION_TIME));
		}

		if(root.has(APPROVED)) {
			response.setApproved(root.getBoolean(APPROVED));
		}

		if(root.has(UPVOTES)) {
			response.setUpvotes(root.getInt(UPVOTES));
		}

		if(root.has(REPLIES)) {
			JSONArray replyArray = root.getJSONArray(REPLIES);
			for (int i = 0; i < replyArray.length(); i++) {
				Reply r = to(replyArray.getJSONObject(i).toString());
				response.addChild(r);
			}
		}

		return response;
	}

	/**
	 * Reply object to JSON Reply
	 * 
	 * @param Reply object
	 * @return JSON String
	 */
	public static JSONObject from(Reply reply) throws JSONException {
		JSONObject root = new JSONObject();

		root.put(BODY, reply.getBody());
		root.put(CREATION_TIME, reply.getCreationTime());
		root.put(REPLY_ID, reply.getId());
		if (reply.getUser() != null) {
			root.put(USER, reply.getUser().getUsername());
		}
		root.put(APPROVED, reply.isApproved());
		root.put(UPVOTES, reply.getUpvotes());
		root.put(REPLIES, ReplyParser.fromList(reply.getChildren()));

		root.put(SELF_COURSE_NOTES_REFERENCES, CourseNotesReferenceParser.fromList(reply.getSelfCourseNotesReferences()));
		root.put(SELF_VIDEO_REFERENCES, VideoReferenceParser.fromList(reply.getSelfVideoReferences()));
		
		reply.getChildren();

		return root;
	}

	/**
	 * Reply object list to JSON Reply list
	 * 
	 * @param List of Reply objects
	 * @return JSON String
	 */
	public static JSONArray fromList(List<Reply> replyList) throws JSONException {
		JSONArray root = new JSONArray();
		for(Reply reply : replyList) {
			JSONObject listElem = from(reply);
			root.put(listElem);
		}
		return root;
	}

}
