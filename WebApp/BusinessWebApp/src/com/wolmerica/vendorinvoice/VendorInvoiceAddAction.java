/*
 * VendorInvoiceAddAction.java
 *
 * Created on December 05, 2005, 4:43 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 *
 * Use VendorInvoiceAdd to insert the first VendorInvoice related to a
 * purchase order.
 * 12/22/2005 Implement tools.formatter library.
 */

package com.wolmerica.vendorinvoice;

//--------------------------------------------------------------------------------
// @author Richard Wolschlager
//--------------------------------------------------------------------------------
import com.wolmerica.employee.EmployeesActionMapping;
import com.wolmerica.service.attributeto.AttributeToService;
import com.wolmerica.service.attributeto.DefaultAttributeToService;
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
import java.util.HashMap;

import org.apache.log4j.Logger;

public class VendorInvoiceAddAction extends Action {

  Logger cat = Logger.getLogger("WOWAPP");

  private AttributeToService attributeToService = new DefaultAttributeToService();
  private TaxService taxService = new DefaultTaxService();
  private UserStateService userStateService = new DefaultUserStateService();

  public AttributeToService getAttributeToService() {
      return attributeToService;
  }

  public void setAttributeToService(AttributeToService attributeToService) {
      this.attributeToService = attributeToService;
  }

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


