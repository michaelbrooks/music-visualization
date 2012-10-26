/*
 * Music Visualizations: http:/github.com/michaelbrooks/music-visualization
 * Copyright 2012, Michael Brooks. BSD License.
 */

package music;

import graphics.ArrayLineGraphPanel;
import graphics.ArrayRingGraphPanel;
import graphics.MultiLineGraphPanel;
import graphics.ScanningLineGraphPanel;
import graphics.WaterfallGraphPanel;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import music.data.DataFrame;
import music.data.calc.NoteConverter;

/**
 *
 * @author michael
 */
public class InterviewGUIManager extends AbstractGUIManager {

    WaterfallGraphPanel powerSpectrumPhase0Waterfall;
    WaterfallGraphPanel powerSpectrumPhase0_5Waterfall;
    MultiLineGraphPanel pitchLoudnessBrightness;
    MultiLineGraphPanel intonationLoudnessBrightness;
    ArrayRingGraphPanel powerSpectrumRing;
    ArrayRingGraphPanel reversePowerSpectrumRing;
    ScanningLineGraphPanel specCentroidLine;
    ArrayLineGraphPanel wavPanel;

    @Override
    public void initialize() {

        super.initialize();

        powerSpectrumPhase0Waterfall = new WaterfallGraphPanel("Waterfall (Color phase 0)", 800, 600, 2, 0);
        powerSpectrumPhase0Waterfall.getGraph().setDataAxisAuto(true);
        powerSpectrumPhase0Waterfall.getGraph().setColorMinMax(1, 30);
        addGraphPanel("Spectrum Waterfall (Phase 0)", powerSpectrumPhase0Waterfall);

        powerSpectrumPhase0_5Waterfall = new WaterfallGraphPanel("Waterfall (Color phase 0.5)", 800, 600, 2, 0.5);
        powerSpectrumPhase0_5Waterfall.getGraph().setDataAxisAuto(true);
        powerSpectrumPhase0_5Waterfall.getGraph().setColorMinMax(1, 30);
        addGraphPanel("Spectrum Waterfall (Phase 0.5)", powerSpectrumPhase0_5Waterfall);

        pitchLoudnessBrightness = new MultiLineGraphPanel(800, 3, 50, "Pitch/Loudness/Brighness", 800, 600);
        pitchLoudnessBrightness.getGraph().setColor(0, Color.yellow);
        pitchLoudnessBrightness.getGraph().setColor(1, Color.blue);
        pitchLoudnessBrightness.getGraph().setColor(2, Color.green);
        pitchLoudnessBrightness.getGraph().setConnectPoints(0, false);
        pitchLoudnessBrightness.getGraph().setConnectPoints(1, false);
        pitchLoudnessBrightness.getGraph().setConnectPoints(2, false);
        pitchLoudnessBrightness.getGraph().setVerticalRange(0, 40, 110);
        pitchLoudnessBrightness.getGraph().setVerticalRange(2, 1, 12);
        pitchLoudnessBrightness.getGraph().plotMinValue(0, false);
        pitchLoudnessBrightness.getGraph().plotMinValue(1, false);
        pitchLoudnessBrightness.getGraph().plotMinValue(2, false);
        pitchLoudnessBrightness.getGraph().setLabel(0, "Pitch");
        pitchLoudnessBrightness.getGraph().setLabel(1, "Loudness");
        pitchLoudnessBrightness.getGraph().setLabel(2, "Brightness");
        addGraphPanel("Pitch/Loudness/Brightness", pitchLoudnessBrightness);

        intonationLoudnessBrightness = new MultiLineGraphPanel(800, 3, 50, "Intonation/Loudness/Brighness", 800, 600);
        intonationLoudnessBrightness.getGraph().setColor(0, Color.yellow);
        intonationLoudnessBrightness.getGraph().setColor(1, Color.blue);
        intonationLoudnessBrightness.getGraph().setColor(2, Color.green);
        intonationLoudnessBrightness.getGraph().setConnectPoints(0, false);
        intonationLoudnessBrightness.getGraph().setConnectPoints(1, false);
        intonationLoudnessBrightness.getGraph().setConnectPoints(2, false);
        intonationLoudnessBrightness.getGraph().setVerticalRange(0, -0.5, 0.5);
        intonationLoudnessBrightness.getGraph().setVerticalRange(2, 1, 12);
        intonationLoudnessBrightness.getGraph().plotMinValue(1, false);
        intonationLoudnessBrightness.getGraph().plotMinValue(2, false);
        intonationLoudnessBrightness.getGraph().setLabel(0, "Intonation");
        intonationLoudnessBrightness.getGraph().setLabel(1, "Loudness");
        intonationLoudnessBrightness.getGraph().setLabel(2, "Brightness");
        addGraphPanel("Intonation/Loudness/Brightness", intonationLoudnessBrightness);

        powerSpectrumRing = new ArrayRingGraphPanel("Power Ring Low to High", 600, 600);
        powerSpectrumRing.getGraph().setConnectPoints(true);
        addGraphPanel("Power Ring Low to High", powerSpectrumRing);

        reversePowerSpectrumRing = new ArrayRingGraphPanel("Power Ring High to Low", 600, 600);
        reversePowerSpectrumRing.getGraph().setConnectPoints(true);
        addGraphPanel("Power Ring High to Low", reversePowerSpectrumRing);

        specCentroidLine = new ScanningLineGraphPanel(800, "Loudness", 709, 388);
        specCentroidLine.getGraph().setVerticalRangeAuto(true);
        specCentroidLine.getGraph().setConnectPoints(true);
        specCentroidLine.getGraph().setColor(Color.orange);
        addGraphPanel("Loudness", specCentroidLine);

        wavPanel = new ArrayLineGraphPanel("Frequency Spectrum", 709, 388);
        wavPanel.getGraph().setVerticalRangeAuto(true);
        wavPanel.getGraph().setColor(Color.cyan);
        wavPanel.getGraph().setConnectPoints(true);
        addGraphPanel("Frequency Spectrum", wavPanel);

        setupThread(new Runnable() {

            Recorder rec;
            long lastGraphedTime = 0;

            public void run() {
                rec = getAudioComponents().getRecorder();

                while (polling()) {

//                    long time1 = System.nanoTime();
//                                    System.out.println("InterviewGUIManager, while loop, start, " + time1);

                    if (!paused()) {
                        
                        List<DataFrame> latestFrames = rec.popFrames();
                        if (latestFrames.size() > 0) {

                            DataFrame frame = latestFrames.get(latestFrames.size() - 1);

                            if (frame.getTime() != lastGraphedTime) {

                                double pitch = 0;
                                double quality = 0;
                                double spectralFlux = 0;
                                double spectralCentroid = 0;
                                double loudness = 0;
                                double intonation = 0;
                                double normSpectralCentroid = 0;
                                double[] flatPS = null;

                                for (int i = 0; i < latestFrames.size(); i++) {
                                    DataFrame subFrame = latestFrames.get(i);

                                    pitch = subFrame.getPitchInfo().getSmoothedPitch();
                                    quality = subFrame.getQualityInfo().getSmoothedCorrelation();
                                    spectralFlux = subFrame.getQualityInfo().getSmoothedFlux();
                                    spectralCentroid = subFrame.getQualityInfo().getSmoothedSpectralCentroidFrequency();
                                    loudness = subFrame.getSM().getSmoothedLoudness();
                                    intonation = subFrame.getPitchInfo().getIntonation();
                                    
                                    normSpectralCentroid = spectralCentroid / NoteConverter.pitchToFrequency(pitch);

                                    if (subFrame.getSM().isSilence() || pitch <= 1 || quality < 0.2) {
                                        pitch = 0;
                                        quality = 0;
                                        spectralFlux = 0;
                                        normSpectralCentroid = 0;
                                        spectralCentroid = 0;
                                        loudness = 0;
                                        intonation = 0;
                                    }

                                    double[] ps = subFrame.getPS().getRawData();

                                    flatPS = Arrays.copyOf(ps, ps.length);
                                    
                                    for (int j = 0; j < ps.length; j++) {
                                        flatPS[j] = Math.pow(flatPS[j], 0.3);
                                    }

                                    if (powerSpectrumPhase0Waterfall.isVisible()) {
                                        double[] values = Arrays.copyOf(flatPS, flatPS.length / 2);
                                        powerSpectrumPhase0Waterfall.graph(values, normSpectralCentroid);
                                    }

                                    if (powerSpectrumPhase0_5Waterfall.isVisible()) {
                                        double[] values = Arrays.copyOf(flatPS, flatPS.length / 2);
                                        powerSpectrumPhase0_5Waterfall.graph(values, normSpectralCentroid);
                                    }

                                    if (pitchLoudnessBrightness.isVisible()) {
                                        ArrayList<Double> values = new ArrayList<Double>();
                                        values.add(pitch);
                                        values.add(loudness);
                                        values.add(normSpectralCentroid);
                                        pitchLoudnessBrightness.graph(values);
                                    }

                                    if (intonationLoudnessBrightness.isVisible()) {
                                        ArrayList<Double> values = new ArrayList<Double>();
                                        values.add(intonation);
                                        values.add(loudness);
                                        values.add(normSpectralCentroid);
                                        intonationLoudnessBrightness.graph(values);
                                    }
                                    
                                    if (specCentroidLine.isVisible()) {
                                        specCentroidLine.graph(loudness);
                                    }

                                    //System.out.println(normSpectralCentroid);

                                }

                                if (specCentroidLine.isVisible()) {
                                    specCentroidLine.repaint();
                                }

                                if (wavPanel.isVisible()) {
                                    wavPanel.graph(frame.getPS().getRawData());
                                }

                                if (powerSpectrumRing.isVisible()) {
                                    int width = flatPS.length / 4;
                                    if (powerSpectrumRing.getGraph().getRight() != width)
                                        powerSpectrumRing.getGraph().setLeftRightBounds(-1, width);
                                    powerSpectrumRing.graph(flatPS);
                                }

                                if (reversePowerSpectrumRing.isVisible()) {
                                    int width = flatPS.length / 4;
                                    if (reversePowerSpectrumRing.getGraph().getRight() != width)
                                        reversePowerSpectrumRing.getGraph().setLeftRightBounds(-1, width);
                                    reversePowerSpectrumRing.graph(flatPS, true);
                                }

                                if (powerSpectrumPhase0Waterfall.isVisible()) {
//                                    long time0 = System.nanoTime();
//                                    System.out.println("InterviewGUIManager, repaint, scheduled, " + time0);
                                    powerSpectrumPhase0Waterfall.repaint();
                                }

                                if (powerSpectrumPhase0_5Waterfall.isVisible()) {
                                    powerSpectrumPhase0_5Waterfall.repaint();
                                }

                                if (pitchLoudnessBrightness.isVisible()) {
                                    pitchLoudnessBrightness.repaint();
                                }

                                if (intonationLoudnessBrightness.isVisible()) {
                                    intonationLoudnessBrightness.repaint();
                                }


                            }

                            lastGraphedTime = frame.getTime();
                        }

                    }

                    try {
                        Thread.sleep(4);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }


        });
    }
    int p = 0;
}
