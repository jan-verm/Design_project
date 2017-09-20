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
public class UserDaoTest {

    UserDao userDao;
    CourseDao courseDao;
    UserDao fakeDao;
    Course course;

    public UserDaoTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() throws ClassicDatabaseException {
        userDao = new UserDao(UserDao.TEST_DATABASE_CONFIG);
        fakeDao = new UserDao("fakedbconfig.properties");
        courseDao = new CourseDao(CourseDao.TEST_DATABASE_CONFIG);
        
        User user = new User("usertestuser", Role.NONE, "password");
        userDao.addUser(user);
        course = new Course("usertestcourse");
        course.setOwner(user);
        courseDao.addCourse(course);
    }

    @After
    public void tearDown() throws ClassicDatabaseException {
        userDao.cleanTable();
        courseDao.cleanTable();
    }

    /**
     * Test of addUser method, of class UserDao.
     */
    @Test
    public void testAddUser() throws Exception {
        User expResult = new User("testAddUser", Role.STUDENT,"password");
        int expResultID = userDao.addUser(expResult);
        User result = userDao.getUser(expResultID);
        assertEquals(expResultID, result.getId());
        assertEquals(expResult.getUsername(), result.getUsername());
        assertEquals(expResult.getRole(), result.getRole());
    }
    
    /**
     * Test of addUser method, of class UserDao.
     */
    @Test(expected=ClassicNameTakenException.class)
    public void testAddUserNameTakenException() throws Exception {
        User expResult = new User("testAddUser", Role.STUDENT,"password");
        userDao.addUser(expResult);
        userDao.addUser(expResult);
    }

    @Test(expected = ClassicDatabaseException.class)
    public void testAddUserDBException() throws ClassicDatabaseException {
        User expResult = new User("user", Role.STUDENT,"password");
        fakeDao.addUser(expResult);
    }

    /**
     * Test of deleteUser method, of class UserDao.
     */
    @Test(expected = ClassicNotFoundException.class)
    public void testDeleteUser() throws Exception {
        User expResult = new User("testDeleteUser", Role.STUDENT,"password");
        int expResultID = userDao.addUser(expResult);
        User result = userDao.getUser(expResultID);
        assertEquals(expResultID, result.getId());
        assertEquals(expResult.getUsername(), result.getUsername());
        assertEquals(expResult.getRole(), result.getRole());
        userDao.deleteUser(expResultID);
        userDao.getUser(expResultID);
    }

    @Test(expected = ClassicDatabaseException.class)
    public void testDeleteUserDBException() throws ClassicDatabaseException, ClassicNotFoundException {
        fakeDao.deleteUser(0);
    }

    @Test(expected = ClassicNotFoundException.class)
    public void testDeleteUserNotFoundException() throws ClassicDatabaseException, ClassicNotFoundException {
        userDao.deleteUser(0);
    }

    /**
     * Test of getUser method, of class UserDao.
     */
    @Test
    public void testGetUser() throws Exception {
        User expResult = new User("testGetUser", Role.STUDENT,"password");
        int expResultID = userDao.addUser(expResult);
        User result = userDao.getUser(expResultID);
        assertEquals(expResultID, result.getId());
        assertEquals(expResult.getUsername(), result.getUsername());
        assertEquals(expResult.getRole(), result.getRole());
    }

    @Test(expected = ClassicNotFoundException.class)
    public void testGetUserNotFoundException() throws ClassicDatabaseException, ClassicNotFoundException {
        userDao.getUser(0);
    }

    @Test(expected = ClassicDatabaseException.class)
    public void testGetUserDBException() throws ClassicDatabaseException, ClassicNotFoundException {
        fakeDao.getUser(0);
    }

    /**
     * Test of getUsers method, of class UserDao.
     */
    @Test
    public void testGetUsers_Role() throws Exception {
        User expResult = new User("testGetUsers_Role", Role.STUDENT,"password");
        int expResultID = userDao.addUser(expResult);

        User otherResult = new User("otherTestGetUsers_Role", Role.TEACHER,"password");
        int otherResultID = userDao.addUser(otherResult);

        List<User> results = userDao.getUsers(Role.STUDENT);
        assertEquals(expResultID, results.get(0).getId());
        assertEquals(expResult.getUsername(), results.get(0).getUsername());
        assertEquals(expResult.getRole(), results.get(0).getRole());
    }

    @Test(expected = ClassicDatabaseException.class)
    public void testGetUsers_RoleDBException() throws ClassicDatabaseException, ClassicNotFoundException {
        fakeDao.getUsers(Role.ADMIN);
    }
    
