/*
 * RebateInstanceListAction.java
 *
 * Created on May 29, 2006, 09:07 AM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package com.wolmerica.rebateinstance;

//--------------------------------------------------------------------------------
// @author Richard Wolschlager
//--------------------------------------------------------------------------------
import com.wolmerica.employee.EmployeesActionMapping;
import com.wolmerica.permission.PermissionListDO;
import com.wolmerica.service.property.PropertyService; 
import com.wolmerica.service.property.DefaultPropertyService; 
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

public class RebateInstanceListAction extends Action {

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

  
  private RebateInstanceListHeadDO getRebateInstance(HttpServletRequest request,
                                                     Integer idKey,
                                                     Integer poKey,
                                                     Integer poiKey,
                                                     String offerNameFilter,
                                                     Integer pageNo)
    throws Exception, SQLException {

    RebateInstanceListHeadDO formHDO = new RebateInstanceListHeadDO();
    RebateInstanceDO rebateInstanceRow = null;
    ArrayList<RebateInstanceDO> rebateInstanceRows = new ArrayList<RebateInstanceDO>();
    
    ArrayList<PermissionListDO> permissionRows = new ArrayList<PermissionListDO>();

    DataSource ds = null;
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    try {
      ds = getDataSource(request);
      conn = ds.getConnection();

      String query = "SELECT rebateinstance.thekey,rebate_key,"
                   + "rebate.offer_name,rebate.amount,"
                   + "purchase_order_num,purchaseorderitem_key,"
                   + "tracking_url,rebateinstance.note_line1,"
                   + "eligible_id,submit_id,complete_id,"
                   + "rebateinstance.create_user,rebateinstance.create_stamp,"
                   + "rebateinstance.update_user,rebateinstance.update_stamp "
                   + "FROM rebateinstance,rebate,purchaseorderitem,purchaseorder ";
      if (poiKey != null)
        query = query + "WHERE purchaseorderitem_key=? ";
      else if (poKey != null)
        query = query + "WHERE purchaseorderitem_key IN (SELECT thekey FROM purchaseorderitem WHERE purchaseorder_key=?) ";
      else if (idKey != null)
        query = query + "WHERE rebate.itemdictionary_key=? ";
      else
        query = query + "WHERE offer_name LIKE ? ";
      query = query + "AND rebate_key = rebate.thekey "
                    + "AND purchaseorderitem_key = purchaseorderitem.thekey "
                    + "AND purchaseorder_key = purchaseorder.thekey "
                    + "ORDER BY rebateinstance.thekey DESC";
      cat.debug(this.getClass().getName() + ": Query #1 " + query);
      ps = conn.prepareStatement(query);
      if (poiKey != null)
        ps.setInt(1, poiKey);
      else if (poKey != null)
        ps.setInt(1, poKey);
      else if (idKey != null)
        ps.setInt(1, idKey);
      else
        ps.setString(1, "%" + offerNameFilter.toUpperCase().trim() + "%");
      rs = ps.executeQuery();

      Integer pageMax = new Integer(getPropertyService().getCustomerProperties(request,"rebateinstance.list.size"));
      Integer firstRecord = ((pageNo.intValue() - 1) * pageMax) + 1;
      Integer lastRecord = firstRecord + (pageMax - 1);

      Integer recordCount = 0;
      while ( rs.next() ) {
        ++recordCount;
        if ((recordCount >= firstRecord) && (recordCount <= lastRecord)) {
          rebateInstanceRow = new RebateInstanceDO();

          rebateInstanceRow.setKey(rs.getInt("thekey"));
          rebateInstanceRow.setRebateKey(rs.getInt("rebate_key"));
          rebateInstanceRow.setOfferName(rs.getString("offer_name"));
          rebateInstanceRow.setAmount(rs.getBigDecimal("amount"));
          rebateInstanceRow.setPurchaseOrderNumber(rs.getString("purchase_order_num"));
          rebateInstanceRow.setPurchaseOrderItemKey(rs.getInt("purchaseorderitem_key"));
          rebateInstanceRow.setTrackingURL(rs.getString("tracking_url"));
          rebateInstanceRow.setNoteLine1(rs.getString("note_line1"));
          rebateInstanceRow.setEligibleId(rs.getBoolean("eligible_id"));
          rebateInstanceRow.setSubmitId(rs.getBoolean("submit_id"));
          rebateInstanceRow.setCompleteId(rs.getBoolean("complete_id"));
          rebateInstanceRow.setCreateUser(rs.getString("create_user"));
          rebateInstanceRow.setCreateStamp(rs.getTimestamp("create_stamp"));
          rebateInstanceRow.setUpdateUser(rs.getString("update_user"));
          rebateInstanceRow.setUpdateStamp(rs.getTimestamp("update_stamp"));

          formHDO.setPageTotal(formHDO.getPageTotal().add(rebateInstanceRow.getAmount()));
//--------------------------------------------------------------------------------
// 07-31-06  Define the permission information in separate class for re-usability.
//--------------------------------------------------------------------------------
          permissionRows.add(getUserStateService().getUserListToken(request,conn,
                                  this.getClass().getName(),rebateInstanceRow.getKey()));
          rebateInstanceRows.add(rebateInstanceRow);
        }
      }

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
      formHDO.setOfferNameFilter(offerNameFilter);
      formHDO.setRecordCount(recordCount);
      formHDO.setFirstRecord(firstRecord);
      formHDO.setLastRecord(lastRecord);
      formHDO.setPreviousPage(prevPage);
      formHDO.setNextPage(nextPage);
//--------------------------------------------------------------------------------
// A formatter issues exists during the populatin of empty lists.
// A work around is to populate one row when there is an emptyList.
// The cooresponding jsp also needs to check for the phantom row.
//--------------------------------------------------------------------------------
      if (rebateInstanceRows.isEmpty()) {
        rebateInstanceRows.add(new RebateInstanceDO());
        permissionRows.add(getUserStateService().getUserListToken(request,conn,
                           this.getClass().getName(),getUserStateService().getNoKey()));
      }
      formHDO.setRebateInstanceForm(rebateInstanceRows);
      formHDO.setPermissionListForm(permissionRows);
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
// Check for a item, purchase order, and purchase order item.
//--------------------------------------------------------------------------------
      Integer idKey = null;
      if (!(request.getParameter("idKey") == null))
        idKey = new Integer(request.getParameter("idKey"));
      Integer poKey = null;
      if (!(request.getParameter("poKey") == null))
        poKey = new Integer(request.getParameter("poKey"));
      Integer poiKey = null;
      if (!(request.getParameter("poiKey") == null))
        poiKey = new Integer(request.getParameter("poiKey"));

      String offerNameFilter = "";
      if (!(request.getParameter("offerNameFilter") == null)) {
        offerNameFilter = request.getParameter("offerNameFilter");
      }

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
                                              getUserStateService().getNoKey());
      if (usToken.equalsIgnoreCase(getUserStateService().getLocked()))
        request.setAttribute(getUserStateService().getDisableEdit(), false);
      else if (usToken.equalsIgnoreCase(getUserStateService().getProhibited()))
        throw new Exception(getUserStateService().getAccessDenied());

      try {
//--------------------------------------------------------------------------------
// Instantiate a formDO object to populate formStr.
//--------------------------------------------------------------------------------
        RebateInstanceListHeadDO formHDO = getRebateInstance(request,
                                                             idKey,
                                                             poKey,
                                                             poiKey,
                                                             offerNameFilter,
                                                             pageNo);
        request.getSession().setAttribute("rebateinstancelistHDO", formHDO);

        // Create the wrapper object for Rebate list.
        RebateInstanceListHeadForm formHStr = new RebateInstanceListHeadForm();
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
