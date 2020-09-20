package com.kttdevelopment.rexedia;

import com.kttdevelopment.rexedia.config.DefaultOptions;
import org.apache.commons.cli.*;

import java.util.logging.Logger;

public abstract class Main {

    public static void main(String[] args){
        // logger
        final Logger logger;
        {
            logger = Logger.getGlobal();

        }

        // config
        try{
            CommandLine cmd = new DefaultParser().parse(DefaultOptions.get(), args);
        }catch(ParseException e){
            e.printStackTrace();
        } // todo: set logger if debug

        // parse meta

        // format

    }

}
