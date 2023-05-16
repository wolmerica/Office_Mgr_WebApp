/*
 * AttachmentAddAction.java
 *
 * Created on September 20, 2006, 9:00 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 *
 * 01/31/2007 - Make sure the file name length does not exceed the 40 character limit.
 */

package com.wolmerica.attachment;

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

import java.io.IOException;
import java.io.File;
import java.io.RandomAccessFile;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.upload.FormFile;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

import org.apache.log4j.Logger;

public class AttachmentAddAction extends Action {

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


  private ActionMessages saveUploadFile(HttpServletRequest request,
                                        ActionForm form)
    throws Exception, SQLException {

    Integer attKey = null;
    ActionMessages actionMessages = new ActionMessages();
    String errorNote = "";

    try {
//--------------------------------------------------------------------------------
// Process the FormFile
//--------------------------------------------------------------------------------
      FileUploadForm myForm = (FileUploadForm) form;
      FormFile uploadForm = myForm.getTheFile();

//--------------------------------------------------------------------------------
// Output the details about the uploaded file.
//--------------------------------------------------------------------------------
      cat.debug(this.getClass().getName() + ": File Name....: " + uploadForm.getFileName());
      cat.debug(this.getClass().getName() + ": Content Type.: " + uploadForm.getContentType());
      cat.debug(this.getClass().getName() + ": File Size....: " + uploadForm.getFileSize());

      Integer maxFileNameLength = new Integer(getPropertyService().getCustomerProperties(request, "fileupload.filename.length"));
      if (uploadForm.getFileName().length() <= maxFileNameLength) {
//--------------------------------------------------------------------------------
// Check if the content type is listed in the CustomerResources.properties.
// getCustomerProperties() returns what was passed in when a value is not found.
//--------------------------------------------------------------------------------
        String contentProperty = getPropertyService().getCustomerProperties(request, uploadForm.getContentType());
        cat.debug(this.getClass().getName() + ": getCustomerProperties.: " + contentProperty);
        if (!(contentProperty.equals(uploadForm.getContentType()))) {
          Integer maxFileSize = new Integer(contentProperty);

//--------------------------------------------------------------------------------
// Check if the size of the uploaded data is below the maximum threshold.
//--------------------------------------------------------------------------------
          if (uploadForm.getFileSize() <= maxFileSize.intValue()) {

//--------------------------------------------------------------------------------
// Save the uploaded file in the specified directory on the server.
// One sub-directory per source type key and one per source key.
//--------------------------------------------------------------------------------
            String savePath = getPropertyService().getCustomerProperties(request, "fileupload.physical.directory");
            String pathSymbol = getPropertyService().getCustomerProperties(request, "fileupload.separator.symbol");
            File myDir = new File(savePath);
            if (myDir.exists()) {
              savePath = savePath + myForm.getSourceTypeKey() + pathSymbol;
              myDir = new File(savePath);
//--------------------------------------------------------------------------------
// Create a source type sub-directory if it does not already exist.
//--------------------------------------------------------------------------------
              if (!(myDir.exists()))
                if (!(myDir.mkdir())) {
                  errorNote = "Unable to create source type directory: " + savePath + ".";
                  actionMessages.add(ActionMessages.GLOBAL_MESSAGE,
                    new ActionMessage("errors.database.error", errorNote));
                  throw new Exception(errorNote);
                }
              savePath = savePath + myForm.getSourceKey() + pathSymbol;
              myDir = new File(savePath);
//--------------------------------------------------------------------------------
// Create a source sub-directory if it does not already exist.
//--------------------------------------------------------------------------------
              if (!(myDir.exists()))
                if (!(myDir.mkdir())) {
                  errorNote = "Unable to create source directory: " + savePath + ".";
                  actionMessages.add(ActionMessages.GLOBAL_MESSAGE,
                    new ActionMessage("errors.database.error", errorNote));
                  throw new Exception(errorNote);
              }

//--------------------------------------------------------------------------------
// Make sure the proper directory structure exists to save the uploaded file.
//--------------------------------------------------------------------------------
              if (myDir.exists()) {
                savePath = savePath + uploadForm.getFileName();
                myDir = new File(savePath);
//--------------------------------------------------------------------------------
// Do not allow a duplicate file name to be uploaded to a sub-directory.
//--------------------------------------------------------------------------------
                if (!(myDir.exists())) {
                  cat.debug(this.getClass().getName() + ": Upload Directory.: " + savePath);
                  RandomAccessFile f = new RandomAccessFile(savePath, "rw");
                  f.write(uploadForm.getFileData(), 0, uploadForm.getFileData().length);
                  f.close();
                }
                else {
                  errorNote = "The upload file [" + uploadForm.getFileName() + "] already exists for this entity.";
                  actionMessages.add(ActionMessages.GLOBAL_MESSAGE,
                    new ActionMessage("errors.database.error", errorNote));
                  throw new Exception(errorNote);
                }
              }
              else {
                errorNote = "The upload full directory does not exist: " + savePath + ".";
                actionMessages.add(ActionMessages.GLOBAL_MESSAGE,
                  new ActionMessage("errors.database.error", errorNote));
                throw new Exception(errorNote);
              }
            }
            else {
              errorNote = "The upload base directory does not exist: " + savePath + ".";
              actionMessages.add(ActionMessages.GLOBAL_MESSAGE,
                new ActionMessage("errors.database.error", errorNote));
              throw new Exception(errorNote);
            }
          }
          else {
            errorNote = "The upload file size exceeds maximum of [" + maxFileSize.toString() + "] bytes.";
            actionMessages.add(ActionMessages.GLOBAL_MESSAGE,
              new ActionMessage("errors.database.error", errorNote));
            throw new Exception(errorNote);
          }
        }
        else {
          errorNote = "The upload unrecognized content type [" + uploadForm.getContentType() + "].";
          actionMessages.add(ActionMessages.GLOBAL_MESSAGE,
            new ActionMessage("errors.database.error", errorNote));
          throw new Exception(errorNote);
        }
      }
      else {
        errorNote = "The upload file name exceeds maximum length of [" + maxFileNameLength.toString() + "] characters.";
        actionMessages.add(ActionMessages.GLOBAL_MESSAGE,
          new ActionMessage("errors.database.error", errorNote));
        throw new Exception(errorNote);
      }
    }
    catch (Exception c) {
      cat.error(this.getClass().getName() + ": Exception : " + c.getMessage());
    }

    return actionMessages;
  }

