/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import courses.Course;
import courses.Lecture;
import courses.Role;
import courses.User;
import dao.CoursesMockUpDAO;
import dao.MockUpDAO;
import databaseaccess.exceptions.ClassicDatabaseException;
import databaseaccess.exceptions.ClassicNotFoundException;
import models.CourseNotes;
import models.Video;

import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

/**
 *
 * @author Juta
 */
public class CourseControllerTest {
    
    private CourseController courseController;
    private MockUpDAO mockup;
    private CoursesMockUpDAO coursemockup;
    
    @Before
    public void setUp() throws ClassicDatabaseException {
        coursemockup = new CoursesMockUpDAO();
        mockup = new MockUpDAO();
        courseController = new CourseController(coursemockup, coursemockup, mockup, mockup, coursemockup, "/var/www/classic/resources/courses");
        profId = coursemockup.addUser(new User("username", Role.ADMIN, "password"));
    }
    
    private int profId;

    /**
     * Test of addCourse method, of class CourseController.
     */
    @Test
    public void testAddCourse() throws Exception {
        Course course = new Course("course");
        int result = courseController.addCourse(course, profId);
        assertNotEquals(0, result);
    }

    /**
     * Test of getCourse method, of class CourseController.
     */
    @Test
    public void testGetCourse() throws Exception {
        Course course = new Course("course");
        int courseId = courseController.addCourse(course, profId);
        Course result = courseController.getCourse(courseId);
        
        assertEquals(courseId, result.getId());
        assertEquals(course.getName(), result.getName());
        assertEquals(true, result.getLectures().isEmpty());
        assertEquals(true, result.getVideos().isEmpty());
        assertEquals(true, result.getCourseNotes().isEmpty());
    }
    
    /**
     * Test of getCourse method, of class CourseController.
     */
    @Test
    public void testGetCourseWithFiles() throws Exception {
        Course course = new Course("course");
        int courseId = courseController.addCourse(course, profId);
        Lecture lecture = new Lecture("lecture");
        int lectureId = courseController.addLecture(courseId, lecture, profId);
        
        VideoController vc = new VideoController(mockup, coursemockup, coursemockup);
        Video v  = new Video("title", "url", 999);
        vc.addVideo(courseId, v, profId);
        
        CourseNotesController cnc = new CourseNotesController(mockup, coursemockup, coursemockup, "/var/www/classic/resources/courses");
        CourseNotes cn = new CourseNotes("title", "url");
        cnc.addCourseNotes(courseId, cn, profId);
        
        Course result = courseController.getCourse(courseId);
        
        assertEquals(courseId, result.getId());
        assertEquals(course.getName(), result.getName());
        assertEquals(false, result.getLectures().isEmpty());
        assertEquals(false, result.getVideos().isEmpty());
        assertEquals(false, result.getCourseNotes().isEmpty());
    }
    
    /**
     * Test of getCourse method, of class CourseController.
     */
    @Test(expected = ClassicNotFoundException.class)
    public void testGetCourseNotFoundException() throws Exception {
        courseController.getCourse(0);
    }

    /**
     * Test of getCourses method, of class CourseController.
     */
    @Test
    public void testGetCourses() throws Exception {
        Course course = new Course("course");
        int courseId = courseController.addCourse(course, profId);
        Course course2 = new Course("course2");
        int courseId2 = courseController.addCourse(course2, profId);
        
        Lecture lecture = new Lecture("lecture");
        int lectureId = courseController.addLecture(courseId, lecture, profId);
        
        VideoController vc = new VideoController(mockup, coursemockup, coursemockup);
        Video v  = new Video("title", "url", 999);
        vc.addVideo(courseId, v, profId);
        
        CourseNotesController cnc = new CourseNotesController(mockup, coursemockup, coursemockup, "/var/www/classic/resources/courses");
        CourseNotes cn = new CourseNotes("title", "url");
        cnc.addCourseNotes(courseId, cn, profId);
        
        List<Course> result = courseController.getCourses();
        assertEquals(courseId, result.get(0).getId());
        assertEquals(courseId2, result.get(1).getId());
        assertEquals(course.getName(), result.get(0).getName());
        assertEquals(course2.getName(), result.get(1).getName());
    }
    
    /**
     * Test of getCourses method, of class CourseController.
     */
    @Test
    public void testGetCoursesIsEmpty() throws Exception {
        List<Course> result = courseController.getCourses();
        assertEquals(true, result.isEmpty());
    }

    /**
     * Test of updateCourse method, of class CourseController.
     */
    @Test
    public void testUpdateCourse() throws Exception {
        Course course = new Course("course");
        int courseId = courseController.addCourse(course, profId);
        course = new Course("new_course");
        courseController.updateCourse(courseId, course, profId);
        Course result = courseController.getCourse(courseId);
        
        assertEquals(courseId, result.getId());
        assertEquals(course.getName(), result.getName());
        assertEquals(true, result.getLectures().isEmpty());
        assertEquals(true, result.getVideos().isEmpty());
        assertEquals(true, result.getCourseNotes().isEmpty());
    }
    
    /**
     * Test of updateCourse method, of class CourseController.
     */
    @Test(expected = ClassicNotFoundException.class)
    public void testUpdateCourseNotFoundException() throws Exception {
        Course course = new Course("course");
        courseController.updateCourse(0, course, profId);
    }

