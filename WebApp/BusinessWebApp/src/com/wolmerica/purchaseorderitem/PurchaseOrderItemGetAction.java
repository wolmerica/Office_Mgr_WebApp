/*
 * PurchaseOrderItemGetAction.java
 *
 * Created on September 9, 2005, 8:14 AM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package com.wolmerica.purchaseorderitem;

//--------------------------------------------------------------------------------
// @author Richard Wolschlager
//--------------------------------------------------------------------------------
import com.wolmerica.employee.EmployeesActionMapping;
import com.wolmerica.service.property.PropertyService;
import com.wolmerica.service.property.DefaultPropertyService;
import com.wolmerica.tools.formatter.FormattingException;
import com.wolmerica.service.userstate.UserStateService;
import com.wolmerica.service.userstate.DefaultUserStateService;
import com.wolmerica.util.common.EnumPurchaseOrderStatus;

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
import java.util.ArrayList;
import java.math.BigDecimal;

import org.apache.log4j.Logger;

public class PurchaseOrderItemGetAction extends Action {

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

  
  private PurchaseOrderItemHeadDO getPurchaseOrderItems(HttpServletRequest request,
                                                          ActionForm form,
                                                          Integer poKey,
                                                          Integer pageNo)
   throws Exception, SQLException {

    PurchaseOrderItemHeadDO formHDO = new PurchaseOrderItemHeadDO();
    PurchaseOrderItemDO purchaseOrderItem = null;
    ArrayList<PurchaseOrderItemDO> purchaseOrderItems = new ArrayList<PurchaseOrderItemDO>();

    DataSource ds = null;
    Connection conn = null;
    PreparedStatement ps = null;
    PreparedStatement ps2 = null;
    ResultSet rs = null;
    ResultSet rs2 = null;

    BigDecimal bdOrderQty = new BigDecimal("0");
    BigDecimal bdExtendCost = new BigDecimal("0");
    BigDecimal bdGrandTotal = new BigDecimal ("0");

    try {
      ds = getDataSource(request);
      conn = ds.getConnection();

      String query = "SELECT purchase_order_num,"
                   + "purchaseorder.order_status,"
                   + "vendor.thekey AS vendor_key,"
                   + "vendor.name "
                   + "FROM purchaseorder,vendor "
                   + "WHERE purchaseorder.thekey=? "
                   + "AND vendor_key = vendor.thekey";
      ps = conn.prepareStatement(query);

      ps.setInt(1, poKey);
      rs = ps.executeQuery();
      if ( rs.next() ) {
        formHDO.setPurchaseOrderKey(poKey);
        formHDO.setPurchaseOrderNumber(rs.getString("purchase_order_num"));
        formHDO.setOrderStatus(rs.getString("order_status"));
        formHDO.setVendorKey(rs.getInt("vendor_key"));
        formHDO.setVendorName(rs.getString("name"));
      }
      else {
        throw new Exception("PurchaseOrder " + poKey.toString() + " not found!");
      }

      query = "SELECT purchaseorderitem.thekey,"
            + "purchaseorderitem.purchaseorder_key,"
            + "purchaseorderitem.itemdictionary_key,"
            + "itemdictionary.item_num,"
            + "itemdictionary.brand_name,"
            + "itemdictionary.size,"
            + "itemdictionary.size_unit,"
            + "purchaseorderitem.order_qty,"
            + "itemdictionary.first_cost,"
            + "itemdictionary.lastftcost,"
            + "purchaseorderitem.note_line1,"
            + "purchaseorder.create_stamp "
            + "FROM purchaseorder, purchaseorderitem, itemdictionary "
            + "WHERE purchaseorder. thekey = ? "
            + "AND purchaseorder.thekey = purchaseorder_key "
            + "AND itemdictionary_key = itemdictionary.thekey "
            + "ORDER by purchaseorderitem.thekey ";
      ps = conn.prepareStatement(query);
      ps.setInt(1, poKey);
      rs = ps.executeQuery();

//--------------------------------------------------------------------------------
// Find out if the item is eligible for a rebate.
//--------------------------------------------------------------------------------
      query = "SELECT count(*) AS rebate_count "
            + "FROM rebate "
            + "WHERE itemdictionary_key = ? "
            + "AND DATE(?) BETWEEN start_date AND submit_date";
      ps = conn.prepareStatement(query);

//--------------------------------------------------------------------------------
// Find out if the purchase order item has any rebates assigned to it.
//--------------------------------------------------------------------------------
      query = "SELECT count(*) AS rebate_count "
            + "FROM rebateinstance "
            + "WHERE purchaseorderitem_key = ? ";
//            + "AND NOT complete_id";
      ps2 = conn.prepareStatement(query);

      Integer pageMax = new Integer(getPropertyService().getCustomerProperties(request,"purchaseorderitem.list.size"));
      Integer firstRecord = ((pageNo.intValue() - 1) * pageMax) + 1;
      Integer lastRecord = firstRecord + (pageMax - 1);
      Short minQty = 0;
      Short maxQty = 0;
      Integer recordCount = 0;
//--------------------------------------------------------------------------------
// Begin to process the purchase order item rows.
//--------------------------------------------------------------------------------
      while ( rs.next() ) {
        ++recordCount;
        if ((recordCount >= firstRecord) && (recordCount <= lastRecord)) {
          purchaseOrderItem = new PurchaseOrderItemDO();
          purchaseOrderItem.setKey(rs.getInt("thekey"));
          purchaseOrderItem.setItemDictionaryKey(rs.getInt("itemdictionary_key"));
          purchaseOrderItem.setItemNum(rs.getString("item_num"));
          purchaseOrderItem.setBrandName(rs.getString("brand_name"));
          purchaseOrderItem.setSize(rs.getBigDecimal("size"));
          purchaseOrderItem.setSizeUnit(rs.getString("size_unit"));
          purchaseOrderItem.setOrderQty(rs.getShort("order_qty"));
          bdOrderQty = rs.getBigDecimal("order_qty");
          purchaseOrderItem.setFirstCost(rs.getBigDecimal("first_cost"));
          purchaseOrderItem.setNoteLine1(rs.getString("note_line1"));
//--------------------------------------------------------------------------------
// Find out if the item is eligible for a rebate.
//--------------------------------------------------------------------------------
          ps.setInt(1, purchaseOrderItem.getItemDictionaryKey());
          ps.setTimestamp(2, rs.getTimestamp("create_stamp"));
          rs2 = ps.executeQuery();
          if ( rs2.next() ) {
            purchaseOrderItem.setRebateCount(rs2.getShort("rebate_count"));
          }
          else {
            throw new Exception("Rebate " + purchaseOrderItem.getItemDictionaryKey() + " not found!");
          }
//--------------------------------------------------------------------------------
// Find out if the purchase order item has any rebates assigned to it.
//--------------------------------------------------------------------------------
          ps2.setInt(1, purchaseOrderItem.getKey());
          rs2 = ps2.executeQuery();
          if ( rs2.next() ) {
            purchaseOrderItem.setRebateInstanceCount(rs2.getShort("rebate_count"));
          }
          else {
            throw new Exception("Rebate Instance " + purchaseOrderItem.getItemDictionaryKey() + " not found!");
          }

          bdExtendCost = purchaseOrderItem.getFirstCost().multiply(bdOrderQty);
          purchaseOrderItem.setExtendCost(bdExtendCost);
          purchaseOrderItem.setItemAction("on");

          bdGrandTotal = bdGrandTotal.add(bdExtendCost);
          purchaseOrderItems.add(purchaseOrderItem);
        }
      }
      // Store the Grand Total and the row list.
      formHDO.setItemTotal(bdGrandTotal);
//--------------------------------------------------------------------------------
// Pagination logic to figure out what the previous and next page
// values will be for the next screen to be displayed.
//--------------------------------------------------------------------------------
      Integer prevPage=0;
      Integer nextPage=0;
      if (recordCount > lastRecord)
        nextPage = pageNo + 1;
      else
        lastRecord = recordCount;
      if (firstRecord > 1)
        prevPage = pageNo - 1;
      if (recordCount == 0)
        firstRecord=0;
//--------------------------------------------------------------------------------
// Store the filter, row count, previous and next page number values.
//--------------------------------------------------------------------------------
      formHDO.setRecordCount(recordCount);
      formHDO.setFirstRecord(firstRecord);
      formHDO.setLastRecord(lastRecord);
      formHDO.setPreviousPage(prevPage);
      formHDO.setNextPage(nextPage);
//--------------------------------------------------------------------------------
// A formatter issues exists during the population of empty lists.
// A work around is to populate one row when there is an emptyList.
// The cooresponding jsp also needs to check for the phantom row.
//--------------------------------------------------------------------------------
      if (purchaseOrderItems.isEmpty()) {
        purchaseOrderItems.add(new PurchaseOrderItemDO());
      }
      formHDO.setPurchaseOrderItemForm(purchaseOrderItems);
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
      if (rs2 != null) {
        try {
          rs2.close();
        }
        catch (SQLException sqle) {
          cat.error(this.getClass().getName() + ": SQLException : " + sqle.getMessage());
        }
        rs2 = null;
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
      if (ps2 != null) {
        try {
          ps2.close();
        }
        catch (SQLException sqle) {
            cat.error(this.getClass().getName() + ": SQLException : " + sqle.getMessage());
        }
        ps2 = null;
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
   throws Exception, IOException, SQLException, ServletException {

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

      Integer pageNo = new Integer("1");
      if (!(request.getParameter("pageNo") == null)) {
        pageNo = new Integer(request.getParameter("pageNo").toString());
        if (pageNo < 0)
          pageNo = new Integer("1");
      }

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
        PurchaseOrderItemHeadDO formHDO = getPurchaseOrderItems(request,
                                                                form,
                                                                theKey,
                                                                pageNo);
        formHDO.setPermissionStatus(usToken);
//--------------------------------------------------------------------------------
// Special scenario to make sure that only the new P.O. is editable.
// Set the disableEdit attribute default to true for P.O.
//--------------------------------------------------------------------------------
        if (formHDO.getOrderStatus().equalsIgnoreCase(EnumPurchaseOrderStatus.N.getValue()) && (usToken.compareToIgnoreCase(getUserStateService().getLocked()) == 0))
          request.setAttribute("disableEdit", false);
        else
          request.setAttribute(getUserStateService().getDisableEdit(), true);
        request.getSession().setAttribute("purchaseorderitemHDO", formHDO);
//--------------------------------------------------------------------------------
// Create the wrapper object for vendorinvoiceitem.
//--------------------------------------------------------------------------------
        PurchaseOrderItemHeadForm formHStr = new PurchaseOrderItemHeadForm();
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
