package com.example.marveldefence.ui;

import android.view.Choreographer;

/** Um relógio decorativo contínuo por superfície; o tempo em suspensão não é acumulado. */
final class VisualClock implements Choreographer.FrameCallback {
    interface Listener { void frame(double seconds); }
    private final Listener listener;
    private boolean running;
    private long previous;
    private double seconds;
    VisualClock(Listener listener) { this.listener = listener; }
    void running(boolean enabled) {
        if (running == enabled) return;
        running = enabled; previous = 0;
        if (enabled) Choreographer.getInstance().postFrameCallback(this);
        else Choreographer.getInstance().removeFrameCallback(this);
    }
    @Override public void doFrame(long time) {
        if (!running) return;
        if (previous != 0) seconds += Math.min(.1,(time-previous)/1_000_000_000.0);
        previous = time; listener.frame(seconds);
        if (running) Choreographer.getInstance().postFrameCallbackDelayed(this,33);
    }
}
