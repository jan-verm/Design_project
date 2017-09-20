/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package databaseaccess.implementation;

import courses.Course;
import courses.Lecture;
import courses.Role;
import courses.User;
import databaseaccess.exceptions.ClassicDatabaseException;
import databaseaccess.exceptions.ClassicNotFoundException;
import java.util.ArrayList;
import java.util.List;
import models.Comment;
import models.CourseNotes;
import models.CourseNotesReference;
import models.Location;
import models.Video;
import models.VideoReference;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author jorsi
 */
public class CourseNotesReferenceDaoTest {

    private CourseDao courseDao;
    private CourseNotesReferenceDao courseNotesReferenceDao;
    private CourseNotesDao courseNotesDao;
    private CommentDao commentDao;
    private UserDao userDao;    
    private LectureDao lectureDao;

    private CourseNotesReferenceDao fakedao;
    
    private User user;
    private Course course;
    private Lecture lecture;

    public CourseNotesReferenceDaoTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() throws ClassicDatabaseException, ClassicNotFoundException {
        courseNotesReferenceDao = new CourseNotesReferenceDao(CourseNotesReferenceDao.TEST_DATABASE_CONFIG);
        courseNotesDao = new CourseNotesDao(CourseNotesDao.TEST_DATABASE_CONFIG);
        commentDao = new CommentDao(CommentDao.TEST_DATABASE_CONFIG);
        courseDao = new CourseDao(CourseDao.TEST_DATABASE_CONFIG);
        userDao = new UserDao(UserDao.TEST_DATABASE_CONFIG);
        lectureDao = new LectureDao(LectureDao.TEST_DATABASE_CONFIG);
        
        fakedao = new CourseNotesReferenceDao("fakedbconfig.properties");
        
        user = new User("test", Role.NONE, "test");
        userDao.addUser(user);
        course = new Course("testcoursenotesref");
        course.setOwner(user);
        courseDao.addCourse(course);
        lecture = new Lecture("testcoursenotesreflecture");        
        lectureDao.addLecture(course.getId(), lecture);        
    }

    @After
    public void tearDown() throws ClassicDatabaseException {
        courseNotesReferenceDao.cleanTable();
        courseNotesDao.cleanTable();
        commentDao.cleanTable();
        courseDao.cleanTable();
        userDao.cleanTable();
    }
    
    @Test
    public void testGetCourseAndLectureForVideo() throws Exception {
        CourseNotes cn = new CourseNotes("testAddCourseNotesReference", "testAddCourseNotesReferenceUrl");
        int cnid = courseNotesDao.addCourseNotes(course.getId(), cn);

        Comment c = new Comment("testAddVideoReferenceBody", false);
        c.setUser(user);
        int cid = commentDao.addComment(c);

        List<Location> locations = new ArrayList<>();
        locations.add(new Location(5, 10, 20, 15, 5));
        CourseNotesReference refTest = new CourseNotesReference(cn, locations);
        int crid = courseNotesReferenceDao.addCourseNotesReference(cnid, cid, refTest);
        
        int[] result = courseNotesReferenceDao.getCourseAndLectureForCourseNotes(cnid);
        assertEquals(course.getId(), result[0]);
        assertEquals(0, result[1]);        
        
        CourseNotes cn2 = new CourseNotes("testAddCourseNotesReference", "testAddCourseNotesReferenceUrl");
        int cnid2 = courseNotesDao.addCourseNotes(lecture.getId(), cn2);

        Comment c2 = new Comment("testAddVideoReferenceBody", false);
        c2.setUser(user);
        int cid2 = commentDao.addComment(c2);

        List<Location> locations2 = new ArrayList<>();
        locations2.add(new Location(5, 10, 20, 15, 5));
        CourseNotesReference refTest2 = new CourseNotesReference(cn2, locations2);
        int crid2 = courseNotesReferenceDao.addCourseNotesReference(cnid2, cid2, refTest2);
        
        int[] result2 = courseNotesReferenceDao.getCourseAndLectureForCourseNotes(cnid2);
        assertEquals(course.getId(), result2[0]);
        assertEquals(lecture.getId(), result2[1]);
    }

