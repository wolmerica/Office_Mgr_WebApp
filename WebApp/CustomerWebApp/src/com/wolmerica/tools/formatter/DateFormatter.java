/*
 * DateFormatter.java
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
import java.util.Calendar;
import java.util.Date;
import java.text.DateFormat;
import java.text.DateFormatSymbols;

import org.apache.log4j.Logger;

/**
 * A Formatter for date values
 */
public class DateFormatter extends Formatter {
    /**  The default date format string */
    public final static String DATE_FORMAT = "MM/dd/yyyy";

    /** The key used to look up the currency error string */
    public final static String DATE_ERROR_KEY = "errors.date";

    /**
     * Returns the error key for this Formatter.
     * @return the error key
     * @see com.aboutobjects.pitfalls.Formatter#getErrorKey()
     */
    public String getErrorKey() { return DATE_ERROR_KEY; }

    /**
     * Unformats its argument and return a java.util.Date instance
     * initialized with the resulting string.
     *
     * @return a java.util.Date intialized with the provided string
     */
    public Object unformat(String target) {
        String errorMsg = "Unable to parse a date value from " + target;

        Date date = null;

        try {
          SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT);
          formatter.setLenient(false);
          date = formatter.parse(target, new ParsePosition(0));
        }
        catch (IllegalArgumentException iae) {
            throw new FormattingException(errorMsg, iae);
        }
        catch (NullPointerException npe) {
            throw new FormattingException(errorMsg, npe);
        }
        catch (Exception e) {
            throw new FormattingException(errorMsg, e);
        }

        if (date == null)
          throw new FormattingException(errorMsg);

        return date;
    }

    /**
     * Returns a string representation of its argument, formatted as a date
     * with the "MM/dd/yyyy" format.
     *
     * @return a formatted String
     */
    public String format(Object value) {
        if (value == null)
            return "";

        StringBuffer buf = new StringBuffer();
        SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT);
        formatter.setLenient(false);
        formatter.format(value, buf, new FieldPosition(0));

        return buf.toString();
    }

    /**
     * Returns a string representation of its argument, formatted as a date
     * with the "Day Mon dd,yyyy" format.
     *
     * @return a formatted String
     */
    public String formatToWords(Object value) {
        if (value == null)
            return "";

        Date inDate = (Date) value;

        Calendar rightNow = Calendar.getInstance();
        rightNow.setTimeInMillis(inDate.getTime());

        String dayNames[] = new DateFormatSymbols().getShortWeekdays();
        String monthNames[] = new DateFormatSymbols().getShortMonths();

        String displayDate = dayNames[rightNow.get(rightNow.DAY_OF_WEEK)];
        displayDate = displayDate + " " + monthNames[rightNow.get(rightNow.MONTH)];
        displayDate = displayDate + " " + rightNow.get(rightNow.DATE);
        displayDate = displayDate + ", " + rightNow.get(rightNow.YEAR);

        return displayDate;
    }
}
