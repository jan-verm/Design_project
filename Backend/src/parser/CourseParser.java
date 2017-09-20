package parser;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import courses.Course;
import courses.Lecture;

/**
 * @author Jan Vermeulen
 */
public class CourseParser {

	/**
	 * JSON Course to Course object
	 * 
	 * @param JSON String
	 * @return Course object
	 */
	public static Course to(String json) throws JSONException {
		Lecture temp = LectureParser.to(json);
		
		Course course = new Course(temp.getName());
		course.setId(temp.getId());
		course.setCourseNotes(temp.getCourseNotes());
		course.setVideos(temp.getVideos());

		JSONObject root = new JSONObject(json);
		if(root.has("lectures")) {
			String lectureListJSON = new JSONObject(json).getJSONArray("lectures").toString();
			course.setLectures(LectureParser.toList(lectureListJSON));
		}

		return course;
	}

	/**
	 * Course object to JSON Course
	 * 
	 * @param Course object
	 * @return JSON String
	 */
	public static JSONObject from(Course course) throws JSONException {
		JSONObject root = LectureParser.from(course, course.getOwner().getUsername());
		root.put("lectures", LectureParser.fromList(course.getLectures(), course.getOwner().getUsername()));
		root.remove("lectureId");
		root.put("courseId", course.getId());
                if (course.getOwner() != null) {
                    root.put("owner", course.getOwner().getUsername());
                }
		return root;
	}

	/**
	 * Course object list to JSON Course list
	 * 
	 * @param Course object
	 * @return JSON String
	 */
	public static JSONArray fromList(List<Course> courseList) throws JSONException {
		JSONArray root = new JSONArray();
		for(Course course : courseList) {
			JSONObject listElem = from(course);
			root.put(listElem);
		}
		return root;
	}

}
