/*
 * AttachmentVerifyAction.java
 *
 * Created on January 31, 2007, 9:31 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 *
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

public class AttachmentVerifyAction extends Action {

  Logger cat = Logger.getLogger("WOWAPP");

  Integer attachCount;
  
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


  private ActionMessages verifyAttachment(HttpServletRequest request)
    throws Exception, SQLException {

    DataSource ds = null;
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    ActionMessages actionMessages = new ActionMessages();
    String errorNote = "";

    try {
      ds = getDataSource(request);
      conn = ds.getConnection();

//--------------------------------------------------------------------------------
// Retrieve the CustomerResource.properties values for "fileupload".
//--------------------------------------------------------------------------------
      String attachPath = getPropertyService().getCustomerProperties(request, "fileupload.physical.directory");
      String pathSymbol = getPropertyService().getCustomerProperties(request, "fileupload.separator.symbol");
      String fileName = "";
      File file = null;

//--------------------------------------------------------------------------------
// Define a query to select all the rows from the attachment table.
//--------------------------------------------------------------------------------
      String query = "SELECT thekey,"
                   + "sourcetype_key,"
                   + "source_key,"
                   + "file_name,"
                   + "file_type,"
                   + "file_size "
                   + "FROM attachment";
      ps = conn.prepareStatement(query);
      rs = ps.executeQuery();

      attachCount = 0;
      while ( rs.next() ) {
        ++attachCount;
//--------------------------------------------------------------------------------
// Construct the attachment path and file name to verify it's existence.
//--------------------------------------------------------------------------------
        fileName = attachPath + rs.getString("sourcetype_key")
                              + pathSymbol + rs.getString("source_key")
                              + pathSymbol + rs.getString("file_name");
        cat.debug(this.getClass().getName() + ": Checking file : " + attachPath + " of size " + rs.getString("file_size"));
        file = new File(fileName);
        if (!(file.exists())) {
          errorNote = "Missing attachment : " + fileName + " of size " + rs.getString("file_size");
          actionMessages.add(ActionMessages.GLOBAL_MESSAGE,
                             new ActionMessage("errors.database.error", errorNote));
        }
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
    return actionMessages;
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

    ActionMessages actionMessages = new ActionMessages();
    
//--------------------------------------------------------------------------------
// Does this action require the user to login.
//--------------------------------------------------------------------------------
    if ( employeesMapping.isLoginRequired() ) {

      if ( request.getSession().getAttribute("USER") == null ) {
//--------------------------------------------------------------------------------
// The user is not logged in.
//--------------------------------------------------------------------------------
        target = "login";
        actionMessages = new ActionMessages();

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
// Make the VerifyAttachment() call.
//--------------------------------------------------------------------------------
      actionMessages = verifyAttachment(request);
      if (!actionMessages.isEmpty())
        throw new Exception("Verify attachment failed"); 
      else
        request.setAttribute("attachmentCnt", attachCount);     
    }
    catch (Exception e) {
      cat.error(this.getClass().getName() + ": Exception : " + e.getMessage());      
      target = "error";      
      if (actionMessages.isEmpty())      
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