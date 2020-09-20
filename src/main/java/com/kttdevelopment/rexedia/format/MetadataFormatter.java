package com.kttdevelopment.rexedia.format;

import com.kttdevelopment.rexedia.preset.Preset;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;

public final class MetadataFormatter {

    private final Preset preset;

    public MetadataFormatter(final Preset preset){
        this.preset = preset;
    }

    public synchronized final void format(final File file) throws IOException{
        // check if input is a media file (verify file integrity)

        // create a backup file
        final File backup;
        {
            final String full_name  = file.getName();
            final String name       = full_name.substring(0,full_name.lastIndexOf('.'));
            final String ext        = full_name.substring(full_name.lastIndexOf('.') + 1);

            backup = getUsableFile(new File(file.getParentFile(),String.format("%s .backup.%s",name,ext)));

            Files.copy(file.toPath(), backup.toPath(), StandardCopyOption.REPLACE_EXISTING);

            // verify file integrity
        }
        // apply cover & metadata
        {

        }

        // verify file integrity

        // delete backup (if -b)

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
