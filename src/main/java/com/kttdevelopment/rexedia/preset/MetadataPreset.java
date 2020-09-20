package com.kttdevelopment.rexedia.preset;

import com.kttdevelopment.core.classes.ToStringBuilder;

import java.util.*;

public final class MetadataPreset {

    private final String key, format;
    private final String[] regex;

    public MetadataPreset(final String key){
        this(key,"%s",new String[]{"(.*)"});
    }

    public MetadataPreset(final String key, final String format){
        this(key,format,new String[]{"(.*)"});
    }

    public MetadataPreset(final String key, final String format, final String[] regex){
        this.key    = key;
        this.format = format;
        this.regex  = regex;
    }

    public final String getKey(){
        return key;
    }

    public final String getFormat(){
        return format;
    }

    public final String[] getRegex(){
        return regex;
    }

    @Override
    public String toString(){
        return new ToStringBuilder(getClass().getSimpleName())
            .addObject("key",key)
            .addObject("format",format)
            .addObject("regex",regex)
            .toString();
    }

    //

    public static class Builder{

        private final String key;
        private String format;
        private final List<String> regex = new ArrayList<>();

        public Builder(final String key){
            this.key    = key;
            this.format = "%s";
        }

        public Builder(final String key, final String format){
            this.key    = key;
            this.format = format;
        }

        public final Builder setStringFormat(final String format){
            this.format = format;
            return this;
        }

        public final Builder addRegex(final String regex){
            this.regex.add(regex);
            return this;
        }

        public final MetadataPreset build(){
            return new MetadataPreset(key,format,regex.toArray(new String[0]));
        }

    }

}
