package config;

import com.kttdevelopment.rexedia.config.Configuration;
import org.apache.commons.cli.*;
import org.junit.*;

import java.io.IOException;

public class ConfigurationTests {

    @Test(expected = MissingOptionException.class)
    public void testMissing() throws IOException, ParseException{
        new Configuration();
    }

    @Test(expected = MissingArgumentException.class)
    public void testMissingInputArg() throws IOException, ParseException{
        new Configuration("-input");
    }

    @Test(expected = MissingOptionException.class)
    public void testMissingMeta() throws IOException, ParseException{
        new Configuration("-input", "file.mp4");
    }

    @Test
    public void testPresetSubstituteMeta() throws IOException, ParseException{
        new Configuration("-input", "file.mp4","-preset","src/test/resources/preset/preset.yml");
    }

    @Test @Ignore
    public void testMultipleInputs() throws IOException, ParseException{
        final Configuration config = new Configuration("-input","file1.mp4","file2.mp4","-meta","","","");

    }

    @Test @Ignore
    public void testMetaArgs() throws IOException, ParseException{
        final Configuration config = new Configuration("-input","file.mp4");
    }

}
