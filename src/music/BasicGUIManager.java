/*
 * Music Visualizations: http:/github.com/michaelbrooks/music-visualization
 * Copyright 2012, Michael Brooks. BSD License.
 */

package music;

import graphics.AnimatedNGraph;
import graphics.AnimatedNGraphPanel;
import graphics.ArrayLineGraphPanel;
import graphics.GraphPanel;
import graphics.ParticleCloud;
import graphics.ParticleLine;
import graphics.ScanningLineGraphPanel;
import graphics.VerticalPitchGraphPanel;
import graphics.VerticalQualityGraphPanel;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import music.data.DataFrame;
import music.data.calc.DPoint;
import music.data.calc.NoteConverter;
import music.data.calc.PeakPicker;
import music.data.calc.Smoother;
import music.data.calc.SmootherTwo;
import music.data.impl.DirectDataFrame;

/**
 *
 * @author michael
 */
public class BasicGUIManager extends AbstractGUIManager {

    public BasicGUIManager() {
    }
    
    VerticalQualityGraphPanel qualityGraphPanel;
    VerticalPitchGraphPanel pitchGraphPanel;
    AnimatedNGraphPanel particleCloudGraphPanel;
    AnimatedNGraphPanel particleLineGraphPanel;
    ScanningLineGraphPanel scanningLineGraphPanel;
    ArrayLineGraphPanel arrayLineGraphPanel;

    @Override
    public void initialize() {

        super.initialize();
        
        qualityGraphPanel = new VerticalQualityGraphPanel("Quality", 120, 300);
        positionGraphPanel(qualityGraphPanel);
        qualityGraphPanel.getGraph().setHighLowValues(1, 0);
        qualityGraphPanel.getGraph().setHighLowColors(Color.green, Color.red);
        addGraphPanel(qualityGraphPanel);

        pitchGraphPanel = new VerticalPitchGraphPanel("Pitch", 120, 500);
        pitchGraphPanel.getGraph().setHighLowNote(120, 40);
        //pitchGraphPanel.getFrame().setLocation(0, 350);
        positionGraphPanel(pitchGraphPanel);
        pitchGraphPanel.getGraph().setDisplayGridLines(true);
        pitchGraphPanel.getGraph().setNoteLineWidth(2);
        addGraphPanel(pitchGraphPanel);

        AnimatedNGraph animatedGraph = new ParticleCloud(300);
        particleCloudGraphPanel = new AnimatedNGraphPanel(animatedGraph, 50, "Particles", 500, 500);
        //particleCloudGraphPanel.getFrame().setLocation(150, 0);
        positionGraphPanel(particleCloudGraphPanel);
        particleCloudGraphPanel.getGraph().setLowerLimit(0, 0);
        particleCloudGraphPanel.getGraph().setUpperLimit(0, 1);
        particleCloudGraphPanel.getGraph().setLowerLimit(1, 0);
        particleCloudGraphPanel.getGraph().setUpperLimit(1, Math.PI * 2);
        particleCloudGraphPanel.getGraph().setLowerLimit(2, 0);
        particleCloudGraphPanel.getGraph().setUpperLimit(2, 1);
        addGraphPanel(particleCloudGraphPanel);

        animatedGraph = new ParticleLine(300);
        particleLineGraphPanel = new AnimatedNGraphPanel(animatedGraph, 50, "Particle Line", 500, 200);
        //particleLineGraphPanel.getFrame().setLocation(680, 0);
        positionGraphPanel(particleLineGraphPanel);
        particleLineGraphPanel.getGraph().setLowerLimit(0, 0);
        particleLineGraphPanel.getGraph().setLowerLimit(0, 1);
        addGraphPanel(particleLineGraphPanel);

        arrayLineGraphPanel = new ArrayLineGraphPanel("WAV", 500, 300);
        //arrayLineGraphPanel.getFrame().setLocation(150, 550);
        positionGraphPanel(arrayLineGraphPanel);
        arrayLineGraphPanel.getGraph().setVerticalRangeAuto(true);
        arrayLineGraphPanel.getGraph().setConnectPoints(true);
        addGraphPanel(arrayLineGraphPanel);
        
        scanningLineGraphPanel = new ScanningLineGraphPanel(400, "Line", 500, 300);
        //scanningLineGraphPanel.getFrame().setLocation(680, 550);
        positionGraphPanel(scanningLineGraphPanel);
        scanningLineGraphPanel.getGraph().setVerticalRange(0,1);
        scanningLineGraphPanel.getGraph().setVerticalRangeAuto(true);
        addGraphPanel(scanningLineGraphPanel);

        setupThread(new Runnable() {

            Recorder rec;
            long lastGraphedTime = 0;

            Smoother qualitySmoother = new SmootherTwo(0.333, 0.333);
            Smoother pitchSmoother = new SmootherTwo(0.333, 0.333);
            Smoother spectralFluxSmoother = new SmootherTwo(0.333, 0.333);
            Smoother spectralCentroidSmoother = new SmootherTwo(0.333, 0.333);
            
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

                            for (int i = 0; i < latestFrames.size(); i++) {
                                DataFrame subFrame = latestFrames.get(i);

                                pitch = subFrame.getPitchInfo().getPrimaryPitch();
                                quality = subFrame.getQualityInfo().getPrimaryCorrelation();
                                spectralFlux = subFrame.getQualityInfo().getSpectralFlux();
                                spectralCentroid = subFrame.getQualityInfo().getSpectralCentroidFrequency();

                                if (subFrame.getSM().isSilence()) {
                                    pitch = 0;
                                    quality = 0;
                                    spectralFlux = 0;
                                    //spectralCentroid = 0;
                                }
                                
                                pitch = pitchSmoother.newValue(pitch);
                                quality = qualitySmoother.newValue(quality);
                                spectralFlux = spectralFluxSmoother.newValue(spectralFlux);
                                spectralCentroid = spectralCentroidSmoother.newValue(spectralCentroid);
                                
                                if (scanningLineGraphPanel.isVisible() && quality > 0.8) {
                                    scanningLineGraphPanel.graph(spectralCentroid / NoteConverter.pitchToFrequency(pitch));
                                }
                            }

                            if (scanningLineGraphPanel.isVisible()) {
                                scanningLineGraphPanel.repaint();
                            }

                            if (arrayLineGraphPanel.isVisible()) {
                                arrayLineGraphPanel.graph(frame.getPS().getRawData());
                            }

                            if (qualityGraphPanel.isVisible())
                                qualityGraphPanel.graph(quality);
                                

                            if (pitchGraphPanel.isVisible()) {
                                pitchGraphPanel.setConfidence(quality);
                                pitchGraphPanel.graph(pitch);
                            }

                            if (particleLineGraphPanel.isVisible()) {
                                particleLineGraphPanel.getGraph().setData(0, 1-quality);
                            }

                            if (particleCloudGraphPanel.isVisible()) {
                                AnimatedNGraph particleCloudGraph = particleCloudGraphPanel.getGraph();
                                particleCloudGraph.setData(2, quality);
                            }

                        }

                        lastGraphedTime = frame.getTime();
                    }

                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
            }

        });
    }
}
