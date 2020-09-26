package com.kttdevelopment.rexedia.preset;

import com.kttdevelopment.core.classes.ToStringBuilder;

import java.util.ArrayList;
import java.util.List;

public abstract class Preset {

    @SuppressWarnings({"unused", "RedundantSuppression"})
    public static final String
        COVER       = "cover",
        METADATA    = "metadata",
        META        = "meta",
        FORMAT      = "format",
        REGEX       = "regex";

    //

    public abstract MetadataPreset getCoverPreset();
    public abstract MetadataPreset[] getPresets();

    Preset(){ }

    @Override
    public String toString(){
        return new ToStringBuilder(getClass().getSimpleName())
            .addObject("coverPreset", getCoverPreset())
            .addObject("presets", getPresets())
            .toString();
    }

    @SuppressWarnings("UnusedReturnValue")
    public static class Builder{

        private MetadataPreset coverPreset = null;
        private final List<MetadataPreset> presets = new ArrayList<>();

        public Builder(){ }

        public final Builder setCoverPreset(final MetadataPreset coverPreset){
            this.coverPreset = coverPreset;
            return this;
        }

        public final Builder addPreset(final MetadataPreset preset){
            presets.add(preset);
            return this;
        }

        public final Preset build(){
            return new Preset() {
                private final MetadataPreset coverPreset = Builder.this.coverPreset;
                private final MetadataPreset[] presets = Builder.this.presets.toArray(new MetadataPreset[0]);

                @Override
                public final MetadataPreset getCoverPreset(){
                    return coverPreset;
                }

                @Override
                public final MetadataPreset[] getPresets(){
                    return presets;
                }

            };
        }

    }

}
