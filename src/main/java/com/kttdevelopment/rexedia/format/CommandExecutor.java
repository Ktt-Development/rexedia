package com.kttdevelopment.rexedia.format;

import java.io.*;
import java.util.*;
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

        String ln;
        while((ln = IN.readLine()) != null){
            OUT.append(ln).append('\n');
            if(consumer != null) consumer.accept(ln);
        }

        try{ process.waitFor();
        }catch(final InterruptedException ignored){ }

        // debug
        System.out.println("\n");
        System.out.println("$ " + String.join(" ", a));
        System.out.println(OUT);

        return OUT.toString().trim();
    }

}
