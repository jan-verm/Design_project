package parser;

import java.util.ArrayList;
import java.util.List;

import models.Video;
import models.VideoReference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Jan Vermeulen
 */
public class VideoReferenceParser {

	private static final String VIDEO_TITLE = "videoTitle";
	private static final String VIDEO_ID = "videoId";
        private static final String COURSE_ID = "courseId";
        private static final String LECTURE_ID = "lectureId";
	private static final String REF_ID = "refId";
	private static final String TIMESTAMP = "timestamp";
	private static final String VISIBLE = "visible";

	/**
	 * JSON VideoReference to VideoReference object
	 * 
	 * @param JSON String
	 * @return VideoReference object
	 */
	public static VideoReference to(String json, Video video) throws JSONException {
		JSONObject root = new JSONObject(json);
		int timestamp = root.getInt(TIMESTAMP);
		VideoReference vRef = new VideoReference(video, timestamp);
		if(root.has(VISIBLE)) vRef.setVisible(root.getBoolean(VISIBLE));
		return vRef;
	}

	/**
	 * JSON VideoReference list to VideoReference object list
	 * 
	 * @param JSON String
	 * @return List of videoReference object
	 */
	public static List<VideoReference> toList(String json, Video video) throws JSONException {
		JSONArray root = new JSONArray(json);
		List<VideoReference> refList = new ArrayList<VideoReference>();
		for(int i=0; i < root.length(); i++) {
			String refJSON = root.getJSONObject(i).toString();
			refList.add(to(refJSON,video));
		}
		return refList;
	}
	
	/**
	 * VideoReference object to JSON VideoReference
	 * 
	 * @param VideoReference object
	 * @return JSON String
	 */
	public static JSONObject from(VideoReference ref) throws JSONException {
		JSONObject root = new JSONObject();
		root.put(REF_ID, ref.getRefId());
		root.put(VIDEO_ID, ref.getVideo().getId());
		root.put(VIDEO_TITLE, ref.getVideo().getTitle());
		root.put(TIMESTAMP, ref.getTimestamp());
		root.put(VISIBLE, ref.isVisible());
		return root;
	}
        
        /**
	 * VideoReference object to JSON VideoReference
	 * 
	 * @param VideoReference object
	 * @return JSON String
	 */
	public static JSONObject from(VideoReference ref, int[] parents) throws JSONException {
            JSONObject root = from(ref);
            root.put(COURSE_ID, parents[0]);
            root.put(LECTURE_ID, parents[1]);
            return root;
        }
	
	/**
	 * VideoReference object list to JSON VideoReference list
	 * 
	 * @param List of videoReference objects
	 * @return JSON String
	 */
	public static JSONArray fromList(List<VideoReference> refList) throws JSONException {
		JSONArray root = new JSONArray();
		for(VideoReference ref : refList) {
			JSONObject listElem = from(ref);
			root.put(listElem);
		}
		return root;
	}
	
	public static int getVideoId(String json) throws JSONException {
		JSONObject root = new JSONObject(json);
		return root.getInt(VIDEO_ID);
	}
	
}
