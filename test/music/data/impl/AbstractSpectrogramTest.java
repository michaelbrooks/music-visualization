/*
 * Music Visualizations: http:/github.com/michaelbrooks/music-visualization
 * Copyright 2012, Michael Brooks. BSD License.
 */

package music.data.impl;

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
public class AbstractSpectrogramTest {

    public AbstractSpectrogramTest() {
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
     * Test of isValidBin method, of class AbstractSpectrogram.
     */
    @Test
    public void testIsValidBin() {
        System.out.println("isValidBin");
        int bin = 0;
        AbstractSpectrogram instance = new AbstractSpectrogramImpl(2048, 2048, 44100);
        boolean expResult = true;
        boolean result = instance.isValidBin(bin);
        assertEquals(expResult, result);
        bin = 2048;
        expResult = false;
        result = instance.isValidBin(bin);
        assertEquals(expResult, result);
    }

    /**
     * Test of getFrequency method, of class AbstractSpectrogram.
     */
    @Test
    public void testGetFrequency() {
        System.out.println("getFrequency");
        
        AbstractSpectrogram instance = new AbstractSpectrogramImpl(2048, 2048, 44100);
        double totalTime = 2048 / 44100.0;
        double binWidth = 1 / totalTime;

        double expResult, result;
        for (int bin = 0; bin < 2048; bin++) {
            expResult = bin * binWidth;
            result = instance.getFrequency(bin);
            assertEquals(expResult, result, 0.0000001);
        }
    }

    /**
     * Test of getBin method, of class AbstractSpectrogram.
     */
    @Test
    public void testGetBin() {
        System.out.println("getBin");
        double frequency = 0.0;
        AbstractSpectrogram instance = new AbstractSpectrogramImpl(2048, 2048, 44100);
        double totalTime = 2048 / 44100.0;
        double binWidth = 1 / totalTime;

        double result, expResult;
        for (int bin = 0; bin < 2048; bin++) {
            expResult = bin;
            frequency = bin * binWidth;
            result = instance.getBin(frequency);
            assertEquals(expResult, result, 0.0000001);
        }
    }

    /**
     * Test of getN method, of class AbstractSpectrogram.
     */
    @Test
    public void testGetN() {
        System.out.println("getN");
        AbstractSpectrogram instance = new AbstractSpectrogramImpl(1024, 2048, 44100);
        int expResult = 1024;
        int result = instance.getN();
        assertEquals(expResult, result);
    }

    /**
     * Test of getSampleRate method, of class AbstractSpectrogram.
     */
    @Test
    public void testGetSampleRate() {
        System.out.println("getSampleRate");
        AbstractSpectrogram instance = new AbstractSpectrogramImpl(2048, 2048, 44100);
        double expResult = 44100;
        double result = instance.getSampleRate();
        assertEquals(expResult, result, 0.0);
    }

    /**
     * Test of size method, of class AbstractSpectrogram.
     */
    @Test
    public void testSize() {
        System.out.println("size");
        AbstractSpectrogram instance = new AbstractSpectrogramImpl(1024, 2048, 44100);
        int expResult = 2048;
        int result = instance.size();
        assertEquals(expResult, result);
    }

    public class AbstractSpectrogramImpl extends AbstractSpectrogram {

        public AbstractSpectrogramImpl(int n, int size, double sampleRate) {
            super(n, size, sampleRate);
        }

        public double[] getRawData() {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }

}