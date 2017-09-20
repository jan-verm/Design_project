package parser;

import java.util.ArrayList;
import java.util.List;

import models.CourseNotes;
import models.CourseNotesReference;
import models.Location;
import models.Video;
import models.VideoReference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Jan Vermeulen
 */
public class CourseNotesReferenceParser {

	private static final String COURSE_NOTES_TITLE = "courseNotesTitle";
	private static final String REF_ID = "refId";
	private static final String COURSE_NOTES_ID = "courseNotesId";
        private static final String COURSE_ID = "courseId";
        private static final String LECTURE_ID = "lectureId";
	private static final String PAGE_NUMBER = "pageNumber";
	private static final String VISIBLE = "visible";
	private static final String Y2 = "y2";
	private static final String Y1 = "y1";
	private static final String X2 = "x2";
	private static final String X1 = "x1";
	private static final String LOCATIONS = "locations";

	/**
	 * JSON CourseNotesReference to CourseNotesReference object
	 * 
	 * @param JSON String
	 * @return CourseNotesReference object
	 */
	public static CourseNotesReference to(String json, CourseNotes courseNotes) throws JSONException {
		JSONObject root = new JSONObject(json);
		JSONArray locationsArray = root.getJSONArray(LOCATIONS);

		List<Location> locations = new ArrayList<>();
		for (int i = 0; i < locationsArray.length(); i++){
			double x1 = locationsArray.getJSONObject(i).getDouble(X1);
			double x2 = locationsArray.getJSONObject(i).getDouble(X2);
			double y1 = locationsArray.getJSONObject(i).getDouble(Y1);
			double y2 = locationsArray.getJSONObject(i).getDouble(Y2);
			int pagenumber = locationsArray.getJSONObject(i).getInt(PAGE_NUMBER);
			Location location = new Location(x1, x2, y1, y2, pagenumber);
			locations.add(location);
		}

		CourseNotesReference cnRef = new CourseNotesReference(courseNotes, locations);
		if(root.has(VISIBLE)) cnRef.setVisible(root.getBoolean(VISIBLE));
		return cnRef;
	}

	/**
	 * JSON CourseNotesReference list to CourseNotesReference object list
	 * 
	 * @param JSON String
	 * @return CourseNotesReference object list
	 */
	public static List<CourseNotesReference> toList(String json, CourseNotes cn) throws JSONException {
		JSONArray root = new JSONArray(json);
		List<CourseNotesReference> refList = new ArrayList<CourseNotesReference>();
		for(int i=0; i < root.length(); i++) {
			String refJSON = root.getJSONObject(i).toString();
			refList.add(to(refJSON,cn));
		}
		return refList;
	}

	public static int getCourseNotesId(String json) throws JSONException {
		JSONObject root = new JSONObject(json);
		return root.getInt(COURSE_NOTES_ID);
	}
        
        /**
	 * JSON CourseNotesReference to CourseNotesReference object
	 * 
	 * @param JSON String
	 * @return CourseNotesReference object
	 */
	public static JSONObject from(CourseNotesReference ref, int[] parents) throws JSONException {
            JSONObject root = from(ref);
            root.put(COURSE_ID, parents[0]);
            root.put(LECTURE_ID, parents[1]);
            return root;
        }

	/**
	 * JSON CourseNotesReference to CourseNotesReference object
	 * 
	 * @param JSON String
	 * @return CourseNotesReference object
	 */
	public static JSONObject from(CourseNotesReference ref) throws JSONException {
		JSONObject root = new JSONObject();
		root.put(REF_ID, ref.getRefId());
		root.put(COURSE_NOTES_ID, ref.getCourseNotes().getId());
		root.put(COURSE_NOTES_TITLE, ref.getCourseNotes().getTitle());
		root.put(VISIBLE, ref.isVisible());
		JSONArray array = new JSONArray();
		for (int i = 0; i < ref.getLocations().size(); i++){
			JSONObject location = new JSONObject();
			location.put(X1, ref.getLocations().get(i).getX1());
			location.put(X2, ref.getLocations().get(i).getX2());
			location.put(Y1, ref.getLocations().get(i).getY1());
			location.put(Y2, ref.getLocations().get(i).getY2());
			location.put(PAGE_NUMBER, ref.getLocations().get(i).getPagenumber());
			array.put(location);
		}
		root.put(LOCATIONS, array);
		return root;
	}

	/**
	 * JSON CourseNotesReference list to CourseNotesReference object list
	 * 
	 * @param JSON String
	 * @return CourseNotesReference object list
	 */
	public static JSONArray fromList(List<CourseNotesReference> refList) throws JSONException {
		JSONArray root = new JSONArray();
		for(CourseNotesReference ref : refList) {
			JSONObject listElem = from(ref);
			root.put(listElem);
		}
		return root;
	}

}