    @Test(expected = ClassicDatabaseException.class)
    public void testGetCourseAndLectureForVideoDBException() throws Exception {
        fakedao.getCourseAndLectureForCourseNotes(0);
    }

    @Test(expected = ClassicNotFoundException.class)
    public void testGetCourseAndLectureForVideoNFException() throws Exception {
        courseNotesReferenceDao.getCourseAndLectureForCourseNotes(0);
    }
    
    @Test
    public void testAddCourseNotesReference() throws Exception {        
        Course course = new Course("test");
        course.setOwner(user);
        int parentID = courseDao.addCourse(course);
        CourseNotes cn = new CourseNotes("testAddCourseNotesReference", "testAddCourseNotesReferenceUrl");
        int cnid = courseNotesDao.addCourseNotes(parentID, cn);

        Comment c = new Comment("testAddCourseNotesReference", false);
        c.setUser(user);
        int cid = commentDao.addComment(c);

        List<Location> locations = new ArrayList<>();
        locations.add(new Location(5, 10, 20, 15, 5));
        CourseNotesReference refTest = new CourseNotesReference(cn, locations);
        int crid = courseNotesReferenceDao.addCourseNotesReference(cnid, cid, refTest);
        CourseNotesReference refResult = courseNotesReferenceDao.getCourseNotesReference(crid);

        assertEquals(refResult.getCourseNotes().getId(), cnid);
        assertEquals(refResult.getLocations().get(0).getPagenumber(), refTest.getLocations().get(0).getPagenumber());
        assertEquals(refResult.getLocations().get(0).getX1(), refTest.getLocations().get(0).getX1(), 0.0001);
        assertEquals(refResult.getLocations().get(0).getX2(), refTest.getLocations().get(0).getX2(), 0.0001);
        assertEquals(refResult.getLocations().get(0).getY1(), refTest.getLocations().get(0).getY1(), 0.0001);
        assertEquals(refResult.getLocations().get(0).getY2(), refTest.getLocations().get(0).getY2(), 0.0001);
    }

    @Test(expected = ClassicNotFoundException.class)
    public void testAddCourseNotesReferenceNFException1() throws Exception {
        Course course = new Course("test");
        course.setOwner(user);
        int parentID = courseDao.addCourse(course);
        CourseNotes cn = new CourseNotes("testAddCourseNotesReference", "testAddCourseNotesReferenceUrl");
        int cnid = courseNotesDao.addCourseNotes(parentID,cn);

        Comment c = new Comment("testAddCourseNotesReference", false);
        c.setUser(user);
        int cid = commentDao.addComment(c);

        List<Location> locations = new ArrayList<>();
        locations.add(new Location(5, 10, 20, 15, 5));
        CourseNotesReference refTest = new CourseNotesReference(cn, locations);
        int crid = courseNotesReferenceDao.addCourseNotesReference(0, cid, refTest);
    }

    @Test(expected = ClassicNotFoundException.class)
    public void testAddCourseNotesReferenceNFException2() throws Exception {
        Course course = new Course("test");
        course.setOwner(user);        
        int parentID = courseDao.addCourse(course);
        CourseNotes cn = new CourseNotes("testAddCourseNotesReference", "testAddCourseNotesReferenceUrl");
        int cnid = courseNotesDao.addCourseNotes(parentID, cn);

        List<Location> locations = new ArrayList<>();
        locations.add(new Location(5, 10, 20, 15, 5));
        CourseNotesReference refTest = new CourseNotesReference(cn, locations);
        int crid = courseNotesReferenceDao.addCourseNotesReference(cnid, 0, refTest);
    }

    @Test(expected = ClassicDatabaseException.class)
    public void testAddCourseNotesReferenceDBException() throws Exception {
        CourseNotes cn = new CourseNotes("testAddCourseNotesReference", "testAddCourseNotesReferenceUrl");
        List<Location> locations = new ArrayList<>();
        locations.add(new Location(5, 10, 20, 15, 5));
        CourseNotesReference refTest = new CourseNotesReference(cn, locations);
        int crid = fakedao.addCourseNotesReference(0, 0, refTest);
    }

