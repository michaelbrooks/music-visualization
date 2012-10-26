/*
 * Music Visualizations: http:/github.com/michaelbrooks/music-visualization
 * Copyright 2012, Michael Brooks. BSD License.
 */

package music.data.impl;

import music.data.DataFrame;
import music.data.PitchInfo;
import music.data.QualityInfo;
import music.data.SM;
import music.data.Wav;

/**
 *
 * @author michael
 */
public abstract class AbstractDataFrame implements DataFrame {
    
    private long sessionID;
    private long timeInSession;
    private Wav flatWav = null;
    private Wav originalWav = null;
    private SM mySM = null;
    private PitchEstimation myPitchEstimation = null;
    
    protected AbstractDataFrame(Wav wav, long time, long sessionID) {
        this.originalWav = wav;
        this.timeInSession = time;
        this.sessionID = sessionID;
    }

    protected AbstractDataFrame(long time, long sessionID) {
        this.timeInSession = time;
        this.sessionID = sessionID;
    }

    public int compareTo(Long o) {
        return (int) (o - getTime());
    }

    public long getSessionID() {
        return sessionID;
    }

    public long getTime() {
        return timeInSession;
    }
    
    public Wav getWav() {
        if (flatWav == null) {
            flatWav = removeDC(originalWav);
        }
        return flatWav;
    }

    protected void setWav(Wav flatWav) {
        this.flatWav = flatWav;
        this.originalWav = flatWav;
    }

    private Wav removeDC(Wav wav) {
        double[] wavData = wav.getRawData();

        //Find the average
        double avg = 0;
        for (int i = 0; i < wav.size(); i++) {
            avg += wavData[i];
        }
        avg /= wavData.length;

        //Remove the average
        double[] newData = new double[wav.size()];
        for (int i = 0; i < newData.length; i++) {
            newData[i] = wavData[i] - avg;
        }

        return new DefaultWav(newData, wav.getSampleRate());
    }

    public SM getSM() {
        if (mySM == null) {
            mySM = new DefaultSM(this);
        }
        return mySM;
    }

    public PitchInfo getPitchInfo() {
        if (myPitchEstimation == null) {
            myPitchEstimation = new PitchEstimation(this);
        }
        return myPitchEstimation;
    }

    public QualityInfo getQualityInfo() {
        if (myPitchEstimation == null) {
            myPitchEstimation = new PitchEstimation(this);
        }
        return myPitchEstimation;
    }
}
