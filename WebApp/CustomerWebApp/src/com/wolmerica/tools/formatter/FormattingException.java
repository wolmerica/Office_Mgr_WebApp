/*
 * FormatterException.java
 *
 * Created on December 02, 2005, 12:15 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package com.wolmerica.tools.formatter;

/**
 *
 */
public class FormattingException extends RuntimeException
{
    private Throwable cause;
    private Formatter formatter;

    public FormattingException(Formatter formatter) {
        this.formatter = formatter;
    }

    public FormattingException(String message) {
        this(message, null);
    }

    public FormattingException(String message, Throwable cause) {
        super(message);
        this.cause = cause;
    }

    public void setCause(Throwable cause) { this.cause = cause; }
    public Throwable getCause() { return cause; }

    public Formatter getFormatter() { return formatter; }
    public void setFormatter(Formatter formatter) {
        this.formatter = formatter;
    }

    public String toString() {
        return super.toString() + "\nOriginal Cause:\n" + cause.toString();
    }

    public void printStackTrace() {
        super.printStackTrace();
        System.out.println("\nOriginal Cause:\n");
        cause.printStackTrace();
    }
}
