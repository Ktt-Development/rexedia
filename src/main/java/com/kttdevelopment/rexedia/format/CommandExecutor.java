package com.kttdevelopment.rexedia.format;

import com.kttdevelopment.rexedia.utility.ToStringBuilder;
import org.apache.commons.exec.*;

import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

final class CommandExecutor {

    private final String[] args;

    public CommandExecutor(){
        this(new String[0]);
    }

    public CommandExecutor(final String... args){
        this.args = args;
    }

    //

    public final String execute() throws IOException{
        return execute(new String[0]);
    }

    public final String execute(final String... args) throws IOException{
        final Logger logger = Logger.getGlobal();

        final List<String> a = new ArrayList<>();
        a.addAll(Arrays.asList(this.args));
        a.addAll(Arrays.asList(args));

        final String asString = String.join(" ", a);

        logger.log(Level.FINER,"Executing args:\n" + asString);

        logger.log(Level.FINER,"--- [ START EXECUTION ] ---");

        final CommandLine cmd           = CommandLine.parse(asString);
        final DefaultExecutor executor  = new DefaultExecutor();
        final ByteArrayOutputStream OUT = new ByteArrayOutputStream();

        String result;

        executor.setWatchdog(new ExecuteWatchdog(30 * 1000));
        executor.setStreamHandler(new PumpStreamHandler(OUT));
        try{
            executor.execute(cmd);
        }catch(final Throwable e){
            result = OUT.toString().trim();
            logger.severe(result.isEmpty() ? "Execution timed out" : '\n' + result);
            logger.log(Level.FINER,"--- [ END EXECUTION ] ---");
            throw e;
        }
        result = OUT.toString().trim();
        logger.finer('\n' + result);
        logger.log(Level.FINER,"--- [ END EXECUTION ] ---");
        return result;
    }

    //

    @Override
    public String toString(){
        return new ToStringBuilder(getClass().getSimpleName())
            .addObject("args", args)
            .toString();
    }

}