    @Test(expected = ClassicNotFoundException.class)
    public void testDeleteCourseNotesReference() throws Exception {
        Course course = new Course("test");
        course.setOwner(user);
        int parentID = courseDao.addCourse(course);
        CourseNotes cn = new CourseNotes("testAddCourseNotesReference", "testAddCourseNotesReferenceUrl");
        int cnid = courseNotesDao.addCourseNotes(parentID, cn);

        Comment c = new Comment("testAddCourseNotesReference", false);
        c.setUser(user);
        int cid = commentDao.addComment(c);

        List<Location> locations = new ArrayList<>();
        locations.add(new Location(5, 10, 20, 15, 5));
        CourseNotesReference refTest = new CourseNotesReference(cn, locations);
        int crid = courseNotesReferenceDao.addCourseNotesReference(cnid, cid, refTest);
        CourseNotesReference refResult = courseNotesReferenceDao.getCourseNotesReference(crid);

        assertEquals(refResult.getCourseNotes().getId(), cnid);
        assertEquals(refResult.getLocations().get(0).getPagenumber(), refTest.getLocations().get(0).getPagenumber());
        assertEquals(refResult.getLocations().get(0).getX1(), refTest.getLocations().get(0).getX1(), 0.0001);
        assertEquals(refResult.getLocations().get(0).getX2(), refTest.getLocations().get(0).getX2(), 0.0001);
        assertEquals(refResult.getLocations().get(0).getY1(), refTest.getLocations().get(0).getY1(), 0.0001);
        assertEquals(refResult.getLocations().get(0).getY2(), refTest.getLocations().get(0).getY2(), 0.0001);

        courseNotesReferenceDao.deleteCourseNotesReference(crid);
        courseNotesReferenceDao.getCourseNotesReference(crid);
    }

    @Test(expected = ClassicDatabaseException.class)
    public void testDeleteCourseNotesReferenceDBException() throws ClassicDatabaseException, ClassicNotFoundException {
        fakedao.deleteCourseNotesReference(0);
    }

    @Test(expected = ClassicNotFoundException.class)
    public void testDeleteCourseNotesReferenceNotFoundException() throws ClassicDatabaseException, ClassicNotFoundException {
        courseNotesReferenceDao.deleteCourseNotesReference(0);
    }

    @Test
    public void testGetSelfCourseNotesReferences() throws Exception {
        Course course = new Course("test");
        course.setOwner(user);
        int parentID = courseDao.addCourse(course);
        CourseNotes cn = new CourseNotes("testAddCourseNotesReference", "testAddCourseNotesReferenceUrl");
        int cnid = courseNotesDao.addCourseNotes(parentID, cn);

        Comment c = new Comment("testAddCourseNotesReference", false);
        c.setUser(user);
        int cid = commentDao.addComment(c);

        List<Location> locations = new ArrayList<>();
        locations.add(new Location(5, 10, 20, 15, 5));
        CourseNotesReference refTest = new CourseNotesReference(cn, locations);
        int crid = courseNotesReferenceDao.addCourseNotesReference(cnid, cid, refTest);
        List<CourseNotesReference> refResults = courseNotesReferenceDao.getSelfCourseNotesReferences(cnid, cid);

        assertEquals(refResults.get(0).getCourseNotes().getId(), cnid);
        assertEquals(refResults.get(0).getLocations().get(0).getPagenumber(), refTest.getLocations().get(0).getPagenumber());
        assertEquals(refResults.get(0).getLocations().get(0).getX1(), refTest.getLocations().get(0).getX1(), 0.0001);
        assertEquals(refResults.get(0).getLocations().get(0).getX2(), refTest.getLocations().get(0).getX2(), 0.0001);
        assertEquals(refResults.get(0).getLocations().get(0).getY1(), refTest.getLocations().get(0).getY1(), 0.0001);
        assertEquals(refResults.get(0).getLocations().get(0).getY2(), refTest.getLocations().get(0).getY2(), 0.0001);
    }

