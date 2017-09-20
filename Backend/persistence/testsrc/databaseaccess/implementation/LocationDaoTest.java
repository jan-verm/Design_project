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
import java.util.ArrayList;
import java.util.List;
import models.Comment;
import models.CourseNotes;
import models.CourseNotesReference;
import models.Location;
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
public class LocationDaoTest {

    private LocationDao locationDao;
    private CourseNotesDao courseNotesDao;
    private CommentDao commentDao;
    private CourseNotesReferenceDao courseNotesReferenceDao;
    private CourseDao courseDao;
    private LocationDao fakedao;
    private UserDao userDao;
    
    private User user;
    private Course course;
    
    public LocationDaoTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() throws ClassicDatabaseException {
        locationDao = new LocationDao(LocationDao.TEST_DATABASE_CONFIG);
        courseNotesDao = new CourseNotesDao(CourseNotesDao.TEST_DATABASE_CONFIG);
        commentDao = new CommentDao(CommentDao.TEST_DATABASE_CONFIG);
        courseNotesReferenceDao = new CourseNotesReferenceDao(CourseNotesReferenceDao.TEST_DATABASE_CONFIG);
        courseDao = new CourseDao(CourseDao.TEST_DATABASE_CONFIG);
        fakedao = new LocationDao("fakedbconfig.properties");
        
        userDao = new UserDao(UserDao.TEST_DATABASE_CONFIG);
        
        user = new User("testlocationuser", Role.NONE, "password");
        userDao.addUser(user);
        course = new Course("testlocation");
        course.setOwner(user);
        courseDao.addCourse(course);
    }

    @After
    public void tearDown() throws ClassicDatabaseException {
        courseNotesDao.cleanTable();
        commentDao.cleanTable();
        courseNotesReferenceDao.cleanTable();
        locationDao.cleanTable();
        courseDao.cleanTable();
        userDao.cleanTable();
    }

    /**
     * Test of addLocation method, of class LocationDao.
     */
    @Test
    public void testAddLocation() throws Exception {
        CourseNotes courseNotes = new CourseNotes("title", "url");
        int coursenotesID = courseNotesDao.addCourseNotes(course.getId(), courseNotes);
        Comment comment = new Comment("body", false);
        comment.setUser(user);        
        int commentID = commentDao.addComment(comment);
        List<Location> locations = new ArrayList<>();
        CourseNotesReference courseNotesReference = new CourseNotesReference(courseNotes, locations);
        int refID = courseNotesReferenceDao.addCourseNotesReference(coursenotesID, commentID, courseNotesReference);

        Location expResult = new Location(1, 2, 3, 4, 10);
        int expResultID = locationDao.addLocation(refID, expResult);

        Location result = locationDao.getLocation(expResultID);
        assertEquals(expResultID, result.getId());
        assertEquals(expResult.getPagenumber(), result.getPagenumber());
        assertEquals(expResult.getX1(), result.getX1(), 0.0001);
        assertEquals(expResult.getY1(), result.getY1(), 0.0001);
        assertEquals(expResult.getX2(), result.getX2(), 0.0001);
        assertEquals(expResult.getY2(), result.getY2(), 0.0001);
    }
    
    /**
     * Test of addLocation method throwing ClassicDatabaseException, of class
     * LocationDao.
     *
     * @throws Exception
     */
    @Test(expected = ClassicDatabaseException.class)
    public void testAddLocationClassicDbException() throws Exception {
        Location location = new Location(1, 2, 3, 4, 10);
        fakedao.addLocation(0,location); 
    }

