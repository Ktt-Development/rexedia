package com.kttdevelopment.rexedia.config;

import com.kttdevelopment.rexedia.preset.MetadataPreset;
import org.apache.commons.cli.*;
import org.junit.*;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

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

    @Rule
    public final TemporaryFolder dir = new TemporaryFolder(new File("."));

    @Test
    public void testPresetSubstituteMeta() throws IOException, ParseException{
        final String yml =
            "cover:\n" +
            "  regex: \"(.+)\"\n" +
            "  format: \"$1\"\n" +
            "metadata:\n" +
            "  - meta: \"name\"\n" +
            "    regex: \"(.+)\"\n" +
            "    format: \"$1\"\n" +
            "output:\n" +
            "  regex: \"(.+)\"\n" +
            "  format: \"$1\"";

        final File presetFile = dir.newFile();
        Files.write(presetFile.toPath(), yml.getBytes());

        new Configuration("-input", "file.mp4","-preset",'"' + presetFile.getAbsolutePath() + '"');
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
        final Configuration config = new Configuration("-input","file.mp4","-c","(.+)","$1");

        Assert.assertEquals(new MetadataPreset(null,"(.+)","$1"),config.getPreset().getCoverPreset());
    }

    @Test
    public void testMetaArgs() throws IOException, ParseException{
        final Configuration config = new Configuration("-input","file.mp4","-m","name","(.+)","$1");

        Assert.assertEquals(new MetadataPreset("name","(.+)","$1"),config.getPreset().getPresets()[0]);
    }

    @Test
    public void testMultipleMeta() throws IOException, ParseException{
        Configuration config = new Configuration("-input","file.mp4","-m","name","(.+)","$1","name2","(.+)","$1");
        Assert.assertEquals(new MetadataPreset("name","(.+)","$1"),config.getPreset().getPresets()[0]);
        Assert.assertEquals(new MetadataPreset("name2","(.+)","$1"),config.getPreset().getPresets()[1]);

        config = new Configuration("-input","file.mp4","-m","name","(.+)","$1","-meta","name2","(.+)","$1");
        Assert.assertEquals(new MetadataPreset("name","(.+)","$1"),config.getPreset().getPresets()[0]);
        Assert.assertEquals(new MetadataPreset("name2","(.+)","$1"),config.getPreset().getPresets()[1]);
    }

    @Test
    public void testOutputArgs() throws IOException, ParseException{
        final Configuration config = new Configuration("-input","file.mp4","-m","name","(.+)","$1","-o","(.+)","$1");

        Assert.assertEquals(new MetadataPreset(null,"(.+)","$1"),config.getPreset().getOutputPreset());
    }

    // remaining flags

    @Test
    public void testDefault() throws IOException, ParseException{
        final Configuration config = new Configuration("-input","file.mp4","-m","name","(.+)","$1","-o","$1","");
        final String[] args = {WALK,BACKUP,LOGGING,PRECOV,PREMETA};

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
            '-'+PRECOV,"false",
            '-'+PREMETA,"true"
        );

        final String[] args = {WALK,BACKUP,LOGGING,PRECOV,PREMETA};

        for(final String arg : args)
             Assert.assertNotEquals(def.getConfiguration().get(arg),config.getConfiguration().get(arg));
    }

}