    @Test(expected = ClassicNotFoundException.class)
    public void testGetSelfCourseNotesReferencesNFException1() throws Exception {
        Course course = new Course("test");
        course.setOwner(user);
        int parentID = courseDao.addCourse(course);
        CourseNotes cn = new CourseNotes("testAddCourseNotesReference", "testAddCourseNotesReferenceUrl");
        int cnid = courseNotesDao.addCourseNotes(parentID, cn);

        Comment c = new Comment("testAddCourseNotesReference", false);
        c.setUser(user);
        int cid = commentDao.addComment(c);

        List<Location> locations = new ArrayList<>();
        locations.add(new Location(5, 10, 20, 15, 5));
        CourseNotesReference refTest = new CourseNotesReference(cn, locations);
        courseNotesReferenceDao.getSelfCourseNotesReferences(0, cid);
    }

    @Test(expected = ClassicNotFoundException.class)
    public void testGetSelfCourseNotesReferencesNFException2() throws Exception {
        Course course = new Course("test");
        course.setOwner(user);
        int parentID = courseDao.addCourse(course);
        CourseNotes cn = new CourseNotes("testAddCourseNotesReference", "testAddCourseNotesReferenceUrl");
        int cnid = courseNotesDao.addCourseNotes(parentID,cn);

        List<Location> locations = new ArrayList<>();
        locations.add(new Location(5, 10, 20, 15, 5));
        CourseNotesReference refTest = new CourseNotesReference(cn, locations);
        courseNotesReferenceDao.getSelfCourseNotesReferences(cnid, 0);
    }

    @Test(expected = ClassicDatabaseException.class)
    public void testGetSelfCourseNotesReferencesDBException() throws ClassicDatabaseException, ClassicNotFoundException {
        fakedao.getSelfCourseNotesReferences(0, 0);
    }

    @Test
    public void testGetCourseNotesReference() throws Exception {
        Course course = new Course("test");
        course.setOwner(user);
        int parentID = courseDao.addCourse(course);
        CourseNotes cn = new CourseNotes("testAddCourseNotesReference", "testAddCourseNotesReferenceUrl");
        int cnid = courseNotesDao.addCourseNotes(parentID,cn);

        Comment c = new Comment("testAddCourseNotesReference", false);
        c.setUser(user);
        int cid = commentDao.addComment(c);

        List<Location> locations = new ArrayList<>();
        locations.add(new Location(5, 10, 20, 15, 5));
        CourseNotesReference refTest = new CourseNotesReference(cn, locations);
        int crid = courseNotesReferenceDao.addCourseNotesReference(cnid, cid, refTest);
        CourseNotesReference refResult = courseNotesReferenceDao.getCourseNotesReference(crid);

        assertEquals(refResult.getCourseNotes().getId(), cnid);
        assertEquals(refResult.getLocations().get(0).getPagenumber(), refTest.getLocations().get(0).getPagenumber());
        assertEquals(refResult.getLocations().get(0).getX1(), refTest.getLocations().get(0).getX1(), 0.0001);
        assertEquals(refResult.getLocations().get(0).getX2(), refTest.getLocations().get(0).getX2(), 0.0001);
        assertEquals(refResult.getLocations().get(0).getY1(), refTest.getLocations().get(0).getY1(), 0.0001);
        assertEquals(refResult.getLocations().get(0).getY2(), refTest.getLocations().get(0).getY2(), 0.0001);
    }

    @Test(expected = ClassicDatabaseException.class)
    public void testGetCourseNotesReferenceDBException() throws ClassicDatabaseException, ClassicNotFoundException {
        fakedao.getCourseNotesReference(0);
    }

    @Test(expected = ClassicNotFoundException.class)
    public void testGetCourseNotesReferenceNotFoundException() throws ClassicDatabaseException, ClassicNotFoundException {
        courseNotesReferenceDao.getCourseNotesReference(0);
    }

