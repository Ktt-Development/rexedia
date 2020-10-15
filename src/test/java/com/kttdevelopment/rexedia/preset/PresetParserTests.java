package com.kttdevelopment.rexedia.preset;

import org.junit.*;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class PresetParserTests {

    @Rule
    public final TemporaryFolder dir = new TemporaryFolder(new File("."));

    @Test
    public void testFile() throws IOException{
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
        Files.write(presetFile.toPath(),yml.getBytes());

        final Preset preset = new PresetParser().parse(presetFile);
        Assert.assertEquals(new MetadataPreset(null,"(.+)","$1"),preset.getCoverPreset());
        Assert.assertEquals(new MetadataPreset("name","(.+)","$1"),preset.getPresets()[0]);
        Assert.assertEquals(new MetadataPreset(null,"(.+)","$1"),preset.getOutputPreset());
    }

    @Test
    public void testNoCover() throws IOException{
        final String yaml =
            "metadata:\n" +
            "  - meta: 'name'\n" +
            "    format: '$1'\n" +
            "    regex: '(.+)'\n" +
            "output:\n" +
            "  format: '$1'\n" +
            "  regex: '(.+)'";

        final Preset preset = new PresetParser().parse(yaml);
        Assert.assertNull(preset.getCoverPreset());
        Assert.assertEquals(new MetadataPreset("name","(.+)","$1"),preset.getPresets()[0]);
        Assert.assertEquals(new MetadataPreset(null,"(.+)","$1"),preset.getOutputPreset());
    }

    @Test
    public void testNoMeta() throws IOException{
        final String yaml =
            "cover:\n" +
            "  format: '$1'\n" +
            "  regex: '(.+)'\n" +
            "output:\n" +
            "  format: '$1'\n" +
            "  regex: '(.+)'";

        final Preset preset = new PresetParser().parse(yaml);
        Assert.assertEquals(new MetadataPreset(null,"(.+)","$1"),new PresetParser().parse(yaml).getCoverPreset());
        Assert.assertEquals(0,preset.getPresets().length);
        Assert.assertEquals(new MetadataPreset(null,"(.+)","$1"),preset.getOutputPreset());
    }

    @Test
    public void testNone(){
        final Preset preset = new Preset.Builder().build();
        Assert.assertNull(preset.getCoverPreset());
        Assert.assertEquals(0,preset.getPresets().length);
        Assert.assertNull(preset.getOutputPreset());
    }

    @Test(expected = NullPointerException.class)
    public void testMissingCoverFormat() throws IOException{
        final String yaml =
            "cover:\n" +
            "  regex: '(.+)'";

        new PresetParser().parse(yaml).getCoverPreset();
    }

    @Test(expected = NullPointerException.class)
    public void testMissingCoverRegex() throws IOException{
        final String yaml =
            "cover:\n" +
            "  format: '$1'";

        new PresetParser().parse(yaml);
    }

        @Test(expected = NullPointerException.class)
    public void testMissingOutputFormat() throws IOException{
        final String yaml =
            "output:\n" +
            "  regex: '(.+)'";

        new PresetParser().parse(yaml).getOutputPreset();
    }

    @Test(expected = NullPointerException.class)
    public void testMissingOutputRegex() throws IOException{
        final String yaml =
            "output:\n" +
            "  format: '$1'";

        new PresetParser().parse(yaml);
    }

    @Test(expected = NullPointerException.class)
    public void testMissingMetaName() throws IOException{
        final String yaml =
            "metadata:\n" +
            "  - format: '$1'\n" +
            "    regex: '(.+)'";
        new PresetParser().parse(yaml);
    }

    @Test(expected = NullPointerException.class)
    public void testMissingMetaFormat() throws IOException{
        final String yaml =
            "metadata:\n" +
            "  - meta: 'name'\n" +
            "    regex: '(.+)'";
        new PresetParser().parse(yaml);
    }

    @Test(expected = NullPointerException.class)
    public void testMissingMetaRegex() throws IOException{
        final String yaml =
            "metadata:\n" +
            "  - meta: 'name'\n" +
            "    format: '$1'";
        new PresetParser().parse(yaml);
    }

    @Test
    public void testFormat() throws IOException{
        final String yaml =
            "metadata:\n" +
            "  - meta: 'name'\n" +
            "    format: '[$1]'\n" +
            "    regex: '(.+)'";

        Assert.assertEquals("[format]",new PresetParser().parse(yaml).getPresets()[0].format("format"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEscapeChar() throws IOException{
        final String yaml =
            "metadata:\n" +
            "  - meta: 'name'\n" +
            "    format: '\\$1\\'\n" +
            "    regex: '(.+)'";

        Assert.assertNotEquals("\\format\\",new PresetParser().parse(yaml).getPresets()[0].format("format"));
    }

    @Test
    public void testEscapedChar() throws IOException{
        final String yaml =
            "metadata:\n" +
            "  - meta: 'name'\n" +
            "    format: '\\\\$1\\\\'\n" +
            "    regex: '(.+)'";

        Assert.assertEquals("\\format\\",new PresetParser().parse(yaml).getPresets()[0].format("format"));
    }

}
