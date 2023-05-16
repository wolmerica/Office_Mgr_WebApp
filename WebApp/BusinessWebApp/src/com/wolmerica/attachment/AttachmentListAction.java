/*
 * AttachmentListAction.java
 *
 * Created on September 20, 2006, 9:31 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 *
 * 01/31/2007 - Get the intial fromDate value from CustomerResource.properties.
 */

package com.wolmerica.attachment;

//--------------------------------------------------------------------------------
// @author Richard Wolschlager
//--------------------------------------------------------------------------------
import com.wolmerica.employee.EmployeesActionMapping;
import com.wolmerica.permission.PermissionListDO;
import com.wolmerica.service.daterange.DateRangeService;
import com.wolmerica.service.daterange.DefaultDateRangeService;
import com.wolmerica.service.property.PropertyService;
import com.wolmerica.service.property.DefaultPropertyService;
import com.wolmerica.service.userstate.UserStateService;
import com.wolmerica.service.userstate.DefaultUserStateService;
import com.wolmerica.tools.formatter.FormattingException;
import com.wolmerica.tools.formatter.DateFormatter;


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
import org.json.JSONArray;
import org.json.JSONObject;

import org.apache.log4j.Logger;

public class AttachmentListAction extends Action {

  Logger cat = Logger.getLogger("WOWAPP");

