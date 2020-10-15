package com.kttdevelopment.rexedia.format;

import com.kttdevelopment.rexedia.logger.LoggerFormatter;
import org.junit.*;
import org.junit.rules.TemporaryFolder;

import java.io.*;
import java.nio.file.Files;
import java.util.Map;
import java.util.UUID;
import java.util.logging.*;

@SuppressWarnings("SpellCheckingInspection")
public class ffmpegTests {

    private static final FFMPEG ffmpeg = new FFMPEG("bin/ffmpeg.exe", "bin/ffprobe.exe");

    @BeforeClass
    public static void initFFMPEG() throws IOException{
        Logger.getGlobal().setLevel(Level.ALL);
        Logger.getGlobal().setUseParentHandlers(false);
        if(Logger.getGlobal().getHandlers().length == 0)
            Logger.getGlobal().addHandler(new ConsoleHandler() {{
                setLevel(Level.ALL);
                setFormatter(new LoggerFormatter(false,true));
            }});

        if(!input.exists())
            Files.copy(new File("src/test/resources/format/video.mp4").toPath(),input.toPath());
    }

    @AfterClass
    public static void denitFFMPEG(){
        try{ Files.delete(input.toPath());
        }catch(final Throwable ignored){ }
        try{ Files.delete(cover2.toPath());
        }catch(final Throwable ignored){ }
        try{ Files.delete(cover3.toPath());
        }catch(final Throwable ignored){ }
        try{ Files.delete(cover4.toPath());
        }catch(final Throwable ignored){ }
    }

    @Rule
    public final TemporaryFolder dir = new TemporaryFolder(new File("."));

    @Test
    public void testDuration() throws IOException{
        Assert.assertEquals(4.97f,ffmpeg.getDuration(new File("src/test/resources/format/video.mp4")),0);
    }

    @Test
    public void testVerify(){
        Assert.assertTrue(ffmpeg.verifyFileIntegrity(new File("src/test/resources/format/video.mp4")));
        Assert.assertFalse(ffmpeg.verifyFileIntegrity(new File("src/test/resources/format/corrupt.mp4")));
    }

    private static final File input = new File("src/test/resources/format/apply/clean.mp4");
    private static final File cover = new File("src/test/resources/format/apply/cover.png");

    private static final File cover2 = new File("src/test/resources/format/apply/cover2.png");
    private static final File cover3 = new File("src/test/resources/format/apply/cover3.png");
    private static final File cover4 = new File("src/test/resources/format/apply/cover4.png");
    
    @SuppressWarnings("ConstantConditions")
    @Test
    public void missingArgs(){
        Assert.assertThrows(FileNotFoundException.class, () -> ffmpeg.apply(null, null, false, null, false, null));
        Assert.assertThrows(FileNotFoundException.class, () -> ffmpeg.apply(new File("null"), null, false, null, false, null));
        Assert.assertThrows(FileNotFoundException.class, () -> ffmpeg.apply(input, null, false, null, false, null));
    }

    @Test
    public void testApplyNone() throws IOException{
        final File out = dir.newFile();

        ffmpeg.apply(input,null,true,null,true,out);
        Assert.assertEquals(input.length(),out.length());
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    public void testApplyCover() throws IOException{
        final File out = dir.newFile(UUID.randomUUID() + ".mp4");
        final File out2 = dir.newFile(UUID.randomUUID() + ".mp4");
        final File out3 = dir.newFile(UUID.randomUUID() + ".mp4");

        ffmpeg.apply(input,cover,false,null,false,out);
        Assert.assertEquals(cover.length(),ffmpeg.getCoverArt(out,cover2).length());

        // test preserve
        ffmpeg.apply(out,null,true,null,false,out2);
        Assert.assertEquals(cover.length(),ffmpeg.getCoverArt(out2,cover3).length());

        // test remove
        ffmpeg.apply(out,null,false,null,false,out3);
        Assert.assertNull(ffmpeg.getCoverArt(out3,cover4));
    }

    @Test(expected = OutOfMemoryError.class)
    public void testOversizedCover() throws IOException{
        final File cover = new File("src/test/resources/format/apply/oversized.png");
        final File out = dir.newFile();

        ffmpeg.apply(input,cover,false,null,false,out);
    }

    @Test
    public void testApplyMetadata() throws IOException{
        final File out = dir.newFile(UUID.randomUUID() + ".mp4");
        final File out2 = dir.newFile(UUID.randomUUID() + ".mp4");
        final File out3 = dir.newFile(UUID.randomUUID() + ".mp4");
        final Map<String,String> metadata = Map.of("title","value","date",String.valueOf(UUID.randomUUID()));

        ffmpeg.apply(input,null,false,metadata,false,out);
        Assert.assertEquals(metadata.get("title"),ffmpeg.getMetadata(out).get("title"));
        Assert.assertEquals(metadata.get("date"),ffmpeg.getMetadata(out).get("date"));

        // test preserve
        ffmpeg.apply(out,null,false,null,true,out2);
        Assert.assertEquals(metadata.get("title"),ffmpeg.getMetadata(out2).get("title"));
        Assert.assertEquals(metadata.get("date"),ffmpeg.getMetadata(out2).get("date"));

        // test remove
        ffmpeg.apply(out,null,false,null,false,out3);
        Assert.assertNull(ffmpeg.getMetadata(out3).get("title"));
        Assert.assertNull(ffmpeg.getMetadata(out3).get("date"));
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    public void testApplyAll() throws IOException{
        final File out = dir.newFile(UUID.randomUUID() + ".mp4");
        final File out2 = dir.newFile(UUID.randomUUID() + ".mp4");
        final File out3 = dir.newFile(UUID.randomUUID() + ".mp4");
        final Map<String,String> metadata = Map.of("title","value","date",String.valueOf(UUID.randomUUID()));

        ffmpeg.apply(input,cover,false,metadata,false,out);
        Assert.assertNotEquals(0,ffmpeg.getCoverArt(out,cover2).length());
        Assert.assertEquals(metadata.get("title"),ffmpeg.getMetadata(out).get("title"));
        Assert.assertEquals(metadata.get("date"),ffmpeg.getMetadata(out).get("date"));

        // test preserve
        ffmpeg.apply(out,cover,false,metadata,false,out2);
        Assert.assertNotEquals(0,ffmpeg.getCoverArt(out2,cover3).length());
        Assert.assertEquals(metadata.get("title"),ffmpeg.getMetadata(out2).get("title"));
        Assert.assertEquals(metadata.get("date"),ffmpeg.getMetadata(out2).get("date"));

        // test remove
        ffmpeg.apply(out,null,false,null,false,out3);
        Assert.assertNull(ffmpeg.getCoverArt(out3,cover4));
        Assert.assertNull(ffmpeg.getMetadata(out3).get("title"));
        Assert.assertNull(ffmpeg.getMetadata(out3).get("date"));
    }

}
