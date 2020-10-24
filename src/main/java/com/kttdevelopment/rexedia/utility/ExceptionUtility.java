package com.kttdevelopment.rexedia.utility;

import java.io.PrintWriter;
import java.io.StringWriter;

public class ExceptionUtility {

    public static String getStackTraceAsString(final Throwable throwable){
        final StringWriter err = new StringWriter();
        throwable.printStackTrace(new PrintWriter(err));
        return err.toString();
    }

}