  private DateRangeService dateRangeService = new DefaultDateRangeService();
  private PropertyService propertyService = new DefaultPropertyService();
  private UserStateService userStateService = new DefaultUserStateService();

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

  
  private AttachmentListHeadDO getAttachmentList(HttpServletRequest request,
                                                 Byte sourceTypeKey,
                                                 Integer sourceKey,
                                                 String subjectFilter,
                                                 String fromDate,
                                                 String toDate,
                                                 Integer pageNo,
                                                 Boolean jsonId)                                                 
    throws Exception, SQLException {

    AttachmentListHeadDO formHDO = new AttachmentListHeadDO();
    AttachmentDO attachmentRow = null;
    ArrayList<AttachmentDO> attachmentRows = new ArrayList<AttachmentDO>();
    ArrayList<PermissionListDO> permissionRows = new ArrayList<PermissionListDO>();

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
      formHDO.setDocumentServerURL(getPropertyService().getCustomerProperties(request,"fileupload.virtual.directory"));

      String query = "SELECT thekey,"
                   + "sourcetype_key,"
                   + "source_key,"
                   + "subject,"
                   + "attachment_date,"
                   + "file_name,"
                   + "file_type,"
                   + "file_size/1024 AS file_size,"
                   + "create_user,create_stamp,"
                   + "update_user,update_stamp "
                   + "FROM attachment "
                   + "WHERE sourcetype_key = ? "
                   + "AND source_key = ? "
                   + "AND subject LIKE ? "
                   + "AND attachment_date BETWEEN ? AND ? "
                   + "ORDER BY attachment_date DESC, subject DESC";
      cat.debug(this.getClass().getName() + ": Query #1 " + query);
      ps = conn.prepareStatement(query);
      ps.setByte(1, sourceTypeKey);
      ps.setInt(2, sourceKey);
      ps.setString(3, "%" + subjectFilter.toUpperCase().trim() + "%");
      ps.setDate(4, new java.sql.Date(formHDO.getFromDate().getTime()));
      ps.setDate(5, new java.sql.Date(formHDO.getToDate().getTime()));
      rs = ps.executeQuery();
      
      Integer pageMax;
      if (!jsonId) {  
        pageMax = new Integer(getPropertyService().getCustomerProperties(request,"attachment.list.size"));
      } else {
        pageMax = new Integer(getPropertyService().getCustomerProperties(request,"json.list.size"));
      }   
      Integer firstRecord = ((pageNo.intValue() - 1) * pageMax) + 1;
      Integer lastRecord = firstRecord + (pageMax - 1);

      Integer recordCount = 0;
      while ( rs.next() ) {
        ++recordCount;
        if ((recordCount >= firstRecord) && (recordCount <= lastRecord)) {
          attachmentRow = new AttachmentDO();

          attachmentRow.setKey(rs.getInt("thekey"));
          attachmentRow.setSourceTypeKey(rs.getByte("sourcetype_key"));
          attachmentRow.setSourceKey(rs.getInt("source_key"));
          attachmentRow.setSubject(rs.getString("subject"));
          attachmentRow.setAttachmentDate(rs.getDate("attachment_date"));
          attachmentRow.setFileName(rs.getString("file_name"));
          attachmentRow.setFileType(rs.getString("file_type"));
          attachmentRow.setFileSize(rs.getInt("file_size"));
          attachmentRow.setCreateUser(rs.getString("create_user"));
          attachmentRow.setCreateStamp(rs.getTimestamp("create_stamp"));
          attachmentRow.setUpdateUser(rs.getString("update_user"));
          attachmentRow.setUpdateStamp(rs.getTimestamp("update_stamp"));

//--------------------------------------------------------------------------------
// JSON doesn't need last service, account balance, or dependency information.
//--------------------------------------------------------------------------------          
          if (!jsonId) {
          
//--------------------------------------------------------------------------------
// 07-31-06  Define the permission information in separate class for re-usability.
//--------------------------------------------------------------------------------
            permissionRows.add(getUserStateService().getUserListToken(request,conn,
                                    this.getClass().getName(),attachmentRow.getKey()));
          }

          attachmentRows.add(attachmentRow);
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
      formHDO.setSubjectFilter(subjectFilter);
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
      if (attachmentRows.isEmpty()) {
        attachmentRows.add(new AttachmentDO());
        permissionRows.add(getUserStateService().getUserListToken(request,conn,
                           this.getClass().getName(),getUserStateService().getNoKey()));
      }
      formHDO.setAttachmentForm(attachmentRows);
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

  private String getAttachmentPath(HttpServletRequest request,
                                   Byte sourceTypeKey,
                                   Integer sourceKey)
   throws Exception {

    String savePath = "";
    try {
      savePath = getPropertyService().getCustomerProperties(request, "fileupload.physical.directory");
      String pathSymbol = getPropertyService().getCustomerProperties(request, "fileupload.separator.symbol");
      savePath += sourceTypeKey + pathSymbol + sourceKey + pathSymbol;
      cat.debug(this.getClass().getName() + ": getAttachmentPath() = " + savePath);
    }
    catch (Exception e) {
      cat.error(this.getClass().getName() + ": Exception : " + e.getMessage());
    }
    return savePath;
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
      String fromDate = getPropertyService().getCustomerProperties(request,"attachment.list.fromDate");
      String toDate = getDateRangeService().getDateToString(getDateRangeService().getYTDToDate());
      Byte sourceTypeKey = null;
      Integer sourceKey = null;
      String sourceName = "";
      String subjectFilter = "";
      Integer pageNo = new Integer("1");
      Boolean jsonId = false;

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
          if (request.getParameter(paramName).toString() != null) {
            if (!(request.getParameter(paramName).toString().equalsIgnoreCase("")))
              sourceTypeKey = new Byte(request.getParameter(paramName));
          }
          cat.debug(this.getClass().getName() + ": get[sourceTypeKey] = " + sourceTypeKey.toString());
        }
        if (paramName.equals(new String("sourceKey"))) {
          if (request.getParameter(paramName).toString() != null) {
            if (!(request.getParameter(paramName).toString().equalsIgnoreCase("")))
              sourceKey = new Integer(request.getParameter(paramName));
          }
          cat.debug(this.getClass().getName() + ": get[sourceKey] = " + sourceKey.toString());
        }
        if (paramName.equals(new String("sourceName"))) {
          sourceName = request.getParameter(paramName);
          cat.debug(this.getClass().getName() + ": get[sourceName] = " + sourceName);
        }
        if (paramName.equals(new String("subjectFilter"))) {
          subjectFilter = request.getParameter(paramName);
          cat.debug(this.getClass().getName() + ": get[subjectFilter] = " + subjectFilter);
        }
        if (paramName.equals(new String("pageNo"))) {
          pageNo = new Integer(request.getParameter(paramName).toString());
          if (pageNo < 0)
            pageNo = new Integer("1");
          cat.debug(this.getClass().getName() + ": get[pageNo] = " + pageNo.toString());
        }
        if (paramName.equals(new String("json"))) {
          jsonId = new Boolean(request.getParameter("json").toString());
          cat.debug(this.getClass().getName() + ": get[json] = " + jsonId);
        }
      }

//--------------------------------------------------------------------------------
// The source type key of the attachment owner is required.
//--------------------------------------------------------------------------------
      if (sourceTypeKey == null)
        throw new Exception("Request getParameter [sourceTypeKey] not found!");

//--------------------------------------------------------------------------------
// The source key of the attachment owner is required.
//--------------------------------------------------------------------------------
      if (sourceKey == null)
        throw new Exception("Request getParameter/getAttribute [sourceKey] not found!");

//--------------------------------------------------------------------------------
// The source name of the attachment owner is required.
//--------------------------------------------------------------------------------
      if (sourceName == null) {
        throw new Exception("Request getParameter [sourceName] not found!");
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
        AttachmentListHeadDO formHDO = getAttachmentList(request,
                                                         sourceTypeKey,
                                                         sourceKey,
                                                         subjectFilter,
                                                         fromDate,
                                                         toDate,
                                                         pageNo,
                                                         jsonId);
        if (jsonId) {
          JSONArray jsonItems = new JSONArray();
          JSONObject obj = null;

          if (formHDO.getRecordCount() > 0) {
//--------------------------------------------------------------------------------
// Vendor results import requires the attachment path along with the file name.
//--------------------------------------------------------------------------------
            String idValue = "";
            String attachPath = "";
            if (sourceTypeKey == 35) {
              attachPath = getAttachmentPath(request,
                                             sourceTypeKey,
                                             sourceKey);
            }
            ArrayList myArray = formHDO.getAttachmentForm();            
            AttachmentDO myDO = new AttachmentDO();
            for (int i = 0; i < myArray.size(); i++) {
              myDO = (AttachmentDO) myArray.get(i);
              if (sourceTypeKey == 35)
                idValue = attachPath + myDO.getFileName();
              else
                idValue = myDO.getKey().toString();
              obj = new JSONObject();
              obj.put("id", idValue);
              obj.put("value", myDO.getSubject());
              obj.put("info", myDO.getFileName());
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
          formHDO.setSourceName(sourceName);
          request.getSession().setAttribute("attachmentListHDO", formHDO);
          
//--------------------------------------------------------------------------------
// Create the wrapper object for vendor invoice list.
//--------------------------------------------------------------------------------          
          AttachmentListHeadForm formHStr = new AttachmentListHeadForm();
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