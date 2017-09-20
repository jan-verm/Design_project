package parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import models.Comment;
import models.CourseNotes;
import models.CourseNotesReference;
import models.Location;
import models.Video;
import models.VideoReference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

public class CommentParserTest {

	@Test
	public void testTo() {
		String json = "{\"question\":\"false\",\"commentId\":1,\"selfVideoReferences\":[{\"refId\":0,\"videoId\":1,\"timestamp\":23}],\"title\":\"title\",\"body\":\"body\"}";
		Comment comment = null;
		try {
			comment = CommentParser.to(json);
		} catch (JSONException e) {
			e.printStackTrace();
			fail("Exception thrown");
		}
		assertEquals(comment.getBody(), "body");
	}

	@Test
	public void getSelfVideoRef() {
		String input = "{\"creationTime\":\"creationtime\",\"commentId\":1,\"selfVideoReferences\":[{\"refId\":0,\"videoId\":1,\"timestamp\":23}],\"title\":\"title\",\"body\":\"body\",\"username\":\"user\"}";

		//expected = "{\"duration\":999999,\"videoId\":1,\"timestamp\":23}";
		JSONArray array = null;
		try {
			array = CommentParser.getSelfVideoRefs(input);
			JSONObject actual = array.getJSONObject(0);
			System.out.println(actual.toString());
			assertEquals(actual.getInt("refId"), 0);
			assertEquals(actual.getInt("videoId"), 1);
			assertEquals(actual.getInt("timestamp"), 23);
		} catch (JSONException e) {
			e.printStackTrace();
			fail("Exception thrown");
		}

	}

	@Test
	public void from() {
		Comment comment = new Comment("body", false);
		comment.setCreationTime(1);
		comment.setId(1);
		Video video = new Video("videotest", "http://www.youtube.com", 999999);
		video.setId(1);
		VideoReference videoRef = new VideoReference(video, 23);
		List<VideoReference> videoRefs = new ArrayList<VideoReference>();
		videoRefs.add(videoRef);
		CourseNotes cn = new CourseNotes("title", "url");
		cn.setId(123);

		List<Location> locList = new ArrayList<>();
		locList.add(new Location(1, 2, 3, 4, 20));
		CourseNotesReference cnRef = new CourseNotesReference(cn, locList);
		List<CourseNotesReference> cnRefs = new ArrayList<>();
		cnRefs.add(cnRef);

		//expected = "{\"creationTime\":\"creationTime\",\"commentId\":1,\"selfVideoReference\":{\"duration\":999999,\"videoId\":1,\"timestamp\":23},\"title\":\"title\",\"body\":\"body\",\"username\":\"user\"}";
		JSONObject actual = null;
		try {
			actual = CommentParser.from(comment, videoRefs, cnRefs);
			System.out.println(actual.toString());
			assertEquals(actual.getInt("creationTime"), 1);
			assertEquals(actual.getInt("commentId"), 1);
			assertEquals(actual.getJSONArray("selfVideoReferences").getJSONObject(0).getInt("refId"), 0);
			assertEquals(actual.getJSONArray("selfVideoReferences").getJSONObject(0).getInt("videoId"), 1);
			assertEquals(actual.getJSONArray("selfVideoReferences").getJSONObject(0).getInt("timestamp"), 23);
			assertEquals(actual.getJSONArray("selfCourseNotesReferences").getJSONObject(0).getJSONArray("locations").getJSONObject(0).getInt("x1"), 1);
			assertEquals(actual.getJSONArray("selfCourseNotesReferences").getJSONObject(0).getJSONArray("locations").getJSONObject(0).getInt("x2"), 2);
			assertEquals(actual.getJSONArray("selfCourseNotesReferences").getJSONObject(0).getJSONArray("locations").getJSONObject(0).getInt("y1"), 3);
			assertEquals(actual.getJSONArray("selfCourseNotesReferences").getJSONObject(0).getJSONArray("locations").getJSONObject(0).getInt("y2"), 4);
			assertEquals(actual.getJSONArray("selfCourseNotesReferences").getJSONObject(0).getJSONArray("locations").getJSONObject(0).getInt("pageNumber"), 20);
			assertEquals(actual.getJSONArray("selfCourseNotesReferences").getJSONObject(0).getInt("courseNotesId"), 123);
		} catch (JSONException e) {
			e.printStackTrace();
			fail("Exception thrown");
		}
	}

	@Test
	public void fromList() {
		// Create comment list
		Comment comment = new Comment("body", false);
		comment.setCreationTime(1);
		comment.setId(1);
		List<Comment> commentList = new ArrayList<>();
		commentList.add(comment);

		// Create videoRef list
		Video video = new Video("videotest", "http://www.youtube.com", 999999);
		video.setId(1);
		VideoReference ref = new VideoReference(video, 23);
		List<VideoReference> selfVideoRefs = new ArrayList<>();
		selfVideoRefs.add(ref);
		List<List<VideoReference>> selfVideoRefsList = new ArrayList<>();
		selfVideoRefsList.add(selfVideoRefs);

		// Create courseNotesRef list
		CourseNotes cn = new CourseNotes("title", "url");
		cn.setId(123);
		List<Location> locList = new ArrayList<>();
		locList.add(new Location(1, 2, 3, 4, 20));
		CourseNotesReference cnRef = new CourseNotesReference(cn, locList);
		List<CourseNotesReference> cnRefs = new ArrayList<>();
		cnRefs.add(cnRef);
		List<List<CourseNotesReference>> selfCnRefsList = new ArrayList<>();
		selfCnRefsList.add(cnRefs);

		// Parse and Assert Comment with videoRef list and courseNotesRef list
		JSONArray actual = null;
		try {
			actual = CommentParser.fromList(commentList, selfVideoRefsList, selfCnRefsList);
			System.out.println(actual.toString());
			assertEquals(actual.getJSONObject(0).getInt("creationTime"), 1);
			assertEquals(actual.getJSONObject(0).getInt("commentId"), 1);
			assertEquals(actual.getJSONObject(0).getJSONArray("selfVideoReferences").getJSONObject(0).getInt("refId"), 0);
			assertEquals(actual.getJSONObject(0).getJSONArray("selfVideoReferences").getJSONObject(0).getInt("videoId"), 1);
			assertEquals(actual.getJSONObject(0).getJSONArray("selfVideoReferences").getJSONObject(0).getInt("timestamp"), 23);
			assertEquals(actual.getJSONObject(0).getJSONArray("selfCourseNotesReferences").getJSONObject(0).getInt("courseNotesId"), 123);
			assertEquals(actual.getJSONObject(0).getJSONArray("selfCourseNotesReferences").getJSONObject(0).getJSONArray("locations").getJSONObject(0).getInt("pageNumber"), 20);
			assertEquals(actual.getJSONObject(0).getJSONArray("selfCourseNotesReferences").getJSONObject(0).getJSONArray("locations").getJSONObject(0).getInt("x1"), 1);
			assertEquals(actual.getJSONObject(0).getJSONArray("selfCourseNotesReferences").getJSONObject(0).getJSONArray("locations").getJSONObject(0).getInt("x2"), 2);
			assertEquals(actual.getJSONObject(0).getJSONArray("selfCourseNotesReferences").getJSONObject(0).getJSONArray("locations").getJSONObject(0).getInt("y1"), 3);
			assertEquals(actual.getJSONObject(0).getJSONArray("selfCourseNotesReferences").getJSONObject(0).getJSONArray("locations").getJSONObject(0).getInt("y2"), 4);
		} catch (JSONException e) {
			e.printStackTrace();
			fail("Exception thrown");
		}
	}

}
