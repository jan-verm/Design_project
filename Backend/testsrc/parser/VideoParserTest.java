package parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import models.Video;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

public class VideoParserTest {

	@Test
	public void testTo() {
		String json = "{\"duration\":999999,\"name\":\"videotest\",\"url\":\"http://www.youtube.com\"}";
		Video video = null;
		try {
			video = VideoParser.to(json);
		} catch (JSONException e) {
			fail("Exception thrown");
			e.printStackTrace();
		}
		assertEquals("videotest", video.getTitle());
		assertEquals(999999, video.getDuration());
		assertEquals("http://www.youtube.com", video.getUrl());
	}
	
	@Test
	public void testFrom() {
		Video video = new Video("videotest", "http://www.youtube.com", 999999);
		video.setId(1);
		
		//expected = "{\"duration\":999999,\"videoId\":1,\"name\":\"videotest\",\"url\":\"http://www.youtube.com\"}";
		JSONObject actual = null;
		try {
			actual = VideoParser.from(video);
                        System.out.println(actual.toString());
                        assertEquals(999999, actual.getInt("duration"));
                        assertEquals(1, actual.getInt("videoId"));
                        assertEquals("videotest", actual.getString("name"));
                        assertEquals("http://www.youtube.com", actual.getString("url"));
		} catch (JSONException e) {
			fail("Exception thrown");
			e.printStackTrace();
		}
	}
	
	@Test
	public void testFromList(){
		Video video1 = new Video("video1", "http://www.youtube.com/1", 1);
		video1.setId(1);
		Video video2 = new Video("video2", "http://www.youtube.com/2", 2);
		video1.setId(2);
		List<Video> list = new ArrayList<>();
		list.add(video1);
		list.add(video2);
		
		String expected = "[{\"duration\":1,\"videoId\":2,\"name\":\"video1\",\"url\":\"http://www.youtube.com/1\"},{\"duration\":2,\"videoId\":0,\"name\":\"video2\",\"url\":\"http://www.youtube.com/2\"}]";
		JSONArray actual = null;
		try {
			actual = VideoParser.fromList(list);
                        System.out.println(actual.toString());
                        assertEquals(1, actual.getJSONObject(0).getInt("duration"));
                        assertEquals(2, actual.getJSONObject(0).getInt("videoId"));
                        assertEquals("video1", actual.getJSONObject(0).getString("name"));
                        assertEquals("http://www.youtube.com/1", actual.getJSONObject(0).getString("url"));
            
                        assertEquals(2, actual.getJSONObject(1).getInt("duration"), 2);
                        assertEquals(0, actual.getJSONObject(1).getInt("videoId"), 0);
                        assertEquals("video2", actual.getJSONObject(1).getString("name"));
                        assertEquals("http://www.youtube.com/2", actual.getJSONObject(1).getString("url"));
		} catch (JSONException e) {
			fail("Exception thrown");
			e.printStackTrace();
		}
		
	}
	
}
