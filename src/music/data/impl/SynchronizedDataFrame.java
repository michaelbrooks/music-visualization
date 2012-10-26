/*
 * Music Visualizations: http:/github.com/michaelbrooks/music-visualization
 * Copyright 2012, Michael Brooks. BSD License.
 */

package music.data.impl;

import music.data.ACF;
import music.data.AMDF;
import music.data.DataFrame;
import music.data.FFT;
import music.data.PS;
import music.data.PitchInfo;
import music.data.QualityInfo;
import music.data.SDF;
import music.data.SM;
import music.data.SNAC;
import music.data.WACF;
import music.data.WSNAC;
import music.data.Wav;

/**
 *
 * @author michael
 */
public class SynchronizedDataFrame implements DataFrame {

    private DataFrame frame;

    public SynchronizedDataFrame(DataFrame frame)
    {
        this.frame = frame;
    }

    public long getSessionID() {
        return frame.getSessionID();
    }

    public long getTime() {
        return frame.getTime();
    }

    public Wav getWav() {
        return frame.getWav();
    }

    public synchronized PS getPS() {
        return frame.getPS();
    }

    public synchronized FFT getFFT() {
        return frame.getFFT();
    }

    public synchronized WACF getWACF() {
        return frame.getWACF();
    }

    public synchronized ACF getACF() {
        return frame.getACF();
    }

    public synchronized SNAC getSNAC() {
        return frame.getSNAC();
    }

    public synchronized WSNAC getWSNAC() {
        return frame.getWSNAC();
    }

    public synchronized SDF getSDF() {
        return frame.getSDF();
    }

    public synchronized AMDF getAMDF() {
        return frame.getAMDF();
    }

    public int compareTo(Long o) {
        return frame.compareTo(o);
    }

    public synchronized PS getWPS() {
        return frame.getWPS();
    }

    public synchronized FFT getWFFT() {
        return frame.getWFFT();
    }

    public synchronized SM getSM() {
        return frame.getSM();
    }

    public synchronized PitchInfo getPitchInfo() {
        return frame.getPitchInfo();
    }

    public synchronized QualityInfo getQualityInfo() {
        return frame.getQualityInfo();
    }

}
