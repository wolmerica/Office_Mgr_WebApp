/*
 * IntegerFormatter.java
 *
 * Created on December 02, 2005, 12:15 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package com.wolmerica.tools.formatter;

/**
 *  A Formatter for integer values
 */
public class IntegerFormatter extends Formatter
{
    /** The key used to look up the currency error string */
    public final static String INTEGER_ERROR_KEY = "errors.integer";

    /**
     * Returns the error key for this Formatter.
     * @return the error key
     * @see com.aboutobjects.pitfalls.Formatter#getErrorKey()
     */
    public String getErrorKey() { return INTEGER_ERROR_KEY; }

    /**
     * Returns an object representation of its argument.
     *
     * @return an object
     */
    public Object unformat(String string) {
        if (string == null || string.trim().length() < 1)
            return null;
        String errMsg = "Unable to parse an Integer value from " + string;
        Integer integerValue = null;

        try {
            integerValue = new Integer(string);
        }
        catch (NumberFormatException e) {
            throw new FormattingException(errMsg, e);
        }

        return integerValue;
    }

    /**
     * Returns a formatted version of its argument.
     *
     * @return a formatted String
     */
    public String format(Integer anInteger) {
        return (anInteger == null ? "" : anInteger.toString());
    }
}
