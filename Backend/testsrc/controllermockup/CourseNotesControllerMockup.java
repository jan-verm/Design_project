package controllermockup;

import interfaces.CourseNotesControllerInterface;

import java.util.ArrayList;
import java.util.List;

import models.Comment;
import models.CourseNotes;
import models.CourseNotesReference;
import databaseaccess.exceptions.ClassicDatabaseException;
import databaseaccess.exceptions.ClassicNotFoundException;

public class CourseNotesControllerMockup implements CourseNotesControllerInterface {

	@Override
	public int addCourseNotes(int parentId, CourseNotes courseNotes, int userId)
			throws ClassicDatabaseException {
		return 123;
	}

	@Override
	public CourseNotes getCourseNotes(int courseNotesId)
			throws ClassicDatabaseException, ClassicNotFoundException {
            if (courseNotesId == 0 ) {
                throw new ClassicNotFoundException();
            }
		CourseNotes cn = new CourseNotes("title", "url");
		cn.setId(123);
		return cn;
	}

	@Override
	public List<CourseNotes> getAllCourseNotes(int parentId)
			throws ClassicDatabaseException {
		CourseNotes cn = new CourseNotes("title", "url");
		cn.setId(123);
		List<CourseNotes> cnList = new ArrayList<CourseNotes>();
		cnList.add(cn);
		return cnList;
	}

	@Override
	public void updateCourseNotes(int courseNotesId, CourseNotes courseNotes, int userId)
			throws ClassicDatabaseException, ClassicNotFoundException {
            if (courseNotesId == 0 ) {
                throw new ClassicNotFoundException();
            }
		return;
	}

	@Override
	public void deleteCourseNotes(int courseNotesId, int userId)
			throws ClassicDatabaseException, ClassicNotFoundException {
            if (courseNotesId == 0 ) {
                throw new ClassicNotFoundException();
            }
		return;
	}

	@Override
	public String addAllPdfComments(CourseNotes courseNotes,
			List<Comment> comments, boolean annotations, boolean questions) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void addPdfComment(String outputFile, Comment comment, CourseNotesReference cnref) {
		throw new UnsupportedOperationException();
	}
}
