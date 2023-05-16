/*
 * SystemListAction.java
 *
 * Created on August 26, 2005, 11:19 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package com.wolmerica.system;

//--------------------------------------------------------------------------------
// @author Richard Wolschlager
//--------------------------------------------------------------------------------
import com.wolmerica.employee.EmployeesActionMapping;
import com.wolmerica.permission.PermissionListDO;
import com.wolmerica.service.attributeto.AttributeToService;
import com.wolmerica.service.attributeto.DefaultAttributeToService;
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
import org.json.JSONArray;
import org.json.JSONObject;

import org.apache.log4j.Logger;

public class SystemListAction extends Action {

  Logger cat = Logger.getLogger("WOWAPP");

  private AttributeToService attributeToService = new DefaultAttributeToService();
  private PropertyService propertyService = new DefaultPropertyService();
  private UserStateService userStateService = new DefaultUserStateService();

  public AttributeToService getAttributeToService() {
      return attributeToService;
  }

  public void setAttributeToService(AttributeToService attributeToService) {
      this.attributeToService = attributeToService;
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


  private SystemListHeadDO getSystemList(HttpServletRequest request,
                                         Integer cKey,
                                         String clientNameFilter,
                                         String makeModelFilter,
                                         Integer pageNo,
                                         Byte sourceTypeKey,
                                         Boolean jsonId)                                         
    throws Exception, SQLException {

    SystemListHeadDO formHDO = new SystemListHeadDO();
    SystemListDO systemRow = null;
    ArrayList<SystemListDO> systemRows = new ArrayList<SystemListDO>();
    
    ArrayList<PermissionListDO> permissionRows = new ArrayList<PermissionListDO>();

    DataSource ds = null;
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    try {
      ds = getDataSource(request);
      conn = ds.getConnection();

//--------------------------------------------------------------------------------
// Prepare an SQL query to retrieve the system records.
//--------------------------------------------------------------------------------
      String query = "SELECT system.thekey AS system_key,"
                   + "customer.thekey AS customer_key,"
                   + "customer.client_name,"
                   + "system.name,"
                   + "system.processor,"
                   + "system.memory,"
                   + "system.operating_system,"
                   + "system.system_date,"
                   + "system.active_id "
                   + "FROM system, customer "
                   + "WHERE UPPER(system.name) LIKE ? "
                   + "AND system.customer_key = customer.thekey ";
      if (cKey != null)
        query = query + " AND customer.thekey = ? "
                      + "AND system.active_id";
      else
        query = query + " AND UPPER(customer.client_name) LIKE ?";
      query = query + " ORDER BY customer.client_name, system.name";
      ps = conn.prepareStatement(query);
      ps.setString(1, "%" + makeModelFilter.toUpperCase().trim() + "%");
      if (cKey != null)
        ps.setInt(2, cKey);
      else
        ps.setString(2, "%" + clientNameFilter.toUpperCase().trim() + "%");
      rs = ps.executeQuery();

//--------------------------------------------------------------------------------
// Define a permissionRow to evaluate the deleteId before checking dependency.
//--------------------------------------------------------------------------------
      PermissionListDO permissionRow = null;

      Integer pageMax;
      if (!jsonId)
        pageMax = new Integer(getPropertyService().getCustomerProperties(request,"system.list.size"));
      else 
        pageMax = new Integer(getPropertyService().getCustomerProperties(request,"json.list.size"));
      
      Integer firstRecord = ((pageNo.intValue() - 1) * pageMax) + 1;
      Integer lastRecord = firstRecord + (pageMax - 1);

      Integer recordCount = 0;
      while ( rs.next() ) {
        ++recordCount;
        if ((recordCount >= firstRecord) && (recordCount <= lastRecord)) {
          systemRow = new SystemListDO();

          systemRow.setKey(rs.getInt("system_key"));
          systemRow.setClientName(rs.getString("client_name"));
          systemRow.setMakeModel(rs.getString("name"));
          systemRow.setProcessor(rs.getString("processor"));
          systemRow.setMemory(rs.getString("memory"));
          systemRow.setOperatingSystem(rs.getString("operating_system"));
          systemRow.setSystemDate(rs.getDate("system_date"));
          systemRow.setActiveId(rs.getBoolean("active_id"));
          
          if (!jsonId) {          
//--------------------------------------------------------------------------------
// Attempt to retrieve the last service date from the event schedule.
//--------------------------------------------------------------------------------
            systemRow.setLastServiceDate(getAttributeToService().getLastServiceDate(conn,
                                                     rs.getInt("customer_key"),
                                                     sourceTypeKey,
                                                     systemRow.getKey()));

//--------------------------------------------------------------------------------
// 07-31-06  Define the permission information in separate class for re-usability.
//--------------------------------------------------------------------------------
            permissionRow = getUserStateService().getUserListToken(request,conn,
                               this.getClass().getName(),systemRow.getKey());
            permissionRows.add(permissionRow);

            if (permissionRow.getDeleteId()) {
//--------------------------------------------------------------------------------
// 10-30-06  Check if the system has any dependency on linked to tables.
//--------------------------------------------------------------------------------
              systemRow.setAllowDeleteId(getAttributeToDependency(conn,
                                                                  sourceTypeKey,
                                                                  systemRow.getKey()));
            }
          }

          systemRows.add(systemRow);
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
      if (cKey != null)
        formHDO.setCustomerKeyFilter(cKey);
      else
        formHDO.setClientNameFilter(clientNameFilter);
      formHDO.setMakeModelFilter(makeModelFilter);
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
      if (systemRows.isEmpty())
        systemRows.add(new SystemListDO());
      if (permissionRows.isEmpty())
        permissionRows.add(getUserStateService().getUserListToken(request,conn,
                           this.getClass().getName(),getUserStateService().getNoKey()));

      formHDO.setSystemListForm(systemRows);
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

  public Boolean getAttributeToDependency(Connection conn,
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
      cStmt = conn.prepareCall("{call GetAttributeToDependency(?,?,?,?)}");
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
      throw new Exception("getAttributeToDependency() " + e.getMessage());
    }
    finally
    {
      try
      {
        cStmt.close();
      }
      catch (SQLException e) {
        cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
        throw new Exception("getAttributeToDependency() " + e.getMessage());
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
      Integer cKey = null;
      if (request.getParameter("customerKeyFilter") != null) {
        if (!request.getParameter("customerKeyFilter").equalsIgnoreCase(""))
          cKey = new Integer(request.getParameter("customerKeyFilter"));
      }

      String clientNameFilter = "";
      if (request.getParameter("clientNameFilter") != null) {
        clientNameFilter = request.getParameter("clientNameFilter");
      }

      String makeModelFilter = "";
      if (request.getParameter("makeModelFilter") != null) {
        makeModelFilter = request.getParameter("makeModelFilter");
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

      try {
//--------------------------------------------------------------------------------
// Instantiate a formDO object to populate formStr.
//--------------------------------------------------------------------------------
        SystemListHeadDO formHDO = getSystemList(request,
                                                 cKey,
                                                 clientNameFilter,
                                                 makeModelFilter,
                                                 pageNo,
                                                 getUserStateService().getFeatureKey().byteValue(),
                                                 jsonId);
        formHDO.setSourceTypeKey(getUserStateService().getFeatureKey().byteValue());
        
        if (jsonId) {
          JSONArray jsonItems = new JSONArray();
          JSONObject obj = null;

          if (formHDO.getRecordCount() > 0) {
            ArrayList myArray = formHDO.getSystemListForm();            
            SystemListDO myDO = new SystemListDO();
            for (int i = 0; i < myArray.size(); i++) {
              myDO = (SystemListDO) myArray.get(i);

              obj = new JSONObject();
              obj.put("id", myDO.getKey());
              obj.put("value", myDO.getMakeModel());
              obj.put("info", myDO.getOperatingSystem());
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
        
          request.getSession().setAttribute("systemListHDO", formHDO);
//--------------------------------------------------------------------------------
// Create the wrapper object for system list.
//--------------------------------------------------------------------------------
          SystemListHeadForm formHStr = new SystemListHeadForm();
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
