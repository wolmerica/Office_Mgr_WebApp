/*
 * LedgerSlipAction.java
 *
 * Created on September 28, 2005, 8:30 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package com.wolmerica.ledger;

//--------------------------------------------------------------------------------
// @author Richard Wolschlager
//--------------------------------------------------------------------------------
import com.wolmerica.employee.EmployeesActionMapping;
import com.wolmerica.service.userstate.UserStateService;
import com.wolmerica.service.userstate.DefaultUserStateService;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;

import org.apache.log4j.Logger;

public class LedgerSlipAction extends Action {

  Logger cat = Logger.getLogger("WOWAPP");

  private UserStateService userStateService = new DefaultUserStateService();

  public UserStateService getUserStateService() {
      return userStateService;
  }

  public void setUserStateService(UserStateService userStateService) {
      this.userStateService = userStateService;
  }
  

  private void setLedgerSlip(HttpServletRequest request,
                               String ledgerSlip)
    throws Exception, SQLException {

    DataSource ds = null;
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    int slipCount = 0;
    String ledgerKey = "";
    String customerKey = "";
    String lastName = "";
    String stName = "";
    String slip = "";

    try {
      ds = getDataSource(request);
      conn = ds.getConnection();

      String query = "SELECT ledger.thekey, "
                   + "customer_key, last_name "
                   + "FROM ledger, customer "
                   + "WHERE customer_key = customer.thekey "
                   + "AND YEAR(post_stamp) >= 1990 "
                   + "AND slip_num IS NULL "
                   + "ORDER by customer_key ";

      ps = conn.prepareStatement(query);
      rs = ps.executeQuery();

      while ( rs.next() ) {
        cat.debug(this.getClass().getName() + ": LOOP = " + rs.getString("thekey"));
        ledgerKey = rs.getString("thekey");
        customerKey = rs.getString("customer_key");
        lastName = rs.getString("last_name");

        if (stName.equalsIgnoreCase(lastName)) {
        }
        else
        {
	stName = lastName;
/*** Calculate a slip number using name, calendar, count ***/
        slip = calculateSlip(lastName, ++slipCount);
        }
