package parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.json.JSONException;
import org.junit.Test;

import courses.Course;

public class CourseParserTest {

	@Test
	public void testTo() {
		String json = "{\"courseId\":\"10\",\"name\":\"Multimedia\",\"videos\":[],\"lectures\":[],\"courseNotes\":[]}";
		Course course = null;
		try {
			course = CourseParser.to(json);
		} catch (JSONException e) {
			e.printStackTrace();
			fail("Exception thrown");
		}
		assertEquals(course.getName(), "Multimedia");
		assertEquals(course.getCourseNotes().size(), 0);
		assertEquals(course.getVideos().size(), 0);
		assertEquals(course.getLectures().size(), 0);
	}
	
}
