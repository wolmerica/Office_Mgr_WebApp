/*
 * Formatter.java
 *
 * Created on December 02, 2005, 12:15 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package com.wolmerica.tools.formatter;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.sql.Timestamp;

/**
 * The base class for all Formatters. Provides default formatting for
 * types for which no specific Formatter subclass can be found.
 * Note that this class does not provide handling for primitive Java types
 * such as int, float, etc.
 */
public class Formatter
{
    /** Indicates that conversion is from string to object */
    public final static int STRING_TO_OBJECT = 0;
    /** Indicates that conversion is from object to string */
    public final static int OBJECT_TO_STRING = 1;
    /** Information about conversion errors */
    //    private Map errorInfo = new HashMap();
    //
    //    public Map getErrorInfo() { return errorInfo; }
    //    public void setErrorInfo(Map errorInfo) { this.errorInfo = errorInfo; }
    public String getErrorKey() { return ""; }

    /**
     * Returns a Formatter class for formatting the given type
     *
     * @param type the class of the object to be formatted
     * @return an initialized instance of Formatter or one of
     *         its subclasses
     */
    public static Formatter getFormatter(Class type) {
    
      if (BigDecimal.class.isAssignableFrom(type))
          return new CurrencyFormatter();
      else if (Timestamp.class.isAssignableFrom(type))
          return new TimestampFormatter();
      else if (Date.class.isAssignableFrom(type))
          return new DateFormatter();
      else if (Boolean.class.isAssignableFrom(type))
          return new BooleanFormatter();
      else if (Byte.class.isAssignableFrom(type))
          return new ByteFormatter();
      else if (Integer.class.isAssignableFrom(type))
          return new IntegerFormatter();
      else if (Short.class.isAssignableFrom(type))
          return new ShortFormatter();
      else
          return new Formatter();
    }
    
    
    /**
     * Returns an object that is the result of formatting the
     * provided value. The type of formatting to be performed is
     * specified by <code>mode</code>, which may be either
     * <code>STRING_TO_OBJECT</code>, or <code>OBJECT_TO_STRING</code>.
     *
     * @param target an object containing the value to be formatted
     * @param type the class of the object to produce if the mode is
     *             <code>STRING_TO_OBJECT</code>; otherwise the class
     *             of the provided object
     * @param mode the formatting mode
     * @return a formatted or unformatted version of the given object
     */
    public Object format(Object target, Class type, int mode) {
        if (mode == OBJECT_TO_STRING)
            return format(target);
        else if (mode == STRING_TO_OBJECT)
            return unformat((String)target);
        else
            throw new FormattingException("Invalid mode: " + mode);
    }

    /**
     * Returns a String representation of a given object. Overridden
     * by subclasses to provide customized behavior for different types.
     *
     * @param target the object to be formatted
     * @return a formatted string representation of the given object
     */
    public String format(Object target) {
        return target == null ? "" : target.toString();
    }

    /**
     * Returns an object representation of the provided string, after any
     * extraneous formatting characters have been removed. Overridden by
     * subclasses to provide customized behavior for different types.s
     *
     * @param target the string to be unformatted
     * @return an object representation of the value in the given string
     */
    public Object unformat(String target) {
        return target;
    }
}
