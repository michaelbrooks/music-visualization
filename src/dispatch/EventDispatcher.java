/*
 * Music Visualizations: http:/github.com/michaelbrooks/music-visualization
 * Copyright 2012, Michael Brooks. BSD License.
 */

package dispatch;

import java.util.HashSet;
import java.util.concurrent.ExecutorService;

/**
 *
 * @author michael
 */
public class EventDispatcher<T extends AudioEvent> {
    
    private ExecutorService executor;

    private HashSet<ListenerRegistration<T>> registry;

    public EventDispatcher(ExecutorService executor)
    {
        this.executor = executor;
        registry = new HashSet<ListenerRegistration<T>>();
    }

    public void addListener(AudioListener<T> l)
    {
        addListener(l, null);
    }

    public void addListener(AudioListener<T> l, Object userData)
    {
        ListenerRegistration reg = new ListenerRegistration(l, userData);
        registry.add(reg);
    }

    public void removeListener(AudioListener<T> l)
    {
        removeListener(l, null);
    }

    public void removeListener(AudioListener<T> l, Object userData)
    {
        ListenerRegistration<T> reg = new ListenerRegistration<T>(l, userData);
        registry.remove(reg);
    }

    public int getListenerCount()
    {
        return registry.size();
    }

    public void fireEvent(T event) {
        for (ListenerRegistration<T> reg : registry) {
            EventRunner<T> runner = new EventRunner<T>(reg, event);
            executor.execute(runner);
            //runner.run();
        }
    }

    public void shutdown()
    {
        executor.shutdown();
    }


}
