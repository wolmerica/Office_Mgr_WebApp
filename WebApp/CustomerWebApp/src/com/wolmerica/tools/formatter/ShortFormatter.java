/*
 * ShortFormatter.java
 *
 * Created on January 25, 2006, 08:55 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package com.wolmerica.tools.formatter;

/**
 *  A Formatter for short values
 */
public class ShortFormatter extends Formatter
{
    /** The default scale for currency values */
    public final static int SHORT_SCALE = 0;

    /**  The default format for currency values */
    public final static String SHORT_FORMAT = "##,000";    
    
    /** The key used to look up the currency error string */
    public final static String SHORT_ERROR_KEY = "errors.short";

    /**
     * Returns the error key for this Formatter.
     * @return the error key
     * @see com.aboutobjects.pitfalls.Formatter#getErrorKey()
     */
    public String getErrorKey() { return SHORT_ERROR_KEY; }

    /**
     * Returns an object representation of its argument.
     *
     * @return an object
     */

    public Object unformat(String string) {
        if (string == null || string.trim().length() < 1)
            return null;
        String errMsg = "Unable to parse an Short value from " + string;
        Short shortValue = null;

        try {
            shortValue = new Short(string);
        }
        catch (NumberFormatException e) {
            throw new FormattingException(errMsg, e);
        }

        return shortValue;
    }

    /**
     * Returns a formatted version of its argument.
     *
     * @return a formatted String
     */
    public String format(Short anShort) {
        return (anShort == null ? "" : anShort.toString());
    }
}
