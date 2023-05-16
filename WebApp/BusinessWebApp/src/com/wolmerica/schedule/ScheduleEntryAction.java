/*
 * ScheduleEntryAction.java
 *
 * Created on September 7, 2006, 10:00 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 * 12/19/2005 Implement tools.formatter library.
 */

package com.wolmerica.schedule;

//--------------------------------------------------------------------------------
// @author Richard Wolschlager
//--------------------------------------------------------------------------------
import com.wolmerica.employee.EmployeesActionMapping;
import com.wolmerica.petvac.PetVacDO;
import com.wolmerica.petboard.PetBoardDO;
import com.wolmerica.service.attributeto.AttributeToService;
import com.wolmerica.service.attributeto.DefaultAttributeToService;
import com.wolmerica.service.userstate.UserStateService;
import com.wolmerica.service.userstate.DefaultUserStateService;
import com.wolmerica.tools.formatter.DateFormatter;
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
import java.util.Date;
import java.util.HashMap;

import org.apache.log4j.Logger;

public class ScheduleEntryAction extends Action {

  Logger cat = Logger.getLogger("WOWAPP");

  private AttributeToService attributeToService = new DefaultAttributeToService();
  private UserStateService userStateService = new DefaultUserStateService();

  public AttributeToService getAttributeToService() {
      return attributeToService;
  }

  public void setAttributeToService(AttributeToService attributeToService) {
      this.attributeToService = attributeToService;
  }

  public UserStateService getUserStateService() {
      return userStateService;
  }

  public void setUserStateService(UserStateService userStateService) {
      this.userStateService = userStateService;
  }


  private ScheduleDO populateFormFromEvent(HttpServletRequest request)
   throws Exception {

    ScheduleDO formDO = new ScheduleDO();
    ScheduleDO scheduleDO = (ScheduleDO) request.getSession().getAttribute("schedule");

    formDO.setEventTypeKey(scheduleDO.getEventTypeKey());
    formDO.setSubject(scheduleDO.getSubject());
    formDO.setLocationId(scheduleDO.getLocationId());
    formDO.setCustomerKey(scheduleDO.getCustomerKey());
    formDO.setClientName(scheduleDO.getClientName());
    formDO.setCustomerTypeKey(scheduleDO.getCustomerTypeKey());
    formDO.setSourceTypeKey(scheduleDO.getSourceTypeKey());
    formDO.setSourceKey(scheduleDO.getSourceKey());
    formDO.setAttributeToName(scheduleDO.getAttributeToName());
    formDO.setStartHour(scheduleDO.getStartHour());
    formDO.setStartMinute(scheduleDO.getStartMinute());
    formDO.setAddressId(scheduleDO.getAddressId());
    formDO.setAddress(scheduleDO.getAddress());
    formDO.setCity(scheduleDO.getCity());
    formDO.setState(scheduleDO.getState());
//--------------------------------------------------------------------------------
// Set the start date to the current date to allow editing of the record.
//--------------------------------------------------------------------------------
    formDO.setStartDate(new Date());
    formDO.setEndDate(formDO.getStartDate());
    formDO.setEndHour(formDO.getStartHour());
    formDO.setEndMinute(formDO.getEndMinute());

    return formDO;
  }