    /**
     * Test of updateLocations method, of class LocationDao.
     * @throws java.lang.Exception
     */
    @Test
    public void testUpdateLocations() throws Exception {        
        CourseNotes courseNotes = new CourseNotes("title", "url");
        int coursenotesID = courseNotesDao.addCourseNotes(course.getId(), courseNotes);
        Comment comment = new Comment("body", false);
        comment.setUser(user);
        int commentID = commentDao.addComment(comment);
        List<Location> locations = new ArrayList<>();
        CourseNotesReference courseNotesReference = new CourseNotesReference(courseNotes, locations);
        int refID = courseNotesReferenceDao.addCourseNotesReference(coursenotesID, commentID, courseNotesReference);

        Location expResult = new Location(1, 2, 3, 4, 10);
        int expResultID = locationDao.addLocation(refID, expResult);
        Location result = locationDao.getLocation(expResultID);
        assertEquals(expResultID, result.getId());
        assertEquals(expResult.getPagenumber(), result.getPagenumber());
        assertEquals(expResult.getX1(), result.getX1(), 0.0001);
        assertEquals(expResult.getY1(), result.getY1(), 0.0001);
        assertEquals(expResult.getX2(), result.getX2(), 0.0001);
        assertEquals(expResult.getY2(), result.getY2(), 0.0001);
        
        
        expResult.setPagenumber(5);
        expResult.setX1(4);
        expResult.setY1(3);
        expResult.setX2(2);
        expResult.setY2(1);
        List<Location> expResults = new ArrayList<>();
        expResults.add(expResult);        
        locationDao.updateLocations(refID, expResults);
        List<Location> updateResult = locationDao.getLocations(refID);
        assertEquals(expResultID, result.getId());
        assertEquals(expResult.getPagenumber(), updateResult.get(0).getPagenumber());
        assertEquals(expResult.getX1(), updateResult.get(0).getX1(), 0.0001);
        assertEquals(expResult.getY1(), updateResult.get(0).getY1(), 0.0001);
        assertEquals(expResult.getX2(), updateResult.get(0).getX2(), 0.0001);
        assertEquals(expResult.getY2(), updateResult.get(0).getY2(), 0.0001);
    }
    
    /**
     * Test of updateLocations method throwing ClassicDatabaseException, of class
     * LocationDao.
     *
     * @throws Exception
     */
    @Test(expected = ClassicDatabaseException.class)
    public void testUpdateLocationsClassicDbException() throws Exception {
        Location location = new Location(1, 2, 3, 4, 10);
        List<Location> locations = new ArrayList<>();
        locations.add(location); 
        fakedao.updateLocations(0,locations); 
    }

    /**
     * Test of deleteLocation method, of class LocationDao.
     * @throws java.lang.Exception
     */
    @Test(expected = ClassicNotFoundException.class)
    public void testDeleteLocation() throws Exception {
        CourseNotes courseNotes = new CourseNotes("title", "url");
        int coursenotesID = courseNotesDao.addCourseNotes(course.getId(), courseNotes);
        Comment comment = new Comment("body", false);
        comment.setUser(user);
        int commentID = commentDao.addComment(comment);
        List<Location> locations = new ArrayList<>();
        CourseNotesReference courseNotesReference = new CourseNotesReference(courseNotes, locations);
        int refID = courseNotesReferenceDao.addCourseNotesReference(coursenotesID, commentID, courseNotesReference);

        Location expResult = new Location(1, 2, 3, 4, 10);
        int expResultID = locationDao.addLocation(refID, expResult);
        Location result = locationDao.getLocation(expResultID);
        assertEquals(expResultID, result.getId());
        assertEquals(expResult.getPagenumber(), result.getPagenumber());
        assertEquals(expResult.getX1(), result.getX1(), 0.0001);
        assertEquals(expResult.getY1(), result.getY1(), 0.0001);
        assertEquals(expResult.getX2(), result.getX2(), 0.0001);
        assertEquals(expResult.getY2(), result.getY2(), 0.0001);
        
        locationDao.deleteLocation(expResultID);
        locationDao.getLocation(expResultID);
    }
    
