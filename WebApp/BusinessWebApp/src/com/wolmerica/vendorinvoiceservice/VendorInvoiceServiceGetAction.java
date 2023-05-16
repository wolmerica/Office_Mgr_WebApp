/*
 * VendorInvoiceServiceGetAction.java
 *
 * Created on November 08, 2008, 12:56 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package com.wolmerica.vendorinvoiceservice;

//--------------------------------------------------------------------------------
// @author Richard Wolschlager
//--------------------------------------------------------------------------------
import com.wolmerica.employee.EmployeesActionMapping;
import com.wolmerica.service.userstate.UserStateService;
import com.wolmerica.service.userstate.DefaultUserStateService;
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

public class VendorInvoiceServiceGetAction extends Action {

  Logger cat = Logger.getLogger("WOWAPP");

  private UserStateService userStateService = new DefaultUserStateService();

  public UserStateService getUserStateService() {
      return userStateService;
  }

  public void setUserStateService(UserStateService userStateService) {
      this.userStateService = userStateService;
  }


  private VendorInvoiceServiceHeadDO getVendorInvoiceServices(HttpServletRequest request,
                                                              ActionForm form,
                                                              Integer viKey)
   throws Exception, SQLException {

    VendorInvoiceServiceHeadDO formHDO = new VendorInvoiceServiceHeadDO();
    VendorInvoiceServiceDO vendorInvoiceService = null;
    ArrayList<VendorInvoiceServiceDO> vendorInvoiceServices = new ArrayList<VendorInvoiceServiceDO>();

    DataSource ds = null;
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    Boolean viServiceTaxId = false;
    Boolean visServiceTaxId = false;    
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
                   + "service_tax_key "                   
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
        viServiceTaxId = rs.getBoolean("service_tax_key");
      }

      query = "SELECT vendorinvoiceservice.thekey,"
            + "vendorinvoiceservice.vendorinvoice_key,"
            + "vendorinvoiceservice.purchaseorderservice_key,"
            + "purchaseorderservice.servicedictionary_key,"
            + "servicedictionary.name AS sd_name,"            
            + "purchaseorderservice.pricetype_key,"
            + "pricetype.name AS pt_name,"            
            + "vendorinvoiceservice.pending_qty,"
            + "vendorinvoiceservice.receive_qty,"
            + "vendorinvoiceservice.pending_qty - ABS(vendorinvoiceservice.receive_qty) AS backorder_qty,"
            + "vendorinvoiceservice.first_cost,"
            + "vendorinvoiceservice.variant_amount,"            
            + "vendorinvoiceservice.service_tax_cost,"
            + "vendorinvoiceservice.handling_cost,"
            + "vendorinvoiceservice.unit_cost,"
            + "vendorinvoiceservice.service_tax_id,"
            + "vendorinvoiceservice.note_line1 "
            + "FROM vendorinvoiceservice, purchaseorderservice, servicedictionary,  pricetype "
            + "WHERE vendorinvoice_key = ? "
            + "AND vendorinvoiceservice.purchaseorderservice_key = purchaseorderservice.thekey "
            + "AND purchaseorderservice.servicedictionary_key = servicedictionary.thekey "
            + "AND purchaseorderservice.pricetype_key = pricetype.thekey "            
            + "ORDER by vendorinvoiceservice.thekey ";
      ps = conn.prepareStatement(query);
      ps.setInt(1, viKey);
      rs = ps.executeQuery();

      Integer recordCount = 0;
      while ( rs.next() ) {
        ++recordCount;

        cat.debug(this.getClass().getName() + ": Service = " + rs.getString("sd_name"));

        vendorInvoiceService = new VendorInvoiceServiceDO();
        vendorInvoiceService.setKey(rs.getInt("thekey"));
        vendorInvoiceService.setVendorInvoiceKey(rs.getInt("vendorinvoice_key"));
        vendorInvoiceService.setPurchaseOrderServiceKey(rs.getInt("purchaseorderservice_key"));
        vendorInvoiceService.setServiceDictionaryKey(rs.getInt("servicedictionary_key"));
        vendorInvoiceService.setServiceName(rs.getString("sd_name"));
        vendorInvoiceService.setPriceTypeKey(rs.getByte("pricetype_key"));
        vendorInvoiceService.setPriceTypeName(rs.getString("pt_name"));
        vendorInvoiceService.setOrderQty(rs.getShort("pending_qty"));
        vendorInvoiceService.setReceiveQty(rs.getBigDecimal("receive_qty").abs().shortValue());
        vendorInvoiceService.setBackOrderQty(rs.getShort("backorder_qty"));
        vendorInvoiceService.setEnableServiceTaxId(viServiceTaxId);
        // Special condition of the vendor invoice being enabled for service tax.
        visServiceTaxId = rs.getBoolean("service_tax_id");
        visServiceTaxId = (visServiceTaxId && viServiceTaxId);
        vendorInvoiceService.setServiceTaxId(visServiceTaxId);
        vendorInvoiceService.setFirstCost(rs.getBigDecimal("first_cost"));
        vendorInvoiceService.setVariantAmount(rs.getBigDecimal("variant_amount"));        
        vendorInvoiceService.setServiceTaxCost(rs.getBigDecimal("service_tax_cost"));
        vendorInvoiceService.setHandlingCost(rs.getBigDecimal("handling_cost"));
        vendorInvoiceService.setUnitCost(rs.getBigDecimal("unit_cost"));
        bdReceiveQty = rs.getBigDecimal("receive_qty");
        bdExtendCost = bdReceiveQty.multiply(vendorInvoiceService.getFirstCost().subtract(vendorInvoiceService.getVariantAmount()));
        vendorInvoiceService.setExtendCost(bdExtendCost);
        vendorInvoiceService.setServiceAction("on");
        bdGrandTotal = bdGrandTotal.add(bdExtendCost);
        vendorInvoiceService.setNoteLine1(rs.getString("note_line1"));

        vendorInvoiceServices.add(vendorInvoiceService);
      }

      // Store the Grand Total and the row list.
      formHDO.setInvoiceTotal(bdGrandTotal);
      formHDO.setLastRecord(recordCount);      
      //===========================================================================
      // A formatter issues exists during the populatin of empty lists.
      // A work around is to populate one row when there is an emptyList.
      // The cooresponding jsp also needs to check for the phantom row.
      //===========================================================================
      if (vendorInvoiceServices.isEmpty()) {
        vendorInvoiceServices.add(new VendorInvoiceServiceDO());
      }
      formHDO.setVendorInvoiceServiceForm(vendorInvoiceServices);
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
        VendorInvoiceServiceHeadDO formHDO = getVendorInvoiceServices(request, form, theKey);
        formHDO.setPermissionStatus(usToken);
//--------------------------------------------------------------------------------
// Make sure that only the active vendor invoice is editable.
//--------------------------------------------------------------------------------
        if (formHDO.getActiveId() && (usToken.compareToIgnoreCase(getUserStateService().getLocked()) == 0))
          request.setAttribute(getUserStateService().getDisableEdit(), false);
        else
          request.setAttribute(getUserStateService().getDisableEdit(), true);
        request.getSession().setAttribute("vendorinvoiceserviceHDO", formHDO);
//--------------------------------------------------------------------------------
// Create the wrapper object for vendorinvoiceservice.
//--------------------------------------------------------------------------------
        VendorInvoiceServiceHeadForm formHStr = new VendorInvoiceServiceHeadForm();
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