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
public class ClassicNameTakenException extends RuntimeException {

    /**
     * Creates a new instance of <code>ClassicUsernameTakenException</code>
     * without detail message.
     */
    public ClassicNameTakenException() {
    }

    /**
     * Constructs an instance of <code>ClassicUsernameTakenException</code> with
     * the specified detail message.
     *
     * @param msg the detail message.
     */
    public ClassicNameTakenException(String msg) {
        super(msg);
    }
    
   /**
     * Constructs an instance of <code>ClassicUsernameTakenException</code> with
     * the specified detail message.
     *
     * @param msg the detail message.
     * @param cause the cause of the exception
     */
    public ClassicNameTakenException(String msg, Throwable cause) {
        super(msg,cause);
    } 
    
}
