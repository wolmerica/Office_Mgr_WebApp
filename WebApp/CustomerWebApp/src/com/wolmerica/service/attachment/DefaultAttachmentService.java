package com.wolmerica.service.attachment;

import com.wolmerica.service.property.PropertyService;
import com.wolmerica.service.property.DefaultPropertyService;

import javax.servlet.http.HttpServletRequest;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import java.sql.Connection;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.io.File;
import java.util.HashMap;

import org.apache.log4j.Logger;

/**
 * @author  Richard Wolschlager
 * @date    August 1, 2010
 */

public class DefaultAttachmentService implements AttachmentService {

  Logger cat = Logger.getLogger("WOWAPP");

  private PropertyService propertyService = new DefaultPropertyService();

  public PropertyService getPropertyService() {
      return propertyService;
  }

  public void setPropertyService(PropertyService propertyService) {
      this.propertyService = propertyService;
  }

  
  public Integer getAttachmentCount(Connection conn,
                                    Byte sourceTypeKey,
                                    Integer sourceKey)
   throws Exception, SQLException {

    CallableStatement cStmt = null;
    Integer attachmentCnt = null;

    try
    {
//--------------------------------------------------------------------------------
// Call a procedure with two IN parameters and seventeen OUT parameters.
//--------------------------------------------------------------------------------
      cStmt = conn.prepareCall("{call GetAttachmentCount(?,?,?)}");

//--------------------------------------------------------------------------------
// Set the value for the IN parameter
//--------------------------------------------------------------------------------
      cStmt.setByte(1, sourceTypeKey);
      cStmt.setInt(2, sourceKey);

//--------------------------------------------------------------------------------
// Execute the stored procedure
//--------------------------------------------------------------------------------
      cStmt.execute();

//--------------------------------------------------------------------------------
// debug messages if necessary.
//--------------------------------------------------------------------------------
      cat.debug(this.getClass().getName() + ": attachmentCnt : " + cStmt.getInt("attachmentCnt"));

//--------------------------------------------------------------------------------
// Retrieve the return values
//--------------------------------------------------------------------------------
      attachmentCnt = cStmt.getInt("attachmentCnt");
    }
    catch (SQLException e) {
      cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
      throw new Exception("getAttachmentCount() " + e.getMessage());
    }
    finally
    {
      try
      {
        cStmt.close();
      }
      catch (SQLException e) {
        cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
        throw new Exception("getAttachmentCount() " + e.getMessage());
      }
    }
    return attachmentCnt;
  }

  public String getAttachmentPhoto(Connection conn,
                                   Byte stKey,
                                   Integer sKey)
   throws Exception, SQLException {

    CallableStatement cStmt = null;
    String photoFileName = "";

    try
    {
//--------------------------------------------------------------------------------
// Call a procedure with two IN parameters and one OUT parameter.
//--------------------------------------------------------------------------------
      cStmt = conn.prepareCall("{call GetAttachmentPhoto(?,?,?)}");
//--------------------------------------------------------------------------------
// Set the value for the IN parameter
//--------------------------------------------------------------------------------
      cStmt.setByte(1, stKey);
      cStmt.setInt(2, sKey);
//--------------------------------------------------------------------------------
// Execute the stored procedure
//--------------------------------------------------------------------------------
      cStmt.execute();
//--------------------------------------------------------------------------------
// debug messages if necessary.
//--------------------------------------------------------------------------------
      photoFileName = cStmt.getString("photoFileName");
      cat.debug(this.getClass().getName() + ": photoFileName : " + photoFileName);
    }
    catch (SQLException e) {
      cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
      throw new Exception("getAttachmentPhoto() " + e.getMessage());
    }
    finally
    {
      try
      {
        cStmt.close();
      }
      catch (SQLException e) {
        cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
        throw new Exception("getAttachmentPhoto() " + e.getMessage());
      }
    }
    return photoFileName;
  }


  public HashMap<String,Object> doHelpAttachment(Connection conn,
                                  Byte stKey,
                                  Integer sKey)
   throws Exception, SQLException {

    CallableStatement cStmt = null;
    HashMap<String,Object> attachMap = new HashMap<String,Object>();
    try
    {
//--------------------------------------------------------------------------------
// Call a procedure with two IN parameters and one OUT parameter.
//--------------------------------------------------------------------------------
      cStmt = conn.prepareCall("{call GetAttachmentCombo(?,?,?,?,?)}");
//--------------------------------------------------------------------------------
// Set the value for the IN parameter
//--------------------------------------------------------------------------------
      cStmt.setByte(1, stKey);
      cStmt.setInt(2, sKey);
//--------------------------------------------------------------------------------
// Execute the stored procedure
//--------------------------------------------------------------------------------
      cStmt.execute();
//--------------------------------------------------------------------------------
// debug messages if necessary.
//--------------------------------------------------------------------------------
      cat.debug(this.getClass().getName() + ": attachHelpCount : " + cStmt.getInt("attachmentCnt"));
      cat.debug(this.getClass().getName() + ": attachHtmlName  : " + cStmt.getString("htmlFileName"));
      cat.debug(this.getClass().getName() + ": attachVideoName : " + cStmt.getString("videoFileName"));

      attachMap.put("attachmentCnt", cStmt.getInt("attachmentCnt"));
      attachMap.put("htmlFileName", cStmt.getString("htmlFileName"));
      attachMap.put("videoFileName", cStmt.getString("videoFileName"));
    }
    catch (SQLException e) {
      cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
      throw new Exception("getAttachmentCombo() " + e.getMessage());
    }
    finally
    {
      try
      {
        cStmt.close();
      }
      catch (SQLException e) {
        cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
        throw new Exception("getAttachmentCombo() " + e.getMessage());
      }
    }
    return attachMap;
  }

