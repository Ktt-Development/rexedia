package com.kttdevelopment.rexedia.config;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

public abstract class DefaultOptions {

    public static Options get(){
        final Option[] options = new Option[]{
            Option.builder("f")
                .longOpt("format")
                .hasArg()
                .desc("The format configuration file  (flags override config file)")
                .build(),
            Option.builder("i")
                .longOpt("input")
                .hasArgs()
                .desc("The files being formatted")
                .required()
                .build(),
            Option.builder("d")
                .longOpt("debug")
                .type(Boolean.class)
                .numberOfArgs(1)
                .optionalArg(true)
                .desc("Whether to use debug logger")
                .build(),
            Option.builder("t")
                .longOpt("threads")
                .desc("The amount of simultaneous threads")
                .build(),
            Option.builder("pc")
                .longOpt("preserveCover")
                .type(Boolean.class)
                .numberOfArgs(1)
                .optionalArg(true)
                .desc("Preserve any existing cover art if new art can not be found")
                .build(),
            Option.builder("pm")
                .longOpt("preserveMeta")
                .type(Boolean.class)
                .numberOfArgs(1)
                .optionalArg(true)
                .desc("Preserve any existing metadata")
                .build(),
            Option.builder("b")
                .longOpt("backup")
                .type(Boolean.class)
                .numberOfArgs(1)
                .optionalArg(true)
                .desc("Create a backup of the original file")
                .build(),
            Option.builder("c")
                .longOpt("cover")
                .numberOfArgs(1)
                .desc("Cover format")
                .build(),
            Option.builder("m")
                .longOpt("meta")
                .hasArgs()
                .desc("Metadata format")
                .build()
        };
        final Options op = new Options();
        for(final Option option : options)
            op.addOption(option);
        return op;
    }

}
