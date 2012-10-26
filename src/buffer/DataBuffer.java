/*
 * Music Visualizations: http:/github.com/michaelbrooks/music-visualization
 * Copyright 2012, Michael Brooks. BSD License.
 */

package buffer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author michael
 */
public class DataBuffer<T> {

    private int subBufferSize;
    private int subBufferCap;
    private ArrayList<T> buffer;
    private ArrayList<T> subBufferTemplate;
    private HashSet<DataReference> references;
    private LinkedList<Integer> emptySpaces;
    
    public DataBuffer(int subBufferSize, int numSubBuffersInit)
    {
        this.subBufferCap = numSubBuffersInit;
        this.subBufferSize = subBufferSize;

        buffer = new ArrayList<T>(this.subBufferSize * this.subBufferCap);
        subBufferTemplate = new ArrayList<T>(this.subBufferSize);
        for (int i = 0; i < this.subBufferSize; i++) {
            subBufferTemplate.add(null);
        }
        for (int i = 0; i < this.subBufferCap; i++) {
            buffer.addAll(subBufferTemplate);
        }
        
        references = new HashSet<DataReference>();

        ArrayList<Integer> emptyIndices = new ArrayList<Integer>(numSubBuffersInit);
        for (int i = 0; i < numSubBuffersInit; i++) {
            emptyIndices.add(i);
        }
        
        emptySpaces = new LinkedList<Integer>(emptyIndices);
    }

    public synchronized int allocationCount() {
        return references.size();
    }

    public synchronized DataReference allocate()
    {
        //Check if we need to add more space
        if (emptySpaces.size() == 0) {
            buffer.addAll(subBufferTemplate);
            emptySpaces.add(subBufferCap);
            subBufferCap++;
        }
        
        //Get the first empty space
        int emptySpace = emptySpaces.removeFirst();

        DataReference dataReference = new DataReference(this, emptySpace);
        references.add(dataReference);
        
        return dataReference;
    }

    synchronized void deallocate(DataReference ref)
    {
        emptySpaces.add(ref.getPosition());
        references.remove(ref);
    }

    synchronized void write(int subBuffer, int index, T value)
    {
        buffer.set(subBuffer * subBufferSize + index, value);
    }

    synchronized T read(int subBuffer, int index)
    {
        return buffer.get(subBuffer * subBufferSize + index);
    }

    int getSubBufferSize() {
        return subBufferSize;
    }

    List<T> getSubList(int from, int to)
    {
        return buffer.subList(from, to);
    }
}
