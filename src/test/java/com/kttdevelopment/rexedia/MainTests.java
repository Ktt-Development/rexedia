package com.kttdevelopment.rexedia;

import com.kttdevelopment.core.tests.TestUtil;
import com.kttdevelopment.rexedia.format.FFMPEG;
import com.kttdevelopment.rexedia.logger.LoggerFormatter;
import org.junit.*;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.Objects;
import java.util.UUID;
import java.util.logging.*;
import java.util.regex.Pattern;

public class MainTests {

    private static final File main = new File("src/test/resources/main");

    private static final File corrupt = new File("src/test/resources/main/corrupt.mp4");
    private static final File video   = new File("src/test/resources/main/video.mp4");

    private static final Path sc = Paths.get("src/test/resources/format/corrupt.mp4");
    private static final Path sv = Paths.get("src/test/resources/format/video.mp4");

    private static final FFMPEG ffmpeg = new FFMPEG("bin/ffmpeg.exe", "bin/ffprobe.exe");

    @BeforeClass
    public static void setup() throws IOException{
        // logging
        Logger.getGlobal().setLevel(Level.ALL);
        Logger.getGlobal().setUseParentHandlers(false);
        if(Logger.getGlobal().getHandlers().length == 0)
            Logger.getGlobal().addHandler(new ConsoleHandler() {{
            setLevel(Level.ALL);
            setFormatter(new LoggerFormatter(false, true));
        }});

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

        corrupt.deleteOnExit();
        video.deleteOnExit();
    }

    @AfterClass
    public static void cleanup() throws IOException{
        for(final File file : Objects.requireNonNullElse(main.listFiles(), new File[0]))
            Files.delete(file.toPath());
    }

    @Rule
    public final TemporaryFolder dir = new TemporaryFolder(new File("."));

    //

