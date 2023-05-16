/*
 * PriceTypeAddAction.java
 *
 * Created on August 15, 2005, 8:45 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 * 12/21/2005 Implement tools.formatter library.
 */

package com.wolmerica.pricetype;

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
import java.math.BigDecimal;

import org.apache.log4j.Logger;

public class PriceTypeAddAction extends Action {

  Logger cat = Logger.getLogger("WOWAPP");

  private UserStateService userStateService = new DefaultUserStateService();

  public UserStateService getUserStateService() {
      return userStateService;
  }

  public void setUserStateService(UserStateService userStateService) {
      this.userStateService = userStateService;
  }

  private Byte insertPriceType(HttpServletRequest request, ActionForm form)
    throws Exception, SQLException {

    String user = null;
    DataSource ds = null;
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    Byte ptKey = new Byte("1");

    try {
      PriceTypeDO formDO = (PriceTypeDO) request.getSession().getAttribute("priceTypeDO");

      ds = getDataSource(request);
      conn = ds.getConnection();

      String query = "SELECT name "
                   + "FROM pricetype "
                   + "WHERE name = ? ";
      ps = conn.prepareStatement(query);
      ps.setString(1, formDO.getName());
      rs = ps.executeQuery();

      if (!(rs.next())) {
        // Retrieve the maximum key from the price type table.
        query = "SELECT max(thekey) + 1 AS ptkey "
              + "FROM pricetype ";

        ps = conn.prepareStatement(query);
        rs = ps.executeQuery();

        if (rs.next()) {
          ptKey = rs.getByte("ptkey");
          formDO.setKey(ptKey);
        }
        else {
          throw new Exception("No maximum price type returned");
        }

        query = "INSERT INTO pricetype (thekey,"
              + "name,"
              + "domain_id,"
              + "full_size_id,"
              + "unit_cost_base_id,"
              + "bluebook_id,"
              + "markup_rate,"
              + "precedence,"
              + "service_rate,"
              + "active_id,"
              + "create_user,"
              + "create_stamp,"
              + "update_user,"
              + "update_stamp )"
              + "VALUES (?,?,?,?,?,?,?,?,?,?,CURRENT_TIMESTAMP,?,CURRENT_TIMESTAMP)";
        ps = conn.prepareStatement(query);
        ps.setByte(1, formDO.getKey());
        ps.setString(2, formDO.getName());
        ps.setBoolean(3, formDO.getDomainId());
        ps.setBoolean(4, formDO.getFullSizeId());
        ps.setBoolean(5, formDO.getUnitCostBaseId());
        ps.setBoolean(6, formDO.getBlueBookId());
        ps.setBigDecimal(7, formDO.getMarkUpRate().setScale(2, BigDecimal.ROUND_HALF_UP));
        ps.setByte(8, formDO.getPrecedence());
        ps.setBigDecimal(9, formDO.getServiceRate().setScale(2, BigDecimal.ROUND_HALF_UP));
        ps.setBoolean(10, formDO.getActiveId());
        ps.setString(11, request.getSession().getAttribute("USERNAME").toString());
        ps.setString(12, request.getSession().getAttribute("USERNAME").toString());
        ps.executeUpdate();
      }
      else {
        throw new Exception("The price type name you entered already exists.");
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
    return ptKey;
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

      Byte theKey = null;
      theKey = insertPriceType(request, form);
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