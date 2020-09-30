package com.kttdevelopment.rexedia.format;

import java.util.*;
import java.util.concurrent.atomic.*;
import java.util.function.UnaryOperator;

public final class ProgressTracker {

    private final List<Integer> size                = Collections.synchronizedList(new ArrayList<>());
    private final List<Float> dps                   = Collections.synchronizedList(new ArrayList<>());
    private final List<List<Float>> fileDuration    = Collections.synchronizedList(new ArrayList<>());

    private final AtomicReference<Float> totalDuration = new AtomicReference<>(0f);
    private final AtomicReference<Float> remainingDuration = new AtomicReference<>(0f);
    private long latest = -1;

    public synchronized final void addFileDuration(final int thread, final float duration){
        if(fileDuration.get(thread) == null){
            fileDuration.add(new ArrayList<>());
            size.add(0);
            dps.add(1f);
        }
        fileDuration.get(thread).add(duration);
        size.set(thread,size.get(thread) + 1);
        totalDuration.getAndUpdate(aFloat -> aFloat + duration);
        remainingDuration.getAndUpdate(aFloat -> aFloat + duration);
    }

    public synchronized final void next(final int thread){
        if(fileDuration.get(thread) == null) return;

        final float delapsed = fileDuration.get(thread).get(0);
        remainingDuration.getAndUpdate(aFloat -> aFloat - delapsed);
        fileDuration.get(thread).remove(0);

        final long now = System.currentTimeMillis(); // total time elapsed
        //elapsed += latest - now;
        latest = now;

        //dps.set(thread, (float) (elapsed / Math.max(delapsed, 1))); // average elapsed time per second in file
    }

    public synchronized final void start(){
        if(latest != -1) latest = System.currentTimeMillis();
    }

    public final String getProgressString(){
        final StringBuilder OUT = new StringBuilder();
        final int size = fileDuration.size();
        for(int i = 0; i < size; i++){
            OUT.append(fileDuration.get(i).size() / this.size.get(i).floatValue());
            if(i < size -1)
                OUT.append(" | ");
        }

        final float dps = Collections.max(this.dps);



        return OUT.toString();
    }

}
