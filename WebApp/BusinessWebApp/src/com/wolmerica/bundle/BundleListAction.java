/*
 * BundleListAction.java
 *
 * Created on March 30, 2007, 7:35 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 *
 * 03/07/2006 - Add pagination to employee list display.
 */

package com.wolmerica.bundle;

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
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONObject;

import org.apache.log4j.Logger;

public class BundleListAction extends Action {

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


  private BundleListHeadDO getBundleList(HttpServletRequest request,
                                         String bundleNameFilter,
                                         String bundleCategoryFilter,
                                         Integer pageNo,
                                         Byte sourceTypeKey,
                                         Boolean jsonId)
    throws Exception, SQLException {

    BundleListHeadDO formHDO = new BundleListHeadDO();
    BundleListDO bundleRow = null;
    ArrayList<BundleListDO> bundleRows = new ArrayList<BundleListDO>();
    
    ArrayList<PermissionListDO> permissionRows = new ArrayList<PermissionListDO>();

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
                   + "category,"
                   + "duration_hour,"
                   + "duration_minute "
                   + "FROM bundle "
                   + "WHERE name LIKE ? "
                   + "AND category LIKE ? "
                   + "ORDER BY name, category";
      ps = conn.prepareStatement(query);
      ps.setString(1, "%" + bundleNameFilter.toUpperCase().trim() + "%");
      ps.setString(2, "%" + bundleCategoryFilter.toUpperCase().trim() + "%");
      rs = ps.executeQuery();

//--------------------------------------------------------------------------------
// Build a query to find out if a release is required for any services.
//--------------------------------------------------------------------------------
      query = "SELECT servicedictionary.name "
            + "FROM bundledetail, customerattributebyservice, pricebyservice, servicedictionary "
            + "WHERE servicedictionary.thekey = customerattributebyservice.servicedictionary_key "
            + "AND servicedictionary.release_id "
            + "AND customerattributebyservice.thekey = pricebyservice.customerattributebyservice_key "
            + "AND pricebyservice.thekey = bundledetail.source_key "
            + "AND bundledetail.bundle_key = ?";
      ps = conn.prepareStatement(query);

      Integer pageMax;
      if (!jsonId)
        pageMax = new Integer(getPropertyService().getCustomerProperties(request,"bundle.list.size"));
      else 
        pageMax = new Integer(getPropertyService().getCustomerProperties(request,"json.list.size"));
      
      Integer firstRecord = ((pageNo.intValue() - 1) * pageMax) + 1;
      Integer lastRecord = firstRecord + (pageMax - 1);

      PermissionListDO permissionRow = null;      
      String prevName = "";
      Boolean releaseId = false;
      Integer recordCount = 0;
      while ( rs.next() ) {
        ++recordCount;
        if ((recordCount >= firstRecord) && (recordCount <= lastRecord)) {
          bundleRow = new BundleListDO();

          bundleRow.setKey(rs.getInt("thekey"));
          bundleRow.setName(rs.getString("name"));
          bundleRow.setCategory(rs.getString("category"));
          bundleRow.setDurationHour(rs.getByte("duration_hour"));
          bundleRow.setDurationMinute(rs.getByte("duration_minute"));

          if (!jsonId) {
//--------------------------------------------------------------------------------
// Look-up the release indicator value for all services in the bundle.
//--------------------------------------------------------------------------------
            if (bundleRow.getName().compareToIgnoreCase(prevName) != 0) {
              ps.setInt(1, bundleRow.getKey());
              rs2 = ps.executeQuery();
              if (rs2.next())
                releaseId = true;
              else
                releaseId = false;
            }
            bundleRow.setReleaseId(releaseId);

//--------------------------------------------------------------------------------
// 07-31-06  Define the permission information in separate class for re-usability.
//--------------------------------------------------------------------------------
            permissionRow = getUserStateService().getUserListToken(request,conn,
                               this.getClass().getName(),bundleRow.getKey());
            permissionRows.add(permissionRow);

            if (permissionRow.getDeleteId() || permissionRow.getEditId()) {
//--------------------------------------------------------------------------------
// 10-30-06  Check if the bundle has any dependency on linked tables.
//--------------------------------------------------------------------------------
              bundleRow.setAllowDeleteId(getBundleDependency(conn,
                                                             sourceTypeKey,
                                                             bundleRow.getKey()));
            }
          }
          bundleRows.add(bundleRow);
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
      formHDO.setBundleNameFilter(bundleNameFilter);
      formHDO.setBundleCategoryFilter(bundleCategoryFilter);
      formHDO.setRecordCount(recordCount);
      formHDO.setFirstRecord(firstRecord);
      formHDO.setLastRecord(lastRecord);
      formHDO.setPreviousPage(prevPage);
      formHDO.setCurrentPage(prevPage + 1);
      formHDO.setNextPage(nextPage);
//--------------------------------------------------------------------------------
// A formatter issues exists during the populatin of empty lists.
// A work around is to populate one row when there is an emptyList.
// The cooresponding jsp also needs to check for the phantom row.
//--------------------------------------------------------------------------------
      if (bundleRows.isEmpty()) {
        bundleRows.add(new BundleListDO());
        permissionRows.add(getUserStateService().getUserListToken(request,conn,
                           this.getClass().getName(),getUserStateService().getNoKey()));
      }
      formHDO.setBundleListForm(bundleRows);
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


public Boolean getBundleDependency(Connection conn,
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
      cStmt = conn.prepareCall("{call GetBundleDependency(?,?,?,?)}");
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
      throw new Exception("setBundleDependency() " + e.getMessage());
    }
    finally
    {
      try
      {
        cStmt.close();
      }
      catch (SQLException e) {
        cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
        throw new Exception("setBundleDependency() " + e.getMessage());
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
      String bundleNameFilter = "";
      if (!(request.getParameter("bundleNameFilter") == null)) {
        bundleNameFilter = request.getParameter("bundleNameFilter");
      }

      String bundleCategoryFilter = "";
      if (!(request.getParameter("bundleCategoryFilter") == null)) {
        bundleCategoryFilter = request.getParameter("bundleCategoryFilter");
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

      try {
//--------------------------------------------------------------------------------
// Instantiate a formHDO object to populate formHStr.
//--------------------------------------------------------------------------------
        BundleListHeadDO formHDO = getBundleList(request,
                                                 bundleNameFilter,
                                                 bundleCategoryFilter,
                                                 pageNo,
                                                 getUserStateService().getFeatureKey().byteValue(),
                                                 jsonId);        
        if (jsonId) {
          JSONArray jsonItems = new JSONArray();
          JSONObject obj = null;

          if (formHDO.getRecordCount() > 0) {
            ArrayList myArray = formHDO.getBundleListForm();            
            BundleListDO myDO = new BundleListDO();
            for (int i = 0; i < myArray.size(); i++) {
              myDO = (BundleListDO) myArray.get(i);

              obj = new JSONObject();
              obj.put("id", myDO.getKey());
              obj.put("value", myDO.getName());
              obj.put("info", myDO.getCategory());
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
        
          request.getSession().setAttribute("bundleListHDO", formHDO);

//--------------------------------------------------------------------------------
// Create the wrapper object for goods and services bundle list.
//--------------------------------------------------------------------------------
          BundleListHeadForm formHStr = new BundleListHeadForm();
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