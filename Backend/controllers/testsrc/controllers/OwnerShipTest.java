package controllers;

import java.util.ArrayList;
import java.util.List;

import models.Comment;
import models.CourseNotes;
import models.CourseNotesReference;
import models.Location;
import models.Reply;
import models.Video;
import models.VideoReference;

import org.junit.Before;
import org.junit.Test;

import courses.Course;
import courses.Lecture;
import courses.Role;
import courses.User;
import dao.CoursesMockUpDAO;
import dao.MockUpDAO;
import databaseaccess.exceptions.ClassicDatabaseException;
import databaseaccess.exceptions.ClassicUnauthorizedException;
import databaseaccess.interfaces.ILectureDao;

public class OwnerShipTest {
	private VideoController videoController;
    private CourseNotesController courseNotesController;
    private CommentController commentController;
    private CourseController courseController;
    private UserController userController;
    private VideoReferenceController videoReferenceController;
    private CourseNotesReferenceController courseNotesReferenceController;
    private int userId;
    private int userId2;
    private int profId;
    private int adminId;
    

    @Before
    public void setUp() throws ClassicDatabaseException {
        MockUpDAO mockup = new MockUpDAO();
        CoursesMockUpDAO coursesMockup = new CoursesMockUpDAO();
        
        commentController = new CommentController(mockup, mockup, mockup, mockup, coursesMockup);
        videoController = new VideoController(mockup, coursesMockup, coursesMockup);
        courseNotesController = new CourseNotesController(mockup, coursesMockup, coursesMockup, "/var/www/classic/resources/courses");
        videoReferenceController =  new VideoReferenceController(mockup, mockup, coursesMockup);
        courseNotesReferenceController = new CourseNotesReferenceController(mockup, mockup, coursesMockup);
        userController = new UserController(coursesMockup);
        courseController = new CourseController(coursesMockup, coursesMockup, mockup, mockup, coursesMockup, "/var/www/classic/resources/courses");
        
        profId = coursesMockup.addUser(new User("username2", Role.TEACHER, "password"));
        userId = coursesMockup.addUser(new User("username", Role.STUDENT, "password"));
        userId2 = coursesMockup.addUser(new User("username2", Role.STUDENT, "password"));
        adminId = coursesMockup.addUser(new User("username2", Role.ADMIN, "password"));
    }
    
    @Test(expected = ClassicUnauthorizedException.class)
    public void testCourse() throws Exception {
        Course course = new Course("course");
    	int courseId = courseController.addCourse(course, profId);
        courseController.updateCourse(courseId, course, userId);
    }
    
    @Test(expected = ClassicUnauthorizedException.class)
    public void testLecture() throws Exception {
        Course course = new Course("course");
    	int courseId = courseController.addCourse(course, profId);
        int lectureId = courseController.addLecture(courseId, new Lecture("lecture"), profId);
        courseController.updateLecture(lectureId, new Lecture("lecture"), userId);
    }
    
    @Test(expected = ClassicUnauthorizedException.class)
    public void testVideo() throws Exception {
        Course course = new Course("course");
    	int courseId = courseController.addCourse(course, profId);
        videoController.addVideo(courseId, new Video("title", "url", 999), userId);
    }
    
    @Test(expected = ClassicUnauthorizedException.class)
    public void testUpdateVideo() throws Exception {
        Course course = new Course("course");
    	int courseId = courseController.addCourse(course, profId);
        int videoId = videoController.addVideo(courseId, new Video("title", "url", 999), adminId);
        videoController.updateVideo(videoId, new Video("title", "url", 999), userId);
    }
    
    @Test(expected = ClassicUnauthorizedException.class)
    public void testCourseNotes() throws Exception {
        Course course = new Course("course");
    	int courseId = courseController.addCourse(course, profId);
        courseNotesController.addCourseNotes(courseId, new CourseNotes("title", "url"), userId);
    }
    
