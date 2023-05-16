/*
 * PetAddAction.java
 *
 * Created on December 09, 2005, 12:50 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 * 12/19/2005 Implement tools.formatter library.
 */

package com.wolmerica.pet;

//--------------------------------------------------------------------------------
// @author Richard Wolschlager
//--------------------------------------------------------------------------------
import com.wolmerica.employee.EmployeesActionMapping;
import com.wolmerica.service.userstate.UserStateService;
import com.wolmerica.service.userstate.DefaultUserStateService;

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

import org.apache.log4j.Logger;


public class PetAddAction extends Action {

  Logger cat = Logger.getLogger("WOWAPP");


  private UserStateService userStateService = new DefaultUserStateService();

  public UserStateService getUserStateService() {
      return userStateService;
  }

  public void setUserStateService(UserStateService userStateService) {
      this.userStateService = userStateService;
  }

  
  private Integer insertPet(HttpServletRequest request,
                            ActionForm form)
    throws Exception, SQLException {

    DataSource ds = null;
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    Integer pKey = 1;

    try {
      ds = getDataSource(request);
      conn = ds.getConnection();

      PetDO formDO = (PetDO) request.getSession().getAttribute("pet");

//--------------------------------------------------------------------------------
// Get the maximum key from the pet key.
//--------------------------------------------------------------------------------
      String query = "SELECT COUNT(*) AS pet_cnt, MAX(thekey)+1 AS pet_key "
                   + "FROM pet";
      ps = conn.prepareStatement(query);
      rs = ps.executeQuery();
      if ( rs.next() ) {
        if ( rs.getInt("pet_cnt") > 0 ) {
          pKey = rs.getInt("pet_key");
        }
      }
      else {
        throw new Exception("Pet MAX() not found!");
      }

//===========================================================================
// Prepare a SQL query to insert a row into the pet table.
//===========================================================================
      query = "INSERT INTO pet "
            + "(thekey,"
            + "customer_key,"
            + "name,"
            + "species_key,"
            + "color,"
            + "sex_id,"
            + "weight,"
            + "birth_date,"
            + "neutered_id,"
            + "neutered_date,"
            + "disposition,"
            + "identification_tag_number,"
            + "rabies_tag_number,"
            + "dvm_resource_key,"
            + "pet_memo,"
            + "last_check_date,"
            + "active_id,"
            + "create_user,"
            + "create_stamp,"
            + "update_user,"
            + "update_stamp )"
            + "values (?,?,?,?,?,?,?,?,?,?,"
            + "?,?,?,?,?,?,?,?,CURRENT_TIMESTAMP,?,CURRENT_TIMESTAMP)";
      ps = conn.prepareStatement(query);
      ps.setInt(1, pKey);
      ps.setInt(2, formDO.getCustomerKey());
      ps.setString(3, formDO.getPetName());
      ps.setInt(4, formDO.getSpeciesKey());
      ps.setString(5, formDO.getPetColor());
      ps.setByte(6, formDO.getPetSexId());
      ps.setBigDecimal(7, formDO.getPetWeight());
      ps.setDate(8, new java.sql.Date(formDO.getBirthDate().getTime()));
      ps.setByte(9, formDO.getNeuteredId());
      ps.setDate(10, new java.sql.Date(formDO.getNeuteredDate().getTime()));
      ps.setString(11, formDO.getDisposition());
      ps.setString(12, formDO.getIdentificationTagNumber());
      ps.setString(13, formDO.getRabiesTagNumber());
      ps.setInt(14, formDO.getDvmResourceKey());
      ps.setString(15, formDO.getPetMemo());
      ps.setDate(16, new java.sql.Date(formDO.getLastCheckDate().getTime()));
      ps.setBoolean(17, formDO.getActiveId());
      ps.setString(18, request.getSession().getAttribute("USERNAME").toString());
      ps.setString(19, request.getSession().getAttribute("USERNAME").toString());
      ps.executeUpdate();
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
    return pKey;
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

      Integer theKey = null;
      theKey = insertPet(request, form);
      request.setAttribute("key", theKey.toString());
      request.setAttribute("sourceTypeKey", getUserStateService().getFeatureKey().toString());
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