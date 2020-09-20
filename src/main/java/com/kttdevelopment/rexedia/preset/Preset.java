package com.kttdevelopment.rexedia.preset;

import com.kttdevelopment.core.classes.ToStringBuilder;

import java.util.ArrayList;
import java.util.List;

public abstract class Preset {

    public abstract MetadataPreset[] getPresets();

    Preset(){ }

    @Override
    public String toString(){
        return new ToStringBuilder(getClass().getSimpleName())
            .addObject("presets",getPresets())
            .toString();
    }

    public static class Builder{

        private final List<MetadataPreset> presets = new ArrayList<>();

        public Builder(){ }

        public final Builder addPreset(final MetadataPreset preset){
            presets.add(preset);
            return this;
        }

        public final Preset build(){
            return new Preset() {
                private final MetadataPreset[] presets = Builder.this.presets.toArray(new MetadataPreset[0]);

                @Override
                public MetadataPreset[] getPresets(){
                    return presets;
                }
            };
        }

    }

}
