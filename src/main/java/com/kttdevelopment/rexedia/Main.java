package com.kttdevelopment.rexedia;

import com.kttdevelopment.core.tests.exceptions.ExceptionUtil;
import com.kttdevelopment.rexedia.config.Configuration;
import com.kttdevelopment.rexedia.preset.Preset;
import com.kttdevelopment.rexedia.utility.FileUtility;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class Main {

    public static void main(String[] args){
        try{
            // config
            final Configuration config = new Configuration(args);

            // logger
            final Logger logger = Logger.getGlobal();

            // parse meta
            final Preset preset = null;

            // format
            final File[] files = null;

            for(final File file : files){

            }
        }catch(final Throwable e){
            final long time = System.currentTimeMillis();
            final String response = "---- rexedia Crash Log ----\n" +
                                    "Time: "            + new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSSaa zzz").format(time) + '\n' +
                                    "OS: "              + System.getProperty("os.name").toLowerCase() + '\n' +
                                    "Java Version: "    + System.getProperty("java.version") + '\n' +
                                    "Args: "            + Arrays.toString(args) + '\n' +
                                    "---- [ Stack Trace ] ----\n" +
                                    ExceptionUtil.getStackTraceAsString(e);
            Logger.getGlobal().log(Level.SEVERE,'\n' + response);
            try{ Files.write(FileUtility.getFreeFile(new File("crash-log-" + time + ".log")).toPath(),response.getBytes(StandardCharsets.UTF_8));
            }catch(final IOException ignored){ }
        }
    }

}
