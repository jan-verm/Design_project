package parser;

import java.util.ArrayList;
import java.util.List;

import models.CourseNotes;
import models.CourseNotesReference;
import models.Video;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

/**
 * @author Jan Vermeulen
 */
public class VideoParser {

	private static final String VIDEO_ID = "videoId";
	private static final String DURATION = "duration";
	private static final String URL = "url";
	private static final String NAME = "name";

	/**
	 * JSON Video to Video object
	 * 
	 * @param JSON String
	 * @return Video object
	 */
	public static Video to(String json) throws JSONException {
		JSONObject root = new JSONObject(json);
		String title = root.getString(NAME);
		String url = root.getString(URL);
		int duration = root.getInt(DURATION);
		
		return new Video(title, url, duration);
	}
	
	/**
	 * JSON Video list to Video object list
	 * 
	 * @param JSON String
	 * @return List of Video objects
	 */
	public static List<Video> toList(String json) throws JSONException {
		JSONArray root = new JSONArray(json);
		List<Video> videoList = new ArrayList<Video>();
		for(int i=0; i < root.length(); i++) {
			String videoJSON = root.getJSONObject(i).toString();
			videoList.add(to(videoJSON));
		}
		return videoList;
	}
	
	/**
	 * Video object to JSON Video
	 * 
	 * @param Video object
	 * @return JSON String
	 */
	public static JSONObject from(Video video) throws JSONException {
		JSONObject root = new JSONObject();
		root.put(VIDEO_ID, video.getId());
		root.put(NAME, video.getTitle());
		root.put(URL, video.getUrl());
		root.put(DURATION, video.getDuration());
		return root;
	}
	
	/**
	 * Video object list to JSON Video list
	 * 
	 * @param List of video objects
	 * @return JSON String
	 */
	public static JSONArray fromList(List<Video> videoList) throws JSONException {
		JSONArray root = new JSONArray();
		for(Video video : videoList) {
			JSONObject listElem = from(video);
			root.put(listElem);
		}
		return root;
	}
	
}
