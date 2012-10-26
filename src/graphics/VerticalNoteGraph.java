/*
 * Music Visualizations: http:/github.com/michaelbrooks/music-visualization
 * Copyright 2012, Michael Brooks. BSD License.
 */

package graphics;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.ArrayList;

/**
 *
 * @author michael
 */
public class VerticalNoteGraph extends VerticalPitchGraph {

    double[] noteArray = new double[1];

    private void resetNoteArray() {
        if (highNote - lowNote != noteArray.length)
            noteArray = new double[(int)(highNote - lowNote)];
    }

    @Override
    public void setHighLowNote(double high, double low) {
        highNote = scaleNote(high);
        lowNote = scaleNote(low);
        resetNoteArray();
    }

    @Override
    public void setHighNote(double note) {
        highNote = scaleNote(note);
        resetNoteArray();
    }

    @Override
    public void setLowNote(double note) {
        lowNote = note;
        resetNoteArray();
    }

    

    @Override
    protected double scaleNote(double note) {
        note = Math.round(note);
        note = note - lowNote;
        
        return note;
    }

    @Override
    public void paint(Graphics2D g) {

        Rectangle clip = g.getClipBounds();
        double windowWidth = clip.getWidth();
        double windowHeight = clip.getHeight();

        g.setColor(backgroundColor);
        g.fillRect(0, 0, (int)windowWidth, (int)windowHeight);

        ArrayList<Double> notes = getNotes();
        double blockHeight = windowHeight / notes.size();

        if (displayGridLines) {
            g.setColor(gridLineColor);

            for (int i = 1; i < notes.size() - 1; i++) {
                double note = notes.get(i);
                
                int noteLineHeight = (int)Math.round(windowHeight - (note + 1) * blockHeight);
                g.drawLine(0, noteLineHeight, (int)windowWidth, noteLineHeight);
            }
        }

        Color lineColor = noteLineColor;
        if (confidence != 1) {
            lineColor = new Color(noteLineColor.getRed(),
                    noteLineColor.getGreen(),
                    noteLineColor.getBlue(),
                    (int)Math.round(255 * confidence));
        }
        g.setColor(lineColor);

        int noteLineHeight = (int)Math.round(windowHeight - (currentNote + 1) * blockHeight);
        g.fillRect(0, noteLineHeight, (int)windowWidth, (int)Math.round(blockHeight));
    }



}
