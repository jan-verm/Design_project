/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package models;

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
public class LocationTest {
    
    private Location instance;
    
    @Before
    public void setUp() {
        instance = new Location(5, 10, 20, 25, 2);
    }
    
    @After
    public void tearDown() {
    }

    @Test
    public void testGetX1() {
        double expResult = instance.getX1();
        assertEquals(5, expResult,4);
    }

    @Test
    public void testSetX1() {
        instance.setX1(4);
        double expResult = instance.getX1();
        assertEquals(4, expResult,4);
    }

    @Test
    public void testGetX2() {
        double expResult = instance.getX2();
        assertEquals(10, expResult,4);
    }

    @Test
    public void testSetX2() {
        instance.setX2(9);
        double expResult = instance.getX2();
        assertEquals(9, expResult,4);
    }

    @Test
    public void testGetY1() {
        double expResult = instance.getY1();
        assertEquals(20, expResult,4);
    }

    @Test
    public void testSetY1() {
        instance.setY1(19);
        double expResult = instance.getY1();
        assertEquals(19, expResult,4);
    }

    @Test
    public void testGetY2() {
        double expResult = instance.getY2();
        assertEquals(25, expResult,4);
    }

    @Test
    public void testSetY2() {
        instance.setY2(24);
        double expResult = instance.getY2();
        assertEquals(24, expResult,4);
    }

    @Test
    public void testGetPagenumber() {
        int expResult = instance.getPagenumber();
        assertEquals(expResult, 2);
    }

    @Test
    public void testSetPagenumber() {
        instance.setPagenumber(3);
        int expResult = instance.getPagenumber();
        assertEquals(expResult, 3);
    }
    
    @Test
    public void testGetAndSetId() {
    	instance.setId(1);
    	int id = instance.getId();
    	
    	assertEquals(1, id);
    }
}
