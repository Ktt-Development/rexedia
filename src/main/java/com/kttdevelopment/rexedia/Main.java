package com.kttdevelopment.rexedia;

import com.kttdevelopment.core.tests.exceptions.ExceptionUtil;
import com.kttdevelopment.rexedia.config.Configuration;
import com.kttdevelopment.rexedia.logger.LoggerFormatter;
import com.kttdevelopment.rexedia.preset.Preset;
import com.kttdevelopment.rexedia.utility.FileUtility;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.logging.*;

public abstract class Main {

    public static void main(String[] args){
        try{
            // config
            final Configuration config = new Configuration(args);

            // logger
            final Logger logger = Logger.getGlobal();
            {
                logger.setLevel(Level.ALL);

                final boolean debug = (Boolean) config.getConfiguration().get(Configuration.DEBUG);
                logger.addHandler(new ConsoleHandler() {{
                    setLevel(debug ? Level.ALL : Level.INFO);
                    setFormatter(new LoggerFormatter(debug, debug));
                }});

                if((Boolean) config.getConfiguration().get(Configuration.LOGGING)){
                    logger.addHandler(new FileHandler(FileUtility.getFreeFile(new File(System.currentTimeMillis() + ".log")).getName()){{
                        setLevel(Level.INFO);
                        setFormatter(new LoggerFormatter(true,false));
                    }});
                    logger.addHandler(new FileHandler("latest.log"){{
                        setLevel(Level.INFO);
                        setFormatter(new LoggerFormatter(true,false));
                    }});
                }
                if(debug)
                    logger.addHandler(new FileHandler("debug.log"){{
                        setLevel(Level.ALL);
                        setFormatter(new LoggerFormatter(true,true));
                    }});
            }

            // format
            final Preset preset = config.getPreset();
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
