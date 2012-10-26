/*
 * Music Visualizations: http:/github.com/michaelbrooks/music-visualization
 * Copyright 2012, Michael Brooks. BSD License.
 */
package music;

import buffer.DataReference;
import javax.sound.sampled.AudioFormat;

/**
 * The conversions come from http://www.jsresources.org/faq_audio.html#reconstruct_samples
 * @author michael
 */
public class AudioConverter {

    public static int getBytes(AudioFormat format, int samples) {
        return format.getFrameSize() * samples;
    }

    public static int getSamples(AudioFormat format, int bytes) {
        return (int) Math.ceil(((double) bytes) / format.getFrameSize());
    }

    static void convertToDouble(AudioFormat format, byte[] buffer, DataReference<Double> output)
    {
        int sampleSize = format.getSampleSizeInBits();
        boolean signed = format.getEncoding() == AudioFormat.Encoding.PCM_SIGNED;
        boolean bigEndian = format.isBigEndian();

        if (sampleSize == 8) {
            if (signed) {
                size8SignedToDouble(buffer, output);
            } else {
                size8UnsignedToDouble(buffer, output);
            }
        } else if (sampleSize == 16) {
            if (bigEndian) {
                size16SignedBigToDouble(buffer, output);
            } else {
                size16SignedLittleToDouble(buffer, output);
            }
        } else if (sampleSize == 24) {
            if (bigEndian) {
                size24SignedBigToDouble(buffer, output);
            } else {
                size24SignedLittleToDouble(buffer, output);
            }
        } else if (sampleSize == 32) {
            if (bigEndian) {
                size32SignedBigToDouble(buffer, output);
            } else {
                size32SignedLittleToDouble(buffer, output);
            }
        }
    }

    static void convertToDouble(AudioFormat format, byte[] buffer, double[] output) {
        int sampleSize = format.getSampleSizeInBits();
        boolean signed = format.getEncoding() == AudioFormat.Encoding.PCM_SIGNED;
        boolean bigEndian = format.isBigEndian();

        if (sampleSize == 8) {
            if (signed) {
                size8SignedToDouble(buffer, output);
            } else {
                size8UnsignedToDouble(buffer, output);
            }
        } else if (sampleSize == 16) {
            if (bigEndian) {
                size16SignedBigToDouble(buffer, output);
            } else {
                size16SignedLittleToDouble(buffer, output);
            }
        } else if (sampleSize == 24) {
            if (bigEndian) {
                size24SignedBigToDouble(buffer, output);
            } else {
                size24SignedLittleToDouble(buffer, output);
            }
        } else if (sampleSize == 32) {
            if (bigEndian) {
                size32SignedBigToDouble(buffer, output);
            } else {
                size32SignedLittleToDouble(buffer, output);
            }
        }
    }

    private static void size8SignedToDouble(byte[] buffer, double[] output) {
        for (int offset = 0; offset < buffer.length; offset++) {
            output[offset] = buffer[offset] / 128.0;
        }
    }

    private static void size8SignedToDouble(byte[] buffer, DataReference<Double> output) {
        for (int offset = 0; offset < buffer.length; offset++) {
            output.set(offset, buffer[offset] / 128.0);
        }
    }

    private static void size8UnsignedToDouble(byte[] buffer, double[] output) {
        for (int offset = 0; offset < buffer.length; offset++) {
            output[offset] = ((buffer[offset] & 0xFF) - 128) / 128.0;
        }
    }


    private static void size8UnsignedToDouble(byte[] buffer, DataReference<Double> output) {
        for (int offset = 0; offset < buffer.length; offset++) {
            output.set(offset, ((buffer[offset] & 0xFF) - 128) / 128.0);
        }
    }

    private static void size16SignedLittleToDouble(byte[] buffer, double[] output) {
        for (int offset = 0, outIdx = 0;
                offset + 1 < buffer.length;
                offset += 2, outIdx++) {
            output[outIdx] = ((buffer[offset + 0] & 0xFF)
                    | (buffer[offset + 1] << 8))
                    / 32768.0;
        }
    }

    private static void size16SignedLittleToDouble(byte[] buffer, DataReference<Double> output) {
        for (int offset = 0, outIdx = 0;
                offset + 1 < buffer.length;
                offset += 2, outIdx++) {
            output.set(outIdx, ((buffer[offset + 0] & 0xFF)
                    | (buffer[offset + 1] << 8))
                    / 32768.0);
        }
    }

