/*
 * AttachmentDeleteAction.java
 *
 * Created on September 20, 2006, 9:31 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package com.wolmerica.attachment;

//--------------------------------------------------------------------------------
// @author Richard Wolschlager
//--------------------------------------------------------------------------------
import com.wolmerica.employee.EmployeesActionMapping;
import com.wolmerica.service.property.PropertyService;
import com.wolmerica.service.property.DefaultPropertyService;
import com.wolmerica.service.userstate.UserStateService;
import com.wolmerica.service.userstate.DefaultUserStateService;

import java.io.IOException;
import java.io.File;
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

public class AttachmentDeleteAction extends Action {

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


  private void removeUploadFile(HttpServletRequest request,
                                Integer attKey)
    throws Exception, SQLException {

    DataSource ds = null;
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    ActionMessages actionMessages = new ActionMessages();
    String errorNote = "";

    Byte sourceTypeKey = null;
    Integer sourceKey = null;
    String fileName = "";

    try {
      ds = getDataSource(request);
      conn = ds.getConnection();

//--------------------------------------------------------------------------------
// Prepare a query to retrive the source type, source, and file name details.
//--------------------------------------------------------------------------------
      String query = "SELECT sourcetype_key,"
                   + "source_key,"
                   + "file_name "
                   + "FROM attachment "
                   + "WHERE thekey = ?";
      ps = conn.prepareStatement(query);
      ps.setInt(1, attKey);
      rs = ps.executeQuery();

      if ( rs.next() ) {
        sourceTypeKey = rs.getByte("sourcetype_key");
        sourceKey = rs.getInt("source_key");
        fileName = rs.getString("file_name");
      }
      else {
        throw new Exception("Attachment  " + attKey.toString() + " not found!");
      }

      try {
//--------------------------------------------------------------------------------
// Save the uploaded file in the specified directory on the server.
// One sub-directory per source type key and one per source key.
//--------------------------------------------------------------------------------
        String savePath = getPropertyService().getCustomerProperties(request, "fileupload.physical.directory");
        String pathSymbol = getPropertyService().getCustomerProperties(request, "fileupload.separator.symbol");
        File myDir = new File(savePath);
        if (myDir.exists()) {
          savePath = savePath + sourceTypeKey + pathSymbol;
          myDir = new File(savePath);
//--------------------------------------------------------------------------------
// Check for a source type sub-directory if it does not already exist.
//--------------------------------------------------------------------------------
          if (myDir.exists()) {
            savePath = savePath + sourceKey + pathSymbol;
            myDir = new File(savePath);
//--------------------------------------------------------------------------------
// Check for a source sub-directory if it does not already exist.
//--------------------------------------------------------------------------------
            if (myDir.exists()){
              savePath = savePath + fileName;
              myDir = new File(savePath);
//--------------------------------------------------------------------------------
// Check for the existence of the file to be deleted.
//--------------------------------------------------------------------------------
              if (myDir.exists()) {
                if (myDir.delete()) {
//--------------------------------------------------------------------------------
// Now we can delete the record associated with the attachment.
//--------------------------------------------------------------------------------
                  query = "DELETE FROM attachment "
                        + "where thekey = ?";
                  ps = conn.prepareStatement(query);
                  ps.setInt(1, attKey);
                  ps.executeUpdate();
                }
                else {
                  throw new Exception("The attachment [" + fileName + "] cannot be deleted.");
                }
              }
              else {
                throw new Exception("The attachment [" + fileName + "] does not exist.");
              }
            }
            else {
              throw new Exception("The source sub-directory [" + sourceKey + "] does not exist.");
            }
          }
          else {
            throw new Exception("The source type sub-directory [" + sourceTypeKey + "] does not exist.");
          }
        }
        else {
          throw new Exception("The upload base directory [" + savePath + "] does not exist.");
        }
      }
      catch (Exception e) {
        cat.error(this.getClass().getName() + ": Exception : " + e.getMessage());
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
        throw new Exception("Request getParameter [sourceTypeKey] not found!");
      }

//--------------------------------------------------------------------------------
// The source key of the attachment owner is required.
//--------------------------------------------------------------------------------
      Integer sourceKey = null;
      if (!(request.getParameter("sourceKey") == null)) {
        sourceKey = new Integer(request.getParameter("sourceKey"));
      }
      else {
        throw new Exception("Request getParameter/getAttribute [sourceKey] not found!");
      }

//--------------------------------------------------------------------------------
// The source name of the attachment owner is required.
//--------------------------------------------------------------------------------
      String sourceName = null;
      if (request.getParameter("sourceName") != null) {
        sourceName = new String(request.getParameter("sourceName"));
      }
      else {
        throw new Exception("Request getParameter [sourceName] not found!");
      }

//--------------------------------------------------------------------------------
// 07-23-06  Code to handle permissions and avoid simultaneous updates to records.
//--------------------------------------------------------------------------------
      String usToken = getUserStateService().getUserToken(request,
                                              this.getDataSource(request).getConnection(),
                                              this.getClass().getName(),
                                              theKey);
      if (usToken.equalsIgnoreCase(getUserStateService().getLocked()))
        removeUploadFile(request, theKey);
      else if (usToken.equalsIgnoreCase(getUserStateService().getProhibited()))
        throw new Exception(getUserStateService().getAccessDenied());
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
