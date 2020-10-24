package com.kttdevelopment.rexedia.config;

import com.kttdevelopment.rexedia.utility.ToStringBuilder;

final class Option<T> {

    private final org.apache.commons.cli.Option option;

    private final T defaultValue;

    public Option(final org.apache.commons.cli.Option option, final T defaultValue){
        this.option         = option;
        this.defaultValue   = defaultValue;
    }

    public final org.apache.commons.cli.Option getOption(){
        return option;
    }

    public final T getDefault(){
        return defaultValue;
    }

    //

    @Override
    public String toString(){
        return new ToStringBuilder(getClass().getSimpleName())
            .addObject("option",option)
            .addObject("defaultValue",defaultValue)
            .toString();
    }

    public static class Builder<T>{

        private final String flag;

        private String longFlag, desc;

        private boolean argsOptional = true;
        private int expectedArgs = 0;

        private T defaultValue;

        public Builder(final String flag){
            this.flag = flag;
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
            builder.optionalArg(argsOptional);
            builder.numberOfArgs(expectedArgs);

            return new Option<>(builder.build(), defaultValue);
        }

        //

        @Override
        public String toString(){
            return new ToStringBuilder(getClass().getSimpleName())
                .addObject("flag", flag)
                .addObject("longFlag", longFlag)
                .addObject("desc", desc)
                .addObject("argsOptional", argsOptional)
                .addObject("expectedArgs", expectedArgs)
                .addObject("defaultValue", defaultValue)
                .toString();
        }

    }

}
