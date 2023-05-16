/*
 * BooleanFormatter.java
 *
 * Created on December 02, 2005, 12:15 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package com.wolmerica.tools.formatter;

/**
 *  A Formatter for boolean values
 */
public class BooleanFormatter extends Formatter
{
    /** The key used to look up the currency error string */
    public final static String BOOLEAN_ERROR_KEY = "errors.boolean";

    /**
     * Returns the error key for this Formatter.
     * @return the error key
     * @see com.aboutobjects.pitfalls.Formatter#getErrorKey()
     */
    public String getErrorKey() { return BOOLEAN_ERROR_KEY; }

    /**
     * Returns an object representation of its argument.
     *
     * @return an object
     */
    public Object unformat(String string) {
        if (string == null || string.trim().length() < 1)
            return null;
        String errMsg = "Unable to parse a Boolean value from " + string;
        Boolean booleanValue = false;

        try {
            booleanValue = new Boolean(string);
        }
        catch (Exception e) {
            throw new FormattingException(errMsg, e);
        }

        return booleanValue;
    }

    /**
     * Returns a formatted version of its argument.
     *
     * @return a formatted String
     */
    public String format(Boolean aboolean) {
        return aboolean.toString();
    }
}