    /**
     * Test of getUsers method, of class UserDao.
     */
    @Test
    public void testGetUser_int() throws Exception {
        User expResult = new User("testGetUser_int", Role.STUDENT,"password");
        int expResultID = userDao.addUser(expResult);        

        User result = userDao.getUser(expResultID);
        assertEquals(expResultID, result.getId());
        assertEquals(expResult.getUsername(), result.getUsername());
        assertEquals(expResult.getRole(), result.getRole());
    }

    @Test(expected = ClassicDatabaseException.class)
    public void testGetUsers_intDBException() throws ClassicDatabaseException, ClassicNotFoundException {
        fakeDao.getUser("notfound");
    }
    
    @Test(expected = ClassicNotFoundException.class)
    public void testGetUsers_intNFException() throws ClassicDatabaseException, ClassicNotFoundException {
        userDao.getUser("notfound");
    }

    /**
     * Test of getUsers method, of class UserDao.
     */
    @Test
    public void testGetUsers_0args() throws Exception {
        User expResult = new User("testGetUsers_0args", Role.STUDENT,"password");
        int expResultID = userDao.addUser(expResult);

        User otherResult = new User("testOtherUser", Role.TEACHER,"password");
        int otherResultID = userDao.addUser(otherResult);

        List<User> results = userDao.getUsers();
        User result = null;
        for (User user : results) {
            if (user.getId() == expResultID) {
                result = user;
            }
        }

        assertEquals(expResultID, result.getId());
        assertEquals(expResult.getUsername(), result.getUsername());
        assertEquals(expResult.getRole(), result.getRole());
    }

    @Test(expected = ClassicDatabaseException.class)
    public void testGetUsers_0argsDBException() throws ClassicDatabaseException, ClassicNotFoundException {
        fakeDao.getUsers();
    }

    /**
     * Test of isUser method, of class UserDao.
     */
    @Test
    public void testIsUser() throws Exception {
        User user = new User("testIsUser", Role.STUDENT,"password");
        int userID = userDao.addUser(user);
        boolean result = userDao.isUser(userID);
        assertEquals(true, result);
    }

    @Test(expected = ClassicDatabaseException.class)
    public void testIsUserDBException() throws ClassicDatabaseException, ClassicNotFoundException {
        fakeDao.isUser(0);
    }

    /**
     * Test of updateUser method, of class UserDao.
     */
    @Test
    public void testUpdateUser() throws Exception {
        User expResult = new User("testUpdateUser", Role.STUDENT,"password");
        int expResultID = userDao.addUser(expResult);
        User result = userDao.getUser(expResultID);
        assertEquals(expResultID, result.getId());
        assertEquals(expResult.getUsername(), result.getUsername());
        assertEquals(expResult.getRole(), result.getRole());

        expResult.setId(expResultID);
        expResult.setUsername("newTestUser");
        expResult.setRole(Role.ADMIN);
        userDao.updateUser(expResult);
        User updateResult = userDao.getUser(expResultID);
        assertEquals(expResultID, updateResult.getId());
        assertEquals(expResult.getUsername(), updateResult.getUsername());
        assertEquals(expResult.getRole(), updateResult.getRole());
    }

    @Test(expected = ClassicDatabaseException.class)
    public void testUpdateUserDBException() throws ClassicDatabaseException, ClassicNotFoundException {
        fakeDao.updateUser(new User("", Role.NONE,"password"));
    }

    @Test(expected = ClassicNotFoundException.class)
    public void testUpdateUserNotFoundException() throws ClassicDatabaseException, ClassicNotFoundException {
        userDao.updateUser(new User("", Role.NONE,"password"));
    }

    /**
     * Test of cleanTable method, of class UserDao.
     */
    @Test(expected = ClassicNotFoundException.class)
    public void testCleanTable() throws Exception {
        User expResult = new User("testCleanTable", Role.STUDENT,"password");
        int expResultID = userDao.addUser(expResult);
        User result = userDao.getUser(expResultID);
        assertEquals(expResultID, result.getId());
        assertEquals(expResult.getUsername(), result.getUsername());
        assertEquals(expResult.getRole(), result.getRole());
        userDao.cleanTable();
        userDao.getUser(expResultID);
    }
    
    @Test(expected = ClassicDatabaseException.class)
    public void testCleanTableDBException() throws ClassicDatabaseException, ClassicNotFoundException {
        fakeDao.cleanTable();
    }

    @Test
    public void testGetUser_String() throws Exception {
        User expResult = new User("testGetUser_String", Role.STUDENT,"password");
        int expResultID = userDao.addUser(expResult);        

        User result = userDao.getUser("testGetUser_String");
        assertEquals(expResultID, result.getId());
        assertEquals(expResult.getUsername(), result.getUsername());
        assertEquals(expResult.getRole(), result.getRole());
    }

