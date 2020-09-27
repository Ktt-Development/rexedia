package format;

import com.kttdevelopment.rexedia.format.FFMPEG;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import org.junit.*;

import java.io.*;
import java.nio.file.Files;
import java.util.Map;

@SuppressWarnings("SpellCheckingInspection")
public class ffmpegTests {

    private static FFMPEG ffmpeg;

    @BeforeClass
    public static void initFFMPEG() throws IOException{
        ffmpeg = new FFMPEG("bin/ffmpeg.exe","bin/ffprobe.exe");
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
        out.deleteOnExit();

        Assert.assertTrue(ffmpeg.apply(input,cover,false,null,false,out));
        Assert.assertEquals(cover.length(),getCoverArt(out,new File(cover.getParentFile(),"cover2.png")).length());

        // test preserve
        Assert.assertTrue(ffmpeg.apply(input,null,true,null,false,out));
        Assert.assertEquals(cover.length(),getCoverArt(out,new File(cover.getParentFile(),"cover2.png")).length());
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
        out.deleteOnExit();;
        final Map<String,String> metadata = Map.of("key","value","now",String.valueOf(System.currentTimeMillis()));

        Assert.assertTrue(ffmpeg.apply(input,null,false,metadata,false,out));
        Assert.assertEquals(metadata,getMetadata(out));

        // test preserve
        Assert.assertTrue(ffmpeg.apply(input,null,false,null,true,out));
        Assert.assertEquals(metadata,getMetadata(out));
    }

    @Test
    public void testApplyAll() throws IOException{
        final File out = new File(input.getParentFile(),System.currentTimeMillis() + ".mp4");
        out.deleteOnExit();
        final Map<String,String> metadata = Map.of("key","value","now",String.valueOf(System.currentTimeMillis()));

        Assert.assertTrue(ffmpeg.apply(input,cover,false,metadata,false,out));
        Assert.assertEquals(cover.length(),getCoverArt(out,new File(cover.getParentFile(),"cover2.png")).length());
        Assert.assertEquals(metadata,getMetadata(out));

        // test preserve
        Assert.assertTrue(ffmpeg.apply(input,cover,false,metadata,false,out));
        Assert.assertEquals(cover.length(),getCoverArt(out,new File(cover.getParentFile(),"cover2.png")).length());
        Assert.assertEquals(metadata,getMetadata(out));
    }

    //

    private Map<String,String> getMetadata(final File input) throws IOException{
        return ffmpeg.ffprobe.probe(input.getAbsolutePath()).getFormat().tags;
    }

    private File getCoverArt(final File input, final File output) throws IOException{
        final FFmpegBuilder builder = new FFmpegBuilder()
            .setInput(input.getAbsolutePath())
            .addExtraArgs("-an")
            .addOutput(output.getAbsolutePath())
                .setVideoCodec("copy")
                .done();
        ffmpeg.ffmpeg.run(builder);
        return output;
    }

}
