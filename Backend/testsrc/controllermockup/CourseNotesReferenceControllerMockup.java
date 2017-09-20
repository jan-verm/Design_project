package controllermockup;

import interfaces.CourseNotesReferenceControllerInterface;

import java.util.ArrayList;
import java.util.List;

import models.CourseNotes;
import models.CourseNotesReference;
import models.Location;
import databaseaccess.exceptions.ClassicDatabaseException;
import databaseaccess.exceptions.ClassicNotFoundException;

public class CourseNotesReferenceControllerMockup implements CourseNotesReferenceControllerInterface {

	@Override
	public int addCourseNotesReference(int courseNotesId, int commentId,
			CourseNotesReference courseNotesRef, int userId)
			throws ClassicDatabaseException, ClassicNotFoundException {
            if (courseNotesId == 0 || commentId == 0) {
                throw new ClassicNotFoundException();
            }
		return 123;
	}

	@Override
	public CourseNotesReference getCourseNotesReference(int referenceId) throws ClassicDatabaseException,
			ClassicNotFoundException {
            if (referenceId == 0) {
                throw new ClassicNotFoundException();
            }
		Location location = new Location(1, 2, 3, 4, 20);
		List<Location> locList = new ArrayList<>();
		locList.add(location);
		CourseNotes courseNotes = new CourseNotes("title", "url");
		courseNotes.setId(123);
		return new CourseNotesReference(courseNotes, locList);
	}

	@Override
	public List<CourseNotesReference> getCourseNotesReferences(int commentId)
			throws ClassicDatabaseException, ClassicNotFoundException {
            if (commentId == 0) {
                throw new ClassicNotFoundException();
            }
		List<CourseNotesReference> list = new ArrayList<CourseNotesReference>();
		Location location = new Location(1, 2, 3, 4, 20);
		List<Location> locList = new ArrayList<>();
		locList.add(location);
		CourseNotes courseNotes = new CourseNotes("title", "url");
		courseNotes.setId(123);
		list.add(new CourseNotesReference(courseNotes, locList));
		return list;
	}

	@Override
	public void updateCourseNotesReference(int commentId, int referenceId,
			CourseNotesReference courseNotesRef, int userId)
			throws ClassicDatabaseException, ClassicNotFoundException {
            if (referenceId == 0 || commentId == 0) {
                throw new ClassicNotFoundException();
            }
		return;
	}

	@Override
	public void deleteCourseNotesReference(int commentId, int referenceId, int userId)
			throws ClassicDatabaseException, ClassicNotFoundException {
            if (referenceId == 0 || commentId == 0) {
                throw new ClassicNotFoundException();
            }
		return;
	}

	@Override
	public List<CourseNotesReference> getSelfCourseNotesReferences(
			int courseNotesId, int commentId) throws ClassicDatabaseException,
			ClassicNotFoundException {
            if (courseNotesId == 0 || commentId == 0) {
                throw new ClassicNotFoundException();
            }
		List<CourseNotesReference> list = new ArrayList<CourseNotesReference>();
		Location location = new Location(1, 2, 3, 4, 20);
		List<Location> locList = new ArrayList<>();
		locList.add(location);
		CourseNotes courseNotes = new CourseNotes("title", "url");
		courseNotes.setId(123);
		list.add(new CourseNotesReference(courseNotes, locList));
		return list;
	}

    @Override
    public int[] getParents(int videoID) throws ClassicDatabaseException, ClassicNotFoundException {
        return new int[]{1,0};
    }

}
