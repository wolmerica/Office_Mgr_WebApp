/*
 * ServiceDictionaryEntryAction.java
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
import com.wolmerica.service.vendor.VendorService;
import com.wolmerica.service.vendor.DefaultVendorService;
import com.wolmerica.service.userstate.UserStateService;
import com.wolmerica.service.userstate.DefaultUserStateService;
import com.wolmerica.tools.formatter.FormattingException;
import com.wolmerica.customertype.CustomerTypeDropList;
import com.wolmerica.pricetype.PriceTypeDropList;

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
import java.util.ArrayList;
import java.util.Enumeration;

import org.apache.log4j.Logger;

public class ServiceDictionaryEntryAction extends Action {

  Logger cat = Logger.getLogger("WOWAPP");

  private UserStateService userStateService = new DefaultUserStateService();
  private VendorService VendorService = new DefaultVendorService();

  public UserStateService getUserStateService() {
      return userStateService;
  }

  public void setUserStateService(UserStateService userStateService) {
      this.userStateService = userStateService;
  }

  public VendorService getVendorService() {
      return VendorService;
  }

  public void setVendorService(VendorService VendorService) {
      this.VendorService = VendorService;
  }

  private ServiceDictionaryDO buildServiceDictionaryForm(HttpServletRequest request,
                                                         ServiceDictionaryDO formDO,
                                                         Integer sdKey)
    throws Exception, SQLException {

    ArrayList customerTypeRows = new ArrayList();
    ArrayList priceTypeRows = new ArrayList();

    DataSource ds = null;
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    try {
//--------------------------------------------------------------------------------
// Initialize the profileNum value to "New"
//--------------------------------------------------------------------------------
      formDO.setProfileNum(new String("New"));

      ds = getDataSource(request);
      conn = ds.getConnection();

      if (sdKey != null) {
        String query = "SELECT thekey,"
                     + "name,"
                     + "category,"
                     + "description,"
                     + "customertype_key,"
                     + "duration_hours,"
                     + "duration_minutes,"
                     + "labor_cost,"
                     + "service_cost,"
                     + "fee1_cost,"
                     + "fee2_cost,"
                     + "markup1_rate,"
                     + "markup2_rate,"
                     + "vendor_key,"
                     + "create_user,"
                     + "create_stamp,"
                     + "update_user,"
                     + "update_stamp "
                     + "FROM servicedictionary "
                     + "WHERE thekey=?";
        ps = conn.prepareStatement(query);
        ps.setInt(1, sdKey);
        rs = ps.executeQuery();
        if (rs.next()) {
//        formDO.setKey(sdKey);
//        formDO.setServiceName(rs.getString("name"));
          formDO.setServiceCategory(rs.getString("category"));
//        formDO.setServiceDescription(rs.getString("description"));
          formDO.setCustomerTypeKey(rs.getByte("customertype_key"));
          formDO.setDurationHours(rs.getByte("duration_hours"));
          formDO.setDurationMinutes(rs.getByte("duration_minutes"));
          formDO.setLaborCost(rs.getBigDecimal("labor_cost"));
          formDO.setServiceCost(rs.getBigDecimal("service_cost"));
          formDO.setFee1Cost(rs.getBigDecimal("fee1_cost"));
          formDO.setFee2Cost(rs.getBigDecimal("fee2_cost"));
          formDO.setMarkUp1Rate(rs.getBigDecimal("markup1_rate"));
          formDO.setMarkUp2Rate(rs.getBigDecimal("markup2_rate"));
          formDO.setVendorKey(rs.getInt("vendor_key"));
//        formDO.setCreateUser(rs.getString("create_user"));
//        formDO.setCreateStamp(rs.getTimestamp("create_stamp"));
//        formDO.setUpdateUser(rs.getString("update_user"));
//        formDO.setUpdateStamp(rs.getTimestamp("update_stamp"));
          if (request.getParameter("serviceNameFilter") != null)
            formDO.setServiceNameFilter(request.getParameter("serviceNameFilter"));
          if (request.getParameter("serviceNumFilter") != null)
            formDO.setServiceNumFilter(request.getParameter("serviceNumFilter"));
          if (request.getParameter("categoryNameFilter") != null)
            formDO.setCategoryNameFilter(request.getParameter("categoryNameFilter"));
          if (request.getParameter("sdPageNo") != null)
            formDO.setCurrentPage(new Integer(request.getParameter("sdPageNo")));
//--------------------------------------------------------------------------------
// Get the name of the preferred vendor for the item.
//--------------------------------------------------------------------------------
          if (formDO.getVendorKey() != null) {
            formDO.setVendorName(getVendorService().getVendorName(conn,
                                                  formDO.getVendorKey()));
          }
        }
        else {
          throw new Exception("ServiceDictionary  " + sdKey.toString() + " not found!");
        }
      }
//--------------------------------------------------------------------------------
// Retrieve the active customer type values available to assign to a service
//--------------------------------------------------------------------------------
      CustomerTypeDropList ctdl = new CustomerTypeDropList();
      customerTypeRows = ctdl.getActiveCustomerTypeList(conn);
      formDO.setCustomerTypeForm(customerTypeRows);

//--------------------------------------------------------------------------------
// Retrieve the active service related price type values.
//--------------------------------------------------------------------------------
      PriceTypeDropList ptdl = new PriceTypeDropList();
      priceTypeRows = ptdl.getServicePriceTypeList(conn);
      formDO.setPriceTypeForm(priceTypeRows);

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
    return formDO;
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
// Traverse the request parameters and populate the appropriate values.
//--------------------------------------------------------------------------------
      Integer theKey = null;
      String serviceNum = "";
      String serviceName = "";
      Integer vendorKey = null;
      String vendorName = "";
      cat.debug(this.getClass().getName() + ": ServiceDictionaryEntry params ");
      Enumeration paramNames = request.getParameterNames();
      String paramName = "";
      while (paramNames.hasMoreElements()) {
        paramName = (String)paramNames.nextElement();
        cat.debug(this.getClass().getName() + ": paramName = " + paramName);

        if (paramName.equals(new String("theKey"))) {
          theKey = new Integer(request.getParameter(paramName));
          cat.debug(this.getClass().getName() + ": get[theKey] = " + theKey.toString());
        }
        if (paramName.equals(new String("serviceNum"))) {
          serviceNum = request.getParameter(paramName);
          cat.debug(this.getClass().getName() + ": get[serviceNum] = " + serviceNum);
        }
        if (paramName.equals(new String("serviceName"))) {
          serviceName = request.getParameter(paramName);
          cat.debug(this.getClass().getName() + ": get[serviceName] = " + serviceName);
        }
        if (paramName.equals(new String("vendorKey"))) {
          vendorKey = new Integer(request.getParameter(paramName));
          cat.debug(this.getClass().getName() + ": get[vendorKey] = " + vendorKey.toString());
        }
        if (paramName.equals(new String("vendorName"))) {
          vendorName = request.getParameter(paramName);
          cat.debug(this.getClass().getName() + ": get[vendorName] = " + vendorName);
        }
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

      try {
//--------------------------------------------------------------------------------
// Instantiate a formDO object to populate formStr.
//--------------------------------------------------------------------------------
        ServiceDictionaryDO formDO = new ServiceDictionaryDO();
        
        formDO.setServiceNum(serviceNum);
        formDO.setServiceName(serviceName);
        formDO.setVendorKey(vendorKey);
        formDO.setVendorName(vendorName);

        formDO = buildServiceDictionaryForm(request,
                                            formDO,
                                            theKey);
        formDO.setPermissionStatus(usToken);

        request.getSession().setAttribute("servicedictionaryDO", formDO);
        ServiceDictionaryForm formStr = new ServiceDictionaryForm();
        formStr.populate(formDO);

        form = formStr;
      }
      catch (FormattingException fe) {
        fe.getMessage();
      }

      if ( form == null ) {
        cat.debug(this.getClass().getName() + ":---->form is null<----");
      }
      if ("request".equals(mapping.getScope())) {
        cat.debug(this.getClass().getName() + ":---->request.setAttribute<----");
        request.setAttribute(mapping.getAttribute(), form);
      }
      else {
        cat.debug(this.getClass().getName() + ":---->session.setAttribute<----");
        request.getSession().setAttribute(mapping.getAttribute(), form);
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