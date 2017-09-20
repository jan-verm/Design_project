package exceptions;

/**
 *
 * @author ?
 */
public class ClassicCharacterNotAllowedException extends RuntimeException {    
    
    /**
     * Creates a new instance of <code>ClassicCharacterNotAllowedException</code>
     * without detail message.
     */
    public ClassicCharacterNotAllowedException() {
    }

    /**
     * Constructs an instance of <code>ClassicCharacterNotAllowedException</code> with
     * the specified detail message.
     *
     * @param msg the detail message.
     */
    public ClassicCharacterNotAllowedException(String msg) {
        super(msg);
    }
    
   /**
     * Constructs an instance of <code>ClassicCharacterNotAllowedException</code> with
     * the specified detail message.
     *
     * @param msg the detail message.
     * @param cause the cause of the exception
     */
    public ClassicCharacterNotAllowedException(String msg, Throwable cause) {
        super(msg,cause);
    } 
    
}
