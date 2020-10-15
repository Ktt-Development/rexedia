package com.kttdevelopment.rexedia;

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

    private static final Path sc = Paths.get("src/test/resources/format/corrupt.mp4");
    private static final Path sv = Paths.get("src/test/resources/format/video.mp4");

    private static final FFMPEG ffmpeg = new FFMPEG("bin/ffmpeg.exe", "bin/ffprobe.exe");

    @BeforeClass
    public static void setup(){
        // logging
        Logger.getGlobal().setLevel(Level.ALL);
        Logger.getGlobal().setUseParentHandlers(false);
        if(Logger.getGlobal().getHandlers().length == 0)
            Logger.getGlobal().addHandler(new ConsoleHandler() {{
            setLevel(Level.ALL);
            setFormatter(new LoggerFormatter(false, true));
        }});
    }

    @Rule
    public final TemporaryFolder dir = new TemporaryFolder(new File("."));

    //

    @Test
    public void testLogging() throws IOException{
        final File latest   = new File("latest.log");
        final File debug    = new File("debug.log");

        Assert.assertNull("The testing directory was not clear. Please remove log files.",getFile(new File("."),"\\d+\\Q.log\\E"));

        latest.deleteOnExit();
        debug.deleteOnExit();

        final String unique = String.valueOf(UUID.randomUUID());
        final File input    = dir.newFile(unique + ".mp4");
        Files.copy(sv,input.toPath(),StandardCopyOption.REPLACE_EXISTING);

        // test none
        Main.main(new String[]{
            "-i", '"' + input.getAbsolutePath() + '"',
            "-m", "\"title\"", "\"(.+)\"", "\"$1\"",
            "-o", "\"(.+)\"", '"' + dir.newFile().getName() + ".avi\"",
        });

        Assert.assertNull(getFile(new File("."),"\\d+\\Q.log\\E"));
        Assert.assertFalse(latest.exists());
        Assert.assertFalse(debug.exists());

        // test logging
        final File log;
        Main.main(new String[]{
            "-i", '"' + input.getAbsolutePath() + '"',
            "-m", "\"title\"", "\"(.+)\"", "\"$1\"",
            "-o", "\"(.+)\"", '"' + dir.newFile().getName() + ".avi\"",
            "-l"
        });

        Assert.assertNotNull(log = getFile(new File("."),"\\d+\\Q.log\\E"));
        Assert.assertTrue(latest.exists());

        log.deleteOnExit();

        // test debug
        Main.main(new String[]{
            "-i", '"' + input.getAbsolutePath() + '"',
            "-m", "\"title\"", "\"(.+)\"", "\"$1\"",
            "-o", "\"(.+)\"", '"' + dir.newFile().getName() + ".avi\"",
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
        // test corrupt
        {
            final String unique = String.valueOf(UUID.randomUUID());
            final File input    = dir.newFile(unique + ".mp4");
            Files.copy(sc,input.toPath(),StandardCopyOption.REPLACE_EXISTING);

            Main.main(new String[]{
                "-i", '"' + input.getAbsolutePath() + '"',
                "-m", "\"title\"", "\"(.+)\"", "\"$1\"",
                "-o", "\"(.+)\"", "\"$1.avi\""
            });
            Assert.assertFalse(new File(dir.getRoot(), unique + ".avi").exists());
        }

        // test walk
        {
            final String unique = String.valueOf(UUID.randomUUID());
            // test no walk
            final File walk = dir.newFolder();
            final File walkv = new File(walk,unique + ".mp4");

            Files.copy(sv,walkv.toPath(),StandardCopyOption.REPLACE_EXISTING);
            walkv.deleteOnExit();

            final File input    = dir.newFile(unique + ".mp4");
            Files.copy(sv,input.toPath(),StandardCopyOption.REPLACE_EXISTING);

            Main.main(new String[]{
                "-i", '"' + input.getAbsolutePath() + '"',
                "-m", "\"title\"", "\"(.+)\"", "\"$1\"",
                "-o", "\"(.+)\"", "\"$1.avi\""
            });

            final File output = new File(dir.getRoot(), unique + ".avi");
            final File outputw = new File(walk, unique + ".avi");
            output.deleteOnExit();
            outputw.deleteOnExit();

            Assert.assertTrue(output.exists());
            Assert.assertFalse(outputw.exists());
            Assert.assertEquals(unique, ffmpeg.getMetadata(output).get("title"));

            // test walk

            Main.main(new String[]{
                "-i", '"' + input.getParentFile().getAbsolutePath() + '"',
                "-m", "\"title\"", "\"(.+)\"", "\"$1\"",
                "-o", "\"(.+)\"", "\"$1.avi\"",
                "-w"
            });

            Assert.assertTrue(output.exists());
            Assert.assertEquals(unique, ffmpeg.getMetadata(output).get("title"));
            Assert.assertTrue(outputw.exists());
            Assert.assertEquals(unique, ffmpeg.getMetadata(outputw).get("title"));
        }
    }

    @Test
    public void testBackup() throws IOException{
        final String unique = String.valueOf(UUID.randomUUID());
        final File input    = dir.newFile(unique + ".mp4");
        Files.copy(sv,input.toPath(),StandardCopyOption.REPLACE_EXISTING);

        Main.main(new String[]{
            "-i", '"' + input.getAbsolutePath() + '"',
            "-m", "\"title\"", "\"(.+)\"", "\"$1\"",
            "-o", "\"(.+)\"", "\"$1.avi\""
        });

        Assert.assertNull(getFile(dir.getRoot(),".+\\Q.backup.\\E.+"));

        Main.main(new String[]{
            "-i", '"' + input.getAbsolutePath() + '"',
            "-m", "\"title\"", "\"(.+)\"", "\"$1\"",
            "-o", "\"(.+)\"", "\"$1.avi\"" +
            "-b"
        });

        Assert.assertNotNull(getFile(dir.getRoot(),".+\\Q.backup.\\E.+"));
    }

    @Test
    public void testPresetOverrideArgs() throws IOException{
        final String unique = String.valueOf(UUID.randomUUID());
        final File input    = dir.newFile(unique + ".mp4");
        Files.copy(sv,input.toPath(),StandardCopyOption.REPLACE_EXISTING);

        final String yml =
            "metadata:\n" +
            "  - meta: 'title'\n" +
            "    regex: '(.+)'\n" +
            "    format: '$1'\n" +
            "output:\n" +
            "  regex: '(.+)'\n" +
            "  format: '$1.avi'\n";

        final File preset = dir.newFile();
        Files.write(preset.toPath(),yml.getBytes());

        Main.main(new String[]{
            "-i", '"' + input.getAbsolutePath() + '"',
            "-m", "\"title\"", "\"(.+)\"", "\"$1\"",
            "-p", '"' + preset.getAbsolutePath() + '"'
        });

        final File output = new File(dir.getRoot(),unique + ".avi");
        output.deleteOnExit();
        Assert.assertEquals(unique, ffmpeg.getMetadata(output).get("title"));
    }

}