  public String getAttachmentName(Connection conn,
                                  Integer attachmentKey)
   throws Exception, SQLException {

    CallableStatement cStmt = null;
    String attachmentName = "";

    try
    {
//--------------------------------------------------------------------------------
// Call a procedure with two IN parameters and one OUT parameter.
//--------------------------------------------------------------------------------
      cStmt = conn.prepareCall("{call GetAttachmentName(?,?)}");
//--------------------------------------------------------------------------------
// Set the value for the IN parameter
//--------------------------------------------------------------------------------
      cStmt.setInt(1, attachmentKey);
//--------------------------------------------------------------------------------
// Execute the stored procedure
//--------------------------------------------------------------------------------
      cStmt.execute();
//--------------------------------------------------------------------------------
// debug messages if necessary.
//--------------------------------------------------------------------------------
      attachmentName = cStmt.getString("attachmentName");
      cat.debug(this.getClass().getName() + ": attachmentName : " + attachmentName);
    }
    catch (SQLException e) {
      cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
      throw new Exception("getAttachmentName() " + e.getMessage());
    }
    finally
    {
      try
      {
        cStmt.close();
      }
      catch (SQLException e) {
        cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
        throw new Exception("getAttachmentName() " + e.getMessage());
      }
    }
    return attachmentName;
  }


  public String getAttachmentSubject(Connection conn,
                                     Integer attachmentKey)
   throws Exception, SQLException {

    CallableStatement cStmt = null;
    String subjectName = "";

    try
    {
//--------------------------------------------------------------------------------
// Call a procedure with two IN parameters and one OUT parameter.
//--------------------------------------------------------------------------------
      cStmt = conn.prepareCall("{call GetAttachmentSubject(?,?)}");
//--------------------------------------------------------------------------------
// Set the value for the IN parameter
//--------------------------------------------------------------------------------
      cStmt.setInt(1, attachmentKey);
//--------------------------------------------------------------------------------
// Execute the stored procedure
//--------------------------------------------------------------------------------
      cStmt.execute();
//--------------------------------------------------------------------------------
// debug messages if necessary.
//--------------------------------------------------------------------------------
      subjectName = cStmt.getString("subjectName");
      cat.debug(this.getClass().getName() + ": subjectName : " + subjectName);
    }
    catch (SQLException e) {
      cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
      throw new Exception("getAttachmentSubject() " + e.getMessage());
    }
    finally
    {
      try
      {
        cStmt.close();
      }
      catch (SQLException e) {
        cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
        throw new Exception("getAttachmentSubject() " + e.getMessage());
      }
    }
    return subjectName;
  }

  public Integer getAttachmentOrderFormKey(Connection conn,
                                           Integer purchaseOrderKey)
   throws Exception, SQLException {

    CallableStatement cStmt = null;
    Integer attachmentKey = null;

    try
    {
//--------------------------------------------------------------------------------
// Call a procedure with two IN parameters and one OUT parameter.
//--------------------------------------------------------------------------------
      cStmt = conn.prepareCall("{call GetAttachmentOrderFormKey(?,?)}");
      cStmt.setInt(1, purchaseOrderKey);
      cStmt.execute();
//--------------------------------------------------------------------------------
// debug messages if necessary.
//--------------------------------------------------------------------------------
      attachmentKey = cStmt.getInt("attachmentKey");
      cat.debug(this.getClass().getName() + ": attachmentKey : " + attachmentKey);
    }
    catch (SQLException e) {
      cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
      throw new Exception("getAttachmentOrderFormKey() " + e.getMessage());
    }
    finally
    {
      try
      {
        cStmt.close();
      }
      catch (SQLException e) {
        cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
        throw new Exception("getAttachmentOrderFormKey() " + e.getMessage());
      }
    }
    return attachmentKey;
  }

