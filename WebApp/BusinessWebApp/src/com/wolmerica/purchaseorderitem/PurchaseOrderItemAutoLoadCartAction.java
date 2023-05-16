/*
 * PurchaseOrderItemAutoLoadCartAction.java
 *
 * Created on July 26, 2007, 9:16 PM
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
import com.wolmerica.itemdictionary.ItemDictionaryDO;
import com.wolmerica.service.property.PropertyService;
import com.wolmerica.service.property.DefaultPropertyService;
import com.wolmerica.service.itemandsrv.ItemAndSrvService;
import com.wolmerica.service.itemandsrv.DefaultItemAndSrvService;
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
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;

public class PurchaseOrderItemAutoLoadCartAction extends Action {

  Logger cat = Logger.getLogger("WOWAPP");

  private ItemAndSrvService itemAndSrvService = new DefaultItemAndSrvService();
  private PropertyService propertyService = new DefaultPropertyService();
  private UserStateService userStateService = new DefaultUserStateService();

  public ItemAndSrvService getItemAndSrvService() {
      return itemAndSrvService;
  }

  public void setItemAndSrvService(ItemAndSrvService itemAndSrvService) {
      this.itemAndSrvService = itemAndSrvService;
  }

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


  private void autoInsertPurchaseOrderItems(HttpServletRequest request,
                                            ActionForm form,
                                            Integer poKey)
   throws Exception, SQLException {

    DataSource ds = null;
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    try {
      ds = getDataSource(request);
      conn = ds.getConnection();

//--------------------------------------------------------------------------------
// Prepare a SQL query to selected the preferred vendor's rows from item dictionary.
//--------------------------------------------------------------------------------
      String query = "SELECT thekey,"
                   + "brand_name,"
                   + "size,"
                   + "size_unit,"
                   + "generic_name,"
                   + "item_num,"
                   + "manufacturer,"
                   + "report_id,"
                   + "item_name,"
                   + "dose,"
                   + "dose_unit,"
                   + "first_cost "
                   + "FROM itemdictionary "
                   + "WHERE vendor_key IN (SELECT vendor_key "
                                        + "FROM purchaseorder "
                                        + "WHERE thekey = ?) "
                   + "ORDER by brand_name";
      ps = conn.prepareStatement(query);
      ps.setInt(1, poKey);      
      rs = ps.executeQuery();

//--------------------------------------------------------------------------------
// Call a procedure with four IN parameters for auto load.
//--------------------------------------------------------------------------------
      CallableStatement cStmt = conn.prepareCall("{call SetPurchaseOrderItemAutoLoad(?,?,?,?)}");
      
//--------------------------------------------------------------------------------
// Instantiate the item and service method to retrieve threshold quantity
// inventory quantity, ordered quantity, and forecast quantity
//--------------------------------------------------------------------------------
      ItemDictionaryDO formDO = new ItemDictionaryDO();
      Integer forecastDays = new Integer(getPropertyService().getCustomerProperties(request,"itemdictionary.forecast.days"));

//--------------------------------------------------------------------------------
// Get the quantity on hand, ordered quantity, and forecast quantity values.
//--------------------------------------------------------------------------------
      Integer poItemQty = 0;
//--------------------------------------------------------------------------------
// Begin to process the purchase order item rows.
//--------------------------------------------------------------------------------
      while ( rs.next() ) {
        formDO.setKey(rs.getInt("thekey"));
        formDO.setBrandName(rs.getString("brand_name"));
        formDO.setGenericName(rs.getString("generic_name"));
        formDO.setItemNum(rs.getString("item_num"));
        formDO.setManufacturer(rs.getString("manufacturer"));
        formDO.setReportId(rs.getByte("report_id"));
        formDO.setItemName(rs.getString("item_name"));
        formDO.setSize(rs.getBigDecimal("size"));
        formDO.setSizeUnit(rs.getString("size_unit"));
        formDO.setDose(rs.getBigDecimal("dose"));
        formDO.setDoseUnit(rs.getString("dose_unit"));
        formDO.setFirstCost(rs.getBigDecimal("first_cost"));
//--------------------------------------------------------------------------------
// Retrieve the threshold, inventory, ordered, and forecast quantity for the item.
//--------------------------------------------------------------------------------
        formDO = getItemAndSrvService().getItemInventory(conn,
                                                         formDO,
                                                         forecastDays);

//--------------------------------------------------------------------------------
// Retrieve the recommended purchase order quantity from the current inventory state.
//--------------------------------------------------------------------------------
        poItemQty = getItemAndSrvService().getRecommendedPOQuantity(formDO.getKey(),
                                                     formDO.getQtyOnHand().intValue(),
                                                     formDO.getOrderedQty().intValue(),
                                                     formDO.getForecastQty().intValue(),
                                                     formDO.getOrderThreshold().intValue());

//--------------------------------------------------------------------------------
// Condition to add to P.O only if the recommended P.O. quantity is greater than zero.
//--------------------------------------------------------------------------------
        if (poItemQty > 0) {
          cat.debug(this.getClass().getName() + ": BrandName : " + formDO.getBrandName());
          cat.debug(this.getClass().getName() + ": Size/Unit : " + formDO.getSize() + formDO.getSizeUnit());
          cat.debug(this.getClass().getName() + ": poItemQty : " + poItemQty);

//--------------------------------------------------------------------------------
// Insert or update the low quantity items to be included in this order.
//--------------------------------------------------------------------------------
          cStmt.setInt(1, poKey);
          cStmt.setInt(2, formDO.getKey());
          cStmt.setInt(3, poItemQty);
          cStmt.setString(4, request.getSession().getAttribute("USERNAME").toString());

//--------------------------------------------------------------------------------
// Execute the SetPurchaseOrderItemAutoLoad stored procedure.
//--------------------------------------------------------------------------------
          cStmt.execute();
        }
      }

    }
    catch (SQLException e) {
      cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
      throw new Exception("autoInsertPurchaseOrderItems() " + e.getMessage());
    }
    finally {
      if (rs != null) {
        try {
          rs.close();
        }
        catch (SQLException e) {
          cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
          throw new Exception("autoInsertPurchaseOrderItems() " + e.getMessage());
        }
        rs = null;
      }
      if (ps != null) {
        try {
          ps.close();
        }
        catch (SQLException e) {
          cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
          throw new Exception("autoInsertPurchaseOrderItems() " + e.getMessage());
        }
        ps = null;
      }
      if (conn != null) {
        try {
          conn.close();
        }
        catch (SQLException e) {
            cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
            throw new Exception("autoInsertPurchaseOrderItems() " + e.getMessage());
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
      if (request.getParameter("key") != null) {
        theKey = new Integer(request.getParameter("key"));
      }
      else {
        if (request.getAttribute("key") != null) {
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

      autoInsertPurchaseOrderItems(request,
                                   form,
                                   theKey);
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
