package com.kttdevelopment.rexedia.preset;

import com.amihaiemil.eoyaml.*;

import java.io.*;
import java.util.Objects;

public final class PresetParser {

    public PresetParser(){ }

    public final Preset parse(final File file) throws IOException, NullPointerException{
        final Preset.Builder preset = new Preset.Builder();
        final YamlMapping yaml = Yaml.createYamlInput(file).readYamlMapping();

        preset.setCoverPreset(parseMetadataPreset(yaml.yamlMapping(Preset.COVER)));
        for(final YamlNode node : yaml.yamlSequence(Preset.METADATA))
            preset.addPreset(parseMetadataPreset(node.asMapping()));

        return preset.build();
    }

    private MetadataPreset parseMetadataPreset(final YamlMapping yaml) throws NullPointerException{
        return new MetadataPreset(Objects.requireNonNull(yaml.string(Preset.META)), Objects.requireNonNull(yaml.string(Preset.FORMAT)), Objects.requireNonNull(yaml.string(Preset.REGEX)));
    }

}
