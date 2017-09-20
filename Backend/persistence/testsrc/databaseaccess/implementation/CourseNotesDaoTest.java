/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package databaseaccess.implementation;

import courses.Course;
import courses.Role;
import courses.User;
import databaseaccess.exceptions.ClassicDatabaseException;
import databaseaccess.exceptions.ClassicNotFoundException;
import java.util.List;
import models.CourseNotes;
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
public class CourseNotesDaoTest {

    private CourseNotesDao coursenotesDao;
    private CourseNotesDao fakedao;
    private CourseDao courseDao;
    private UserDao userDao;
    
    private User user;
    private Course course;

    public CourseNotesDaoTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() throws ClassicDatabaseException {
        coursenotesDao = new CourseNotesDao(CourseNotesDao.TEST_DATABASE_CONFIG);
        fakedao = new CourseNotesDao("fakedbconfig.properties");
        courseDao = new CourseDao(CourseDao.TEST_DATABASE_CONFIG);
        userDao = new UserDao(UserDao.TEST_DATABASE_CONFIG);
        
        user = new User("testcoursenotesuser", Role.NONE, "password");
        userDao.addUser(user);
        course = new Course("testcoursenotescourse");
        course.setOwner(user);
        courseDao.addCourse(course);
    }

    @After
    public void tearDown() throws ClassicDatabaseException {
        coursenotesDao.cleanTable();
        courseDao.cleanTable();
        userDao.cleanTable();
    }

    /**
     * Test of addCourseNotes method, of class CourseNotesDao.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testAddCourseNotes() throws Exception {        
        CourseNotes expResult = new CourseNotes("testAddCourseNotes", "testAddCourseNotesUrl");
        int expResultID = coursenotesDao.addCourseNotes(course.getId(), expResult);
        CourseNotes result = coursenotesDao.getCourseNotes(expResultID);
        assertEquals(expResultID, result.getId());
        assertEquals(expResult.getTitle(), result.getTitle());
        assertEquals(expResult.getUrl(), result.getUrl());
    }

    @Test(expected = ClassicDatabaseException.class)
    public void testAddCourseNotesDBException() throws Exception {
        CourseNotes expResult = new CourseNotes("testAddCourseNotes", "testAddCourseNotesUrl");
        fakedao.addCourseNotes(0, expResult);
    }
    
    @Test(expected = ClassicNotFoundException.class)
    public void testAddCourseNotesNFException() throws Exception {
        CourseNotes expResult = new CourseNotes("testAddCourseNotes", "testAddCourseNotesUrl");
        coursenotesDao.addCourseNotes(0, expResult);
    }

    /**
     * Test of deleteCourseNotes method, of class CourseNotesDao.
     *
     * @throws java.lang.Exception
     */
    @Test(expected = ClassicNotFoundException.class)
    public void testDeleteCourseNotes() throws Exception {        
        CourseNotes expResult = new CourseNotes("testDeleteCourseNotes", "testDeleUrl");
        int expResultID = coursenotesDao.addCourseNotes(course.getId(), expResult);
        CourseNotes result = coursenotesDao.getCourseNotes(expResultID);
        assertEquals(expResultID, result.getId());
        assertEquals(expResult.getTitle(), result.getTitle());
        assertEquals(expResult.getUrl(), result.getUrl());
        coursenotesDao.deleteCourseNotes(expResultID);
        CourseNotes courseNotes = coursenotesDao.getCourseNotes(expResultID);
    }

    @Test(expected = ClassicDatabaseException.class)
    public void testDeleteCourseNotesDBException() throws ClassicDatabaseException, ClassicNotFoundException {
        fakedao.deleteCourseNotes(0);
    }

    @Test(expected = ClassicNotFoundException.class)
    public void testDeleteCourseNotesNotFoundException() throws ClassicDatabaseException, ClassicNotFoundException {
        coursenotesDao.deleteCourseNotes(0);
    }

