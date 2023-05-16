/*
 * CustomerListAction.java
 *
 * Created on August 21, 2005, 10:51 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package com.wolmerica.customer;

//--------------------------------------------------------------------------------
// @author Richard Wolschlager
//--------------------------------------------------------------------------------
import com.wolmerica.employee.EmployeesActionMapping;
import com.wolmerica.service.accounting.AccountingService;
import com.wolmerica.service.accounting.DefaultAccountingService;
import com.wolmerica.service.customer.CustomerService;
import com.wolmerica.service.customer.DefaultCustomerService;
import com.wolmerica.service.property.PropertyService;
import com.wolmerica.service.property.DefaultPropertyService;
import com.wolmerica.service.userstate.UserStateService;
import com.wolmerica.service.userstate.DefaultUserStateService;
import com.wolmerica.permission.PermissionListDO;
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
import java.util.Date;
import java.util.HashMap;
import java.math.BigDecimal;
import org.json.JSONArray;
import org.json.JSONObject;

import org.apache.log4j.Logger;

public class CustomerListAction extends Action {

  Logger cat = Logger.getLogger("WOWAPP");

  private AccountingService accountingService = new DefaultAccountingService();
  private CustomerService customerService = new DefaultCustomerService();
  private PropertyService propertyService = new DefaultPropertyService();
  private UserStateService userStateService = new DefaultUserStateService();

  public AccountingService getAccountingService() {
      return accountingService;
  }

  public void setAccountingService(AccountingService accountingService) {
      this.accountingService = accountingService;
  }

  public CustomerService getCustomerService() {
      return customerService;
  }

  public void setCustomerService(CustomerService customerService) {
      this.customerService = customerService;
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


  private CustomerListHeadDO getCustomerList(HttpServletRequest request,
                                             Boolean clinicId,
                                             Boolean activeId,
                                             String lastNameFilter,
                                             Integer pageNo,
                                             Byte sourceTypeKey,
                                             Boolean jsonId)
    throws Exception, SQLException {

    CustomerListHeadDO formHDO = new CustomerListHeadDO();
    CustomerListDO customerRow = null;
    ArrayList<CustomerListDO> customerRows = new ArrayList<CustomerListDO>();
    
    ArrayList<PermissionListDO> permissionRows = new ArrayList<PermissionListDO>();
    HashMap acctMap;

    DataSource ds = null;
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    try {
      ds = getDataSource(request);
      conn = ds.getConnection();

      String query = "SELECT customer.thekey,"
                   + "code_num,"
                   + "first_name,"
                   + "last_name,"
                   + "client_name,"
                   + "ship_to,"
                   + "address,"
                   + "address2,"
                   + "city,"
                   + "state,"
                   + "zip,"
                   + "phone_num,"
                   + "phone_ext,"
                   + "mobile_num,"
                   + "fax_num,"
                   + "acct_num,"
                   + "acct_name,"
                   + "ledger_id,"
                   + "report_id,"
                   + "primary_key,"
                   + "clinic_id,"
                   + "customertype.name,"
                   + "email,"
                   + "email2,"
                   + "website,"
                   + "line_of_business,"
                   + "contact_name,"
                   + "referred_by,"
                   + "note_line1,"
                   + "note_line2 "
                   + "FROM customer, customertype "
                   + "WHERE UPPER(client_name) LIKE ? "
                   + "AND customertype.thekey = customertype_key";
      if (activeId != null)
        query = query + " AND customer.active_id ";
      if (clinicId != null)
        query = query + " AND clinic_id = ? ";
      query = query + " ORDER by clinic_id DESC, last_name";
      ps = conn.prepareStatement(query);
      ps.setString(1, "%" + lastNameFilter.toUpperCase().trim() + "%");
      if (clinicId != null)
        ps.setBoolean(2, clinicId);
      rs = ps.executeQuery();

//--------------------------------------------------------------------------------
// Get the accounting values associated with this CUSTOMER.
// Define a permissionRow to evaluate the deleteId before checking dependency.
//--------------------------------------------------------------------------------
      PermissionListDO permissionRow = null;

      Integer pageMax;
      if (!jsonId) {
        if (request.getRequestURI().contains("Menu"))
          pageMax = new Integer(getPropertyService().getCustomerProperties(request,"menu.list.size"));
        else
          pageMax = new Integer(getPropertyService().getCustomerProperties(request,"customer.list.size"));
      } else {
        pageMax = new Integer(getPropertyService().getCustomerProperties(request,"json.list.size"));
      }

      Integer firstRecord = ((pageNo.intValue() - 1) * pageMax) + 1;
      Integer lastRecord = firstRecord + (pageMax - 1);

      Integer recordCount = 0;
      while ( rs.next() ) {
        if (!(request.getRequestURI().contains("Menu")))
          recordCount++;
        if (((recordCount >= firstRecord) && (recordCount <= lastRecord)) || (request.getRequestURI().contains("Menu"))) {
          customerRow = new CustomerListDO();

          customerRow.setKey(rs.getInt("thekey"));
          customerRow.setCodeNum(rs.getString("code_num"));
          if (rs.getBoolean("clinic_id"))
            customerRow.setCustomerTypeName(rs.getString("code_num"));
          else {
//--------------------------------------------------------------------------------
// Put a "+" in front of the primary and "-" in front of secondary accounts.
//--------------------------------------------------------------------------------
            if (customerRow.getKey() == rs.getInt("primary_key"))
              customerRow.setCustomerTypeName("+" + rs.getString("name"));
            else
              customerRow.setCustomerTypeName("-" + rs.getString("name"));
          }
          customerRow.setFirstName(rs.getString("first_name"));
          customerRow.setLastName(rs.getString("last_name"));
          customerRow.setClientName(rs.getString("client_name"));
          customerRow.setAddress(rs.getString("address"));
          customerRow.setAddress2(rs.getString("address2"));
          customerRow.setCity(rs.getString("city"));
          customerRow.setState(rs.getString("state"));
          customerRow.setZip(rs.getString("zip"));
          customerRow.setPhoneNum(rs.getString("phone_num"));
          customerRow.setPhoneExt(rs.getString("phone_ext"));
          customerRow.setMobileNum(rs.getString("mobile_num"));
          customerRow.setFaxNum(rs.getString("fax_num"));
          customerRow.setClinicId(rs.getBoolean("clinic_id"));
          customerRow.setAcctNum(rs.getString("acct_num"));
          customerRow.setEmail(rs.getString("email"));
          customerRow.setEmail2(rs.getString("email2"));
          customerRow.setWebSite(rs.getString("website"));
          customerRow.setLineOfBusiness(rs.getString("line_of_business"));
          customerRow.setContactName(rs.getString("contact_name"));
          customerRow.setReferredBy(rs.getString("referred_by"));
          customerRow.setNoteLine1(rs.getString("note_line1"));
          customerRow.setNoteLine2(rs.getString("note_line2"));

//--------------------------------------------------------------------------------
// JSON doesn't need last service, account balance, or dependency information.
//--------------------------------------------------------------------------------
          if (!jsonId) {
//--------------------------------------------------------------------------------
// Attempt to retrieve the last service date from the event schedule.
//--------------------------------------------------------------------------------
            customerRow.setLastServiceDate(getCustomerService().getLastServiceDate(conn,
                                                                 customerRow.getKey()));

//--------------------------------------------------------------------------------
// Retrieve the balance due amount from the customer accounting records.
//--------------------------------------------------------------------------------
            acctMap = getAccountingService().getCustomerBalance(conn, customerRow.getKey());
            customerRow.setAccountBalanceAmount((BigDecimal)(acctMap.get("balanceAmt")));
            customerRow.setAccountBalanceDate((Date)(acctMap.get("balanceDate")));

//--------------------------------------------------------------------------------
// 07-31-06  Define the permission information in separate class for re-usability.
//--------------------------------------------------------------------------------
            permissionRow = getUserStateService().getUserListToken(request,conn,
                               this.getClass().getName(),customerRow.getKey());
            permissionRows.add(permissionRow);

            if (permissionRow.getDeleteId() && clinicId == null) {
//--------------------------------------------------------------------------------
// 10-30-06  Check if the customer has any dependency on linked to tables.
//--------------------------------------------------------------------------------
              customerRow.setAllowDeleteId(getCustomerDependency(conn,
                                                                 sourceTypeKey,
                                                                 customerRow.getKey()));
            }
          }
//--------------------------------------------------------------------------------
// 02-22-07  Determine whether the content will go to menu or a regular customer list.
//--------------------------------------------------------------------------------
          if (request.getRequestURI().contains("Menu") && (customerRow.getAccountBalanceAmount().compareTo(new BigDecimal("0")) != 0)) {
            recordCount++;
            if ((recordCount >= firstRecord) && (recordCount <= lastRecord))
              customerRows.add(customerRow);
          }

          if (!(request.getRequestURI().contains("Menu")))
            customerRows.add(customerRow);
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
      formHDO.setLastNameFilter(lastNameFilter);
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
      if (customerRows.isEmpty()) {
        customerRows.add(new CustomerListDO());
        permissionRows.add(getUserStateService().getUserListToken(request,conn,
                           this.getClass().getName(),getUserStateService().getNoKey()));
      }
      formHDO.setCustomerListForm(customerRows);
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


  public Boolean getCustomerDependency(Connection conn,
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
      cStmt = conn.prepareCall("{call GetCustomerDependency(?,?,?,?)}");
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
      throw new Exception("getCustomerDependency() " + e.getMessage());
    }
    finally
    {
      try
      {
        cStmt.close();
      }
      catch (SQLException e) {
        cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
        throw new Exception("getCustomerDependency() " + e.getMessage());
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
      Boolean clinicId = null;
      if (request.getParameter("clinicId") != null) {
        clinicId = new Boolean(request.getParameter("clinicId").toString());
      }

      Boolean activeId = null;
      if (request.getParameter("activeId") != null) {
        activeId = new Boolean(request.getParameter("activeId").toString());
      }

      String lastNameFilter = "";
      if (request.getParameter("lastNameFilter") != null) {
        lastNameFilter = request.getParameter("lastNameFilter");
      }

      Integer pageNo = new Integer("1");
      if (request.getParameter("pageNo") != null) {
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
// The export action will leverage the already populated customer list.
//--------------------------------------------------------------------------------
      if (!(request.getRequestURI().contains("Export"))) {

        try {
//--------------------------------------------------------------------------------
// Instantiate a formDO object to populate formStr.
//--------------------------------------------------------------------------------
          CustomerListHeadDO formHDO = getCustomerList(request,
                                                       clinicId,
                                                       activeId,
                                                       lastNameFilter,
                                                       pageNo,
                                                       getUserStateService().getFeatureKey().byteValue(),
                                                       jsonId);
          if (jsonId) {
            JSONArray jsonItems = new JSONArray();
            JSONObject obj = null;

            if (formHDO.getRecordCount() > 0) {
              ArrayList myArray = formHDO.getCustomerListForm();
              CustomerListDO myDO = new CustomerListDO();
              for (int i = 0; i < myArray.size(); i++) {
                myDO = (CustomerListDO) myArray.get(i);

                obj = new JSONObject();
                obj.put("id", myDO.getKey().toString());
                obj.put("value", myDO.getClientName());
                obj.put("info", myDO.getAcctNum());
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
            request.getSession().setAttribute("customerListHDO", formHDO);

//--------------------------------------------------------------------------------
// Create the wrapper object for customer list.
//--------------------------------------------------------------------------------
            CustomerListHeadForm formHStr = new CustomerListHeadForm();
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
