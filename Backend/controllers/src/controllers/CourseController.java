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
import databaseaccess.exceptions.ClassicDatabaseException;
import databaseaccess.exceptions.ClassicNotFoundException;
import databaseaccess.exceptions.ClassicUnauthorizedException;
import databaseaccess.interfaces.ICourseDao;
import databaseaccess.interfaces.ICourseNotesDao;
import databaseaccess.interfaces.ILectureDao;
import databaseaccess.interfaces.IUserDao;
import databaseaccess.interfaces.IVideoDao;
import interfaces.CourseControllerInterface;
import java.io.File;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import models.CourseNotes;
import models.Video;

/**
 *
 * @author Juta
 */
public class CourseController implements CourseControllerInterface {
    
    private final ICourseDao courseDao;
    private final ILectureDao lectureDao;
    private final IVideoDao videoDao;
    private final ICourseNotesDao courseNotesDao;
    private final IUserDao userdao;
    private final String prefix;
    
    /**
     * create a course controller
     * @param coursedao class that interacts with the db for courses
     * @param lecturedao class that interacts with the db for lectures
     * @param videoDao class that interacts with the db for videos
     * @param courseNotesDao class that interacts with the db for course notes
     * @param userdao class that interacts with the db for users
     * @param prefix path where the course notes will be uploaded
     */
    public CourseController(ICourseDao coursedao, ILectureDao lecturedao, IVideoDao videoDao, ICourseNotesDao courseNotesDao, IUserDao userdao, String prefix){

        this.courseDao = coursedao;
        this.lectureDao = lecturedao;
        this.videoDao = videoDao;
        this.courseNotesDao = courseNotesDao;
        this.userdao = userdao;
		this.prefix = prefix;
    }
    
    private User getUser(int userId) throws ClassicDatabaseException, ClassicNotFoundException{
        return userdao.getUser(userId);
    }
    
    private void checkOwnershipCourse(int userId, int courseId) throws ClassicDatabaseException, ClassicNotFoundException, ClassicUnauthorizedException {
        User user = getUser(userId);
        User owner = courseDao.getOwner(courseId);
        if (user.getRole() != Role.ADMIN && user.getId() != owner.getId()) {
            throw new ClassicUnauthorizedException("user with id " + userId + " is not allowed to perform this action");
        }
    }
    
    private void checkOwnershipLecture(int userId, int lectureId) throws ClassicDatabaseException, ClassicNotFoundException, ClassicUnauthorizedException {
        User user = getUser(userId);
        User owner = lectureDao.getOwner(lectureId);
        if (user.getRole() != Role.ADMIN && user.getId() != owner.getId()) {
            throw new ClassicUnauthorizedException("user with id " + userId + " is not allowed to perform this action");
        }
    }

    @Override
    public int addCourse(Course course, int userId) throws ClassicDatabaseException, ClassicNotFoundException {
        course.setOwner(getUser(userId));
        return courseDao.addCourse(course);
    }

    @Override
    public Course getCourse(int courseid) throws ClassicDatabaseException, ClassicNotFoundException {
        Course course = courseDao.getCourse(courseid);
        User owner = courseDao.getOwner(courseid);
        course.setOwner(owner);
        course.setLectures(getLectures(courseid));
        //add course files
        for (Video video: videoDao.getVideos(courseid)) {
            course.addVideo(video);
        }
        //add courseNotes
        for (CourseNotes cn: courseNotesDao.getParentCourseNotes(courseid)) {
            course.addCourseNotes(cn);
        }
        return course;
    }

    @Override
    public List<Course> getCourses() throws ClassicDatabaseException {
        List<Course> courses = courseDao.getCourses();
        for (Course course : courses)
            try {
                course.setOwner(courseDao.getOwner(course.getId()));
                course.setLectures(getLectures(course.getId()));
                //add course files
                for (Video video: videoDao.getVideos(course.getId())) {
                    course.addVideo(video);
                }
                //add courseNotes
                for (CourseNotes cn: courseNotesDao.getParentCourseNotes(course.getId())) {
                    course.addCourseNotes(cn);
                }
            } catch (ClassicNotFoundException ex) {
                // this should never happen
                Logger.getLogger(CourseController.class.getName()).log(Level.SEVERE, null, ex);
            }
        return courses;
    }

    @Override
    public void updateCourse(int courseId, Course course, int userId) throws ClassicDatabaseException, ClassicNotFoundException, ClassicUnauthorizedException {
        checkOwnershipCourse(userId, courseId);
        course.setId(courseId);
        course.setOwner(getUser(userId));
        courseDao.updateCourse(course);
    }

    @Override
    public void deleteCourse(int courseId, int userId) throws ClassicDatabaseException, ClassicNotFoundException {  
		checkOwnershipCourse(userId, courseId);
	
        // first delete courseNotes from this course and all its lectures in the filesystem
        for (CourseNotes cn: courseNotesDao.getParentCourseNotes(courseId)) {
            deleteCN(cn);
        }
        for (Lecture lect: lectureDao.getLectures(courseId)) {
            deleteLecture(lect.getId(), userId);
        }
        // then delete from the db
        courseDao.deleteCourse(courseId);
    }

    @Override
    public int addLecture(int courseId, Lecture lecture, int userId) throws ClassicDatabaseException, ClassicNotFoundException, ClassicUnauthorizedException {
        checkOwnershipCourse(userId, courseId);
        return lectureDao.addLecture(courseId, lecture);
    }

    @Override
    public Lecture getLecture(int lectureId) throws ClassicDatabaseException, ClassicNotFoundException {
        Lecture lecture = lectureDao.getLecture(lectureId);
        //add videos
        for (Video video: videoDao.getVideos(lectureId)) {
            lecture.addVideo(video);
        }
        //add courseNotes
        for (CourseNotes cn: courseNotesDao.getParentCourseNotes(lectureId)) {
            lecture.addCourseNotes(cn);
        }
        return lecture;
    }

    @Override
    public List<Lecture> getLectures(int courseId) throws ClassicDatabaseException, ClassicNotFoundException {
        return lectureDao.getLectures(courseId);
    }

    @Override
    public void updateLecture(int lectureId, Lecture lecture, int userId) throws ClassicDatabaseException, ClassicNotFoundException, ClassicUnauthorizedException {
       checkOwnershipLecture(userId, lectureId);
       lecture.setId(lectureId);
       lectureDao.updateLecture(lecture);
    }

    @Override
    public void deleteLecture(int lectureId, int userId) throws ClassicDatabaseException, ClassicNotFoundException {
		checkOwnershipLecture(userId, lectureId);

        // first delete all courseNotes from this lecture in the filesystem
        for (CourseNotes cn: courseNotesDao.getParentCourseNotes(lectureId)) {
            deleteCN(cn);
        }
        // then delete from the db
        lectureDao.deleteLecture(lectureId);
    }
    
    private void deleteCN(CourseNotes cn) {
        String url = cn.getUrl();
        // Parse pdfName out of the url in the db
        String pdfName = url.substring(url.lastIndexOf('/')+1, url.length());
        // concat the file system prefix to the pdfName
        File cnf = new File(prefix + "/" + pdfName);
        // finally delete
        if (cnf.exists()){
            cnf.delete();
        }
    }
    
}
