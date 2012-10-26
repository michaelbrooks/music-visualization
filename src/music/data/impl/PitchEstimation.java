/*
 * Music Visualizations: http:/github.com/michaelbrooks/music-visualization
 * Copyright 2012, Michael Brooks. BSD License.
 */

package music.data.impl;

import music.data.DataFrame;
import music.data.PS;
import music.data.PitchInfo;
import music.data.QualityInfo;
import music.data.SpectralFluxCalculator;
import music.data.WSNAC;
import music.data.calc.DPoint;
import music.data.calc.NoteConverter;
import music.data.calc.PeakPicker;
import music.data.calc.Smoother;
import music.data.calc.SmootherTwo;

/**
 *
 * @author michael
 */
public class PitchEstimation implements PitchInfo, QualityInfo {

    DataFrame myFrame;

    public PitchEstimation(DataFrame frame) {
        myFrame = frame;
    }

    static Smoother correlationSmoother = new SmootherTwo(0.333, 0.333);
    static Smoother frequencySmoother = new SmootherTwo(0.333, 0.333);
    static Smoother spectralFluxSmoother = new SmootherTwo(0.333, 0.333);
    static Smoother spectralCentroidSmoother = new SmootherTwo(0.333, 0.333);

    boolean wsnacEstimationDone = false;
    boolean wsnacSmoothingDone = false;
    DPoint wsnacPeak = null;
    double wsnacPitch = 0;
    double smoothPitch = 0;
    double wsnacFrequency = 0;
    double smoothFrequency = 0;
    double wsnacCorrelation = 0;
    double smoothCorrelation = 0;
    double spectralFlux = 0;
    double smoothFlux = 0;
    boolean calculatedSpectralFlux = false;
    boolean doneFluxSmoothing = false;
    double spectralCentroidFrequency = 0;
    double smoothSpectralCentroid = 0;
    boolean calculatedSpectralCentroidFrequency = false;
    boolean doneCentroidSmoothing = false;
    
    private void doWSNACEstimation() {
        if (!wsnacEstimationDone) {
            wsnacEstimationDone = true;
            WSNAC wsnac = myFrame.getWSNAC();
            wsnacPeak = PeakPicker.findFirstPeriodic(wsnac);
            if (wsnacPeak != null) {
                wsnacCorrelation = wsnacPeak.getY();
                wsnacFrequency = wsnac.getFrequency(wsnacPeak.getX());
                wsnacPitch = NoteConverter.frequencyToPitch(wsnacFrequency);
            }
        }
    }

    private void doWSNACSmoothing() {
        if (!wsnacSmoothingDone) {

            doWSNACEstimation();

            if (wsnacPeak != null) {

                double value = getPrimaryCorrelation();
                smoothCorrelation = correlationSmoother.newValue(value);

                value = getPrimaryFrequency();
                smoothFrequency = frequencySmoother.newValue(value);

                smoothPitch = NoteConverter.frequencyToPitch(smoothFrequency);
            }
            else {
                smoothPitch = 1;
                smoothFrequency = 1;
            }

            wsnacSmoothingDone = true;
        }
    }

    public double getPrimaryFrequency() {
        doWSNACEstimation();

        return wsnacFrequency;
    }

    public double getPrimaryPitch() {
        doWSNACEstimation();

        return wsnacPitch;
    }

    public double getPrimaryCorrelation() {
        doWSNACEstimation();

        return wsnacCorrelation;
    }
    
    public double getSpectralFlux() {
        if (!calculatedSpectralFlux) {
            PS myPower = myFrame.getPS();
            spectralFlux = SpectralFluxCalculator.getInstance().newValue(myPower);
            calculatedSpectralFlux = true;
        }
        return spectralFlux;
    }

    public double getSpectralCentroidFrequency() {
        if (!calculatedSpectralCentroidFrequency) {
            PS myPower = myFrame.getPS();
            double[] ps = myPower.getRawData();

            double weightedSum = 0;
            double sumOfWeights = 0;
            for (int i = 0; i < ps.length; i++) {
                weightedSum += ps[i] * i;
                sumOfWeights += ps[i];
            }
            spectralCentroidFrequency = myPower.getFrequency(weightedSum / sumOfWeights);
        }

        return spectralCentroidFrequency;
    }

    public double getSmoothedPitch() {
        doWSNACSmoothing();

        return smoothPitch;
    }

    public double getSmoothedCorrelation() {
        doWSNACSmoothing();

        return smoothCorrelation;
    }

    public double getSmoothedFlux() {
        if (!doneFluxSmoothing) {
            double value = getSpectralFlux();
            smoothFlux = spectralFluxSmoother.newValue(value);
        }

        return smoothFlux;
    }

    public double getSmoothedSpectralCentroidFrequency() {
        if (!doneCentroidSmoothing) {
            double value = getSpectralCentroidFrequency();
            smoothSpectralCentroid = spectralCentroidSmoother.newValue(value);
        }

        return smoothSpectralCentroid;
    }

    public double getSmoothedFrequency() {
        doWSNACSmoothing();

        return smoothFrequency;
    }

    public double getIntonation() {
        double p = getSmoothedPitch();
        return p - Math.round(p);
    }

    public double getIntonation(double[] notePitches) {
        double p = getSmoothedPitch();

        double dist = Double.POSITIVE_INFINITY;
        int bestDistIndex = -1;
        for (int i = 0; i < notePitches.length; i++) {
            if (Math.abs(p - notePitches[i]) < dist) {
                dist = Math.abs(p - notePitches[i]);
                bestDistIndex = i;
            } else {
                break;
            }
        }

        return p - notePitches[bestDistIndex];
    }

}
