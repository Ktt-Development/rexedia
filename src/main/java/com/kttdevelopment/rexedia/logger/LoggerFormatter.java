package com.kttdevelopment.rexedia.logger;

import com.kttdevelopment.rexedia.utility.ToStringBuilder;

import java.text.SimpleDateFormat;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public final class LoggerFormatter extends Formatter {

    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSSaa zzz");

    private static final String trace = "%s@%s#%s", name = "%s >";

    private final boolean hasTimestamp, hasTrace;

    public LoggerFormatter(final boolean hasTimestamp, final boolean hasTrace){
        this.hasTimestamp = hasTimestamp;
        this.hasTrace     = hasTrace;
    }

    @Override
    public final String format(final LogRecord record){
        return
            (hasTimestamp ? '[' + sdf.format(record.getMillis()) + ']' + ' ' : "") +
            '[' + record.getLevel().getName() + ']' + ' ' +
            (hasTrace ? '[' + String.format(trace,record.getThreadID(),record.getSourceClassName(),record.getSourceMethodName()) + ']' + ' ' : "") +
            record.getMessage() + '\n';
    }

    //

    @Override
    public boolean equals(final Object o){
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        final LoggerFormatter that = (LoggerFormatter) o;
        return hasTimestamp == that.hasTimestamp &&
               hasTrace == that.hasTrace;
    }

    @Override
    public String toString(){
        return new ToStringBuilder("LoggerFormatter")
            .addObject("timestampSDF",sdf.toPattern())
            .addObject("traceString",trace)
            .addObject("nameString",name)
            .addObject("hasTimestamp",hasTimestamp)
            .addObject("hasTrace",hasTrace)
            .toString();
    }

}