    /**
     * Test of deleteLocation method throwing ClassicNotFoundException, of class
     * LocationDao.
     *
     * @throws Exception
     */
    @Test(expected = ClassicNotFoundException.class)
    public void testDeleteLocationClassicNFException() throws Exception {
        locationDao.deleteLocation(0); 
    }
    
    /**
     * Test of deleteLocation method throwing ClassicDatabaseException, of class
     * LocationDao.
     *
     * @throws Exception
     */
    @Test(expected = ClassicDatabaseException.class)
    public void testDeleteLocationClassicDbException() throws Exception {
        fakedao.deleteLocation(0); 
    }

    /**
     * Test of getLocation method, of class LocationDao.
     * @throws java.lang.Exception
     */
    @Test
    public void testGetLocation() throws Exception {        
        CourseNotes courseNotes = new CourseNotes("title", "url");
        int coursenotesID = courseNotesDao.addCourseNotes(course.getId(), courseNotes);
        Comment comment = new Comment("body", false);
        comment.setUser(user);
        int commentID = commentDao.addComment(comment);
        List<Location> locations = new ArrayList<>();
        CourseNotesReference courseNotesReference = new CourseNotesReference(courseNotes, locations);
        int refID = courseNotesReferenceDao.addCourseNotesReference(coursenotesID, commentID, courseNotesReference);

        Location expResult = new Location(1, 2, 3, 4, 10);
        int expResultID = locationDao.addLocation(refID, expResult);

        Location result = locationDao.getLocation(expResultID);
        assertEquals(expResultID, result.getId());
        assertEquals(expResult.getPagenumber(), result.getPagenumber());
        assertEquals(expResult.getX1(), result.getX1(), 0.0001);
        assertEquals(expResult.getY1(), result.getY1(), 0.0001);
        assertEquals(expResult.getX2(), result.getX2(), 0.0001);
        assertEquals(expResult.getY2(), result.getY2(), 0.0001);
    }

    /**
     * Test of getLocation method throwing ClassicNotFoundException, of class
     * LocationDao.
     *
     * @throws Exception
     */
    @Test(expected = ClassicNotFoundException.class)
    public void testGetLocationClassicNFException() throws Exception {
        locationDao.getLocation(0); 
    }
    
    /**
     * Test of getLocation method throwing ClassicDatabaseException, of class
     * LocationDao.
     *
     * @throws Exception
     */
    @Test(expected = ClassicDatabaseException.class)
    public void testGetLocationClassicDbException() throws Exception {
        fakedao.getLocation(0); 
    }
    
    /**
     * Test of getLocations method, of class LocationDao.
     * @throws java.lang.Exception
     */
    @Test
    public void testGetLocations_0args() throws Exception {        
        CourseNotes courseNotes = new CourseNotes("title", "url");
        int coursenotesID = courseNotesDao.addCourseNotes(course.getId(), courseNotes);
        Comment comment = new Comment("body", false);
        comment.setUser(user);
        int commentID = commentDao.addComment(comment);
        List<Location> locations = new ArrayList<>();
        CourseNotesReference courseNotesReference = new CourseNotesReference(courseNotes, locations);
        int refID = courseNotesReferenceDao.addCourseNotesReference(coursenotesID, commentID, courseNotesReference);

        Location expResult = new Location(1, 2, 3, 4, 10);
        int expResultID = locationDao.addLocation(refID, expResult);

        List<Location> results = locationDao.getLocations();
        Location result = null;
        for (Location location : results) {
            if (expResultID == location.getId()) {
                result = location;
            }
        }        
        assertEquals(expResultID, result.getId());
        assertEquals(expResult.getPagenumber(), result.getPagenumber());
        assertEquals(expResult.getX1(), result.getX1(), 0.0001);
        assertEquals(expResult.getY1(), result.getY1(), 0.0001);
        assertEquals(expResult.getX2(), result.getX2(), 0.0001);
        assertEquals(expResult.getY2(), result.getY2(), 0.0001);
    }
    
