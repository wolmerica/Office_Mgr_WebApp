/*
 * LogisticsEditAction.java
 *
 * Created on November 15, 2008, 05:00 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 *
 */

package com.wolmerica.logistics;

//--------------------------------------------------------------------------------
// @author Richard Wolschlager
//--------------------------------------------------------------------------------
import com.wolmerica.employee.EmployeesActionMapping;
import com.wolmerica.service.property.PropertyService;
import com.wolmerica.service.property.DefaultPropertyService;
import com.wolmerica.service.userstate.UserStateService;
import com.wolmerica.service.userstate.DefaultUserStateService;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import java.util.ArrayList;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;

public class LogisticsEditAction extends Action {

  Logger cat = Logger.getLogger("WOWAPP");

  private PropertyService propertyService = new DefaultPropertyService();
  private UserStateService userStateService = new DefaultUserStateService();

  public PropertyService getPropertyService() {
      return propertyService;
  }

  public void setPropertyService(PropertyService propertyService) {
      this.propertyService = propertyService;
  }

  public UserStateService getUserStateService() {
      return userStateService;
  }

  public void setUserStateService(UserStateService userStateService) {
      this.userStateService = userStateService;
  }

  
  private void updateLogistics(HttpServletRequest request,
                               ActionForm form)
   throws Exception, IOException, SQLException {

    DataSource ds = null;
    Connection conn = null;
    PreparedStatement ps = null;

    try {
      ds = getDataSource(request);
      conn = ds.getConnection();

      LogisticsListHeadDO formHDO = (LogisticsListHeadDO) request.getSession().getAttribute("logisticsHDO");

//--------------------------------------------------------------------------------
// Prepare a query to update the goods and services in work order table.
//--------------------------------------------------------------------------------
      String query = "UPDATE logistics SET "
                   + "shipping_method = ?,"
                   + "tracking_number = ?,"
                   + "note_line1 = ?,"
                   + "update_user = ?,"
                   + "update_stamp = CURRENT_TIMESTAMP "
                   + "WHERE thekey = ?";
      ps = conn.prepareStatement(query);
//--------------------------------------------------------------------------------
// Traverse the work order object.
//--------------------------------------------------------------------------------
      ArrayList logList = formHDO.getLogisticsForm();
      LogisticsDO formDO = null;

//--------------------------------------------------------------------------------
// Iterate through the rows in the logistics list.
//--------------------------------------------------------------------------------
      for (int j=0; j < logList.size(); j++) {
        formDO = (LogisticsDO) logList.get(j);

        if (formDO.getKey() != null) {
//--------------------------------------------------------------------------------
// Perform the normal edit operations for logistics.
//--------------------------------------------------------------------------------
          cat.debug(this.getClass().getName() + ": update " + formDO.getTrackingNumber());            
          ps.setString(1, formDO.getShippingMethod());
          ps.setString(2, formDO.getTrackingNumber());
          ps.setString(3, formDO.getNoteLine1());
          ps.setString(4, request.getSession().getAttribute("USERNAME").toString());
          ps.setInt(5, formDO.getKey());        

          ps.executeUpdate();
        } 
        else {
          if (!(formDO.getTrackingNumber().equalsIgnoreCase(getPropertyService().getCustomerProperties(request,"logistics.default.trackingNumber")))) {
            cat.debug(this.getClass().getName() + ": insert " + formDO.getTrackingNumber());              
            Integer logKey = insertLogistics(conn,
                                             formDO.getSourceTypeKey(),
                                             formDO.getSourceKey(),
                                             formDO.getShippingMethod(),
                                             formDO.getTrackingNumber(),
                                             formDO.getNoteLine1(),
                                             request.getSession().getAttribute("USERNAME").toString());
          }
        }
      }
    }
    catch (SQLException e) {
      cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
    }
    finally {
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
  
  private Integer insertLogistics(Connection conn,
                                  Byte sourceTypeKey,
                                  Integer sourceKey,
                                  String shippingMethod,
                                  String trackingNumber,
                                  String noteLine1,
                                  String userName)
    throws Exception, SQLException {

    PreparedStatement ps = null;
    ResultSet rs = null;

    Integer logKey = 1;

    try {
//--------------------------------------------------------------------------------
// Get the maximum key from the logistics table.
//--------------------------------------------------------------------------------
      String query = "SELECT COUNT(*) AS log_cnt, MAX(thekey)+1 AS log_key "
                   + "FROM logistics";
      ps = conn.prepareStatement(query);
      rs = ps.executeQuery();
      if ( rs.next() ) {
        if ( rs.getInt("log_cnt") > 0 ) {
          logKey = rs.getInt("log_key");
        }
      }
      else {
        throw new Exception("Logistics MAX() not found!");
      }

//--------------------------------------------------------------------------------
// Prepare an insert statement for the logistics table.
//--------------------------------------------------------------------------------
      query = "INSERT INTO logistics "
            + "(thekey,"
            + "sourcetype_key,"
            + "source_key,"
            + "shipping_method,"
            + "tracking_number,"
            + "note_line1,"
            + "create_user,"
            + "create_stamp,"
            + "update_user,"
            + "update_stamp) "
            + "VALUES (?,?,?,?,?,?,"
            + "?,CURRENT_TIMESTAMP,?,CURRENT_TIMESTAMP)";
      ps = conn.prepareStatement(query);

      ps.setInt(1, logKey);
      ps.setByte(2, sourceTypeKey);
      ps.setInt(3, sourceKey);
      ps.setString(4, shippingMethod);
      ps.setString(5, trackingNumber);
      ps.setString(6, noteLine1);
      ps.setString(7, userName);
      ps.setString(8, userName);

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
    }
    return logKey;
  }
  

    @Override
  public ActionForward execute(ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
   throws Exception, SQLException {

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
// The source type key of the attachment owner is required.
//--------------------------------------------------------------------------------
      Byte sourceTypeKey = null;
      if (request.getParameter("sourceTypeKey") != null) {
        sourceTypeKey = new Byte(request.getParameter("sourceTypeKey").toString());
      }
      else {
        throw new Exception("Request getParameter [sourceTypeKey] not found!");
      }

//--------------------------------------------------------------------------------
// The source key of the attachment owner is required.
//--------------------------------------------------------------------------------
      Integer sourceKey = null;
      if (request.getParameter("sourceKey") != null) {
        sourceKey = new Integer(request.getParameter("sourceKey"));
      }
      else {
        throw new Exception("Request getParameter [sourceKey] not found!");
      }
      
//--------------------------------------------------------------------------------
// The source name for which the logistics belong too.
//--------------------------------------------------------------------------------
      String sourceName = null;
      if (request.getParameter("sourceName") != null) {
        sourceName = new String(request.getParameter("sourceName"));
      }
      else {
        throw new Exception("Request getParameter [sourceName] not found!");
      }      

//--------------------------------------------------------------------------------
// 07-23-06  Code to handle permissions and avoid simultaneous updates to records.
//--------------------------------------------------------------------------------
      
      String usToken = getUserStateService().getUserToken(request,
                                              this.getDataSource(request).getConnection(),
                                              this.getClass().getName(),
                                              sourceKey);
      if (usToken.equalsIgnoreCase(getUserStateService().getLocked()))
        request.setAttribute(getUserStateService().getDisableEdit(), false);
      else if (usToken.equalsIgnoreCase(getUserStateService().getProhibited()))
        throw new Exception(getUserStateService().getAccessDenied());

      updateLogistics(request,form);
      
      request.setAttribute("sourceTypeKey", sourceTypeKey.toString());
      request.setAttribute("sourceKey", sourceKey.toString());      
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