package parser;

import java.util.ArrayList;
import java.util.List;

import models.CourseNotes;
import models.Video;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Jan Vermeulen
 */
public class CourseNotesParser {
	
	private static final String COURSE_NOTES_ID = "courseNotesId";
	private static final String URL = "url";
	private static final String NAME = "name";

	/**
	 * JSON CourseNotes to CourseNotes object
	 * 
	 * @param JSON String
	 * @return CourseNotes object
	 */
	public static CourseNotes to(String json) throws JSONException {
		JSONObject root = new JSONObject(json);
		String title = root.getString(NAME);
		String url = root.getString(URL);
		
		CourseNotes cn = new CourseNotes(title, url);
		return cn;
	}
	
	/**
	 * JSON CourseNotes list to CourseNotes object
	 * 
	 * @param JSON String
	 * @return CourseNotes object
	 */
	public static List<CourseNotes> toList(String json) throws JSONException {
		JSONArray root = new JSONArray(json);
		List<CourseNotes> cnList = new ArrayList<CourseNotes>();
		for(int i=0; i < root.length(); i++) {
			String cnJSON = root.getJSONObject(i).toString();
			cnList.add(to(cnJSON));
		}
		return cnList;
	}
	
	/**
	 * CourseNotes object to JSON CourseNotes
	 * 
	 * @param CourseNotes object
	 * @return JSON String
	 */
	public static JSONObject from(CourseNotes courseNotes) throws JSONException {
		JSONObject root = new JSONObject();
		root.put(COURSE_NOTES_ID, courseNotes.getId());
		root.put(NAME, courseNotes.getTitle());
		root.put(URL, courseNotes.getUrl());
		return root;
	}
	
	/**
	 * list of CourseNotes objects to JSON CourseNotes list
	 * 
	 * @param CourseNotes object list
	 * @return JSON String
	 */
	public static JSONArray fromList(List<CourseNotes> courseNotesList) throws JSONException {
		JSONArray root = new JSONArray();
		for(CourseNotes courseNotes : courseNotesList) {
			JSONObject listElem = from(courseNotes);
			root.put(listElem);
		}
		return root;
	}
}
