/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package models;

import courses.Role;
import courses.User;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Juta
 */
public class CommentReferenceTest {

    /**
     * Test of getComment method, of class AnnotationReference.
     */
    @Test
    public void testGetAnnotation() {
        Comment expResult = new Comment("this is the content of the annotation", false);
        CommentReference instance = new CommentReference(expResult);
        Comment result = instance.getComment();
        assertEquals(expResult, result);
    }
    
}
