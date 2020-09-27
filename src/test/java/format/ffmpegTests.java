package format;

import com.kttdevelopment.rexedia.format.FFMPEG;
import com.kttdevelopment.rexedia.format.ForcedFFMPEGBuilder;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import org.junit.*;

import java.io.*;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;

@SuppressWarnings("SpellCheckingInspection")
public class ffmpegTests {

    private static FFMPEG ffmpeg;

    @BeforeClass
    public static void initFFMPEG() throws IOException{
        try{
            ffmpeg = new FFMPEG();
        }catch(IOException ignored){
            ffmpeg = new FFMPEG("bin/ffmpeg.exe", "bin/ffprobe.exe");
        }
        Files.copy(new File("src/test/resources/format/video.mp4").toPath(),input.toPath());
        input.deleteOnExit();
        new File(cover.getParentFile(),"cover2.png").deleteOnExit();
    }

    @Test
    public void testDuration() throws IOException{
        Assert.assertEquals(4.97,ffmpeg.getDuration(new File("src/test/resources/format/video.mp4")),0);
    }

    @Test
    public void testVerify(){
        Assert.assertTrue(ffmpeg.verifyFileIntegrity(new File("src/test/resources/format/video.mp4")));
        Assert.assertFalse(ffmpeg.verifyFileIntegrity(new File("src/test/resources/format/corrupt.mp4")));
    }

    private static final File input = new File("src/test/resources/format/apply/clean.mp4");
    private static final File cover = new File("src/test/resources/format/apply/cover.png");

    @Test
    public void missingArgs(){
        Assert.assertThrows(FileNotFoundException.class, () -> ffmpeg.apply(null, null, false, null, false, null));
        Assert.assertThrows(FileNotFoundException.class, () -> ffmpeg.apply(new File("null"), null, false, null, false, null));
        Assert.assertThrows(FileNotFoundException.class, () -> ffmpeg.apply(input, null, false, null, false, null));
    }

    @Test
    public void testApplyNone() throws IOException{
        final File out = new File(input.getParentFile(),System.currentTimeMillis() + ".mp4");
        out.deleteOnExit();

        Assert.assertTrue(ffmpeg.apply(input,null,false,null,false,out));
        Assert.assertEquals(input.length(),out.length());
    }

    @Test
    public void testApplyCover() throws IOException{
        final File out = new File(input.getParentFile(),System.currentTimeMillis() + ".mp4");
        //out.deleteOnExit();

        Assert.assertTrue(ffmpeg.apply(input,cover,false,null,false,out));
        System.exit(100);
        Assert.assertEquals(cover.length(),getCoverArt(out,new File(cover.getParentFile(),"cover2.png")).length());

        // test preserve
        Assert.assertTrue(ffmpeg.apply(input,null,true,null,false,out));
        Assert.assertEquals(cover.length(),getCoverArt(out,new File(cover.getParentFile(),"cover2.png")).length());

        // test remove
        Assert.assertTrue(ffmpeg.apply(input,null,false,null,false,out));
        // todo
    }

    @Test(expected = OutOfMemoryError.class)
    public void testOversizedCover() throws IOException{
        final File cover = new File("src/test/resources/format/apply/oversized.png");
        final File out = new File(input.getParentFile(),System.currentTimeMillis() + ".mp4");
        out.deleteOnExit();

        Assert.assertTrue(ffmpeg.apply(input,cover,false,null,false,out));
    }

    @Test
    public void testApplyMetadata() throws IOException{
        final File out = new File(input.getParentFile(),System.currentTimeMillis() + ".mp4");
        out.deleteOnExit();
        final Map<String,String> metadata = Map.of("title","value","date",String.valueOf(System.currentTimeMillis()));

        Assert.assertTrue(ffmpeg.apply(input,null,false,metadata,false,out));
        Assert.assertEquals(metadata.get("title"),getMetadata(out).get("title"));
        Assert.assertEquals(metadata.get("date"),getMetadata(out).get("date"));

        // test preserve
        final File out2 = new File(input.getParentFile(),System.currentTimeMillis() + ".mp4");
        out2.deleteOnExit();

        Assert.assertTrue(ffmpeg.apply(out,null,false,null,true,out2));
        Assert.assertEquals(metadata.get("title"),getMetadata(out2).get("title"));
        Assert.assertEquals(metadata.get("date"),getMetadata(out2).get("date"));

        // test remove
        final File out3 = new File(input.getParentFile(),System.currentTimeMillis() + ".mp4");
        out3.deleteOnExit();

        Assert.assertTrue(ffmpeg.apply(out,null,false,null,false,out3));
        Assert.assertNull(getMetadata(out3).get("title"));
        Assert.assertNull(getMetadata(out3).get("date"));
    }

    @Test
    public void testApplyAll() throws IOException{
        final File out = new File(input.getParentFile(),System.currentTimeMillis() + ".mp4");
        out.deleteOnExit();
        final Map<String,String> metadata = Map.of("title","value","date",String.valueOf(System.currentTimeMillis()));

        Assert.assertTrue(ffmpeg.apply(input,cover,false,metadata,false,out));
        Assert.assertEquals(cover.length(),getCoverArt(out,new File(cover.getParentFile(),"cover2.png")).length());
        Assert.assertEquals(metadata.get("title"),getMetadata(out).get("title"));
        Assert.assertEquals(metadata.get("date"),getMetadata(out).get("date"));

        // test preserve
        final File out2 = new File(input.getParentFile(),System.currentTimeMillis() + ".mp4");
        out2.deleteOnExit();

        Assert.assertTrue(ffmpeg.apply(out,cover,false,metadata,false,out2));
        Assert.assertEquals(cover.length(),getCoverArt(out2,new File(cover.getParentFile(),"cover2.png")).length());
        Assert.assertEquals(metadata.get("title"),getMetadata(out2).get("title"));
        Assert.assertEquals(metadata.get("date"),getMetadata(out2).get("date"));
    }

    //

    private Map<String,String> getMetadata(final File input) throws IOException{
        return ffmpeg.ffprobe.probe(input.getAbsolutePath()).format.tags;
    }

    // fixme
    private File getCoverArt(final File input, final File output) throws IOException{
        final List<String> args = List.of(
            "-i", input.getAbsolutePath(),
            "-map","0:v",
            "-map","0:V",
            "-c","copy",
            output.getAbsolutePath()
        );

        ffmpeg.ffmpeg.run(new ForcedFFMPEGBuilder(args));
        return output;
    }

}
