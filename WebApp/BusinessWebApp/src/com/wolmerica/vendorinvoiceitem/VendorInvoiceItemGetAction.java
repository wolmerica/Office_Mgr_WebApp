/*
 * VendorInvoiceItemGetAction.java
 *
 * Created on September 9, 2005, 8:14 AM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package com.wolmerica.vendorinvoiceitem;

//--------------------------------------------------------------------------------
// @author Richard Wolschlager
//--------------------------------------------------------------------------------
import com.wolmerica.employee.EmployeesActionMapping;
import com.wolmerica.service.userstate.UserStateService;
import com.wolmerica.service.userstate.DefaultUserStateService;
import com.wolmerica.service.vendor.VendorService;
import com.wolmerica.service.vendor.DefaultVendorService;
import com.wolmerica.tools.formatter.FormattingException;

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
import java.util.ArrayList;
import java.math.BigDecimal;

import org.apache.log4j.Logger;

public class VendorInvoiceItemGetAction extends Action {

  
  Logger cat = Logger.getLogger("WOWAPP");

  private UserStateService userStateService = new DefaultUserStateService();
  private VendorService VendorService = new DefaultVendorService();

  public UserStateService getUserStateService() {
      return userStateService;
  }

  public void setUserStateService(UserStateService userStateService) {
      this.userStateService = userStateService;
  }
  public VendorService getVendorService() {
      return VendorService;
  }

  public void setVendorService(VendorService VendorService) {
      this.VendorService = VendorService;
  }  

  private VendorInvoiceItemHeadDO getVendorInvoiceItems(HttpServletRequest request,
                                                          ActionForm form,
                                                          Integer viKey)
   throws Exception, SQLException {

    VendorInvoiceItemHeadDO formHDO = new VendorInvoiceItemHeadDO();
    VendorInvoiceItemDO vendorInvoiceItem = null;
    ArrayList<VendorInvoiceItemDO> vendorInvoiceItems = new ArrayList<VendorInvoiceItemDO>();

    DataSource ds = null;
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    Boolean viSalesTaxId = false;
    Boolean viCarryFactorId = false;
    Boolean viiSalesTaxId = false;
    BigDecimal bdReceiveQty = new BigDecimal("0");
    BigDecimal bdExtendCost = new BigDecimal("0");
    BigDecimal bdGrandTotal = new BigDecimal ("0");

    try {
      ds = getDataSource(request);
      conn = ds.getConnection();

      String query = "SELECT invoice_num,"
                   + "vendorinvoice.thekey AS vendorinvoice_key,"
                   + "vendorinvoice.active_id,"
                   + "vendorinvoice.genesis_key,"
                   + "vendor.name, "
                   + "vendor.thekey AS vendor_key, "
                   + "sales_tax_key,"
                   + "carry_factor_id "
                   + "FROM purchaseorder,vendorinvoice,vendor "
                   + "WHERE vendorinvoice.thekey = ? "
                   + "AND purchaseorder.thekey = purchaseorder_key "
                   + "AND vendor_key = vendor.thekey ";
      ps = conn.prepareStatement(query);
      ps.setInt(1, viKey);
      rs = ps.executeQuery();
      if ( rs.next() ) {
        formHDO.setInvoiceNumber(rs.getString("invoice_num"));
        formHDO.setVendorInvoiceKey(rs.getInt("vendorinvoice_key"));
        formHDO.setActiveId(rs.getBoolean("active_id"));
        formHDO.setCreditId(!(formHDO.getVendorInvoiceKey().equals(rs.getInt("genesis_key"))));
        formHDO.setVendorName(rs.getString("name"));
        formHDO.setVendorKey(rs.getInt("vendor_key"));
        viSalesTaxId = rs.getBoolean("sales_tax_key");
        viCarryFactorId = rs.getBoolean("carry_factor_id");
      }

      query = "SELECT vendorinvoiceitem.thekey,"
            + "vendorinvoiceitem.vendorinvoice_key,"
            + "vendorinvoiceitem.purchaseorderitem_key,"
            + "purchaseorderitem.itemdictionary_key,"
            + "itemdictionary.brand_name,"
            + "itemdictionary.size,"
            + "itemdictionary.size_unit,"
            + "itemdictionary.license_key_id,"
            + "vendorinvoiceitem.pending_qty,"
            + "vendorinvoiceitem.receive_qty,"
            + "vendorinvoiceitem.pending_qty - ABS(vendorinvoiceitem.receive_qty) AS backorder_qty,"
            + "vendorinvoiceitem.first_cost,"
            + "vendorinvoiceitem.variant_amount,"            
            + "vendorinvoiceitem.use_tax_cost,"
            + "vendorinvoiceitem.handling_cost,"
            + "vendorinvoiceitem.unit_cost,"
            + "vendorinvoiceitem.sales_tax_id,"
            + "vendorinvoiceitem.carry_factor,"
            + "vendorinvoiceitem.expiration_date,"
            + "vendorinvoiceitem.note_line1 "
            + "FROM vendorinvoiceitem, purchaseorderitem, itemdictionary "
            + "WHERE vendorinvoice_key = ? "
            + "AND vendorinvoiceitem.purchaseorderitem_key = purchaseorderitem.thekey "
            + "AND purchaseorderitem.itemdictionary_key = itemdictionary.thekey "
            + "ORDER by vendorinvoiceitem.thekey ";
      ps = conn.prepareStatement(query);
      ps.setInt(1, viKey);
      rs = ps.executeQuery();

      Integer recordCount = 0;
      while ( rs.next() ) {
        ++recordCount;

        cat.debug(this.getClass().getName() + ": Brand Name = " + rs.getString("brand_name"));

        vendorInvoiceItem = new VendorInvoiceItemDO();
        vendorInvoiceItem.setKey(rs.getInt("thekey"));
        vendorInvoiceItem.setVendorInvoiceKey(rs.getInt("vendorinvoice_key"));
        vendorInvoiceItem.setPurchaseOrderItemKey(rs.getInt("purchaseorderitem_key"));
        vendorInvoiceItem.setItemDictionaryKey(rs.getInt("itemdictionary_key"));
        vendorInvoiceItem.setBrandName(rs.getString("brand_name"));
        vendorInvoiceItem.setSize(rs.getBigDecimal("size"));
        vendorInvoiceItem.setSizeUnit(rs.getString("size_unit"));
        vendorInvoiceItem.setLicenseKeyId(rs.getBoolean("license_key_id"));
        vendorInvoiceItem.setOrderQty(rs.getShort("pending_qty"));
        vendorInvoiceItem.setReceiveQty(rs.getBigDecimal("receive_qty").abs().shortValue());
        vendorInvoiceItem.setBackOrderQty(rs.getShort("backorder_qty"));
        vendorInvoiceItem.setEnableSalesTaxId(viSalesTaxId);
        vendorInvoiceItem.setEnableCarryFactorId(viCarryFactorId);
        // Special condition of the vendor invoice being enabled for sales tax.
        viiSalesTaxId = rs.getBoolean("sales_tax_id");
        viiSalesTaxId = (viiSalesTaxId && viSalesTaxId);
        vendorInvoiceItem.setSalesTaxId(viiSalesTaxId);
        vendorInvoiceItem.setCarryFactor(rs.getBigDecimal("carry_factor"));
        vendorInvoiceItem.setExpirationDate(rs.getDate("expiration_date"));
        vendorInvoiceItem.setFirstCost(rs.getBigDecimal("first_cost"));
        vendorInvoiceItem.setVariantAmount(rs.getBigDecimal("variant_amount"));
        vendorInvoiceItem.setUseTaxCost(rs.getBigDecimal("use_tax_cost"));
        vendorInvoiceItem.setHandlingCost(rs.getBigDecimal("handling_cost"));
        vendorInvoiceItem.setUnitCost(rs.getBigDecimal("unit_cost"));
        bdReceiveQty = rs.getBigDecimal("receive_qty");
        bdExtendCost = bdReceiveQty.multiply(vendorInvoiceItem.getFirstCost().subtract(vendorInvoiceItem.getVariantAmount()));
        vendorInvoiceItem.setExtendCost(bdExtendCost);
        vendorInvoiceItem.setItemAction("on");
        bdGrandTotal = bdGrandTotal.add(bdExtendCost);
        vendorInvoiceItem.setNoteLine1(rs.getString("note_line1"));

//--------------------------------------------------------------------------------
// Look up the product key values if necessary.
//--------------------------------------------------------------------------------
        if (vendorInvoiceItem.getLicenseKeyId()) {
          vendorInvoiceItem.setLicenseKeyCount(getVendorService().getLicenseCount(conn,
                                                     getUserStateService().getFeatureKey().byteValue(),
                                                     vendorInvoiceItem.getKey()));
        }

        vendorInvoiceItems.add(vendorInvoiceItem);
      }

      // Store the Grand Total and the row list.
      formHDO.setInvoiceTotal(bdGrandTotal);
      formHDO.setLastRecord(recordCount);      
      //===========================================================================
      // A formatter issues exists during the populatin of empty lists.
      // A work around is to populate one row when there is an emptyList.
      // The cooresponding jsp also needs to check for the phantom row.
      //===========================================================================
      if (vendorInvoiceItems.isEmpty()) {
        vendorInvoiceItems.add(new VendorInvoiceItemDO());
      }
      formHDO.setVendorInvoiceItemForm(vendorInvoiceItems);
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
    return formHDO;
  }

    @Override
  public ActionForward execute(ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
   throws Exception, SQLException {

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
      if (!(request.getAttribute("key") == null)) {
        theKey = new Integer(request.getAttribute("key").toString());
      }
      else {
        if (!(request.getParameter("key") == null)) {
          theKey = new Integer(request.getParameter("key"));
        }
        else {
          throw new Exception("Request getParameter/getAttribute [key] not found!");
        }
      }
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
        VendorInvoiceItemHeadDO formHDO = getVendorInvoiceItems(request, form, theKey);
        formHDO.setPermissionStatus(usToken);
//--------------------------------------------------------------------------------
// Make sure that only the active vendor invoice is editable.
//--------------------------------------------------------------------------------
        if (formHDO.getActiveId() && (usToken.compareToIgnoreCase(getUserStateService().getLocked()) == 0))
          request.setAttribute(getUserStateService().getDisableEdit(), false);
        else
          request.setAttribute(getUserStateService().getDisableEdit(), true);
        request.getSession().setAttribute("vendorinvoiceitemHDO", formHDO);
//--------------------------------------------------------------------------------
// Create the wrapper object for vendorinvoiceitem.
//--------------------------------------------------------------------------------
        VendorInvoiceItemHeadForm formHStr = new VendorInvoiceItemHeadForm();
        formHStr.populate(formHDO);

        form = formHStr;
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