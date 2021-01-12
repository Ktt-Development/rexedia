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

package com.kttdevelopment.rexedia.config;

import com.kttdevelopment.rexedia.utility.ToStringBuilder;
import com.kttdevelopment.rexedia.preset.*;
import com.kttdevelopment.rexedia.utility.CollectionsUtility;
import org.apache.commons.cli.*;

import java.io.File;
import java.io.IOException;
import java.util.*;

public final class Configuration {

    @SuppressWarnings({"SpellCheckingInspection", "RedundantSuppression"})
    public static final String
        INPUT   = "i",
        WALK    = "w",
        BACKUP  = "b",
        DEBUG   = "d",
        LOGGING = "l",
        VERIFY  = "v",
        VERDIFF = "vd",
        PRECOV  = "pc",
        PREMETA = "pm",
        PRESET  = "p",
        COVER   = "c",
        META    = "m",
        OUTPUT  = "o";

    private final Option<?>[] defaultOptions = {
        new Option.Builder<>("h")
            .setLongFlag("help")
            .build(),
        new Option.Builder<>(INPUT)
            .setLongFlag("input")
            .setDesc("The file or directory to format")
            .unlimitedArgs()
            .argsRequired()
            .build(),
        new Option.Builder<>(WALK)
            .setLongFlag("walk")
            .setDesc("Should subdirectories also be formatted")
            .setExpectedArgs(1)
            .argsOptional()
            .setDefaultValue(false)
            .build(),
        new Option.Builder<>(BACKUP)
            .setLongFlag("backup")
            .setDesc("Should a backup file be kept of the original")
            .setExpectedArgs(1)
            .argsOptional()
            .setDefaultValue(false)
            .build(),
        new Option.Builder<>(DEBUG)
            .setLongFlag("debug")
            .setDesc("Run logging in debug mode and create a debug file")
            .setExpectedArgs(1)
            .argsOptional()
            .setDefaultValue(false)
            .build(),
        new Option.Builder<>(LOGGING)
            .setLongFlag("log")
            .setDesc("Log process to a file")
            .setExpectedArgs(1)
            .argsOptional()
            .setDefaultValue(false)
            .build(),
        new Option.Builder<>(VERIFY)
            .setLongFlag("verify")
            .setDesc("Set file validation level, 0 = none, 1 = frames within range (default), 2 = exact frames or exceeding within range, 3 = exact frame count")
            .setExpectedArgs(1)
            .argsRequired()
            .setDefaultValue(1)
            .build(),
        new Option.Builder<>(VERDIFF)
            .setLongFlag("verifyDiscrepancy")
            .setDesc("Set the maximum frame difference discrepancy")
            .setExpectedArgs(1)
            .argsRequired()
            .setDefaultValue(100)
            .build(),
        new Option.Builder<>(PRECOV)
            .setLongFlag("preserveCover")
            .setDesc("Should files with no new cover art keep their original cover art")
            .setExpectedArgs(1)
            .argsOptional()
            .setDefaultValue(true)
            .build(),
        new Option.Builder<>(PREMETA)
            .setLongFlag("preserveMeta")
            .setDesc("Should files preserve any existing metadata")
            .setExpectedArgs(1)
            .argsOptional()
            .setDefaultValue(false)
            .build(),
        new Option.Builder<>(PRESET)
            .setLongFlag("preset")
            .setDesc("The preset file to use")
            .setExpectedArgs(1)
            .argsRequired()
            .build(),
        new Option.Builder<>(COVER)
            .setLongFlag("cover")
            .setDesc("The cover format to use")
            .setExpectedArgs(2)
            .argsRequired()
            .build(),
        new Option.Builder<>(META)
            .setLongFlag("metadata")
            .setDesc("The metadata format to use")
            .unlimitedArgs()
            .argsRequired()
            .build(),
        new Option.Builder<>(OUTPUT)
            .setLongFlag("output")
            .setDesc("The output format to use")
            .setExpectedArgs(2)
            .argsRequired()
            .build()
    };

    private final Preset preset;
    private final Map<String,Object> configuration = new HashMap<>();

