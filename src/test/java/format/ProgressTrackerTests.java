package format;

import com.kttdevelopment.rexedia.format.ProgressTracker;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

public class ProgressTrackerTests {

    // tests aren't perfect so there is a small margin of error
    private static final int delta = 15;

    @Test
    public void testSingle() throws InterruptedException{
        final long now = System.currentTimeMillis();
        final float duration = 5;
        final ProgressTracker tracker = new ProgressTracker(Collections.singletonList(duration));

        // init
        ProgressTracker.Progress progress = tracker.getProgress();
        Assert.assertEquals(0,progress.getFilesProcessed());
        Assert.assertEquals(1,progress.getTotalFiles());
        Assert.assertEquals(0,progress.getTimeElapsed(), delta);
        Assert.assertEquals(now + (duration * 1000 * progress.getTotalFiles()), progress.getEstCompleteTime(), delta);

        // after duration
        Thread.sleep((long) (duration * 1000));

        progress = tracker.getProgress();
        Assert.assertEquals(0,progress.getFilesProcessed());
        Assert.assertEquals(1,progress.getTotalFiles());
        Assert.assertEquals(duration * 1000,progress.getTimeElapsed(),delta);
        Assert.assertEquals(System.currentTimeMillis(), tracker.getProgress().getEstCompleteTime(),delta);
    }

    @Test
    public void testDouble() throws InterruptedException{
        final long now = System.currentTimeMillis();
        final float duration = 5;
        final ProgressTracker tracker = new ProgressTracker(Arrays.asList(duration,duration));

        // init
        ProgressTracker.Progress progress = tracker.getProgress();
        Assert.assertEquals(0,progress.getFilesProcessed());
        Assert.assertEquals(2,progress.getTotalFiles());
        Assert.assertEquals(0,progress.getTimeElapsed(), delta);
        Assert.assertEquals(now + (duration * 1000 * progress.getTotalFiles()), progress.getEstCompleteTime(), delta);

        // after duration
        Thread.sleep((long) (duration * 500));
        tracker.next();

        progress = tracker.getProgress();
        Assert.assertEquals(1,progress.getFilesProcessed());
        Assert.assertEquals(2,progress.getTotalFiles());
        Assert.assertEquals(duration * 500 * progress.getFilesProcessed(),progress.getTimeElapsed(),delta);
        Assert.assertEquals(System.currentTimeMillis() + duration/2, tracker.getProgress().getEstCompleteTime(),delta);
    }

}
