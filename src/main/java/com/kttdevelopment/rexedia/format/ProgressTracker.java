package com.kttdevelopment.rexedia.format;

import java.util.*;
import java.util.concurrent.atomic.*;

public final class ProgressTracker {

    private final List<Float> fileDuration;
    private final int size;
    private final AtomicInteger index = new AtomicInteger(0);

    private final float totalDuration;
    private final AtomicReference<Float> remainingDuration = new AtomicReference<>(0f);
    private final AtomicReference<Float> dps               = new AtomicReference<>(1f);

    private final long start        = System.currentTimeMillis();
    private final AtomicLong latest = new AtomicLong(start);

    public ProgressTracker(final List<Float> fileDuration){
        this.fileDuration = Collections.unmodifiableList(fileDuration);
        this.size = fileDuration.size();

        float duration = 0;
        for(final Float aFloat : fileDuration)
            duration += aFloat;
        this.totalDuration = duration;
        this.remainingDuration.set(duration);
    }

    public synchronized final void next(){
        final long now     = System.currentTimeMillis();
        final long elapsed = latest.get() - now;
        latest.set(now);

        final float durationElapsed = fileDuration.get(index.get());
        remainingDuration.getAndUpdate(f -> f - durationElapsed);
        index.incrementAndGet();

        // average elapsed time is total elapsed time over processed duration
        dps.set((start - now) / Math.max(totalDuration - remainingDuration.get(),1));
    }

    public final Progress getProgress(){
        return new Progress(index.get() + 1, size, start - System.currentTimeMillis(), (long) (remainingDuration.get() * dps.get()));
    }

    private static class Progress{

        private final int filesProcessed, totalFiles;
        private final long timeElapsed, estTimeRemaining;

        public Progress(final int filesProcessed, final int totalFiles, final long timeElapsed, final long estTimeRemaining){
            this.filesProcessed = filesProcessed;
            this.totalFiles = totalFiles;
            this.timeElapsed = timeElapsed;
            this.estTimeRemaining = estTimeRemaining;
        }

        public final int getFilesProcessed(){
            return filesProcessed;
        }

        public final int getTotalFiles(){
            return totalFiles;
        }

        public final long getTimeElapsed(){
            return timeElapsed;
        }

        public final long getEstTimeRemaining(){
            return estTimeRemaining;
        }

    }

}
