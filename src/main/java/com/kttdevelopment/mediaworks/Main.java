package com.kttdevelopment.mediaworks;

import com.kttdevelopment.mediaworks.config.DefaultOptions;
import org.apache.commons.cli.*;

public abstract class Main {

    public static void main(String[] args){
        // logger

        // config
        try{
            CommandLine cmd = new DefaultParser().parse(DefaultOptions.get(), args);
        }catch(ParseException e){
            e.printStackTrace();
        } // todo: set logger if debug
    }

}
