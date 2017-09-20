/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllermockup;

import courses.Course;
import courses.Lecture;
import courses.Role;
import courses.User;
import databaseaccess.exceptions.ClassicDatabaseException;
import databaseaccess.exceptions.ClassicNotFoundException;
import interfaces.CourseControllerInterface;

import java.util.ArrayList;
import java.util.List;

import models.CourseNotes;
import models.Video;

/**
 *
 * @author Juta
 */
public class CourseControllerMockup implements CourseControllerInterface{

    @Override
    public int addCourse(Course course, int profId) throws ClassicDatabaseException, ClassicNotFoundException {
        return 1;
    }

    @Override
    public Course getCourse(int courseid) throws ClassicDatabaseException, ClassicNotFoundException {
        Course course = new Course("course");
        course.setId(1);
        Video video = new Video("title", "url", 999);
        video.setId(1);
        CourseNotes cn = new CourseNotes("title", "url");
        cn.setId(1);
        Lecture lecture = new Lecture("lecture");
        lecture.setId(1);
        course.addCourseNotes(cn);
        course.addLecture(lecture);
        course.addVideo(video);
        User owner = new User("user", Role.ADMIN, "password");
        course.setOwner(owner);
        return course;
    }

    @Override
    public List<Course> getCourses() throws ClassicDatabaseException {
        List<Course> courses = new ArrayList<>();
        Course course = new Course("course");
        course.setId(1);
        courses.add(course);
        User owner = new User("user", Role.ADMIN, "password");
        course.setOwner(owner);
        return courses;
    }

    @Override
    public void updateCourse(int courseId, Course course, int userId) throws ClassicDatabaseException, ClassicNotFoundException {
        
    }

    @Override
    public void deleteCourse(int courseId, int userId) throws ClassicDatabaseException, ClassicNotFoundException {
        
    }

    @Override
    public int addLecture(int courseId, Lecture lecture, int profId) throws ClassicDatabaseException, ClassicNotFoundException {
        return 1;
    }

    @Override
    public Lecture getLecture(int lectureId) throws ClassicDatabaseException, ClassicNotFoundException {
        Lecture lecture = new Lecture("lecture");
        lecture.setId(1);
        Video video = new Video("title", "url", 999);
        video.setId(1);
        CourseNotes cn = new CourseNotes("title", "url");
        cn.setId(1);
        lecture.addCourseNotes(cn);
        lecture.addVideo(video);
        return lecture;
    }

    @Override
    public List<Lecture> getLectures(int courseId) throws ClassicDatabaseException, ClassicNotFoundException {
        List<Lecture> lectures = new ArrayList<>();
        Lecture lecture = new Lecture("lecture");
        lecture.setId(1);
        lectures.add(lecture);
        return lectures;
    }

    @Override
    public void updateLecture(int lectureId, Lecture lecture, int userId) throws ClassicDatabaseException, ClassicNotFoundException {
        
    }

    @Override
    public void deleteLecture(int lectureId, int userId) throws ClassicDatabaseException, ClassicNotFoundException {
        
    }
    
}
