package com.kttdevelopment.rexedia.config;

import com.kttdevelopment.rexedia.preset.MetadataPreset;
import com.kttdevelopment.rexedia.preset.Preset;
import org.apache.commons.cli.*;

import java.io.File;
import java.util.*;
import java.util.function.Function;

public final class Configuration {

    @SuppressWarnings("SpellCheckingInspection")
    public static final String
        INPUT   = "i",
        WALK    = "w",
        PRESET  = "p",
        BACKUP  = "b",
        THREADS = "t",
        PRECOV  = "pc",
        PREMETA = "pm",
        COVER   = "c",
        META    = "m",
        DEBUG   = "d";

    private final Function<String[],File> fileSupplier       = (arg) -> new File(arg[0]);
    private final Function<String[],Boolean> booleanSupplier = (arg) -> arg.length == 0 || Boolean.parseBoolean(arg[0]);

    private final Option<?>[] defaultOptions = {
        new Option.Builder<>(INPUT, fileSupplier)
            .setLongFlag("input")
            .setDesc("The file or directory to format (can be used multiple times)")
            .setExpectedArgs(1)
            .argsRequired()
            .required()
            .build(),
        new Option.Builder<>(WALK, booleanSupplier)
            .setLongFlag("walk")
            .setDesc("Should subdirectories also be formatted")
            .setExpectedArgs(1)
            .argsOptional()
            .setDefaultValue(false)
            .build(),
        new Option.Builder<>(PRESET, fileSupplier)
            .setLongFlag("preset")
            .setDesc("The preset file to use")
            .setExpectedArgs(1)
            .argsRequired()
            .build(),
        new Option.Builder<>(BACKUP, booleanSupplier)
            .setLongFlag("backup")
            .setDesc("Should a backup file be kept of the original")
            .setExpectedArgs(1)
            .argsOptional()
            .setDefaultValue(false)
            .build(),
        new Option.Builder<Integer>(THREADS)
            .setLongFlag("threads")
            .setDesc("How many files can be formatted simultaneously")
            .setExpectedArgs(1)
            .argsRequired()
            .setDefaultValue(1)
            .build(),
        new Option.Builder<>(PRECOV, booleanSupplier)
            .setLongFlag("preserveCover")
            .setDesc("Should files with no new cover art keep their original cover art")
            .setExpectedArgs(1)
            .argsOptional()
            .setDefaultValue(true)
            .build(),
        new Option.Builder<>(PREMETA, booleanSupplier)
            .setLongFlag("preserveMeta")
            .setDesc("Should files preserve any existing metadata")
            .setExpectedArgs(1)
            .argsOptional()
            .setDefaultValue(false)
            .build(),
        new Option.Builder<MetadataPreset>(COVER, null) // todo
            .setLongFlag("cover")
            .setDesc("The cover string.format regex equation")
            .unlimitedArgs()
            .argsRequired()
            .build(),
        new Option.Builder<MetadataPreset>(META, null) // todo
            .setLongFlag("cover")
            .setDesc("The metadata string.format regex equation (can be used multiple times)")
            .unlimitedArgs()
            .argsRequired()
            .build(),
        new Option.Builder<>(DEBUG, booleanSupplier)
            .setLongFlag("debug")
            .setDesc("Should the program run in debug mode")
            .setExpectedArgs(1)
            .argsOptional()
            .setDefaultValue(false)
            .build()
    };

    private final Map<String,Object> options = new HashMap<>();

    public final Preset preset  = new Preset.Builder().build();

    public Configuration(final String... args) throws ParseException{
        final Options options = new Options();
        for(final Option<?> option : defaultOptions){
            options.addOption(option.getOption());
            this.options.put(option.getOption().getArgName(),option.getDefault());
        }


        final CommandLine cmd = new DefaultParser().parse(options, args);

        if(cmd.hasOption(INPUT)){
            final String[] sargs = cmd.getOptionValues(INPUT);

        }

    }

}
