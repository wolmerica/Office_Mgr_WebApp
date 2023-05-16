/*
 * PetVacListAction.java
 *
 * Created on May 07, 2007  08:40 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package com.wolmerica.petvac;

//--------------------------------------------------------------------------------
// @author Richard Wolschlager
//--------------------------------------------------------------------------------
import com.wolmerica.employee.EmployeesActionMapping;
import com.wolmerica.permission.PermissionListDO;
import com.wolmerica.tools.formatter.FormattingException;
import com.wolmerica.tools.formatter.DateFormatter;
import com.wolmerica.service.attributeto.AttributeToService;
import com.wolmerica.service.attributeto.DefaultAttributeToService;
import com.wolmerica.service.customer.CustomerService;
import com.wolmerica.service.customer.DefaultCustomerService;
import com.wolmerica.service.property.PropertyService; 
import com.wolmerica.service.property.DefaultPropertyService; 
import com.wolmerica.service.daterange.DateRangeService;
import com.wolmerica.service.daterange.DefaultDateRangeService;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.apache.log4j.Logger;

public class PetVacListAction extends Action {

  Logger cat = Logger.getLogger("WOWAPP");

  private AttributeToService attributeToService = new DefaultAttributeToService();
  private CustomerService CustomerService = new DefaultCustomerService();
  private DateRangeService dateRangeService = new DefaultDateRangeService();
  private PropertyService propertyService = new DefaultPropertyService();
  private UserStateService userStateService = new DefaultUserStateService();

  public AttributeToService getAttributeToService() {
      return attributeToService;
  }

  public void setAttributeToService(AttributeToService attributeToService) {
      this.attributeToService = attributeToService;
  }

  public CustomerService getCustomerService() {
      return CustomerService;
  }

  public void setCustomerService(CustomerService CustomerService) {
      this.CustomerService = CustomerService;
  }

  public DateRangeService getDateRangeService() {
      return dateRangeService;
  }

  public void setDateRangeService(DateRangeService dateRangeService) {
      this.dateRangeService = dateRangeService;
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


  private PetVacListHeadDO getPetVacList(HttpServletRequest request,
                                         Integer cKey,
                                         Integer pKey,
                                         String fromDate,
                                         String toDate,
                                         Integer pageNo,
                                         Byte sourceTypeKey)
    throws Exception, SQLException {

    PetVacListHeadDO formHDO = new PetVacListHeadDO();
    PetVacListDO petVacRow = null;
    ArrayList<PetVacListDO> petVacRows = new ArrayList<PetVacListDO>();
    
    ArrayList<PermissionListDO> permissionRows = new ArrayList<PermissionListDO>();
    HashMap nameMap;

    DataSource ds = null;
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    try {
      ds = getDataSource(request);
      conn = ds.getConnection();

//--------------------------------------------------------------------------------
// Call the DateFormatter to convert the Date to a String like YYYY-MM-DD.
//--------------------------------------------------------------------------------
      DateFormatter dateFormatter = new DateFormatter();
      Date myDate = (Date) dateFormatter.unformat(fromDate);
      formHDO.setFromDate(myDate);
      myDate = (Date) dateFormatter.unformat(toDate);
      formHDO.setToDate(myDate);

      String query = "SELECT petvaccination.thekey,"
                   + "schedule.customer_key,"
                   + "customer.client_name,"
                   + "pet.thekey AS pet_key,"
                   + "DATE(start_stamp) AS vac_date,"
                   + "petvaccination.rabies_id,"
                   + "petvaccination.vac_expiration_date,"
                   + "petvaccination.canine_distemper_id,"
                   + "petvaccination.corunna_id,"
                   + "petvaccination.feline_distemper_id,"
                   + "petvaccination.feline_leukemia_id,"
                   + "petvaccination.other_id "
                   + "FROM customer, pet, petvaccination, schedule "
                   + "WHERE petvaccination.schedule_key = schedule.thekey "
                   + "AND schedule.source_key = pet.thekey "
                   + "AND schedule.customer_key = customer.thekey "
                   + "AND DATE(schedule.start_stamp) BETWEEN ? AND ?";
      if (pKey != null)
        query = query + " AND pet.thekey = " + pKey;
      if (cKey != null)
        query = query + " AND customer.thekey = " + cKey;
      query = query + " ORDER BY vac_date DESC, customer.client_name, pet.name";
      ps = conn.prepareStatement(query);
      ps.setDate(1, new java.sql.Date(formHDO.getFromDate().getTime()));
      ps.setDate(2, new java.sql.Date(formHDO.getToDate().getTime()));
      rs = ps.executeQuery();

//--------------------------------------------------------------------------------
// Define a permissionRow to evaluate the deleteId before checking dependency.
//--------------------------------------------------------------------------------
      PermissionListDO permissionRow = null;

      Integer pageMax = new Integer(getPropertyService().getCustomerProperties(request,"pet.list.size"));
      Integer firstRecord = ((pageNo.intValue() - 1) * pageMax) + 1;
      Integer lastRecord = firstRecord + (pageMax - 1);

      Integer recordCount = 0;
      while ( rs.next() ) {
        ++recordCount;
        if ((recordCount >= firstRecord) && (recordCount <= lastRecord)) {
          petVacRow = new PetVacListDO();

          petVacRow.setKey(rs.getInt("thekey"));
          petVacRow.setClientKey(rs.getInt("customer_key"));
          petVacRow.setClientName(rs.getString("client_name"));
          petVacRow.setPetKey(rs.getInt("pet_key"));
          petVacRow.setVacDate(rs.getDate("vac_date"));
          petVacRow.setRabiesId(rs.getBoolean("rabies_id"));
          petVacRow.setVacExpirationDate(rs.getDate("vac_expiration_date"));
          petVacRow.setCanineDistemperId(rs.getBoolean("canine_distemper_id"));
          petVacRow.setCorunnaId(rs.getBoolean("corunna_id"));
          petVacRow.setFelineDistemperId(rs.getBoolean("feline_distemper_id"));
          petVacRow.setFelineLeukemiaId(rs.getBoolean("feline_leukemia_id"));
          petVacRow.setOtherId(rs.getBoolean("other_id"));

          nameMap = getAttributeToService().getAttributeToName(conn, new Byte("4"), petVacRow.getPetKey());
          petVacRow.setPetName(nameMap.get("attributeToName").toString());

//--------------------------------------------------------------------------------
// 07-31-06  Define the permission information in separate class for re-usability.
//--------------------------------------------------------------------------------
          permissionRow = getUserStateService().getUserListToken(request,conn,
                             this.getClass().getName(),petVacRow.getKey());
          permissionRows.add(permissionRow);

          if (permissionRow.getDeleteId()) {
//--------------------------------------------------------------------------------
// 10-30-06  Check if the pet has any dependency on linked to tables.
//--------------------------------------------------------------------------------
            petVacRow.setAllowDeleteId(true);
          }

          petVacRows.add(petVacRow);
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
      if (pKey != null) {
        formHDO.setPetKey(pKey);
        nameMap = getAttributeToService().getAttributeToName(conn,
                                         sourceTypeKey,
                                         formHDO.getPetKey());
        formHDO.setPetName(nameMap.get("sourceName").toString());
        formHDO.setCustomerKey(new Integer(nameMap.get("customerKey").toString()));
        formHDO.setClientName(getCustomerService().getClientName(conn, formHDO.getCustomerKey()));
      }
      if (cKey != null) {
        formHDO.setCustomerKey(cKey);
        formHDO.setClientName(getCustomerService().getClientName(conn, cKey));
      }
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
      if (petVacRows.isEmpty())
        petVacRows.add(new PetVacListDO());
      if (permissionRows.isEmpty())
        permissionRows.add(getUserStateService().getUserListToken(request,conn,
                           this.getClass().getName(),getUserStateService().getNoKey()));

      formHDO.setPetVacListForm(petVacRows);
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
      Integer cKey = null;
      if (request.getParameter("customerKey") != null) {
        cKey = new Integer(request.getParameter("customerKey"));
      }

      Integer pKey = null;
      if (request.getParameter("petKey") != null) {
        pKey = new Integer(request.getParameter("petKey"));
      }

//--------------------------------------------------------------------------------
// The user can only select the the day.  The hour, minute, second, etc
// values are set to zero for the "fromDate" and set to the end of the
// day for the "toDate".
//--------------------------------------------------------------------------------
      String fromDate = getDateRangeService().getDateToString(getDateRangeService().getBACKFromDate(new Integer(getPropertyService().getCustomerProperties(request,"petvac.days.back")).intValue()));
      String toDate = getDateRangeService().getDateToString(getDateRangeService().getFWDToDate(new Integer(getPropertyService().getCustomerProperties(request,"petvac.days.back")).intValue()));

      if (request.getParameter("fromDate") != null) {
        if (request.getParameter("fromDate").length() > 0 ) {
          fromDate = request.getParameter("fromDate");
        }
      }

      if (request.getParameter("toDate") != null) {
        if (request.getParameter("toDate").length() > 0 ) {
          toDate = request.getParameter("toDate");
        }
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

      try {
//--------------------------------------------------------------------------------
// Instantiate a formDO object to populate formStr.
//--------------------------------------------------------------------------------
        PetVacListHeadDO formHDO = getPetVacList(request,
                                                 cKey,
                                                 pKey,
                                                 fromDate,
                                                 toDate,
                                                 pageNo,
                                                 getUserStateService().getFeatureKey().byteValue());
        request.getSession().setAttribute("petVacListHDO", formHDO);
//--------------------------------------------------------------------------------
// Create the wrapper object for system list.
//--------------------------------------------------------------------------------
        PetVacListHeadForm formHStr = new PetVacListHeadForm();
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