    /**
     * Test of getAllCourseNotes method, of class CourseNotesDao.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testGetCourseNotes_0args() throws Exception {        
        CourseNotes expResult = new CourseNotes("testGetCourseNotes", "testGetCourseNotesUrl");
        int expResultID = coursenotesDao.addCourseNotes(course.getId(), expResult);
        List<CourseNotes> results = coursenotesDao.getAllCourseNotes();
        CourseNotes result = null;
        for (CourseNotes r : results) {
            if (r.getId() == expResultID) {
                result = r;
            }
        }
        assertEquals(expResultID, result.getId());
        assertEquals(expResult.getTitle(), result.getTitle());
        assertEquals(expResult.getUrl(), result.getUrl());
    }

    @Test(expected = ClassicDatabaseException.class)
    public void testGetCourseNotesDBException() throws ClassicDatabaseException, ClassicNotFoundException {
        fakedao.getCourseNotes(0);
    }

    @Test(expected = ClassicNotFoundException.class)
    public void testGetCourseNotesNotFoundException() throws ClassicDatabaseException, ClassicNotFoundException {
        coursenotesDao.getCourseNotes(0);
    }

    /**
     * Test of getAllCourseNotes method, of class CourseNotesDao.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testGetCourseNotes_int() throws Exception {        
        CourseNotes expResult = new CourseNotes("testGetCourseNote", "testGetCourseNoteUrl");
        int expResultID = coursenotesDao.addCourseNotes(course.getId(), expResult);
        CourseNotes result = coursenotesDao.getCourseNotes(expResultID);
        assertEquals(expResultID, result.getId());
        assertEquals(expResult.getTitle(), result.getTitle());
        assertEquals(expResult.getUrl(), result.getUrl());
    }

    @Test(expected = ClassicDatabaseException.class)
    public void testGetCourseNotes_intDBException() throws ClassicDatabaseException, ClassicNotFoundException {
        fakedao.getAllCourseNotes();
    }

    @Test
    public void testGetParentCourseNotes() throws Exception {        
        CourseNotes expResult = new CourseNotes("testGetCourseNotes", "testGetCourseNotesUrl");
        int expResultID = coursenotesDao.addCourseNotes(course.getId(), expResult);
        List<CourseNotes> results = coursenotesDao.getParentCourseNotes(course.getId());
        CourseNotes result = null;
        for (CourseNotes r : results) {
            if (r.getId() == expResultID) {
                result = r;
            }
        }
        assertEquals(expResultID, result.getId());
        assertEquals(expResult.getTitle(), result.getTitle());
        assertEquals(expResult.getUrl(), result.getUrl());
    }

    @Test(expected = ClassicDatabaseException.class)
    public void testGetParentCourseNotesDBException() throws ClassicDatabaseException, ClassicNotFoundException {
        fakedao.getParentCourseNotes(0);
    }

    @Test(expected = ClassicNotFoundException.class)
    public void testGetParentCourseNotesNFException() throws ClassicDatabaseException, ClassicNotFoundException {
        coursenotesDao.getParentCourseNotes(0);
    }

    @Test
    public void testIsCourseNotes() throws Exception {        
        CourseNotes expResult = new CourseNotes("testGetCourseNote", "testGetCourseNoteUrl");
        int expResultID = coursenotesDao.addCourseNotes(course.getId(), expResult);
        boolean result = coursenotesDao.isCourseNotes(expResultID);
        assertTrue(result);        
    }
    
    @Test(expected = ClassicDatabaseException.class)
    public void testIsCourseNotesDBException() throws ClassicDatabaseException, ClassicNotFoundException {
        fakedao.isCourseNotes(0);
    }

    /**
     * Test of updateCourseNotes method, of class CourseNotesDao.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testUpdateCourseNotes() throws Exception {        
        CourseNotes expResult = new CourseNotes("testGetCourseNotes", "testGetCourseNotesUrl");
        int expResultID = coursenotesDao.addCourseNotes(course.getId(), expResult);
        CourseNotes result = coursenotesDao.getCourseNotes(expResultID);
        assertEquals(expResultID, result.getId());
        assertEquals(expResult.getTitle(), result.getTitle());
        assertEquals(expResult.getUrl(), result.getUrl());
        expResult.setId(expResultID);
        expResult.setTitle("testUpdateCourseNotes");
        expResult.setUrl("testUpdateCourseNotesUrl");
        coursenotesDao.updateCourseNotes(expResult);
        CourseNotes result2 = coursenotesDao.getCourseNotes(expResultID);
        assertEquals(expResultID, result2.getId());
        assertEquals(expResult.getTitle(), result2.getTitle());
        assertEquals(expResult.getUrl(), result2.getUrl());
    }

    @Test(expected = ClassicDatabaseException.class)
    public void testUpdateCourseNotesDBException() throws ClassicDatabaseException, ClassicNotFoundException {
        fakedao.updateCourseNotes(new CourseNotes("title", "url"));
    }

    @Test(expected = ClassicNotFoundException.class)
    public void testUpdateCourseNotesNotFoundException() throws ClassicDatabaseException, ClassicNotFoundException {
        coursenotesDao.updateCourseNotes(new CourseNotes("title", "url"));
    }

    /**
     * Test of cleanTable method, of class CourseNotesDao.
     *
     * @throws java.lang.Exception
     */
    @Test(expected = ClassicNotFoundException.class)
    public void testCleanTable() throws Exception {        
        CourseNotes expResult = new CourseNotes("testCleanTable", "testCleanTable");
        int expResultID = coursenotesDao.addCourseNotes(course.getId(), expResult);
        coursenotesDao.cleanTable();
        courseDao.cleanTable();
        coursenotesDao.getCourseNotes(expResultID);
    }

    @Test(expected = ClassicDatabaseException.class)
    public void testCleanTableDBException() throws ClassicDatabaseException, ClassicNotFoundException {
        fakedao.cleanTable();
    }

    @Test
    public void testGetOwner() throws Exception {
        CourseNotes cn = new CourseNotes("testAddCourseNotes", "testAddCourseNotesUrl");
        int cnid = coursenotesDao.addCourseNotes(course.getId(), cn);                
        
        User result = coursenotesDao.getOwner(cnid);
        
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
        coursenotesDao.getOwner(0);
    }
}
