package parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import models.CourseNotes;
import models.CourseNotesReference;
import models.Location;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

public class CourseNotesReferenceParserTest {

	@Test
	public void testTo() {
		String json = "{\"courseNotesId\": 123, \"locations\":[{\"pageNumber\": 20, \"x1\": 1, \"y1\": 3, \"x2\": 2, \"y2\": 4 }]}";
		CourseNotes cn = new CourseNotes("title", "url");
		cn.setId(123);
		CourseNotesReference ref = null;
		try {
			ref = CourseNotesReferenceParser.to(json, cn);
                        assertEquals(123, ref.getCourseNotes().getId());
                        assertEquals(1.0, ref.getLocations().get(0).getX1(), 0.0001);
                        assertEquals(2.0, ref.getLocations().get(0).getX2(), 0.0001);
                        assertEquals(3.0, ref.getLocations().get(0).getY1(), 0.0001);
                        assertEquals(4.0, ref.getLocations().get(0).getY2(), 0.0001);
                        assertEquals(20, ref.getLocations().get(0).getPagenumber());
		} catch (JSONException e) {
			fail();
			e.printStackTrace();
		}
	}
	
	@Test
	public void testFrom() {
		CourseNotes cn = new CourseNotes("title", "url");
		cn.setId(123);
		Location location = new Location(1, 2, 3, 4, 20);
                List<Location> locations = new ArrayList<>();
                locations.add(location);
		CourseNotesReference ref = new CourseNotesReference(cn, locations);
		
		JSONObject actual = null;
		try {
			actual = CourseNotesReferenceParser.from(ref);
                        assertEquals(1.0, actual.getJSONArray("locations").getJSONObject(0).getDouble("x1"), 0.0001);
                        assertEquals(2.0, actual.getJSONArray("locations").getJSONObject(0).getDouble("x2"), 0.0001);
                        assertEquals(3.0, actual.getJSONArray("locations").getJSONObject(0).getDouble("y1"), 0.0001);
                        assertEquals(4.0, actual.getJSONArray("locations").getJSONObject(0).getDouble("y2"), 0.0001);
                        assertEquals(20, actual.getJSONArray("locations").getJSONObject(0).getInt("pageNumber"));
                        assertEquals(123, actual.getInt("courseNotesId"));
		} catch (JSONException e) {
			fail();
			e.printStackTrace();
		}
	}
	
	@Test
	public void testFromList() {
		CourseNotes cn = new CourseNotes("title", "url");
		cn.setId(123);
		Location location = new Location(1, 2, 3, 4, 20);
                List<Location> locations = new ArrayList<>();
                locations.add(location);
		CourseNotesReference ref = new CourseNotesReference(cn, locations);
		List<CourseNotesReference> refList = new ArrayList<>();
		refList.add(ref);
		
		JSONArray actual = null;
		try {
			actual = CourseNotesReferenceParser.fromList(refList);
                        assertEquals(1.0, actual.getJSONObject(0).getJSONArray("locations").getJSONObject(0).getDouble("x1"), 0.0001);
                        assertEquals(2.0, actual.getJSONObject(0).getJSONArray("locations").getJSONObject(0).getDouble("x2"), 0.0001);
                        assertEquals(3.0, actual.getJSONObject(0).getJSONArray("locations").getJSONObject(0).getDouble("y1"), 0.0001);
                        assertEquals(4.0, actual.getJSONObject(0).getJSONArray("locations").getJSONObject(0).getDouble("y2"), 0.0001);
                        assertEquals(20, actual.getJSONObject(0).getJSONArray("locations").getJSONObject(0).getInt("pageNumber"));
                        assertEquals(123, actual.getJSONObject(0).getInt("courseNotesId"));
		} catch (JSONException e) {
			fail();
			e.printStackTrace();
		}
	}
	
}
