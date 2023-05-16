/*
 * YahooMessengerAPI.java
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

public class YahooMessengerAppender extends AppenderSkeleton {

  private String fromYahooId;
  private String fromYahooIdPassword;
  private String toYahooId;

  protected void append(LoggingEvent event) {
     String errMsg =  new  YahooMessengerAPI().sendYahooIM(getFromYahooId(),
                                                           getFromYahooIdPassword(),
                                                           getToYahooId(),
                                                           (String)event.getMessage());
  }

  public void close() {}

  public boolean requiresLayout() {
    return false;
  }

  public void setFromYahooId(String fromYahooId) {
    this.fromYahooId = fromYahooId;
  }

  public String getFromYahooId() {
    return fromYahooId;
  }

  public void setFromYahooIdPassword(String fromYahooIdPassword) {
    this.fromYahooIdPassword = fromYahooIdPassword;
  }

  public String getFromYahooIdPassword() {
    return fromYahooIdPassword;
  }

  public void setToYahooId(String toYahooId) {
    this.toYahooId = toYahooId;
  }

  public String getToYahooId() {
    return toYahooId;
  }
}
