/*
 * EmployeeListAction.java
 *
 * Created on August 15, 2005, 7:49 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package com.wolmerica.employee;

//--------------------------------------------------------------------------------
// @author Richard Wolschlager
//--------------------------------------------------------------------------------
import com.wolmerica.permission.PermissionListDO;
import com.wolmerica.service.property.PropertyService;
import com.wolmerica.service.property.DefaultPropertyService;
import com.wolmerica.service.userstate.UserStateService;
import com.wolmerica.service.userstate.DefaultUserStateService;
import com.wolmerica.tools.formatter.FormattingException;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.ServletContext;
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

public class EmployeeListAction extends Action {

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

  
  private EmployeeListHeadDO getEmployeeList(HttpServletRequest request,
                                             String employeeNameFilter,
                                             Integer pageNo,
                                             Byte sourceTypeKey)
    throws Exception, SQLException {

    EmployeeListHeadDO formHDO = new EmployeeListHeadDO();
    EmployeeListDO employeeRow = null;
    ArrayList<EmployeeListDO> employeeRows = new ArrayList<EmployeeListDO>();
    
    ArrayList<PermissionListDO> permissionRows = new ArrayList<PermissionListDO>();

    DataSource ds = null;
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    ServletContext context = servlet.getServletContext();

    try {
      ds = getDataSource(request);
      conn = ds.getConnection();

//--------------------------------------------------------------------------------
// Look-up the employer from the customer table to populate "Company" for export.
//--------------------------------------------------------------------------------      
      String employerName = "";
      String query = "SELECT client_name "
                   + "FROM customer "
                   + "WHERE clinic_id";
      ps = conn.prepareStatement(query);
      rs = ps.executeQuery();
      if (rs.next())
        employerName = rs.getString("client_name");

//--------------------------------------------------------------------------------
// An ADMIN user will be able to view all the employee records.  A non-ADMIN
// user will only have privileges to view and edit their own record.
//--------------------------------------------------------------------------------
      query = "SELECT thekey,"
            + "user_name,"
            + "admin_id,"
            + "first_name,"
            + "last_name,"
            + "address,"
            + "address2,"
            + "city,"
            + "state,"
            + "zip,"
            + "phone,"
            + "phone_num,"
            + "phone_ext,"                   
            + "mobile_num,"            
            + "email,"
            + "email2,"
            + "yim_id "
            + "FROM employee "
            + "WHERE UPPER(first_name) LIKE ? ";      
//--------------------------------------------------------------------------------
// The non-admin user will only be able to view there own account information.
//--------------------------------------------------------------------------------      
      if (request.getSession().getAttribute("ADMIN").toString().compareToIgnoreCase("false") == 0)
        query = query + "AND user_name = ? ";      
//--------------------------------------------------------------------------------
// Only get the employees who have a email address.
//--------------------------------------------------------------------------------
      if (request.getRequestURI().contains("Event"))
        query = query + "AND email != '' ";      
      query = query + "ORDER BY user_name";
      
      cat.debug(this.getClass().getName() + ": URI : " + request.getRequestURI());      
      cat.debug(this.getClass().getName() + ": query : " + query);
      
      ps = conn.prepareStatement(query);
      ps.setString(1, "%" + employeeNameFilter.toUpperCase().trim() + "%");
      if (request.getSession().getAttribute("ADMIN").toString().compareToIgnoreCase("false") == 0)
        ps.setString(2, request.getSession().getAttribute("USERNAME").toString());
      rs = ps.executeQuery();
          
//--------------------------------------------------------------------------------
// Define a permissionRow to evaluate the deleteId before checking dependency.
//--------------------------------------------------------------------------------
      PermissionListDO permissionRow = null;

      Integer pageMax = new Integer(getPropertyService().getCustomerProperties(request,"employee.list.size"));
      Integer firstRecord = ((pageNo.intValue() - 1) * pageMax) + 1;
      Integer lastRecord = firstRecord + (pageMax - 1);

      Integer recordCount = 0;
      while ( rs.next() ) {
        ++recordCount;
        if ((recordCount >= firstRecord) && (recordCount <= lastRecord)) {
          employeeRow = new EmployeeListDO();

          employeeRow.setKey(rs.getInt("thekey"));
          employeeRow.setUserName(rs.getString("user_name"));
          employeeRow.setAdminId(rs.getBoolean("admin_id"));
          employeeRow.setFirstName(rs.getString("first_name"));
          employeeRow.setLastName(rs.getString("last_name"));
          employeeRow.setAddress(rs.getString("address"));
          employeeRow.setAddress2(rs.getString("address2"));
          employeeRow.setCity(rs.getString("city"));
          employeeRow.setState(rs.getString("state"));
          employeeRow.setZip(rs.getString("zip"));
          employeeRow.setPhone(rs.getString("phone"));
          employeeRow.setPhoneNum(rs.getString("phone_num"));
          employeeRow.setPhoneExt(rs.getString("phone_ext"));          
          employeeRow.setMobileNum(rs.getString("mobile_num"));          
          employeeRow.setEmail(rs.getString("email"));
          employeeRow.setEmail2(rs.getString("email2"));
          employeeRow.setYimId(rs.getString("yim_id"));
          employeeRow.setEmployerName(employerName);
              
//--------------------------------------------------------------------------------
// Skip the permissions if we are only interested in showing the yahoo id.
//--------------------------------------------------------------------------------          
          if (!(request.getRequestURI().contains("Event"))) {
//--------------------------------------------------------------------------------
// 07-31-06  Define the permission information in separate class for re-usability.
//--------------------------------------------------------------------------------
            permissionRow = getUserStateService().getUserListToken(request,conn,
                               this.getClass().getName(),employeeRow.getKey());
            permissionRows.add(permissionRow);

            if (permissionRow.getDeleteId()) {
//--------------------------------------------------------------------------------
// 10-30-06  Check if the employee has any dependency on linked to tables.
//--------------------------------------------------------------------------------
              employeeRow.setAllowDeleteId(getEmployeeDependency(conn,
                                                                 sourceTypeKey,
                                                                 employeeRow.getKey()));
            }
          }
          employeeRows.add(employeeRow);
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
      formHDO.setEmployeeNameFilter(employeeNameFilter);
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
      if (employeeRows.isEmpty()) {
        employeeRows.add(new EmployeeListDO());
      }
      if (permissionRows.isEmpty()) {      
        permissionRows.add(getUserStateService().getUserListToken(request,conn,
                           this.getClass().getName(),getUserStateService().getNoKey()));
      }
      formHDO.setEmployeeListForm(employeeRows);
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

  public Boolean getEmployeeDependency(Connection conn,
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
      cStmt = conn.prepareCall("{call GetEmployeeDependency(?,?,?,?)}");
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
      throw new Exception("getEmployeeDependency() " + e.getMessage());
    }
    finally
    {
      try
      {
        cStmt.close();
      }
      catch (SQLException e) {
        cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
        throw new Exception("getEmployeeDependency() " + e.getMessage());
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
      String employeeNameFilter = "";
      if (!(request.getParameter("employeeNameFilter") == null)) {
        employeeNameFilter = request.getParameter("employeeNameFilter");
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
      
//--------------------------------------------------------------------------------
// The export action will leverage the already populated customer list.
//--------------------------------------------------------------------------------
      if (!(request.getRequestURI().contains("Export"))) {         

        try {
//--------------------------------------------------------------------------------
// Instantiate a formDO object to populate formStr.
//--------------------------------------------------------------------------------
          EmployeeListHeadDO formHDO = getEmployeeList(request,
                                                       employeeNameFilter,
                                                       pageNo,
                                                       getUserStateService().getFeatureKey().byteValue());
          request.getSession().setAttribute("employeeListHDO", formHDO);

//--------------------------------------------------------------------------------          
// Create the wrapper object for employee list.
//--------------------------------------------------------------------------------
          EmployeeListHeadForm formHStr = new EmployeeListHeadForm();
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
