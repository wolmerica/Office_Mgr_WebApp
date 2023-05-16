/*
 * BreedListAction.java
 *
 * Created on December 13, 2005, 01:23 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 *
 * 03/08/2006 - Add pagination to breed list display.
 */

package com.wolmerica.breed;

//--------------------------------------------------------------------------------
// @author Richard Wolschlager
//--------------------------------------------------------------------------------
import com.wolmerica.employee.EmployeesActionMapping;
import com.wolmerica.service.speciesbreed.SpeciesBreedService;
import com.wolmerica.service.speciesbreed.DefaultSpeciesBreedService;
import com.wolmerica.service.property.PropertyService;
import com.wolmerica.service.property.DefaultPropertyService;
import com.wolmerica.service.userstate.UserStateService;
import com.wolmerica.service.userstate.DefaultUserStateService;
import com.wolmerica.permission.PermissionListDO;
import com.wolmerica.tools.formatter.FormattingException;

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
import java.util.Enumeration;
import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONObject;

import org.apache.log4j.Logger;

public class BreedListAction extends Action {

  Logger cat = Logger.getLogger("WOWAPP");

  private PropertyService propertyService = new DefaultPropertyService();
  private SpeciesBreedService speciesBreedService = new DefaultSpeciesBreedService();
  private UserStateService userStateService = new DefaultUserStateService();

  public SpeciesBreedService getSpeciesBreedService() {
      return speciesBreedService;
  }

