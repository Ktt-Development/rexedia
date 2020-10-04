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

        logger.log(Level.FINER,"Executing args: " + String.join(" ", a));

        final ProcessBuilder builder = new ProcessBuilder();
        builder.redirectErrorStream(true);
        builder.command(a.toArray(new String[0]));

        final Process process   = builder.start();
        final BufferedReader IS = new BufferedReader(new InputStreamReader(process.getInputStream()));
        final BufferedReader ES = new BufferedReader(new InputStreamReader(process.getErrorStream()));
        final StringBuilder OUT = new StringBuilder();

        logger.log(Level.FINER,"--- [ START EXECUTION ] ---");

        final Consumer<BufferedReader> append = (IN) -> {
            final ExecutorService executor = Executors.newSingleThreadExecutor();
            while(true){
                try{ // fix thread hold on no end line
                    logger.finest("b4 future");
                    final Future<String> future = executor.submit(IN::readLine);
                    final String ln = future.get(10, TimeUnit.SECONDS);
                    if(ln == null) break;
                    logger.log(Level.FINER, ln);
                    logger.finest("ar future");
                    synchronized(this){
                        OUT.append(ln).append('\n');
                    }
                }catch(InterruptedException | ExecutionException | TimeoutException ignored){
                    logger.finest("broke from while loop");
                    try{ IN.close();
                    }catch(final IOException ignored1){ }
                    break;
                }
            }
            executor.shutdownNow();
        };

        final Thread ISR = new Thread(() -> append.accept(IS));
        ISR.start();
        final Thread ESR = new Thread(() -> append.accept(ES));
        ESR.start();

        logger.finest("before waitfor");

        try{ process.waitFor();
        }catch(final InterruptedException ignored){ }finally{
            process.destroy();
        }
        ISR.stop();
        ESR.stop();
        
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
