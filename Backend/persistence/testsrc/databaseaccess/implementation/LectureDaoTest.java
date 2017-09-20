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
import java.util.List;
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
public class LectureDaoTest {

    private LectureDao lectureDao;
    private CourseDao courseDao;
    private LectureDao fakedao;
    private UserDao userDao;
    
    private User user;
    private Course course;
    
    public LectureDaoTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() throws ClassicDatabaseException {
        lectureDao = new LectureDao(LectureDao.TEST_DATABASE_CONFIG);
        courseDao = new CourseDao(CourseDao.TEST_DATABASE_CONFIG);
        fakedao = new LectureDao("fakedbconfig.properties");
        userDao = new UserDao(userDao.TEST_DATABASE_CONFIG);
        
        user = new User("testlectureuser", Role.NONE, "password");
        userDao.addUser(user);
        course = new Course("testlecture");
        course.setOwner(user);
        courseDao.addCourse(course);
    }

    @After
    public void tearDown() throws ClassicDatabaseException {
        lectureDao.cleanTable();
        courseDao.cleanTable();
        userDao.cleanTable();
    }

    /**
     * Test of addLecture method, of class LectureDao.
     */
    @Test
    public void testAddLecture() throws Exception {        
        Lecture expResult = new Lecture("testAddLecture");
        int expResultID = lectureDao.addLecture(course.getId(), expResult);
        Lecture result = lectureDao.getLecture(expResultID);
        assertEquals(expResultID, result.getId());
        assertEquals(expResult.getName(), result.getName());
    }

    /**
     * Test of addLecture method throwing ClassicNotFoundException, of class
     * LectureDao.
     *
     * @throws Exception
     */
    @Test(expected = ClassicNotFoundException.class)
    public void testAddLectureClassicNFException() throws Exception {
        Lecture expResult = new Lecture("testAddLecture");
        int expResultID = lectureDao.addLecture(0, expResult);
    }

    /**
     * Test of addLecture method throwing ClassicDatabaseException, of class
     * LectureDao.
     *
     * @throws Exception
     */
    @Test(expected = ClassicDatabaseException.class)
    public void testAddLectureClassicDbException() throws Exception {
        Lecture expResult = new Lecture("testAddLecture");
        int expResultID = fakedao.addLecture(0, expResult);
    }

    /**
     * Test of deleteLecture method, of class LectureDao.
     *
     * @throws java.lang.Exception
     */
    @Test(expected = ClassicNotFoundException.class)
    public void testDeleteLecture() throws Exception {        
        Lecture expResult = new Lecture("testAddLecture");
        int expResultID = lectureDao.addLecture(course.getId(), expResult);
        Lecture result = lectureDao.getLecture(expResultID);
        assertEquals(expResultID, result.getId());
        assertEquals(expResult.getName(), result.getName());
        lectureDao.deleteLecture(expResultID);
        lectureDao.getLecture(expResultID);
    }

    /**
     * Test of deleteLecture method throwing ClassicNotFoundException, of class
     * LectureDao.
     *
     * @throws Exception
     */
    @Test(expected = ClassicNotFoundException.class)
    public void testDeleteLectureClassicNFException() throws Exception {
        lectureDao.deleteLecture(0);
    }

    /**
     * Test of deleteLecture method throwing ClassicDatabaseException, of class
     * LectureDao.
     *
     * @throws Exception
     */
    @Test(expected = ClassicDatabaseException.class)
    public void testDeleteLectureClassicDbException() throws Exception {
        fakedao.deleteLecture(0);
    }

    /**
     * Test of getLecture method, of class LectureDao.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testGetLecture() throws Exception {        
        Lecture expResult = new Lecture("testAddLecture");
        int expResultID = lectureDao.addLecture(course.getId(), expResult);
        Lecture result = lectureDao.getLecture(expResultID);
        assertEquals(expResultID, result.getId());
        assertEquals(expResult.getName(), result.getName());
    }

    /**
     * Test of getLecture method throwing ClassicNotFoundException, of class
     * LectureDao.
     *
     * @throws Exception
     */
    @Test(expected = ClassicNotFoundException.class)
    public void testGetLectureClassicNFException() throws Exception {
        lectureDao.getLecture(0);
    }

    /**
     * Test of getLecture method throwing ClassicDatabaseException, of class
     * LectureDao.
     *
     * @throws Exception
     */
    @Test(expected = ClassicDatabaseException.class)
    public void testGetLectureClassicDbException() throws Exception {
        fakedao.getLecture(0);
    }

