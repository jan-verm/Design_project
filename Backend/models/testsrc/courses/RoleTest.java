/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package courses;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Juta
 */
public class RoleTest {
    
    public RoleTest() {
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

    /**
     * Test of values method, of class Role.
     */
    @Test
    public void testValues() {
        Role[] expResult = new Role[]{Role.NONE, Role.ADMIN, Role.TEACHER, Role.STUDENT};
        Role[] result = Role.values();
        assertArrayEquals(expResult, result);
    }

    /**
     * Test of valueOf method, of class Role.
     */
    @Test
    public void testValueOf() {
        Role expResult = Role.NONE;
        Role result = Role.valueOf("NONE");
        assertEquals(expResult, result);
    }

    /**
     * Test of getValue method, of class Role.
     */
    @Test
    public void testGetValue() {
        Role instance = Role.NONE;
        int expResult = 0;
        int result = instance.getValue();
        assertEquals(expResult, result);
    }
    
}
