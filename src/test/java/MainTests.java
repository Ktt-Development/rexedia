import com.kttdevelopment.rexedia.Main;
import org.junit.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.Objects;
import java.util.regex.Pattern;

public class MainTests {

    private static final File corrupt = new File("src/test/resources/main/corrupt.mp4");
    private static final File video   = new File("src/test/resources/main/video.mp4");

    @BeforeClass
    public static void setup() throws IOException{
        Files.copy(
            Paths.get("src/test/resources/format/corrupt.mp4"),
            corrupt.toPath(),
            StandardCopyOption.REPLACE_EXISTING
        );
        Files.copy(
            Paths.get("src/test/resources/format/video.mp4"),
            video.toPath(),
            StandardCopyOption.REPLACE_EXISTING
        );
    }

    @Test @Ignore // todo: note that config runs before logging (corrupt can't be used)
    public void testLogging(){
        final File directory = video.getParentFile();
        final String input = video.getPath();
        // test none
        Main.main(new String[]{
            "-i", '"' + input + '"',
            "-m", "'(.+)'", "'$1'",
            "-o", "(.+)", "'$1.avi'"
        });

        // test logging

        // test debug
    }

    private boolean fileExists(final File directory, final String regex){
        final Pattern pattern = Pattern.compile(regex);
        for(final File file : Objects.requireNonNullElse(directory.listFiles(),new File[0]))
            if(pattern.matcher(file.getName()).matches())
                return false;
        return true;
    }

    @Test @Ignore
    public void testInput(){
        // test corrupt

        // test no walk
            // ffmpeg verify tests
        // test walk
            // ffmpeg verify tests
    }

    @Test @Ignore
    public void testBackup(){

    }

    @Test @Ignore
    public void testPresetAsArgs(){

    }

    @Test @Ignore
    public void testPresetOverrideArgs(){

    }

}
