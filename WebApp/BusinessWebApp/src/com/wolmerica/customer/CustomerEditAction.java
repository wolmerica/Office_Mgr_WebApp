/*
 * CustomerEditAction.java
 *
 * Created on August 23, 2005, 11:30 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 * 12/21/2005 Implement tools.formatter library.
 *
 */

package com.wolmerica.customer;

//--------------------------------------------------------------------------------
// @author Richard Wolschlager
//--------------------------------------------------------------------------------
import com.wolmerica.employee.EmployeesActionMapping;
import com.wolmerica.service.property.PropertyService;
import com.wolmerica.service.property.DefaultPropertyService;
import com.wolmerica.service.userstate.UserStateService;
import com.wolmerica.service.userstate.DefaultUserStateService;

import java.io.IOException;
import java.io.File;
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

import org.apache.log4j.Logger;

public class CustomerEditAction extends Action {

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


  private void updateCustomer(HttpServletRequest request,
                              ActionForm form)
    throws Exception, SQLException {

    String user = null;
    DataSource ds = null;
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    try {
      CustomerDO formDO = (CustomerDO) request.getSession().getAttribute("customer");

      ds = getDataSource(request);
      conn = ds.getConnection();

       String query = "UPDATE customer SET "
                    + "last_name=?,"
                    + "first_name=?,"
                    + "ship_to=?,"
                    + "address=?,"
                    + "address2=?,"
                    + "city=?,"
                    + "state=?,"
                    + "zip=?,"
                    + "phone_num=?,"
                    + "phone_ext=?,"
                    + "mobile_num=?,"
                    + "fax_num=?,"
                    + "acct_num=?,"
                    + "acct_name=?,"
                    + "customertype_key=?,"
                    + "ledger_id=?,"
                    + "client_name=?,"
                    + "report_id=?,"
                    + "primary_key=?,"
                    + "clinic_id=?,"
                    + "email=?,"
                    + "email2=?,"
                    + "website=?,"
                    + "line_of_business=?,"
                    + "contact_name=?,"
                    + "referred_by=?,"
                    + "note_line1=?,"
                    + "note_line2=?,"                    
                    + "active_id=?,"
                    + "reminder_pref_key=?,"
                    + "update_user=?,"
                    + "update_stamp=CURRENT_TIMESTAMP "
                    + "WHERE thekey=?";
      ps = conn.prepareStatement(query);

      cat.debug(this.getClass().getName() + ": getPrimaryKey() = " + formDO.getPrimaryKey());

      ps.setString(1, formDO.getLastName());
      ps.setString(2, formDO.getFirstName());
      ps.setString(3, formDO.getShipTo());
      ps.setString(4, formDO.getAddress());
      ps.setString(5, formDO.getAddress2());
      ps.setString(6, formDO.getCity());
      ps.setString(7, formDO.getState());
      ps.setString(8, formDO.getZip());
      ps.setString(9, formDO.getPhoneNum());
      ps.setString(10, formDO.getPhoneExt());      
      ps.setString(11, formDO.getMobileNum());
      ps.setString(12, formDO.getFaxNum());
      ps.setString(13, formDO.getAcctNum());
      ps.setString(14, formDO.getAcctName());
      ps.setByte(15, formDO.getCustomerTypeKey());
      ps.setBoolean(16, formDO.getLedgerId());
      ps.setString(17, formDO.getClientName());
      ps.setBoolean(18, formDO.getReportId());
      ps.setInt(19, formDO.getPrimaryKey());
      ps.setBoolean(20, formDO.getClinicId());
      ps.setString(21, formDO.getEmail());
      ps.setString(22, formDO.getEmail2());
      ps.setString(23, formDO.getWebSite());
      ps.setString(24, formDO.getLineOfBusiness());
      ps.setString(25, formDO.getContactName());
      ps.setString(26, formDO.getReferredBy());
      ps.setString(27, formDO.getNoteLine1());
      ps.setString(28, formDO.getNoteLine2());      
      ps.setBoolean(29, formDO.getActiveId());
      ps.setByte(30, formDO.getReminderPrefKey());
      ps.setString(31, request.getSession().getAttribute("USERNAME").toString());
      ps.setInt(32, formDO.getKey());

      ps.executeUpdate();

      if (!(formDO.getActiveId())) {
//--------------------------------------------------------------------------------
// Move the customer attachments to the appropriate prospect folder after domotion.
//--------------------------------------------------------------------------------
        String savePath = getPropertyService().getCustomerProperties(request, "fileupload.physical.directory");
        String pathSymbol = getPropertyService().getCustomerProperties(request, "fileupload.separator.symbol");
        String customerId = getPropertyService().getCustomerProperties(request, "customer.feature.id");
        String prospectId = getPropertyService().getCustomerProperties(request, "prospect.feature.id");
//--------------------------------------------------------------------------------
// File (or directory) to be moved
//--------------------------------------------------------------------------------
        File file = new File(savePath + customerId + pathSymbol + formDO.getKey());
        if (file.exists()) {
//--------------------------------------------------------------------------------
// Destination directory
//--------------------------------------------------------------------------------
          File dir = new File(savePath + prospectId + pathSymbol + formDO.getKey());
          if (!(dir.exists())) {
//--------------------------------------------------------------------------------
// Move file to new directory
//--------------------------------------------------------------------------------
            boolean success = file.renameTo(dir);
            if (!success) {
              cat.error(this.getClass().getName() + ": Failed renameTo() dir: " + file.getPath() + file.getName());
              cat.error(this.getClass().getName() + ": to dir: " + dir.getPath() + dir.getName());
            }
            else
            {
//--------------------------------------------------------------------------------
// Update the attachment table to indicate the movement of files to customer.
//--------------------------------------------------------------------------------
              query = "UPDATE attachment SET "
                    + "sourcetype_key = ? "
                    + "WHERE sourcetype_key = ? "
                    + "AND source_key = ?";
              ps = conn.prepareStatement(query);
              ps.setString(1, prospectId);
              ps.setString(2, customerId);
              ps.setInt(3, formDO.getKey());
              ps.executeUpdate();
            }
          }
        }
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
      request.setAttribute("key", theKey.toString());

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

      updateCustomer(request, form);
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
