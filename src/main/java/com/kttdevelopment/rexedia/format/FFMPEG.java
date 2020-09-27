package com.kttdevelopment.rexedia.format;

import com.kttdevelopment.core.tests.exceptions.ExceptionUtil;
import net.bramp.ffmpeg.*;
import net.bramp.ffmpeg.job.FFmpegJob;
import net.bramp.ffmpeg.probe.FFmpegProbeResult;
import net.bramp.ffmpeg.progress.ProgressListener;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.*;

@SuppressWarnings("SpellCheckingInspection")
public final class FFMPEG {

    public final FFmpeg ffmpeg;
    public final FFprobe ffprobe;

    public FFMPEG(final String pathToFFMPEG, final String pathToFFPROBE) throws IOException{
        ffmpeg  = new FFmpeg(pathToFFMPEG);
        ffprobe = new FFprobe(pathToFFPROBE);
    }

// ffprobe

    // duration in seconds
    public final double getDuration(final File input) throws IOException{
        if(!input.exists()) throw new FileNotFoundException(input.getAbsolutePath());

        final FFmpegProbeResult result = ffprobe.probe(input.getAbsolutePath());
        if(result.hasError())
            throw new IOException(String.format("[%s] %s", result.error.code, result.error.string));
        return !result.hasError() ? result.getFormat().duration : -1;
    }

    public final boolean verifyFileIntegrity(final File input){
        /* the ffprobe wrapper was not made correctly, so this verified method can't be used
        final List<String> args = Arrays.asList(
            "-show_entries", "stream=r_frame_rate,nb_read_frames,duration",
            "-select_streams", "v",
            "-count_frames",
            "-of", "compact=p=1:nk=1",
            "-threads","3",
            "-v", "0",
            file.getAbsolutePath());
        ffprobe.run(args);
        */
        return ExceptionUtil.requireNonExceptionElse(() -> getDuration(input) != -1,false);
    }

// ffmpeg

    public final boolean apply(
        final File INPUT,
        final File cover, final boolean preserveCover,
        final Map<String,String> metadata, final boolean preserveMeta,
        final File OUT) throws IOException{
        return apply(INPUT,cover,preserveCover,metadata,preserveMeta,OUT,null);
    }

    public final boolean apply(
        final File INPUT,
        final File cover, final boolean preserveCover,
        final Map<String,String> metadata, final boolean preserveMeta,
        final File OUT,
        final ProgressListener listener) throws IOException{


        if(INPUT == null)
            throw new FileNotFoundException();
        else if(!INPUT.exists())
            throw new FileNotFoundException(INPUT.getAbsolutePath());
        else if(OUT == null)
            throw new FileNotFoundException();
        else if(!OUT.getParentFile().exists() && !OUT.getParentFile().mkdirs())
            throw new FileNotFoundException(OUT.getParentFile().getAbsolutePath());
        else if(cover != null && cover.exists() && cover.length() > 1e+7)
            throw new OutOfMemoryError("Cover art files exceeding 10MB will corrupt video");

        if((cover == null || !cover.exists()) && (metadata == null || metadata.isEmpty())){ // skip if no changes
            Files.copy(INPUT.toPath(), OUT.toPath(), StandardCopyOption.REPLACE_EXISTING);
            return true;
        }

        final List<String> args = new ArrayList<>();
        args.add("-i");
            args.add('"' + INPUT.getAbsolutePath().replace('\\','/') + '"');
        args.add("-y"); // override ? "-y" : "-n"

        if(cover != null && cover.exists()){
            args.add("-i");
                args.add('"' + cover.getAbsolutePath().replace('\\','/') + '"');
            args.add("-c");
                args.add("copy");
            args.add("-map");
                args.add("0");
            args.add("-map");
                args.add("1");
        }else if((cover == null || !cover.exists()) && !preserveCover){
            args.add("-c");
                args.add("copy");
            args.add("-map");
                args.add("0");
            args.add("-map");
                args.add("-0:v");
        }

        args.add("-acodec");
            args.add("copy");
        args.add("-vcodec");
            args.add("copy");

        if(!preserveMeta)
            args.add("-map_metadata");
                args.add("-1");
        if(metadata != null && !metadata.isEmpty())
            metadata.forEach((k,v) -> {
                args.add("-metadata");
                    args.add(String.format("\"%s\"=\"%s\"",k,v));
            });

        args.add('"' + OUT.getAbsolutePath().replace('\\','/') + '"');

        System.out.println(String.join(" ",args));

        // ffmpeg wrapper does not add extra arguments in correct order
        final FFmpegJob job = new FFmpegExecutor(ffmpeg,ffprobe).createJob(new ForcedFFMPEGBuilder(args), listener);
        job.run();

        switch(job.getState()){
            case FINISHED:
                return true;
            default:
            case RUNNING:
            case WAITING:
            case FAILED:
                return false;
        }
    }

}
