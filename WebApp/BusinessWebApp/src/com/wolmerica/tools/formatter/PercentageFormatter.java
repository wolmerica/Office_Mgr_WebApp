/*
 * PercentageFormatter.java
 *
 * Created on December 02, 2005, 12:15 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package com.wolmerica.tools.formatter;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;

/**
 *  A Formatter for percentage values.
 */
public class PercentageFormatter extends Formatter {
    /** The default scale for percentage values */
    public final static int PERCENTAGE_SCALE = 2;
    /** The default format for percentage values */
    public final static String PERCENTAGE_FORMAT = "##0.00";
    /** The key used to look up the currency error string */
    public final static String PERCENTAGE_ERROR_KEY = "errors.percentage";

    /**
     * Returns the error key for this Formatter.
     * @return the error key
     * @see com.aboutobjects.pitfalls.Formatter#getErrorKey()
     */
    public String getErrorKey() { return PERCENTAGE_ERROR_KEY; }

    /**
     * Unformats its argument and returns a BigDecimal instance
     * initialized with the resulting string value
     *
     * @return a BigDecimal initialized with the provided string
     */
    public Object unformat(String target) {
        if (target == null || target.trim().length() < 1)
            return null;

        String errorMsg = "Unable to parse a PERCENTAGE value from " +
            target;
        BigDecimal value;
        try {
            DecimalFormat formatter = new DecimalFormat(PERCENTAGE_FORMAT);
            Number parsedNumber = formatter.parse(target.trim());
            value = new BigDecimal(parsedNumber.doubleValue());
        }
        catch (NumberFormatException nfe) {
            throw new FormattingException(errorMsg, nfe);
        }
        catch (ParseException pe) {
            throw new FormattingException(errorMsg, pe);
        }
        
        value = value.setScale(2, BigDecimal.ROUND_HALF_UP);

        return value;
    }

    /**
     * Returns a string representation of its argument, formatted as a
     * percentage value.
     *
     * @return a formatted String
     */
    public String format(Object value) {
        if (value == null)
            return "N/A";

        String stringValue = "";
        try {
            BigDecimal bigDecValue = (BigDecimal)value;
            bigDecValue = bigDecValue.setScale(PERCENTAGE_SCALE,
                                               BigDecimal.ROUND_HALF_UP);
            DecimalFormat df = new DecimalFormat(PERCENTAGE_FORMAT);
            stringValue = df.format(bigDecValue.doubleValue());
        }
        catch (IllegalArgumentException iae) {
            throw new FormattingException("Unable to format " + value +
                "as a percentage value", iae);
        }

        return stringValue;
    }
}
