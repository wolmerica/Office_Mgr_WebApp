/*
 * LicenseOptionAction.java
 *
 * Created on April 12, 2010  11:25 AM
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
import com.wolmerica.service.vendor.VendorService;
import com.wolmerica.service.vendor.DefaultVendorService;
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
import java.util.HashMap;

import org.apache.log4j.Logger;

public class LicenseOptionAction extends Action {

  Logger cat = Logger.getLogger("WOWAPP");

  private PropertyService propertyService = new DefaultPropertyService();
  private UserStateService userStateService = new DefaultUserStateService();
  private VendorService VendorService = new DefaultVendorService();

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

  public VendorService getVendorService() {
      return VendorService;
  }

  public void setVendorService(VendorService VendorService) {
      this.VendorService = VendorService;
  }

  private LicenseListHeadDO optionLicense(HttpServletRequest request,
                                          Byte invoiceTypeKey,
                                          Integer invoiceKey,
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
// Look up the license assigned count.
//--------------------------------------------------------------------------------
      HashMap licenseMap = getVendorService().getLicenseAssignedCount(conn,
                                                      invoiceTypeKey,
                                                      invoiceKey);
      Integer licenseCnt = new Integer(licenseMap.get("licenseCnt").toString());
      Integer orderQty = new Integer(licenseMap.get("orderQty").toString());
      cat.debug(" licenseCnt = " + licenseCnt);
      cat.debug(" orderQty   = " + orderQty);
//--------------------------------------------------------------------------------
// Get the assigned and available license keys
//--------------------------------------------------------------------------------
      String query = "SELECT thekey,"
                   + "sourcetype_key,"
                   + "source_key,"
                   + "license_user,"
                   + "license_key,"
                   + "note_line1,"
                   + "invoicetype_key,"
                   + "invoice_key,"
                   + "create_user,"
                   + "create_stamp,"
                   + "update_user,"
                   + "update_stamp "
                   + "FROM license "
                   + "WHERE ((invoicetype_key = ? "
                   + "AND invoice_key = ? ) "
                   + "OR (sourcetype_key = 13 "
                   + "AND source_key IN (SELECT vendorinvoiceitem_key "
                                      + "FROM customerinvoiceitem "
                                      + "WHERE master_key = ?) "
                   + "AND invoice_key IS NULL)) "
                   + "ORDER by invoice_key DESC, thekey";
      ps = conn.prepareStatement(query);
      ps.setByte(1, invoiceTypeKey);
      ps.setInt(2, invoiceKey);
      ps.setInt(3, invoiceKey);
      rs = ps.executeQuery();

      Integer pageMax = new Integer(getPropertyService().getCustomerProperties(request,"license.list.size"));
      Integer firstRecord = ((pageNo.intValue() - 1) * pageMax) + 1;
      Integer lastRecord = firstRecord + (pageMax - 1);
      cat.debug(this.getClass().getName() + ": invoiceTypeKey.: " + invoiceTypeKey);
      cat.debug(this.getClass().getName() + ": invoiceKey.....: " + invoiceKey);

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
          license.setInvoiceTypeKey(rs.getByte("invoicetype_key"));
          license.setInvoiceKey(rs.getInt("invoice_key"));
          license.setCreateUser(rs.getString("create_user"));
          license.setCreateStamp(rs.getTimestamp("create_stamp"));
          license.setUpdateUser(rs.getString("update_user"));
          license.setUpdateStamp(rs.getTimestamp("update_stamp"));

          // Set the release indicator to true if the license is assigned.
          cat.debug(" getInvoiceKey() = [" + license.getInvoiceKey() + "]");
          cat.debug(" invoiceKey      = [" + invoiceKey + "]");
          if (license.getInvoiceKey().compareTo(invoiceKey) == 0) {
            cat.debug(" setReleaseId() to true");
            license.setReleaseId(true);
          } else {
          // Set the assign indicator to true if more license keys are needed.
            if (licenseCnt < orderQty) {
              cat.debug(" setAssignId() true");
              license.setAssignId(true);
            }
          }

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
      if (recordCount == 0) {
        firstRecord=0;
        license = new LicenseDO();
        licenses.add(license);
      }

//--------------------------------------------------------------------------------
// Store the filter, row count, previous and next page number values.
//--------------------------------------------------------------------------------
      formHDO.setInvoiceTypeKey(invoiceTypeKey);
      formHDO.setInvoiceKey(invoiceKey);
      formHDO.setRecordCount(recordCount);
      formHDO.setFirstRecord(firstRecord);
      formHDO.setLastRecord(lastRecord);
      formHDO.setPreviousPage(prevPage);
      formHDO.setNextPage(nextPage);

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
// The invoice type key of the attachment owner is required.
//--------------------------------------------------------------------------------
      Byte invoiceTypeKey = null;
      if (request.getParameter("invoiceTypeKey") != null) {
        invoiceTypeKey = new Byte(request.getParameter("invoiceTypeKey"));
      }
      else {
        if (request.getAttribute("invoiceTypeKey") != null) {
          invoiceTypeKey = new Byte(request.getAttribute("invoiceTypeKey").toString());
        }
        else {
          throw new Exception("Request getParameter/getAttribute [invoiceTypeKey] not found!");
        }
      }

//--------------------------------------------------------------------------------
// The invoice key of the attachment owner is required.
//--------------------------------------------------------------------------------
      Integer invoiceKey = null;
      if (request.getParameter("invoiceKey") != null) {
        invoiceKey = new Integer(request.getParameter("invoiceKey"));
      }
      else {
        if (request.getAttribute("invoiceKey") != null) {
          invoiceKey = new Integer(request.getAttribute("invoiceKey").toString());
        }
        else {
          throw new Exception("Request getParameter/getAttribute [invoiceKey] not found!");
        }
      }

//--------------------------------------------------------------------------------
// The invoice name for which the license belong too.
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
        LicenseListHeadDO formHDO = optionLicense(request,
                                                  invoiceTypeKey,
                                                  invoiceKey,
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