    @Test
    public void testLogging(){
        final String unique = String.valueOf(UUID.randomUUID());
        final File latest   = new File("latest.log");
        final File debug    = new File("debug.log");

        Assert.assertNull("The testing directory was not clear. Please remove log files.",getFile(new File("."),"\\d+\\Q.log\\E"));

        latest.deleteOnExit();
        debug.deleteOnExit();

        final String input = video.getAbsolutePath();
        // test none
        Main.main(new String[]{
            "-i", '"' + input + '"',
            "-m", "\"title\"", "\"(.+)\"", "\"$1\"",
            "-o", "\"(.+)\"", "\"$1-" + unique + "\"",
        });

        Assert.assertNull(getFile(new File("."),"\\d+\\Q.log\\E"));
        Assert.assertFalse(latest.exists());
        Assert.assertFalse(debug.exists());

        // test logging
        final File log;
        Main.main(new String[]{
            "-i", '"' + input + '"',
            "-m", "\"title\"", "\"(.+)\"", "\"$1\"",
            "-o", "\"(.+)\"", "\"$1-" + unique + "\"",
            "-l"
        });

        Assert.assertNotNull(log = getFile(new File("."),"\\d+\\Q.log\\E"));
        Assert.assertTrue(latest.exists());

        log.deleteOnExit();

        // test debug
        Main.main(new String[]{
            "-i", '"' + input + '"',
            "-m", "\"title\"", "\"(.+)\"", "\"$1\"",
            "-o", "\"(.+)\"", "\"$1-" + unique + "\"",
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
        final File walk = dir.newFolder();
        final File walkv = new File(walk,"video.mp4");
        Assert.assertTrue(walk.exists() || walk.mkdirs());
        Files.copy(video.toPath(),walkv.toPath(),StandardCopyOption.REPLACE_EXISTING);
        walkv.deleteOnExit();

        // test corrupt
        {
            final String unique = String.valueOf(UUID.randomUUID());
            final String input = corrupt.getAbsolutePath();

            Main.main(new String[]{
                "-i", '"' + input + '"',
                "-m", "\"title\"", "\"(.+)\"", "\"$1\"",
                "-o", "\"(.+)\"", "\"$1-" + unique + "\""
            });
            Assert.assertFalse(new File(dir.getRoot(), "video-" + unique + ".mp4").exists());
        }
        // test no walk
        {
            final String unique = String.valueOf(UUID.randomUUID());
            final String input = video.getAbsolutePath();

            Main.main(new String[]{
                "-i", '"' + input + '"',
                "-m", "\"title\"", "\"(.+)\"", "\"$1\"",
                "-o", "\"(.+)\"", "\"$1-" + unique + "\""
            });

            final File output = new File(dir.getRoot(), "video-" + unique + ".mp4");
            final File outputw = new File(walk, "video-" + unique + ".mp4");
            output.deleteOnExit();

            Assert.assertTrue(output.exists());
            Assert.assertFalse(outputw.exists());
            Assert.assertEquals("video", ffmpeg.getMetadata(output).get("title"));
        }
        // test walk
        {
            final String unique = String.valueOf(UUID.randomUUID());
            final String input  = video.getParentFile().getAbsolutePath();

            Main.main(new String[]{
                "-i", '"' + input + '"',
                "-m", "\"title\"", "\"(.+)\"", "\"$1\"",
                "-o", "\"(.+)\"", "\"$1-" + unique + "\"",
                "-w"
            });

            final File output = new File(dir.getRoot(), "video-" + unique + ".mp4");
            output.deleteOnExit();
            final File outputw = new File(walk, "video-" + unique + ".mp4");
            outputw.deleteOnExit();

            Assert.assertTrue(output.exists());
            Assert.assertEquals("video", ffmpeg.getMetadata(output).get("title"));
            Assert.assertTrue(outputw.exists());
            Assert.assertEquals("video", ffmpeg.getMetadata(outputw).get("title"));
        }
    }

    @Test
    public void testBackup(){
        final String unique = String.valueOf(UUID.randomUUID());
        final String input  = video.getAbsolutePath();

        Main.main(new String[]{
            "-i", '"' + input + '"',
            "-m", "\"title\"", "\"(.+)\"", "\"$1\"",
            "-o", "\"(.+)\"", "\"$1-" + unique + "\""
        });

        Assert.assertNull(getFile(dir.getRoot(),".+\\Q.backup.\\E.+"));

        Main.main(new String[]{
            "-i", '"' + input + '"',
            "-m", "\"title\"", "\"(.+)\"", "\"$1\"",
            "-o", "\"(.+)\"", "\"$1-" + unique + "\"" +
            "-b"
        });

        Assert.assertNotNull(getFile(dir.getRoot(),".+\\Q.backup.\\E.+"));
    }

    @Test
    public void testPresetOverrideArgs() throws IOException{
        final String unique = String.valueOf(UUID.randomUUID());
        final String input  = video.getAbsolutePath();

        final String yml =
            "metadata:\n" +
            "  - meta: 'title'\n" +
            "    regex: '(.+)'\n" +
            "    format: '" + unique + "'\n" +
            "output:\n" +
            "  regex: '(.+)'\n" +
            "  format: '$1-" + unique + "'";

        final File preset = dir.newFile();
        Files.write(preset.toPath(),yml.getBytes());

        TestUtil.createTestFile(
            new File("preset.yml"),
            "metadata:\n" +
            "  - meta: 'title'\n" +
            "    regex: '(.+)'\n" +
            "    format: '" + unique + "'\n" +
            "output:\n" +
            "  regex: '(.+)'\n" +
            "  format: '$1-" + unique + "'"
        );

        Main.main(new String[]{
            "-i", '"' + input + '"',
            "-m", "\"title\"", "\"(.+)\"", "\"$1\"",
            "-p", '"' + preset.getAbsolutePath() + '"'
        });

        final File output = new File(dir.getRoot(),"video-" + unique + ".mp4");
        output.deleteOnExit();
        Assert.assertEquals(unique, ffmpeg.getMetadata(output).get("title"));
    }

}