    private static void size16SignedBigToDouble(byte[] buffer, double[] output) {
        for (int offset = 0, outIdx = 0;
                offset + 1 < buffer.length;
                offset += 2, outIdx++) {
            output[outIdx] = ((buffer[offset + 0] << 8)
                    | (buffer[offset + 1] & 0xFF))
                    / 32768.0;
        }
    }

    private static void size16SignedBigToDouble(byte[] buffer, DataReference<Double> output) {
        for (int offset = 0, outIdx = 0;
                offset + 1 < buffer.length;
                offset += 2, outIdx++) {
            output.set(outIdx, ((buffer[offset + 0] << 8)
                    | (buffer[offset + 1] & 0xFF))
                    / 32768.0);
        }
    }

    private static void size24SignedLittleToDouble(byte[] buffer, double[] output) {
        for (int offset = 0, outIdx = 0;
                offset + 2 < buffer.length;
                offset += 3, outIdx++) {
            output[outIdx] = ((buffer[offset + 0] & 0xFF)
                    | ((buffer[offset + 1] & 0xFF) << 8)
                    | (buffer[offset + 2] << 16))
                    / 8388606.0;
        }
    }

    private static void size24SignedLittleToDouble(byte[] buffer, DataReference<Double> output) {
        for (int offset = 0, outIdx = 0;
                offset + 2 < buffer.length;
                offset += 3, outIdx++) {
            output.set(outIdx, ((buffer[offset + 0] & 0xFF)
                    | ((buffer[offset + 1] & 0xFF) << 8)
                    | (buffer[offset + 2] << 16))
                    / 8388606.0);
        }
    }

    private static void size24SignedBigToDouble(byte[] buffer, double[] output) {
        for (int offset = 0, outIdx = 0;
                offset + 2 < buffer.length;
                offset += 3, outIdx++) {
            output[outIdx] = ((buffer[offset + 0] << 16)
                    | ((buffer[offset + 1] & 0xFF) << 8)
                    | (buffer[offset + 2] & 0xFF))
                    / 8388606.0;
        }
    }

    private static void size24SignedBigToDouble(byte[] buffer, DataReference<Double> output) {
        for (int offset = 0, outIdx = 0;
                offset + 2 < buffer.length;
                offset += 3, outIdx++) {
            output.set(outIdx, ((buffer[offset + 0] << 16)
                    | ((buffer[offset + 1] & 0xFF) << 8)
                    | (buffer[offset + 2] & 0xFF))
                    / 8388606.0);
        }
    }

    private static void size32SignedBigToDouble(byte[] buffer, double[] output) {
        for (int offset = 0, outIdx = 0;
                offset + 3 < buffer.length;
                offset += 4, outIdx++) {
            output[outIdx] = ((buffer[offset + 0] & 0xFF)
                    | ((buffer[offset + 1] & 0xFF) << 8)
                    | ((buffer[offset + 2] & 0xFF) << 16)
                    | (buffer[offset + 3] << 24))
                    / 2147483648.0;
        }
    }

    private static void size32SignedBigToDouble(byte[] buffer, DataReference<Double> output) {
        for (int offset = 0, outIdx = 0;
                offset + 3 < buffer.length;
                offset += 4, outIdx++) {
            output.set(outIdx, ((buffer[offset + 0] & 0xFF)
                    | ((buffer[offset + 1] & 0xFF) << 8)
                    | ((buffer[offset + 2] & 0xFF) << 16)
                    | (buffer[offset + 3] << 24))
                    / 2147483648.0);
        }
    }

    private static void size32SignedLittleToDouble(byte[] buffer, double[] output) {
        for (int offset = 0, outIdx = 0;
                offset + 3 < buffer.length;
                offset += 4, outIdx++) {
            output[outIdx] = ((buffer[offset + 0] << 24)
                    | ((buffer[offset + 1] & 0xFF) << 16)
                    | ((buffer[offset + 2] & 0xFF) << 8)
                    | (buffer[offset + 3] & 0xFF))
                    / 2147483648.0;
        }
    }

    private static void size32SignedLittleToDouble(byte[] buffer, DataReference<Double> output) {
        for (int offset = 0, outIdx = 0;
                offset + 3 < buffer.length;
                offset += 4, outIdx++) {
            output.set(outIdx, ((buffer[offset + 0] << 24)
                    | ((buffer[offset + 1] & 0xFF) << 16)
                    | ((buffer[offset + 2] & 0xFF) << 8)
                    | (buffer[offset + 3] & 0xFF))
                    / 2147483648.0);
        }
    }