    /**
     * Test of deleteCourse method, of class CourseController.
     */
    @Test(expected = ClassicNotFoundException.class)
    public void testDeleteCourse() throws Exception {
        Course course = new Course("course");
        int courseId = courseController.addCourse(course, profId);
        
        courseController.deleteCourse(courseId, profId);
        
        courseController.getCourse(courseId);
    }
    
    

    /**
     * Test of addLecture method, of class CourseController.
     */
    @Test
    public void testAddLecture() throws Exception {
        Course course = new Course("course");
        int courseId = courseController.addCourse(course, profId);
        Lecture lecture = new Lecture("lecture");
        int result = courseController.addLecture(courseId, lecture, profId);
        
        assertNotEquals(0, result);
    }
    
    /**
     * Test of addLecture method, of class CourseController.
     */
    @Test(expected = ClassicNotFoundException.class)
    public void testAddLectureNotFoundException() throws Exception {
        Lecture lecture = new Lecture("lecture");
        courseController.addLecture(0, lecture, profId);
    }

    /**
     * Test of getLecture method, of class CourseController.
     */
    @Test
    public void testGetLecture() throws Exception {
        Course course = new Course("course");
        int courseId = courseController.addCourse(course, profId);
        Lecture lecture = new Lecture("lecture");
        int lectureId = courseController.addLecture(courseId, lecture, profId);
        Lecture result = courseController.getLecture(lectureId);
        
        assertEquals(lectureId, result.getId());
        assertEquals(lecture.getName(), result.getName());
        assertEquals(true, result.getCourseNotes().isEmpty());
        assertEquals(true, result.getVideos().isEmpty());
    }
    
    /**
     * Test of getLecture method, of class CourseController.
     */
    @Test
    public void testGetLectureWithFiles() throws Exception {
        Course course = new Course("course");
        int courseId = courseController.addCourse(course, profId);
        Lecture lecture = new Lecture("lecture");
        int lectureId = courseController.addLecture(courseId, lecture, profId);
        
        VideoController vc = new VideoController(mockup, coursemockup, coursemockup);
        Video v2  = new Video("title2", "url2", 999);
        vc.addVideo(lectureId, v2, profId);
        
        CourseNotesController cnc = new CourseNotesController(mockup, coursemockup, coursemockup, "/var/www/classic/resources/courses");
        CourseNotes cn2 = new CourseNotes("title2", "url2");
        cnc.addCourseNotes(lectureId, cn2, profId);
        
        Lecture result = courseController.getLecture(lectureId);
        
        assertEquals(lectureId, result.getId());
        assertEquals(lecture.getName(), result.getName());
        assertEquals(false, result.getCourseNotes().isEmpty());
        assertEquals(false, result.getVideos().isEmpty());
    }
    
    /**
     * Test of getLecture method, of class CourseController.
     */
    @Test(expected = ClassicNotFoundException.class)
    public void testGetLectureNotFoundException() throws Exception {
        courseController.getLecture(0);
    }

    /**
     * Test of getLectures method, of class CourseController.
     */
    @Test
    public void testGetLectures() throws Exception {
        Course course = new Course("course");
        int courseId = courseController.addCourse(course, profId);
        Lecture lecture = new Lecture("lecture");
        int lectureId = courseController.addLecture(courseId, lecture, profId);
        Lecture lecture2 = new Lecture("lecture2");
        int lectureId2 = courseController.addLecture(courseId, lecture2, profId);
        
        List<Lecture> result = courseController.getLectures(courseId);
        assertEquals(lectureId, result.get(0).getId());
        assertEquals(lectureId2, result.get(1).getId());
        assertEquals(lecture.getName(), result.get(0).getName());
        assertEquals(lecture2.getName(), result.get(1).getName());
    }
    
    /**
     * Test of getLectures method, of class CourseController.
     */
    @Test(expected = ClassicNotFoundException.class)
    public void testGetLecturesNotFoundException() throws Exception {
        courseController.getLectures(0);
    }

    /**
     * Test of updateLecture method, of class CourseController.
     */
    @Test
    public void testUpdateLecture() throws Exception {
        Course course = new Course("course");
        int courseId = courseController.addCourse(course, profId);
        Lecture lecture = new Lecture("lecture");
        int lectureId = courseController.addLecture(courseId, lecture, profId);
        
        lecture = new Lecture("new_lecture");
        courseController.updateLecture(lectureId, lecture, profId);
        Lecture result = courseController.getLecture(lectureId);
        
        assertEquals(lectureId, result.getId());
        assertEquals(lecture.getName(), result.getName());
        assertEquals(true, result.getCourseNotes().isEmpty());
        assertEquals(true, result.getVideos().isEmpty());
    }
    
    /**
     * Test of updateLecture method, of class CourseController.
     */
    @Test(expected = ClassicNotFoundException.class)
    public void testUpdateLectureNotFoundException() throws Exception {
        Lecture lecture = new Lecture("lecture");
        courseController.updateLecture(0, lecture, profId);
    }

    /**
     * Test of deleteLecture method, of class CourseController.
     */
    @Test(expected = ClassicNotFoundException.class)
    public void testDeleteLecture() throws Exception {
        Course course = new Course("course");
        int courseId = courseController.addCourse(course, profId);
        Lecture lecture = new Lecture("lecture");
        int lectureId = courseController.addLecture(courseId, lecture, profId);
        
        courseController.deleteLecture(lectureId, profId);
        
        courseController.getLecture(lectureId);
    }
    
}
