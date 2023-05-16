/*
 * TimestampFormatter.java
 *
 * Created on December 02, 2005, 12:15 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package com.wolmerica.tools.formatter;

import java.text.SimpleDateFormat;
import java.text.ParsePosition;
import java.text.FieldPosition;
import java.util.Date;
import java.sql.Timestamp;

/**
 * A Formatter for timestamp values
 */
public class TimestampFormatter extends Formatter {
    /**  The default date format string */
// Retiring the HSQLDB format    
//    public final static String TIMESTAMP_FORMAT = "MM/dd/yyyy hh:mm:ss.SSS";
    
// MySQL format    
    public final static String TIMESTAMP_FORMAT = "MM/dd/yyyy hh:mm:ss";
    /** The key used to look up the currency error string */
    public final static String TIMESTAMP_ERROR_KEY = "errors.timestamp";

    /**
     * Returns the error key for this Formatter.
     * @return the error key
     * @see com.aboutobjects.pitfalls.Formatter#getErrorKey()
     */
    public String getErrorKey() { return TIMESTAMP_ERROR_KEY; }

    /**
     * Unformats its argument and return a java.util.Date instance
     * initialized with the resulting string.
     *
     * @return a java.util.Date intialized with the provided string
     */
    public Object unformat(String target) {
//System.out.println(" unformat " + target);        
        if (target == null || target.trim().length() < 1)
            return null;
        String errorMsg = "Unable to parse a timestamp value from " + target;
        Date date = null;
        Timestamp timestamp = null;
        SimpleDateFormat formatter = new SimpleDateFormat(TIMESTAMP_FORMAT);
        formatter.setLenient(true);
        date = formatter.parse(target, new ParsePosition(0));

        if (date == null)
            throw new FormattingException(errorMsg);
        
        timestamp = new Timestamp(date.getTime());
        return timestamp;
    }

    /**
     * Returns a string representation of its argument, formatted as a date
     * with the "MM/dd/yyyy hh:mm:ss" format.
     *
     * @return a formatted String
     */
    
    public String format(Object value) {
        if (value == null)
            return "";

        StringBuffer buf = new StringBuffer();
        SimpleDateFormat formatter = new SimpleDateFormat(TIMESTAMP_FORMAT);
        formatter.setLenient(false);
        formatter.format(value, buf, new FieldPosition(0));

        return buf.toString();
    }
}
