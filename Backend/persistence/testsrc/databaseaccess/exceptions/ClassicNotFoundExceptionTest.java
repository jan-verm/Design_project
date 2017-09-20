/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package databaseaccess.exceptions;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author jorsi
 */
public class ClassicNotFoundExceptionTest {
    
    public ClassicNotFoundExceptionTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    @Test(expected = ClassicNotFoundException.class)
    public void testConstructor1() throws Exception {
        throw new ClassicNotFoundException();
    }
    
    @Test(expected = ClassicNotFoundException.class)
    public void testConstructor2() throws Exception {
        throw new ClassicNotFoundException("TestException");   
    }
    
    @Test(expected = ClassicNotFoundException.class)
    public void testConstructor3() throws Exception {
        throw new ClassicNotFoundException("TestException", new Exception());    
    }
    
}
