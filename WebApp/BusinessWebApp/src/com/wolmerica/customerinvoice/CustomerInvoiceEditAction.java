/*
 * CustomerInvoiceEditAction.java
 *
 * Created on October 23, 2005, 7:07 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 * 12/20/2005 Implement tools.formatter library.
 */

package com.wolmerica.customerinvoice;

//--------------------------------------------------------------------------------
// @author Richard Wolschlager
//--------------------------------------------------------------------------------
import com.wolmerica.employee.EmployeesActionMapping;
import com.wolmerica.service.tax.TaxService;
import com.wolmerica.service.tax.DefaultTaxService;
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

import org.apache.log4j.Logger;

public class CustomerInvoiceEditAction extends Action {

  Logger cat = Logger.getLogger("WOWAPP");

  private TaxService taxService = new DefaultTaxService();
  private UserStateService userStateService = new DefaultUserStateService();

  public TaxService getTaxService() {
      return taxService;
  }

  public void setTaxService(TaxService taxService) {
      this.taxService = taxService;
  }

  public UserStateService getUserStateService() {
      return userStateService;
  }

  public void setUserStateService(UserStateService userStateService) {
      this.userStateService = userStateService;
  }


  private void updateCustomerInvoice(HttpServletRequest request,
                                     ActionForm form)
    throws Exception, SQLException {

    DataSource ds = null;
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    try {
      ds = getDataSource(request);
      conn = ds.getConnection();

      CustomerInvoiceDO formDO = (CustomerInvoiceDO) request.getSession().getAttribute("customerinvoice");

//--------------------------------------------------------------------------------
// Get the ITEM SALES tax rate related to the sales tax key.  The item and
// service sales tax rates are paired in the TaxAndMarkUp table by a factor
// of 50.  Item sales tax key = 0 and service sales tax = 50.
//--------------------------------------------------------------------------------
      Integer serviceTaxKey = formDO.getSalesTaxKey() + 50;
      formDO.setSalesTaxRate(getTaxService().getTaxRate(conn, formDO.getSalesTaxKey()));

//----------------------------------------------------------------------
// Get the SERVICE tax rate related to the service tax key.
//----------------------------------------------------------------------
      formDO.setServiceTaxKey(serviceTaxKey.byteValue());
      formDO.setServiceTaxRate(getTaxService().getTaxRate(conn, formDO.getServiceTaxKey()));
      
//----------------------------------------------------------------------
// Update the customer invoice with the user entered values.
//----------------------------------------------------------------------
      String query = "UPDATE customerinvoice SET "
                   + "customer_key=?,"
                   + "customertype_key=?,"
                   + "scenario_key=?,"
                   + "note_line1=?,"
                   + "note_line2=?,"
                   + "note_line3=?,"
                   + "sales_tax_key=?,"
                   + "sales_tax_rate=?,"
                   + "service_tax_key=?,"
                   + "service_tax_rate=?,"
                   + "debit_adjustment=?,"
                   + "packaging=?,"
                   + "freight=?,"
                   + "miscellaneous=?,"
                   + "credit_adjustment=?,"
                   + "sourcetype_key=?,"
                   + "source_key=?,"
                   + "update_user=?,"
                   + "update_stamp=CURRENT_TIMESTAMP "
                   + "WHERE thekey=?";
        ps = conn.prepareStatement(query);
        ps.setInt(1, formDO.getCustomerKey());
        ps.setByte(2, formDO.getCustomerTypeKey());
        ps.setByte(3, formDO.getScenarioKey());
        ps.setString(4, formDO.getNoteLine1());
        ps.setString(5, formDO.getNoteLine2());
        ps.setString(6, formDO.getNoteLine3());
        ps.setByte(7, formDO.getSalesTaxKey());
        ps.setBigDecimal(8, formDO.getSalesTaxRate().setScale(2, BigDecimal.ROUND_HALF_UP));
        ps.setByte(9, formDO.getServiceTaxKey());
        ps.setBigDecimal(10, formDO.getServiceTaxRate().setScale(2, BigDecimal.ROUND_HALF_UP));
        ps.setBigDecimal(11, formDO.getDebitAdjustment().setScale(2, BigDecimal.ROUND_HALF_UP));
        ps.setBigDecimal(12, formDO.getPackagingCost().setScale(2, BigDecimal.ROUND_HALF_UP));
        ps.setBigDecimal(13, formDO.getFreightCost().setScale(2, BigDecimal.ROUND_HALF_UP));
        ps.setBigDecimal(14, formDO.getMiscellaneousCost().setScale(2, BigDecimal.ROUND_HALF_UP));
        ps.setBigDecimal(15, formDO.getCreditAdjustment().setScale(2, BigDecimal.ROUND_HALF_UP));
        ps.setByte(16, formDO.getSourceTypeKey());
        ps.setInt(17, formDO.getSourceKey());
        ps.setString(18, formDO.getUpdateUser());
        ps.setInt(19, formDO.getKey());
        ps.executeUpdate();
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

      updateCustomerInvoice(request, form);
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
