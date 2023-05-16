/*
 * ByteFormatter.java
 *
 * Created on December 16, 2005, 08:55 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package com.wolmerica.tools.formatter;

/**
 *  A Formatter for byte values
 */
public class ByteFormatter extends Formatter
{
    /** The key used to look up the currency error string */
    public final static String BYTE_ERROR_KEY = "errors.byte";

    /**
     * Returns the error key for this Formatter.
     * @return the error key
     * @see com.aboutobjects.pitfalls.Formatter#getErrorKey()
     */
    public String getErrorKey() { return BYTE_ERROR_KEY; }

    /**
     * Returns an object representation of its argument.
     *
     * @return an object
     */
    public Object unformat(String string) {
        if (string == null || string.trim().length() < 1)
            return null;
        String errMsg = "Unable to parse an Byte value from " + string;
        Byte byteValue = null;

        try {
            byteValue = new Byte(string);
        }
        catch (NumberFormatException e) {
            throw new FormattingException(errMsg, e);
        }

        return byteValue;
    }

    /**
     * Returns a formatted version of its argument.
     *
     * @return a formatted String
     */
    public String format(Byte anByte) {
        return (anByte == null ? "" : anByte.toString());
    }
}
