/*
 * Music Visualizations: http:/github.com/michaelbrooks/music-visualization
 * Copyright 2012, Michael Brooks. BSD License.
 */

package music;

import graphics.AnimatedNGraph;
import graphics.AnimatedNGraphPanel;
import graphics.ArrayRingGraphPanel;
import graphics.ParticleCloud;
import graphics.ScanningLineGraphPanel;
import graphics.VerticalPitchGraphPanel;
import graphics.VerticalQualityGraphPanel;
import graphics.WeightedColorLineGraphPanel;
import java.awt.Color;
import java.util.Arrays;
import java.util.List;
import music.data.DataFrame;
import music.data.calc.NoteConverter;
import music.data.calc.Smoother;
import music.data.calc.SmootherTwo;

/**
 *
 * @author michael
 */
public class VisualizationGUIManager extends AbstractGUIManager {

    ScanningLineGraphPanel pitchLine;
    VerticalQualityGraphPanel periodicityGraphPanel;
    VerticalPitchGraphPanel pitchGraphPanel;
    AnimatedNGraphPanel particleCloudGraphPanel;
    AnimatedNGraphPanel particleLineGraphPanel;
    ArrayRingGraphPanel ringGraphPanel;
    WeightedColorLineGraphPanel wclGraphPanel;
    
    @Override
    public void initialize() {

        super.initialize();

        periodicityGraphPanel = new VerticalQualityGraphPanel("Periodicity", 120, 200);
        periodicityGraphPanel.getGraph().setHighLowValues(1, 0);
        periodicityGraphPanel.getGraph().setHighLowColors(Color.green, Color.red);
        positionGraphPanel(periodicityGraphPanel);
        addGraphPanel(periodicityGraphPanel);

        pitchGraphPanel = new VerticalPitchGraphPanel("Pitch", 120, 300);
        pitchGraphPanel.getGraph().setHighLowNote(100, 40);
        pitchGraphPanel.getGraph().setDisplayGridLines(true);
        pitchGraphPanel.getGraph().setNoteLineWidth(2);
        positionGraphPanel(pitchGraphPanel);
        addGraphPanel(pitchGraphPanel);

        positionNewColumn();

        pitchLine = new ScanningLineGraphPanel(500, "Pitch Line", 400, 200);
        pitchLine.getGraph().setVerticalRange(20, 120);
        //pitchLine.getGraph().setConnectPoints(true);
        positionGraphPanel(pitchLine);
        addGraphPanel(pitchLine);

        ringGraphPanel = new ArrayRingGraphPanel("Power Spectrum Ring", 400, 400);
        ringGraphPanel.getGraph().setHighLowValues(1, 0);
        positionGraphPanel(ringGraphPanel);
        addGraphPanel(ringGraphPanel);
        
        positionNewColumn();

        AnimatedNGraph animatedGraph = new ParticleCloud(300);
        particleCloudGraphPanel = new AnimatedNGraphPanel(animatedGraph, 50, "Cloud", 400, 400);
        particleCloudGraphPanel.getGraph().setLowerLimit(0, 0);
        particleCloudGraphPanel.getGraph().setUpperLimit(0, 1);
        particleCloudGraphPanel.getGraph().setLowerLimit(1, 0);
        particleCloudGraphPanel.getGraph().setUpperLimit(1, Math.PI * 2);
        particleCloudGraphPanel.getGraph().setLowerLimit(2, 0);
        particleCloudGraphPanel.getGraph().setUpperLimit(2, 1);
        positionGraphPanel(particleCloudGraphPanel);
        addGraphPanel(particleCloudGraphPanel);

        wclGraphPanel = new WeightedColorLineGraphPanel(500, "WCL", 400, 200);
        wclGraphPanel.getGraph().setVerticalRange(30, 120);
        wclGraphPanel.getGraph().setColorRange(5, 30);
        wclGraphPanel.getGraph().setThicknessRangeAuto(true);
        wclGraphPanel.getGraph().setConnectPoints(true);
        positionGraphPanel(wclGraphPanel);
        addGraphPanel(wclGraphPanel);

        setupThread(new Runnable() {

            Recorder rec;
            long lastGraphedTime = 0;

            Smoother periodicitySmoother = new SmootherTwo(0.333, 0.333);
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
                            double periodicity = 0;
                            double spectralFlux = 0;
                            double spectralCentroid = 0;
                            double loudness = 0;
                            
                            for (int i = 0; i < latestFrames.size(); i++) {
                                DataFrame subFrame = latestFrames.get(i);

                                pitch = subFrame.getPitchInfo().getPrimaryPitch();
                                periodicity = subFrame.getQualityInfo().getPrimaryCorrelation();
                                spectralFlux = subFrame.getQualityInfo().getSpectralFlux();
                                spectralCentroid = subFrame.getQualityInfo().getSpectralCentroidFrequency();
                                loudness = subFrame.getSM().getPowerDB();
                                
                                if (subFrame.getSM().isSilence()) {
                                    pitch = 0;
                                    periodicity = 0;
                                    spectralFlux = 0;
                                    spectralCentroid = 0;
                                }

                                pitch = pitchSmoother.newValue(pitch);
                                periodicity = periodicitySmoother.newValue(periodicity);
                                spectralFlux = spectralFluxSmoother.newValue(spectralFlux);
                                spectralCentroid = spectralCentroidSmoother.newValue(spectralCentroid);

                                if (pitchLine.isVisible()) {
                                    pitchLine.graph(pitch);
                                }

                                double normSpectralCentroid = spectralCentroid / NoteConverter.frequencyToPitch(pitch);
                                System.out.println(normSpectralCentroid);
                                if (wclGraphPanel.isVisible()) {
                                    wclGraphPanel.graph(pitch, normSpectralCentroid, loudness, 1);
                                }
                            }

                            if (pitchLine.isVisible()) {
                                pitchLine.repaint();
                            }

                            if (wclGraphPanel.isVisible()) {
                                wclGraphPanel.repaint();
                            }

                            if (ringGraphPanel.isVisible()) {
                                ringGraphPanel.getGraph().setLeftRightBounds(-1, frame.getPS().getRawData().length / 4);
                                ringGraphPanel.graph(frame.getPS().getRawData());
                            }
                            if (particleCloudGraphPanel.isVisible()) {
                                particleCloudGraphPanel.getGraph().setData(0, 1-periodicity);
                                particleCloudGraphPanel.getGraph().setData(2, periodicity);
                            }

                            if (periodicityGraphPanel.isVisible()) {
                                periodicityGraphPanel.graph(periodicity);
                            }

                            if (pitchGraphPanel.isVisible()) {
                                pitchGraphPanel.graph(pitch);
                            }

                        }

                        lastGraphedTime = frame.getTime();
                    }

                    try {
                        Thread.sleep(20);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
            }

        });
    }

}
