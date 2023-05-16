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
import ymsg.network.Session;
import org.apache.log4j.Logger;

public class YahooMessengerAPI {

  Logger cat = Logger.getLogger("WOWAPP");

  public String sendYahooIM(String FromYahooId,
                            String FromYahooIdPassword,
                            String ToYahooId,
                            String msg) {
    String errMsg = "";
    
    try {
//--------------------------------------------------------------------------------
//Step 1: Create a Session Object
//--------------------------------------------------------------------------------
      Session _session = new Session();

//--------------------------------------------------------------------------------
//Step 2: Login to the From Yahoo Account
//--------------------------------------------------------------------------------
      _session.login(FromYahooId,FromYahooIdPassword);

//--------------------------------------------------------------------------------
//Step 3: Send the Message
//--------------------------------------------------------------------------------
      _session.sendMessage(ToYahooId,msg);

//--------------------------------------------------------------------------------
//Step 4: Logout from the Session
//--------------------------------------------------------------------------------
      _session.logout();
    }
    catch (Exception e) {
      errMsg = e.getMessage();
      cat.debug(this.getClass().getName() + ": Exception : " + e.getMessage());
    }
  return errMsg;
  }
}