  public String getAttachmentPath(HttpServletRequest request,
                                  Byte sourceTypeKey,
                                  Integer sourceKey)
   throws Exception {
    cat.debug("Start getAttachmentPath()  [" + sourceTypeKey + "] [" + sourceKey + "]");
    ActionMessages actionMessages = new ActionMessages();
    String errorNote = "";
    File myDir = null;
    try {
//--------------------------------------------------------------------------------
// Save the uploaded file in the specified directory on the server.
// One sub-directory per source type key and one per source key.
//--------------------------------------------------------------------------------
      String savePath = getPropertyService().getCustomerProperties(request, "fileupload.physical.directory");
      String pathSymbol = getPropertyService().getCustomerProperties(request, "fileupload.separator.symbol");
      myDir = new File(savePath);
      if (myDir.exists()) {
        savePath = savePath + sourceTypeKey + pathSymbol;
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
        savePath = savePath + sourceKey + pathSymbol;
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
        if (!(myDir.exists())) {
          errorNote = "The upload full directory does not exist: " + savePath + ".";
          actionMessages.add(ActionMessages.GLOBAL_MESSAGE,
            new ActionMessage("errors.database.error", errorNote));
          throw new Exception(errorNote);
        }
      } else {
        errorNote = "The upload base directory does not exist: " + savePath + ".";
        actionMessages.add(ActionMessages.GLOBAL_MESSAGE,
          new ActionMessage("errors.database.error", errorNote));
        throw new Exception(errorNote);
      }
    }
    catch (Exception e) {
      cat.error(this.getClass().getName() + ": Exception : " + e.getMessage());
    }
    cat.debug("Finish getAttachmentPath()..: " + myDir.getAbsolutePath());
    return myDir.getAbsolutePath();
  }

  public Integer insertAttachmentRow(Connection conn,
                                     Byte sourceTypeKey,
                                     Integer sourceKey,
                                     String fileName,
                                     String contentType,
                                     Integer fileSize,
                                     String userName)
    throws Exception, SQLException {
    cat.debug("Start insertAttachmentRow()");
    cat.debug("       sourceTypeKey...: " + sourceTypeKey);
    cat.debug("       sourceKey.......: " + sourceKey);
    cat.debug("       fileName........: " + fileName);
    cat.debug("       contentType.....: " + contentType);
    cat.debug("       fileSize........: " + fileSize);

    PreparedStatement ps = null;
    ResultSet rs = null;
    Integer attKey = 1;
    try {
//--------------------------------------------------------------------------------
// Get the maximum key from the attachment table.
//--------------------------------------------------------------------------------
      String query = "SELECT COUNT(*) AS att_cnt, MAX(thekey)+1 AS att_key "
                   + "FROM attachment";
      cat.debug("       query...: " + query);
      ps = conn.prepareStatement(query);
      rs = ps.executeQuery();
      if (rs.next()) {
        if ( rs.getInt("att_cnt") > 0 )
          attKey = rs.getInt("att_key");
      } else {
        throw new Exception("Attachment MAX() not found!");
      }
      cat.debug("       attKey....: " + attKey);

//--------------------------------------------------------------------------------
// Generate a subject line from the upload file name.
// 1) drop the file extension.
// 2) replace underscores with blanks.
//--------------------------------------------------------------------------------
      String attSubject = "";
      int x = fileName.indexOf(".");
      cat.debug("       fileName.indexOf().: " + x);
      if (x > 0)
        attSubject = fileName.substring(0,x);
      attSubject = attSubject.replace('_', ' ');
      cat.debug("       attSubject......: " + attSubject);
//--------------------------------------------------------------------------------
// Prepare an insert statement for the attachment table.
//--------------------------------------------------------------------------------
      query = "INSERT INTO attachment "
            + "(thekey,"
            + "sourcetype_key,"
            + "source_key,"
            + "subject,"
            + "attachment_date,"
            + "file_name,"
            + "file_type,"
            + "file_size,"
            + "create_user,"
            + "create_stamp,"
            + "update_user,"
            + "update_stamp) "
            + "VALUES (?,?,?,?,DATE(CURRENT_TIMESTAMP),?,?,?,"
            + "?,CURRENT_TIMESTAMP,?,CURRENT_TIMESTAMP)";
      ps = conn.prepareStatement(query);
      ps.setInt(1, attKey);
      ps.setByte(2, sourceTypeKey);
      ps.setInt(3, sourceKey);
      ps.setString(4, attSubject);
      ps.setString(5, fileName);
      ps.setString(6, contentType);
      ps.setInt(7, fileSize);
      ps.setString(8, userName);
      ps.setString(9, userName);
      ps.executeUpdate();
    }
    catch (SQLException e) {
      cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
      throw new Exception("insertAttachmentRow() " + e.getMessage());
    }
    finally {
      if (rs != null) {
        try {
          rs.close();
        }
        catch (SQLException e) {
          cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
          throw new Exception("insertAttachmentRow() " + e.getMessage());
        }
        rs = null;
      }
      if (ps != null) {
        try {
          ps.close();
        }
        catch (SQLException e) {
          cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
          throw new Exception("insertAttachmentRow() " + e.getMessage());
        }
        ps = null;
      }
    }
    cat.debug("Finish insertAttachmentRow() [" + attKey + "]");
    return attKey;
  }

}