    @Test
    public void testGetCourseNotesReferences() throws Exception {
        Course course = new Course("test");
        course.setOwner(user);
        int parentID = courseDao.addCourse(course);
        CourseNotes cn = new CourseNotes("testAddCourseNotesReference", "testAddCourseNotesReferenceUrl");
        int cnid = courseNotesDao.addCourseNotes(parentID, cn);

        Comment c = new Comment("testAddCourseNotesReference", false);
        c.setUser(user);
        int cid = commentDao.addComment(c);

        List<Location> locations = new ArrayList<>();
        locations.add(new Location(5, 10, 20, 15, 5));
        CourseNotesReference refTest = new CourseNotesReference(cn, locations);
        int crid = courseNotesReferenceDao.addCourseNotesReference(cnid, cid, refTest);
        List<CourseNotesReference> refResults = courseNotesReferenceDao.getCourseNotesReferences(cid);

        assertEquals(refResults.get(0).getCourseNotes().getId(), cnid);
        assertEquals(refResults.get(0).getLocations().get(0).getPagenumber(), refTest.getLocations().get(0).getPagenumber());
        assertEquals(refResults.get(0).getLocations().get(0).getX1(), refTest.getLocations().get(0).getX1(), 0.0001);
        assertEquals(refResults.get(0).getLocations().get(0).getX2(), refTest.getLocations().get(0).getX2(), 0.0001);
        assertEquals(refResults.get(0).getLocations().get(0).getY1(), refTest.getLocations().get(0).getY1(), 0.0001);
        assertEquals(refResults.get(0).getLocations().get(0).getY2(), refTest.getLocations().get(0).getY2(), 0.0001);
    }

        @Test(expected = ClassicNotFoundException.class)
    public void testGetCourseNotesReferencesNFException1() throws Exception {        
        courseNotesReferenceDao.getCourseNotesReferences(0);
    }
    
    @Test(expected = ClassicDatabaseException.class)
    public void testGetCourseNotesReferencesDBException() throws ClassicDatabaseException, ClassicNotFoundException {
        fakedao.getCourseNotesReferences(0);
    }

    @Test
    public void testUpdateCourseNotesReference() throws Exception {     
        Course course = new Course("test");
        course.setOwner(user);
        int parentID = courseDao.addCourse(course);
        CourseNotes cn = new CourseNotes("testAddCourseNotesReference", "testAddCourseNotesReferenceUrl");
        int cnid = courseNotesDao.addCourseNotes(parentID, cn);
        
        Comment c = new Comment("testAddCourseNotesReference", false);
        c.setUser(user);
        int cid = commentDao.addComment(c);

        List<Location> locations = new ArrayList<>();
        locations.add(new Location(5, 10, 20, 15, 5));
        CourseNotesReference refTest = new CourseNotesReference(cn, locations);
        int crid = courseNotesReferenceDao.addCourseNotesReference(cnid, cid, refTest);
        CourseNotesReference refResult = courseNotesReferenceDao.getCourseNotesReference(crid);

        assertEquals(refResult.getCourseNotes().getId(), cnid);
        assertEquals(refResult.getLocations().get(0).getPagenumber(), refTest.getLocations().get(0).getPagenumber());
        assertEquals(refResult.getLocations().get(0).getX1(), refTest.getLocations().get(0).getX1(), 0.0001);
        assertEquals(refResult.getLocations().get(0).getX2(), refTest.getLocations().get(0).getX2(), 0.0001);
        assertEquals(refResult.getLocations().get(0).getY1(), refTest.getLocations().get(0).getY1(), 0.0001);
        assertEquals(refResult.getLocations().get(0).getY2(), refTest.getLocations().get(0).getY2(), 0.0001);

        refTest.setRefId(crid);
        refTest.getLocations().get(0).setPagenumber(10);
        refTest.getLocations().get(0).setX1(20);
        refTest.getLocations().get(0).setY1(2);
        refTest.getLocations().get(0).setX2(3);
        refTest.getLocations().get(0).setY2(5);
        courseNotesReferenceDao.updateCourseNotesReference(refTest);
        CourseNotesReference updateResult = courseNotesReferenceDao.getCourseNotesReference(crid);

        assertEquals(updateResult.getCourseNotes().getId(), cnid);
        assertEquals(updateResult.getLocations().get(0).getPagenumber(), refTest.getLocations().get(0).getPagenumber());
        assertEquals(updateResult.getLocations().get(0).getX1(), refTest.getLocations().get(0).getX1(), 0.0001);
        assertEquals(updateResult.getLocations().get(0).getX2(), refTest.getLocations().get(0).getX2(), 0.0001);
        assertEquals(updateResult.getLocations().get(0).getY1(), refTest.getLocations().get(0).getY1(), 0.0001);
        assertEquals(updateResult.getLocations().get(0).getY2(), refTest.getLocations().get(0).getY2(), 0.0001);
    }

