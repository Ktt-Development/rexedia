package com.kttdevelopment.rexedia.format;

import com.kttdevelopment.core.tests.exceptions.ExceptionUtil;
import com.kttdevelopment.rexedia.preset.Preset;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.logging.Logger;

public final class MetadataFormatter {

    private final FFMPEG ffmpeg;
    private final Preset preset;

    public MetadataFormatter(final FFMPEG ffmpeg, final Preset preset){
        this.ffmpeg = ffmpeg;
        this.preset = preset;
    }

    public synchronized final boolean format(final File file){
        final Logger logger = Logger.getGlobal();
        final String abs    = file.getAbsolutePath();
        // check if input is a media file (verify file integrity)
        {
            logger.info("Verifying " + abs);
            if(!ffmpeg.verifyFileIntegrity(file)){
                logger.severe("Failed to verify " + abs + " (file was corrupt)");
                return false;
            }else
                logger.finer("Verified file " + abs);
        }
        // create a backup file
        final File backup;
        {
            final String full_name  = file.getName();
            final String name       = full_name.substring(0,full_name.lastIndexOf('.'));
            final String ext        = full_name.substring(full_name.lastIndexOf('.') + 1);

            backup = getUsableFile(new File(file.getParentFile(),String.format("%s.backup.%s",name,ext)));
            final String babs = backup.getAbsolutePath();

            logger.info("Creating backup at " + babs);
            try{
                Files.copy(file.toPath(), backup.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }catch(final IOException e){
                logger.severe("Failed to copy " + abs + " to backup " + babs + '\n' + ExceptionUtil.getStackTraceAsString(e));
                return false;
            }

            // verify backup integrity
            logger.info("Verifying backup " + babs);
            if(!ffmpeg.verifyFileIntegrity(backup)){
                logger.severe("Failed to verify backup " + babs + " (file was corrupt)");
                return false;
            }else
                logger.finer("Verified backup " + babs);
        }
        // apply cover & metadata
        {
            // todo
        }

        // verify file integrity
        {
            logger.info("Verifying output " + abs);
            if(!ffmpeg.verifyFileIntegrity(file)){
                logger.severe("Failed to verify output " + abs + " (file was corrupt)");
                return false;
            }else
                logger.finer("Verified output file " + abs);
        }
        return true;
    }

    // return a file that is not currently used by appending a copy number
    private File getUsableFile(final File file){
        if(!file.exists())
            return file;

        final String full_name  = file.getName();
        final String name       = full_name.substring(0,full_name.lastIndexOf('.'));
        final String ext        = full_name.substring(full_name.lastIndexOf('.') + 1);
        long copyno = 0;
        File copy;

        while((copy = new File(file.getParentFile(),String.format("%s (%s).%s",name,copyno,ext))).exists())
            copyno++;
        return copy;
    }

}
