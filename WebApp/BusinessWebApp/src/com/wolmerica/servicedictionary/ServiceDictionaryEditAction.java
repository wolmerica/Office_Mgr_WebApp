/*
 * ServiceDictionaryEditAction.java
 *
 * Created on June 20, 2006, 07:58 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 * 12/21/2005 Implement tools.formatter library.
 */


package com.wolmerica.servicedictionary;

//--------------------------------------------------------------------------------
// @author Richard Wolschlager
//--------------------------------------------------------------------------------
import com.wolmerica.employee.EmployeesActionMapping;
import com.wolmerica.service.itemandsrv.ItemAndSrvService;
import com.wolmerica.service.itemandsrv.DefaultItemAndSrvService;
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
import java.util.HashMap;

import org.apache.log4j.Logger;

public class ServiceDictionaryEditAction extends Action {

  Logger cat = Logger.getLogger("WOWAPP");

  private ItemAndSrvService itemAndSrvService = new DefaultItemAndSrvService();
  private UserStateService userStateService = new DefaultUserStateService();

  public ItemAndSrvService getItemAndSrvService() {
      return itemAndSrvService;
  }

  public void setItemAndSrvService(ItemAndSrvService itemAndSrvService) {
      this.itemAndSrvService = itemAndSrvService;
  }

  public UserStateService getUserStateService() {
      return userStateService;
  }

  public void setUserStateService(UserStateService userStateService) {
      this.userStateService = userStateService;
  }



  private HashMap<String, Object> updateServiceDictionary(HttpServletRequest request,
                                          ActionForm form)
    throws Exception, SQLException {

    DataSource ds = null;
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;
    HashMap<String, Object> serviceMap = new HashMap<String, Object>();

    try {
      ServiceDictionaryDO formDO = (ServiceDictionaryDO) request.getSession().getAttribute("servicedictionaryDO");

      ds = getDataSource(request);
      conn = ds.getConnection();

//--------------------------------------------------------------------------------
// Check the service dictionary table for any duplicate item_num and profile_num values.
//--------------------------------------------------------------------------------
      serviceMap = getItemAndSrvService().GetDupServiceDictionary(request,
                                                                  conn,
                                                                  formDO);
      Integer serviceKey = new Integer(serviceMap.get("serviceKey").toString());
      cat.debug(this.getClass().getName() + ": GetDupServiceDictionary().: " + serviceKey);
      if (serviceKey >= 0) {

        String query = "UPDATE servicedictionary SET "
                     + "name=?,"
                     + "category=?,"
                     + "service_num=?,"
                     + "description=?,"
                     + "profile_num=?,"
                     + "other=?,"
                     + "release_id=?,"
                     + "pricetype_key=?,"
                     + "billable_id=?,"
                     + "customertype_key=?,"
                     + "duration_hours=?,"
                     + "duration_minutes=?,"
                     + "labor_cost=?,"
                     + "service_cost=?,"
                     + "markup1_rate=?,"
                     + "markup2_rate=?,"
                     + "fee1_cost=?,"
                     + "fee2_cost=?,"
                     + "vendor_key=?,"
                     + "vendor_specific_id=?,"
                     + "update_user=?,"
                     + "update_stamp=CURRENT_TIMESTAMP "
                     + "WHERE thekey=?";
        ps = conn.prepareStatement(query);
        ps.setString(1, formDO.getServiceName());
        ps.setString(2, formDO.getServiceCategory());
        ps.setString(3, formDO.getServiceNum());
        ps.setString(4, formDO.getServiceDescription());
        ps.setString(5, formDO.getProfileNum());
        ps.setString(6, formDO.getOther());
        ps.setBoolean(7, formDO.getReleaseId());
        ps.setByte(8, formDO.getPriceTypeKey());
        ps.setBoolean(9, formDO.getBillableId());
        ps.setByte(10, formDO.getCustomerTypeKey());
        ps.setByte(11, formDO.getDurationHours());
        ps.setByte(12, formDO.getDurationMinutes());
        ps.setBigDecimal(13, formDO.getLaborCost().setScale(2, BigDecimal.ROUND_HALF_UP));
        ps.setBigDecimal(14, formDO.getServiceCost().setScale(2, BigDecimal.ROUND_HALF_UP));
        ps.setBigDecimal(15, formDO.getMarkUp1Rate().setScale(2, BigDecimal.ROUND_HALF_UP));
        ps.setBigDecimal(16, formDO.getMarkUp2Rate().setScale(2, BigDecimal.ROUND_HALF_UP));
        ps.setBigDecimal(17, formDO.getFee1Cost().setScale(2, BigDecimal.ROUND_HALF_UP));
        ps.setBigDecimal(18, formDO.getFee2Cost().setScale(2, BigDecimal.ROUND_HALF_UP));
        ps.setInt(19, formDO.getVendorKey());
        ps.setBoolean(20, formDO.getVendorSpecificId());
        ps.setString(21, request.getSession().getAttribute("USERNAME").toString());
        ps.setInt(22, formDO.getKey());
        ps.executeUpdate();
        cat.debug(this.getClass().getName() + ": serviceKey.......: " + serviceKey);
        serviceMap.put("serviceKey", serviceKey);
      }
    }
    catch (SQLException e) {
      cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
      throw new Exception("updateServiceDictionary() " + e.getMessage());
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
    return serviceMap;
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
      Integer theKey = null;
      if (!(request.getParameter("key") == null)) {
        theKey = new Integer(request.getParameter("key"));
      }
      else {
        if (!(request.getAttribute("key") == null)) {
          theKey = new Integer(request.getAttribute("key").toString());
        }
        else {
          throw new Exception("Request getParameter/getAttribute [key] not found!");
        }
      }
      cat.debug(this.getClass().getName() + ": get[key] = " + theKey.toString());

//--------------------------------------------------------------------------------
// 07-23-06  Code to handle permissions and avoid simultaneous updates to records.
//--------------------------------------------------------------------------------
      
      String usToken = getUserStateService().getUserToken(request,
                                              this.getDataSource(request).getConnection(),
                                              this.getClass().getName(),
                                              theKey);
      if (usToken.equalsIgnoreCase(getUserStateService().getLocked()))
        request.setAttribute(getUserStateService().getDisableEdit(), false);
      else if (usToken.equalsIgnoreCase(getUserStateService().getProhibited()))
        throw new Exception(getUserStateService().getAccessDenied());

      HashMap serviceMap = updateServiceDictionary(request, form);

      if (serviceMap.containsKey("webServiceStatus")) {
        cat.debug("webServiceStatus...: " + serviceMap.get("webServiceStatus"));
        target = "alert";
        request.setAttribute("popupMessage", serviceMap.get("webServiceStatus").toString());
      } else {
//--------------------------------------------------------------------------------
// A service dictionary key of -10 indicates an invalid service num.
//--------------------------------------------------------------------------------
        theKey = new Integer(serviceMap.get("serviceKey").toString());
        if (theKey < 0) {
          target = "invalid";
          ActionMessages errors = new ActionMessages();
          if (theKey == -1)
            errors.add("serviceName", new ActionMessage("errors.duplicate"));
          else if (theKey == -2)
            errors.add("serviceNum", new ActionMessage("errors.duplicate"));
          else if (theKey == -3)
            errors.add("profileNum", new ActionMessage("errors.duplicate"));
          else if (theKey == -10)
            errors.add("serviceNum", new ActionMessage("errors.invalid"));
        saveErrors(request, errors);
        }
      }
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
