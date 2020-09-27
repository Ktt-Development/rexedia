package format;

import com.kttdevelopment.rexedia.format.FFMPEG;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import org.junit.*;

import java.io.*;
import java.util.Map;

@SuppressWarnings("SpellCheckingInspection")
public class FFMPEGtests {

    private static FFMPEG ffmpeg;

    @BeforeClass
    public static void initFFMPEG() throws IOException{
        ffmpeg = new FFMPEG("bin/ffmpeg.exe","bin/ffprobe.exe");
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

    // todo
    // each test should copy its own file to ensure clean tests

    @Test @Ignore
    public void testApplyNone() throws IOException{
        final File in = null, out = null;
        Assert.assertTrue(ffmpeg.apply(in,null,false,null,false,out));
        Assert.assertEquals(in.length(),out.length());
    }

    @Test @Ignore
    public void testApplyCover() throws IOException{
        final File in = null, cover = null, out = null;
        Assert.assertTrue(ffmpeg.apply(in,cover,false,null,false,out));
        Assert.assertEquals(cover.length(),getCoverArt(cover,new File(cover.getParentFile(),"cover2.png")).length());

        // test preserve
        Assert.assertTrue(ffmpeg.apply(in,null,true,null,false,out));
        Assert.assertEquals(cover.length(),getCoverArt(cover,new File(cover.getParentFile(),"cover2.png")).length());
    }

    @Test(expected = OutOfMemoryError.class) @Ignore
    public void testOversizedCover() throws IOException{
        final File in = null, cover = null, out = null;
        Assert.assertTrue(ffmpeg.apply(in,cover,false,null,false,out));
    }

    @Test @Ignore
    public void testApplyMetadata() throws IOException{
        final File in = null, out = null;
        final Map<String,String> metadata = Map.of();
        Assert.assertTrue(ffmpeg.apply(in,null,false,metadata,false,out));
        Assert.assertEquals(metadata,getMetadata(out));

        // test preserve
        Assert.assertTrue(ffmpeg.apply(in,null,false,null,true,out));
        Assert.assertEquals(metadata,getMetadata(out));
    }

    @Test @Ignore
    public void testApplyAll() throws IOException{
        final File in = null, cover = null, out = null;
        final Map<String,String> metadata = Map.of();
        Assert.assertTrue(ffmpeg.apply(in,cover,false,metadata,false,out));
        Assert.assertEquals(cover.length(),getCoverArt(cover,new File(cover.getParentFile(),"cover2.png")).length());
        Assert.assertEquals(metadata,getMetadata(out));

        // test preserve
        Assert.assertTrue(ffmpeg.apply(in,cover,false,metadata,false,out));
        Assert.assertEquals(cover.length(),getCoverArt(cover,new File(cover.getParentFile(),"cover2.png")).length());
        Assert.assertEquals(metadata,getMetadata(out));
    }

    //

    private Map<String,String> getMetadata(final File input) throws IOException{
        return ffmpeg.ffprobe.probe(input.getAbsolutePath()).getFormat().tags;
    }

    private File getCoverArt(final File input, final File output) throws IOException{
        final FFmpegBuilder builder = new FFmpegBuilder()
            .addInput(input.getAbsolutePath())
            .addExtraArgs("-an")
            .addOutput(output.getAbsolutePath())
                .setVideoCodec("copy")
                .done();
        ffmpeg.ffmpeg.run(builder);
        return output;
    }


}
