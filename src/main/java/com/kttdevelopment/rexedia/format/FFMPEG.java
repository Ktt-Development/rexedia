package com.kttdevelopment.rexedia.format;

import com.kttdevelopment.core.classes.ToStringBuilder;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("SpellCheckingInspection")
public final class FFMPEG {

    private final FFMPEGExecutor executor;

    public FFMPEG(){
        executor = new FFMPEGExecutor("ffmpeg","ffprobe"); // local installation
    }

    public FFMPEG(final String pathToFFMPEG, final String pathToFFPROBE){
        executor = new FFMPEGExecutor(pathToFFMPEG,pathToFFPROBE);
    }

    public final boolean isValidInstallation(){
        try{
            return !executor.executeFFMPEG(new String[]{"-version"}).contains("is not recognized as an internal or external command,\noperable program or batch file.") && !executor.executeFFPROBE(new String[]{"-version"}).contains("is not recognized as an internal or external command,\noperable program or batch file.");
        }catch(final IOException ignored){
            return false;
        }
    }

// ffprobe

    private final Pattern duration = Pattern.compile("\\Q[FORMAT]\\E\\n\\Qduration=\\E(\\d+\\.\\d+)\\n\\Q[/FORMAT]\\E",Pattern.MULTILINE);
    // duration in seconds
    public final float getDuration(final File input) throws IOException{
        if(!input.exists()) throw new FileNotFoundException(input.getAbsolutePath());

        final String[] args = new String[]{
            "-i", '"' + input.getAbsolutePath() + '"',
            "-v", "0",
            "-show_entries", "format=duration"
        };

        final String result   = executor.executeFFPROBE(args);
        final Matcher matcher = duration.matcher(result);

        return result.isBlank() || matcher.matches()
               ? (float) (Math.ceil(Float.parseFloat(matcher.group(1)) * 100) / 100)
               : -1f;
    }

    private final Pattern frames = Pattern.compile("\\Qstream|\\E(\\d+)/(\\d+)\\|(\\d+\\.\\d+)\\|(\\d*)",Pattern.MULTILINE);
    public final boolean verifyFileIntegrity(final File input){
        if(!input.exists()) return false;

        final String[] args = new String[]{
            "-i", '"' + input.getAbsolutePath() + '"',
            "-select_streams", "v",
            "-v", "0",
            "-show_entries", "stream=r_frame_rate,nb_read_frames,duration",
            "-count_frames",
            "-of","compact=p=1:nk=1",
        };

        try{
            final String result   = executor.executeFFPROBE(args);
            final Matcher matcher = frames.matcher(result);

            if(!matcher.matches())
                return false;

            final int framerate     = Integer.parseInt(matcher.group(1)) / Integer.parseInt(matcher.group(2));
            final float duration    = Float.parseFloat(matcher.group(3));
            final int frames        = Integer.parseInt(matcher.group(4));

            // return framerate * duration (calculated frame rate)
            return framerate * duration == frames;
        }catch(final IOException | NumberFormatException ignored){
            return false;
        }
    }

    final Pattern metadata = Pattern.compile("^\\QTAG:\\E(.+)=(.+)$",Pattern.MULTILINE);
    public final Map<String,String> getMetadata(final File input){
        if(!input.exists()) return Collections.emptyMap();

        final String[] args = new String[]{
            "-i", '"' + input.getAbsolutePath() + '"',
            "-v", "0",
            "-show_entries", "format_tags",
        };

        try{
            final String result = executor.executeFFPROBE(args);
            final Matcher matcher = metadata.matcher(result);

            // #matches can not be used because it tests the whole string, not look for a match

            final Map<String,String> metadata = new HashMap<>();
            while(matcher.find())
                metadata.put(matcher.group(1), matcher.group(2));
            return metadata;
        }catch(final IOException ignored){ }
        return Collections.emptyMap();
    }

    // (technically ffmpeg but we are getting a file)
    public final File getCoverArt(final File input, final File output){
        final String[] args = {
            "-i", '"' + input.getAbsolutePath() + '"',
            "-map", "0:v",
            "-map", "0:V",
            "-c","copy",
            '"' + output.getAbsolutePath() + '"'
        };

        try{
            executor.executeFFMPEG(args);
            return output.exists() ? output : null;
        }catch(final IOException ignored){
            return null;
        }
    }

// ffmpeg

    public final boolean apply(
        final File INPUT,
        final File cover, final boolean preserveCover,
        final Map<String,String> metadata, final boolean preserveMeta,
        final File OUT) throws IOException{

        if(INPUT == null)
            throw new FileNotFoundException();
        else if(!INPUT.exists())
            throw new FileNotFoundException(INPUT.getAbsolutePath());
        else if(OUT == null)
            throw new FileNotFoundException();
        else if(!OUT.getParentFile().exists() && !OUT.getParentFile().mkdirs())
            throw new FileNotFoundException(OUT.getParentFile().getAbsolutePath());
        else if(cover != null && cover.exists() && cover.length() > 1e+7)
            throw new OutOfMemoryError("Cover art files exceeding 10MB will corrupt video file");

        if(((cover == null || !cover.exists()) && preserveCover) && ((metadata == null || metadata.isEmpty())) && preserveMeta){ // skip if no changes and preserve
            Files.copy(INPUT.toPath(), OUT.toPath(), StandardCopyOption.REPLACE_EXISTING);
            return true;
        }

        final List<String> args = new ArrayList<>();
        args.add("-i");
            args.add('"' + INPUT.getAbsolutePath() + '"');

        if(cover != null && cover.exists()){ // if cover exists
            args.add("-i");
                args.add('"' + cover.getAbsolutePath() + '"');
            args.add("-map");
                args.add("1");
            args.add("-map");
                args.add("0");
            args.add("-disposition:0");
                args.add("attached_pic");
        }else if((cover == null || !cover.exists()) && !preserveCover){ // if no cover and no preserve (remove cover)
            args.add("-map");
                args.add("0");
            args.add("-map");
                args.add("-0:v");
        }

        args.add("-y"); // override ? "-y" : "-n"

        args.add("-acodec");
            args.add("copy");
        args.add("-vcodec");
            args.add("copy");

        if(!preserveMeta){ // if no preserve (remove previous metadata)
            args.add("-map_metadata");
                args.add("-1");
        }
        if(metadata != null && !metadata.isEmpty())
            metadata.forEach((k,v) -> {
                args.add("-metadata");
                    args.add(String.format("\"%s\"=\"%s\"",k,v));
            });

        args.add('"' + OUT.getAbsolutePath() + '"');

        executor.executeFFMPEG(args.toArray(new String[0])); // todo
        return true;
    }

    //

    @Override
    public String toString(){
        return new ToStringBuilder(getClass().getSimpleName())
            .addObject("executor",executor)
            .addObject("duration_regexp",duration.pattern())
            .addObject("frames_regexp",frames.pattern())
            .addObject("metadata_regexp",metadata.pattern())
            .toString();
    }

}
