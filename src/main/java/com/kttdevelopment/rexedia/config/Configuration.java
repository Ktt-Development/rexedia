package com.kttdevelopment.rexedia.config;

import com.kttdevelopment.rexedia.preset.MetadataPreset;
import com.kttdevelopment.rexedia.preset.Preset;
import org.apache.commons.cli.*;

import java.io.File;
import java.util.*;
import java.util.function.Function;

public final class Configuration {

    @SuppressWarnings({"SpellCheckingInspection", "RedundantSuppression"})
    public static final String
        INPUT   = "i",
        WALK    = "w",
        BACKUP  = "b",
        DEBUG   = "d",
        LOGGING = "l",
        THREADS = "t",
        PRECOV  = "pc",
        PREMETA = "pm",
        PRESET  = "p";

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
        new Option.Builder<>(BACKUP, booleanSupplier)
            .setLongFlag("backup")
            .setDesc("Should a backup file be kept of the original")
            .setExpectedArgs(1)
            .argsOptional()
            .setDefaultValue(false)
            .build(),
        new Option.Builder<>(LOGGING, booleanSupplier)
            .setLongFlag("log")
            .setDesc("Log process to a file")
            .setExpectedArgs(1)
            .argsOptional()
            .setDefaultValue(false)
            .build(),
        new Option.Builder<>(DEBUG, booleanSupplier)
            .setLongFlag("debug")
            .setDesc("Run logging in debug mode and create a debug file")
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
        new Option.Builder<>(PRESET, fileSupplier)
            .setLongFlag("preset")
            .setDesc("The preset file to use")
            .setExpectedArgs(1)
            .argsRequired()
            .build(),
    };

    private final Map<String,Object> configuration = new HashMap<>();

    public final Preset preset  = new Preset.Builder().build();

    public Configuration(final String... args) throws ParseException{
        final Options options = new Options();
        for(final Option<?> option : defaultOptions){
            options.addOption(option.getOption());
            configuration.put(option.getOption().getArgName(), option.getDefault());
        }


        final CommandLine cmd = new DefaultParser().parse(options, args);

        if(cmd.hasOption(INPUT)){
            final String[] sargs = cmd.getOptionValues(INPUT);
            final List<File> files = new ArrayList<>();
            for(final String arg : sargs)
                files.add(new File(arg));
            configuration.put(INPUT, files.toArray());
        }
        if(cmd.hasOption(WALK))
            configuration.put(WALK, cmd.getOptionValue(WALK) == null || Boolean.parseBoolean(cmd.getOptionValue(WALK)));
        if(cmd.hasOption(BACKUP))
            configuration.put(BACKUP, cmd.getOptionValue(BACKUP) == null || Boolean.parseBoolean(cmd.getOptionValue(BACKUP)));
        if(cmd.hasOption(LOGGING))
            configuration.put(LOGGING, cmd.getOptionValue(LOGGING) == null || Boolean.parseBoolean(cmd.getOptionValue(LOGGING)));
        if(cmd.hasOption(DEBUG))
            configuration.put(DEBUG, cmd.getOptionValue(DEBUG) == null || Boolean.parseBoolean(cmd.getOptionValue(DEBUG)));
        if(cmd.hasOption(THREADS))
            configuration.put(THREADS, Integer.parseInt(cmd.getOptionValue(THREADS)));
        if(cmd.hasOption(PRECOV))
            configuration.put(PRECOV, cmd.getOptionValue(PRECOV) == null || Boolean.parseBoolean(cmd.getOptionValue(PRECOV)));
        if(cmd.hasOption(PREMETA))
            configuration.put(PREMETA, cmd.getOptionValue(PREMETA) == null || Boolean.parseBoolean(cmd.getOptionValue(PREMETA)));
        // todo: preset
    }

    public final Map<String,Object> getConfiguration(){
        return Collections.unmodifiableMap(configuration);
    }

}
