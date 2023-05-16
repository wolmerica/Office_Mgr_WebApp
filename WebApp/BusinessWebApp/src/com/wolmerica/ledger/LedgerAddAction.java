/*
 * LedgerAddAction.java
 *
 * Created on August 15, 2005, 8:45 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 * 12/22/2005 Implement tools.formatter library.
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
import java.sql.Timestamp;
import java.util.Date;

import org.apache.log4j.Logger;

public class LedgerAddAction extends Action {

  Logger cat = Logger.getLogger("WOWAPP");

  private UserStateService userStateService = new DefaultUserStateService();

  public UserStateService getUserStateService() {
      return userStateService;
  }

  public void setUserStateService(UserStateService userStateService) {
      this.userStateService = userStateService;
  }
  

  private Integer insertLedger(HttpServletRequest request, ActionForm form)
    throws Exception {

    String user = null;
    DataSource ds = null;
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    Timestamp lTimestamp = new Timestamp(new Date().getTime());
    Integer idKey = 1;

    try {
      ds = getDataSource(request);
      conn = ds.getConnection();

      LedgerDO formDO = (LedgerDO) request.getSession().getAttribute("ledgerDO");

      String query = "INSERT INTO ledger "
                   + "(thekey,"
                   + "customer_invoice_key,"
                   + "customer_key,"
                   + "invoice_num,"
                   + "invoice_total,"
                   + "post_stamp,"
                   + "slip_num,"
                   + "archived_id,"
                   + "create_user,"
                   + "create_stamp,"
                   + "update_user,"
                   + "update_stamp )"
                   + "VALUES (NULL,?,?,?,?,?,?,?,?,?,?,?)";

      ps = conn.prepareStatement(query);

      ps.setInt(1, formDO.getCustomerInvoiceKey());
      ps.setInt(2, formDO.getCustomerKey());
      ps.setString(3, formDO.getInvoiceNumber());
      ps.setBigDecimal(4, formDO.getInvoiceTotal());
      ps.setTimestamp(5, formDO.getPostStamp());
      ps.setString(6, formDO.getSlipNumber());
      ps.setByte(7, formDO.getArchivedId());
      ps.setString(8, request.getSession().getAttribute("USERNAME").toString());
      ps.setTimestamp(9, lTimestamp);
      ps.setString(10, request.getSession().getAttribute("USERNAME").toString());
      ps.setTimestamp(11, lTimestamp);

      ps.executeUpdate();

      // Retrieve the newly inserted key from the pet table.
      query = "SELECT thekey "
            + "FROM ledger "
            + "WHERE create_stamp = ?";

      ps = conn.prepareStatement(query);
      ps.setTimestamp(1, lTimestamp);
      rs = ps.executeQuery();

      if ( rs.next() ) {
        idKey = rs.getInt("thekey");
      }
      else {
        throw new Exception("No ledger with createStamp: "+ lTimestamp.toString() );
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
    return idKey;
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

      Integer theKey = null;
      theKey = insertLedger(request, form);
      request.setAttribute("key", theKey.toString());
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