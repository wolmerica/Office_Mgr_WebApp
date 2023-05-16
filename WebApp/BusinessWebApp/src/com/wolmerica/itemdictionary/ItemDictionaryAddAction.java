/*
 * ItemDictionaryAddAction.java
 *
 * Created on August 24, 2005, 2:17 PM
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

public class ItemDictionaryAddAction extends Action {

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


  private Integer insertItemDictionary(HttpServletRequest request, ActionForm form)
    throws Exception, SQLException {

    DataSource ds = null;
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    Integer idKey = 1;

    try {
      ItemDictionaryDO formDO = (ItemDictionaryDO) request.getSession().getAttribute("itemdictionary");

      ds = getDataSource(request);
      conn = ds.getConnection();
//--------------------------------------------------------------------------------
// Check the item dictionary table for any duplicate brand_name, size, and size unit.
// As well as item_num and profile_num values.
//--------------------------------------------------------------------------------
      try {
        idKey =  getItemAndSrvService().GetDupItemDictionary(conn, formDO);
      }
      catch (Exception e) {
        cat.error(this.getClass().getName() + ": Exception : " + e.getMessage());
        throw new Exception("insertItemDictionary() " + e.getMessage());
      }

      if (idKey >= 0) {
        cat.debug(this.getClass().getName() + ": No record found in Item Dictionary : ");
//--------------------------------------------------------------------------------
// Get the maximum key from the item dictionary.
//--------------------------------------------------------------------------------
        String query = "SELECT COUNT(*) AS id_cnt, MAX(thekey)+1 AS id_key "
                     + "FROM itemdictionary";
        ps = conn.prepareStatement(query);
//--------------------------------------------------------------------------------
// Query to retrieve the maximum key value in the customer invoice
// item table.  We then increase the maximum by one before insertion.
//--------------------------------------------------------------------------------
        rs = ps.executeQuery();
        if ( rs.next() ) {
          if ( rs.getInt("id_cnt") > 0 )
            idKey = rs.getInt("id_key");
          else
            idKey = 1;
        }
        else {
          throw new Exception("ItemDictionary MAX() not found!");
        }

//--------------------------------------------------------------------------------
// Set the profileNum value to the serviceKey when equals "New"
//--------------------------------------------------------------------------------
        if (formDO.getProfileNum().equalsIgnoreCase("New"))
          formDO.setProfileNum(idKey.toString());

//--------------------------------------------------------------------------------
// Prepare a query to insert a new row into the item dictionary table.
//--------------------------------------------------------------------------------
        query = "INSERT INTO itemdictionary(thekey,"
              + "brand_name,"
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
              + "update_stamp) "
              + "VALUES (?,?,?,?,?,?,?,?,?,?,"
              + "?,?,?,?,?,?,?,?,ROUND(?,2),ROUND(?,2),"
              + "?,?,?,?,?,?,?,?,?,CURRENT_TIMESTAMP,?,CURRENT_TIMESTAMP)";
        ps = conn.prepareStatement(query);

        cat.debug(this.getClass().getName() + ": firstCost = " + formDO.getFirstCost());
        cat.debug(this.getClass().getName() + ": unitCost = " + formDO.getUnitCost());
//--------------------------------------------------------------------------------
// Insert a new row into the item dictionary table.
//--------------------------------------------------------------------------------
        ps.setInt(1, idKey);
        ps.setString(2, formDO.getBrandName());
        ps.setBigDecimal(3, formDO.getSize().setScale(2, BigDecimal.ROUND_HALF_UP));
        ps.setString(4, formDO.getSizeUnit());
        ps.setString(5, formDO.getGenericName());
        ps.setString(6, formDO.getItemNum());
        ps.setString(7, formDO.getProfileNum());
        ps.setString(8, formDO.getManufacturer());
        ps.setInt(9, formDO.getVendorKey());
        ps.setByte(10, formDO.getVendorSpecificId());
        ps.setByte(11, formDO.getCustomerTypeKey());
        ps.setByte(12, formDO.getLicenseKeyId());
        ps.setByte(13, formDO.getReportId());
        ps.setString(14, formDO.getItemName());
        ps.setBigDecimal(15, formDO.getDose().setScale(2, BigDecimal.ROUND_HALF_UP));
        ps.setString(16, formDO.getDoseUnit());
        ps.setString(17, formDO.getOther());
        ps.setString(18, formDO.getItemMemo());
        ps.setBigDecimal(19, formDO.getCarryFactor().setScale(2, BigDecimal.ROUND_HALF_UP));
        ps.setBigDecimal(20, formDO.getPercentUse().setScale(2, BigDecimal.ROUND_HALF_UP));
        ps.setBigDecimal(21, formDO.getFirstCost().setScale(2, BigDecimal.ROUND_HALF_UP));
        ps.setBigDecimal(22, formDO.getPrevFirstCost().setScale(2, BigDecimal.ROUND_HALF_UP));
        ps.setBigDecimal(23, formDO.getUnitCost().setScale(2, BigDecimal.ROUND_HALF_UP));
        ps.setBigDecimal(24, formDO.getPrevUnitCost().setScale(2, BigDecimal.ROUND_HALF_UP));
        ps.setBigDecimal(25, formDO.getMuAdditional().setScale(2, BigDecimal.ROUND_HALF_UP));
        ps.setBigDecimal(26, formDO.getMuVendor().setScale(2, BigDecimal.ROUND_HALF_UP));
        ps.setBigDecimal(27, formDO.getLabelCost().setScale(2, BigDecimal.ROUND_HALF_UP));
        ps.setBigDecimal(28, formDO.getOrderThreshold().setScale(2, BigDecimal.ROUND_HALF_UP));
        ps.setString(29, request.getSession().getAttribute("USERNAME").toString());
        ps.setString(30, request.getSession().getAttribute("USERNAME").toString());
        ps.executeUpdate();
      }
    }
    catch (SQLException e) {
      cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
      throw new Exception("insertItemDictionary() " + e.getMessage());
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

      Integer theKey = null;
      theKey = insertItemDictionary(request, form);
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