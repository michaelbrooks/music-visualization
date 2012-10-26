/*
 * Music Visualizations: http:/github.com/michaelbrooks/music-visualization
 * Copyright 2012, Michael Brooks. BSD License.
 */

package music;

import graphics.ScanningLineGraphPanel;
import java.util.List;
import music.data.DataFrame;
import music.data.calc.NoteConverter;
import music.data.calc.Smoother;
import music.data.calc.SmootherTwo;

/**
 *
 * @author michael
 */
public class ScanningGUIManager extends AbstractGUIManager {

    ScanningLineGraphPanel pitchLine;
    ScanningLineGraphPanel periodicityLine;
    ScanningLineGraphPanel spectralFluxLine;
    ScanningLineGraphPanel spectralCentroidLine;

    @Override
    public void initialize() {

        super.initialize();

        pitchLine = new ScanningLineGraphPanel(500, "Pitch Line", 450, 300);
        pitchLine.getGraph().setVerticalRange(20, 120);
        //pitchLine.getGraph().setConnectPoints(true);
        positionGraphPanel(pitchLine);
        addGraphPanel(pitchLine);

        periodicityLine = new ScanningLineGraphPanel(500, "Periodicity", 450, 300);
        periodicityLine.getGraph().setVerticalRange(0, 1);
        periodicityLine.getGraph().setConnectPoints(true);
        positionGraphPanel(periodicityLine);
        addGraphPanel(periodicityLine);

        positionNewColumn();

        spectralFluxLine = new ScanningLineGraphPanel(500, "Spectral Flux", 450, 300);
        spectralFluxLine.getGraph().setVerticalRange(0, 1);
        spectralFluxLine.getGraph().setConnectPoints(true);
        positionGraphPanel(spectralFluxLine);
        addGraphPanel(spectralFluxLine);

        spectralCentroidLine = new ScanningLineGraphPanel(500, "Spectral Centroid", 450, 300);
        spectralCentroidLine.getGraph().setVerticalRange(0, 10);
        spectralCentroidLine.getGraph().setConnectPoints(true);
        positionGraphPanel(spectralCentroidLine);
        addGraphPanel(spectralCentroidLine);

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

                            for (int i = 0; i < latestFrames.size(); i++) {
                                DataFrame subFrame = latestFrames.get(i);

                                //Play it back
                                AudioOutput output = getAudioComponents().getAudioOutput();
                                output.play(subFrame.getWav());

                                pitch = subFrame.getPitchInfo().getPrimaryPitch();
                                periodicity = subFrame.getQualityInfo().getPrimaryCorrelation();
                                spectralFlux = subFrame.getQualityInfo().getSpectralFlux();
                                spectralCentroid = subFrame.getQualityInfo().getSpectralCentroidFrequency();

                                if (subFrame.getSM().isSilence()) {
                                    pitch = 0;
                                    periodicity = 0;
                                    spectralFlux = 0;
                                    spectralCentroid = 0;
                                }

                                pitch = pitchSmoother.newValue(pitch);
                                periodicity = periodicitySmoother.newValue(periodicity);
                                spectralFlux = spectralFluxSmoother.newValue(spectralFlux);
                                spectralCentroid = spectralCentroidSmoother.newValue(spectralCentroid / NoteConverter.pitchToFrequency(pitch));

                                if (pitchLine.isVisible()) {
                                    pitchLine.graph(pitch);
                                }

                                if (periodicityLine.isVisible()) {
                                    periodicityLine.graph(periodicity);
                                }

                                if (spectralFluxLine.isVisible()) {
                                    spectralFluxLine.graph(spectralFlux);
                                }

                                if (spectralCentroidLine.isVisible()) {
                                    spectralCentroidLine.graph(spectralCentroid);
                                }
                            }

                            if (pitchLine.isVisible()) {
                                pitchLine.repaint();
                            }

                            if (periodicityLine.isVisible()) {
                                periodicityLine.repaint();
                            }

                            if (spectralFluxLine.isVisible()) {
                                spectralFluxLine.repaint();
                            }

                            if (spectralCentroidLine.isVisible()) {
                                spectralCentroidLine.repaint();
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