    @Test
    public void testSubscribeCourse() throws Exception {
        User user = new User("testSubscribeCourse", Role.STUDENT,"password");
        int userID = userDao.addUser(user);        
        userDao.subscribeCourse(userID, course.getId());
        
        List<Course> results = userDao.getSubscriptions(userID);
        assertEquals(course.getId(), results.get(0).getId());
        assertEquals(course.getName(), results.get(0).getName());
        assertEquals(course.getOwner().getId(), results.get(0).getOwner().getId());
    }
    
    @Test
    public void testSubscribeCourseTwice() throws Exception {
        User user = new User("testSubscribeCourse", Role.STUDENT,"password");
        int userID = userDao.addUser(user);        
        userDao.subscribeCourse(userID, course.getId());
        userDao.subscribeCourse(userID, course.getId());
        
        List<Course> results = userDao.getSubscriptions(userID);
        assertEquals(1, results.size());
        assertEquals(course.getId(), results.get(0).getId());
        assertEquals(course.getName(), results.get(0).getName());
        assertEquals(course.getOwner().getId(), results.get(0).getOwner().getId());
    }
    
    @Test(expected = ClassicDatabaseException.class)
    public void testSubscribeCourseDBException() throws ClassicDatabaseException, ClassicNotFoundException {
        fakeDao.subscribeCourse(0,0);
    }
    
    @Test(expected = ClassicNotFoundException.class)
    public void testSubscribeCourseNFException() throws ClassicDatabaseException, ClassicNotFoundException {
        userDao.subscribeCourse(0,0);
    }

    @Test
    public void testUnsubscribeCourse() throws Exception {
        User user = new User("testUnsubscribeCourse", Role.STUDENT,"password");
        int userID = userDao.addUser(user);        
        userDao.subscribeCourse(userID, course.getId());
        
        List<Course> results = userDao.getSubscriptions(userID);
        assertEquals(course.getId(), results.get(0).getId());
        assertEquals(course.getName(), results.get(0).getName());
        assertEquals(course.getOwner().getId(), results.get(0).getOwner().getId());
        
        userDao.unsubscribeCourse(userID, course.getId());
        User result2 = userDao.getUser(userID);
        assertTrue(result2.getSubscriptions().isEmpty());
    }
    
    @Test(expected = ClassicDatabaseException.class)
    public void testUnsubscribeCourseDBException() throws ClassicDatabaseException, ClassicNotFoundException {
        fakeDao.unsubscribeCourse(0,0);
    }
    
    @Test(expected = ClassicNotFoundException.class)
    public void testUnsubscribeCourseNFException() throws ClassicDatabaseException, ClassicNotFoundException {
        userDao.unsubscribeCourse(0,0);
    }

    @Test
    public void testUpdateUserPassword() throws Exception {
        User expResult = new User("testGetUser_String", Role.STUDENT,"password");
        int expResultID = userDao.addUser(expResult);        

        User result = userDao.getUser(expResultID);
        assertEquals(expResultID, result.getId());
        assertEquals(expResult.getUsername(), result.getUsername());
        assertEquals(expResult.getRole(), result.getRole());
        
        String expPassword = "newPassword";
        userDao.updateUserPassword(expResultID, "newPassword");
        User result2 = userDao.getUser(expResultID);
        assertEquals(expPassword, result2.getPassword());        
    }
    
    @Test(expected = ClassicDatabaseException.class)
    public void testUpdateUserPasswordDBException() throws ClassicDatabaseException, ClassicNotFoundException {
        fakeDao.updateUserPassword(0,"");
    }
    
    @Test(expected = ClassicNotFoundException.class)
    public void testUpdateUserPasswordNFException() throws ClassicDatabaseException, ClassicNotFoundException {
        userDao.updateUserPassword(0,"");
    }    

    @Test
    public void testIsTaken() throws Exception {
        boolean result = userDao.isTaken("testIsTaken");
        assertFalse(result);
        
        User expResult = new User("testIsTaken", Role.STUDENT,"password");
        int expResultID = userDao.addUser(expResult);        

        boolean result2 = userDao.isTaken("testIsTaken");
        assertTrue(result2);
    }
    
    @Test(expected = ClassicDatabaseException.class)
    public void testIsTakenDBException() throws ClassicDatabaseException, ClassicNotFoundException {
        fakeDao.isTaken("");
    }

    @Test
    public void testGetSubscriptions() throws Exception {
        User user = new User("testSubscribeCourse", Role.STUDENT,"password");
        int userID = userDao.addUser(user);        
        userDao.subscribeCourse(userID, course.getId());
        
        List<Course> results = userDao.getSubscriptions(userID);
        assertEquals(course.getId(), results.get(0).getId());
        assertEquals(course.getName(), results.get(0).getName());
        assertEquals(course.getOwner().getId(), results.get(0).getOwner().getId());
    }

}