    @Test(expected = ClassicDatabaseException.class)
    public void testUpdateCourseNotesReferenceDBException() throws ClassicDatabaseException, ClassicNotFoundException {
        CourseNotes cn = new CourseNotes("testAddCourseNotesReference", "testAddCourseNotesReferenceUrl");

        List<Location> locations = new ArrayList<>();
        locations.add(new Location(5, 10, 20, 15, 5));
        CourseNotesReference refTest = new CourseNotesReference(cn, locations);
        refTest.setRefId(0);
        fakedao.updateCourseNotesReference(refTest);
    }

    @Test(expected = ClassicNotFoundException.class)
    public void testUpdateCourseNotesReferenceNotFoundException() throws ClassicDatabaseException, ClassicNotFoundException {
        CourseNotes cn = new CourseNotes("testAddCourseNotesReference", "testAddCourseNotesReferenceUrl");
        List<Location> locations = new ArrayList<>();
        locations.add(new Location(5, 10, 20, 15, 5));
        CourseNotesReference refTest = new CourseNotesReference(cn, locations);
        refTest.setRefId(0);
        courseNotesReferenceDao.updateCourseNotesReference(refTest);
    }

    @Test(expected = ClassicNotFoundException.class)
    public void testCleanTable() throws Exception {
        Course course = new Course("test");
        course.setOwner(user);
        int parentID = courseDao.addCourse(course);
        CourseNotes cn = new CourseNotes("testAddCourseNotesReference", "testAddCourseNotesReferenceUrl");
        int cnid = courseNotesDao.addCourseNotes(parentID, cn);

        Comment c = new Comment("testAddCourseNotesReference", false);
        c.setUser(user);
        int cid = commentDao.addComment(c);

        List<Location> locations = new ArrayList<>();
        locations.add(new Location(5, 10, 20, 15, 5));
        CourseNotesReference refTest = new CourseNotesReference(cn, locations);
        int crid = courseNotesReferenceDao.addCourseNotesReference(cnid, cid, refTest);
        CourseNotesReference refResult = courseNotesReferenceDao.getCourseNotesReference(crid);

        assertEquals(refResult.getCourseNotes().getId(), cnid);
        assertEquals(refResult.getLocations().get(0).getPagenumber(), refTest.getLocations().get(0).getPagenumber());
        assertEquals(refResult.getLocations().get(0).getX1(), refTest.getLocations().get(0).getX1(), 0.0001);
        assertEquals(refResult.getLocations().get(0).getX2(), refTest.getLocations().get(0).getX2(), 0.0001);
        assertEquals(refResult.getLocations().get(0).getY1(), refTest.getLocations().get(0).getY1(), 0.0001);
        assertEquals(refResult.getLocations().get(0).getY2(), refTest.getLocations().get(0).getY2(), 0.0001);

        courseNotesReferenceDao.cleanTable();
        courseNotesReferenceDao.getCourseNotesReference(crid);

    }

    @Test(expected = ClassicDatabaseException.class)
    public void testCleanTableDBException() throws ClassicDatabaseException, ClassicNotFoundException {
        fakedao.cleanTable();
    }
}
