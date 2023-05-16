/*
 * VendorListAction.java
 *
 * Created on August 23, 2005, 10:31 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package com.wolmerica.vendor;

//--------------------------------------------------------------------------------
// @author Richard Wolschlager
//--------------------------------------------------------------------------------
import com.wolmerica.employee.EmployeesActionMapping;
import com.wolmerica.permission.PermissionListDO;
import com.wolmerica.service.accounting.AccountingService;
import com.wolmerica.service.accounting.DefaultAccountingService;
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
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.math.BigDecimal;
import org.json.JSONArray;
import org.json.JSONObject;

import org.apache.log4j.Logger;

public class VendorListAction extends Action {

  Logger cat = Logger.getLogger("WOWAPP");

  private AccountingService accountingService = new DefaultAccountingService();
  private PropertyService propertyService = new DefaultPropertyService();
  private UserStateService userStateService = new DefaultUserStateService();

  public AccountingService getAccountingService() {
      return this.accountingService;
  }

  public void setAccountingService(AccountingService accountingService) {
      this.accountingService = accountingService;
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


  private VendorListHeadDO getVendorList(HttpServletRequest request,
                                         String nameFilter,
                                         Byte trackResultId,
                                         Integer pageNo,
                                         Byte sourceTypeKey,
                                         Boolean jsonId)
    throws Exception, SQLException {

    VendorListHeadDO formHDO = new VendorListHeadDO();
    VendorListDO vendorRow = null;
    ArrayList<VendorListDO> vendorRows = new ArrayList<VendorListDO>();
    
    ArrayList<PermissionListDO> permissionRows = new ArrayList<PermissionListDO>();
    HashMap acctMap;

    DataSource ds = null;
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;
    ResultSet rs2 = null;
    
    try {
      ds = getDataSource(request);
      conn = ds.getConnection();

      String query = "SELECT thekey,"
                   + "name,"
                   + "contact_name,"
                   + "address,"
                   + "address2,"
                   + "city,"
                   + "state,"
                   + "zip,"
                   + "phone_num,"
                   + "phone_ext,"                   
                   + "fax_num,"
                   + "email,"
                   + "email2,"
                   + "website,"
                   + "acct_num,"
                   + "terms "
                   + "FROM vendor "
                   + "WHERE UPPER(name) LIKE ? ";
      if (trackResultId != null)
        query += " AND track_result_id = ? ";
      query += " ORDER BY clinic_id DESC, name";

      cat.debug(this.getClass().getName() + ": Query #1 " + query);
      ps = conn.prepareStatement(query);
      ps.setString(1, "%" + nameFilter.toUpperCase().trim() + "%");
      if (trackResultId != null)
        ps.setByte(2, trackResultId);
      rs = ps.executeQuery();

//--------------------------------------------------------------------------------
// Prepare an SQL query to retrieve the last order date from purchase order.
//--------------------------------------------------------------------------------
      query = "SELECT DATE(MAX(submit_order_stamp)) AS order_date "
            + "FROM purchaseorder "
            + "WHERE vendor_key = ?";
      ps = conn.prepareStatement(query);            
      
//--------------------------------------------------------------------------------
// Get the accounting values associated with this VENDOR.
// Define a permissionRow to evaluate the deleteId before checking dependency.
//--------------------------------------------------------------------------------
      PermissionListDO permissionRow = null;
      
      Integer pageMax;
      if (!jsonId) {
        if (request.getRequestURI().contains("Menu"))
          pageMax = new Integer(getPropertyService().getCustomerProperties(request,"menu.list.size"));
        else
          pageMax = new Integer(getPropertyService().getCustomerProperties(request,"vendor.list.size"));
      } else {
        pageMax = new Integer(getPropertyService().getCustomerProperties(request,"json.list.size"));
      }
      
      Integer firstRecord = ((pageNo.intValue() - 1) * pageMax) + 1;
      Integer lastRecord = firstRecord + (pageMax - 1);

      Integer recordCount = 0;
      while ( rs.next() ) {
        if (!(request.getRequestURI().contains("Menu")))
          ++recordCount;
        if (((recordCount >= firstRecord) && (recordCount <= lastRecord)) || (request.getRequestURI().contains("Menu"))) {
          vendorRow = new VendorListDO();

          vendorRow.setKey(rs.getInt("thekey"));
          vendorRow.setName(rs.getString("name"));
          vendorRow.setContactName(rs.getString("contact_name"));
          vendorRow.setAddress(rs.getString("address"));
          vendorRow.setAddress2(rs.getString("address2"));
          vendorRow.setCity(rs.getString("city"));
          vendorRow.setState(rs.getString("state"));
          vendorRow.setZip(rs.getString("zip"));
          vendorRow.setPhoneNum(rs.getString("phone_num"));
          vendorRow.setPhoneExt(rs.getString("phone_ext"));
          vendorRow.setFaxNum(rs.getString("fax_num"));
          vendorRow.setEmail(rs.getString("email"));
          vendorRow.setEmail2(rs.getString("email2"));
          vendorRow.setWebSite(rs.getString("website"));
          vendorRow.setAcctNum(rs.getString("acct_num"));
          vendorRow.setTerms(rs.getString("terms"));
          
//--------------------------------------------------------------------------------
// JSON does not require the last order, account, or dependency information.
//--------------------------------------------------------------------------------
          if (!jsonId) {
          
//--------------------------------------------------------------------------------
// Attempt to retrieve the last order date from the purchase order.
//--------------------------------------------------------------------------------
            ps.setInt(1, vendorRow.getKey());
            rs2 = ps.executeQuery();
            if (rs2.next()) {
              vendorRow.setLastOrderDate(rs2.getDate("order_date"));
            }          

//--------------------------------------------------------------------------------
// Retrieve the balance due amount from the vendor accounting records.
//--------------------------------------------------------------------------------
            acctMap = getAccountingService().getVendorBalance(conn, vendorRow.getKey());
            vendorRow.setAccountBalanceAmount((BigDecimal)acctMap.get("balanceAmt"));

//--------------------------------------------------------------------------------
// 07-31-06  Define the permission information in separate class for re-usability.
//--------------------------------------------------------------------------------
            permissionRow = getUserStateService().getUserListToken(request,conn,
                               this.getClass().getName(),vendorRow.getKey());
            permissionRows.add(permissionRow);

            if (permissionRow.getDeleteId()) {
//--------------------------------------------------------------------------------
// 10-30-06  Check if the vendor has any dependency on linked to tables.
//--------------------------------------------------------------------------------
              vendorRow.setAllowDeleteId(getVendorDependency(conn,
                                                             sourceTypeKey,
                                                             vendorRow.getKey()));
            }
          }  
//--------------------------------------------------------------------------------
// 02-22-07  Determine whether the content will go to menu or a regular vendor list.
//--------------------------------------------------------------------------------
          if (request.getRequestURI().contains("Menu") && (vendorRow.getAccountBalanceAmount().compareTo(new BigDecimal("0")) != 0)) {
            ++recordCount;              
            if ((recordCount >= firstRecord) && (recordCount <= lastRecord))
              vendorRows.add(vendorRow);
          }
          if (!(request.getRequestURI().contains("Menu")))
            vendorRows.add(vendorRow);
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
      formHDO.setNameFilter(nameFilter);
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
      if (vendorRows.isEmpty()) {
        vendorRows.add(new VendorListDO());
        permissionRows.add(getUserStateService().getUserListToken(request,conn,
                           this.getClass().getName(),getUserStateService().getNoKey()));
      }
      formHDO.setVendorListForm(vendorRows);
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

  public Boolean getVendorDependency(Connection conn,
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
      cStmt = conn.prepareCall("{call GetVendorDependency(?,?,?,?)}");
      cStmt.setByte(1, stKey);
      cStmt.setInt(2, sKey);
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
      throw new Exception("getVendorDependency() " + e.getMessage());
    }
    finally
    {
      try
      {
        cStmt.close();
      }
      catch (SQLException e) {
        cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
        throw new Exception("getVendorDependency() " + e.getMessage());
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
      String nameFilter = "";
      if (!(request.getParameter("nameFilter") == null)) {
        nameFilter = request.getParameter("nameFilter");
      }

      Byte trackResultId = null;
      if (request.getParameter("trackResultId") != null) {
        if (!(request.getParameter("trackResultId").equalsIgnoreCase(""))) {
          trackResultId = new Byte(request.getParameter("trackResultId"));
        }
      }

      Integer pageNo = new Integer("1");
      if (!(request.getParameter("pageNo") == null)) {
        pageNo = new Integer(request.getParameter("pageNo").toString());
        if (pageNo < 0)
          pageNo = new Integer("1");
      }
      
      Boolean jsonId = false;
      if (request.getParameter("json") != null) {
        jsonId = new Boolean(request.getParameter("json").toString());
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

//--------------------------------------------------------------------------------
// The export action will leverage the already populated vendor list.
//--------------------------------------------------------------------------------          
      if (!(request.getRequestURI().contains("Export"))) {       
      
        try {
//--------------------------------------------------------------------------------
// Instantiate a formHDO object to populate formHStr.
//--------------------------------------------------------------------------------
          VendorListHeadDO formHDO = getVendorList(request,
                                                   nameFilter,
                                                   trackResultId,
                                                   pageNo,
                                                   getUserStateService().getFeatureKey().byteValue(),
                                                   jsonId);
          if (jsonId) {
            JSONArray jsonItems = new JSONArray();
            JSONObject obj = null;

            if (formHDO.getRecordCount() > 0) {
              ArrayList myArray = formHDO.getVendorListForm();            
              VendorListDO myDO = new VendorListDO();
              for (int i = 0; i < myArray.size(); i++) {
                myDO = (VendorListDO) myArray.get(i);

                obj = new JSONObject();
                obj.put("id", myDO.getKey().toString());
                obj.put("value", myDO.getName());
                obj.put("info", myDO.getCity() + " " + myDO.getState());
                jsonItems.put(obj);
              }
            }
            JSONObject json = new JSONObject();
            json.put("results", jsonItems);

            response.setContentType("application/json");
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expires", 0);
            response.setHeader("Pragma", "no-cache");
            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentLength(json.toString().length());
            response.getWriter().write(json.toString());
            response.getWriter().flush();
          }
          else {
            request.getSession().setAttribute("vendorListHDO", formHDO);
//--------------------------------------------------------------------------------          
// Create the wrapper object for Vendor list.
//--------------------------------------------------------------------------------
            VendorListHeadForm formHStr = new VendorListHeadForm();
            formHStr.populate(formHDO);
            form = formHStr;
          }            
        }
        catch (FormattingException fe) {
          fe.getMessage();
        }

        if (!jsonId) {        
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
