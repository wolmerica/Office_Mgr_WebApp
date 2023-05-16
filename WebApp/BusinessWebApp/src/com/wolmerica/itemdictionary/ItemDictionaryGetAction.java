/*
 * ItemDictionaryGetAction.java
 *
 * Created on August 30, 2005, 4:58 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 * 12/22/2005 Implement tools.formatter library.
 */

package com.wolmerica.itemdictionary;

//--------------------------------------------------------------------------------
// @author Richard Wolschlager
//--------------------------------------------------------------------------------
import com.wolmerica.employee.EmployeesActionMapping;
import com.wolmerica.service.attachment.AttachmentService;
import com.wolmerica.service.attachment.DefaultAttachmentService;
import com.wolmerica.service.itemandsrv.ItemAndSrvService;
import com.wolmerica.service.itemandsrv.DefaultItemAndSrvService;
import com.wolmerica.service.property.PropertyService;
import com.wolmerica.service.property.DefaultPropertyService;
import com.wolmerica.service.rebate.RebateService;
import com.wolmerica.service.rebate.DefaultRebateService;
import com.wolmerica.service.userstate.UserStateService;
import com.wolmerica.service.userstate.DefaultUserStateService;
import com.wolmerica.service.vendor.VendorService;
import com.wolmerica.service.vendor.DefaultVendorService;
import com.wolmerica.tools.formatter.FormattingException;
import com.wolmerica.customertype.CustomerTypeDropList;

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

import org.apache.log4j.Logger;

public class ItemDictionaryGetAction extends Action {

  Logger cat = Logger.getLogger("WOWAPP");
  
  private AttachmentService attachmentService = new DefaultAttachmentService();
  private ItemAndSrvService itemAndSrvService = new DefaultItemAndSrvService();
  private PropertyService propertyService = new DefaultPropertyService();
  private RebateService rebateService = new DefaultRebateService();
  private UserStateService userStateService = new DefaultUserStateService();
  private VendorService vendorService = new DefaultVendorService();
  
  public AttachmentService getAttachmentService() {
      return attachmentService;
  }

  public void setAttachmentService(AttachmentService attachmentService) {
      this.attachmentService = attachmentService;
  }

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

  public RebateService getRebateService() {
      return rebateService;
  }

  public void setRebateService(RebateService rebateService) {
      this.rebateService = rebateService;
  }

  public UserStateService getUserStateService() {
      return userStateService;
  }

  public void setUserStateService(UserStateService userStateService) {
      this.userStateService = userStateService;
  }

  public VendorService getVendorService() {
      return vendorService;
  }

  public void setVendorService(VendorService vendorService) {
      this.vendorService = vendorService;
  }

