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
import databaseaccess.exceptions.ClassicNameTakenException;
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
public class CourseDaoTest {
    
    private CourseDao courseDao;
    private CourseDao fakedao;
    private UserDao userDao;
    private User user;
    
    public CourseDaoTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() throws ClassicDatabaseException {
        courseDao = new CourseDao(CourseDao.TEST_DATABASE_CONFIG);
        userDao = new UserDao(UserDao.TEST_DATABASE_CONFIG);
        fakedao = new CourseDao("fakedbconfig.properties");

        user = new User("coursetestuser", Role.NONE, "password");
        userDao.addUser(user);
    }
    
    @After
    public void tearDown() throws ClassicDatabaseException {
        courseDao.cleanTable();
        userDao.cleanTable();
    }

    /**
     * Test of addCourse method, of class CourseDao.
     */
    @Test
    public void testAddCourse() throws Exception {
        Course expResult = new Course("testAddCourse");
        expResult.setOwner(user);
        int expResultID = courseDao.addCourse(expResult);
        Course result = courseDao.getCourse(expResultID);
        assertEquals(expResultID, result.getId());
        assertEquals(expResult.getName(), result.getName());
    }
    
    /**
     * Test of addCourse method, of class CourseDao.
     */
    @Test(expected=ClassicNameTakenException.class)
    public void testAddCourseNameTakenException() throws Exception {
        Course expResult = new Course("testAddCourse");
        expResult.setOwner(user);
        courseDao.addCourse(expResult);
        courseDao.addCourse(expResult);
    }
    
    /**
     * Test of addCourse method throwing ClassicDatabaseException, of class
     * CourseDao.
     *
     * @throws Exception
     */
    @Test(expected = ClassicDatabaseException.class)
    public void testAddCourseClassicDbException() throws Exception {
        Course course = new Course("");
        fakedao.addCourse(course);        
    }

    /**
     * Test of deleteCourse method, of class CourseDao.
     */
    @Test(expected = ClassicNotFoundException.class)
    public void testDeleteCourse() throws Exception {
        Course expResult = new Course("testDeleteCourse");
        expResult.setOwner(user);
        int expResultID = courseDao.addCourse(expResult);
        Course result = courseDao.getCourse(expResultID);
        assertEquals(expResultID, result.getId());
        assertEquals(expResult.getName(), result.getName());
        courseDao.deleteCourse(expResultID);
        courseDao.getCourse(expResultID);
    }
    
    /**
     * Test of deleteCourse method throwing ClassicNotFoundException, of class
     * CourseDao.
     *
     * @throws Exception
     */
    @Test(expected = ClassicNotFoundException.class)
    public void testDeleteCourseClassicNFException() throws Exception {        
        courseDao.deleteCourse(0);        
    }
    
    /**
     * Test of deleteCourse method throwing ClassicDatabaseException, of class
     * CourseDao.
     *
     * @throws Exception
     */
    @Test(expected = ClassicDatabaseException.class)
    public void testDeleteCourseClassicDbException() throws Exception {        
        fakedao.deleteCourse(0);        
    }

    /**
     * Test of getCourse method, of class CourseDao.
     */
    @Test
    public void testGetCourse() throws Exception {
        Course expResult = new Course("testGetCourse");
        expResult.setOwner(user);
        int expResultID = courseDao.addCourse(expResult);
        Course result = courseDao.getCourse(expResultID);
        assertEquals(expResultID, result.getId());
        assertEquals(expResult.getName(), result.getName());
    }
    
    /**
     * Test of getCourse method throwing ClassicNotFoundException, of class
     * CourseDao.
     *
     * @throws Exception
     */
    @Test(expected = ClassicNotFoundException.class)
    public void testGetCourseClassicNFException() throws Exception {        
        courseDao.getCourse(0);        
    }
    
