/*
 * Copyright (C) 2021 Ktt Development
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package com.kttdevelopment.rexedia.format;

import com.kttdevelopment.rexedia.config.Configuration;
import com.kttdevelopment.rexedia.preset.MetadataPreset;
import com.kttdevelopment.rexedia.preset.Preset;
import com.kttdevelopment.rexedia.utility.ExceptionUtility;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public final class MetadataFormatter {

    private final int verify, verifyDiscrepancy;
    private final boolean preserveCover, preserveMetadata, preserveBackup;
    private final FFMPEG ffmpeg;
    private final Preset preset;

    public MetadataFormatter(final Configuration configuration, final FFMPEG ffmpeg, final Preset preset){
        final Map<String,?> config = configuration.getConfiguration();
        this.verify             = (int) config.get(Configuration.VERIFY);
        this.verifyDiscrepancy  = (int) config.get(Configuration.VERDIFF);
        this.preserveCover      = (boolean) config.get(Configuration.PRECOV);
        this.preserveMetadata   = (boolean) config.get(Configuration.PREMETA);
        this.preserveBackup     = (boolean) config.get(Configuration.BACKUP);

        this.ffmpeg = ffmpeg;
        this.preset = preset;
    }

    public synchronized final boolean format(final File file, final int current, final int total){
        final String lstr = "| STAGE > %s (%s/5) \t| FILE " + current + '/' + total + "\t| " + file.getName();

        final Logger logger = Logger.getGlobal();
        final String abs    = file.getAbsolutePath();

        // check if input is a media file (verify file integrity)
        {
            logger.info(String.format(lstr, "VERIFY / MEDIA ", 1));
            logger.fine("Verifying " + abs);
            if(!ffmpeg.verifyFileIntegrity(file, verify, verifyDiscrepancy)){
                logger.severe("Failed to verify " + abs + " (file was " + (file.exists() ? "corrupt" : "missing") + ")");
                return false;
            }else
                logger.finer("Verified file " + abs);
        }

        final File parent = file.getParentFile();

        final String full_name  = file.getName();
        final String name       = full_name.contains(".")
            ? full_name.substring(0, full_name.lastIndexOf('.'))
            : full_name;
        final String ext        = full_name.contains(".") ? full_name.substring(full_name.lastIndexOf('.') + 1) : ""; // contains check not needed because if none found the index is 0

        // create a backup file
        final File backup;
        final String babs;
        {
            backup = getUsableFile(new File(parent,String.format("%s.backup.%s",name,ext)));

            babs   = backup.getAbsolutePath();

            logger.info(String.format(lstr, "CLONE  / BACKUP", 2));
            logger.fine("Creating backup at " + babs);
            try{
                Files.copy(file.toPath(), backup.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }catch(final IOException e){
                logger.severe("Failed to copy " + abs + " to backup " + babs + '\n' + ExceptionUtility.getStackTraceAsString(e));
                return false;
            }

            // verify backup integrity
            logger.info(String.format(lstr, "VERIFY / BACKUP", 3));
            logger.fine("Verifying backup " + babs);
            if(!ffmpeg.verifyFileIntegrity(backup, verify, verifyDiscrepancy)){
                logger.severe("Failed to verify " + babs + " (file was " + (backup.exists() ? "corrupt" : "missing") + ")");
                return false;
            }else
                logger.finer("Verified backup " + babs);
        }

        // set output file
        final File output;
        {
            final MetadataPreset target = preset.getOutputPreset();
            logger.fine("Setting output file using preset " + target);

            if(target == null)
                output = file;
            else{
                final String out = target.format(name);
                output = getUsableFile(new File(parent,out + (out.contains(".") ? "" : '.' + ext)));
            }
            logger.fine("Set output file as " + output.getAbsolutePath());
        }

        // apply cover & metadata
        {
            final MetadataPreset covp = preset.getCoverPreset();

            final File cover = covp == null ? null : new File(parent,preset.getCoverPreset().format(name));
            final Map<String,String> metadata = new HashMap<>();
            for(final MetadataPreset meta : preset.getPresets())
                metadata.put(meta.getKey(),meta.format(name));
            logger.fine("Applying preset to file " + abs + '\n' + preset);
            logger.finer("Cover file: " + (covp == null ? null : cover.getAbsolutePath()));
            logger.finer("Metadata: " + metadata);
            logger.info(String.format(lstr, "APPLY  / MEDIA ", 4));

            try{
                ffmpeg.apply(backup,cover,preserveCover, metadata, preserveMetadata, output);
            }catch(final IOException e){
                logger.severe("Failed to format file " + abs + '\n' + ExceptionUtility.getStackTraceAsString(e));
                return false;
            }

        }

        // verify file integrity
        {
            logger.fine("Verifying output " + abs);
            logger.info(String.format(lstr, "VERIFY / FINAL ", 5));
            if(!ffmpeg.verifyFileIntegrity(output, verify, verifyDiscrepancy)){
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
                    logger.warning("Failed to delete backup " + babs + '\n' + ExceptionUtility.getStackTraceAsString(e));
                }
        }
        return true;
    }

    // return a file that is not currently used by appending a copy number
    private File getUsableFile(final File file){
        if(!file.exists())
            return file;

        final String full_name  = file.getName();
        final String name       = full_name.contains(".")
            ? full_name.substring(0, full_name.lastIndexOf('.'))
            : full_name;
        final String ext        = full_name.substring(full_name.lastIndexOf('.') + 1); // contains check not needed because if none found the index is 0
        long copyno = 0;
        File copy;

        while((copy = new File(file.getParentFile(),String.format("%s (%s).%s",name,copyno,ext))).exists())
            copyno++;
        return copy;
    }

}
