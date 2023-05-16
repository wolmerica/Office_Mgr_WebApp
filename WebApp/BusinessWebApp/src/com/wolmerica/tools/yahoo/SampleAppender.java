/*
 * SampleAppender.java
 *
 * Created on July 31, 2007, 7:15 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package com.wolmerica.tools.yahoo;

//--------------------------------------------------------------------------------
// @author Richard Wolschlager
//--------------------------------------------------------------------------------
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;

public class SampleAppender extends AppenderSkeleton {

  protected void append(LoggingEvent event) {
       System.out.println("--> " + event.getMessage() + " <--");
  }

  public boolean requiresLayout() {
    return false;
  }

  public void close() {}

}