    /**
     * Test of getLocations method throwing ClassicDatabaseException, of class
     * LocationDao.
     *
     * @throws Exception
     */
    @Test(expected = ClassicDatabaseException.class)
    public void testGetLocations_0argsClassicDbException() throws Exception {
        fakedao.getLocations(); 
    }

    /**
     * Test of getLocations method, of class LocationDao.
     * @throws java.lang.Exception
     */
    @Test
    public void testGetLocations_int() throws Exception {        
        CourseNotes courseNotes = new CourseNotes("title", "url");
        int coursenotesID = courseNotesDao.addCourseNotes(course.getId(), courseNotes);
        Comment comment = new Comment("body", false);
        comment.setUser(user);
        int commentID = commentDao.addComment(comment);
        List<Location> locations = new ArrayList<>();
        CourseNotesReference courseNotesReference = new CourseNotesReference(courseNotes, locations);
        int refID = courseNotesReferenceDao.addCourseNotesReference(coursenotesID, commentID, courseNotesReference);

        Location expResult = new Location(1, 2, 3, 4, 10);
        int expResultID = locationDao.addLocation(refID, expResult);

        List<Location> results = locationDao.getLocations(refID);
        Location result = null;
        for (Location location : results) {
            if (expResultID == location.getId()) {
                result = location;
            }
        }        
        assertEquals(expResultID, result.getId());
        assertEquals(expResult.getPagenumber(), result.getPagenumber());
        assertEquals(expResult.getX1(), result.getX1(), 0.0001);
        assertEquals(expResult.getY1(), result.getY1(), 0.0001);
        assertEquals(expResult.getX2(), result.getX2(), 0.0001);
        assertEquals(expResult.getY2(), result.getY2(), 0.0001);
    }
       
    /**
     * Test of getLocations method throwing ClassicDatabaseException, of class
     * LocationDao.
     *
     * @throws Exception
     */
    @Test(expected = ClassicDatabaseException.class)
    public void testGetLocations_intClassicDbException() throws Exception {
        fakedao.getLocations(0); 
    }

    /**
     * Test of isLocation method, of class LocationDao.
     */
    @Test
    public void testIsLocation() throws Exception {        
        CourseNotes courseNotes = new CourseNotes("title", "url");
        int coursenotesID = courseNotesDao.addCourseNotes(course.getId(), courseNotes);
        Comment comment = new Comment("body", false);
        comment.setUser(user);
        int commentID = commentDao.addComment(comment);
        List<Location> locations = new ArrayList<>();
        CourseNotesReference courseNotesReference = new CourseNotesReference(courseNotes, locations);
        int refID = courseNotesReferenceDao.addCourseNotesReference(coursenotesID, commentID, courseNotesReference);

        Location expResult = new Location(1, 2, 3, 4, 10);
        int expResultID = locationDao.addLocation(refID, expResult);
        boolean result = locationDao.isLocation(expResultID);        
        assertEquals(true, result);
    }
    
        /**
     * Test of isLocation method throwing ClassicDatabaseException, of class
     * LocationDao.
     *
     * @throws Exception
     */
    @Test(expected = ClassicDatabaseException.class)
    public void testIsLocationClassicDbException() throws Exception {
        fakedao.isLocation(0); 
    }