  public void setSpeciesBreedService(SpeciesBreedService speciesBreedService) {
      this.speciesBreedService = speciesBreedService;
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


  private BreedListHeadDO getBreedList(HttpServletRequest request,
                                       Integer speciesKey,
                                       String breedNameFilter,
                                       Integer pageNo,
                                       Byte sourceTypeKey,
                                       Boolean jsonId)
    throws Exception, SQLException {

    BreedListHeadDO formHDO = new BreedListHeadDO();
    BreedDO breedRow = null;
    ArrayList<BreedDO> breedRows = new ArrayList<BreedDO>();
    
    ArrayList<PermissionListDO> permissionRows = new ArrayList<PermissionListDO>();

    DataSource ds = null;
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    try {
      ds = getDataSource(request);
      conn = ds.getConnection();

      String query = "SELECT thekey,"
                   + "species_key,"
                   + "breed_name,"
                   + "breed_ext_id,"
                   + "create_user,"
                   + "create_stamp,"
                   + "update_user,"
                   + "update_stamp "
                   + "FROM breed "
                   + "WHERE breed_name LIKE ? ";
      if (speciesKey != null) {
        query += " AND species_key = ? ";
      }
      query += " ORDER BY breed_name";
      cat.debug(this.getClass().getName() + ": Query #1 " + query);
      ps = conn.prepareStatement(query);
      ps.setString(1, "%" + breedNameFilter.toUpperCase().trim() + "%");
      if (speciesKey != null) {
        ps.setInt(2, speciesKey);
      }
      rs = ps.executeQuery();
      cat.debug(this.getClass().getName() + ": executeQuery(): ");

      Integer pageMax;
      if (!jsonId)
        pageMax = new Integer(getPropertyService().getCustomerProperties(request,"breed.list.size"));
      else 
        pageMax = new Integer(getPropertyService().getCustomerProperties(request,"json.list.size"));
      
      int firstRecord = ((pageNo.intValue() - 1) * pageMax) + 1;
      int lastRecord = firstRecord + (pageMax - 1);

      PermissionListDO permissionRow = null;
      int recordCount = 0;
      while ( rs.next() ) {
        ++recordCount;
        if ((recordCount >= firstRecord) && (recordCount <= lastRecord)) {
          breedRow = new BreedDO();

          breedRow.setKey(rs.getInt("thekey"));
          breedRow.setSpeciesKey(rs.getInt("species_key"));
          breedRow.setBreedName(rs.getString("breed_name"));
          breedRow.setBreedExtId(rs.getString("breed_ext_id"));
          breedRow.setCreateUser(rs.getString("create_user"));
          breedRow.setCreateStamp(rs.getTimestamp("create_stamp"));
          breedRow.setUpdateUser(rs.getString("update_user"));
          breedRow.setUpdateStamp(rs.getTimestamp("update_stamp"));
          cat.debug(this.getClass().getName() + ": loop key: " + breedRow.getKey());
//--------------------------------------------------------------------------------
// 07-31-06  Define the permission information in separate class for re-usability.
//--------------------------------------------------------------------------------
          permissionRow = getUserStateService().getUserListToken(request,conn,
                             this.getClass().getName(),breedRow.getKey());
          permissionRows.add(permissionRow);

          if (permissionRow.getDeleteId() || permissionRow.getEditId()) {
//--------------------------------------------------------------------------------
// 10-30-06  Check if the bundle has any dependency on linked tables.
//--------------------------------------------------------------------------------
            breedRow.setAllowDeleteId(getBreedDependency(conn,
                                                         sourceTypeKey,
                                                         breedRow.getKey()));
          }

//--------------------------------------------------------------------------------
// Look up the species and breed names give the respective keys.
//--------------------------------------------------------------------------------
          if (breedRow.getSpeciesKey() != null)
            breedRow.setSpeciesName(getSpeciesBreedService().getSpeciesName(conn, breedRow.getSpeciesKey()));

          breedRows.add(breedRow);
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
      if (speciesKey != null) {
        cat.debug(this.getClass().getName() + ": formHDO.setSpecesKey()");
        formHDO.setSpeciesKey(speciesKey);
        formHDO.setSpeciesName(getSpeciesBreedService().getSpeciesName(conn, speciesKey));
      }
      
      formHDO.setBreedNameFilter(breedNameFilter);
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
      if (breedRows.isEmpty()) {
        breedRows.add(new BreedDO());
        permissionRows.add(getUserStateService().getUserListToken(request,conn,
                           this.getClass().getName(),getUserStateService().getNoKey()));
      }
      formHDO.setBreedListForm(breedRows);
      formHDO.setPermissionListForm(permissionRows);
      cat.debug(this.getClass().getName() + ": formHDO.setPermissionListForm()");
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

public Boolean getBreedDependency(Connection conn,
                                  Byte stKey,
                                  Integer brdKey)
   throws Exception, SQLException {

    CallableStatement cStmt = null;
    Boolean allowDeleteId = false;

    try
    {
//--------------------------------------------------------------------------------
// Call a procedure with two IN parameters and one OUT parameter.
//--------------------------------------------------------------------------------
      cStmt = conn.prepareCall("{call GetBreedDependency(?,?,?,?)}");
//--------------------------------------------------------------------------------
// Set the value for the IN parameter
//--------------------------------------------------------------------------------
      cStmt.setByte(1, stKey);
      cStmt.setInt(2, brdKey);
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
      throw new Exception("getBreedDependency() " + e.getMessage());
    }
    finally
    {
      try
      {
        cStmt.close();
      }
      catch (SQLException e) {
        cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
        throw new Exception("getBreedDependency() " + e.getMessage());
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
//--------------------------------------------------------------------------------
// Default target to success
//--------------------------------------------------------------------------------
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
// Traverse the request parameters and populate the appropriate values.
//--------------------------------------------------------------------------------
      Integer speciesKey = null;
      String breedNameFilter = "";
      Integer pageNo = new Integer("1");
      Boolean jsonId = false;
      cat.debug(this.getClass().getName() + ": BreedList params ");
      Enumeration paramNames = request.getParameterNames();
      String paramName = "";
      while (paramNames.hasMoreElements()) {
        paramName = (String)paramNames.nextElement();
        cat.debug(this.getClass().getName() + ": paramName = " + paramName);

        if (paramName.equals(new String("breedNameFilter"))) {
          breedNameFilter = request.getParameter(paramName);
          cat.debug(this.getClass().getName() + ": get[breedNameFilter] = " + breedNameFilter);
        }
        if (paramName.equals(new String("pageNo"))) {
          pageNo = new Integer(request.getParameter(paramName).toString());
          if (pageNo < 0)
            pageNo = new Integer("1");
          cat.debug(this.getClass().getName() + ": get[pageNo] = " + pageNo);
        }
        if (paramName.equals(new String("json"))) {
          jsonId = new Boolean(request.getParameter("json").toString());
          cat.debug(this.getClass().getName() + ": get[json] = " + jsonId);
        }
      }

      if (request.getParameter("speciesKey") != null) {
        if (!(request.getParameter("speciesKey").equalsIgnoreCase(""))) {
          speciesKey = new Integer(request.getParameter("speciesKey"));
          cat.debug(this.getClass().getName() + ": get[speciesKey] = " + speciesKey.toString());
        }
      }
      if (speciesKey != null)
        cat.debug(this.getClass().getName() + ": Not NULL speciesKey");
      else
        cat.debug(this.getClass().getName() + ": NULL speciesKey");

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
         // Instantiate a formHDO object to populate formHStr.
         BreedListHeadDO formHDO = getBreedList(request,
                                                speciesKey,
                                                breedNameFilter,
                                                pageNo,
                                                getUserStateService().getFeatureKey().byteValue(),
                                                jsonId);

          if (jsonId) {
            JSONArray jsonItems = new JSONArray();
            JSONObject obj = null;

            if (formHDO.getRecordCount() > 0) {
              ArrayList myArray = formHDO.getBreedListForm();
              BreedDO myDO = new BreedDO();
              for (int i = 0; i < myArray.size(); i++) {
                myDO = (BreedDO) myArray.get(i);

                obj = new JSONObject();
                obj.put("id", myDO.getKey().toString());
                obj.put("value", myDO.getBreedName());
                obj.put("info", myDO.getBreedExtId());
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
            cat.debug(this.getClass().getName() + ": setAttribute('breedListHDO'");
            request.getSession().setAttribute("breedListHDO", formHDO);

           // Create the wrapper object for breed list.
           BreedListHeadForm formHStr = new BreedListHeadForm();
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
