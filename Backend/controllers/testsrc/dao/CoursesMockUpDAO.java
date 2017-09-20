/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import courses.Course;
import courses.Lecture;
import courses.Role;
import courses.User;
import databaseaccess.exceptions.ClassicDatabaseException;
import databaseaccess.exceptions.ClassicNotFoundException;
import databaseaccess.interfaces.ICourseDao;
import databaseaccess.interfaces.ILectureDao;
import databaseaccess.interfaces.IUserDao;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Juta
 */
public class CoursesMockUpDAO implements ICourseDao, ILectureDao, IUserDao {
    
    private List<Course> courses = new ArrayList<>();
    private List<User> users = new ArrayList<>();
    private int idCounter = 0;
    

    @Override
    public int addCourse(Course course) throws ClassicDatabaseException {
        idCounter++;	
        course.setId(idCounter);
        courses.add(course);
        return idCounter;
    }

    @Override
    public void deleteCourse(int courseID) throws ClassicDatabaseException, ClassicNotFoundException {
        Course c = null;
        for (Course course : courses) {
            if (course.getId() == courseID) {
                c = course;
            }
        }
        if (c == null) {
            throw new ClassicNotFoundException();
        }
        courses.remove(c);
    }

    @Override
    public Course getCourse(int courseID) throws ClassicDatabaseException, ClassicNotFoundException {
        for (Course course : courses) {
            if (course.getId() == courseID) {
                return course;
            }
        }
        throw new ClassicNotFoundException();
    }

    @Override
    public List<Course> getCourses() throws ClassicDatabaseException {
        return courses;
    }

    @Override
    public void updateCourse(Course course) throws ClassicDatabaseException, ClassicNotFoundException {
        Course old = null;
        for (Course c : courses) {
            if (course.getId() == c.getId()) {
                old = c;
            }
        }
        if (old == null) {
            throw new ClassicNotFoundException();
        }
        courses.remove(old);
        courses.add(course);
    }

    @Override
    public void cleanTable() throws ClassicDatabaseException {
        courses.clear();
        users.clear();
        
    }

    @Override
    public int addLecture(int parentID, Lecture lecture) throws ClassicDatabaseException, ClassicNotFoundException {
        Course c = getCourse(parentID);
        idCounter++;
        lecture.setId(idCounter);
        c.addLecture(lecture);
        return idCounter;
    }

    @Override
    public void deleteLecture(int lectureID) throws ClassicDatabaseException, ClassicNotFoundException {
        Lecture l = null;
        for (Course course : courses) {
            for (Lecture lecture : course.getLectures())
            if (lecture.getId() == lectureID) {
                l = lecture;
            }
            if (l != null){
                course.getLectures().remove(l);
                break;
            }
        }
        if (l == null) {
            throw new ClassicNotFoundException();
        }
    }

    @Override
    public Lecture getLecture(int lectureId) throws ClassicDatabaseException, ClassicNotFoundException {
        for (Course course : courses) {
            for (Lecture lecture : course.getLectures())
            if (lecture.getId() == lectureId) {
               return lecture;
            }
        }
        throw new ClassicNotFoundException();
    }

    @Override
    public List<Lecture> getLectures(int parentID) throws ClassicDatabaseException, ClassicNotFoundException {
        return getCourse(parentID).getLectures();
    }

    @Override
    public void updateLecture(Lecture lecture) throws ClassicDatabaseException, ClassicNotFoundException {
        Lecture l = getLecture(lecture.getId());
        l.setName(lecture.getName());
        l.setCourseNotes(lecture.getCourseNotes());
        l.setVideos(lecture.getVideos());
    }

    @Override
    public int addUser(User user) throws ClassicDatabaseException {
        idCounter++;
        user.setId(idCounter);
        users.add(user);
        return idCounter;
    }

    @Override
    public void deleteUser(int userID) throws ClassicDatabaseException, ClassicNotFoundException {
        User u = null;
        for (User user : users) {
            if (user.getId() == userID) {
                u = user;
            }
        }
        if (u == null) {
            throw new ClassicNotFoundException();
        }
        users.remove(u);
    }

    @Override
    public User getUser(int userID) throws ClassicDatabaseException, ClassicNotFoundException {
        for (User user : users) {
            if (user.getId() == userID) {
                return user;
            }
        }
        throw new ClassicNotFoundException();
    }

    @Override
    public User getUser(String username) throws ClassicDatabaseException, ClassicNotFoundException {
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }
        throw new ClassicNotFoundException();
    }

    @Override
    public List<User> getUsers(Role role) throws ClassicDatabaseException {
        List<User> result = new ArrayList<>();
        for (User user : users) {
            if (user.getRole() == role) {
                result.add(user);
            }
        }
        return result;
    }

    @Override
    public List<User> getUsers() throws ClassicDatabaseException {
        return users;
    }

    @Override
    public void updateUser(User user) throws ClassicDatabaseException, ClassicNotFoundException {
        User u = getUser(user.getId());
        u.setRole(user.getRole());
        u.setUsername(user.getUsername());
        u.setSubscriptions(user.getSubscriptions());
    }

    @Override
    public void subscribeCourse(int userID, int courseID) throws ClassicDatabaseException, ClassicNotFoundException {
        Course c = getCourse(courseID);
        User u = getUser(userID);
        u.addSubscription(c);
    }

    @Override
    public void unsubscribeCourse(int userID, int courseID) throws ClassicDatabaseException, ClassicNotFoundException {
        Course c = getCourse(courseID);
        User u = getUser(userID);
        u.getSubscriptions().remove(0);
    }

    @Override
    public User getOwner(int courseID) throws ClassicDatabaseException, ClassicNotFoundException {
        return users.get(0);
    }

    @Override
    public void updateUserPassword(int userID, String newPassword) throws ClassicDatabaseException, ClassicNotFoundException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isTaken(String username) throws ClassicDatabaseException {
        return false;
    }

    @Override
    public List<Course> getSubscriptions(int userID) throws ClassicDatabaseException, ClassicNotFoundException {
        return getUser(userID).getSubscriptions();
    }
    
}