    static void convertToBytes(AudioFormat format, DataReference<Double> buffer, byte[] output) {

        int sampleSize = format.getSampleSizeInBits();
        boolean signed = format.getEncoding() == AudioFormat.Encoding.PCM_SIGNED;
        boolean bigEndian = format.isBigEndian();
        assert(sampleSize == 16);
        assert(signed == true);

        if (bigEndian) {
            for (int sample = 0, outIdx = 0; sample < buffer.size(); sample++, outIdx += 2) {
                // the sample to process
                double dSample = (Double)buffer.get(sample);
                // saturation
                dSample = Math.min(1.0, Math.max(-1.0, dSample));
                // scaling and conversion to integer
                int nSample = (int) Math.round(dSample * 32767.0);
                byte high = (byte) ((nSample >> 8) & 0xFF);
                byte low = (byte) (nSample & 0xFF);
                output[outIdx] = high;
                output[outIdx + 1] = low;
            }
        }
        else {
            for (int sample = 0, outIdx = 0; sample < buffer.size(); sample++, outIdx += 2) {
                // the sample to process
                double dSample = (Double)buffer.get(sample);
                // saturation
                dSample = Math.min(1.0, Math.max(-1.0, dSample));
                // scaling and conversion to integer
                int nSample = (int) Math.round(dSample * 32767.0);
                byte high = (byte) ((nSample >> 8) & 0xFF);
                byte low = (byte) (nSample & 0xFF);
                output[outIdx + 1] = high;
                output[outIdx] = low;
            }
        }

    }

    static void convertToBytes(AudioFormat format, double[] buffer, byte[] output) {

        int sampleSize = format.getSampleSizeInBits();
        boolean signed = format.getEncoding() == AudioFormat.Encoding.PCM_SIGNED;
        boolean bigEndian = format.isBigEndian();
        assert(sampleSize == 16);
        assert(signed == true);

        if (bigEndian) {
            for (int sample = 0, outIdx = 0; sample < buffer.length; sample++, outIdx += 2) {
                // the sample to process
                double dSample = buffer[sample];
                // saturation
                dSample = Math.min(1.0, Math.max(-1.0, dSample));
                // scaling and conversion to integer
                int nSample = (int) Math.round(dSample * 32767.0);
                byte high = (byte) ((nSample >> 8) & 0xFF);
                byte low = (byte) (nSample & 0xFF);
                output[outIdx] = high;
                output[outIdx + 1] = low;
            }
        }
        else {
            for (int sample = 0, outIdx = 0; sample < buffer.length; sample++, outIdx += 2) {
                // the sample to process
                double dSample = buffer[sample];
                // saturation
                dSample = Math.min(1.0, Math.max(-1.0, dSample));
                // scaling and conversion to integer
                int nSample = (int) Math.round(dSample * 32767.0);
                byte high = (byte) ((nSample >> 8) & 0xFF);
                byte low = (byte) (nSample & 0xFF);
                output[outIdx + 1] = high;
                output[outIdx] = low;
            }
        }

    }

    /*
    static void convertToBytes(AudioFormat format, double[] buffer, byte[] output) {
        int sampleSize = format.getSampleSizeInBits();
        boolean signed = format.getEncoding() == AudioFormat.Encoding.PCM_SIGNED;
        boolean bigEndian = format.isBigEndian();

        if (sampleSize == 8) {
            if (signed) {
                size8SignedToBytes(buffer, output);
            } else {
                size8UnsignedToBytes(buffer, output);
            }
        } else if (sampleSize == 16) {
            if (bigEndian) {
                size16SignedBigToBytes(buffer, output);
            } else {
                size16SignedLittleToBytes(buffer, output);
            }
        } else if (sampleSize == 24) {
            if (bigEndian) {
                size24SignedBigToBytes(buffer, output);
            } else {
                size24SignedLittleToBytes(buffer, output);
            }
        } else if (sampleSize == 32) {
            if (bigEndian) {
                size32SignedBigToBytes(buffer, output);
            } else {
                size32SignedLittleToBytes(buffer, output);
            }
        }
    }

    private static void size8SignedToBytes(double[] buffer, byte[] output) {
        
    }

    private static void size8UnsignedToBytes(double[] buffer, byte[] output) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private static void size16SignedBigToBytes(double[] buffer, byte[] output) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private static void size16SignedLittleToBytes(double[] buffer, byte[] output) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private static void size24SignedBigToBytes(double[] buffer, byte[] output) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private static void size24SignedLittleToBytes(double[] buffer, byte[] output) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private static void size32SignedBigToBytes(double[] buffer, byte[] output) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private static void size32SignedLittleToBytes(double[] buffer, byte[] output) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
    */
}
