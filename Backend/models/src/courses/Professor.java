/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package courses;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Juta
 */
public class Professor extends User {
    
    private List<Course> managedCourses;
    
    public Professor(String username, String password) {
        super(username, Role.TEACHER, password);
        managedCourses = new ArrayList<>();
    }

    /**
     * set courses of prof
     * @return
     */
    public List<Course> getManagedCourses() {
        return managedCourses;
    }

    /**
     * get courses of prof
     * @param managedCourses courses the prof manages
     */
    public void setManagedCourses(List<Course> managedCourses) {
        this.managedCourses = managedCourses;
    }
    
    /**
     * add new course of prof
     * @param managedCourses
     */
    public void addManagedCourse(Course course) {
        managedCourses.add(course);
    }
}
