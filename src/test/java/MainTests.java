import com.kttdevelopment.core.tests.TestUtil;
import com.kttdevelopment.rexedia.Main;
import com.kttdevelopment.rexedia.format.FFMPEG;
import com.kttdevelopment.rexedia.logger.LoggerFormatter;
import org.junit.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.Objects;
import java.util.logging.*;
import java.util.regex.Pattern;

public class MainTests {

    private static final File main    = new File("src/test/resources/main");
    private static final File corrupt = new File("src/test/resources/main/corrupt.mp4");
    private static final File video   = new File("src/test/resources/main/video.mp4");

    private static final Path sc = Paths.get("src/test/resources/format/corrupt.mp4");
    private static final Path sv = Paths.get("src/test/resources/format/video.mp4");

    private static FFMPEG ffmpeg = new FFMPEG();

    @BeforeClass
    public static void setup() throws IOException{
        // logging
        Logger.getGlobal().setLevel(Level.ALL);
        Logger.getGlobal().setUseParentHandlers(false);
        Logger.getGlobal().addHandler(new ConsoleHandler() {{
            setLevel(Level.ALL);
            setFormatter(new LoggerFormatter(false, true));
        }});

        // ffmpeg
        if(!ffmpeg.isValidInstallation()) // use bin if no local installation
            ffmpeg = new FFMPEG("bin/ffmpeg.exe", "bin/ffprobe.exe");

        // files
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

    //

    @Test
    public void testLogging(){
        final File latest = new File("latest.log");
        final File debug  = new File("debug.log");

        Assert.assertNull("The testing directory was not clear. Please remove log files.",getFile(new File("."),"\\d+\\Q.log\\E"));

        latest.deleteOnExit();
        debug.deleteOnExit();

        final String input = video.getAbsolutePath();
        // test none
        Main.main(new String[]{
            "-i", '"' + input + '"',
            "-m", "\"title\"", "\"(.+)\"", "\"$1\"",
            "-o", "(.+)", "\"$1-logging\""
        });

        Assert.assertNull(getFile(new File("."),"\\d+\\Q.log\\E"));
        Assert.assertFalse(latest.exists());
        Assert.assertFalse(debug.exists());

        // test logging
        final File log;
        Main.main(new String[]{
            "-i", '"' + input + '"',
            "-m", "\"title\"", "\"(.+)\"", "\"$1\"",
            "-o", "(.+)", "\"$1-logging\"",
            "-l"
        });

        Assert.assertNotNull(log = getFile(new File("."),"\\d+\\Q.log\\E"));
        Assert.assertTrue(latest.exists());

        log.deleteOnExit();

        // test debug
        Main.main(new String[]{
            "-i", '"' + input + '"',
            "-m", "\"title\"", "\"(.+)\"", "\"$1\"",
            "-o", "(.+)", "\"$1-logging\"",
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

    @Test // this method also tests preset as args
    public void testInput() throws IOException{
        final File walk = new File(main,"walk");
        final File walkv = new File(walk,"video.mp4");
        Assert.assertTrue(walk.exists() || walk.mkdirs());
        Files.copy(video.toPath(),walkv.toPath(),StandardCopyOption.REPLACE_EXISTING);
        walkv.deleteOnExit();

        // test corrupt
        {
            final String input = corrupt.getAbsolutePath();

            Main.main(new String[]{
                "-i", '"' + input + '"',
                "-m", "\"title\"", "\"(.+)\"", "\"$1\"",
                "-o", "(.+)", "\"$1-corrupt\""
            });
            Assert.assertFalse(new File(main, "video-corrupt.mp4").exists());
        }
        // test no walk
        {
            final String input = video.getAbsolutePath();

            Main.main(new String[]{
                "-i", '"' + input + '"',
                "-m", "\"title\"", "\"(.+)\"", "\"$1\"",
                "-o", "(.+)", "\"$1-valid\""
            });

            final File output = new File(main, "video-valid.mp4");
            final File outputw = new File(walk, "video-valid.mp4");
            output.deleteOnExit();

            Assert.assertTrue(output.exists());
            Assert.assertFalse(outputw.exists());
            Assert.assertEquals("video", ffmpeg.getMetadata(output).get("title"));
        }
        // test walk
        {
            final String input = video.getAbsolutePath();

            Main.main(new String[]{
                "-i", '"' + input + '"',
                "-m", "\"title\"", "\"(.+)\"", "\"$1\"",
                "-o", "(.+)", "\"$1-valid2\""
            });

            final File output = new File(main, "video-valid2.mp4");
            output.deleteOnExit();
            final File outputw = new File(walk, "video-valid2.mp4");
            outputw.deleteOnExit();

            Assert.assertTrue(output.exists());
            Assert.assertEquals("video", ffmpeg.getMetadata(output).get("title"));
            Assert.assertTrue(outputw.exists());
            Assert.assertEquals("video", ffmpeg.getMetadata(outputw).get("title"));
        }
    }

    @Test
    public void testBackup(){
        final String input = video.getAbsolutePath();

        Main.main(new String[]{
            "-i", '"' + input + '"',
            "-m", "\"title\"", "\"(.+)\"", "\"$1\"",
            "-o", "(.+)", "\"$1-backup\""
        });

        Assert.assertNull(getFile(main,".+\\Q.backup.\\E.+"));

        Main.main(new String[]{
            "-i", '"' + input + '"',
            "-m", "\"title\"", "\"(.+)\"", "\"$1\"",
            "-o", "(.+)", "\"$1-backup\"",
            "-b"
        });

        Assert.assertNotNull(getFile(main,".+\\Q.backup.\\E.+"));
    }

    @Test
    public void testPresetOverrideArgs(){
        final File preset = new File("preset.yml");
        final String input = video.getAbsolutePath();

        TestUtil.createTestFile(
            new File("preset.yml"),
            "metadata:\n" +
            "  - meta: 'title'\n" +
            "    regex: '(.+)'\n" +
            "    format: '$1.preset'"
        );

        Main.main(new String[]{
            "-i", '"' + input + '"',
            "-m", "\"title\"", "\"(.+)\"", "\"$1\"",
            "-o", "(.+)", "\"$1-preset\"",
            "-p", '"' + preset.getAbsolutePath() + '"'
        });

        Assert.assertEquals("video.preset", ffmpeg.getMetadata(new File(main,"video-preset.mp4")).get("title"));
    }

}
