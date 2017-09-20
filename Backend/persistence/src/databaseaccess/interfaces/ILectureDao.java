/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package databaseaccess.interfaces;

import courses.Lecture;
import courses.User;
import databaseaccess.exceptions.ClassicDatabaseException;
import databaseaccess.exceptions.ClassicNotFoundException;
import java.util.List;

/**
 *
 * @author jorsi
 */
public interface ILectureDao {
    
    /**
     * Add a lecture to the Classic database.
     * @param parentID Id of the course to which the lecture belongs.
     * @param lecture Lecture object to be added.
     * @return Id of the added lecture.
     * @throws ClassicDatabaseException
     * @throws ClassicNotFoundException
     */
    public int addLecture(int parentID, Lecture lecture) throws ClassicDatabaseException, ClassicNotFoundException;
    
    /**
     * Delete a lecture from the Classic database.
     * @param lectureID Id of the lecture to be deleted.
     * @throws ClassicDatabaseException
     * @throws ClassicNotFoundException
     */
    public void deleteLecture(int lectureID) throws ClassicDatabaseException, ClassicNotFoundException;
    
    /**
     * Get a lecture from the Classic database
     * @param lectureId Id of the lecture to get.
     * @return The wanted lecture object.
     * @throws ClassicDatabaseException
     * @throws ClassicNotFoundException
     */
    public Lecture getLecture(int lectureId) throws ClassicDatabaseException, ClassicNotFoundException;
        
    /**
     * Get a list of all lectures that belong to a specific course.
     * @param parentID Id of the parent course.
     * @return A list of lecture object belonging to a specific course.
     * @throws ClassicDatabaseException
     * @throws ClassicNotFoundException
     */
    public List<Lecture> getLectures(int parentID) throws ClassicDatabaseException, ClassicNotFoundException;
    
    /**
     * Get the owner of a lecture from the Classic database
     * @param courseID
     * @return
     * @throws ClassicDatabaseException
     * @throws ClassicNotFoundException
     */
    public User getOwner(int courseID) throws ClassicDatabaseException, ClassicNotFoundException;
    
    /**
     * Update a lecture in the database. Can be used to edit the lecture name.
     * @param lecture Updated lecture object.
     * @throws ClassicDatabaseException
     * @throws ClassicNotFoundException
     */
    public void updateLecture(Lecture lecture) throws ClassicDatabaseException, ClassicNotFoundException;
    
    /**
     * Delete all entries from the lecture table.
     * @throws ClassicDatabaseException
     */
    public void cleanTable() throws ClassicDatabaseException;
    
}
