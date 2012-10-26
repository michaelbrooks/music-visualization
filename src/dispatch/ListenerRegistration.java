/*
 * Music Visualizations: http:/github.com/michaelbrooks/music-visualization
 * Copyright 2012, Michael Brooks. BSD License.
 */

package dispatch;

/**
 *
 * @author michael
 */
class ListenerRegistration<LT extends AudioEvent> {

    private AudioListener<LT> listener;
    private Object userData;

    public ListenerRegistration(AudioListener<LT> listener, Object userData) {
        this.listener = listener;
        this.userData = userData;
    }

    public AudioListener<LT> getListener() {
        return listener;
    }

    public Object getUserData() {
        return userData;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ListenerRegistration<LT> other = (ListenerRegistration<LT>) obj;
        if (this.listener != other.listener && (this.listener == null || !this.listener.equals(other.listener))) {
            return false;
        }
        if (this.userData != other.userData && (this.userData == null || !this.userData.equals(other.userData))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + (this.listener != null ? this.listener.hashCode() : 0);
        hash = 67 * hash + (this.userData != null ? this.userData.hashCode() : 0);
        return hash;
    }
}
