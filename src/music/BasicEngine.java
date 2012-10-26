/*
 * Music Visualizations: http:/github.com/michaelbrooks/music-visualization
 * Copyright 2012, Michael Brooks. BSD License.
 */

package music;

import buffer.DataReference;
import dispatch.AudioAvailableEvent;
import music.data.DataFrame;
import music.data.Wav;
import music.data.impl.DataFactory;

/**
 *
 * @author michael
 */
class BasicEngine extends AbstractComponent implements Engine {

    public BasicEngine() {
    }

    @Override
    public void initialize() {
    }

    @Override
    public void start() {
    }

    public void update() {

    }

    long framesRecorded = 0;
    public void eventFired(AudioAvailableEvent event, Object userData) {

//        long time0 = System.nanoTime();
//        System.out.println("BasicEngine, eventFired, start, " + time0);

        DataReference<Double> ref = event.getAudioData();
        double[] wavData = new double[ref.size()];
        for (int i = 0; i < wavData.length; i++) {
            wavData[i] = ref.get(i);
        }
        AudioInput input = getAudioComponents().getAudioInput();
        long timeOfFirstSample = input.samplesToNanoTime(framesRecorded);

        Wav wav = DataFactory.newWav(wavData, getAudioComponents().getAudioInputFormat().getSampleRate());
        DataFrame frame = DataFactory.newDataFrame(wav, timeOfFirstSample, 0);
        
        //Filter noise
        getAudioComponents().getNoiseFilter().addToNoiseFloor(frame, 4);
        frame = getAudioComponents().getNoiseFilter().filter(frame);

        //Do some calculation
        frame.getPitchInfo().getSmoothedPitch();
        frame.getQualityInfo().getSmoothedCorrelation();
        frame.getQualityInfo().getSmoothedFlux();
        frame.getQualityInfo().getSmoothedSpectralCentroidFrequency();
        frame.getSM().getSmoothedPowerDB();
        
        //System.out.println("Time taken: " + (time1 - time0) / 1000000000.0);

        getAudioComponents().getAudioOutput().play(frame.getWav());

        Recorder rec = getAudioComponents().getRecorder();
        rec.addFrame(frame);

        framesRecorded += wav.size();
        
        ref.returnInstance();
    }
}