/*
 * HelpDO.java
 *
 * Created on April 09, 2006, 06:47 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 * 12/21/2005 Implement tools.formatter library.
 */

package com.wolmerica.help;

/**
 *
 * @author Richard
 */
import com.wolmerica.tools.formatter.AbstractDO;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.sql.Timestamp;

public class HelpDO extends AbstractDO implements Serializable {

   private Integer key = null;
   private Byte step = null;
   private String description = "";
   private Integer attachmentCount = null;
   private String documentServerURL = "";
   private String htmlFileName = "";
   private String videoFileName = "";
   private String updateUser = "";
   private Timestamp updateStamp = null;

  public void setKey(Integer key) {
    this.key = key;
  }

  public Integer getKey() {
    return key;
  }

  public void setStep(Byte step) {
    this.step = step;
  }

  public Byte getStep() {
    return step;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getDescription() {
    return description;
  }

  public void setAttachmentCount(Integer attachmentCount) {
    this.attachmentCount = attachmentCount;
  }

  public Integer getAttachmentCount() {
    return attachmentCount;
  }

  public void setDocumentServerURL(String documentServerURL) {
    this.documentServerURL = documentServerURL;
  }

  public String getDocumentServerURL() {
    return documentServerURL;
  }

  public void setHtmlFileName(String htmlFileName) {
    this.htmlFileName = htmlFileName;
  }

  public String getHtmlFileName() {
    return htmlFileName;
  }

  public void setVideoFileName(String videoFileName) {
    this.videoFileName = videoFileName;
  }

  public String getVideoFileName() {
    return videoFileName;
  }

  public void setUpdateUser(String updateUser) {
    this.updateUser = updateUser;
  }

  public String getUpdateUser() {
    return updateUser;
  }

  public void setUpdateStamp(Timestamp updateStamp) {
    this.updateStamp = updateStamp;
  }

  public Timestamp getUpdateStamp() {
    return updateStamp;
  }
}