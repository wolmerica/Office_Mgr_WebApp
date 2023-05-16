/*
 * UserStateListAction.java
 *
 * Created on July 25, 2006, 06:23 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 *
 * 03/08/2006 - Add pagination to list display.
 */

package com.wolmerica.userstate;

//--------------------------------------------------------------------------------
// @author Richard Wolschlager
//--------------------------------------------------------------------------------
import com.wolmerica.employee.EmployeesActionMapping;
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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.apache.log4j.Logger;

public class UserStateListAction extends Action {

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
  
  private UserStateListHeadDO getUserState(HttpServletRequest request,
                                           Integer pageNo)
    throws Exception, SQLException {

    UserStateListHeadDO formHDO = new UserStateListHeadDO();
    UserStateListDO userStateRow = null;
    ArrayList<UserStateListDO> userStateRows = new ArrayList<UserStateListDO>();
        
    ArrayList<PermissionListDO> permissionRows = new ArrayList<PermissionListDO>();    

    DataSource ds = null;
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    try {
      ds = getDataSource(request);
      conn = ds.getConnection();

      String query = "SELECT thekey,"
                   + "user_name,"
                   + "token_name,"
                   + "feature_type,"
                   + "feature_key,"
                   + "create_stamp,"
                   + "update_stamp "
                   + "FROM userstate "
                   + "ORDER by user_name, update_stamp";
      cat.debug(this.getClass().getName() + ": Query #1 " + query);
      ps = conn.prepareStatement(query);
      rs = ps.executeQuery();
//--------------------------------------------------------------------------------
// Define a permissionRow to evaluate the deleteId before checking dependency.
//--------------------------------------------------------------------------------
      PermissionListDO permissionRow = null;
      
      Integer pageMax = new Integer(getPropertyService().getCustomerProperties(request,"userstate.list.size"));
      int firstRecord = ((pageNo.intValue() - 1) * pageMax) + 1;
      int lastRecord = firstRecord + (pageMax - 1);

      int recordCount = 0;
      int menuKey = 0;
      while ( rs.next() ) {
        ++recordCount;
        if ((recordCount >= firstRecord) && (recordCount <= lastRecord)) {
          userStateRow = new UserStateListDO();

          userStateRow.setKey(rs.getInt("thekey"));
          userStateRow.setUserName(rs.getString("user_name"));
          userStateRow.setTokenName(rs.getString("token_name"));
          userStateRow.setFeatureType(rs.getInt("feature_type"));
          userStateRow.setFeatureKey(rs.getInt("feature_key"));
          userStateRow.setCreateStamp(rs.getTimestamp("create_stamp"));
          userStateRow.setUpdateStamp(rs.getTimestamp("update_stamp"));

//--------------------------------------------------------------------------------
// Add the feature name to the list for readability reasons.
//--------------------------------------------------------------------------------
          menuKey = userStateRow.getFeatureType() / 10;
          userStateRow.setFeatureName(getPropertyService().getFeatureProperties("userstate."+menuKey));
          
//--------------------------------------------------------------------------------
// 07-31-06  Define the permission information in separate class for re-usability.
//--------------------------------------------------------------------------------
          permissionRow = getUserStateService().getUserListToken(request,conn,
                             this.getClass().getName(),userStateRow.getKey());
          permissionRows.add(permissionRow);
          
          userStateRows.add(userStateRow);
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
      if (userStateRows.isEmpty())
        userStateRows.add(new UserStateListDO());
      if (permissionRows.isEmpty())
        permissionRows.add(getUserStateService().getUserListToken(request,conn,
                           this.getClass().getName(),getUserStateService().getNoKey()));

      formHDO.setUserStateListForm(userStateRows);
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
// Instantiate a formHDO object to populate formHStr.
//--------------------------------------------------------------------------------
         UserStateListHeadDO formHDO = getUserState(request,
                                                    pageNo);
         request.getSession().setAttribute("userStateListHDO", formHDO);

//--------------------------------------------------------------------------------
// Create the wrapper object for user state list.
//--------------------------------------------------------------------------------
         UserStateListHeadForm formHStr = new UserStateListHeadForm();
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
