package com.wolmerica.service.userstate;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.sql.Timestamp;
import java.util.Date;

import org.apache.log4j.Logger;

/**
 * @author  Richard Wolschlager
 * @date    Sept 4, 2010
 */

public class DefaultUserStateService implements UserStateService {

  Logger cat = Logger.getLogger("CUSTAPP");

  private final String ACCTKEY = "ACCTKEY";
  private final String ACCTNUM = "ACCTNUM";
  private final String ACCTNAME = "ACCTNAME";
  private final String MULTIACCT = "MULTIACCT";
  private final String DEFAULTINSTANCENAME = "DEFAULTINSTANCENAME";
  private final String INSTANCENAME = "INSTANCENAME";

  public String getAcctKey() {
    return ACCTKEY;
  }

  public String getAcctNum() {
    return ACCTNUM;
  }

  public String getAcctName() {
    return ACCTNAME;
  }

  public String getMultiAcct() {
    return MULTIACCT;
  }

  public String getDefaultInstanceName() {
    return DEFAULTINSTANCENAME;
  }

  public String getInstanceName() {
    return INSTANCENAME;
  }


//--------------------------------------------------------------------------------
// Author: Richard Wolschlager
// Date..: 09/02/2007
// Method: SessionAttributeList
// 1) HttpServletRequest
//--------------------------------------------------------------------------------
  public void SessionAttributeCleanUp(HttpServletRequest request)
   throws Exception {

   cat.debug(request.getSession().getAttribute(this.INSTANCENAME) + "|"
          + request.getRequestURI().substring(request.getRequestURI().lastIndexOf("/")+1) + "|"
          + request.getSession().getAttribute(this.ACCTNUM) + "|"
          + "|" + new Timestamp(new Date().getTime()).toString());
//--------------------------------------------------------------------------------
// Traverse through the session attributes
//--------------------------------------------------------------------------------
    Enumeration attNames = request.getSession().getAttributeNames();
    String attName = "";
    while (attNames.hasMoreElements()) {
      attName = (String) attNames.nextElement();
      if (attName.equalsIgnoreCase(this.ACCTKEY) ||
         attName.equalsIgnoreCase(this.ACCTNUM) ||
         attName.equalsIgnoreCase(this.ACCTNAME) ||
         attName.equalsIgnoreCase(this.MULTIACCT) ||
         attName.equalsIgnoreCase(this.DEFAULTINSTANCENAME) ||
         attName.equalsIgnoreCase(this.INSTANCENAME) ||
         attName.contains("org")) {
         cat.debug(this.getClass().getName() + " Keep attribute...: " + attName + " value of " + request.getSession().getAttribute(attName).toString());
      } else {
         cat.debug(this.getClass().getName() + " Remove attribute...: " + attName);
         request.getSession().removeAttribute(attName);
      }
    }
  }

//--------------------------------------------------------------------------------
// Author: Richard Wolschlager
// Date..: 09/07/2007
// Method: SessionInstanceValidate
// 1) HttpServletRequest
// Prohibit the user from crossing instances with their user session.  For example
// a user who logged in to the "lindaw" instance should not be able to access the
// "wolmerica" instance.  The user will only be able to access the instance from
// which the login had taken place.  An error message will be displayed otherwise.
//--------------------------------------------------------------------------------
  public void SessionInstanceValidate(HttpServletRequest request)
   throws Exception {

    String reqInstanceName = request.getRequestURI();
    int sp = reqInstanceName.indexOf("WebApp/")+7;
    int ep = reqInstanceName.length();
    reqInstanceName = reqInstanceName.substring(sp,ep);
    ep = reqInstanceName.indexOf("/");
    if (ep > 0)
      reqInstanceName = reqInstanceName.substring(0,ep);
    else
      reqInstanceName = request.getSession().getAttribute(this.DEFAULTINSTANCENAME).toString();

    String sesInstanceName = request.getSession().getAttribute(this.INSTANCENAME).toString();
    if (sesInstanceName.compareTo(reqInstanceName) != 0)
      throw new Exception("Invalid session instance - click the browser back button to continue...");
  }
}