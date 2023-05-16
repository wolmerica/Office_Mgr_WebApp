/*
 * ItemDictionaryEditAction.java
 *
 * Created on August 23, 2005, 11:30 PM
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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.math.BigDecimal;

import org.apache.log4j.Logger;

public class ItemDictionaryEditAction extends Action {

  Logger cat = Logger.getLogger("WOWAPP");

  private ItemAndSrvService itemAndSrvService = new DefaultItemAndSrvService();
  private UserStateService userStateService = new DefaultUserStateService();

  public ItemAndSrvService getItemAndSrvService() {
      return itemAndSrvService;
  }

  public void setItemAndSrvService(ItemAndSrvService itemAndSrvService) {
      this.itemAndSrvService = itemAndSrvService;
  }

  public UserStateService getUserStateService() {
      return userStateService;
  }

  public void setUserStateService(UserStateService userStateService) {
      this.userStateService = userStateService;
  }


  private Integer updateItemDictionary(HttpServletRequest request, ActionForm form)
    throws Exception, SQLException {

    String user = null;
    DataSource ds = null;
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;
    Integer idKey = 1;

    try {
      ds = getDataSource(request);
      conn = ds.getConnection();

      ItemDictionaryDO formDO = (ItemDictionaryDO) request.getSession().getAttribute("itemdictionary");

//--------------------------------------------------------------------------------
// Check the item dictionary table for any duplicate brand_name, size, and size unit.
// As well as item_num and profile_num values.
//-------------------------------------------------------------------------------- 
      try {
        idKey =  getItemAndSrvService().GetDupItemDictionary(conn, formDO);
      }
      catch (Exception e) {
        cat.error(this.getClass().getName() + ": Exception : " + e.getMessage());
        throw new Exception("updateItemDictionary() " + e.getMessage());
      }

      if (idKey >= 0) {
//---------------------------------------------------------------------------
// No update needed for brand_name, size, and size_unit.
// As well as create_user and create_stamp.
//---------------------------------------------------------------------------
        String query = "UPDATE itemdictionary SET "
                     + "brand_name=?,"
                     + "size_unit=?,"
                     + "generic_name=?,"
                     + "item_num=?,"
                     + "profile_num=? collate utf8_unicode_ci,"
                     + "manufacturer=?,"
                     + "vendor_key=?,"
                     + "vendor_specific_id=?,"
                     + "customertype_key=?,"
                     + "license_key_id=?,"
                     + "report_id=?,"
                     + "item_name=?,"
                     + "dose=?,"
                     + "dose_unit=?,"
                     + "other=?,"
                     + "item_memo=?,"
                     + "carry_factor=?,"
                     + "percent_use=?,"
                     + "first_cost=?,"
                     + "unit_cost=?,"
                     + "muadd=?,"
                     + "muvendor=?,"
                     + "label_cost=?,"
                     + "order_threshold=?,"
                     + "update_user=?,"
                     + "update_stamp=CURRENT_TIMESTAMP "
                     + "WHERE thekey = ?";
        ps = conn.prepareStatement(query);

        cat.debug("Key().......: " + formDO.getKey());
        cat.debug("BrandName().: " + formDO.getBrandName().toUpperCase());
        cat.debug("Size()......: " + formDO.getSize());
        cat.debug("SizeUnit()..: " + formDO.getSizeUnit().toUpperCase());
        cat.debug("ItemNum()...: " + formDO.getItemNum().toUpperCase());
        cat.debug("ProfileNum..: " + formDO.getProfileNum().toUpperCase());
        cat.debug("firstCost...: " + formDO.getFirstCost().setScale(2, BigDecimal.ROUND_HALF_UP));
        cat.debug("orderThreshold: " + formDO.getOrderThreshold().setScale(2, BigDecimal.ROUND_HALF_UP));

        ps.setString(1, formDO.getBrandName());
        ps.setString(2, formDO.getSizeUnit());
        ps.setString(3, formDO.getGenericName());
        ps.setString(4, formDO.getItemNum());
        ps.setString(5, formDO.getProfileNum());
        ps.setString(6, formDO.getManufacturer());
        ps.setInt(7, formDO.getVendorKey());
        ps.setByte(8, formDO.getVendorSpecificId());
        ps.setByte(9, formDO.getCustomerTypeKey());
        ps.setByte(10, formDO.getLicenseKeyId());
        ps.setByte(11, formDO.getReportId());
        ps.setString(12, formDO.getItemName());
        ps.setBigDecimal(13, formDO.getDose().setScale(2, BigDecimal.ROUND_HALF_UP));
        ps.setString(14, formDO.getDoseUnit());
        ps.setString(15, formDO.getOther());
        ps.setString(16, formDO.getItemMemo());
        ps.setBigDecimal(17, formDO.getCarryFactor().setScale(2, BigDecimal.ROUND_HALF_UP));
        ps.setBigDecimal(18, formDO.getPercentUse().setScale(2, BigDecimal.ROUND_HALF_UP));
        ps.setBigDecimal(19, formDO.getFirstCost().setScale(2, BigDecimal.ROUND_HALF_UP));
        ps.setBigDecimal(20, formDO.getUnitCost().setScale(2, BigDecimal.ROUND_HALF_UP));
        ps.setBigDecimal(21, formDO.getMuAdditional().setScale(2, BigDecimal.ROUND_HALF_UP));
        ps.setBigDecimal(22, formDO.getMuVendor().setScale(2, BigDecimal.ROUND_HALF_UP));
        ps.setBigDecimal(23, formDO.getLabelCost().setScale(2, BigDecimal.ROUND_HALF_UP));
        ps.setBigDecimal(24, formDO.getOrderThreshold().setScale(2, BigDecimal.ROUND_HALF_UP));
        ps.setString(25, request.getSession().getAttribute("USERNAME").toString());
        ps.setInt(26, formDO.getKey());
        ps.executeUpdate();
        cat.debug(this.getClass().getName() + ": post orderThreshold = " + formDO.getOrderThreshold().setScale(2, BigDecimal.ROUND_HALF_UP));
      }
    }
    catch (SQLException e) {
      cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
      throw new Exception("updateItemDictionary() " + e.getMessage());
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
    return idKey;
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
      request.setAttribute("key", theKey.toString());

      // Price adjustment - Passes row id via the request attribute.
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

      // Check if a customer type key via the request attribute.
      Byte ctKey = null;
      if (!(request.getParameter("ctkey") == null)) {
        ctKey = new Byte(request.getParameter("ctkey"));
      }
      else {
        if (!(request.getAttribute("ctkey") == null)) {
          ctKey = new Byte(request.getAttribute("ctkey").toString());
        }
      }
      if (ctKey != null)
        request.setAttribute("ctkey", ctKey.toString());

      // Check if a price type key via the request attribute.
      Byte ptKey = null;
      if (!(request.getParameter("ptkey") == null)) {
        ptKey = new Byte(request.getParameter("ptkey"));
      }
      else {
        if (!(request.getAttribute("ptkey") == null)) {
          ptKey = new Byte(request.getAttribute("ptkey").toString());
        }
      }
      if (ptKey != null)
        request.setAttribute("ptkey", ptKey.toString());

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

      theKey = updateItemDictionary(request, form);
//--------------------------------------------------------------------------------
// A item dictionary key of null indicates a duplicate in the brand name/size/size unit.
//--------------------------------------------------------------------------------
      if (theKey < 1) {
        target = "invalid";
        ActionMessages errors = new ActionMessages();
        if (theKey == -1) {
          errors.add("brandName", new ActionMessage("errors.duplicate"));
          errors.add("size", new ActionMessage("errors.duplicate"));
          errors.add("sizeUnit", new ActionMessage("errors.duplicate"));
        }
        if (theKey == -2) {
          errors.add("itemNum", new ActionMessage("errors.duplicate"));
        }
        if (theKey == -3) {
          errors.add("profileNum", new ActionMessage("errors.duplicate"));
        }
        saveErrors(request, errors);
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
