/*
 * RebateListAction.java
 *
 * Created on May 27, 2006, 11:51 AM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package com.wolmerica.rebate;

//--------------------------------------------------------------------------------
// @author Richard Wolschlager
//--------------------------------------------------------------------------------
import com.wolmerica.employee.EmployeesActionMapping;
import com.wolmerica.service.property.PropertyService;
import com.wolmerica.service.property.DefaultPropertyService;
import com.wolmerica.service.rebate.RebateService;
import com.wolmerica.service.rebate.DefaultRebateService;
import com.wolmerica.permission.PermissionListDO;
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
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.apache.log4j.Logger;

public class RebateListAction extends Action {

  Logger cat = Logger.getLogger("WOWAPP");

  private PropertyService propertyService = new DefaultPropertyService();
  private RebateService rebateService = new DefaultRebateService();
  private UserStateService userStateService = new DefaultUserStateService();

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
  
  private RebateListHeadDO getRebateList(HttpServletRequest request,
                                         Integer idKey,
                                         Integer poKey,
                                         Integer poiKey,
                                         String offerNameFilter,
                                         Integer pageNo,
                                         Byte sourceTypeKey)
    throws Exception, SQLException {

    RebateListHeadDO formHDO = new RebateListHeadDO();
    RebateListDO rebateRow = null;
    ArrayList<RebateListDO> rebateRows = new ArrayList<RebateListDO>();
    
    ArrayList<PermissionListDO> permissionRows = new ArrayList<PermissionListDO>();

    DataSource ds = null;
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    try {
      ds = getDataSource(request);
      conn = ds.getConnection();

      String query = "SELECT rebate.thekey,"
                   + "itemdictionary_key,"
                   + "itemdictionary.brand_name,"
                   + "itemdictionary.size,"
                   + "itemdictionary.size_unit,"
                   + "rebate.offer_name,"
                   + "rebate.start_date,"
                   + "rebate.end_date,"
                   + "rebate.submit_date,"
                   + "rebate.amount "
                   + "FROM rebate, itemdictionary ";
      if (poiKey != null)
        query = query + " WHERE itemdictionary_key IN "
              + "(SELECT itemdictionary_key FROM purchaseorderitem WHERE thekey=?) ";
      else if (poKey != null)
        query = query + " WHERE itemdictionary_key IN "
              + "(SELECT itemdictionary_key FROM purchaseorderitem WHERE purchaseorder_key=?) ";
      else if (idKey != null)
        query = query + " WHERE itemdictionary_key = ? ";
      else
        query = query + " WHERE UPPER(offer_name) LIKE ? ";
      query = query + " AND itemdictionary_key = itemdictionary.thekey "
//                + "AND DATE(CURRENT_TIMESTAMP) BETWEEN start_date AND submit_date "
                    + "ORDER BY end_date DESC, offer_name";
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

//--------------------------------------------------------------------------------
// Define a permissionRow to evaluate the deleteId before checking dependency.
//--------------------------------------------------------------------------------
      PermissionListDO permissionRow = null;

      Integer pageMax = new Integer(getPropertyService().getCustomerProperties(request,"rebate.list.size"));
      Integer firstRecord = ((pageNo.intValue() - 1) * pageMax) + 1;
      Integer lastRecord = firstRecord + (pageMax - 1);

      Integer recordCount = 0;
      while ( rs.next() ) {
        ++recordCount;
        if ((recordCount >= firstRecord) && (recordCount <= lastRecord)) {
          rebateRow = new RebateListDO();

          rebateRow.setKey(rs.getInt("thekey"));
          rebateRow.setItemDictionaryKey(rs.getInt("itemdictionary_key"));
          rebateRow.setBrandName(rs.getString("brand_name"));
          rebateRow.setSize(rs.getBigDecimal("size"));
          rebateRow.setSizeUnit(rs.getString("size_unit"));
          rebateRow.setOfferName(rs.getString("offer_name"));
          rebateRow.setStartDate(rs.getDate("start_date"));
          rebateRow.setEndDate(rs.getDate("end_date"));
          rebateRow.setSubmitDate(rs.getDate("submit_date"));
          rebateRow.setAmount(rs.getBigDecimal("amount"));
          
//--------------------------------------------------------------------------------
// 07-07-2007 Find out how many times this rebate has been applied for.
//--------------------------------------------------------------------------------
          rebateRow.setRebateInstanceCount(getRebateService().getRebateInstanceCountByRebate(conn,
                                                                    rebateRow.getKey()));

//--------------------------------------------------------------------------------
// 07-31-06  Define the permission information in separate class for re-usability.
//--------------------------------------------------------------------------------
          permissionRows.add(getUserStateService().getUserListToken(request,conn,
                                  this.getClass().getName(),rebateRow.getKey()));

//--------------------------------------------------------------------------------
// 07-31-06  Define the permission information in separate class for re-usability.
//--------------------------------------------------------------------------------
          permissionRow = getUserStateService().getUserListToken(request,conn,
                             this.getClass().getName(),rebateRow.getKey());
          permissionRows.add(permissionRow);

          if (permissionRow.getDeleteId()) {
//--------------------------------------------------------------------------------
// 10-30-06  Check if the rebate has any dependency on linked to tables.
//--------------------------------------------------------------------------------
            rebateRow.setAllowDeleteId(getRebateDependency(conn,
                                                           sourceTypeKey,
                                                           rebateRow.getKey()));
          }

          rebateRows.add(rebateRow);
        }
      }

      //===========================================================================
      // Pagination logic to figure out what the previous and next page
      // values will be for the next screen to be displayed.
      //===========================================================================
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
      //===========================================================================
      // Store the filter, row count, previous and next page number values.
      //===========================================================================
      formHDO.setOfferNameFilter(offerNameFilter);
      formHDO.setRecordCount(recordCount);
      formHDO.setFirstRecord(firstRecord);
      formHDO.setLastRecord(lastRecord);
      formHDO.setPreviousPage(prevPage);
      formHDO.setNextPage(nextPage);
      //===========================================================================
      // A formatter issues exists during the populatin of empty lists.
      // A work around is to populate one row when there is an emptyList.
      // The cooresponding jsp also needs to check for the phantom row.
      //===========================================================================
      if (rebateRows.isEmpty()) {
        rebateRows.add(new RebateListDO());
        permissionRows.add(getUserStateService().getUserListToken(request,conn,
                           this.getClass().getName(),getUserStateService().getNoKey()));
      }
      formHDO.setRebateListForm(rebateRows);
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

  public Boolean getRebateDependency(Connection conn,
                                       Byte stKey,
                                       Integer sKey)
   throws Exception, SQLException {

    CallableStatement cStmt = null;
    Boolean allowDeleteId = false;

    try
    {
//--------------------------------------------------------------------------------
// Call a procedure with two IN parameters and one OUT parameter.
//--------------------------------------------------------------------------------
      cStmt = conn.prepareCall("{call GetRebateDependency(?,?,?,?)}");
//--------------------------------------------------------------------------------
// Set the value for the IN parameter
//--------------------------------------------------------------------------------
      cStmt.setByte(1, stKey);
      cStmt.setInt(2, sKey);
//--------------------------------------------------------------------------------
// Execute the stored procedure
//--------------------------------------------------------------------------------
      cStmt.execute();
//--------------------------------------------------------------------------------
// debug messages if necessary.
//--------------------------------------------------------------------------------
      cat.debug(this.getClass().getName() + ": dependencyCnt : " + cStmt.getInt("dependencyCnt"));
      cat.debug(this.getClass().getName() + ": tableName : " + cStmt.getString("tableName"));
//--------------------------------------------------------------------------------
// Retrieve the dependency count return value
//--------------------------------------------------------------------------------
      if (cStmt.getInt("dependencyCnt") == 0)
        allowDeleteId = true;
    }
    catch (SQLException e) {
      cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
      throw new Exception("getRebateDependency() " + e.getMessage());
    }
    finally
    {
      try
      {
        cStmt.close();
      }
      catch (SQLException e) {
        cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
        throw new Exception("getRebateDependency() " + e.getMessage());
      }
    }
    return allowDeleteId;
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
      if (request.getParameter("idKey") != null)
        idKey = new Integer(request.getParameter("idKey"));
      Integer poKey = null;
      if (request.getParameter("poKey") != null)
        poKey = new Integer(request.getParameter("poKey"));
      Integer poiKey = null;
      if (request.getParameter("poiKey") != null)
        poiKey = new Integer(request.getParameter("poiKey"));

      String offerNameFilter = "";
      if (request.getParameter("offerNameFilter") != null) {
        offerNameFilter = request.getParameter("offerNameFilter");
      }

      Integer pageNo = new Integer("1");
      if (request.getParameter("pageNo") != null) {
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
        RebateListHeadDO formHDO = getRebateList(request,
                                                 idKey,
                                                 poKey,
                                                 poiKey,
                                                 offerNameFilter,
                                                 pageNo,
                                                 getUserStateService().getFeatureKey().byteValue());
        request.getSession().setAttribute("rebatelistHDO", formHDO);
//--------------------------------------------------------------------------------
        // Create the wrapper object for Rebate list.
//--------------------------------------------------------------------------------
        RebateListHeadForm formHStr = new RebateListHeadForm();
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
