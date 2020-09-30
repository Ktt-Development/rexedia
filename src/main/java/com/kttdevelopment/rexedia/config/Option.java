package com.kttdevelopment.rexedia.config;

import java.util.function.Function;

final class Option<T> {

    private final org.apache.commons.cli.Option option;

    private final Function<String[],T> supplier;

    private final T defaultValue;

    public Option(final org.apache.commons.cli.Option option, final T defaultValue){
        this.option         = option;
        this.supplier       = (args) -> defaultValue;
        this.defaultValue   = defaultValue;
    }

    public Option(final org.apache.commons.cli.Option option, final Function<String[], T> supplier){
        this.option         = option;
        this.supplier       = supplier;
        this.defaultValue   = null;
    }

    public Option(final org.apache.commons.cli.Option option, final Function<String[], T> supplier, final T defaultValue){
        this.option         = option;
        this.supplier       = supplier;
        this.defaultValue   = defaultValue;
    }

    public final org.apache.commons.cli.Option getOption(){
        return option;
    }

    public final T getDefault(){
        return defaultValue;
    }

    public final T getValue(final String... subArgs){
        return supplier.apply(subArgs);
    }

    //

    public static class Builder<T>{

        private final String flag;
        private Function<String[],T> supplier;

        private String longFlag, desc;

        private boolean argsOptional = true;
        private int expectedArgs = 0;

        private boolean required = false;

        private T defaultValue;

        public Builder(final String flag){
            this.flag = flag;
        }

        public Builder(final String flag, final Function<String[],T> supplier){
            this.flag = flag;
            this.supplier = supplier;
        }

        public final Builder<T> setLongFlag(final String flag){
            this.longFlag = flag;
            return this;
        }

        public final Builder<T> setDesc(final String desc){
            this.desc = desc;
            return this;
        }

        public final Builder<T> argsOptional(){
            return argsOptional(true);
        }

        public final Builder<T> argsOptional(final boolean optional){
            this.argsOptional = optional;
            return this;
        }

        public final Builder<T> argsRequired(){
            return argsOptional(false);
        }

        public final Builder<T> argsRequired(final boolean required){
            return argsOptional(!required);
        }

        public final Builder<T> noArgs(){
            return setExpectedArgs(0);
        }

        public final Builder<T> unlimitedArgs(){
            return setExpectedArgs(org.apache.commons.cli.Option.UNLIMITED_VALUES);
        }

        public final Builder<T> setExpectedArgs(final int args){
            this.expectedArgs = args;
            return this;
        }

        public final Builder<T> required(){
            return required(true);
        }

        public final Builder<T> required(final boolean required){
            this.required = required;
            return this;
        }

        public final Builder<T> setDefaultValue(final T defaultValue){
            this.defaultValue = defaultValue;
            return this;
        }

        //

        public final Option<T> build(){
            final org.apache.commons.cli.Option.Builder builder = org.apache.commons.cli.Option.builder(flag);
            if(longFlag != null)
                builder.longOpt(longFlag);
            if(desc != null)
                builder.desc(desc);
            if(argsOptional)
                builder.optionalArg(true);
            builder.numberOfArgs(expectedArgs);
            if(required)
                builder.required();

            return supplier != null
                ? new Option<>(builder.build(), supplier, defaultValue)
                : new Option<>(builder.build(), defaultValue);
        }

    }

}
