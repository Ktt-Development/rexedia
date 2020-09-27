package format;

import com.kttdevelopment.rexedia.format.FFMPEG;
import org.junit.*;

import java.io.File;
import java.io.IOException;

@SuppressWarnings("SpellCheckingInspection")
public class FFMPEGtests {

    private static FFMPEG ffmpeg;

    @BeforeClass
    public static void initFFMPEG() throws IOException{
        ffmpeg = new FFMPEG("bin/ffmpeg.exe","bin/ffprobe.exe");
    }

    @Test @Ignore
    public void verify() throws IOException{
        ffmpeg.verifyFileIntegrity(new File("README.md"));
    }

}
