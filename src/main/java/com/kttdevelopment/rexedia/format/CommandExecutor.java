package com.kttdevelopment.rexedia.format;

import com.kttdevelopment.core.classes.ToStringBuilder;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;
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
        final List<String> a = new ArrayList<>();
        a.addAll(Arrays.asList(this.args));
        a.addAll(Arrays.asList(args));

        Logger.getGlobal().log(Level.FINER,"Executing args: " + String.join(" ", a));

        final ProcessBuilder builder = new ProcessBuilder();
        builder.redirectErrorStream(true);
        builder.command(a.toArray(new String[0]));

        final Process process   = builder.start();
        final BufferedReader IN = new BufferedReader(new InputStreamReader(process.getInputStream()));
        final StringBuilder OUT = new StringBuilder();

        Logger.getGlobal().log(Level.FINER,"--- [ START EXECUTION ] ---");

        final ExecutorService executor = Executors.newSingleThreadExecutor();
        String ln = "";
        while(true){ // fix BufferedReader#readLine holding thread
            final Future<String> future = executor.submit(IN::readLine);
            try{
                Logger.getGlobal().log(Level.FINEST,"<↓> NEXT LINE <↓>");
                ln = future.get(10, TimeUnit.SECONDS);
                if(ln == null) break;
                Logger.getGlobal().log(Level.FINER, ln);
                OUT.append(ln).append('\n');
            }catch(InterruptedException | ExecutionException | TimeoutException ignored){
                break;
            }
        }
        executor.shutdownNow();

        Logger.getGlobal().log(Level.FINER,"--- [ END EXECUTION ] ---");
        Logger.getGlobal().log(Level.FINER,"LAST LINE: " + ln);

        if(ln == null || (!ln.trim().equalsIgnoreCase("Terminate batch job (Y/N)?") && !ln.trim().equalsIgnoreCase("Press any key to continue . . .")))
            try{ process.waitFor();
            }catch(final InterruptedException ignored){ }

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
