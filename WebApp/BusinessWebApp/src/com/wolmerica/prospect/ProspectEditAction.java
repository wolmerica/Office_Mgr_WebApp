/*
 * ProspectEditAction.java
 *
 * Created on September 27, 2006, 10:18 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 * 12/21/2005 Implement tools.formatter library.
 */


package com.wolmerica.prospect;

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

public class ProspectEditAction extends Action {

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


  private void updateProspect(HttpServletRequest request,
                              ActionForm form)
    throws Exception, SQLException {

    DataSource ds = null;
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    try {
      ds = getDataSource(request);
      conn = ds.getConnection();

      ProspectDO formDO = (ProspectDO) request.getSession().getAttribute("prospect");

      String query = "UPDATE customer SET "
                   + "client_name=?,"
                   + "contact_name=?,"
                   + "line_of_business=?,"
                   + "address=?,"
                   + "address2=?,"
                   + "city=?,"
                   + "state=?,"
                   + "zip=?,"
                   + "phone_num=?,"
                   + "mobile_num=?,"
                   + "fax_num=?,"
                   + "email=?,"
                   + "email2=?,"
                   + "website=?,"
                   + "referred_by=?,"
                   + "note_line1=?,"
                   + "active_id=?,"
                   + "reminder_pref_key=?,"
                   + "update_user=?,"
                   + "update_stamp=CURRENT_TIMESTAMP "
                   + "WHERE thekey=?";
      ps = conn.prepareStatement(query);

      ps.setString(1, formDO.getName());
      ps.setString(2, formDO.getContactName());
      ps.setString(3, formDO.getLineOfBusiness());
      ps.setString(4, formDO.getAddress());
      ps.setString(5, formDO.getAddress2());
      ps.setString(6, formDO.getCity());
      ps.setString(7, formDO.getState());
      ps.setString(8, formDO.getZip());
      ps.setString(9, formDO.getPhoneNum());
      ps.setString(10, formDO.getMobileNum());
      ps.setString(11, formDO.getFaxNum());
      ps.setString(12, formDO.getEmail());
      ps.setString(13, formDO.getEmail2());      
      ps.setString(14, formDO.getWebSite());
      ps.setString(15, formDO.getReferredBy());
      ps.setString(16, formDO.getNoteLine1());
      ps.setBoolean(17, formDO.getActiveId());
      ps.setByte(18, formDO.getReminderPrefKey());
      ps.setString(19, formDO.getUpdateUser());
      ps.setInt(20, formDO.getKey());

      ps.executeUpdate();

      if (formDO.getActiveId()) {
//--------------------------------------------------------------------------------
// Move the customer attachments to the appropriate prospect folder after demotion.
//--------------------------------------------------------------------------------
        String savePath = getPropertyService().getCustomerProperties(request, "fileupload.physical.directory");
        String pathSymbol = getPropertyService().getCustomerProperties(request, "fileupload.separator.symbol");
        String customerId = getPropertyService().getCustomerProperties(request, "customer.feature.id");
        String prospectId = getPropertyService().getCustomerProperties(request, "prospect.feature.id");
//--------------------------------------------------------------------------------
// File (or directory) to be moved
//--------------------------------------------------------------------------------
        File file = new File(savePath + prospectId + pathSymbol + formDO.getKey());
        if (file.exists()) {
//--------------------------------------------------------------------------------
// Destination directory
//--------------------------------------------------------------------------------
          File dir = new File(savePath + customerId + pathSymbol + formDO.getKey());
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
              ps.setString(1, customerId);
              ps.setString(2, prospectId);
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

      updateProspect(request, form);
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
