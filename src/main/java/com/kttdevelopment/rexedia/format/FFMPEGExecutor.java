package com.kttdevelopment.rexedia.format;

import com.kttdevelopment.core.classes.ToStringBuilder;

import java.io.IOException;

final class FFMPEGExecutor {

    private final String pathToFFMPEG, pathToFFPROBE;

    public FFMPEGExecutor(final String pathToFFMPEG, final String pathToFFPROBE){
        this.pathToFFMPEG  = pathToFFMPEG;
        this.pathToFFPROBE = pathToFFPROBE;
    }

    public final String executeFFMPEG(final String[] args) throws IOException{
        return execute(pathToFFMPEG, args);
    }

    public final String executeFFPROBE(final String[] args) throws IOException{
        return execute(pathToFFPROBE, args);
    }

    //

    private String execute(final String path, final String[] args) throws IOException{
        final CommandExecutor executor = new CommandExecutor(path);
        return executor.execute(args);
    }

    //

    @Override
    public String toString(){
        return new ToStringBuilder(getClass().getSimpleName())
            .addObject("pathToFFMPEG",pathToFFMPEG)
            .addObject("pathToFFPROBE",pathToFFPROBE)
            .toString();
    }

}