  private Integer insertAttachment(HttpServletRequest request,
                                   FileUploadForm myForm)
   throws Exception, SQLException {

    DataSource ds = null;
    Connection conn = null;
    Integer attachmentKey = null;
    try {
      ds = getDataSource(request);
      conn = ds.getConnection();

      FormFile uploadForm = myForm.getTheFile();
      Byte sourceTypeKey = new Byte(myForm.getSourceTypeKey());
      Integer sourceKey = new Integer(myForm.getSourceKey());
      String fileName = uploadForm.getFileName();
      String contentType = uploadForm.getContentType();
      Integer fileSize = uploadForm.getFileSize();
      String userName = request.getSession().getAttribute("USERNAME").toString();

      attachmentKey = getAttachmentService().insertAttachmentRow(conn,
                                                                 sourceTypeKey,
                                                                 sourceKey,
                                                                 fileName,
                                                                 contentType,
                                                                 fileSize,
                                                                 userName);
    }
    catch (SQLException e) {
      cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
    }
    finally {
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
    return attachmentKey;
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
// The source type key of the attachment owner is required.
//--------------------------------------------------------------------------------
      Byte sourceTypeKey = null;
      if (request.getParameter("sourceTypeKey") != null) {
        sourceTypeKey = new Byte(request.getParameter("sourceTypeKey").toString());
      }
      else {
        throw new Exception("Request getParameter [sourceTypeKey] not found!");
      }

//--------------------------------------------------------------------------------
// The source key of the attachment owner is required.
//--------------------------------------------------------------------------------
      Integer sourceKey = null;
      if (request.getParameter("sourceKey") != null) {
        sourceKey = new Integer(request.getParameter("sourceKey"));
      }
      else {
        throw new Exception("Request getParameter/getAttribute [sourceKey] not found!");
      }

//--------------------------------------------------------------------------------
// The source name of the attachment owner is required.
//--------------------------------------------------------------------------------
      String sourceName = null;
      if (!(request.getParameter("sourceName") == null)) {
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
                                              getUserStateService().getNoKey());
      if (usToken.equalsIgnoreCase(getUserStateService().getLocked()))
        request.setAttribute(getUserStateService().getDisableEdit(), false);
      else if (usToken.equalsIgnoreCase(getUserStateService().getProhibited()))
        throw new Exception(getUserStateService().getAccessDenied());

      ActionMessages actionMessages = saveUploadFile(request, form);
//--------------------------------------------------------------------------------
// Report any ActionMessages we have discovered back to the original form
//--------------------------------------------------------------------------------
      if (actionMessages.isEmpty()) {
        FileUploadForm uploadForm = (FileUploadForm) form;
        Integer theKey = insertAttachment(request, uploadForm);
        request.setAttribute("key", theKey.toString());
        request.setAttribute("sourceTypeKey", sourceTypeKey.toString());
        request.setAttribute("sourceKey", sourceKey.toString());
        request.setAttribute("sourceName", sourceName);
      }
      else {
        target = "error";
        saveMessages(request, actionMessages);
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
