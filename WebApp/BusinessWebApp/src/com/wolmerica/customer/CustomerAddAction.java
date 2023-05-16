/*
 * CustomerAddAction.java
 *
 * Created on August 24, 2005, 2:17 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 * 12/21/2005 Implement tools.formatter library.
 */

package com.wolmerica.customer;

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
import java.util.Calendar;

import org.apache.log4j.Logger;

public class CustomerAddAction extends Action {

  Logger cat = Logger.getLogger("WOWAPP");

  private UserStateService userStateService = new DefaultUserStateService();


  public UserStateService getUserStateService() {
      return userStateService;
  }

  public void setUserStateService(UserStateService userStateService) {
      this.userStateService = userStateService;
  }
  

  private Integer insertCustomer(HttpServletRequest request,
                                 ActionForm form)
    throws Exception, SQLException {

    DataSource ds = null;
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    Integer cKey = 1;

    try {
      CustomerDO formDO = (CustomerDO) request.getSession().getAttribute("customer");

      ds = getDataSource(request);
      conn = ds.getConnection();

//--------------------------------------------------------------------------------
// Check the customer table for any duplicate acct_num values.
//--------------------------------------------------------------------------------
      String query = "SELECT acct_num "
                   + "FROM customer "
                   + "WHERE UPPER(acct_num) = ?";
      ps = conn.prepareStatement(query);
      ps.setString(1, formDO.getAcctNum().toUpperCase());
      rs = ps.executeQuery();

      if (!(rs.next())) {
//--------------------------------------------------------------------------------
// Get the maximum key from the customer.
//--------------------------------------------------------------------------------
        query = "SELECT COUNT(*) AS c_cnt, MAX(thekey)+1 AS c_key "
              + "FROM customer";
        ps = conn.prepareStatement(query);
//--------------------------------------------------------------------------------
// Query to retrieve the maximum key value in the customer table.
//--------------------------------------------------------------------------------
        rs = ps.executeQuery();
        if ( rs.next() ) {
          if ( rs.getInt("c_cnt") > 0 ) {
            cKey = rs.getInt("c_key");
          }
        }
        else {
          throw new Exception("Customer MAX() not found!");
        }

//--------------------------------------------------------------------------------
// Construct a new code number value using the current year and the customer key.
//--------------------------------------------------------------------------------
        Calendar rightNow = Calendar.getInstance();
        formDO.setCodeNum(rightNow.get(Calendar.YEAR) + "-");

        if (cKey < 9)
          formDO.setCodeNum(formDO.getCodeNum() + "0000" + cKey.toString());
        else if (cKey < 99)
          formDO.setCodeNum(formDO.getCodeNum() + "000" + cKey.toString());
        else if (cKey < 999)
          formDO.setCodeNum(formDO.getCodeNum() + "00" + cKey.toString());
        else if (cKey < 9999)
          formDO.setCodeNum(formDO.getCodeNum() + "0" + cKey.toString());
        else if (cKey < 99999)
          formDO.setCodeNum(formDO.getCodeNum() + "-" + cKey.toString());

//--------------------------------------------------------------------------------
// Set the account number value equal to the code number when the account is new.
//--------------------------------------------------------------------------------
        if (formDO.getAcctNum().equalsIgnoreCase("New"))
          formDO.setAcctNum(formDO.getCodeNum());

//--------------------------------------------------------------------------------
// Prepare an insert statement for the customer table.
//--------------------------------------------------------------------------------
        query = "INSERT INTO customer "
              + "(thekey,"
              + "code_num,"
              + "last_name,"
              + "first_name,"
              + "ship_to,"
              + "address,"
              + "address2,"
              + "city,"
              + "state,"
              + "zip,"
              + "phone_num,"
              + "phone_ext,"
              + "mobile_num,"
              + "fax_num,"
              + "acct_num,"
              + "acct_name,"
              + "customertype_key,"
              + "ledger_id,"
              + "client_name,"
              + "report_id,"
              + "primary_key,"
              + "clinic_id,"
              + "email,"
              + "email2,"
              + "website,"
              + "line_of_business,"
              + "contact_name,"
              + "referred_by,"
              + "note_line1,"
              + "note_line2,"              
              + "active_id,"
              + "reminder_pref_key,"
              + "create_user,"
              + "create_stamp,"
              + "update_user,"
              + "update_stamp) "
              + "VALUES (?,?,?,?,?,?,?,?,?,?,"
              + "?,?,?,?,?,?,?,?,?,?,"
              + "?,?,?,?,?,?,?,?,?,?,"
              + "?,?,?,CURRENT_TIMESTAMP,?,CURRENT_TIMESTAMP)";
        ps = conn.prepareStatement(query);

        ps.setInt(1, cKey);
        ps.setString(2, formDO.getCodeNum());
        ps.setString(3, formDO.getLastName());
        ps.setString(4, formDO.getFirstName());
        ps.setString(5, formDO.getShipTo());
        ps.setString(6, formDO.getAddress());
        ps.setString(7, formDO.getAddress2());
        ps.setString(8, formDO.getCity());
        ps.setString(9, formDO.getState());
        ps.setString(10, formDO.getZip());
        ps.setString(11, formDO.getPhoneNum());
        ps.setString(12, formDO.getPhoneExt());
        ps.setString(13, formDO.getMobileNum());
        ps.setString(14, formDO.getFaxNum());
        ps.setString(15, formDO.getAcctNum());
        ps.setString(16, formDO.getAcctName());
        ps.setByte(17, formDO.getCustomerTypeKey());
        ps.setBoolean(18, formDO.getLedgerId());
        ps.setString(19, formDO.getClientName());
        ps.setBoolean(20, formDO.getReportId());
        ps.setInt(21, cKey);
        ps.setBoolean(22, formDO.getClinicId());
        ps.setString(23, formDO.getEmail());
        ps.setString(24, formDO.getEmail2());
        ps.setString(25, formDO.getWebSite());
        ps.setString(26, formDO.getLineOfBusiness());
        ps.setString(27, formDO.getContactName());
        ps.setString(28, formDO.getReferredBy());
        ps.setString(29, formDO.getNoteLine1());
        ps.setString(30, formDO.getNoteLine2());        
        ps.setBoolean(31, formDO.getActiveId());
        ps.setByte(32, formDO.getReminderPrefKey());
        ps.setString(33, request.getSession().getAttribute("USERNAME").toString());
        ps.setString(34, request.getSession().getAttribute("USERNAME").toString());

        ps.executeUpdate();
      }
      else {
//--------------------------------------------------------------------------------
// The customer accout number you entered already exists.
//--------------------------------------------------------------------------------
        cKey = null;
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
    return cKey;
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
      theKey = insertCustomer(request, form);
//--------------------------------------------------------------------------------
// A customer key of null indicates a duplicate in the code number field.
//--------------------------------------------------------------------------------
      if (theKey == null) {
        target = "error";
        ActionMessages errors = new ActionMessages();
        errors.add("acctNum", new ActionMessage("errors.duplicate"));
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
      saveMessages(request, actionMessages);
    }
//--------------------------------------------------------------------------------
// Forward to the appropriate View
//--------------------------------------------------------------------------------
    return (mapping.findForward(target));
  }
}