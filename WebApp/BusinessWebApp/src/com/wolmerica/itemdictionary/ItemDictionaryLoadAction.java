/*
 * ItemDictionaryLoadAction.java
 *
 * Created on June 19, 2007, 7:58 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package com.wolmerica.itemdictionary;

//--------------------------------------------------------------------------------
// @author Richard Wolschlager
//--------------------------------------------------------------------------------
import com.wolmerica.employee.EmployeesActionMapping;
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

public class ItemDictionaryLoadAction extends Action {

  Logger cat = Logger.getLogger("WOWAPP");

  private UserStateService userStateService = new DefaultUserStateService();

  public UserStateService getUserStateService() {
      return userStateService;
  }

  public void setUserStateService(UserStateService userStateService) {
      this.userStateService = userStateService;
  }
  

  private void loadCustomerAttributeByItem(HttpServletRequest request)

    throws Exception, SQLException {

    DataSource ds = null;
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;
    ItemDictionaryDO formDO = null;

    try {
      ds = getDataSource(request);
      conn = ds.getConnection();

      String query = "SELECT thekey,"
                   + "brand_name,"
                   + "size,"
                   + "size_unit,"
                   + "generic_name,"
                   + "item_num,"
                   + "manufacturer,"
                   + "vendor_key,"
                   + "customertype_key,"
                   + "report_id,"
                   + "item_name,"
                   + "dose,"
                   + "dose_unit,"
                   + "other,"
                   + "first_cost,"
                   + "lastftcost,"
                   + "unit_cost,"
                   + "lastuncost,"
                   + "muadd,"
                   + "muvendor,"
                   + "label_cost,"
                   + "item_memo,"
                   + "carry_factor,"
                   + "percent_use "
                   + "FROM itemimport ";
      ps = conn.prepareStatement(query);
      rs = ps.executeQuery();

//--------------------------------------------------------------------------------
// Prepare a query to insert a new row into the item dictionary table.
//--------------------------------------------------------------------------------
      query = "INSERT INTO itemdictionary(thekey,"
            + "brand_name,"
            + "size,"
            + "size_unit,"
            + "generic_name,"
            + "item_num,"
            + "manufacturer,"
            + "vendor_key,"
            + "customertype_key,"
            + "report_id,"
            + "item_name,"
            + "dose,"
            + "dose_unit,"
            + "other,"
            + "first_cost,"
            + "lastftcost,"
            + "unit_cost,"
            + "lastuncost,"
            + "muadd,"
            + "muvendor,"
            + "label_cost,"
            + "item_memo,"
            + "carry_factor,"
            + "percent_use,"
            + "order_threshold,"
            + "create_user,"
            + "create_stamp,"
            + "update_user,"
            + "update_stamp) "
            + "VALUES (?,?,?,?,?,?,?,?,?,?,"
            + "?,?,?,?,?,?,?,?,?,?,"
            + "?,?,?,?,?,?,CURRENT_TIMESTAMP,?,CURRENT_TIMESTAMP)";
      ps = conn.prepareStatement(query);

      while ( rs.next() ) {

        formDO = new ItemDictionaryDO();

        formDO.setKey(rs.getInt("thekey"));
        formDO.setBrandName(rs.getString("brand_name"));
        formDO.setSize(rs.getBigDecimal("size"));
        formDO.setSizeUnit(rs.getString("size_unit"));
        formDO.setGenericName(rs.getString("generic_name"));
        formDO.setItemNum(rs.getString("item_num"));
        formDO.setManufacturer(rs.getString("manufacturer"));
        formDO.setVendorKey(rs.getInt("vendor_key"));
        formDO.setCustomerTypeKey(rs.getByte("customertype_key"));
        formDO.setReportId(rs.getByte("report_id"));
        formDO.setItemName(rs.getString("item_name"));
        formDO.setDose(rs.getBigDecimal("dose"));
        formDO.setDoseUnit(rs.getString("dose_unit"));
        formDO.setOther(rs.getString("other"));
        formDO.setFirstCost(rs.getBigDecimal("first_cost"));
        formDO.setPrevFirstCost(rs.getBigDecimal("lastftcost"));
        formDO.setUnitCost(rs.getBigDecimal("unit_cost"));
        formDO.setPrevUnitCost(rs.getBigDecimal("lastuncost"));
        formDO.setMuAdditional(rs.getBigDecimal("muadd"));
        formDO.setMuVendor(rs.getBigDecimal("muvendor"));
        formDO.setLabelCost(rs.getBigDecimal("label_cost"));
        formDO.setItemMemo(rs.getString("item_memo"));
        formDO.setCarryFactor(rs.getBigDecimal("carry_factor"));
        formDO.setPercentUse(rs.getBigDecimal("percent_use"));
        formDO.setOrderThreshold(new BigDecimal("0"));

//--------------------------------------------------------------------------------
// Insert a new row into the item dictionary table.
//--------------------------------------------------------------------------------
        cat.warn(this.getClass().getName() + ": Key....... : " + formDO.getKey());
        cat.warn(this.getClass().getName() + ": Brand name.: " + formDO.getBrandName());
        cat.warn(this.getClass().getName() + ": First cost.: " + formDO.getFirstCost());

        ps.setInt(1, formDO.getKey());
        ps.setString(2, formDO.getBrandName());
        ps.setBigDecimal(3, formDO.getSize());
        ps.setString(4, formDO.getSizeUnit());
        ps.setString(5, formDO.getGenericName());
        ps.setString(6, formDO.getItemNum());
        ps.setString(7, formDO.getManufacturer());
        ps.setInt(8, formDO.getVendorKey());
        ps.setByte(9, formDO.getCustomerTypeKey());
        ps.setByte(10, formDO.getReportId());
        ps.setString(11, formDO.getItemName());
        ps.setBigDecimal(12, formDO.getDose());
        ps.setString(13, formDO.getDoseUnit());
        ps.setString(14, formDO.getOther());
        ps.setBigDecimal(15, formDO.getFirstCost());
        ps.setBigDecimal(16, formDO.getPrevFirstCost());
        ps.setBigDecimal(17, formDO.getUnitCost());
        ps.setBigDecimal(18, formDO.getPrevUnitCost());
        ps.setBigDecimal(19, formDO.getMuAdditional());
        ps.setBigDecimal(20, formDO.getMuVendor());
        ps.setBigDecimal(21, formDO.getLabelCost());
        ps.setString(22, formDO.getItemMemo());
        ps.setBigDecimal(23, formDO.getCarryFactor());
        ps.setBigDecimal(24, formDO.getPercentUse());
        ps.setBigDecimal(25, formDO.getOrderThreshold());
        ps.setString(26, request.getSession().getAttribute("USERNAME").toString());
        ps.setString(27, request.getSession().getAttribute("USERNAME").toString());

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
  }

    @Override
  public ActionForward execute(ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws IOException, ServletException {

    cat.warn(this.getClass().getName() + ": EXECUTE ");
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

      cat.warn(this.getClass().getName() + ": START id Load ");
      loadCustomerAttributeByItem(request);
      cat.warn(this.getClass().getName() + ": FINISH id Load ");
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