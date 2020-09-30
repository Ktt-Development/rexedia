package com.kttdevelopment.rexedia.format;

import java.io.IOException;
import java.util.function.Consumer;

public class FFMPEGExecutor {

    private final String pathToFFMPEG, pathToFFPROBE;

    public FFMPEGExecutor(final String pathToFFMPEG, final String pathToFFPROBE){
        this.pathToFFMPEG  = pathToFFMPEG;
        this.pathToFFPROBE = pathToFFPROBE;
    }

    public final String executeFFMPEG(final String[] args) throws IOException{
        return execute(pathToFFMPEG, args, null);
    }

    public final String executeFFMPEG(final String[] args, final Consumer<String> printStream) throws IOException{
        return execute(pathToFFMPEG, args, printStream);
    }

    public final String executeFFPROBE(final String[] args) throws IOException{
        return execute(pathToFFPROBE, args, null);
    }

    public final String executeFFPROBE(final String[] args, final Consumer<String> printStream) throws IOException{
        return execute(pathToFFPROBE, args, printStream);
    }

    //

    private String execute(final String path, final String[] args, final Consumer<String> printStream) throws IOException{
        final CommandExecutor executor = new CommandExecutor(path, printStream);
        return executor.execute(args);
    }

}
