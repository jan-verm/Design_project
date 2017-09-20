package parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import models.Video;
import models.VideoReference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

public class VideoReferenceParserTest {

	@Test
	public void testTo() {
		String json = "{\"duration\":9999,\"videoId\":1,\"timestamp\":12}";
		Video video = new Video("title", "url", 9999);
		video.setId(1);
		VideoReference ref = null;
		try {
			ref = VideoReferenceParser.to(json, video);
		} catch (JSONException e) {
			fail();
			e.printStackTrace();
		}
		assertEquals(ref.getTimestamp(), 12);
		assertEquals(ref.getVideo().getId(), 1);
		assertEquals(ref.getVideo().getDuration(), 9999);
		assertEquals(ref.getVideo().getUrl(), "url");
		
	}
	
	@Test
	public void testFrom() {
		Video video = new Video("title", "url", 9999);
		video.setId(1);
		VideoReference ref = new VideoReference(video, 12);
		JSONObject actual = null;
		//expected = "{\"duration\":9999,\"videoId\":1,\"timestamp\":12}";
		try {
			actual = VideoReferenceParser.from(ref);
            System.out.println(actual.toString());
            assertEquals(actual.getInt("refId"), 0);
            assertEquals(actual.getInt("videoId"), 1);
            assertEquals(actual.getInt("timestamp"), 12);
		} catch (JSONException e) {
			fail();
			e.printStackTrace();
		}
	}
	
	@Test
	public void testFromList() {
		Video video = new Video("title", "url", 9999);
		video.setId(1);
		VideoReference ref = new VideoReference(video, 12);
		JSONArray actual = null;
		//expected = "[{\"duration\":9999,\"videoId\":1,\"timestamp\":12}]";
		List<VideoReference> list = new ArrayList<>();
		list.add(ref);
		try {
			actual = VideoReferenceParser.fromList(list);
            System.out.println(actual.toString());
            assertEquals(actual.getJSONObject(0).getInt("refId"), 0);
            assertEquals(actual.getJSONObject(0).getInt("videoId"), 1);
            assertEquals(actual.getJSONObject(0).getInt("timestamp"), 12);
		} catch (JSONException e) {
			fail();
			e.printStackTrace();
		}
	}
	
}
