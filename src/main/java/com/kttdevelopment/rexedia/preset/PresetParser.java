package com.kttdevelopment.rexedia.preset;

import com.amihaiemil.eoyaml.*;

import java.io.*;
import java.nio.file.Files;
import java.util.Objects;

public final class PresetParser {

    public PresetParser(){ }

    public final Preset parse(final File file) throws IOException{
        return parse(Files.readString(file.toPath()));
    }

    public final Preset parse(final String yaml) throws NullPointerException, IOException{
        final Preset.Builder preset = new Preset.Builder();
        final YamlMapping map = Yaml.createYamlInput(yaml).readYamlMapping();

        // cover
        final YamlMapping cover = map.yamlMapping(Preset.COVER);
        if(cover != null)
            preset.setCoverPreset(new MetadataPreset(null, Objects.requireNonNull(cover.string(Preset.REGEX)), Objects.requireNonNull(cover.string(Preset.FORMAT))));
        // meta
        final YamlSequence meta = map.yamlSequence(Preset.METADATA);
        if(meta != null)
            for(final YamlNode node : meta)
                preset.addPreset(parseMetadataPreset(node.asMapping()));
        // output
        final YamlMapping output = map.yamlMapping(Preset.OUTPUT);
        if(output != null)
            preset.setOutputPreset(new MetadataPreset(null, Objects.requireNonNull(output.string(Preset.REGEX)), Objects.requireNonNull(output.string(Preset.FORMAT))));

        return preset.build();
    }

    private MetadataPreset parseMetadataPreset(final YamlMapping yaml) throws NullPointerException{
        return new MetadataPreset(Objects.requireNonNull(yaml.string(Preset.META)), Objects.requireNonNull(yaml.string(Preset.REGEX)), Objects.requireNonNull(yaml.string(Preset.FORMAT)));
    }

}
