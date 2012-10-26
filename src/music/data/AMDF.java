/*
 * Music Visualizations: http:/github.com/michaelbrooks/music-visualization
 * Copyright 2012, Michael Brooks. BSD License.
 */

package music.data;

import java.io.Serializable;

/**
 * Represents an average magnitude difference function.
 *
 * For signal x, the value at offset j is given by
 * AMDF(x,j) = 1/|x| SUM { | x[n] - x[n + j] | : n from 0 to |x|-1 }
 * 
 * de la Cuadra P, Master A, Sapp C. Efficient pitch detection techniques for interactive music. In: Proceedings of the 2001 International Computer Music Conference. Citeseer; 2001. Available at: http://citeseerx.ist.psu.edu/viewdoc/download?doi=10.1.1.142.7667&rep=rep1&type=pdf.
 *
 * @author michael
 */
public interface AMDF extends Correlogram {

}
