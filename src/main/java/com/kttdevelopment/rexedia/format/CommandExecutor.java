package com.kttdevelopment.rexedia.format;

import com.kttdevelopment.core.classes.ToStringBuilder;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Consumer;

final class CommandExecutor {

    private final String[] args;
    private final Consumer<String> consumer;

    public CommandExecutor(){
        this(new String[0],null);
    }

    public CommandExecutor(final Consumer<String> consumer){
        this(new String[0],consumer);
    }

    public CommandExecutor(final String arg){
        this(new String[]{arg},null);
    }

    public CommandExecutor(final String... args){
        this(args,null);
    }

    public CommandExecutor(final String arg, final Consumer<String> consumer){
        this(new String[]{arg},consumer);
    }

    public CommandExecutor(final String[] args, final Consumer<String> consumer){
        this.args     = args;
        this.consumer = consumer;
    }

    //

    public final String execute() throws IOException{
        return execute(new String[0]);
    }

    public final String execute(final String... args) throws IOException{
        final List<String> a = new ArrayList<>();
        a.addAll(Arrays.asList(this.args));
        a.addAll(Arrays.asList(args));

        final ProcessBuilder builder = new ProcessBuilder();
        builder.redirectErrorStream(true);
        builder.command(a.toArray(new String[0]));

        final Process process = builder.start();

        final BufferedReader IN = new BufferedReader(new InputStreamReader(process.getInputStream()));
        final StringBuilder OUT = new StringBuilder();

        while(true){ // fix BufferedReader#readLine holding thread
            final ExecutorService executor = Executors.newSingleThreadExecutor();
            final Future<String> future = executor.submit(IN::readLine);
            final String ln;
            try{
                ln = future.get(1, TimeUnit.SECONDS);
                if(ln == null) break;
                OUT.append(ln).append('\n');
                if(consumer != null) consumer.accept(ln);
            }catch(InterruptedException | ExecutionException | TimeoutException e){
                if(e instanceof TimeoutException | e instanceof InterruptedException)
                    break;
            }
        }

        // debug
        System.out.println("\n");
        System.out.println("$ " + String.join(" ", a));
        System.out.println(OUT);

        return OUT.toString().trim();
    }

    //

    @Override
    public String toString(){
        return new ToStringBuilder(getClass().getSimpleName())
            .addObject("args",args)
            .addObject("consumer",consumer)
            .toString();
    }

}
