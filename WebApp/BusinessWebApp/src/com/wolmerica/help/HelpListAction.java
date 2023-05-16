/*
 * HelpListAction.java
 *
 * Created on August 16, 2007, 07:45 PM
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

public class HelpListAction extends Action {

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

  
  private HelpHeadDO getHelpList(HttpServletRequest request)
    throws Exception, SQLException {

    HelpHeadDO formHDO = new HelpHeadDO();
    HelpDO helpRow = null;
    ArrayList<HelpDO> helpRows = new ArrayList<HelpDO>();
    HashMap attachMap;
    Integer packCount = null;
    Integer opsCount = null;

    Byte helpFeatureKey = new Byte("0");
    String documentServerURL = getPropertyService().getCustomerProperties(request, "fileupload.virtual.directory");

    DataSource ds = null;
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;
    ResultSet rs2 = null;

    try {
      ds = getDataSource(request);
      conn = ds.getConnection();

//--------------------------------------------------------------------------------
// Select all the help package records and order them by the key value.
//--------------------------------------------------------------------------------
      String query = "SELECT thekey,"
                   + "precedence,"
                   + "package "
                   + "FROM helppackage "
                   + "ORDER BY precedence";
      ps = conn.prepareStatement(query);
      rs = ps.executeQuery();

//--------------------------------------------------------------------------------
// Select all the help operations that are associated with a package.
//--------------------------------------------------------------------------------
      query = "SELECT thekey, "
            + "operation "
            + "FROM helpoperation "
            + "WHERE package_key = ?";
      ps = conn.prepareStatement(query);

//--------------------------------------------------------------------------------
// Traverse through all the packages and get the operations associated with them.
//--------------------------------------------------------------------------------
      packCount = 0;
      while (rs.next()) {
        ++packCount;
        helpRow = new HelpDO();

        helpRow.setKey(rs.getInt("thekey"));
        helpRow.setStep(new Byte("1"));
        helpRow.setDescription(rs.getString("package"));

        cat.debug(this.getClass().getName() + ": Package Key = " + helpRow.getKey());

        helpRows.add(helpRow);
//--------------------------------------------------------------------------------
// Get all the operations that are associated with the package.
//--------------------------------------------------------------------------------
        ps.setInt(1, helpRow.getKey());
        rs2 = ps.executeQuery();
	opsCount = 0;
        while ( rs2.next() ) {
          ++opsCount;
          helpRow = new HelpDO();

          helpRow.setKey(rs2.getInt("thekey"));
          helpRow.setStep(new Byte("2"));
          helpRow.setDescription(rs2.getString("operation"));

          cat.debug(this.getClass().getName() + ": Operation Key = " + helpRow.getKey());
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
      }
//===========================================================================
// A formatter issues exists during the populatin of empty lists.
// A work around is to populate one row when there is an emptyList.
// The cooresponding jsp also needs to check for the phantom row.
//===========================================================================
      if (helpRows.isEmpty()) {
        helpRows.add(new HelpDO());
      }
      Integer recordCount = packCount + opsCount;
      formHDO.setLevelKey(new Byte("0"));
      formHDO.setOperationName("All Support Info");
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
      if (rs2 != null) {
        try {
          rs2.close();
        }
        catch (SQLException sqle) {
          cat.error(this.getClass().getName() + ": SQLException : " + sqle.getMessage());
        }
        rs2 = null;
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

//--------------------------------------------------------------------------------
// New nested object coding.
//--------------------------------------------------------------------------------
      try {
//--------------------------------------------------------------------------------
// Instantiate a formHDO object to populate formHStr.
//--------------------------------------------------------------------------------
        HelpHeadDO formHDO = getHelpList(request);
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
