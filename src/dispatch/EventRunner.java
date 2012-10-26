/*
 * Music Visualizations: http:/github.com/michaelbrooks/music-visualization
 * Copyright 2012, Michael Brooks. BSD License.
 */
package dispatch;

/**
 *
 * @author michael
 */
class EventRunner<RT extends AudioEvent> implements Runnable {

    ListenerRegistration<RT> reg;
    RT event;

    public EventRunner(ListenerRegistration<RT> reg, RT event) {
        this.reg = reg;
        this.event = event;
    }

    public void run() {
        AudioListener<RT> listener = reg.getListener();
        listener.eventFired(event, reg.getUserData());
    }
}
