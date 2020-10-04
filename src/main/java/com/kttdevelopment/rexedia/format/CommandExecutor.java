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

        logger.log(Level.FINER,"Executing args:\n" + String.join(" ", a));

        final StringBuilder OUT = new StringBuilder();

        logger.log(Level.FINER,"--- [ START EXECUTION ] ---");

        final ProcessBuilder builder = new ProcessBuilder();
        builder.redirectErrorStream(true);
        builder.command(a.toArray(new String[0]));

        final Process process = builder.start();

        try(final BufferedReader IN = new BufferedReader(new InputStreamReader(process.getInputStream()))){
            String ln;
            while((ln = IN.readLine()) != null){
                logger.log(Level.FINEST, ln);
                OUT.append(ln).append('\n');
            }
        }
        // try{ process.waitFor();
        // }catch(final InterruptedException ignored){ }finally{
        //     process.destroy();
        // }
        try{
            process.waitFor(10,TimeUnit.SECONDS);
        }catch(final InterruptedException e){
            e.printStackTrace();
        }finally{
            process.destroyForcibly();

            if(process.isAlive())
                throw new IllegalThreadStateException(process.toString());
        }
        
        logger.log(Level.FINER,"--- [ END EXECUTION ] ---");

        logger.finest("About to return!");
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
