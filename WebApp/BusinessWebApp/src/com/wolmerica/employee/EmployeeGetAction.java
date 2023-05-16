/*
 * EmployeeGetAction.java
 *
 * Created on August 15, 2005, 8:39 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 * 12/21/2005 Implement tools.formatter library.
 */

package com.wolmerica.employee;

//--------------------------------------------------------------------------------
// @author Richard Wolschlager
//--------------------------------------------------------------------------------
import com.wolmerica.service.attachment.AttachmentService;
import com.wolmerica.service.attachment.DefaultAttachmentService;
import com.wolmerica.service.property.PropertyService;
import com.wolmerica.service.property.DefaultPropertyService;
import com.wolmerica.service.userstate.UserStateService;
import com.wolmerica.service.userstate.DefaultUserStateService;
import com.wolmerica.tools.formatter.PhoneNumberFormatter;
import com.wolmerica.tools.formatter.FormattingException;

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

import org.apache.log4j.Logger;

public class EmployeeGetAction extends Action {

  Logger cat = Logger.getLogger("WOWAPP");
  

  private AttachmentService attachmentService = new DefaultAttachmentService();
  private PropertyService propertyService = new DefaultPropertyService();
  private UserStateService userStateService = new DefaultUserStateService();

  public AttachmentService getAttachmentService() {
      return attachmentService;
  }

  public void setAttachmentService(AttachmentService attachmentService) {
      this.attachmentService = attachmentService;
  }

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


  private EmployeeDO buildEmployeeForm(HttpServletRequest request,
                                         Integer eKey)
   throws Exception, SQLException {

    String user = null;
    DataSource ds = null;
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    EmployeeDO formDO = null;

    try {
      ds = getDataSource(request);
      conn = ds.getConnection();

      String query = "SELECT thekey,"
                   + "user_name,"
                   + "password,"
                   + "admin_id,"
                   + "first_name,"
                   + "last_name,"
                   + "address,"
                   + "address2,"
                   + "city,"
                   + "state,"
                   + "zip,"
                   + "phone,"
                   + "phone_num,"
                   + "phone_ext,"
                   + "mobile_num,"                   
                   + "email,"
                   + "email2,"
                   + "yim_id,"
                   + "permission_slip,"
                   + "login_stamp,"
                   + "INET_NTOA(login_ipn) AS login_ip,"
                   + "create_user,"
                   + "create_stamp,"
                   + "update_user,"
                   + "update_stamp "
                   + "FROM employee "
                   + "WHERE thekey = ?";
      ps = conn.prepareStatement(query);
      ps.setInt(1, eKey);
      rs = ps.executeQuery();
      if ( rs.next() ) {

        formDO = new EmployeeDO();

        formDO.setKey(rs.getInt("thekey"));
        formDO.setUserName(rs.getString("user_name"));
        formDO.setPassword(rs.getString("password"));
        formDO.setAdminId(rs.getBoolean("admin_id"));
        formDO.setFirstName(rs.getString("first_name"));
        formDO.setLastName(rs.getString("last_name"));
        formDO.setAddress(rs.getString("address"));
        formDO.setAddress2(rs.getString("address2"));
        formDO.setCity(rs.getString("city"));
        formDO.setState(rs.getString("state"));
        formDO.setZip(rs.getString("zip"));
        formDO.setPhone(rs.getString("phone"));
        formDO.setPhoneNum(rs.getString("phone_num"));
        formDO.setPhoneExt(rs.getString("phone_ext"));        
        formDO.setMobileNum(rs.getString("mobile_num"));        
        formDO.setEmail(rs.getString("email"));
        formDO.setEmail2(rs.getString("email2"));
        formDO.setYimId(rs.getString("yim_id"));
        formDO.setDocumentServerURL(getPropertyService().getCustomerProperties(request, "fileupload.virtual.directory"));
        formDO.setPhotoFileName(getAttachmentService().getAttachmentPhoto(conn,
                                                      getUserStateService().getFeatureKey().byteValue(),
                                                      formDO.getKey()));
        formDO.setPermissionSlip(rs.getString("permission_slip"));
        formDO.setLoginStamp(rs.getTimestamp("login_stamp"));
        formDO.setLoginIpAddress(rs.getString("login_ip"));
        formDO.setCreateUser(rs.getString("create_user"));
        formDO.setCreateStamp(rs.getTimestamp("create_stamp"));
        formDO.setUpdateUser(rs.getString("update_user"));
        formDO.setUpdateStamp(rs.getTimestamp("update_stamp"));

//--------------------------------------------------------------------------------
// Get the count of attachments associated with this EMPLOYEE.
//--------------------------------------------------------------------------------
        formDO.setAttachmentCount(getAttachmentService().getAttachmentCount(conn,
                                                        getUserStateService().getFeatureKey().byteValue(),
                                                        formDO.getKey()));
      }
      else {
        throw new Exception("Employee " + eKey.toString() + " not found!");
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

      try {
//--------------------------------------------------------------------------------
// Instantiate a formDO object to populate formStr.
//--------------------------------------------------------------------------------
        EmployeeDO formDO = buildEmployeeForm(request, theKey);
        formDO.setPermissionStatus(usToken);
        
        request.getSession().setAttribute("employee", formDO);
        EmployeeForm formStr = new EmployeeForm();
        PhoneNumberFormatter phf = new PhoneNumberFormatter();       
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