/*
 * VendorInvoiceEditAction.java
 *
 * Created on October 23, 2005, 7:07 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 * 12/22/2005 Implement tools.formatter library.
 */

package com.wolmerica.vendorinvoice;

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

public class VendorInvoiceEditAction extends Action {

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


  private Integer updateVendorInvoice(HttpServletRequest request,
                                      ActionForm form)
    throws Exception, SQLException {

    DataSource ds = null;
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    Integer viKey = null;

    try {
      VendorInvoiceDO formDO = (VendorInvoiceDO) request.getSession().getAttribute("vendorinvoice");

      ds = getDataSource(request);
      conn = ds.getConnection();

      viKey = formDO.getKey();
//--------------------------------------------------------------------------------
// Check if vendor invoice number is being changed.
//--------------------------------------------------------------------------------
      String query = "SELECT invoice_num "
                   + "FROM vendorinvoice "
                   + "WHERE thekey = ?";
      ps = conn.prepareStatement(query);
      ps.setInt(1, formDO.getKey());
      rs = ps.executeQuery();
      if (rs.next()) {
//--------------------------------------------------------------------------------
// A change to the vendor invoice number requires us to check to make sure the
// new value doesn't already exist for an different vendor invoice.
//--------------------------------------------------------------------------------
        cat.debug(this.getClass().getName() + ": stored=" + rs.getString("invoice_num"));
        cat.debug(this.getClass().getName() + ": form=" + formDO.getInvoiceNumber());
        if (!(rs.getString("invoice_num").equalsIgnoreCase(formDO.getInvoiceNumber()))) {
          query = "SELECT thekey "
                + "FROM vendorinvoice "
                + "WHERE UPPER(invoice_num) = ?";
          cat.debug(this.getClass().getName() + ": build query=" + query);
          ps = conn.prepareStatement(query);
          ps.setString(1, formDO.getInvoiceNumber().toUpperCase());
          rs = ps.executeQuery();
          if (rs.next())
            viKey = null;
        }
      }
      else {
        throw new Exception("VendorInvoice  " + formDO.getKey().toString() + " not found!");
      }
//--------------------------------------------------------------------------------
// The vendor invoice number does not already exists.
//--------------------------------------------------------------------------------
      if (viKey != null) {
//--------------------------------------------------------------------------------
// Get the ITEM SALES tax rate related to the sales tax key.  The item and
// service sales tax rates are paired in the TaxAndMarkUp table by a factor
// of 50.  Item sales tax key = 0 and service sales tax = 50.
//--------------------------------------------------------------------------------
        formDO.setSalesTaxRate(getTaxService().getTaxRate(conn, formDO.getSalesTaxKey()));
        cat.debug(this.getClass().getName() + " sales tax rate: " + formDO.getSalesTaxRate());

//----------------------------------------------------------------------
// Get the SERVICE tax rate related to the service tax key.
//----------------------------------------------------------------------
        Integer serviceTaxKey = formDO.getSalesTaxKey() + 50;
        formDO.setServiceTaxKey(serviceTaxKey.byteValue());
        formDO.setServiceTaxRate(getTaxService().getTaxRate(conn, formDO.getServiceTaxKey()));
        cat.debug(this.getClass().getName() + " service tax rate: " + formDO.getServiceTaxRate());

//----------------------------------------------------------------------
// Update the vendor invoice with the user entered values.
//----------------------------------------------------------------------
        query = "UPDATE vendorinvoice SET "
              + "invoice_num=?,"
              + "invoice_date=?,"
              + "invoice_due_date=?,"
              + "sales_tax_cost=?,"
              + "packaging=?,"
              + "freight=?,"
              + "miscellaneous=?,"
              + "grand_total=?,"
              + "sales_tax_key=?,"
              + "sales_tax_rate=?,"
              + "service_tax_key=?,"
              + "service_tax_rate=?,"
              + "carry_factor_id=?,"
              + "rma_number=?,"
              + "note_line1=?,"
              + "update_user=?,"
              + "update_stamp=CURRENT_TIMESTAMP "
              + "WHERE thekey=?";
        ps = conn.prepareStatement(query);
        ps.setString(1, formDO.getInvoiceNumber());
        ps.setDate(2, new java.sql.Date(formDO.getInvoiceDate().getTime()));
        ps.setDate(3, new java.sql.Date(formDO.getInvoiceDueDate().getTime()));
        ps.setBigDecimal(4, formDO.getSalesTaxCost().setScale(2, BigDecimal.ROUND_HALF_UP));
        ps.setBigDecimal(5, formDO.getPackagingCost().setScale(2, BigDecimal.ROUND_HALF_UP));
        ps.setBigDecimal(6, formDO.getFreightCost().setScale(2, BigDecimal.ROUND_HALF_UP));
        ps.setBigDecimal(7, formDO.getMiscellaneousCost().setScale(2, BigDecimal.ROUND_HALF_UP));
        ps.setBigDecimal(8, formDO.getInvoiceTotal().setScale(2, BigDecimal.ROUND_HALF_UP));
        ps.setByte(9, formDO.getSalesTaxKey());
        ps.setBigDecimal(10, formDO.getSalesTaxRate().setScale(2, BigDecimal.ROUND_HALF_UP));
        ps.setByte(11, formDO.getServiceTaxKey());
        ps.setBigDecimal(12, formDO.getServiceTaxRate().setScale(2, BigDecimal.ROUND_HALF_UP));
        ps.setBoolean(13, formDO.getCarryFactorId());
        ps.setString(14, formDO.getRmaNumber());
        ps.setString(15, formDO.getNoteLine1());
        ps.setString(16, request.getSession().getAttribute("USERNAME").toString());
        ps.setInt(17, formDO.getKey());
        ps.executeUpdate();
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
    return viKey;
  }

    @Override
  public ActionForward execute(ActionMapping mapping,
                               ActionForm form,
                               HttpServletRequest request,
                               HttpServletResponse response)
   throws IOException, ServletException {

//--------------------------------------------------------------------------------
// Default target to success
//--------------------------------------------------------------------------------
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

      theKey = null;
      theKey = updateVendorInvoice(request, form);
//--------------------------------------------------------------------------------
// A customer key of null indicates a duplicate in the code number field.
//--------------------------------------------------------------------------------
      if (theKey == null) {
        target = "error";
        ActionMessages errors = new ActionMessages();
        errors.add("invoiceNumber", new ActionMessage("errors.duplicate"));
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
