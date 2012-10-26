/*
 * Music Visualizations: http:/github.com/michaelbrooks/music-visualization
 * Copyright 2012, Michael Brooks. BSD License.
 */

package music;

import graphics.ArrayLineGraphPanel;
import graphics.WaterfallGraphPanel;
import graphics.WeightedColorLineGraphPanel;
import java.util.Arrays;
import java.util.List;
import music.data.DataFrame;

/**
 *
 * @author michael
 */
public class PowerWCLGUIManager extends AbstractGUIManager {

    ArrayLineGraphPanel powerGraphPanel;
    ArrayLineGraphPanel wacfGraphPanel;
    WeightedColorLineGraphPanel pitchGraphPanel;
    WeightedColorLineGraphPanel brightnessGraphPanel;
    WaterfallGraphPanel waterfallGraphPanel;
    
    @Override
    public void initialize() {

        super.initialize();


        pitchGraphPanel = new WeightedColorLineGraphPanel(500, "Pitch", 600, 400);
        pitchGraphPanel.getGraph().setVerticalRange(30, 120);
        pitchGraphPanel.getGraph().setColorRange(0.5, 10);
        pitchGraphPanel.getGraph().setThicknessRangeAuto(true);
        pitchGraphPanel.getGraph().setOpacityRangeAuto(true);
        pitchGraphPanel.getGraph().setConnectPoints(true);
        positionGraphPanel(pitchGraphPanel);
        addGraphPanel("foo", pitchGraphPanel);

        brightnessGraphPanel = new WeightedColorLineGraphPanel(500, "Brightness", 600, 400);
        brightnessGraphPanel.getGraph().setVerticalRange(0.5, 10);
        brightnessGraphPanel.getGraph().setColorRange(30, 120);
        brightnessGraphPanel.getGraph().setThicknessRangeAuto(true);
        brightnessGraphPanel.getGraph().setOpacityRangeAuto(true);
        brightnessGraphPanel.getGraph().setConnectPoints(true);
        positionGraphPanel(brightnessGraphPanel);
        addGraphPanel(brightnessGraphPanel);

        //powerGraphPanel = new ArrayLineGraphPanel("WAV", 500, 300);
        //positionGraphPanel(powerGraphPanel);
        //powerGraphPanel.getGraph().setVerticalRangeAuto(true);
        //powerGraphPanel.getGraph().setConnectPoints(true);
        //addGraphPanel(powerGraphPanel);

        //wacfGraphPanel = new ArrayLineGraphPanel("WACF", 500, 300);
        //positionGraphPanel(wacfGraphPanel);
        //wacfGraphPanel.getGraph().setVerticalRangeAuto(true);
        //wacfGraphPanel.getGraph().setConnectPoints(true);
        //addGraphPanel(wacfGraphPanel);

        waterfallGraphPanel = new WaterfallGraphPanel("Waterfall", 400, 400);
        waterfallGraphPanel.getGraph().setDataAxisAuto(true);
        waterfallGraphPanel.getGraph().setColorMinMax(1, 30);
        positionGraphPanel(waterfallGraphPanel);
        addGraphPanel(waterfallGraphPanel);

        setupThread(new Runnable() {

            Recorder rec;
            long lastGraphedTime = 0;

            

            public void run() {
                rec = getAudioComponents().getRecorder();

                while (polling()) {

                    List<DataFrame> latestFrames = rec.popFrames();
                    if (latestFrames.size() > 0) {

                        DataFrame frame = latestFrames.get(latestFrames.size() - 1);

                        if (frame.getTime() != lastGraphedTime) {
                            double pitch = 0;
                            double quality = 0;
                            double spectralFlux = 0;
                            double spectralCentroid = 0;
                            double loudness = 0;
                            
                            for (int i = 0; i < latestFrames.size(); i++) {
                                DataFrame subFrame = latestFrames.get(i);

                                pitch = subFrame.getPitchInfo().getSmoothedPitch();
                                quality = subFrame.getQualityInfo().getSmoothedCorrelation();
                                spectralFlux = subFrame.getQualityInfo().getSmoothedFlux();
                                spectralCentroid = subFrame.getQualityInfo().getSmoothedSpectralCentroidFrequency();
                                loudness = subFrame.getSM().getSmoothedPowerDB();


                                double normSpectralCentroid = spectralCentroid / subFrame.getPitchInfo().getSmoothedFrequency();

                                if (subFrame.getSM().isSilence() || pitch <= 1 || quality < 0.2) {
                                    pitch = 0;
                                    quality = 0;
                                    spectralFlux = 0;
                                    spectralCentroid = 0;
                                    normSpectralCentroid = 100;
                                    loudness = 0;
                                }

                                
                                
                                //normSpectralCentroid = spectralCentroidSmoother.newValue(normSpectralCentroid);

                                //System.out.println(normSpectralCentroid);
                                if (pitchGraphPanel.isVisible()) {
                                    pitchGraphPanel.graph(pitch, normSpectralCentroid, loudness, 1);
                                }

                                if (brightnessGraphPanel.isVisible()) {
                                    brightnessGraphPanel.graph(normSpectralCentroid, pitch, 0, 1);
                                }

                                if (waterfallGraphPanel.isVisible()) {
                                    //if (subFrame.getSM().isSilence() || subFrame.getQualityInfo().getPrimaryCorrelation() < 0.5) {
                                    //    waterfallGraphPanel.graph(new double[] { 0 }, 0);
                                    //} else {
                                        double[] ps = subFrame.getPS().getRawData();

                                        double[] values = Arrays.copyOf(ps, ps.length / 2);
                                        for (int j = 0; j < values.length; j++) {
                                            values[j] = Math.pow(values[j], 0.2);
                                        }
                                        waterfallGraphPanel.graph(values, normSpectralCentroid);
                                    //}

//                                        double[] log = new double[subFrame.getPS().getRawData().length / 2 - 1];
//                                        double[] floors = getAudioComponents().getNoiseFilter().getFrequencyFloors();
//                                        for (int j = 0; j < log.length; j++) {
//
//                                            log[j] = 10 * Math.log(subFrame.getPS().getRawData()[j + 1]);
//                                            if (Double.isInfinite(log[j])) {
//                                                log[j] = -200;
//                                            }
//                                        }
//                                        //double[] d = new double[subFrame.getPS().getRawData().length];
//                                        //for (int f = 0; f < d.length; f++) { d[f] = 1; }
//                                        System.out.println(Arrays.toString(log));
//                                        waterfallGraphPanel.graph(log, normSpectralCentroid);
                                    //}
                                }
                            }

                            if (pitchGraphPanel.isVisible()) {
                                pitchGraphPanel.repaint();
                            }

                            if (brightnessGraphPanel.isVisible()) {
                                brightnessGraphPanel.repaint();
                            }

                            if (waterfallGraphPanel.isVisible()) {
                                waterfallGraphPanel.repaint();
                            }
                            
                            //if (powerGraphPanel.isVisible()) {
                            //    powerGraphPanel.graph(frame.getPS().getRawData());
                            //}

                            //if (wacfGraphPanel.isVisible()) {
                            //    wacfGraphPanel.graph(frame.getWACF().getRawData());
                            //}

                        }

                        lastGraphedTime = frame.getTime();
                    }

                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
            }

        });
    }
}
