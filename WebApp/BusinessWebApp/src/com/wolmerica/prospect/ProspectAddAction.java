/*
 * ProspectAddAction.java
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


public class ProspectAddAction extends Action {

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

  private Integer insertProspect(HttpServletRequest request,
                                 ActionForm form)
   throws Exception, SQLException {

    DataSource ds = null;
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    Integer pKey = 1;

    try {
      ProspectDO formDO = (ProspectDO) request.getSession().getAttribute("prospect");

      ds = getDataSource(request);
      conn = ds.getConnection();
//--------------------------------------------------------------------------------
// Check the prospect table for any duplicate name values.
//--------------------------------------------------------------------------------
      String query = "SELECT client_name "
                   + "FROM customer "
                   + "WHERE UPPER(client_name) = ?";
      ps = conn.prepareStatement(query);
      ps.setString(1, formDO.getName().toUpperCase());
      rs = ps.executeQuery();

      if (!(rs.next())) {

//--------------------------------------------------------------------------------
// Get the maximum key from the prospect.
//--------------------------------------------------------------------------------
        query = "SELECT COUNT(*) AS p_cnt, MAX(thekey)+1 AS p_key "
              + "FROM customer";
        ps = conn.prepareStatement(query);
//--------------------------------------------------------------------------------
// Query to retrieve the maximum key value in the prospect table.
//--------------------------------------------------------------------------------
        rs = ps.executeQuery();
        if ( rs.next() ) {
          if ( rs.getInt("p_cnt") > 0 ) {
            pKey = rs.getInt("p_key");
          }
        }
        else {
          throw new Exception("Prospect MAX() not found!");
        }

//--------------------------------------------------------------------------------
// Construct a new code number value using the current year and the customer key.
//--------------------------------------------------------------------------------
        Calendar rightNow = Calendar.getInstance();
        formDO.setCodeNum(rightNow.get(Calendar.YEAR) + "-");

        if (pKey < 9)
          formDO.setCodeNum(formDO.getCodeNum() + "0000" + pKey.toString());
        else if (pKey < 99)
          formDO.setCodeNum(formDO.getCodeNum() + "000" + pKey.toString());
        else if (pKey < 999)
          formDO.setCodeNum(formDO.getCodeNum() + "00" + pKey.toString());
        else if (pKey < 9999)
          formDO.setCodeNum(formDO.getCodeNum() + "0" + pKey.toString());
        else if (pKey < 99999)
          formDO.setCodeNum(formDO.getCodeNum() + "-" + pKey.toString());

        Byte customerTypeKey = new Byte(getPropertyService().getCustomerProperties(request,"prospect.default.customertypekey"));

//--------------------------------------------------------------------------------
// Prepare query to insert a row into the prospect table.
//--------------------------------------------------------------------------------
        query = "INSERT INTO customer "
              + "(thekey,"
              + "code_num,"
              + "customertype_key,"
              + "primary_key,"
              + "client_name,"
              + "ship_to,"
              + "acct_name,"
              + "contact_name,"
              + "line_of_business,"
              + "address,"
              + "address2,"
              + "city,"
              + "state,"
              + "zip,"
              + "phone_num,"
              + "mobile_num,"
              + "fax_num,"
              + "email,"
              + "email2,"
              + "website,"
              + "acct_num,"
              + "referred_by,"
              + "note_line1,"
              + "active_id,"
              + "reminder_pref_key,"
              + "create_user,"
              + "create_stamp,"
              + "update_user,"
              + "update_stamp) "
              + "VALUES (?,?,?,?,?,?,?,?,?,?,"
              + "?,?,?,?,?,?,?,?,?,?,"
              + "?,?,?,?,?,?,CURRENT_TIMESTAMP,?,CURRENT_TIMESTAMP)";
        ps = conn.prepareStatement(query);

        ps.setInt(1, pKey);
        ps.setString(2, formDO.getCodeNum());
        ps.setByte(3, customerTypeKey);
        ps.setInt(4, pKey);
        ps.setString(5, formDO.getName());
        ps.setString(6, formDO.getName());
        ps.setString(7, formDO.getName());
        ps.setString(8, formDO.getContactName());
        ps.setString(9, formDO.getLineOfBusiness());
        ps.setString(10, formDO.getAddress());
        ps.setString(11, formDO.getAddress2());
        ps.setString(12, formDO.getCity());
        ps.setString(13, formDO.getState());
        ps.setString(14, formDO.getZip());
        ps.setString(15, formDO.getPhoneNum());
        ps.setString(16, formDO.getMobileNum());
        ps.setString(17, formDO.getFaxNum());
        ps.setString(18, formDO.getEmail());
        ps.setString(19, formDO.getEmail2());
        ps.setString(20, formDO.getWebSite());
        ps.setString(21, formDO.getCodeNum());
        ps.setString(22, formDO.getReferredBy());
        ps.setString(23, formDO.getNoteLine1());
        ps.setBoolean(24, formDO.getActiveId());
        ps.setByte(25, formDO.getReminderPrefKey());
        ps.setString(26, request.getSession().getAttribute("USERNAME").toString());
        ps.setString(27, request.getSession().getAttribute("USERNAME").toString());
        ps.executeUpdate();
      }
      else {
//--------------------------------------------------------------------------------
// The prospect name you entered already exists.
//--------------------------------------------------------------------------------
        pKey = null;
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
    return pKey;
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
      theKey = insertProspect(request, form);
//--------------------------------------------------------------------------------
// A prospect key of null indicates a duplicate in the prospect name field.
//--------------------------------------------------------------------------------
      if (theKey == null) {
        target = "error";
        ActionMessages errors = new ActionMessages();
        errors.add("name", new ActionMessage("errors.duplicate"));
        saveErrors(request, errors);
      }
      else
      {
        request.setAttribute("key", theKey.toString());
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