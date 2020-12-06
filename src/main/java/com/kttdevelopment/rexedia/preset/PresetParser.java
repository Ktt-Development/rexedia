package com.kttdevelopment.rexedia.preset;

import com.esotericsoftware.yamlbeans.YamlReader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

public final class PresetParser {

    public PresetParser(){ }

    public final Preset parse(final File file) throws ClassCastException, NullPointerException, IOException{
        return parse(Files.readString(file.toPath()));
    }

    public final Preset parse(final String yaml) throws ClassCastException, NullPointerException, IOException{
        final Preset.Builder preset = new Preset.Builder();
        final Map<String,Object> map = asStringMap((Map<?,?>) new YamlReader(yaml).read());

        // cover
        final Map<String,Object> cover = asStringMap((Map<?,?>) map.get(Preset.COVER));
        if(!cover.isEmpty())
            preset.setCoverPreset(new MetadataPreset(
                null,
                Objects.requireNonNull(cover.get(Preset.REGEX)).toString(),
                Objects.requireNonNull(cover.get(Preset.FORMAT)).toString()
            ));
        // meta
        final List<?> meta = (List<?>) map.get(Preset.METADATA);
        if(meta != null && !meta.isEmpty())
            for(final Object node : meta)
                preset.addPreset(parseMetadataPreset(asStringMap((Map<?,?>) node)));
        // output
        final Map<String,Object> output = asStringMap((Map<?,?>) map.get(Preset.OUTPUT));
        if(!output.isEmpty())
            preset.setOutputPreset(new MetadataPreset(
                null,
                Objects.requireNonNull(output.get(Preset.REGEX)).toString(),
                Objects.requireNonNull(output.get(Preset.FORMAT)).toString()
            ));

        return preset.build();
    }

    private MetadataPreset parseMetadataPreset(final Map<String,?> yaml) throws NullPointerException{
        return new MetadataPreset(
            Objects.requireNonNull(yaml.get(Preset.META)).toString(),
            Objects.requireNonNull(yaml.get(Preset.REGEX)).toString(),
            Objects.requireNonNull(yaml.get(Preset.FORMAT)).toString()
        );
    }

    private Map<String,Object> asStringMap(final Map<?,?> map){
        if(map == null || map.isEmpty()) return new HashMap<>();
        final Map<String,Object> OUT = new HashMap<>();
        for(final Map.Entry<?, ?> entry : map.entrySet())
            OUT.put(entry.getKey().toString(), entry.getValue());
        return OUT;
    }

}
