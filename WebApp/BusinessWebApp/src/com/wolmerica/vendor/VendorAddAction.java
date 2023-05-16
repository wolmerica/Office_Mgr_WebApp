/*
 * VendorAddAction.java
 *
 * Created on August 24, 2005, 11:15 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 * 12/21/2005 Implement tools.formatter library.
 */

package com.wolmerica.vendor;

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

import org.apache.log4j.Logger;


public class VendorAddAction extends Action {

  Logger cat = Logger.getLogger("WOWAPP");

  private UserStateService userStateService = new DefaultUserStateService();

  public UserStateService getUserStateService() {
      return userStateService;
  }

  public void setUserStateService(UserStateService userStateService) {
      this.userStateService = userStateService;
  }
  

  private Integer insertVendor(HttpServletRequest request,
                                 ActionForm form)
   throws Exception, SQLException {

    DataSource ds = null;
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    Integer vKey = 1;

    try {
      VendorDO formDO = (VendorDO) request.getSession().getAttribute("vendor");

      ds = getDataSource(request);
      conn = ds.getConnection();
//--------------------------------------------------------------------------------
// Check the vendor table for any duplicate name values.
//--------------------------------------------------------------------------------
      String query = "SELECT name "
                   + "FROM vendor "
                   + "WHERE UPPER(name) = ?";
      ps = conn.prepareStatement(query);
      ps.setString(1, formDO.getName().toUpperCase());
      rs = ps.executeQuery();

      if (!(rs.next())) {
//--------------------------------------------------------------------------------
// Get the maximum key from the vendor.
//--------------------------------------------------------------------------------
        query = "SELECT COUNT(*) AS v_cnt, MAX(thekey)+1 AS v_key "
              + "FROM vendor";
        ps = conn.prepareStatement(query);
//--------------------------------------------------------------------------------
// Query to retrieve the maximum key value in the vendor table.
//--------------------------------------------------------------------------------
        rs = ps.executeQuery();
        if ( rs.next() ) {
          if ( rs.getInt("v_cnt") > 0 ) {
            vKey = rs.getInt("v_key");
          }
        }
        else {
          throw new Exception("Vendor MAX() not found!");
        }

//--------------------------------------------------------------------------------
// Prepare query to insert a row into the vendor table.
//--------------------------------------------------------------------------------

        query = "INSERT INTO vendor "
              + "(thekey,"
              + "name,"
              + "contact_name,"
              + "address,"
              + "address2,"
              + "city,"
              + "state,"
              + "zip,"
              + "phone_num,"
              + "phone_ext,"
              + "fax_num,"
              + "email,"
              + "email2,"
              + "website,"
              + "acct_num,"
              + "terms,"
              + "markup,"
              + "order_form_key,"
              + "clinic_id,"
              + "track_result_id,"
              + "web_service_id,"
              + "active_id,"
              + "create_user,create_stamp,"
              + "update_user,update_stamp )"
              + "values (?,?,?,?,?,?,?,?,?,?,"
              + "?,?,?,?,?,?,?,?,?,?,"
              + "?,?,?,CURRENT_TIMESTAMP,?,CURRENT_TIMESTAMP)";

        ps = conn.prepareStatement(query);

        ps.setInt(1, vKey);
        ps.setString(2, formDO.getName());
        ps.setString(3, formDO.getContactName());
        ps.setString(4, formDO.getAddress());
        ps.setString(5, formDO.getAddress2());
        ps.setString(6, formDO.getCity());
        ps.setString(7, formDO.getState());
        ps.setString(8, formDO.getZip());
        ps.setString(9, formDO.getPhoneNum());
        ps.setString(10, formDO.getPhoneExt());        
        ps.setString(11, formDO.getFaxNum());
        ps.setString(12, formDO.getEmail());
        ps.setString(13, formDO.getEmail2());
        ps.setString(14, formDO.getWebSite());
        ps.setString(15, formDO.getAcctNum());
        ps.setString(16, formDO.getTerms());
        ps.setBigDecimal(17, formDO.getMarkUp());
        ps.setByte(18, formDO.getOrderFormKey());
        ps.setBoolean(19, formDO.getClinicId());
        ps.setBoolean(20, formDO.getTrackResultId());
        ps.setBoolean(21, formDO.getWebServiceId());
        ps.setBoolean(22, formDO.getActiveId());
        ps.setString(23, request.getSession().getAttribute("USERNAME").toString());
        ps.setString(24, request.getSession().getAttribute("USERNAME").toString());
        ps.executeUpdate();
      }
      else {
//--------------------------------------------------------------------------------
// The vendor name you entered already exists.
//--------------------------------------------------------------------------------
        vKey = null;
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
    return vKey;
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
      theKey = insertVendor(request, form);
//--------------------------------------------------------------------------------
// A vendor key of null indicates a duplicate in the vendor name field.
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