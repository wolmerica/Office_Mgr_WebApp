/*
 * EmailFormatter.java
 *
 * Created on March 15, 2007, 01:16 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package com.wolmerica.tools.formatter;

import java.text.SimpleDateFormat;
import java.text.ParsePosition;
import java.text.FieldPosition;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * A Formatter for email addresses
 */
public class EmailFormatter extends Formatter
{
    public final static String EMAIL_ERROR_KEY = "errors.email";

    static final String PARSE_MSG = "Unable to parse an email address from ";
    static final String FORMAT_MSG = "Unable to format an email address from ";

    /**
     * Returns the error key for this Formatter.
     * @see Formatter#getErrorKey()
     */
    public String getErrorKey() { return EMAIL_ERROR_KEY; }

    /**
     * Check to make sure the entered email address is valid.
     * Return the email address value only when valid.
     */
    public Object unformat(String target)
    {
      if (target.length() > 0) {
        if (!(isValidEmailAddress(target)))
          throw new FormattingException(PARSE_MSG + target);
      }        
      return target;
    }
    
    /**
     * Nothing special to do for the email address.
     * Return the stored value just as it i.s
     */
    public String format(Object value) {
      if (value == null)
        return null;
        
      if (!(value instanceof String))
          throw new FormattingException(FORMAT_MSG + value);

      String email = (String) value;

      return email;
    }    
    
    /**
     * Validate the form of an email address.
     *
     * Return "true" only if emailAddress can successfully construct an
     * when parsed with "@" as delimiter, emailAddress contains
     * two tokens which satisfy
     *
     * The second condition arises since local email addresses, simply of the form
     * "woltech", for example, are valid for, but almost always undesired.
     */
    public boolean isValidEmailAddress(String emailAddress){
      if (emailAddress == null) return false;
      boolean result = true;
      try {
        InternetAddress emailAddr = new InternetAddress(emailAddress);
        if (!(hasNameAndDomain(emailAddress))) {
          result = false;
        }
      }
      catch (AddressException ex){
        result = false;
      }
      return result;
    }

    private boolean hasNameAndDomain(String emailAddress)
    {
      String[] tokens = emailAddress.split("@");
      return ((tokens.length == 2) &&
              (tokens[0].length() > 0) &&
              (tokens[1].length() > 0)) ;
    } 

}
