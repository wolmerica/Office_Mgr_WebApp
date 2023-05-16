/*
 * HelpOperationListAction.java
 *
 * Created on April 09, 2006, 06:59 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package com.wolmerica.help;

//--------------------------------------------------------------------------------
// @author Richard Wolschlager
//--------------------------------------------------------------------------------
import com.wolmerica.employee.EmployeesActionMapping;
import com.wolmerica.service.attachment.AttachmentService;
import com.wolmerica.service.attachment.DefaultAttachmentService;
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
import java.util.HashMap;
import java.util.ArrayList;

import org.apache.log4j.Logger;

public class HelpOperationListAction extends Action {

  Logger cat = Logger.getLogger("WOWAPP");
  

  private AttachmentService attachmentService = new DefaultAttachmentService();
  private PropertyService propertyService = new DefaultPropertyService();
  private UserStateService userStateService = new DefaultUserStateService();

  public AttachmentService getAttachmentService() {
      return attachmentService;
  }

  public void setAttachmentService(AttachmentService attachmentService) {
      this.attachmentService = attachmentService;
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


  private HelpHeadDO getHelpOperationList(HttpServletRequest request,
                                          Integer packKey)
    throws Exception, SQLException {

    HelpHeadDO formHDO = new HelpHeadDO();
    HelpDO helpRow = null;
    ArrayList<HelpDO> helpRows = new ArrayList<HelpDO>();
    HashMap attachMap;
    
    Byte helpFeatureKey = new Byte("0");
    String documentServerURL = getPropertyService().getCustomerProperties(request, "fileupload.virtual.directory");

    DataSource ds = null;
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    try {
      ds = getDataSource(request);
      conn = ds.getConnection();

      String query = "SELECT thekey, "
                   + "operation "
                   + "FROM helpoperation "
                   + "WHERE package_key = ?";
      ps = conn.prepareStatement(query);
      ps.setInt(1, packKey);
      rs = ps.executeQuery();

      Integer recordCount = 0;
      while ( rs.next() ) {
        ++recordCount;
        helpRow = new HelpDO();

        helpRow.setKey(rs.getInt("thekey"));
        helpRow.setStep(recordCount.byteValue());
        helpRow.setDescription(rs.getString("operation"));
        
        cat.debug(this.getClass().getName() + ": key = " + helpRow.getKey());
//--------------------------------------------------------------------------------
// Get the count of attachments associated with this Help Operation.
//--------------------------------------------------------------------------------
        attachMap = getAttachmentService().doHelpAttachment(conn,
                                           helpFeatureKey,
                                           helpRow.getKey());
        helpRow.setAttachmentCount((Integer)attachMap.get("attachmentCnt"));
        cat.debug(this.getClass().getName() + ": attachment = " + helpRow.getAttachmentCount());
        
        helpRow.setDocumentServerURL(documentServerURL);
//--------------------------------------------------------------------------------
// Get name of the "html" file that discribes this Help Operation.
//--------------------------------------------------------------------------------
        helpRow.setHtmlFileName((String)attachMap.get("htmlFileName"));
        cat.debug(this.getClass().getName() + ": htmlFileName = " + helpRow.getHtmlFileName());
//--------------------------------------------------------------------------------
// Get name of the "avi" file that displays this Help Operation.
//--------------------------------------------------------------------------------
        helpRow.setVideoFileName((String)attachMap.get("videoFileName"));
        cat.debug(this.getClass().getName() + ": videoFileName = " + helpRow.getVideoFileName());
        helpRows.add(helpRow);
      }
//===========================================================================
// Store the filter, row count, previous and next page number values.
//===========================================================================
      query = "SELECT thekey, "
            + "package "
            + "FROM helppackage "
            + "WHERE thekey = ? ";

      ps = conn.prepareStatement(query);
      ps.setInt(1, packKey);
      rs = ps.executeQuery();

      if ( rs.next() ) {
        formHDO.setPackageKey(rs.getInt("thekey"));
        formHDO.setPackageName(rs.getString("package"));
      }
      else {
        throw new Exception("HelpPackage  " + packKey.toString() + " not found!");
      }
//===========================================================================
// A formatter issues exists during the populatin of empty lists.
// A work around is to populate one row when there is an emptyList.
// The cooresponding jsp also needs to check for the phantom row.
//===========================================================================
      if (helpRows.isEmpty()) {
        helpRows.add(new HelpDO());
      }
      formHDO.setLevelKey(new Byte("2"));
      formHDO.setOperationName(formHDO.getPackageName());
      formHDO.setRecordCount(recordCount);
      formHDO.setHelpForm(helpRows);
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

//--------------------------------------------------------------------------------
// New nested object coding.
//--------------------------------------------------------------------------------      
      try {
//--------------------------------------------------------------------------------
// Instantiate a formHDO object to populate formHStr.
//--------------------------------------------------------------------------------
        HelpHeadDO formHDO = getHelpOperationList(request,
                                                  theKey);
        request.getSession().setAttribute("helpHDO", formHDO);
//--------------------------------------------------------------------------------
// Create the wrapper object for Vendor list.
//--------------------------------------------------------------------------------        
        HelpHeadForm formHStr = new HelpHeadForm();
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
