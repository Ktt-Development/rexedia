package com.kttdevelopment.rexedia.preset;

import com.kttdevelopment.rexedia.utility.ToStringBuilder;

import java.util.*;

public abstract class Preset {

    @SuppressWarnings({"unused", "RedundantSuppression"})
    public static final String
        COVER       = "cover",
        METADATA    = "metadata",
        META        = "meta",
        FORMAT      = "format",
        REGEX       = "regex",
        OUTPUT      = "output";

    //

    public abstract MetadataPreset getCoverPreset();
    public abstract MetadataPreset[] getPresets();
    public abstract MetadataPreset getOutputPreset();

    Preset(){ }

    @Override
    public boolean equals(final Object o){
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        final Preset that = (Preset) o;
        return Objects.equals(getCoverPreset(),that.getCoverPreset()) &&
               Arrays.equals(getPresets(), that.getPresets());
    }

    @Override
    public String toString(){
        return new ToStringBuilder(getClass().getSimpleName())
            .addObject("coverPreset", getCoverPreset())
            .addObject("presets", getPresets())
            .addObject("outputPreset", getOutputPreset())
            .toString();
    }

    @SuppressWarnings("UnusedReturnValue")
    public static class Builder{

        private MetadataPreset coverPreset = null;
        private final List<MetadataPreset> presets = new ArrayList<>();
        private MetadataPreset outputPreset = null;

        public Builder(){ }

        public final Builder setCoverPreset(final MetadataPreset coverPreset){
            this.coverPreset = coverPreset;
            return this;
        }

        public final Builder addPreset(final MetadataPreset preset){
            presets.add(preset);
            return this;
        }

        public final Builder setOutputPreset(final MetadataPreset outputPreset){
            this.outputPreset = outputPreset;
            return this;
        }

        public final Preset build(){
            return new Preset() {
                private final MetadataPreset coverPreset = Builder.this.coverPreset;
                private final MetadataPreset[] presets = Builder.this.presets.toArray(new MetadataPreset[0]);
                private final MetadataPreset outputPreset = Builder.this.outputPreset;

                @Override
                public final MetadataPreset getCoverPreset(){
                    return coverPreset;
                }

                @Override
                public final MetadataPreset[] getPresets(){
                    return presets;
                }

                @Override
                public final MetadataPreset getOutputPreset(){
                    return outputPreset;
                }
            };
        }

    }

}
