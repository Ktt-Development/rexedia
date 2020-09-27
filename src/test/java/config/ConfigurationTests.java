package config;

import com.kttdevelopment.rexedia.config.Configuration;
import com.kttdevelopment.rexedia.preset.MetadataPreset;
import org.apache.commons.cli.*;
import org.junit.*;

import java.io.File;
import java.io.IOException;

import static com.kttdevelopment.rexedia.config.Configuration.*;

public class ConfigurationTests {

    @Test(expected = MissingOptionException.class)
    public void testMissingRequiredOptions() throws IOException, ParseException{
        new Configuration();
    }

    // input

    @Test(expected = MissingArgumentException.class)
    public void testMissingInputArg() throws IOException, ParseException{
        new Configuration("-input");
    }

    @Test
    public void testMultipleInputs() throws IOException, ParseException{
        Configuration config = new Configuration("-input","file1.mp4","file2.mp4","-meta","","","");
        File[] input = (File[]) config.getConfiguration().get(INPUT);
        Assert.assertEquals(new File("file1.mp4"), input[0]);
        Assert.assertEquals(new File("file2.mp4"),input[1]);

        config = new Configuration("-input","file1.mp4","-input","file2.mp4","-meta","","","");
        input = (File[]) config.getConfiguration().get(INPUT);
        Assert.assertEquals(new File("file1.mp4"), input[0]);
        Assert.assertEquals(new File("file2.mp4"),input[1]);
    }

    // meta

    @Test(expected = MissingOptionException.class)
    public void testMissingMeta() throws IOException, ParseException{
        new Configuration("-input", "file.mp4");
    }

    @Test
    public void testPresetSubstituteMeta() throws IOException, ParseException{
        new Configuration("-input", "file.mp4","-preset","src/test/resources/preset/preset.yml");
    }

    @Test(expected = MissingArgumentException.class)
    public void testMissingCoverArgs() throws IOException, ParseException{
        new Configuration("-input","file.mp4","-c");
    }

    @Test(expected = MissingArgumentException.class)
    public void testMissingMetaArgs() throws IOException, ParseException{
        new Configuration("-input","file.mp4","-m");
    }

    @Test
    public void testCoverArgs() throws IOException, ParseException{
        final Configuration config = new Configuration("-input","file.mp4","-c","$1","(.+)");

        Assert.assertEquals(new MetadataPreset(null,"$1","(.+)"),config.getPreset().getCoverPreset());
    }

    @Test
    public void testMetaArgs() throws IOException, ParseException{
        final Configuration config = new Configuration("-input","file.mp4","-m","name","$1","(.+)");

        Assert.assertEquals(new MetadataPreset("name","$1","(.+)"),config.getPreset().getPresets()[0]);
    }

    @Test
    public void testMultipleMeta() throws IOException, ParseException{
        Configuration config = new Configuration("-input","file.mp4","-m","name","$1","(.+)","name2","$1","(.+)");
        Assert.assertEquals(new MetadataPreset("name","$1","(.+)"),config.getPreset().getPresets()[0]);
        Assert.assertEquals(new MetadataPreset("name2","$1","(.+)"),config.getPreset().getPresets()[1]);

        config = new Configuration("-input","file.mp4","-m","name","$1","(.+)","-meta","name2","$1","(.+)");
        Assert.assertEquals(new MetadataPreset("name","$1","(.+)"),config.getPreset().getPresets()[0]);
        Assert.assertEquals(new MetadataPreset("name2","$1","(.+)"),config.getPreset().getPresets()[1]);
    }

    // remaining flags

    @Test
    public void testDefault() throws IOException, ParseException{
        final Configuration config = new Configuration("-input","file1.mp4","-meta","","","");
        final String[] args = {WALK,BACKUP,LOGGING,THREADS,PRECOV,PREMETA};

        for(final String arg : args)
             Assert.assertNotNull(config.getConfiguration().get(arg));
    }

        // test not needed for malformed (default is false)

    @Test
    public void testArgs() throws IOException, ParseException{
        final Configuration def = new Configuration("-input","file1.mp4","-meta","","","");
        final Configuration config = new Configuration("-input","file1.mp4","-meta","","","",
            '-'+WALK,"true",
            '-'+BACKUP,"true",
            '-'+LOGGING,"true",
            '-'+THREADS,"2",
            '-'+PRECOV,"false",
            '-'+PREMETA,"true"
        );

        final String[] args = {WALK,BACKUP,LOGGING,THREADS,PRECOV,PREMETA};

        for(final String arg : args)
             Assert.assertNotEquals(def.getConfiguration().get(arg),config.getConfiguration().get(arg));
    }

    @Test(expected = NumberFormatException.class)
    public void testMalformedThreads() throws IOException, ParseException{
        new Configuration("-input","file1.mp4","-meta","","","","-t","one");
    }

    @Test
    public void testNegativeThreads() throws IOException, ParseException{
        final Configuration config = new Configuration("-input","file1.mp4","-meta","","","","-t","-1");
        Assert.assertEquals(1,config.getConfiguration().get(THREADS));
    }

}
