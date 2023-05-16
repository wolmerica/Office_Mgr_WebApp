/*
 * VendorGetAction.java
 *
 * Created on August 24, 2005, 10:58 PM
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
import com.wolmerica.service.accounting.AccountingService;
import com.wolmerica.service.accounting.DefaultAccountingService;
import com.wolmerica.service.attachment.AttachmentService;
import com.wolmerica.service.attachment.DefaultAttachmentService;
import com.wolmerica.service.userstate.UserStateService;
import com.wolmerica.service.userstate.DefaultUserStateService;
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
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;

import org.apache.log4j.Logger;

public class VendorGetAction extends Action {

  Logger cat = Logger.getLogger("WOWAPP");
  

  private AccountingService accountingService = new DefaultAccountingService();
  private AttachmentService attachmentService = new DefaultAttachmentService();
  private UserStateService userStateService = new DefaultUserStateService();

  public AccountingService getAccountingService() {
      return accountingService;
  }

  public void setAccountingService(AccountingService accountingService) {
      this.accountingService = accountingService;
  }

  public AttachmentService getAttachmentService() {
      return attachmentService;
  }

  public void setAttachmentService(AttachmentService attachmentService) {
      this.attachmentService = attachmentService;
  }

  public UserStateService getUserStateService() {
      return userStateService;
  }

  public void setUserStateService(UserStateService userStateService) {
      this.userStateService = userStateService;
  }


  private VendorDO buildVendorForm(HttpServletRequest request,
                                       Integer vKey)
   throws Exception, SQLException {

    DataSource ds = null;
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;
    VendorDO formDO = null;

    try {
      ds = getDataSource(request);
      conn = ds.getConnection();

      String query = "SELECT name,"
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
                   + "create_user,"
                   + "create_stamp,"
                   + "update_user,"
                   + "update_stamp "
                   + "FROM vendor "
                   + "WHERE thekey = ?";

      ps = conn.prepareStatement(query);
      ps.setInt(1, vKey);
      rs = ps.executeQuery();

      if ( rs.next() ) {
        formDO = new VendorDO();

        formDO.setKey(vKey);
        formDO.setName(rs.getString("name"));
        formDO.setContactName(rs.getString("contact_name"));
        formDO.setAddress(rs.getString("address"));
        formDO.setAddress2(rs.getString("address2"));
        formDO.setCity(rs.getString("city"));
        formDO.setState(rs.getString("state"));
        formDO.setZip(rs.getString("zip"));
        formDO.setPhoneNum(rs.getString("phone_num"));
        formDO.setPhoneExt(rs.getString("phone_ext"));        
        formDO.setFaxNum(rs.getString("fax_num"));
        formDO.setEmail(rs.getString("email"));
        formDO.setEmail2(rs.getString("email2"));
        formDO.setWebSite(rs.getString("website"));
        formDO.setAcctNum(rs.getString("acct_num"));
        formDO.setTerms(rs.getString("terms"));
        formDO.setMarkUp(rs.getBigDecimal("markup"));
        formDO.setOrderFormKey(rs.getByte("order_form_key"));
        formDO.setClinicId(rs.getBoolean("clinic_id"));
        formDO.setTrackResultId(rs.getBoolean("track_result_id"));
        formDO.setWebServiceId(rs.getBoolean("web_service_id"));
        formDO.setActiveId(rs.getBoolean("active_id"));
        formDO.setCreateUser(rs.getString("create_user"));
        formDO.setCreateStamp(rs.getTimestamp("create_stamp"));
        formDO.setUpdateUser(rs.getString("update_user"));
        formDO.setUpdateStamp(rs.getTimestamp("update_stamp"));

//--------------------------------------------------------------------------------
// Get the count of attachments associated with this VENDOR.
//--------------------------------------------------------------------------------
        formDO.setAttachmentCount(getAttachmentService().getAttachmentCount(conn,
                                                        getUserStateService().getFeatureKey().byteValue(),
                                                        formDO.getKey()));
      }
      else {
        throw new Exception("Vendor  " + vKey.toString() + " not found!");
      }

//--------------------------------------------------------------------------------
// Retrieve the last payment amount from the vendor accounting records.
//--------------------------------------------------------------------------------
      HashMap acctMap = getAccountingService().getVendorLastPayment(conn,formDO.getKey());
      formDO.setLastPaymentAmount((BigDecimal)(acctMap.get("lastPaymentAmt")));
      formDO.setLastPaymentDate((Date)(acctMap.get("lastPaymentDate")));
      
//--------------------------------------------------------------------------------
// Retrieve the balance due amount from the vendor accounting records.
//--------------------------------------------------------------------------------
      acctMap = getAccountingService().getVendorBalance(conn,formDO.getKey());
      formDO.setAccountBalanceAmount((BigDecimal)(acctMap.get("balanceAmt")));
      formDO.setAccountBalanceDate((Date)(acctMap.get("balanceDate")));
     
//--------------------------------------------------------------------------------
// A balance greater than zero indicates a payment is due.
//--------------------------------------------------------------------------------
      if (formDO.getAccountBalanceAmount().compareTo(new BigDecimal("0")) > 0)
        formDO.setAllowPaymentId(true);
//--------------------------------------------------------------------------------
// A balance greater than zero indicates a refund is available.
//--------------------------------------------------------------------------------
      if (formDO.getAccountBalanceAmount().compareTo(new BigDecimal("0")) < 0)
        formDO.setAllowRefundId(true);

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
        VendorDO formDO = buildVendorForm(request, theKey);
        formDO.setPermissionStatus(usToken);
        request.getSession().setAttribute("vendor", formDO);
        VendorForm formStr = new VendorForm();
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