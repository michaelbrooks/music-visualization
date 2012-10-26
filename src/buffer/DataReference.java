/*
 * Music Visualizations: http:/github.com/michaelbrooks/music-visualization
 * Copyright 2012, Michael Brooks. BSD License.
 */

package buffer;

import java.util.List;

/**
 *
 * @author michael
 */
public class DataReference<RT> {

    private Object userData;
    private int position;
    private DataBuffer<RT> buffer;
    private int shareCount = 0;
    private boolean enabled = false;
    private boolean permanentlyDisabled = false;

    DataReference(DataBuffer<RT> buffer, int position) {
        this.buffer = buffer;
        this.position = position;
    }

    public int size() {
        return buffer.getSubBufferSize();
    }

    public synchronized void setUserData(Object userData) {
        this.userData = userData;
    }

    public synchronized Object getUserData() {
        return this.userData;
    }

    private void disable() {
        this.enabled = false;
        this.permanentlyDisabled = true;
    }

    public synchronized boolean enable() {
        if (!permanentlyDisabled) {
            this.enabled = true;
            return true;
        }
        return false;
    }

    public synchronized boolean enabled() {
        return this.enabled;
    }

    public synchronized void shareInstances(int num) {
        if (enabled()) {
            shareCount += num;
        } else {
            throw new IllegalStateException("Can not share disabled reference.");
        }
    }

    public synchronized void shareInstance() {
        if (enabled()) {
            shareCount++;
        } else {
            throw new IllegalStateException("Can not share disabled reference.");
        }
    }

    public synchronized void returnInstance() {
        if (enabled()) {
            shareCount--;

            if (shareCount == 0) {
                buffer.deallocate(this);
                disable();
                notify();
            }
        } else {
            throw new IllegalStateException("Can not return disabled reference.");
        }
    }

    public synchronized RT get(int index) {
        if (enabled()) {
            return buffer.read(position, index);
        } else {
            return null;
        }
    }

    public synchronized void set(int index, RT value) {
        if (enabled()) {
            buffer.write(position, index, value);
        }
    }

    public List<RT> asList() {
        return buffer.getSubList(position, position + buffer.getSubBufferSize());
    }

    int getPosition()
    {
        return position;
    }
}
