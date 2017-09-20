/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package databaseaccess.exceptions;

/**
 *
 * @author jorsi
 */
public class ClassicNotFoundException extends Exception {
       
    public static final String COURSE_OBJECT = "Course";
    public static final String LECTURE_OBJECT = "Lecture";
    public static final String PROFESSOR_OBJECT = "Professor";
    public static final String USER_OBJECT = "User";
    public static final String COMMENT_OBJECT = "Comment";
    public static final String COMMENTREFERENCE_OBJECT = "CommentReference";
    public static final String COURSENOTES_OBJECT = "CourseNotes";
    public static final String COURSENOTESREFERENCE_OBJECT = "CommentReference";
    public static final String LOCATION_OBJECT = "Location";
    public static final String REPLY_OBJECT = "Reply";
    public static final String VIDEO_OBJECT = "Video";
    public static final String VIDEOREFERENCE_OBJECT = "VideoReference";    
    
    /**
     * Constructs an instance of <code>ClassicNotFoundException</code>.
     */
    public ClassicNotFoundException() {        
        super();
    }
    
    /**
     * Constructs an instance of <code>ClassicNotFoundException</code> with
     * a structured message based on the object type and object ID.
     * @param object Object type. For example <code>ClassicNotFoundException.VIDEO_OBJECT</code>
     * @param objectID Id of the object
     */
    public ClassicNotFoundException(String object, int objectID) {        
        super("The object \"" + object + "\" with ID = " + objectID + " was not found in the Classic database.");
    } 
    
    /**
     * Constructs an instance of <code>ClassicNotFoundException</code> with
     * a specified detail message.
     * @param message Message to add to the exception.
     */
    public ClassicNotFoundException(String message) {
        super(message);
    } 
    
    /**
     * Constructs an instance of <code>ClassicNotFoundException</code> with
     * a specified detail message and a cause.
     * @param message Message to add to the exception.
     * @param cause Cause of the exception, usualy another exception.
     */
    public ClassicNotFoundException(String message,Throwable cause){
        super(message,cause);        
    } 
}
