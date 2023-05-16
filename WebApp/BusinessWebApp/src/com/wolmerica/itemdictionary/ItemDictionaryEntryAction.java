/*
 * ItemDictionaryEntryAction.java
 *
 * Created on September 9, 2005, 6:44 PM
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
import com.wolmerica.customertype.CustomerTypeDropList;
import com.wolmerica.service.userstate.UserStateService;
import com.wolmerica.service.userstate.DefaultUserStateService;
import com.wolmerica.tools.formatter.FormattingException;

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

public class ItemDictionaryEntryAction extends Action {

  Logger cat = Logger.getLogger("WOWAPP");

  private UserStateService userStateService = new DefaultUserStateService();

  public UserStateService getUserStateService() {
      return userStateService;
  }

  public void setUserStateService(UserStateService userStateService) {
      this.userStateService = userStateService;
  }

  
  private ItemDictionaryDO buildItemDictionaryForm(HttpServletRequest request,
                                                   Integer idKey)
    throws Exception, SQLException {

    ItemDictionaryDO formDO = new ItemDictionaryDO();
    ArrayList customerTypeRows = new ArrayList();

    DataSource ds = null;
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    try {
//--------------------------------------------------------------------------------
// Initialize the profileNum value to "New"
//--------------------------------------------------------------------------------
      formDO.setProfileNum(new String("New"));

      ds = getDataSource(request);
      conn = ds.getConnection();

      if (idKey != null) {
        String query = "SELECT brand_name,"
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

        if (rs.next()) {
          formDO.setGenericName(rs.getString("generic_name"));
          formDO.setManufacturer(rs.getString("manufacturer"));
          formDO.setVendorKey(rs.getInt("vendor_key"));
          formDO.setCustomerTypeKey(rs.getByte("customertype_key"));
          formDO.setReportId(rs.getByte("report_id"));
          formDO.setSize(rs.getBigDecimal("size"));
          formDO.setSizeUnit(rs.getString("size_unit"));
          formDO.setDose(rs.getBigDecimal("dose"));
          formDO.setDoseUnit(rs.getString("dose_unit"));
          formDO.setOther(rs.getString("other"));
          formDO.setItemMemo(rs.getString("item_memo"));
          formDO.setCarryFactor(rs.getBigDecimal("carry_factor"));
          formDO.setPercentUse(rs.getBigDecimal("percent_use"));
          formDO.setFirstCost(rs.getBigDecimal("first_cost"));
          formDO.setUnitCost(rs.getBigDecimal("unit_cost"));
          formDO.setMuAdditional(rs.getBigDecimal("muadd"));
          formDO.setMuVendor(rs.getBigDecimal("muvendor"));
          formDO.setLabelCost(rs.getBigDecimal("label_cost"));
          formDO.setOrderThreshold(rs.getBigDecimal("order_threshold"));
          if (request.getParameter("brandNameFilter") != null)
            formDO.setBrandNameFilter(request.getParameter("brandNameFilter"));
          if (request.getParameter("itemNumFilter") != null)
            formDO.setItemNumFilter(request.getParameter("itemNumFilter"));
          if (request.getParameter("genericNameFilter") != null)
            formDO.setGenericNameFilter(request.getParameter("genericNameFilter"));
          if (request.getParameter("idPageNo") != null)
            formDO.setCurrentPage(new Integer(request.getParameter("idPageNo")));
        }
        else {
          throw new Exception("ItemDictionary  " + idKey.toString() + " not found!");
        }
      }
//--------------------------------------------------------------------------------
// Retrieve the active customer type values available to assign to a item.
//--------------------------------------------------------------------------------
      CustomerTypeDropList ctdl = new CustomerTypeDropList();
      customerTypeRows = ctdl.getActiveCustomerTypeList(conn);
      formDO.setCustomerTypeForm(customerTypeRows);
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
// Instantiate a formDO object to populate formStr.
//--------------------------------------------------------------------------------
      try {
        ItemDictionaryDO formDO = null;
        Integer theKey = null;
        if (request.getParameter("key") != null)
          theKey = new Integer(request.getParameter("key"));

        formDO = buildItemDictionaryForm(request, theKey);
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