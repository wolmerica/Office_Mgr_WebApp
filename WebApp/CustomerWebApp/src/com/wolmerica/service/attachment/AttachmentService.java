package com.wolmerica.service.attachment;

import javax.servlet.http.HttpServletRequest;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;

/**
 * @author  Richard Wolschlager
 * @date    August 1, 2010
 */
public interface AttachmentService {

  public Integer getAttachmentCount(Connection conn,
                                    Byte sourceTypeKey,
                                    Integer sourceKey)
  throws Exception, SQLException;

  public String getAttachmentPhoto(Connection conn,
                                   Byte stKey,
                                   Integer sKey)
  throws Exception, SQLException;

  public HashMap doHelpAttachment(Connection conn,
                                  Byte stKey,
                                  Integer sKey)
  throws Exception, SQLException;

  public String getAttachmentName(Connection conn,
                                  Integer attachmentKey)
  throws Exception, SQLException;

  public String getAttachmentSubject(Connection conn,
                                     Integer attachmentKey)
  throws Exception, SQLException;

  public Integer getAttachmentOrderFormKey(Connection conn,
                                           Integer purchaseOrderKey)
  throws Exception, SQLException;

  public String getAttachmentPath(HttpServletRequest request,
                                  Byte sourceTypeKey,
                                  Integer sourceKey)
  throws Exception;

  public Integer insertAttachmentRow(Connection conn,
                                     Byte sourceTypeKey,
                                     Integer sourceKey,
                                     String fileName,
                                     String contentType,
                                     Integer fileSize,
                                     String userName)
   throws Exception, SQLException ;


}
