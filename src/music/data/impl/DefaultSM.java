/*
 * Music Visualizations: http:/github.com/michaelbrooks/music-visualization
 * Copyright 2012, Michael Brooks. BSD License.
 */

package music.data.impl;

import music.SimpleNoiseFilter;
import music.data.DataFrame;
import music.data.SM;
import music.data.Wav;
import music.data.calc.Smoother;
import music.data.calc.SmootherTwo;

/**
 *
 * @author michael
 */
public class DefaultSM implements SM {

    private DataFrame myFrame;

    private boolean calculated = false;

    private double avgAmplitude;
    private double meanSquaredAmplitude;
    private double powerDB;
    private double maxAmplitude;
    private double minAmplitude;
    private double loudness;
    
    private boolean smoothed = false;

    static Smoother maxAmplitudeSmoother = new SmootherTwo(0.333, 0.333);
    static Smoother minAmplitudeSmoother = new SmootherTwo(0.333, 0.333);
    static Smoother avgAmplitudeSmoother = new SmootherTwo(0.333, 0.333);
    static Smoother meanSquaredAmplitudeSmoother = new SmootherTwo(0.333, 0.333);
    static Smoother powerDBSmoother = new SmootherTwo(0.333, 0.333);
    static Smoother loudnessSmoother = new SmootherTwo(0.333, 0.333);
    
    private double smoothedAvgAmplitude;
    private double smoothedMeanSquaredAmplitude;
    private double smoothedPowerDB;
    private double smoothedMaxAmplitude;
    private double smoothedMinAmplitude;
    private double smoothedLoudness;
    
    public DefaultSM(DataFrame frame) {
        this.myFrame = frame;
    }

    private void calculate() {
        Wav wav = myFrame.getWav();
        double[] wavData = wav.getRawData();

        maxAmplitude = Double.NEGATIVE_INFINITY;
        minAmplitude = Double.POSITIVE_INFINITY;
        avgAmplitude = 0;
        meanSquaredAmplitude = 0;
        double sample;
        for (int i = 0; i < wavData.length; i++) {
            sample = wavData[i];
            avgAmplitude += Math.abs(sample);
            meanSquaredAmplitude += sample * sample;
            maxAmplitude = Math.max(maxAmplitude, sample);
            minAmplitude = Math.min(minAmplitude, sample);
        }
        avgAmplitude /= wavData.length;
        meanSquaredAmplitude /= wavData.length;

        powerDB = 20 * Math.log10(meanSquaredAmplitude);
        if (meanSquaredAmplitude < 0.000000001)
            powerDB = -200;

        double silenceMSA = SimpleNoiseFilter.getSilentMSA();
        loudness = 10 * Math.log(meanSquaredAmplitude / silenceMSA);

        calculated = true;
    }

    private void smooth() {
        if (!calculated)
            calculate();

        if (!smoothed) {
            smoothedMaxAmplitude = maxAmplitudeSmoother.newValue(maxAmplitude);
            smoothedMinAmplitude = minAmplitudeSmoother.newValue(minAmplitude);
            smoothedAvgAmplitude = avgAmplitudeSmoother.newValue(avgAmplitude);
            smoothedMeanSquaredAmplitude = meanSquaredAmplitudeSmoother.newValue(meanSquaredAmplitude);
            smoothedPowerDB = powerDBSmoother.newValue(powerDB);
            smoothedLoudness = loudnessSmoother.newValue(loudness);
            
            smoothed = true;
        }
    }

    public double getMaxAmplitude() {
        if (!calculated)
            calculate();
        return maxAmplitude;
    }

    public double getMinAmplitude() {
        if (!calculated)
            calculate();
        return minAmplitude;
    }

    public double getAverageAmplitude() {
        if (!calculated)
            calculate();
        return avgAmplitude;
    }

    public double getPowerDB() {
        if (!calculated)
            calculate();
        return powerDB;
    }

    public double getMeanSquaredAmplitude() {
        if (!calculated)
            calculate();
        return meanSquaredAmplitude;
    }

    static double silentCluster = -170;
    static double noiseCluster = -100;
    static int silentClusterSize = 0;
    static int noiseClusterSize = 0;

    private boolean isSilenceClustering() {
        double pow = getPowerDB();

        double distToSilent = Math.abs(silentCluster - pow);
        double distToNoise = Math.abs(noiseCluster - pow);

        if (silentClusterSize < 20 || noiseClusterSize < 20) {

            if (distToSilent < distToNoise) {
                distToSilent = silentCluster * silentClusterSize;
                distToSilent += pow;
                silentClusterSize++;
                silentCluster = distToSilent / silentClusterSize;
            } else {
                distToNoise = noiseCluster * noiseClusterSize;
                distToNoise += pow;
                noiseClusterSize++;
                noiseCluster = distToNoise / noiseClusterSize;
            }

        }

        //System.out.println("silence = " + silentCluster + ", noise = " + noiseCluster);

        if (distToSilent < distToNoise) {
            return true;
        }
        else return false;
    }

    static double minimumPower = 0;
    static double minimumAverage = 0;
    static int numInMinAverage = 0;
    
    private boolean isSilenceMin() {
        double power = getPowerDB();
        
        if (power < minimumPower) {
            minimumAverage *= numInMinAverage;
            minimumAverage += power;
            numInMinAverage++;
            minimumAverage /= numInMinAverage;
            minimumPower = power;
        }

        //System.out.println("minavg = " + minimumAverage);

        if (power < 0.9 * minimumAverage)
            return true;

        return false;
    }

    public double getLoudness() {
        if (!calculated)
            calculate();

        return loudness;
    }

    public double getSmoothedLoudness() {
        if (!smoothed)
            smooth();

        return smoothedLoudness;
    }

    public boolean isSilence() {

        if (getSmoothedLoudness() < 6) {
            return true;
        }
        else return false;

        
//        boolean b1 = isSilenceMin();
//        boolean b2 = isSilenceClustering();
//        if (b1 == b2) {
//            return b1;
//        }
//        else return false;
    }

    public double getSmoothedAverageAmplitude() {
        if (!smoothed)
            smooth();

        return smoothedAvgAmplitude;
    }

    public double getSmoothedPowerDB() {
        if (!smoothed)
            smooth();

        return smoothedPowerDB;
    }

    public double getSmoothedMeanSquaredAmplitude() {
        if (!smoothed)
            smooth();

        return smoothedMeanSquaredAmplitude;
    }

    public double getSmoothedMaxAmplitude() {
        if (!smoothed)
            smooth();

        return smoothedMaxAmplitude;
    }

    public double getSmoothedMinAmplitude() {
        if (!smoothed)
            smooth();

        return smoothedMinAmplitude;
    }

}