  private Integer insertVendorInvoice(HttpServletRequest request,
                                      Boolean poLookUp,
                                      Integer poKey)
   throws Exception, SQLException {

    DataSource ds = null;
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;
    VendorInvoiceDO formDO = null;

    Integer viKey = 1;
    Integer masterKey = null;

    try {
      ds = getDataSource(request);
      conn = ds.getConnection();

//--------------------------------------------------------------------------------
// Get the maximum key from the vendor invoice key.
//--------------------------------------------------------------------------------
      String query = "SELECT COUNT(*) AS vi_cnt, MAX(thekey)+1 AS vi_key "
                   + "FROM vendorinvoice ";
      ps = conn.prepareStatement(query);
      rs = ps.executeQuery();
      if ( rs.next() ) {
        if ( rs.getInt("vi_cnt") > 0 ) {
          viKey = rs.getInt("vi_key");
        }
      }
      else {
        throw new Exception("VendorInvoice MAX() not found!");
      }
      cat.debug(this.getClass().getName() + " SELECT MAX() vendorinvoice = " + viKey.toString());

//--------------------------------------------------------------------------------
// When dealing with a vendor invoice credit we already have a form with the details.
//--------------------------------------------------------------------------------
      if (!poLookUp) {
        formDO = (VendorInvoiceDO) request.getSession().getAttribute("vendorinvoice");
      }
      else
      {
//===========================================================================
// Retrieve the Master key for sequence of purchase order check-in.
// We need to add a check to see if the vendor invoice is active.
// We do not want the master_key from a previous P.O. check-in.
//===========================================================================
        cat.debug(this.getClass().getName() + " Processing for a regular vendor invoice.");
        query = "SELECT master_key "
              + "FROM vendorinvoice "
              + "WHERE purchaseorder_key = ? "
              + "AND master_key = thekey "
              + "AND active_id";
        ps = conn.prepareStatement(query);
        ps.setInt(1, new Integer(poKey));
        rs = ps.executeQuery();

        if (rs.next())
          masterKey = rs.getInt("master_key");
        else
          masterKey = viKey;
        cat.debug(this.getClass().getName() + " masterKey = " + masterKey);
//===========================================================================
// Information from the purchase order will be passed along next sequence
// which is the creation of a vendor invoice to be leveraged for check-in.
//===========================================================================
        query = "SELECT purchaseorder.purchase_order_num, "
              + "vendor.name, "
              + "customertype.attribute_to_entity,"
              + "purchaseorder.sourcetype_key,"
              + "purchaseorder.source_key "
              + "FROM purchaseorder, vendor, customer, customertype "
              + "WHERE purchaseorder.thekey = ? "
              + "AND purchaseorder.vendor_key = vendor.thekey "
              + "AND purchaseorder.customer_key = customer.thekey "
              + "AND customer.customertype_key = customertype.thekey";
        ps = conn.prepareStatement(query);
        ps.setInt(1, poKey);
        rs = ps.executeQuery();

        cat.debug(this.getClass().getName() + " masterKey = " + masterKey);
        if ( rs.next() ) {
          cat.debug(this.getClass().getName() + " PO Number = " + rs.getString("purchase_order_num"));
          formDO = new VendorInvoiceDO();

          formDO.setKey(viKey);
          formDO.setMasterKey(masterKey);
          if (formDO.getGenesisKey() < 0)
            formDO.setGenesisKey(viKey);
          formDO.setPurchaseOrderKey(poKey);
          formDO.setPurchaseOrderNumber(rs.getString("purchase_order_num"));
          formDO.setVendorName(rs.getString("name"));
          formDO.setAttributeToEntity(rs.getString("attribute_to_entity"));
          formDO.setSourceTypeKey(rs.getByte("sourcetype_key"));
          formDO.setSourceKey(rs.getInt("source_key"));          

//--------------------------------------------------------------------------------
// The source type key value is based on the FeatureResources.properties file.
//--------------------------------------------------------------------------------
          if (formDO.getSourceTypeKey().compareTo(new Byte("-1")) > 0) {
//--------------------------------------------------------------------------------
// Retrieve the pet, system, or vehicle attributed to this invoice.
//--------------------------------------------------------------------------------
            HashMap nameMap = getAttributeToService().getAttributeToName(conn,
                                                      formDO.getSourceTypeKey(),
                                                      formDO.getSourceKey());
            formDO.setAttributeToName(nameMap.get("attributeToName").toString());
          }

          cat.debug(this.getClass().getName() + " " + formDO.getPurchaseOrderKey().toString());
          cat.debug(this.getClass().getName() + " " + formDO.getPurchaseOrderNumber());
          cat.debug(this.getClass().getName() + " " + formDO.getMasterKey().toString());
          cat.debug(this.getClass().getName() + " " + formDO.getGenesisKey().toString());
          cat.debug(this.getClass().getName() + " " + formDO.getVendorName());
        }
        else {
          throw new Exception("Parent Purchase Order " + poKey.toString() + " not found!");
        }
      }

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
      
//===========================================================================
// Creation of a vendor invoice to track a check-in sequence related to a
// purchase order.  Multiple vendor invoices may be issued for a single P.O.
//===========================================================================
      query = "INSERT INTO vendorinvoice "
            + "(thekey,"
            + "purchaseorder_key,"
            + "master_key,"
            + "genesis_key,"
            + "invoice_num,"
            + "invoice_date,"
            + "invoice_due_date,"
            + "line_item_total,"
            + "sub_total,"
            + "sales_tax_cost,"
            + "packaging,"
            + "freight,"
            + "miscellaneous,"
            + "grand_total,"
            + "sales_tax_key,"
            + "sales_tax_rate,"
            + "taxable_total,"
            + "service_tax_key,"
            + "service_tax_rate,"
            + "service_tax_cost,"
            + "carry_factor_id,"
            + "active_id,"
            + "create_user,"
            + "create_stamp,"
            + "update_user,"
            + "update_stamp) "
            + "VALUES (?,?,?,?,?,?,?,?,?,?,"
            + "?,?,?,?,?,?,?,?,?,?,"
            + "?,?,?,CURRENT_TIMESTAMP,?,CURRENT_TIMESTAMP)";
      ps = conn.prepareStatement(query);
      ps.setInt(1, viKey);
      ps.setInt(2, formDO.getPurchaseOrderKey());
      ps.setInt(3, formDO.getMasterKey());
      ps.setInt(4, formDO.getGenesisKey());
      ps.setString(5, formDO.getInvoiceNumber());
      if (!poLookUp) {
        ps.setDate(6, new java.sql.Date(formDO.getInvoiceDate().getTime()));
        ps.setDate(7, new java.sql.Date(formDO.getInvoiceDueDate().getTime()));
      }
      else {
        ps.setDate(6, null);
        ps.setDate(7, null);
      }
      ps.setBigDecimal(8, formDO.getLineItemTotal().setScale(2, BigDecimal.ROUND_HALF_UP));
      ps.setBigDecimal(9, formDO.getLineItemTotal().add(formDO.getServiceTotal()).setScale(2, BigDecimal.ROUND_HALF_UP));
      ps.setBigDecimal(10, formDO.getSalesTaxCost().setScale(2, BigDecimal.ROUND_HALF_UP));
      ps.setBigDecimal(11, formDO.getPackagingCost().setScale(2, BigDecimal.ROUND_HALF_UP));
      ps.setBigDecimal(12, formDO.getFreightCost().setScale(2, BigDecimal.ROUND_HALF_UP));
      ps.setBigDecimal(13, formDO.getMiscellaneousCost().setScale(2, BigDecimal.ROUND_HALF_UP));
      ps.setBigDecimal(14, formDO.getInvoiceTotal().setScale(2, BigDecimal.ROUND_HALF_UP));
      ps.setByte(15, formDO.getSalesTaxKey());
      ps.setBigDecimal(16, formDO.getSalesTaxRate().setScale(2, BigDecimal.ROUND_HALF_UP));
      ps.setBigDecimal(17, formDO.getNonTaxableTotal().setScale(2, BigDecimal.ROUND_HALF_UP));
      ps.setByte(18, formDO.getServiceTaxKey());
      ps.setBigDecimal(19, formDO.getServiceTaxRate().setScale(2, BigDecimal.ROUND_HALF_UP));
      ps.setBigDecimal(20, formDO.getServiceTaxCost().setScale(2, BigDecimal.ROUND_HALF_UP));
      ps.setBoolean(21, formDO.getCarryFactorId());
      ps.setBoolean(22, formDO.getActiveId());
      ps.setString(23, request.getSession().getAttribute("USERNAME").toString());
      ps.setString(24, request.getSession().getAttribute("USERNAME").toString());
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
    cat.debug(this.getClass().getName() + ": return viKey = " + viKey);
    return viKey;
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
// Purchase order look-up and key variable initialization.
//--------------------------------------------------------------------------------
      Boolean poLookUp = true;
      Integer poKey = null;
      if (request.getParameter("poKey") != null) {
        poKey = new Integer(request.getParameter("poKey"));
      }
      else {
        if (request.getAttribute("poKey") != null) {
          poKey = new Integer(request.getAttribute("poKey").toString());
        }
        else {
          throw new Exception("Request getParameter/getAttribute [poKey] not found!");
        }
      }
      cat.debug(this.getClass().getName() + ": get[poKey] = " + poKey.toString());
      request.setAttribute("poKey", poKey.toString());

//--------------------------------------------------------------------------------
// The poKey will be set to null when dealing with a new vendor invoice credit.
//--------------------------------------------------------------------------------
      if (request.getParameter("creditKey") != null) {
        poLookUp = false;
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

//--------------------------------------------------------------------------------
// Vendor Invoice Key
//--------------------------------------------------------------------------------
      Integer theKey = null;
      theKey = insertVendorInvoice(request, poLookUp, poKey);
      request.setAttribute("key", theKey.toString());
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