    /**
     * Test of getCourse method throwing ClassicDatabaseException, of class
     * CourseDao.
     *
     * @throws Exception
     */
    @Test(expected = ClassicDatabaseException.class)
    public void testGetCourseClassicDbException() throws Exception {        
        fakedao.getCourse(0);        
    }

    /**
     * Test of getCourses method, of class CourseDao.
     */
    @Test
    public void testGetCourses() throws Exception {
        Course expResult = new Course("testGetCourses");
        expResult.setOwner(user);
        int expResultID = courseDao.addCourse(expResult);
        List<Course> results = courseDao.getCourses();        
        assertEquals(expResultID, results.get(0).getId());
        assertEquals(expResult.getName(), results.get(0).getName());
    }
    
    /**
     * Test of getCourses method throwing ClassicDatabaseException, of class
     * CourseDao.
     *
     * @throws Exception
     */
    @Test(expected = ClassicDatabaseException.class)
    public void testGetCoursesClassicDbException() throws Exception {        
        fakedao.getCourses();        
    }
    
     /**
     * Test of isCourse method, of class CourseDao.
     */
    @Test
    public void testIsCourse() throws Exception {
        Course expResult = new Course("testIsCourse");
        expResult.setOwner(user);
        int expResultID = courseDao.addCourse(expResult);
        boolean result = courseDao.isCourse(expResultID);        
        assertTrue(result);
    }
    
     /**
     * Test of isCourse method throwing ClassicDatabaseException, of class
     * CourseDao.
     *
     * @throws Exception
     */
    @Test(expected = ClassicDatabaseException.class)
    public void testIsCourseClassicDbException() throws Exception {        
        fakedao.isCourse(0);        
    }

    /**
     * Test of updateCourse method, of class CourseDao.
     */
    @Test
    public void testUpdateCourse() throws Exception {
        Course expResult = new Course("testGetCourse");
        expResult.setOwner(user);
        int expResultID = courseDao.addCourse(expResult);
        Course course = courseDao.getCourse(expResultID);
        assertEquals(expResultID, course.getId());
        assertEquals(expResult.getName(), course.getName());
        
        expResult.setName("testUpdateCourse");
        courseDao.updateCourse(expResult);
        Course result = courseDao.getCourse(expResultID);
        assertEquals(expResultID, result.getId());
        assertEquals(expResult.getName(), result.getName());
    }
    
    /**
     * Test of updateCourse method throwing ClassicNotFoundException, of class
     * CourseDao.
     *
     * @throws Exception
     */
    @Test(expected = ClassicNotFoundException.class)
    public void testUpdateCourseClassicNFException() throws Exception {        
        Course course = new Course("testUpdateCourse");
        courseDao.updateCourse(course);        
    }
    
    /**
     * Test of updateCourse method throwing ClassicDatabaseException, of class
     * CourseDao.
     *
     * @throws Exception
     */
    @Test(expected = ClassicDatabaseException.class)
    public void testUpdateCourseClassicDbException() throws Exception {  
        Course course = new Course("testUpdateCourse");
        fakedao.updateCourse(course); 
    }

    /**
     * Test of cleanTable method, of class CourseDao.
     */
    @Test(expected = ClassicNotFoundException.class)
    public void testCleanTable() throws Exception {
        Course expResult = new Course("testDeleteCourse");
        expResult.setOwner(user);
        int expResultID = courseDao.addCourse(expResult);
        Course result = courseDao.getCourse(expResultID);
        assertEquals(expResultID, result.getId());
        assertEquals(expResult.getName(), result.getName());
        courseDao.cleanTable();
        courseDao.getCourse(expResultID);
    }
    
    /**
     * Test of cleanTable method throwing ClassicDatabaseException, of class
     * CourseDao.
     *
     * @throws Exception
     */
    @Test(expected = ClassicDatabaseException.class)
    public void testCleanTableClassicDbException() throws Exception {
        fakedao.cleanTable(); 
    }
    
}
