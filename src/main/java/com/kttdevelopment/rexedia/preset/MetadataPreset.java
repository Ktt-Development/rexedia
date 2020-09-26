package com.kttdevelopment.rexedia.preset;

import com.kttdevelopment.core.classes.ToStringBuilder;

import java.util.regex.Pattern;

public final class MetadataPreset {

    private final String metadata_key, format;
    private final Pattern regex;

    public MetadataPreset(final String key, final String format, final String regex){
        this.metadata_key = key;
        this.format       = format;
        this.regex        = Pattern.compile(regex);
    }

    public final String format(final String string){
        return regex.matcher(string).replaceAll(format);
    }

    @Override
    public String toString(){
        return new ToStringBuilder(getClass().getSimpleName())
            .addObject("key",metadata_key)
            .addObject("format",format)
            .addObject("regex",regex.pattern())
            .toString();
    }

}
