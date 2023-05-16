/*
 * CurrencyFormatter.java
 *
 * Created on December 02, 2005, 12:15 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 *
 */

package com.wolmerica.tools.formatter;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.DecimalFormat;
import java.math.BigDecimal;

/**
 *  A Formatter for currency values.
 */
public class CurrencyFormatter extends Formatter {
    /** The default scale for currency values */
    public final static int CURRENCY_SCALE = 2;

    /**  The default format for currency values */
    public final static String CURRENCY_FORMAT = "###,##0.00";

    /** The key used to look up the currency error string */
    public final static String CURRENCY_ERROR_KEY = "errors.currency";

    /**
     * Returns the error key for this Formatter.
     * @return the error key
     * @see com.aboutobjects.pitfalls.Formatter#getErrorKey()
     */
    public String getErrorKey() { return CURRENCY_ERROR_KEY; }

    /**
     * Unformats its argument and returns a BigDecimal instance
     * initialized with the resulting string value
     *
     * @return a BigDecimal initialized with the provided string
     *
     * 08/12/2006 - Add a call to round() before returning BigDecimal value.
     */
    public Object unformat(String target) {
        if (target == null || target.trim().length() < 1)
            return null;

        String errorMsg = "Unable to parse a currency value from " +
            target;
        BigDecimal value;
        try {
            DecimalFormat formatter = new DecimalFormat(CURRENCY_FORMAT);
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
     * currency value.
     *
     * @return a formatted String
     */
    public String format(Object value) {
        if (value == null)
            return "";

        String stringValue = "";
        try {
            BigDecimal bigDecValue = (BigDecimal)value;
            bigDecValue = bigDecValue.setScale(CURRENCY_SCALE,
                                               BigDecimal.ROUND_HALF_UP);
            DecimalFormat df = new DecimalFormat(CURRENCY_FORMAT);
            stringValue = df.format(bigDecValue.doubleValue());
        }
        catch (IllegalArgumentException iae) {
            throw new FormattingException("Unable to format " + value +
                "as a currency value", iae);
        }

        return stringValue;
    }
}