  private ScheduleDO populateFormFromPetVac(HttpServletRequest request)
   throws Exception, SQLException {

    DataSource ds = null;
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    ScheduleDO formDO = new ScheduleDO();

    try {
      ds = getDataSource(request);
      conn = ds.getConnection();

      PetVacDO petVacDO = (PetVacDO) request.getSession().getAttribute("petvac");
//--------------------------------------------------------------------------------
// We need to get more details about the customer using the getPetKey() value.
//--------------------------------------------------------------------------------
      String query = "SELECT pet.thekey AS pet_key,"
                   + "customer.thekey AS customer_key,"
                   + "customer.client_name,"
                   + "customer.customertype_key,"
                   + "customertype.attribute_to_entity "
                   + "FROM pet, customer, customertype, schedule "
                   + "WHERE schedule.thekey = ? "
                   + "AND schedule.source_key = pet.thekey "
                   + "AND schedule.customer_key = customer.thekey "
                   + "AND customer.customertype_key = customertype.thekey";
      ps = conn.prepareStatement(query);
      ps.setInt(1, petVacDO.getScheduleKey());
      rs = ps.executeQuery();
      if (rs.next()) {
        formDO.setCustomerKey(rs.getInt("customer_key"));
        formDO.setClientName(rs.getString("client_name"));
        formDO.setSourceKey(rs.getInt("pet_key"));
        formDO.setCustomerTypeKey(rs.getByte("customertype_key"));
        formDO.setAttributeToEntity(rs.getString("attribute_to_entity"));
        formDO.setSourceTypeKey(new Byte("4"));

        HashMap nameMap = getAttributeToService().getAttributeToName(conn, new Byte("4"), formDO.getSourceKey());
        formDO.setAttributeToName(nameMap.get("attributeToName").toString());
      }
      else {
        throw new Exception("Customer details via schedule " + petVacDO.getScheduleKey() + " not found!");
      }

//--------------------------------------------------------------------------------
// Populate the rest of the schedule object from the pet vaccination values.
//--------------------------------------------------------------------------------
      formDO.setEventTypeKey(new Byte("5"));
      formDO.setSubject(new String("Vaccination Expiration"));

//--------------------------------------------------------------------------------
// Set the start date to the current date to allow editing of the record.
//--------------------------------------------------------------------------------
      formDO.setStartDate(petVacDO.getVacExpirationDate());
      formDO.setEndDate(formDO.getStartDate());
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

    return formDO;
  }


  private ScheduleDO populateFormFromPetBoard(HttpServletRequest request)
   throws Exception, SQLException {

    DataSource ds = null;
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    ScheduleDO formDO = new ScheduleDO();

    try {
      ds = getDataSource(request);
      conn = ds.getConnection();

      PetBoardDO petBoardDO = (PetBoardDO) request.getSession().getAttribute("petboard");
//--------------------------------------------------------------------------------
// We need to get more details about the customer using the getPetKey() value.
//--------------------------------------------------------------------------------
      String query = "SELECT customer.thekey,"
                   + "customer.client_name,"
                   + "customer.customertype_key,"
                   + "customertype.attribute_to_entity "
                   + "FROM pet, customer, customertype "
                   + "WHERE pet.thekey = ? "
                   + "AND pet.customer_key = customer.thekey "
                   + "AND customer.customertype_key = customertype.thekey";
      ps = conn.prepareStatement(query);
      ps.setInt(1, petBoardDO.getPetKey());
      rs = ps.executeQuery();
      if (rs.next()) {
        formDO.setCustomerKey(rs.getInt("thekey"));
        formDO.setClientName(rs.getString("client_name"));
        formDO.setCustomerTypeKey(rs.getByte("customertype_key"));
        formDO.setAttributeToEntity(rs.getString("attribute_to_entity"));
        formDO.setSourceTypeKey(new Byte("4"));
      }
      else {
        throw new Exception("Customer details for pet " + petBoardDO.getPetKey() + " not found!");
      }

//--------------------------------------------------------------------------------
// Populate the rest of the schedule object from the pet boarding values.
//--------------------------------------------------------------------------------
      formDO.setEventTypeKey(new Byte("7"));
      formDO.setSubject(new String("Animal Boarding"));
      formDO.setSourceKey(petBoardDO.getPetKey());
      formDO.setAttributeToName(petBoardDO.getPetName());
//--------------------------------------------------------------------------------
// Set the start date to the current date to allow editing of the record.
//--------------------------------------------------------------------------------
      formDO.setStartDate(new Date());
      formDO.setEndDate(formDO.getStartDate());
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

    return formDO;
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

      ScheduleDO formDO = null;
      Integer theKey = null;
//--------------------------------------------------------------------------------
// Replicate an existing schedule record.  In event schedule the 
// user will be offered the option to copy the work order line items
// from the orginal order.  This is done by setting the request attribute
// "orgsKey" and adding logic to the scheduleedit.jsp to evaluate.
//--------------------------------------------------------------------------------
      if (request.getParameter("key") != null) {
        theKey = new Integer(request.getParameter("key"));
        if (request.getSession().getAttribute("schedule") != null)
          formDO = populateFormFromEvent(request);
        request.setAttribute("orgsKey", theKey.toString());        
        cat.debug(this.getClass().getName() + ": : orgsKey Attribute..:  " + theKey);          
      }
      else {
//--------------------------------------------------------------------------------
// Posting a pet vaccination reminder
//--------------------------------------------------------------------------------
        if (request.getParameter("pvKey") != null) {
          if (request.getSession().getAttribute("petvac") != null)
            formDO = populateFormFromPetVac(request);
        }
        else {
//--------------------------------------------------------------------------------
// Posting a pet boarding appointment
//--------------------------------------------------------------------------------
          if (request.getParameter("pbKey") != null) {
            if (request.getSession().getAttribute("petboard") != null)
              formDO = populateFormFromPetBoard(request);
          }
          else {
//--------------------------------------------------------------------------------
// Creating a new schedule object
//--------------------------------------------------------------------------------
            formDO = new ScheduleDO();

//--------------------------------------------------------------------------------
// Assign the date value from the calendar month to the start date field.
// Call the DateFormatter to convert the Date to a String like YYYY-MM-DD.
//--------------------------------------------------------------------------------
            if (request.getParameter("calendarDate") != null) {
              String calendarDate = request.getParameter("calendarDate");
              DateFormatter dateFormatter = new DateFormatter();
              Date myDate = (Date) dateFormatter.unformat(calendarDate);
              formDO.setStartDate(myDate);
              formDO.setEndDate(formDO.getStartDate());
            }
          }
        }
      }
      formDO.setPermissionStatus(usToken);

      try {
//--------------------------------------------------------------------------------
// Instantiate a formDO object to populate formStr.
//--------------------------------------------------------------------------------
        request.getSession().setAttribute("schedule", formDO);
        ScheduleForm formStr = new ScheduleForm();

        formStr.populate(formDO);
        form = formStr;
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