    @Test(expected = ClassicUnauthorizedException.class)
    public void testUpdateCourseNotes() throws Exception {
        Course course = new Course("course");
    	int courseId = courseController.addCourse(course, profId);
        int notesId = courseNotesController.addCourseNotes(courseId, new CourseNotes("title", "url"), adminId);
        courseNotesController.updateCourseNotes(notesId, new CourseNotes("title", "url"), userId);
    }
    
    @Test(expected = ClassicUnauthorizedException.class)
    public void testComments() throws Exception {
        Course course = new Course("course");
        CourseNotes cn = new CourseNotes("title", "url");
    	int courseId = courseController.addCourse(course, profId);
        int courseNotesId = courseNotesController.addCourseNotes(courseId, cn, adminId);
        
        Comment comment = new Comment("body", true);
        List<Location> locations = new ArrayList<>();
        locations.add(new Location(20, 1, 2, 3, 4));
        CourseNotesReference cnRef = new CourseNotesReference(cn, locations);
        List<CourseNotesReference> refs = new ArrayList<>();
        refs.add(cnRef);
        int commentId = commentController.addCourseNotesComment(courseNotesId, comment, refs, userId);
        commentController.updateComment(commentId, comment, userId2);
    }
    
    @Test(expected = ClassicUnauthorizedException.class)
    public void testReplies() throws Exception {
        Course course = new Course("course");
        CourseNotes cn = new CourseNotes("title", "url");
    	int courseId = courseController.addCourse(course, profId);
        int courseNotesId = courseNotesController.addCourseNotes(courseId, cn, adminId);
        
        Comment comment = new Comment("body", true);
        List<Location> locations = new ArrayList<>();
        locations.add(new Location(20, 1, 2, 3, 4));
        CourseNotesReference cnRef = new CourseNotesReference(cn, locations);
        List<CourseNotesReference> refs = new ArrayList<>();
        refs.add(cnRef);
        int commentId = commentController.addCourseNotesComment(courseNotesId, comment, refs, userId);
        
        Reply reply = new Reply("body2");
        int replyId = commentController.addReplytoComment(commentId, reply, userId);
        commentController.updateReply(replyId, reply, userId2);
    }
    
    @Test(expected = ClassicUnauthorizedException.class)
    public void testCourseNotesRefs() throws Exception {
        Course course = new Course("course");
        CourseNotes cn = new CourseNotes("title", "url");
    	int courseId = courseController.addCourse(course, profId);
        int courseNotesId = courseNotesController.addCourseNotes(courseId, cn, adminId);
        
        Comment comment = new Comment("body", true);
        List<Location> locations = new ArrayList<>();
        locations.add(new Location(20, 1, 2, 3, 4));
        CourseNotesReference cnRef = new CourseNotesReference(cn, locations);
        List<CourseNotesReference> refs = new ArrayList<>();
        refs.add(cnRef);
        int commentId = commentController.addCourseNotesComment(courseNotesId, comment, refs, userId);
        
        List<Location> locations2 = new ArrayList<>();
        locations2.add(new Location(10, 2, 3, 4, 5));
        CourseNotesReference expResult2 = new CourseNotesReference(cn, locations2);
        courseNotesReferenceController.addCourseNotesReference(courseNotesId, commentId, expResult2, userId2);
    }
    
    @Test(expected = ClassicUnauthorizedException.class)
    public void testVideoRefs() throws Exception {
        Course course = new Course("course");
    	int courseId = courseController.addCourse(course, profId);
    	Video cn = new Video("title", "url", 999);
        int cnId = videoController.addVideo(courseId, cn, adminId);
        Comment comment = new Comment("body", false);
        VideoReference expResult = new VideoReference(cn, 20);
        
        
        List<VideoReference> refs = new ArrayList<>();
        refs.add(expResult);
        int commentId = commentController.addVideoComment(cnId, comment, refs, userId);
        
        VideoReference expResult2 = new VideoReference(cn, 10);
        videoReferenceController.addVideoReference(cnId, commentId, expResult2, userId2);
    }
}
