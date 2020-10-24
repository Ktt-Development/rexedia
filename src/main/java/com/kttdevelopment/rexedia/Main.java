package com.kttdevelopment.rexedia;

import com.kttdevelopment.rexedia.utility.ExceptionUtility;
import com.kttdevelopment.rexedia.config.Configuration;
import com.kttdevelopment.rexedia.format.FFMPEG;
import com.kttdevelopment.rexedia.format.MetadataFormatter;
import com.kttdevelopment.rexedia.logger.LoggerFormatter;
import com.kttdevelopment.rexedia.preset.Preset;
import com.kttdevelopment.rexedia.utility.FileUtility;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.*;

public abstract class Main {

    private static Configuration config = null;
    private static Preset preset = null;
    private static FFMPEG ffmpeg = null;

    public static void main(String[] args){
        try{
            // logger
            final Logger logger = Logger.getGlobal();
            {
                logger.setLevel(Level.ALL);
                logger.setUseParentHandlers(false);
            }
            final Handler consoleHandler = new ConsoleHandler() {{
                setLevel(Level.INFO);
                setFormatter(new LoggerFormatter(false, false));
            }};
            logger.addHandler(consoleHandler);

            // config
            config = new Configuration(args);

            // config dependent loggers
            {
                final boolean debug = (Boolean) config.getConfiguration().get(Configuration.DEBUG);

                consoleHandler.setLevel(debug ? Level.ALL : Level.INFO);
                consoleHandler.setFormatter(new LoggerFormatter(debug, debug));

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
            {
                logger.fine("Preallocating files");
                preset = config.getPreset();
                // preallocate
                final boolean walk = (boolean) config.getConfiguration().get(Configuration.WALK);
                final File[] files = (File[]) config.getConfiguration().get(Configuration.INPUT);

                final List<File> queue = new ArrayList<>();
                for(final File file : files)
                    if(file.isFile())
                        queue.add(file);
                    else
                        if(!walk)
                            queue.addAll(Arrays.asList(Objects.requireNonNullElse(file.listFiles(File::isFile), new File[0])));
                        else
                            try{
                                Files.walk(file.toPath()).filter(path -> path.toFile().isFile()).forEach(path -> queue.add(path.toFile()));
                            }catch(final IOException e){
                                logger.warning("Failed to walk through directory " + file.getAbsolutePath() + '\n' + ExceptionUtility.getStackTraceAsString(e));
                            }
                logger.fine("Starting file format");
                logger.fine("Loaded file queue: " + queue);
                ffmpeg = new FFMPEG("bin/ffmpeg","bin/ffprobe");
                final MetadataFormatter formatter = new MetadataFormatter(config, ffmpeg, preset);

                final int size = queue.size();
                for(int i = 0; i < size; i++)
                    formatter.format(queue.get(i),i+1,size);
            }
        }catch(final Throwable e){
            final long time = System.currentTimeMillis();
            final String response = "---- rexedia Crash Log ----\n" +
                                    "Time: "            + new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSSaa zzz").format(time) + '\n' +
                                    "OS: "              + System.getProperty("os.name").toLowerCase() + '\n' +
                                    "Java Version: "    + System.getProperty("java.version") + '\n' +
                                    "Args: "            + Arrays.toString(args) + '\n' +
                                    "Config: "          + config + '\n' +
                                    "Preset: "          + preset + '\n' +
                                    "FFMPEG: "          + ffmpeg + '\n' +
                                    "---- [ Stack Trace ] ----\n" +
                                    ExceptionUtility.getStackTraceAsString(e);
            Logger.getGlobal().log(Level.SEVERE,'\n' + response);
            try{ Files.write(FileUtility.getFreeFile(new File("crash-log-" + time + ".log")).toPath(),response.getBytes(StandardCharsets.UTF_8));
            }catch(final IOException ignored){ }
        }
    }

}
