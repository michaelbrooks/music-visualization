/*
 * Music Visualizations: http:/github.com/michaelbrooks/music-visualization
 * Copyright 2012, Michael Brooks. BSD License.
 */

package music.data.calc;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author michael
 */
public class ParabolaTest {

    public ParabolaTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of getValue method, of class Parabola.
     */
    @Test
    public void testGetValue() {
        System.out.println("getValue");
        Parabola instance = new Parabola(4, 2, 1);
        double x = 0.0;
        double expResult = 1;
        double result = instance.getValue(x);
        System.out.println("x = " + x);
        assertEquals(expResult, result, 0.0);
        x = 2;
        expResult = 21;
        result = instance.getValue(x);
        System.out.println("x = " + x);
        assertEquals(expResult, result, 0.0);
    }

    /**
     * Test of getExtreme method, of class Parabola.
     */
    @Test
    public void testGetExtreme() {
        System.out.println("getExtreme");
        Parabola instance = new Parabola(-4, 2, 1);
        DPoint expResult = new DPoint(0.25, 1.25);
        DPoint result = instance.getExtreme();
        assertEquals(expResult, result);
    }

    /**
     * Test of interpolate method, of class Parabola.
     */
    @Test
    public void testInterpolate() {
        System.out.println("interpolate");
        Parabola instance = new Parabola(-4, 2, 1);
        DPoint left = new DPoint(-2, instance.getValue(-2));
        DPoint mid = new DPoint(0, instance.getValue(0));
        DPoint right = new DPoint(4, instance.getValue(4));
        Parabola expResult = instance;
        Parabola result = Parabola.interpolate(left, mid, right);
        assertEquals(expResult, result);
        DPoint expExtreme = instance.getExtreme();
        DPoint resultExtreme = result.getExtreme();
        assertEquals(expExtreme, resultExtreme);
    }

}