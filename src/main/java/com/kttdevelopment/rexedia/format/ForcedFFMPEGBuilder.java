package com.kttdevelopment.rexedia.format;

import net.bramp.ffmpeg.builder.FFmpegBuilder;

import java.util.Collections;
import java.util.List;

final class ForcedFFMPEGBuilder extends FFmpegBuilder {

    private final List<String> args;

    public ForcedFFMPEGBuilder(final List<String> args){
        this.args = args;
    }

    @Override
    public final List<String> build(){
        return Collections.unmodifiableList(args);
    }

}
