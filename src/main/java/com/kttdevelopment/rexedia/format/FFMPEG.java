package com.kttdevelopment.rexedia.format;

import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import net.bramp.ffmpeg.probe.FFmpegProbeResult;

import java.io.File;
import java.io.IOException;

public final class FFMPEG {

    private final FFmpeg ffmpeg;
    private final FFprobe ffprobe;

    public FFMPEG(final String pathToFFMPEG, final String pathToFFPROBE) throws IOException{
        ffmpeg  = new FFmpeg(pathToFFMPEG);
        ffprobe = new FFprobe(pathToFFPROBE);
    }

    public final int getDuration(final File file) throws IOException{
        final FFmpegProbeResult result = ffprobe.probe(file.getAbsolutePath());
        if(result.hasError())
            throw new IOException(String.format("[%s] %s", result.error.code, result.error.string));
        return (int) (!result.hasError() ? result.getFormat().duration : -1f);
    }

    public final boolean verifyFileIntegrity(final File file){
        ffmpeg.builder()
            .addInput(file.getAbsolutePath())
            .setVerbosity(FFmpegBuilder.Verbosity.ERROR)
            .addExtraArgs("-f",null)
            .build();
        return false;
    }

}
