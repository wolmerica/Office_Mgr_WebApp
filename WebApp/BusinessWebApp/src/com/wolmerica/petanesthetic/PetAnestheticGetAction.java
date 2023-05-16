/*
 * PetAnestheticGetAction.java
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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.apache.log4j.Logger;

public class PetAnestheticGetAction extends Action {

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

  private PetAnestheticListHeadDO getPetAnesthetic(HttpServletRequest request,
                                                   Integer peKey,
                                                   Integer pageNo)
   throws Exception, SQLException {

    PetAnestheticListHeadDO formHDO = new PetAnestheticListHeadDO();
    PetAnestheticDO petA = null;
    ArrayList<PetAnestheticDO> petAs = new ArrayList<PetAnestheticDO>();

    DataSource ds = null;
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    try {
      ds = getDataSource(request);
      conn = ds.getConnection();

//--------------------------------------------------------------------------------
// Get the pet anesthetic records for the pet exam.
//--------------------------------------------------------------------------------
      String query = "SELECT petanesthetic.thekey,"
                   + "petanesthetic.petexam_key,"
                   + "DATE(schedule.start_stamp) AS treatment_date,"
                   + "petanesthetic.itemdictionary_key,"
                   + "itemdictionary.brand_name,"
                   + "itemdictionary.size,"
                   + "itemdictionary.size_unit,"
                   + "petanesthetic.application_type,"
                   + "petanesthetic.dose,"
                   + "petanesthetic.dose_unit,"
                   + "petanesthetic.route,"
                   + "HOUR(petanesthetic.start_stamp) AS start_hour,"
                   + "MINUTE(petanesthetic.start_stamp) AS start_minute,"              
                   + "petanesthetic.resource_key,"
                   + "resource.name AS resource_name,"
                   + "petanesthetic.create_user,"
                   + "petanesthetic.create_stamp,"
                   + "petanesthetic.update_user,"
                   + "petanesthetic.update_stamp "              
                   + "FROM petanesthetic, petexam, itemdictionary, resource, schedule "
                   + "WHERE petanesthetic.petexam_key = ? "
                   + "AND petanesthetic.petexam_key = petexam.thekey "
                   + "AND petexam.schedule_key = schedule.thekey "
                   + "AND petanesthetic.itemdictionary_key = itemdictionary.thekey "
                   + "AND petanesthetic.resource_key = resource.thekey "
                   + "ORDER BY petanesthetic.start_stamp";
      ps = conn.prepareStatement(query);
      ps.setInt(1, peKey);
      rs = ps.executeQuery();

      Integer pageMax = new Integer(getPropertyService().getCustomerProperties(request,"petanesthetic.list.size"));
      Integer firstRecord = ((pageNo.intValue() - 1) * pageMax) + 1;
      Integer lastRecord = firstRecord + (pageMax - 1);
      Short minQty = 0;
      Short maxQty = 0;

      Integer recordCount = 0;
      while ( rs.next() ) {
        ++recordCount;
        if ((recordCount >= firstRecord) && (recordCount <= lastRecord)) {
          petA = new PetAnestheticDO();
          
          petA.setKey(rs.getInt("thekey"));         
          petA.setPetExamKey(rs.getInt("petexam_key"));
          petA.setTreatmentDate(rs.getDate("treatment_date"));    
          petA.setItemDictionaryKey(rs.getInt("itemdictionary_key"));
          petA.setBrandName(rs.getString("brand_name"));
          petA.setSize(rs.getBigDecimal("size"));
          petA.setSizeUnit(rs.getString("size_unit"));
          petA.setApplicationType(rs.getByte("application_type"));
          petA.setDose(rs.getBigDecimal("dose"));
          petA.setDoseUnit(rs.getString("dose_unit"));
          petA.setRoute(rs.getString("route"));
          petA.setStartHour(rs.getByte("start_hour"));
          petA.setStartMinute(rs.getByte("start_minute"));
          petA.setResourceKey(rs.getInt("resource_key"));
          petA.setResourceName(rs.getString("resource_name"));
          petA.setCreateUser(rs.getString("create_user"));
          petA.setCreateStamp(rs.getTimestamp("create_stamp"));
          petA.setUpdateUser(rs.getString("update_user"));
          petA.setUpdateStamp(rs.getTimestamp("update_stamp"));
          
          petAs.add(petA);
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
      formHDO.setPetExamKey(peKey);
      formHDO.setRecordCount(recordCount);
      formHDO.setFirstRecord(firstRecord);
      formHDO.setLastRecord(lastRecord);
      formHDO.setPreviousPage(prevPage);
      formHDO.setNextPage(nextPage);
//--------------------------------------------------------------------------------
// A formatter issues exists during the population of empty lists.
// A work around is to populate one row when there is an emptyList.
// The cooresponding jsp also needs to check for the phantom row.
//--------------------------------------------------------------------------------
      if (petAs.isEmpty()) {
        petAs.add(new PetAnestheticDO());
      }
      formHDO.setPetAnestheticForm(petAs);
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
   throws Exception, IOException, SQLException, ServletException {

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
      request.setAttribute("key", theKey.toString());

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
                                              theKey);
      if (usToken.equalsIgnoreCase(getUserStateService().getLocked()))
        request.setAttribute(getUserStateService().getDisableEdit(), false);
      else if (usToken.equalsIgnoreCase(getUserStateService().getProhibited()))
        throw new Exception(getUserStateService().getAccessDenied());

      try {
//--------------------------------------------------------------------------------
// Instantiate a formHDO object to populate formHStr.
//--------------------------------------------------------------------------------
        PetAnestheticListHeadDO formHDO = getPetAnesthetic(request,
                                                           theKey,
                                                           pageNo);

        formHDO.setPermissionStatus(usToken);

        request.getSession().setAttribute("petanestheticHDO", formHDO);
//--------------------------------------------------------------------------------
// Create the wrapper object for customerinvoiceitem.
//--------------------------------------------------------------------------------
        PetAnestheticListHeadForm formHStr = new PetAnestheticListHeadForm();
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
