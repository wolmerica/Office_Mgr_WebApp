/*
 * PhoneNumberFormatter.java
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

/**
 * A Formatter for phone numbers
 */
public class PhoneNumberFormatter extends Formatter
{
    public final static String PHONE_NUMBER_ERROR_KEY = "errors.phone";

    static final String PARSE_MSG = "Unable to parse a phone number from ";
    static final String FORMAT_MSG = "Unable to format a phone number from ";
    static final int NUM_DIGITS = 10;

    /**
     * Returns the error key for this Formatter.
     * @see Formatter#getErrorKey()
     */
    public String getErrorKey() { return PHONE_NUMBER_ERROR_KEY; }

    /**
     * Removes formatting characters from the provided phone number and
     * returns just the digits. Very lenient about formatting, but requires
     * a ten-digit number.
     */
    public Object unformat(String target)
    {
      String digits = "";
      if (target.length() > 0) {        
        digits = target.replaceAll("[^0-9]", "");
        if (digits.length() != NUM_DIGITS)
          throw new FormattingException(PARSE_MSG + target);
      }
      
      return digits;
    }

    /**
     * Returns its argument formatted as a phone number in the style:<p>
     * <pre>
     * (999) 999-9999
     * </pre>
     */
    public String format(Object value) {
        if ((value == null) || (value.toString() == ""))
            return null;
        if (!(value instanceof String))
            throw new FormattingException(FORMAT_MSG + value);
        String digits = (String) value; 
// Only attempt to format the phone number if the length is greater than zero.
// The UI will allow the phone values to be blank.        
        StringBuffer buf = new StringBuffer("");
        if (digits.length() > 0) {
          if (digits.length() != NUM_DIGITS)
              throw new FormattingException(FORMAT_MSG + digits);     

          buf = new StringBuffer("(");
          buf.append(digits.substring(0, 3));
          buf.append(") ");
          buf.append(digits.substring(3, 6));
          buf.append("-" );
          buf.append(digits.substring(6));
        }
        
        return buf.toString();
    }
}
