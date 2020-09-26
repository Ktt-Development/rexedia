package preset;

import com.kttdevelopment.rexedia.preset.*;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

public class PresetParserTests {

    @Test
    public void testFile() throws IOException{
        final Preset preset = new PresetParser().parse(new File("src/test/resources/preset/preset.yml"));

        Assert.assertEquals(new MetadataPreset(null,"$1","(.*)"),preset.getCoverPreset());
        Assert.assertEquals(new MetadataPreset("name","$1","(.*)"),preset.getPresets()[0]);
    }

    @Test
    public void testNoCover() throws IOException{
        final String yaml =
            "metadata:\n" +
            "  - meta: 'name'\n" +
            "    format: '$1'\n" +
            "    regex: '(.*)'";

        Assert.assertEquals(new MetadataPreset("name","$1","(.*)"),new PresetParser().parse(yaml).getPresets()[0]);
    }

    @Test
    public void testNoMeta() throws IOException{
        final String yaml =
            "cover:\n" +
            "  format: '$1'\n" +
            "  regex: '(.*)'";

        Assert.assertEquals(new MetadataPreset(null,"$1","(.*)"),new PresetParser().parse(yaml).getCoverPreset());
    }

    @Test
    public void testNone(){
        final Preset preset = new Preset.Builder().build();
        Assert.assertNull(preset.getCoverPreset());
        Assert.assertEquals(0,preset.getPresets().length);
    }

    @Test(expected = NullPointerException.class)
    public void testMissingCoverFormat() throws IOException{
        final String yaml =
            "cover:\n" +
            "  regex: '(.*)'";

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
    public void testMissingMetaName() throws IOException{
        final String yaml =
            "metadata:\n" +
            "  - format: '$1'\n" +
            "    regex: '(.*)'";
        new PresetParser().parse(yaml);
    }

    @Test(expected = NullPointerException.class)
    public void testMissingMetaFormat() throws IOException{
        final String yaml =
            "metadata:\n" +
            "  - meta: 'name'\n" +
            "    regex: '(.*)'";
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

}
