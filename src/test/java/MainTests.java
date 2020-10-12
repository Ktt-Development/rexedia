import com.kttdevelopment.rexedia.Main;
import com.kttdevelopment.rexedia.logger.LoggerFormatter;
import org.junit.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.Objects;
import java.util.logging.*;
import java.util.regex.Pattern;

public class MainTests {

    private static final File corrupt = new File("src/test/resources/main/corrupt.mp4");
    private static final File video   = new File("src/test/resources/main/video.mp4");

    private static final Path sc = Paths.get("src/test/resources/format/corrupt.mp4");
    private static final Path sv = Paths.get("src/test/resources/format/video.mp4");

    @BeforeClass
    public static void setup() throws IOException{
        // logging
        Logger.getGlobal().setLevel(Level.ALL);
        Logger.getGlobal().setUseParentHandlers(false);
        Logger.getGlobal().addHandler(new ConsoleHandler() {{
            setLevel(Level.ALL);
            setFormatter(new LoggerFormatter(false, true));
        }});

        // files
        final File main = new File("src/test/resources/main");
        Assert.assertTrue(main.exists() || main.mkdirs());

        Files.copy(
                sc,
            corrupt.toPath(),
            StandardCopyOption.REPLACE_EXISTING
        );
        Files.copy(
                sv,
            video.toPath(),
            StandardCopyOption.REPLACE_EXISTING
        );
    }

    @AfterClass
    public static void cleanup() throws IOException{
        Files.delete(corrupt.toPath());
        Files.delete(video.toPath());
    }

    @Test
    public void testLogging(){
        final File latest = new File("latest.log");
        final File debug  = new File("debug.log");

        Assert.assertNull("The testing directory was not clear. Please remove log files.",getFile(new File("."),"\\d+\\Q.log\\E"));

        latest.deleteOnExit();
        debug.deleteOnExit();

        final String input = video.getPath();
        // test none
        Main.main(new String[]{
            "-i", '"' + input + '"',
            "-m", "\"title\"", "\"(.+)\"", "\"$1\"",
            "-o", "(.+)", "\"$1.avi\""
        });

        Assert.assertTrue(new File(sv.getParent().toFile(),"video.avi").exists()); // test output flag

        Assert.assertNull(getFile(new File("."),"\\d+\\Q.log\\E"));
        Assert.assertFalse(latest.exists());
        Assert.assertFalse(debug.exists());

        // test logging
        final File log;
        Main.main(new String[]{
            "-i", '"' + input + '"',
            "-m", "\"title\"", "\"(.+)\"", "\"$1\"",
            "-o", "(.+)", "\"$1.avi\"",
            "-l"
        });
        Assert.assertNotNull(log = getFile(new File("."),"\\d+\\Q.log\\E"));
        Assert.assertTrue(latest.exists());

        log.deleteOnExit();

        // test debug
        Main.main(new String[]{
            "-i", '"' + input + '"',
            "-m", "\"title\"", "\"(.+)\"", "\"$1\"",
            "-o", "(.+)", "\"$1.avi\"",
            "-d"
        });
        Assert.assertTrue(debug.exists());
    }

    private File getFile(final File directory, final String regex){
        final Pattern pattern = Pattern.compile(regex);
        for(final File file : Objects.requireNonNullElse(directory.listFiles(),new File[0]))
            if(pattern.matcher(file.getName()).matches())
                return file;
        return null;
    }

    private int getMatches(final File directory, final String regex){
        int matches = 0;
        final Pattern pattern = Pattern.compile(regex);
        for(final File file : Objects.requireNonNullElse(directory.listFiles(),new File[0]))
            if(pattern.matcher(file.getName()).matches())
                matches++;
        return matches;
    }

    @Test @Ignore // test if output exists and can verify
    public void testInput(){
        // test corrupt

        // test no walk
            // ffmpeg verify tests
        // test walk
            // ffmpeg verify tests
    }

    @Test @Ignore // test only if backup exists
    public void testBackup(){

    }

    @Test @Ignore // verify meta
    public void testPresetAsArgs(){

    }

    @Test @Ignore // verify meta
    public void testPresetOverrideArgs(){

    }

}