  private ItemDictionaryDO buildItemDictionaryForm(HttpServletRequest request,
                                                   Integer idKey)
    throws Exception, SQLException {

    ItemDictionaryDO formDO = null;
    ArrayList customerTypeRows = new ArrayList();

    DataSource ds = null;
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    try {
      ds = getDataSource(request);
      conn = ds.getConnection();

      Integer forecastDays = new Integer(getPropertyService().getCustomerProperties(request,"itemdictionary.forecast.days"));

//--------------------------------------------------------------------------------
// Prepare a SQL query to get the item dictionary details.
//--------------------------------------------------------------------------------
      String query = "SELECT brand_name,"
                   + "size,"
                   + "size_unit,"
                   + "generic_name,"
                   + "item_num,"
                   + "profile_num,"
                   + "manufacturer,"
                   + "vendor_key,"
                   + "vendor_specific_id,"
                   + "customertype_key,"
                   + "license_key_id,"
                   + "report_id,"
                   + "item_name,"
                   + "dose,"
                   + "dose_unit,"
                   + "other,"
                   + "item_memo,"
                   + "carry_factor,"
                   + "percent_use,"
                   + "first_cost,"
                   + "lastftcost,"
                   + "unit_cost,"
                   + "lastuncost,"
                   + "muadd,"
                   + "muvendor,"
                   + "label_cost,"
                   + "order_threshold,"
                   + "create_user,"
                   + "create_stamp,"
                   + "update_user,"
                   + "update_stamp "
                   + "FROM itemdictionary "
                   + "WHERE thekey = ?";
      ps = conn.prepareStatement(query);
      ps.setInt(1, idKey);
      rs = ps.executeQuery();
      if ( rs.next() ) {

        formDO = new ItemDictionaryDO();

        formDO.setKey(idKey);
        formDO.setBrandName(rs.getString("brand_name"));
        formDO.setGenericName(rs.getString("generic_name"));
        formDO.setItemNum(rs.getString("item_num"));
        formDO.setProfileNum(rs.getString("profile_num"));
        formDO.setManufacturer(rs.getString("manufacturer"));
        formDO.setVendorKey(rs.getInt("vendor_key"));
        formDO.setVendorSpecificId(rs.getByte("vendor_specific_id"));
        formDO.setCustomerTypeKey(rs.getByte("customertype_key"));
        formDO.setLicenseKeyId(rs.getByte("license_key_id"));
        formDO.setReportId(rs.getByte("report_id"));
        formDO.setItemName(rs.getString("item_name"));
        formDO.setSize(rs.getBigDecimal("size"));
        formDO.setSizeUnit(rs.getString("size_unit"));
        formDO.setDose(rs.getBigDecimal("dose"));
        formDO.setDoseUnit(rs.getString("dose_unit"));
        formDO.setOther(rs.getString("other"));
        formDO.setItemMemo(rs.getString("item_memo"));
        formDO.setCarryFactor(rs.getBigDecimal("carry_factor"));
        formDO.setPercentUse(rs.getBigDecimal("percent_use"));
        formDO.setFirstCost(rs.getBigDecimal("first_cost"));
        formDO.setPrevFirstCost(rs.getBigDecimal("lastftcost"));
        formDO.setUnitCost(rs.getBigDecimal("unit_cost"));
        formDO.setPrevUnitCost(rs.getBigDecimal("lastuncost"));
        formDO.setMuAdditional(rs.getBigDecimal("muadd"));
        formDO.setMuVendor(rs.getBigDecimal("muvendor"));
        formDO.setLabelCost(rs.getBigDecimal("label_cost"));
        formDO.setOrderThreshold(rs.getBigDecimal("order_threshold"));
        formDO.setCreateUser(rs.getString("create_user"));
        formDO.setCreateStamp(rs.getTimestamp("create_stamp"));
        formDO.setUpdateUser(rs.getString("update_user"));
        formDO.setUpdateStamp(rs.getTimestamp("update_stamp"));
        if (request.getParameter("brandNameFilter") != null)
          formDO.setBrandNameFilter(request.getParameter("brandNameFilter"));
        if (request.getParameter("itemNumFilter") != null)
          formDO.setItemNumFilter(request.getParameter("itemNumFilter"));
        if (request.getParameter("genericNameFilter") != null)
          formDO.setGenericNameFilter(request.getParameter("genericNameFilter"));
        if (request.getParameter("idPageNo") != null)
          formDO.setCurrentPage(new Integer(request.getParameter("idPageNo")));

//--------------------------------------------------------------------------------
// Get the count of attachments associated with this ITEM DICTIONARY.
//--------------------------------------------------------------------------------
        formDO.setAttachmentCount(getAttachmentService().getAttachmentCount(conn,
                                                        getUserStateService().getFeatureKey().byteValue(),
                                                        formDO.getKey()));
      }
      else {
        throw new Exception("ItemDictionary  " + idKey.toString() + " not found!");
      }

//--------------------------------------------------------------------------------
// Get the quantity on hand, ordered quantity, and forecast quantity values.
//--------------------------------------------------------------------------------
      formDO = getItemAndSrvService().getItemInventory(conn,
                                                       formDO,
                                                       forecastDays);

//--------------------------------------------------------------------------------
// Find out if the item is eligible for a rebate.
//--------------------------------------------------------------------------------
      formDO.setRebateCount(getRebateService().getRebateCountByItem(conn,
                                                                    idKey));

//--------------------------------------------------------------------------------
// Find out if the purchase order item has any rebates assigned to it.
//--------------------------------------------------------------------------------
      formDO.setRebateInstanceCount(getRebateService().getRebateInstanceCountByItem(conn,
                                                                                    idKey));

//--------------------------------------------------------------------------------
// Retrieve the active customer type values available to assign to a item.
//--------------------------------------------------------------------------------
      CustomerTypeDropList ctdl = new CustomerTypeDropList();
      customerTypeRows = ctdl.getActiveCustomerTypeList(conn);
      formDO.setCustomerTypeForm(customerTypeRows);

//--------------------------------------------------------------------------------
// Get the name of the preferred vendor for the item.
//--------------------------------------------------------------------------------
      formDO.setVendorName(getVendorService().getVendorName(conn,
                                            formDO.getVendorKey()));
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
      request.setAttribute("key", theKey.toString());

//--------------------------------------------------------------------------------
// Price adjustment - Passes row id via the request attribute.
//--------------------------------------------------------------------------------
      Byte theRow = null;
      if (!(request.getParameter("row") == null)) {
        theRow = new Byte(request.getParameter("row"));
      }
      else {
        if (!(request.getAttribute("row") == null)) {
          theRow = new Byte(request.getAttribute("row").toString());
        }
      }
      if (theRow != null)
        request.setAttribute("row", theRow.toString());

//--------------------------------------------------------------------------------
// Check if a customer type key via the request attribute.
//--------------------------------------------------------------------------------
      Byte ctKey = null;
      if (!(request.getParameter("ctkey") == null)) {
        ctKey = new Byte(request.getParameter("ctkey"));
      }
      else {
        if (!(request.getAttribute("ctkey") == null)) {
          ctKey = new Byte(request.getAttribute("ctkey").toString());
        }
      }
      if (ctKey != null) {
        request.setAttribute("ctkey", ctKey.toString());
        target = new String("pricing");
      }

//--------------------------------------------------------------------------------
// Check if a price type key via the request attribute.
//--------------------------------------------------------------------------------
      Byte ptKey = null;
      if (!(request.getParameter("ptkey") == null)) {
        ptKey = new Byte(request.getParameter("ptkey"));
      }
      else {
        if (!(request.getAttribute("ptkey") == null)) {
          ptKey = new Byte(request.getAttribute("ptkey").toString());
        }
      }
      if (ptKey != null) {
        request.setAttribute("ptkey", ptKey.toString());
        target = new String("pricing");
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
        ItemDictionaryDO formDO = buildItemDictionaryForm(request, theKey);
        formDO.setPermissionStatus(usToken);
        request.getSession().setAttribute("itemdictionary", formDO);
        ItemDictionaryForm formStr = new ItemDictionaryForm();
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