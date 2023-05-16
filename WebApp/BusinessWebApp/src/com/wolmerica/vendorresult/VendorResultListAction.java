/*
 * VendorResultListAction.java
 *
 * Created on January 21, 2010, 9:25 AM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package com.wolmerica.vendorresult;

//--------------------------------------------------------------------------------
// @author Richard Wolschlager
//--------------------------------------------------------------------------------
import com.wolmerica.employee.EmployeesActionMapping;
import com.wolmerica.permission.PermissionListDO;
import com.wolmerica.service.attachment.AttachmentService;
import com.wolmerica.service.attachment.DefaultAttachmentService;
import com.wolmerica.service.attributeto.AttributeToService;
import com.wolmerica.service.attributeto.DefaultAttributeToService;
import com.wolmerica.service.customer.CustomerService;
import com.wolmerica.service.customer.DefaultCustomerService;
import com.wolmerica.service.daterange.DateRangeService;
import com.wolmerica.service.daterange.DefaultDateRangeService;
import com.wolmerica.service.property.PropertyService;
import com.wolmerica.service.property.DefaultPropertyService;
import com.wolmerica.service.vendor.VendorService;
import com.wolmerica.service.vendor.DefaultVendorService;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;

import org.apache.log4j.Logger;

public class VendorResultListAction extends Action {

  Logger cat = Logger.getLogger("WOWAPP");
  

  private AttachmentService attachmentService = new DefaultAttachmentService();
  private AttributeToService attributeToService = new DefaultAttributeToService();
  private CustomerService CustomerService = new DefaultCustomerService();
  private DateRangeService dateRangeService = new DefaultDateRangeService();
  private PropertyService propertyService = new DefaultPropertyService();
  private UserStateService userStateService = new DefaultUserStateService();
  private VendorService VendorService = new DefaultVendorService();

  public AttachmentService getAttachmentService() {
      return attachmentService;
  }

  public void setAttachmentService(AttachmentService attachmentService) {
      this.attachmentService = attachmentService;
  }

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
  
  public VendorService getVendorService() {
      return VendorService;
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


  public void setVendorService(VendorService VendorService) {
      this.VendorService = VendorService;
  }

  private VendorResultListHeadDO getVendorResultList(HttpServletRequest request,
                                                     Integer vKey,
                                                     Integer cKey,
                                                     Byte stKey,
                                                     Integer sKey,
                                                     String fromDate,
                                                     String toDate,
                                                     Boolean testDisplayType,
                                                     Integer pageNo)
    throws Exception, FormattingException, SQLException {

    cat.debug(this.getClass().getName() + ": getVendorResultList()");
    VendorResultListHeadDO formHDO = new VendorResultListHeadDO();
    VendorResultListDO vendorResultRow = null;
    ArrayList<VendorResultListDO> vendorResultRows = new ArrayList<VendorResultListDO>();
    ArrayList<PermissionListDO> permissionRows = new ArrayList<PermissionListDO>();
    PermissionListDO permissionRow = null;

    DataSource ds = null;
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    Byte sourceTypeKey = null;
    Integer sourceKey = null;

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

      String query = "SELECT vr.thekey,"
                   + "vr.purchaseorder_key,"
                   + "vendor.name AS vendor_name,"
                   + "customer.client_name,"
                   + "customertype.attribute_to_entity,"
                   + "po.purchase_order_num,"
                   + "po.sourcetype_key,"
                   + "po.source_key,"
                   + "vr.status,"
                   + "vr.import_file,"
                   + "vr.order_status,"
                   + "vr.site_name,"
                   + "vr.receive_date,"
                   + "vr.receive_accession_id,"
                   + "vr.result_date,"
                   + "vr.result_accession_id,"
                   + "vr.profile_num,"
                   + "vr.unit_status,"
                   + "vr.unit_code,"
                   + "vr.unit_name,"
                   + "vr.abnormal_status,"
                   + "vr.test_status,"
                   + "vr.test_code,"
                   + "vr.test_name,"
                   + "vr.test_value,"
                   + "vr.test_units,"
                   + "vr.test_range,"
                   + "vr.test_comment,"
                   + "vr.error_message,"
                   + "vr.create_user,"
                   + "vr.create_stamp,"
                   + "vr.update_user,"
                   + "vr.update_stamp "
                   + "FROM vendorresult vr, purchaseorder po, "
                   + "vendor, customer, customertype "
                   + "WHERE vr.purchaseorder_key = po.thekey "
                   + "AND ((vr.result_date BETWEEN ? AND ?) "
                   + "OR (vr.result_date = '1999-12-31')) "
                   + "AND po.vendor_key = vendor.thekey "
                   + "AND po.customer_key = customer.thekey "
                   + "AND customer.customertype_key = customertype.thekey ";
      if (vKey != null) {
        query = query + "AND po.vendor_key = ? ";
      }
      if (cKey != null) {
        query = query + "AND po.customer_key = ? ";
      }
      if (sKey != null) {
        query = query + "AND po.sourcetype_key = ? "
                      + "AND po.source_key = ? ";
      }
      query = query + "ORDER BY vr.status DESC, vr.order_status,"
                    + "vr.result_date DESC, vr.thekey";

      cat.debug(this.getClass().getName() + ": Query #1 " + query);
      ps = conn.prepareStatement(query);
      ps.setDate(1, new java.sql.Date(formHDO.getFromDate().getTime()));
      ps.setDate(2, new java.sql.Date(formHDO.getToDate().getTime()));
      int nextParam = 3;
      if (vKey != null) {
        ps.setInt(nextParam++, vKey);
      }
      if (cKey != null) {
        ps.setInt(nextParam++, cKey);
      }
      if (sKey != null) {
        ps.setByte(nextParam++, stKey);
        ps.setInt(nextParam++, sKey);
      }
      rs = ps.executeQuery();

      Integer pageMax;
      pageMax = new Integer(getPropertyService().getCustomerProperties(request,"vendorresult.list.size"));

      Integer firstRecord = ((pageNo.intValue() - 1) * pageMax) + 1;
      Integer lastRecord = firstRecord + (pageMax - 1);

      Integer recordCount = 0;
      while ( rs.next() ) {
        if (!(request.getRequestURI().contains("Menu")))
          ++recordCount;
        if (((recordCount >= firstRecord) && (recordCount <= lastRecord)) || (request.getRequestURI().contains("Menu"))) {
          vendorResultRow = new VendorResultListDO();

cat.debug("client_name...: " + rs.getString("client_name"));

          vendorResultRow.setKey(rs.getInt("thekey"));
          vendorResultRow.setPurchaseOrderKey(rs.getInt("purchaseorder_key"));
          vendorResultRow.setVendorName(rs.getString("vendor_name"));
          vendorResultRow.setCustomerName(rs.getString("client_name"));
          vendorResultRow.setAttributeToEntityName(rs.getString("attribute_to_entity"));
          vendorResultRow.setPurchaseOrderNum(rs.getString("purchase_order_num"));
          sourceTypeKey = rs.getByte("sourcetype_key");
          sourceKey = rs.getInt("source_key");
          vendorResultRow.setStatus(rs.getString("status"));
          vendorResultRow.setImportFilename(rs.getString("import_file"));
          vendorResultRow.setOrderStatus(rs.getString("order_status"));
          vendorResultRow.setSiteName(rs.getString("site_name"));
          vendorResultRow.setReceiveDate(rs.getDate("receive_date"));
          vendorResultRow.setReceiveAssessionId(rs.getString("receive_accession_id"));
          vendorResultRow.setResultDate(rs.getDate("result_date"));
          vendorResultRow.setResultAssessionId(rs.getString("result_accession_id"));
          vendorResultRow.setProfileNum(rs.getString("profile_num"));
          vendorResultRow.setUnitStatus(rs.getString("unit_status"));
          vendorResultRow.setUnitCode(rs.getString("unit_code"));
          vendorResultRow.setUnitName(rs.getString("unit_name"));
          vendorResultRow.setAbnormalStatus(rs.getString("abnormal_status"));
          vendorResultRow.setTestStatus(rs.getString("test_status"));
          vendorResultRow.setTestCode(rs.getString("test_code"));
          vendorResultRow.setTestName(rs.getString("test_name"));
          vendorResultRow.setTestValue(rs.getString("test_value"));
          vendorResultRow.setTestUnits(rs.getString("test_units"));
          vendorResultRow.setTestRange(rs.getString("test_range"));
          vendorResultRow.setTestComment(rs.getString("test_comment"));
          vendorResultRow.setErrorMessage(rs.getString("error_message"));
          vendorResultRow.setCreateUser(rs.getString("create_user"));
          vendorResultRow.setCreateStamp(rs.getTimestamp("create_stamp"));
          vendorResultRow.setUpdateUser(rs.getString("update_user"));
          vendorResultRow.setUpdateStamp(rs.getTimestamp("update_stamp"));

//--------------------------------------------------------------------------------
// Look up the attribute to name with the source type and source key.
//--------------------------------------------------------------------------------
          if (sourceTypeKey != null) {
            HashMap nameMap = getAttributeToService().getAttributeToName(conn,
                                                    sourceTypeKey,
                                                    sourceKey);
            vendorResultRow.setAttributeToName(nameMap.get("attributeToName").toString());
          }

//--------------------------------------------------------------------------------
// 07-31-06  Define the permission information in separate class for re-usability.
//--------------------------------------------------------------------------------
          permissionRow = getUserStateService().getUserListToken(request,conn,
                             this.getClass().getName(),vendorResultRow.getKey());
          permissionRows.add(permissionRow);

          vendorResultRows.add(vendorResultRow);
        }
      }

//---------------------------------------------------------------------------
// Pagination logic to figure out what the previous and next page
// values will be for the next screen to be displayed.
//---------------------------------------------------------------------------
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
//---------------------------------------------------------------------------
// Store the filter, row count, previous and next page number values.
//---------------------------------------------------------------------------
      if (sKey != null) {
        formHDO.setSourceTypeKey(stKey);
        formHDO.setSourceKey(sKey);
        HashMap nameMap = getAttributeToService().getAttributeToName(conn,
                                                 formHDO.getSourceTypeKey(),
                                                 formHDO.getSourceKey());
        formHDO.setAttributeToName(nameMap.get("attributeToName").toString());
        formHDO.setCustomerKey(new Integer(nameMap.get("customerKey").toString()));

        formHDO.setClientName(getCustomerService().getClientName(conn, formHDO.getCustomerKey()));
      }
//---------------------------------------------------------------------------
// Populate the client name value when a customer is defined.
//---------------------------------------------------------------------------
      if (cKey != null) {
        formHDO.setCustomerKey(cKey);
        formHDO.setClientName(getCustomerService().getClientName(conn, cKey));
      }

//---------------------------------------------------------------------------
// Try to identify one vendor with result tracking set.
//---------------------------------------------------------------------------
      if (vKey == null) {
        vKey = getVendorService().GetVendorKeyForTrackResult(conn);
      }

//---------------------------------------------------------------------------
// Populate the vendor name and the attachments for the specific vendor.
//---------------------------------------------------------------------------
      if (vKey != null) {
        formHDO.setVendorKey(vKey);
        formHDO.setVendorName(getVendorService().getVendorName(conn, vKey));

        cat.debug(this.getClass().getName() + ": getAttachmentCount(): " + formHDO.getVendorKey());
        cat.debug(this.getClass().getName() + ": featureKey..........: " + getUserStateService().getFeatureKey());
        formHDO.setAttachmentCount(getAttachmentService().getAttachmentCount(conn,
                                                         getUserStateService().getFeatureKey().byteValue(),
                                                         formHDO.getVendorKey()));
      }
      formHDO.setTestDisplayType(testDisplayType);
      formHDO.setRecordCount(recordCount);
      formHDO.setFirstRecord(firstRecord);
      formHDO.setLastRecord(lastRecord);
      formHDO.setPreviousPage(prevPage);
      formHDO.setNextPage(nextPage);
//-----------------------------------------------------------------------------
// A formatter issues exists during the populatin of empty lists.
// A work around is to populate one row when there is an emptyList.
// The cooresponding jsp also needs to check for the phantom row.
//-----------------------------------------------------------------------------
      if (vendorResultRows.isEmpty()) {
        vendorResultRows.add(new VendorResultListDO());
        permissionRows.add(getUserStateService().getUserListToken(request,conn,
                           this.getClass().getName(),getUserStateService().getNoKey()));
      }
      formHDO.setVendorResultListForm(vendorResultRows);
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
//--------------------------------------------------------------------------------
// Traverse the request parameters and populate the appropriate values.
//--------------------------------------------------------------------------------
      String fromDate = getDateRangeService().getDateToString(getDateRangeService().getBACKFromDate(new Integer(getPropertyService().getCustomerProperties(request,"petvac.days.back")).intValue()));
      String toDate = getDateRangeService().getDateToString(getDateRangeService().getFWDToDate(new Integer(getPropertyService().getCustomerProperties(request,"petvac.days.back")).intValue()));
      Byte sourceTypeKey = null;
      Integer pageNo = new Integer("1");
      Boolean testDisplayType = false;

      cat.debug(this.getClass().getName() + ": VendorResultList params ");
      Enumeration paramNames = request.getParameterNames();
      String paramName = "";
      while (paramNames.hasMoreElements()) {
        paramName = (String)paramNames.nextElement();
        cat.debug(this.getClass().getName() + ": paramName = " + paramName);

        if (paramName.equals(new String("fromDate"))) {
          if (request.getParameter("fromDate").length() > 0 )
            fromDate = request.getParameter("fromDate");
          cat.debug(this.getClass().getName() + ": get[fromDate] = " + fromDate);
        }
        if (paramName.equals(new String("toDate"))) {
          if (request.getParameter("toDate").length() > 0 )
            toDate = request.getParameter("toDate");
          cat.debug(this.getClass().getName() + ": get[toDate] = " + toDate);
        }
        if (paramName.equals(new String("sourceTypeKey"))) {
          if (!(request.getParameter(paramName).equalsIgnoreCase("")))
            sourceTypeKey = new Byte(request.getParameter(paramName));
          cat.debug(this.getClass().getName() + ": get[sourceTypeKey] = " + sourceTypeKey.toString());
        }
        if (paramName.equals(new String("pageNo"))) {
          pageNo = new Integer(request.getParameter(paramName).toString());
          if (pageNo < 0)
            pageNo = new Integer("1");
          cat.debug(this.getClass().getName() + ": get[pageNo] = " + pageNo.toString());
        }
        if (paramName.equals(new String("testDisplayType"))) {
          testDisplayType = request.getParameter("testDisplayType").toString().equalsIgnoreCase("true");
          cat.debug(this.getClass().getName() + ": get[testDisplayType] = " + testDisplayType);
        }
      }

      Integer customerKey = null;
      if (request.getParameter("customerKey") != null) {
        if (!(request.getParameter("customerKey").equalsIgnoreCase("")))
          customerKey = new Integer(request.getParameter("customerKey"));
      }
      Integer vendorKey = null;
      if (request.getParameter("vendorKey") != null) {
        if (!(request.getParameter("vendorKey").equalsIgnoreCase(""))) {
          vendorKey = new Integer(request.getParameter("vendorKey"));
          cat.debug(this.getClass().getName() + ": getParameter[vendorKey] = " + vendorKey.toString());
        }
      }
      else {
        if (request.getAttribute("vendorKey") != null) {
          if (!(request.getAttribute("vendorKey").toString().equalsIgnoreCase(""))) {
            vendorKey = new Integer(request.getAttribute("vendorKey").toString());
            cat.debug(this.getClass().getName() + ": getAttribute[vendorKey] = " + vendorKey.toString());
          }
        }
      }

      Integer sourceKey = null;
      if (request.getParameter("sourceKey") != null) {
        if (!(request.getParameter("sourceKey").equalsIgnoreCase("")))
          sourceKey = new Integer(request.getParameter("sourceKey"));
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
        VendorResultListHeadDO formHDO = getVendorResultList(request,
                                                             vendorKey,
                                                             customerKey,
                                                             sourceTypeKey,
                                                             sourceKey,
                                                             fromDate,
                                                             toDate,
                                                             testDisplayType,
                                                             pageNo);
        request.getSession().setAttribute("vendorResultListHDO", formHDO);
//--------------------------------------------------------------------------------
// Create the wrapper object for Vendor list.
//--------------------------------------------------------------------------------
        VendorResultListHeadForm formHStr = new VendorResultListHeadForm();
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