    /**
     * Test of getLectures method, of class LectureDao.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testGetLectures() throws Exception {        
        Lecture expResult = new Lecture("testAddLecture");
        int expResultID = lectureDao.addLecture(course.getId(), expResult);
        List<Lecture> results = lectureDao.getLectures(course.getId());
        assertEquals(expResultID, results.get(0).getId());
        assertEquals(expResult.getName(), results.get(0).getName());
    }

    /**
     * Test of getLectures method throwing ClassicNotFoundException, of class
     * LectureDao.
     *
     * @throws Exception
     */
    @Test(expected = ClassicNotFoundException.class)
    public void testGetLecturesClassicNFException() throws Exception {
        lectureDao.getLectures(0);
    }

    /**
     * Test of getLectures method throwing ClassicDatabaseException, of class
     * LectureDao.
     *
     * @throws Exception
     */
    @Test(expected = ClassicDatabaseException.class)
    public void testGetLecturesClassicDbException() throws Exception {
        fakedao.getLectures(0);
    }

    /**
     * Test of isLecture method, of class LectureDao.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testIsLecture() throws Exception {        
        Lecture expResult = new Lecture("testIsLecture");
        int expResultID = lectureDao.addLecture(course.getId(), expResult);
        boolean result = lectureDao.isLecture(expResultID);
        assertTrue(result);
    }
    
    /**
     * Test of isLecture method throwing ClassicDatabaseException, of class
     * LectureDao.
     *
     * @throws Exception
     */
    @Test(expected = ClassicDatabaseException.class)
    public void testIsLecturesClassicDbException() throws Exception {
        fakedao.isLecture(0);
    }

    /**
     * Test of updateLecture method, of class LectureDao.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testUpdateLecture() throws Exception {        
        Lecture expResult = new Lecture("testAddLecture");
        int expResultID = lectureDao.addLecture(course.getId(), expResult);
        Lecture lecture = lectureDao.getLecture(expResultID);
        assertEquals(expResultID, lecture.getId());
        assertEquals(expResult.getName(), lecture.getName());

        expResult.setName("testUpdateLecture");
        lectureDao.updateLecture(expResult);
        Lecture result = lectureDao.getLecture(expResultID);
        assertEquals(expResultID, result.getId());
        assertEquals(expResult.getName(), result.getName());
    }
    
    /**
     * Test of updateLecture method throwing ClassicNotFoundException, of class
     * LectureDao.
     *
     * @throws Exception
     */
    @Test(expected = ClassicNotFoundException.class)
    public void testUpdateLectureClassicNFException() throws Exception {
        Lecture lecture = new Lecture("");
        lectureDao.updateLecture(lecture);
    }

    /**
     * Test of updateLecture method throwing ClassicDatabaseException, of class
     * LectureDao.
     *
     * @throws Exception
     */
    @Test(expected = ClassicDatabaseException.class)
    public void testUpdateLectureClassicDbException() throws Exception {
        Lecture lecture = new Lecture("");
        fakedao.updateLecture(lecture);        
    }

    /**
     * Test of cleanTable method, of class LectureDao.
     */
    @Test(expected = ClassicNotFoundException.class)
    public void testCleanTable() throws Exception {        
        Lecture expResult = new Lecture("testAddLecture");
        int expResultID = lectureDao.addLecture(course.getId(), expResult);
        Lecture result = lectureDao.getLecture(expResultID);
        assertEquals(expResultID, result.getId());
        assertEquals(expResult.getName(), result.getName());
        lectureDao.cleanTable();
        lectureDao.getLecture(expResultID);
    }
    
    /**
     * Test of cleanTable method throwing ClassicDatabaseException, of class
     * LectureDao.
     *
     * @throws Exception
     */
    @Test(expected = ClassicDatabaseException.class)
    public void testCleanTableClassicDbException() throws Exception {        
        fakedao.cleanTable();        
    }
    
    @Test
    public void testGetOwner() throws Exception {
        Lecture lecture = new Lecture("testAddLecture");
        int lectureID = lectureDao.addLecture(course.getId(), lecture);
        User result = lectureDao.getOwner(lectureID);
        
        assertEquals(user.getId(), result.getId());
        assertEquals(user.getPassword(), result.getPassword());
        assertEquals(user.getRole(), result.getRole());
        assertEquals(user.getUsername(), result.getUsername());
    }

     @Test(expected=ClassicDatabaseException.class)
    public void testGetOwnerDBException() throws ClassicDatabaseException, ClassicNotFoundException {
        fakedao.getOwner(0);
    }
    
    @Test(expected=ClassicNotFoundException.class)
    public void testGetOwnerNFException() throws ClassicDatabaseException, ClassicNotFoundException {
        lectureDao.getOwner(0);
    }
}
