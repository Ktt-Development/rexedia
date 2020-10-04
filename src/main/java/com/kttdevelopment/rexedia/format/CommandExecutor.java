package com.kttdevelopment.rexedia.format;

import com.kttdevelopment.core.classes.ToStringBuilder;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Consumer;
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

        a.addAll(Arrays.asList("&&","echo","<> done <>"));

        logger.log(Level.FINER,"Executing args: " + String.join(" ", a));

        final StringBuilder OUT = new StringBuilder();

        logger.log(Level.FINER,"--- [ START EXECUTION ] ---");

        final ProcessBuilder builder = new ProcessBuilder();
        builder.redirectErrorStream(true);
        builder.command(a.toArray(new String[0]));

        final Process process = builder.start();

        final ExecutorService executor = Executors.newSingleThreadExecutor();
        final BufferedReader IN = new BufferedReader(new InputStreamReader(process.getInputStream()));
        while(true){
            try{
                logger.finest("b4 future");
                final Future<String> future = executor.submit(IN::readLine);
                final String ln = future.get(10, TimeUnit.SECONDS);
                if(ln == null) break;
                logger.log(Level.FINER, ln);
                logger.finest("ar future");
                synchronized(this){
                    logger.finest("b4 append");
                    OUT.append(ln).append('\n');
                    logger.finest("ar append");
                }
            }catch(final Throwable e){
                e.printStackTrace();
                logger.finest("broke from while loop");
                break;
            }finally{
                logger.finest("finally");
                IN.close();
                executor.shutdownNow();
            }
        }

        logger.finest("before waitfor");

        try{ process.waitFor();
        }catch(final InterruptedException ignored){ }finally{
            process.destroy();
        }
        
        logger.log(Level.FINER,"--- [ END EXECUTION ] ---");

        return OUT.toString().trim();
    }

    //

    @Override
    public String toString(){
        return new ToStringBuilder(getClass().getSimpleName())
            .addObject("args",args)
            .toString();
    }

}
