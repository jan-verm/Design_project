package parser;

import java.util.ArrayList;
import java.util.List;

import models.CourseNotes;
import models.Video;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import courses.Lecture;

/**
 * @author Jan Vermeulen
 */
public class LectureParser {

	private static final String LECTURE_ID = "lectureId";
	private static final String COURSE_NOTES = "courseNotes";
	private static final String VIDEOS = "videos";
	private static final String NAME = "name";
	private static final String OWNER = "owner";

	/**
	 * JSON Lecture to Lecture object
	 * 
	 * @param JSON String
	 * @return Lecture object
	 */
	public static Lecture to(String json) throws JSONException {
		JSONObject root = new JSONObject(json);
		Lecture lecture = new Lecture(root.getString(NAME));
		if(root.has(VIDEOS)) {
			List<Video> vlist = VideoParser.toList(root.getJSONArray(VIDEOS).toString());
			lecture.setVideos(vlist);
		}
		if(root.has(COURSE_NOTES)) {
			List<CourseNotes> cnlist = CourseNotesParser.toList(root.getJSONArray(COURSE_NOTES).toString());
			lecture.setCourseNotes(cnlist);
		}
		if (root.has(LECTURE_ID)) {
			lecture.setId(root.getInt(LECTURE_ID));
		}
		return lecture;
	}

	/**
	 * JSON Lecture list to Lecture object list
	 * 
	 * @param JSON String
	 * @return Lecture object list
	 */
	public static List<Lecture> toList(String json) throws JSONException {
		JSONArray root = new JSONArray(json);
		List<Lecture> lectureList = new ArrayList<Lecture>();
		for(int i=0; i < root.length(); i++) {
			String lectureJSON = root.getJSONObject(i).toString();
			lectureList.add(to(lectureJSON));
		}
		return lectureList;
	}

	/**
	 * Lecture object to JSON Lecture
	 * 
	 * @param Lecture object
	 * @return JSON String
	 */
	public static JSONObject from(Lecture lecture, String owner) throws JSONException {
		JSONObject root = new JSONObject();
		root.put(LECTURE_ID, lecture.getId());
		root.put(NAME, lecture.getName());
		root.put(VIDEOS, VideoParser.fromList(lecture.getVideos()));
		root.put(COURSE_NOTES, CourseNotesParser.fromList(lecture.getCourseNotes()));
		root.put(OWNER, owner);
		return root;
	}

	/**
	 * Lecture object list to JSON Lecture list
	 * 
	 * @param Lecture object
	 * @return JSON String
	 */
	public static JSONArray fromList(List<Lecture> lectureList, String owner) throws JSONException {
		JSONArray root = new JSONArray();
		for(Lecture lecture : lectureList) {
			JSONObject listElem = from(lecture, owner);
			root.put(listElem);
		}
		return root;
	}

}
