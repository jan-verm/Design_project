/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package courses;


import java.util.ArrayList;
import java.util.List;
import models.CourseNotes;
import models.Video;

/**
 * Lecture object
 * @author Juta
 */
public class Lecture {
    private List<Video> videos;
    private List<CourseNotes> courseNotes;
    private String name;
    private int id;
    
    /**
     * Creates a lecture with given name
     * @param name name of the lecture
     */
    public Lecture(String name) {
        this.name = name;
        videos = new ArrayList<>();
        courseNotes = new ArrayList<>();
    }
    
    /**
     * returns the videos
     * @return list of videos
     */
    public List<Video> getVideos() {
        return videos;
    }
    
    /**
     * adds a new video
     * @param video new video
     */
    public void addVideo(Video video) {
        videos.add(video);
    }

    /**
     * changes the videos
     * @param videos
     */
    public void setVideos(List<Video> videos) {
        this.videos = videos;
    }

    /**
     * returns the course notes
     * @return list of course notes
     */
    public List<CourseNotes> getCourseNotes() {
        return courseNotes;
    }
    
    /**
     * adds a new course notes
     * @param courseNote new course notes
     */
    public void addCourseNotes(CourseNotes courseNote) {
        courseNotes.add(courseNote);
    }

    /**
     * changes all the course notes
     * @param courseNotes list of new course notes
     */
    public void setCourseNotes(List<CourseNotes> courseNotes) {
        this.courseNotes = courseNotes;
    }

    /**
     * returns the name
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * Changes the name
     * @param name new name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     *
     * @return
     */
    public int getId() {
        return id;
    }

    /**
     *
     * @param id
     */
    public void setId(int id) {
        this.id = id;
    }
}
