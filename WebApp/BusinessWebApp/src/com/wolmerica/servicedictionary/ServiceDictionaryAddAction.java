/*
 * ServiceDictionaryAddAction.java
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

public class ServiceDictionaryAddAction extends Action {

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


  private HashMap<String, Object> insertServiceDictionary(HttpServletRequest request,
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
//===========================================================================
// Get the maximum key from the service table.
//===========================================================================
        String query = "SELECT COUNT(*) AS service_cnt, MAX(thekey)+1 AS service_key "
                     + "FROM servicedictionary";
        ps = conn.prepareStatement(query);
        rs = ps.executeQuery();
        if ( rs.next() ) {
          if ( rs.getInt("service_cnt") > 0 ) 
             serviceKey = rs.getInt("service_key");
          else
             serviceKey = 1;
        }
        else {
          throw new Exception("Service Dictionary MAX() not found!");
        }

//--------------------------------------------------------------------------------
// Information used for debugging.
//--------------------------------------------------------------------------------
        cat.debug(this.getClass().getName() + ": serviceKey..: " + serviceKey);
        cat.debug(this.getClass().getName() + ": name........: " + formDO.getServiceName());
        cat.debug(this.getClass().getName() + ": category....: " + formDO.getServiceCategory());
        cat.debug(this.getClass().getName() + ": number......: " + formDO.getServiceNum());
        cat.debug(this.getClass().getName() + ": description.: " + formDO.getServiceDescription());
        cat.debug(this.getClass().getName() + ": other.......: " + formDO.getOther());
        cat.debug(this.getClass().getName() + ": releaseId...: " + formDO.getReleaseId());
        cat.debug(this.getClass().getName() + ": custTypeKey.: " + formDO.getServiceNum());
        cat.debug(this.getClass().getName() + ": durHours....: " + formDO.getDurationHours());
        cat.debug(this.getClass().getName() + ": durMinutes..: " + formDO.getDurationMinutes());
        cat.debug(this.getClass().getName() + ": vendorKey...: " + formDO.getVendorKey());

//--------------------------------------------------------------------------------
// Set the profileNum value to the serviceKey when equals "New"
//--------------------------------------------------------------------------------
        if (formDO.getProfileNum().equalsIgnoreCase("New"))
          formDO.setProfileNum(serviceKey.toString());

//--------------------------------------------------------------------------------
// Prepare insert statement to the servicedictionary.
//--------------------------------------------------------------------------------
        query = "INSERT INTO servicedictionary "
              + "(thekey,"
              + "name,"
              + "category,"
              + "service_num,"
              + "description,"
              + "profile_num,"
              + "other,"
              + "release_id,"
              + "pricetype_key,"
              + "billable_id,"
              + "customertype_key,"
              + "duration_hours,"
              + "duration_minutes,"
              + "labor_cost,"
              + "service_cost,"
              + "markup1_rate,"
              + "markup2_rate,"
              + "fee1_cost,"
              + "fee2_cost,"
              + "vendor_key,"
              + "vendor_specific_id,"
              + "create_user,"
              + "create_stamp,"
              + "update_user,"
              + "update_stamp) "
              + "VALUES(?,?,?,?,?,?,?,?,?,?,"
              + "?,?,?,?,?,?,?,?,?,?,"
              + "?,?,CURRENT_TIMESTAMP,?,CURRENT_TIMESTAMP)";
        cat.debug(this.getClass().getName() + ": query...: " + query);
        ps = conn.prepareStatement(query);
//--------------------------------------------------------------------------------
// Insert new record into the servicedictionary table.
//--------------------------------------------------------------------------------
        ps.setInt(1, serviceKey);
        ps.setString(2, formDO.getServiceName());
        ps.setString(3, formDO.getServiceCategory());
        ps.setString(4, formDO.getServiceNum());
        ps.setString(5, formDO.getServiceDescription());
        ps.setString(6, formDO.getProfileNum());
        ps.setString(7, formDO.getOther());
        ps.setBoolean(8, formDO.getReleaseId());
        ps.setByte(9, formDO.getPriceTypeKey());
        ps.setBoolean(10, formDO.getBillableId());
        ps.setByte(11, formDO.getCustomerTypeKey());
        ps.setByte(12, formDO.getDurationHours());
        ps.setByte(13, formDO.getDurationMinutes());
        ps.setBigDecimal(14, formDO.getLaborCost().setScale(2, BigDecimal.ROUND_HALF_UP));
        ps.setBigDecimal(15, formDO.getServiceCost().setScale(2, BigDecimal.ROUND_HALF_UP));
        ps.setBigDecimal(16, formDO.getMarkUp1Rate().setScale(2, BigDecimal.ROUND_HALF_UP));
        ps.setBigDecimal(17, formDO.getMarkUp2Rate().setScale(2, BigDecimal.ROUND_HALF_UP));
        ps.setBigDecimal(18, formDO.getFee1Cost().setScale(2, BigDecimal.ROUND_HALF_UP));
        ps.setBigDecimal(19, formDO.getFee2Cost().setScale(2, BigDecimal.ROUND_HALF_UP));
        ps.setInt(20, formDO.getVendorKey());
        ps.setBoolean(21, formDO.getVendorSpecificId());
        ps.setString(22, request.getSession().getAttribute("USERNAME").toString());
        ps.setString(23, request.getSession().getAttribute("USERNAME").toString());
        ps.executeUpdate();
        cat.debug(this.getClass().getName() + ": serviceKey.......: " + serviceKey);
        serviceMap.put("serviceKey", serviceKey);
      }
    }
    catch (SQLException e) {
      cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
      throw new Exception("insertServiceDictionary() " + e.getMessage());
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
      HashMap serviceMap = insertServiceDictionary(request, form);

      if (serviceMap.containsKey("webServiceStatus")) {
        cat.debug("webServiceStatus...: " + serviceMap.get("webServiceStatus"));
        target = "alert";
        request.setAttribute("popupMessage", serviceMap.get("webServiceStatus"));
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
        else
        {
          request.setAttribute("key", theKey.toString());
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