/*
 * PetAnestheticEditAction.java
 *
 * Created on August 24, 2007, 07:40 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 *
 */

package com.wolmerica.petanesthetic;

//--------------------------------------------------------------------------------
// @author Richard Wolschlager
//--------------------------------------------------------------------------------
import com.wolmerica.employee.EmployeesActionMapping;
import com.wolmerica.service.userstate.UserStateService;
import com.wolmerica.service.userstate.DefaultUserStateService;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import java.util.ArrayList;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Calendar;
import java.sql.Timestamp;

import org.apache.log4j.Logger;

public class PetAnestheticEditAction extends Action {

  Logger cat = Logger.getLogger("WOWAPP");

  private UserStateService userStateService = new DefaultUserStateService();

  public UserStateService getUserStateService() {
      return userStateService;
  }

  public void setUserStateService(UserStateService userStateService) {
      this.userStateService = userStateService;
  }
  

  private void updatePetAnesthetic(HttpServletRequest request,
                                  ActionForm form)
   throws Exception, IOException, SQLException {

    DataSource ds = null;
    Connection conn = null;
    PreparedStatement ps = null;
    PreparedStatement ps2 = null;

    try {
      ds = getDataSource(request);
      conn = ds.getConnection();

      PetAnestheticListHeadDO formHDO = (PetAnestheticListHeadDO) request.getSession().getAttribute("petanestheticHDO");

//--------------------------------------------------------------------------------
// Prepare a query to update the goods and services bundle detail table.
//--------------------------------------------------------------------------------
      String query = "UPDATE petanesthetic SET "
                   + "application_type=?,"
                   + "dose=?,"
                   + "dose_unit=?,"
                   + "route=?,"
                   + "start_stamp=?,"
                   + "resource_key=?,"
                   + "update_user=?,"
                   + "update_stamp=CURRENT_TIMESTAMP "
                   + "WHERE thekey=?";
      ps = conn.prepareStatement(query);

//--------------------------------------------------------------------------------
// Traverse the bundle detail object.
//--------------------------------------------------------------------------------
      ArrayList paList = formHDO.getPetAnestheticForm();
      PetAnestheticDO formDO = null;
      Calendar calValue = Calendar.getInstance();
      Timestamp startStamp = null;

//--------------------------------------------------------------------------------
// Iterate through the rows in the pet anesthetic list.
//--------------------------------------------------------------------------------
      for (int j=0; j < paList.size(); j++) {
        formDO = (PetAnestheticDO) paList.get(j);

//--------------------------------------------------------------------------------
// Construct the start stamp value using the treatment date with start hour/minute.
//--------------------------------------------------------------------------------    
         calValue.setTime(formDO.getTreatmentDate()); 
         calValue.set(calValue.get(Calendar.YEAR),
                     calValue.get(Calendar.MONTH),
                     calValue.get(Calendar.DATE),
                     formDO.getStartHour().intValue(),
                     formDO.getStartMinute().intValue(),
                     0);
        startStamp = new Timestamp(calValue.getTime().getTime());
        startStamp.setNanos(0);        

//--------------------------------------------------------------------------------
// Perform the normal edit operations for pet anesthetic.
//--------------------------------------------------------------------------------
        ps.setByte(1, formDO.getApplicationType());
        ps.setBigDecimal(2, formDO.getDose());
        ps.setString(3, formDO.getDoseUnit());
        ps.setString(4, formDO.getRoute());
        ps.setTimestamp(5, startStamp);
        ps.setInt(6, formDO.getResourceKey());
        ps.setString(7, request.getSession().getAttribute("USERNAME").toString());
        ps.setInt(8, formDO.getKey());
        ps.executeUpdate();
      }
    }
    catch (SQLException e) {
      cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
    }
    finally {
      if (ps != null) {
        try {
          ps.close();
        }
        catch (SQLException sqle) {
            cat.error(this.getClass().getName() + ": SQLException : " + sqle.getMessage());
        }
        ps = null;
      }
      if (ps2 != null) {
        try {
          ps2.close();
        }
        catch (SQLException sqle) {
            cat.error(this.getClass().getName() + ": SQLException : " + sqle.getMessage());
        }
        ps2 = null;
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
  }

    @Override
  public ActionForward execute(ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
   throws Exception, SQLException {

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
      Integer theKey = null;
      if (!(request.getParameter("key") == null)) {
        theKey = new Integer(request.getParameter("key"));
      }
      else {
        if (!(request.getAttribute("key") == null)) {
          theKey = new Integer(request.getAttribute("key").toString());
        }
        else {
          throw new Exception("Request getParameter/getAttribute [key] not found!");
        }
      }
      cat.debug(this.getClass().getName() + ": get[key] = " + theKey.toString());

//--------------------------------------------------------------------------------
// 07-23-06  Code to handle permissions and avoid simultaneous updates to records.
//--------------------------------------------------------------------------------
      
      String usToken = getUserStateService().getUserToken(request,
                                              this.getDataSource(request).getConnection(),
                                              this.getClass().getName(),
                                              theKey);
      if (usToken.equalsIgnoreCase(getUserStateService().getLocked()))
        request.setAttribute(getUserStateService().getDisableEdit(), false);
      else if (usToken.equalsIgnoreCase(getUserStateService().getProhibited()))
        throw new Exception(getUserStateService().getAccessDenied());

      updatePetAnesthetic(request,form);
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