    /**
     * Test of updateLocation method, of class LocationDao.
     */
    @Test
    public void testUpdateLocation() throws Exception {        
        CourseNotes courseNotes = new CourseNotes("title", "url");
        int coursenotesID = courseNotesDao.addCourseNotes(course.getId(), courseNotes);
        Comment comment = new Comment("body", false);
        comment.setUser(user);
        int commentID = commentDao.addComment(comment);
        List<Location> locations = new ArrayList<>();
        CourseNotesReference courseNotesReference = new CourseNotesReference(courseNotes, locations);
        int refID = courseNotesReferenceDao.addCourseNotesReference(coursenotesID, commentID, courseNotesReference);

        Location expResult = new Location(1, 2, 3, 4, 10);
        int expResultID = locationDao.addLocation(refID, expResult);
        Location result = locationDao.getLocation(expResultID);
        assertEquals(expResultID, result.getId());
        assertEquals(expResult.getPagenumber(), result.getPagenumber());
        assertEquals(expResult.getX1(), result.getX1(), 0.0001);
        assertEquals(expResult.getY1(), result.getY1(), 0.0001);
        assertEquals(expResult.getX2(), result.getX2(), 0.0001);
        assertEquals(expResult.getY2(), result.getY2(), 0.0001);
        
        expResult.setId(expResultID);
        expResult.setPagenumber(5);
        expResult.setX1(4);
        expResult.setY1(3);
        expResult.setX2(2);
        expResult.setY2(1);
        locationDao.updateLocation(expResult);
        Location updateResult = locationDao.getLocation(expResultID);
        assertEquals(expResultID, updateResult.getId());
        assertEquals(expResult.getPagenumber(), updateResult.getPagenumber());
        assertEquals(expResult.getX1(), updateResult.getX1(), 0.0001);
        assertEquals(expResult.getY1(), updateResult.getY1(), 0.0001);
        assertEquals(expResult.getX2(), updateResult.getX2(), 0.0001);
        assertEquals(expResult.getY2(), updateResult.getY2(), 0.0001);
    }
    
    /**
     * Test of updateLocation method throwing ClassicNotFoundException, of class
     * LocationDao.
     *
     * @throws Exception
     */
    @Test(expected = ClassicNotFoundException.class)
    public void testUpdateLocationClassicNFException() throws Exception {
        Location location = new Location(1, 2, 3, 4, 10);
        locationDao.updateLocation(location); 
    }
    
    /**
     * Test of updateLocation method throwing ClassicDatabaseException, of class
     * LocationDao.
     *
     * @throws Exception
     */
    @Test(expected = ClassicDatabaseException.class)
    public void testUpdateLocationClassicDbException() throws Exception {
        Location location = new Location(1, 2, 3, 4, 10);
        fakedao.updateLocation(location);         
    }

    /**
     * Test of cleanTable method, of class LocationDao.
     */
    @Test(expected = ClassicNotFoundException.class)
    public void testCleanTable() throws Exception {        
        CourseNotes courseNotes = new CourseNotes("title", "url");
        int coursenotesID = courseNotesDao.addCourseNotes(course.getId(), courseNotes);
        Comment comment = new Comment("body", false);
        comment.setUser(user);
        int commentID = commentDao.addComment(comment);
        List<Location> locations = new ArrayList<>();
        CourseNotesReference courseNotesReference = new CourseNotesReference(courseNotes, locations);
        int refID = courseNotesReferenceDao.addCourseNotesReference(coursenotesID, commentID, courseNotesReference);

        Location expResult = new Location(1, 2, 3, 4, 10);
        int expResultID = locationDao.addLocation(refID, expResult);
        Location result = locationDao.getLocation(expResultID);
        assertEquals(expResultID, result.getId());
        assertEquals(expResult.getPagenumber(), result.getPagenumber());
        assertEquals(expResult.getX1(), result.getX1(), 0.0001);
        assertEquals(expResult.getY1(), result.getY1(), 0.0001);
        assertEquals(expResult.getX2(), result.getX2(), 0.0001);
        assertEquals(expResult.getY2(), result.getY2(), 0.0001);
        
        locationDao.cleanTable();
        locationDao.getLocation(expResultID);
    }
    
    /**
     * Test of cleanTable method throwing ClassicDatabaseException, of class
     * LocationDao.
     *
     * @throws Exception
     */
    @Test(expected = ClassicDatabaseException.class)
    public void testCleanTableClassicDbException() throws Exception {
        fakedao.cleanTable();         
    }

}