    @SuppressWarnings("SpellCheckingInspection")
    public Configuration(final String... args) throws ParseException, IOException{
        final Options options = new Options();
        for(final Option<?> option : defaultOptions){
            options.addOption(option.getOption());
            configuration.put(option.getOption().getOpt(), option.getDefault());
        }

        final CommandLine cmd = new DefaultParser().parse(options, args);

        if(args.length == 0 || cmd.hasOption("h")){
            new HelpFormatter().printHelp("rexedia", options);
            System.exit(0);
        }
        if(cmd.hasOption(INPUT)){
            final String[] sargs = cmd.getOptionValues(INPUT);
            if(sargs.length < 1)
                throw new MissingArgumentException(INPUT);
            final List<File> files = new ArrayList<>();
            for(final String arg : sargs)
                files.add(new File(arg));
            configuration.put(INPUT, files.toArray(new File[0]));
        }else{
            throw new MissingOptionException(INPUT);
        }
        if(cmd.hasOption(PRESET)){
            String path = cmd.getOptionValue(PRESET);
            preset = new PresetParser().parse(new File(path));
            configuration.put(PRESET, path);
        }else if(cmd.hasOption(COVER) || cmd.hasOption(META) || cmd.hasOption(OUTPUT)){
            final Preset.Builder p = new Preset.Builder();
            if(cmd.hasOption(COVER)){
                final String[] v = cmd.getOptionValues(COVER);
                p.setCoverPreset(new MetadataPreset(null,v[0],v[1]));
            }
            if(cmd.hasOption(META)){
                final List<List<String>> v = CollectionsUtility.partitionList(cmd.getOptionValues(META),3);
                if(v.isEmpty())
                    throw new MissingArgumentException(META);
                for(final List<String> strings : v)
                    try{
                        p.addPreset(new MetadataPreset(strings.get(0), strings.get(1), strings.get(2)));
                    }catch(final IndexOutOfBoundsException ignored){
                        throw new MissingArgumentException(META);
                    }
            }
            if(cmd.hasOption(OUTPUT)){
                final String[] v = cmd.getOptionValues(OUTPUT);
                p.setOutputPreset(new MetadataPreset(null,v[0],v[1]));
            }
            preset = p.build();
        }else{
            throw new MissingOptionException(META);
        }
        if(cmd.hasOption(VERIFY)){
            configuration.put(VERIFY, Integer.parseInt(cmd.getOptionValue(VERIFY)));
            int v = Integer.parseInt(cmd.getOptionValue(VERIFY));
            if(v < 0 || v > 3)
                throw new IllegalArgumentException("Verify mode can only be 0-3");
            if(cmd.hasOption(VERDIFF)){
                configuration.put(VERDIFF, Integer.parseInt(cmd.getOptionValue(VERDIFF)));
                if(Integer.parseInt(cmd.getOptionValue(VERDIFF)) < 0)
                    throw new IllegalArgumentException("Verify discrepancy must be a positive integer");
            }
        }
        if(cmd.hasOption(WALK))
            configuration.put(WALK, cmd.getOptionValue(WALK) == null || Boolean.parseBoolean(cmd.getOptionValue(WALK)));
        if(cmd.hasOption(BACKUP))
            configuration.put(BACKUP, cmd.getOptionValue(BACKUP) == null || Boolean.parseBoolean(cmd.getOptionValue(BACKUP)));
        if(cmd.hasOption(LOGGING))
            configuration.put(LOGGING, cmd.getOptionValue(LOGGING) == null || Boolean.parseBoolean(cmd.getOptionValue(LOGGING)));
        if(cmd.hasOption(DEBUG))
            configuration.put(DEBUG, cmd.getOptionValue(DEBUG) == null || Boolean.parseBoolean(cmd.getOptionValue(DEBUG)));
        if(cmd.hasOption(PRECOV))
            configuration.put(PRECOV, cmd.getOptionValue(PRECOV) == null || Boolean.parseBoolean(cmd.getOptionValue(PRECOV)));
        if(cmd.hasOption(PREMETA))
            configuration.put(PREMETA, cmd.getOptionValue(PREMETA) == null || Boolean.parseBoolean(cmd.getOptionValue(PREMETA)));
    }

    public final Preset getPreset(){
        return preset;
    }

    public final Map<String,Object> getConfiguration(){
        return Collections.unmodifiableMap(configuration);
    }

    //

    @Override
    public String toString(){
        return new ToStringBuilder(getClass().getSimpleName())
            .addObject("preset",preset)
            .addObject("configuration", configuration)
            .toString();
    }

}
