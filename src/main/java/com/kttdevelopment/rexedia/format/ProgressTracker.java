package com.kttdevelopment.rexedia.format;

import com.kttdevelopment.core.classes.ToStringBuilder;

import java.util.*;
import java.util.concurrent.atomic.*;

public final class ProgressTracker {

    private final List<Float> fileDuration;
    private final int size;
    private final AtomicInteger index = new AtomicInteger(0);

    private final float totalDuration;
    private final AtomicReference<Float> remainingDuration = new AtomicReference<>(0f);
    private final AtomicReference<Float> dps               = new AtomicReference<>(1f);

    private final long start = System.currentTimeMillis();

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
        final long now = System.currentTimeMillis();

        final float durationElapsed = fileDuration.get(index.get());
        remainingDuration.getAndUpdate(f -> f - durationElapsed);
        index.incrementAndGet();

        // average elapsed time is total elapsed time over processed duration
        dps.set((start - now) / Math.max(totalDuration - remainingDuration.get(),1));
    }

    public final Progress getProgress(){
        final long now = System.currentTimeMillis();
        return new Progress(
            index.get(),
            size,
            now - start,
            now - (long) (remainingDuration.get() * dps.get())
        );
    }

    @Override
    public String toString(){
        return new ToStringBuilder(getClass().getSimpleName())
            .addObject("fileDuration",fileDuration)
            .addObject("size",size)
            .addObject("index",index.get())
            .addObject("totalDuration",totalDuration)
            .addObject("remainingDuration",remainingDuration.get())
            .addObject("dps",dps.get())
            .addObject("stat",start)
            .toString();
    }

    public static class Builder{

        private final List<Float> fileDuration = new ArrayList<>();

        public Builder(){ }

        public final Builder addFileDuration(final float duration){
            fileDuration.add(duration);
            return this;
        }

        public final ProgressTracker build(){
            return new ProgressTracker(fileDuration);
        }

    }

    public static class Progress{

        private final int filesProcessed, totalFiles;
        private final long timeElapsed, estCompleteTime;

        public Progress(final int filesProcessed, final int totalFiles, final long timeElapsed, final long estTimeRemaining){
            this.filesProcessed = filesProcessed;
            this.totalFiles = totalFiles;
            this.timeElapsed = timeElapsed;
            this.estCompleteTime = estTimeRemaining;
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

        public final long getEstCompleteTime(){
            return estCompleteTime;
        }

        //

        @Override
        public String toString(){
            return new ToStringBuilder(getClass().getSimpleName())
                .addObject("filesProcessed",filesProcessed)
                .addObject("totalFiles",totalFiles)
                .addObject("timeElapsed",timeElapsed)
                .addObject("estCompleteTime", estCompleteTime)
                .toString();
        }

    }

}
