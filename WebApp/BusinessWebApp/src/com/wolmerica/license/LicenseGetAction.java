/*
 * LicenseGetAction.java
 *
 * Created on April 9, 2010  1:30 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 *
 */

package com.wolmerica.license;

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

public class LicenseGetAction extends Action {

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


  private LicenseListHeadDO getLicense(HttpServletRequest request,
                                           Byte sourceTypeKey,
                                           Integer sourceKey,
                                           Integer pageNo)
   throws Exception, SQLException {

    LicenseListHeadDO formHDO = new LicenseListHeadDO();
    LicenseDO license = null;
    ArrayList<LicenseDO> licenses = new ArrayList<LicenseDO>();

    DataSource ds = null;
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    try {
      ds = getDataSource(request);
      conn = ds.getConnection();

//--------------------------------------------------------------------------------
// Get the work order records related to the schedule.
//--------------------------------------------------------------------------------
      String query = "SELECT thekey,"
                   + "sourcetype_key,"
                   + "source_key,"
                   + "license_user,"
                   + "license_key,"
                   + "note_line1,"
                   + "create_user,"
                   + "create_stamp,"
                   + "update_user,"
                   + "update_stamp "
                   + "FROM license "
                   + "WHERE sourcetype_key = ? "
                   + "AND source_key = ? "
                   + "ORDER by thekey";
      ps = conn.prepareStatement(query);
      ps.setByte(1, sourceTypeKey);
      ps.setInt(2, sourceKey);
      rs = ps.executeQuery();

      Integer pageMax = new Integer(getPropertyService().getCustomerProperties(request,"license.list.size"));
      Integer firstRecord = ((pageNo.intValue() - 1) * pageMax) + 1;
      Integer lastRecord = firstRecord + (pageMax - 1);
      cat.debug(this.getClass().getName() + ": sourceTypeKey.: " + sourceTypeKey);
      cat.debug(this.getClass().getName() + ": sourceKey.....: " + sourceKey);

      Integer recordCount = 0;
      while ( rs.next() ) {
        ++recordCount;
        if ((recordCount >= firstRecord) && (recordCount <= lastRecord)) {
          license = new LicenseDO();

          license.setKey(rs.getInt("thekey"));
          license.setSourceTypeKey(rs.getByte("sourcetype_key"));
          license.setSourceKey(rs.getInt("source_key"));
          license.setLicenseUser(rs.getString("license_user"));
          license.setLicenseKey(rs.getString("license_key"));
          license.setNoteLine1(rs.getString("note_line1"));
          license.setCreateUser(rs.getString("create_user"));
          license.setCreateStamp(rs.getTimestamp("create_stamp"));
          license.setUpdateUser(rs.getString("update_user"));
          license.setUpdateStamp(rs.getTimestamp("update_stamp"));

          licenses.add(license);
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
      formHDO.setSourceTypeKey(sourceTypeKey);
      formHDO.setSourceKey(sourceKey);
      formHDO.setRecordCount(recordCount);
      formHDO.setFirstRecord(firstRecord);
      formHDO.setLastRecord(lastRecord);
      formHDO.setPreviousPage(prevPage);
      formHDO.setNextPage(nextPage);
//--------------------------------------------------------------------------------
// A formatter issues exists during the population of empty lists.
// A work around is to populate one row when there is an emptyList.
// The additional row will be used to enter a new tracking number.
//--------------------------------------------------------------------------------
      license = new LicenseDO();
      license.setSourceTypeKey(sourceTypeKey);
      license.setSourceKey(sourceKey);
      license.setLicenseUser(getPropertyService().getCustomerProperties(request,"license.default.user"));
      license.setLicenseKey(getPropertyService().getCustomerProperties(request,"license.default.key"));

      licenses.add(license);

      formHDO.setLicenseForm(licenses);
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
//--------------------------------------------------------------------------------
// The source type key of the attachment owner is required.
//--------------------------------------------------------------------------------
      Byte sourceTypeKey = null;
      if (request.getParameter("sourceTypeKey") != null) {
        sourceTypeKey = new Byte(request.getParameter("sourceTypeKey"));
      }
      else {
        if (request.getAttribute("sourceTypeKey") != null) {
          sourceTypeKey = new Byte(request.getAttribute("sourceTypeKey").toString());
        }
        else {
          throw new Exception("Request getParameter/getAttribute [sourceTypeKey] not found!");
        }
      }

//--------------------------------------------------------------------------------
// The source key of the attachment owner is required.
//--------------------------------------------------------------------------------
      Integer sourceKey = null;
      if (request.getParameter("sourceKey") != null) {
        sourceKey = new Integer(request.getParameter("sourceKey"));
      }
      else {
        if (request.getAttribute("sourceKey") != null) {
          sourceKey = new Integer(request.getAttribute("sourceKey").toString());
        }
        else {
          throw new Exception("Request getParameter/getAttribute [sourceKey] not found!");
        }
      }

//--------------------------------------------------------------------------------
// The source name for which the license belong too.
//--------------------------------------------------------------------------------
      String sourceName = null;
      if (request.getParameter("sourceName") != null) {
        sourceName = new String(request.getParameter("sourceName"));
      }
      else {
        throw new Exception("Request getParameter [sourceName] not found!");
      }

//--------------------------------------------------------------------------------
// Get the page number value.
//--------------------------------------------------------------------------------
      Integer pageNo = new Integer("1");
      if (request.getParameter("pageNo") != null) {
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
        LicenseListHeadDO formHDO = getLicense(request,
                                                   sourceTypeKey,
                                                   sourceKey,
                                                   pageNo);
        formHDO.setPermissionStatus(usToken);
        formHDO.setSourceName(sourceName);
        request.getSession().setAttribute("licenseHDO", formHDO);
//--------------------------------------------------------------------------------
// Create the wrapper object the license.
//--------------------------------------------------------------------------------
        LicenseListHeadForm formHStr = new LicenseListHeadForm();
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
