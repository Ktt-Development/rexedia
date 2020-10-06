package com.kttdevelopment.rexedia.format;

import com.kttdevelopment.core.tests.exceptions.ExceptionUtil;
import com.kttdevelopment.rexedia.config.Configuration;
import com.kttdevelopment.rexedia.preset.MetadataPreset;
import com.kttdevelopment.rexedia.preset.Preset;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public final class MetadataFormatter {

    private final boolean preserveCover, preserveMetadata, preserveBackup;
    private final FFMPEG ffmpeg;
    private final Preset preset;

    public MetadataFormatter(final Configuration configuration, final FFMPEG ffmpeg, final Preset preset){
        this.preserveCover    = (boolean) configuration.getConfiguration().get(Configuration.PRECOV);
        this.preserveMetadata = (boolean) configuration.getConfiguration().get(Configuration.PREMETA);
        this.preserveBackup   = (boolean) configuration.getConfiguration().get(Configuration.BACKUP);

        this.ffmpeg = ffmpeg;
        this.preset = preset;
    }

    public synchronized final boolean format(final File file, final int current, final int total){
        final String lstr = "| STAGE > %s (%s/5) \t| FILES > (" + current + '/' + total + ')';

        final Logger logger = Logger.getGlobal();
        final String abs    = file.getAbsolutePath();
        // check if input is a media file (verify file integrity)
        {
            logger.info(String.format(lstr, "VERIFY / MEDIA ", 1));
            logger.fine("Verifying " + abs);
            if(!ffmpeg.verifyFileIntegrity(file)){
                logger.severe("Failed to verify " + abs + " (file was corrupt)");
                return false;
            }else
                logger.finer("Verified file " + abs);
        }
        // create a backup file
        final File backup;
        final String babs;
        {
            final String full_name  = file.getName();
            final String name       = full_name.substring(0,full_name.lastIndexOf('.'));
            final String ext        = full_name.substring(full_name.lastIndexOf('.') + 1);

            backup = getUsableFile(new File(file.getParentFile(),String.format("%s.backup.%s",name,ext)));
            babs   = backup.getAbsolutePath();

            logger.info(String.format(lstr, "CLONE  / BACKUP", 2));
            logger.fine("Creating backup at " + babs);
            try{
                Files.copy(file.toPath(), backup.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }catch(final IOException e){
                logger.severe("Failed to copy " + abs + " to backup " + babs + '\n' + ExceptionUtil.getStackTraceAsString(e));
                return false;
            }

            // verify backup integrity
            logger.info(String.format(lstr, "VERIFY / BACKUP", 3));
            logger.fine("Verifying backup " + babs);
            if(!ffmpeg.verifyFileIntegrity(backup)){
                logger.severe("Failed to verify backup " + babs + " (file was corrupt)");
                return false;
            }else
                logger.finer("Verified backup " + babs);
        }
        // apply cover & metadata
        {
            final String name = file.getName();
            final File cover = new File(file.getParentFile(),preset.getCoverPreset().format(name));
            final Map<String,String> metadata = new HashMap<>();
            for(final MetadataPreset meta : preset.getPresets())
                metadata.put(meta.getKey(),meta.format(name));
            logger.fine("Applying preset to file " + abs + '\n' + preset);
            logger.finer("Cover file: " + cover.getAbsolutePath());
            logger.finer("Metadata: " + metadata);
            logger.info(String.format(lstr, "APPLY  / MEDIA ", 4));

            try{
                ffmpeg.apply(backup,cover,preserveCover, metadata, preserveMetadata, file);
            }catch(final IOException e){
                logger.severe("Failed to format file " + abs + '\n' + ExceptionUtil.getStackTraceAsString(e));
                return false;
            }
        }

        // verify file integrity
        {
            logger.fine("Verifying output " + abs);
            logger.info(String.format(lstr, "VERIFY / FINAL ", 5));
            if(!ffmpeg.verifyFileIntegrity(file)){
                logger.severe("Failed to verify output " + abs + " (file was corrupt)");
                return false;
            }else
                logger.finer("Verified output file " + abs);
        }

        // delete backup
        {
            if(!preserveBackup)
                try{
                    Files.delete(backup.toPath());
                }catch(final IOException e){
                    logger.warning("Failed to delete backup " + babs + '\n' + ExceptionUtil.getStackTraceAsString(e));
                }
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
