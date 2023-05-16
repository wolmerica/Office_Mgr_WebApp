/*
 * AttachmentGetAction.java
 *
 * Created on September 20, 2006, 9:31 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 * 12/22/2005 Implement tools.formatter library.
 */

package com.wolmerica.attachment;

//--------------------------------------------------------------------------------
// @author Richard Wolschlager
//--------------------------------------------------------------------------------
import com.wolmerica.employee.EmployeesActionMapping;
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

import org.apache.log4j.Logger;

public class AttachmentGetAction extends Action {

  Logger cat = Logger.getLogger("WOWAPP");

  private UserStateService userStateService = new DefaultUserStateService();


  public UserStateService getUserStateService() {
      return userStateService;
  }

  public void setUserStateService(UserStateService userStateService) {
      this.userStateService = userStateService;
  }

  
  private AttachmentDO buildAttachmentForm(HttpServletRequest request,
                                           Integer attKey)
   throws Exception, SQLException {

    AttachmentDO formDO = null;

    DataSource ds = null;
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    try {
      ds = getDataSource(request);
      conn = ds.getConnection();

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
                   + "WHERE thekey = ?";
      ps = conn.prepareStatement(query);
      ps.setInt(1, attKey);
      rs = ps.executeQuery();

      if ( rs.next() ) {
        formDO = new AttachmentDO();

        formDO.setKey(rs.getInt("thekey"));
        formDO.setSourceTypeKey(rs.getByte("sourcetype_key"));
        formDO.setSourceKey(rs.getInt("source_key"));
        formDO.setSubject(rs.getString("subject"));
        formDO.setAttachmentDate(rs.getDate("attachment_date"));
        formDO.setFileName(rs.getString("file_name"));
        formDO.setFileType(rs.getString("file_type"));
        formDO.setFileSize(rs.getInt("file_size"));
        formDO.setCreateUser(rs.getString("create_user"));
        formDO.setCreateStamp(rs.getTimestamp("create_stamp"));
        formDO.setUpdateUser(rs.getString("update_user"));
        formDO.setUpdateStamp(rs.getTimestamp("update_stamp"));
      }
      else {
        throw new Exception("Attachment  " + attKey.toString() + " not found!");
      }
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
// The attachment key.
//--------------------------------------------------------------------------------
      Integer theKey = null;
      if (!(request.getAttribute("key") == null)) {
        theKey = new Integer(request.getAttribute("key").toString());
      }
      else {
      if (!(request.getParameter("key") == null)) {
        theKey = new Integer(request.getParameter("key"));
      }
        else {
          throw new Exception("Request getParameter/getAttribute [key] not found!");
        }
      }
      cat.debug(this.getClass().getName() + ": get[key] = " + theKey.toString());

//--------------------------------------------------------------------------------
// The source type key of the attachment owner is required.
//--------------------------------------------------------------------------------
      Byte sourceTypeKey = null;
      if (!(request.getParameter("sourceTypeKey") == null)) {
        sourceTypeKey = new Byte(request.getParameter("sourceTypeKey").toString());
      }
      else {
        if (!(request.getAttribute("sourceTypeKey") == null)) {
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
      if (!(request.getParameter("sourceKey") == null)) {
        sourceKey = new Integer(request.getParameter("sourceKey"));
      }
      else {
        if (!(request.getAttribute("sourceKey") == null)) {
          sourceKey = new Integer(request.getAttribute("sourceKey").toString());
        }
        else {
          throw new Exception("Request getParameter/getAttribute [sourceKey] not found!");
        }
      }

//--------------------------------------------------------------------------------
// The source name of the attachment owner is required.
//--------------------------------------------------------------------------------
      String sourceName = null;
      if (!(request.getParameter("sourceName") == null)) {
        sourceName = new String(request.getParameter("sourceName"));
      }
      else {
        if (!(request.getAttribute("sourceName") == null)) {
          sourceName = request.getAttribute("sourceName").toString();
        }
        else {
          throw new Exception("Request getParameter/getAttribute [sourceName] not found!");
        }
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
// Instantiate a formDO object to populate formStr.
//--------------------------------------------------------------------------------
        AttachmentDO formDO = buildAttachmentForm(request, theKey);
        formDO.setSourceTypeKey(sourceTypeKey);
        formDO.setSourceKey(sourceKey);
        formDO.setSourceName(sourceName);
        formDO.setPermissionStatus(usToken);

        request.getSession().setAttribute("attachment", formDO);
        AttachmentForm formStr = new AttachmentForm();
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