/*** Assign the slip number to the ledger record ***/
        updateSlip(request, ledgerKey, slip);
      }
    }
    catch (SQLException e) {
      cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
    }
    finally {
      if (rs != null) {
        try {
          rs.close();
        }
        catch (SQLException sqle) {
          cat.error(this.getClass().getName() + ": SQLException : " + sqle.getMessage());
        }
        rs = null;
      }
      if (ps != null) {
        try {
          ps.close();
        }
        catch (SQLException sqle) {
            cat.error(this.getClass().getName() + ": SQLException : " + sqle.getMessage());
        }
        ps = null;
      }
      if (conn != null) {
        try {
          conn.close();
        }
        catch (SQLException sqle) {
            cat.error(this.getClass().getName() + ": SQLException : " + sqle.getMessage());
        }
        conn = null;
      }
    }
  }

  private void updateSlip(HttpServletRequest request,
                            String ledgerKey,
                            String ledgerSlip)
    throws Exception, SQLException {

    DataSource ds = null;
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    try {
      ds = getDataSource(request);
      conn = ds.getConnection();

      String query = "UPDATE ledger "
                   + "SET slip_num = ?,"
                   + "update_user = ?,"
                   + "update_stamp = CURRENT_TIMESTAMP "
                   + "WHERE thekey = ?";

      ps = conn.prepareStatement(query);

      ps.setString(1, ledgerSlip);
      ps.setString(2, request.getSession().getAttribute("USERNAME").toString());
      ps.setInt(3, new Integer(ledgerKey));

      ps.executeUpdate();
    }
    catch (SQLException e) {
      cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
    }
    finally {
      if (rs != null) {
        try {
          rs.close();
        }
        catch (SQLException sqle) {
          cat.error(this.getClass().getName() + ": SQLException : " + sqle.getMessage());
        }
        rs = null;
      }
      if (ps != null) {
        try {
          ps.close();
        }
        catch (SQLException sqle) {
            cat.error(this.getClass().getName() + ": SQLException : " + sqle.getMessage());
        }
        ps = null;
      }
      if (conn != null) {
        try {
          conn.close();
        }
        catch (SQLException sqle) {
            cat.error(this.getClass().getName() + ": SQLException : " + sqle.getMessage());
        }
        conn = null;
      }
    }
  }

  private String calculateSlip(String name,
                               int slipCount)
    throws Exception {

/*** ASCII value of the first letter of the last name ***/
    char aChar= name.charAt(0);
    int charVal = aChar;
//-------------------------------------------------------------------
// Use the Calendar methods to find the current month, day, and year
//-------------------------------------------------------------------
    Calendar now = Calendar.getInstance();
    Integer myYear = now.get(Calendar.YEAR);
    Integer myMonth = now.get(Calendar.MONTH) + 1;
    Integer myDay = now.get(Calendar.DATE);
//-------------------------------------------------------------------
// Convert the year, month and day values to strings.
//-------------------------------------------------------------------
    String postYear = myYear.toString().substring(2,3);
    String postMonth = myMonth.toString();
    String postDay = myDay.toString();
    if (myMonth<10)
      postMonth = "0" + postMonth;
    if (myDay<10)
      postDay = "0" + postDay;
//-------------------------------------------------------------------
// Construct slip with year, month, ASCII, day, and Count
// Add the last two digits of the year to the front of the slip number.
//-------------------------------------------------------------------
    String slip = "";
    slip = slip + postYear + postMonth + "-";
    if (charVal < 10)
      slip=slip+"0"+charVal;
    else
      slip =slip+charVal;
    slip=slip+postDay;
    if (slipCount < 100)
      slip=slip+"0";
    if (slipCount < 10)
      slip=slip+"0"+slipCount;
    else
      slip=slip+slipCount;

    cat.debug(this.getClass().getName() + ": ledger slip =" + slip);
    return slip;
  }

    @Override
  public ActionForward execute(ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws IOException, ServletException {

// Default target to success
    String target = "success";

    EmployeesActionMapping employeesMapping =
      (EmployeesActionMapping)mapping;

//--------------------------------------------------------------------------------
// Does this action require the user to login.
//--------------------------------------------------------------------------------
    if ( employeesMapping.isLoginRequired() ) {

      if ( request.getSession().getAttribute("USER") == null ) {
//--------------------------------------------------------------------------------
// The user is not logged in.
//--------------------------------------------------------------------------------
        target = "login";
        ActionMessages actionMessages = new ActionMessages();

        actionMessages.add(ActionMessages.GLOBAL_MESSAGE,
          new ActionMessage("errors.login.required"));
//--------------------------------------------------------------------------------
// Report any ActionMessages we have discovered back to the original form.
//--------------------------------------------------------------------------------
        if (!actionMessages.isEmpty()) {
          saveMessages(request, actionMessages);
        }
//--------------------------------------------------------------------------------
// Forward to the request to the login screen.
//--------------------------------------------------------------------------------
        return (mapping.findForward(target));
      }
    }

    try {
      String ledgerSlip = null;

      if (request.getParameter("slip") == null) {
        cat.debug(this.getClass().getName() + ": slip = NULL");
      }
      else {
        ledgerSlip = new String(request.getParameter("slip"));
        cat.debug(this.getClass().getName() + ": slip = " + ledgerSlip);
      }

//--------------------------------------------------------------------------------
// 07-23-06  Code to handle permissions and avoid simultaneous updates to records.
//--------------------------------------------------------------------------------
      
      String usToken = getUserStateService().getUserToken(request,
                                              this.getDataSource(request).getConnection(),
                                              this.getClass().getName(),
                                              getUserStateService().getNoKey());
      if (usToken.equalsIgnoreCase(getUserStateService().getLocked()))
        request.setAttribute(getUserStateService().getDisableEdit(), false);
      else if (usToken.equalsIgnoreCase(getUserStateService().getProhibited()))
        throw new Exception(getUserStateService().getAccessDenied());

      setLedgerSlip(request, ledgerSlip);
    }
    catch (Exception e) {
      cat.error(this.getClass().getName() + ": Exception : " + e.getMessage());
      target = "error";
      ActionMessages actionMessages = new ActionMessages();
      actionMessages.add(ActionMessages.GLOBAL_MESSAGE,
        new ActionMessage("errors.database.error", e.getMessage()));
//--------------------------------------------------------------------------------
// Report any ActionMessages
//--------------------------------------------------------------------------------
      if (!actionMessages.isEmpty()) {
        saveMessages(request, actionMessages);
      }
    }
//--------------------------------------------------------------------------------
// Forward to the appropriate View
//--------------------------------------------------------------------------------
    return (mapping.findForward(target));
  }
}
