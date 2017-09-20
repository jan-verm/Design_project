package controllers;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import courses.Course;
import courses.Lecture;
import courses.Role;
import courses.User;
import databaseaccess.exceptions.ClassicDatabaseException;
import databaseaccess.exceptions.ClassicUnauthorizedException;
import databaseaccess.implementation.CommentDao;
import databaseaccess.implementation.CourseDao;
import databaseaccess.implementation.CourseNotesDao;
import databaseaccess.implementation.CourseNotesReferenceDao;
import databaseaccess.implementation.LectureDao;
import databaseaccess.implementation.ReplyDao;
import databaseaccess.implementation.UserDao;
import databaseaccess.implementation.VideoDao;
import databaseaccess.implementation.VideoReferenceDao;
import databaseaccess.interfaces.ICommentDao;
import databaseaccess.interfaces.ICourseDao;
import databaseaccess.interfaces.ICourseNotesDao;
import databaseaccess.interfaces.ICourseNotesReferenceDao;
import databaseaccess.interfaces.ILectureDao;
import databaseaccess.interfaces.IReplyDao;
import databaseaccess.interfaces.IUserDao;
import databaseaccess.interfaces.IVideoDao;
import databaseaccess.interfaces.IVideoReferenceDao;
import models.Comment;
import models.CourseNotes;
import models.CourseNotesReference;
import models.Location;
import models.Reply;
import models.Video;
import models.VideoReference;
import org.junit.After;

public class OwnerShipIT {
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
    
    private final String TEST_DATABASE_CONFIG = "testdbconfig.properties";
    private IVideoDao videodao;
    private ICourseNotesDao courseNotesdao;
    private ICommentDao commentdao;
    
    //courses 
    private ICourseDao coursedao;
    private ILectureDao lecturedao;
    private IUserDao userdao;
    private int courseId;
    

    @Before
    public void setUp() throws ClassicDatabaseException {
        
        
        commentdao = new CommentDao(TEST_DATABASE_CONFIG);
        videodao = new VideoDao(TEST_DATABASE_CONFIG);
        courseNotesdao = new CourseNotesDao(TEST_DATABASE_CONFIG);
        IVideoReferenceDao videorefdao = new VideoReferenceDao(TEST_DATABASE_CONFIG);
        ICourseNotesReferenceDao coursenotesrefdao = new CourseNotesReferenceDao(TEST_DATABASE_CONFIG);
        IReplyDao replydao = new ReplyDao(TEST_DATABASE_CONFIG);
        
        coursedao = new CourseDao(TEST_DATABASE_CONFIG);
        lecturedao = new LectureDao(TEST_DATABASE_CONFIG);
        userdao = new UserDao(TEST_DATABASE_CONFIG);
        courseController = new CourseController(coursedao, lecturedao, videodao, courseNotesdao, userdao, "/var/www/classic/resources/courses");
        userController = new UserController(userdao);
        
        commentController = new CommentController(commentdao, replydao, videorefdao, coursenotesrefdao, userdao);
        videoController = new VideoController(videodao, coursedao, userdao);
        courseNotesController = new CourseNotesController(courseNotesdao, coursedao, userdao, "/var/www/classic/resources/courses");
        videoReferenceController = new VideoReferenceController(videorefdao, commentdao, userdao);
        courseNotesReferenceController = new CourseNotesReferenceController(coursenotesrefdao, commentdao, userdao);
        
        profId = userController.addUser(new User("teacher", Role.TEACHER, "password"));
        userId = userController.addUser(new User("student", Role.STUDENT, "password"));
        userId2 = userController.addUser(new User("student2", Role.STUDENT, "password"));
        adminId = userController.addUser(new User("admin", Role.ADMIN, "password"));
    }
    
    @After
    public void tearDown() throws ClassicDatabaseException {
        videodao.cleanTable();
        courseNotesdao.cleanTable();
        commentdao.cleanTable();
        coursedao.cleanTable();
        userdao.cleanTable();
    }
    
    @Test(expected = ClassicUnauthorizedException.class)
    public void testUser() throws Exception {
        userController.deleteUser(userId, userId2);
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
