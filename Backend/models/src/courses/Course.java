/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package courses;

import java.util.ArrayList;
import java.util.List;

/**
 * Course object
 * @author Juta
 */
public class Course extends Lecture {

    private List<Lecture> lectures;
    private User owner;
    
    /**
     * Creates a course with given name
     * @param name name of the course
     */
    public Course(String name) {
        super(name);
        lectures = new ArrayList<>();
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }
    
    /**
     * returns the lectures
     * @return list of lectures
     */
    public List<Lecture> getLectures() {
        return lectures;
    }
    
    /**
     * adds a a new lecture
     * @param lecture new lecture
     */
    public void addLecture(Lecture lecture) {
        lectures.add(lecture);
    }

    /**
     * changes the lectures
     * @param lectures new list of lectures
     */
    public void setLectures(List<Lecture> lectures) {
        this.lectures = lectures;
    }   
    
}
