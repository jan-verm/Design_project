/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package databaseaccess.exceptions;

/**
 * This exception iss used if an error occurs in the classic database
 * @author jorsi
 */
public class ClassicDatabaseException extends Exception {

    /**
     * Constructs an instance of <code>ClassicDatabaseException</code>.
     */
    public ClassicDatabaseException() {
        super();
    }
    
    /**
     * Constructs an instance of <code>ClassicDatabaseException</code> with
     * the specified detail message.
     * @param message Message to add to the exception.
     */
    public ClassicDatabaseException(String message) {
        super(message);        
    } 
    
    /**
     * Constructs an instance of <code>ClassicDatabaseException</code> with
     * the specified detail message.
     * @param message Message to add to the exception.
     * @param cause Cause of the exception, usualy another exception.
     */
    public ClassicDatabaseException(String message,Throwable cause){
        super(message,cause);        
    } 
    
     
}
