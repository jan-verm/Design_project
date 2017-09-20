/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package databaseaccess.exceptions;

/**
 *
 * @author Juta
 */
public class ClassicUnauthorizedException extends RuntimeException {
    
    /**
     * Constructs an instance of <code>ClassicUnauthorizedException</code>.
     */
    public ClassicUnauthorizedException() {
        super();
    }
    
    /**
     * Constructs an instance of <code>ClassicUnauthorizedException</code> with
     * a specified detail message.
     * @param message Message to add to the exception.
     */
    public ClassicUnauthorizedException(String message) {
        super(message);        
    } 
    
    /**
     * Constructs an instance of <code>ClassicUnauthorizedException</code> with
     * a specified detail message and a cause.
     * @param message Message to add to the exception.
     * @param cause Cause of the exception, usualy another exception.
     */
    public ClassicUnauthorizedException(String message,Throwable cause){
        super(message,cause);        
    } 
    
}
