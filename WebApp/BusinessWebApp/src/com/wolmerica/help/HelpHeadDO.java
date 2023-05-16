/*
 * HelpHeadDO.java
 *
 * Created on April 09, 2006, 6:31 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 * 01/05/2006 Implement tools.formatter library.
 */

package com.wolmerica.help;

import com.wolmerica.tools.formatter.AbstractDO;
import java.io.Serializable;
import java.util.ArrayList;
import java.math.BigDecimal;
import java.util.Date;
import java.sql.Timestamp;

public class HelpHeadDO extends AbstractDO implements Serializable
{
  private Byte levelKey = new Byte("1");
  private Integer packageKey = null;
  private String packageName = "";
  private Integer operationKey = null;
  private String operationName = "";
  private Integer recordCount = new Integer("0");
  private ArrayList helpForm;

  public void setLevelKey(Byte levelKey) {
    this.levelKey = levelKey;
  }

  public Byte getLevelKey() {
    return levelKey;
  } 
  
  public void setPackageKey(Integer packageKey) {
    this.packageKey = packageKey;
  }

  public Integer getPackageKey() {
    return packageKey;
  }

  public void setPackageName(String packageName) {
    this.packageName = packageName;
  }

  public String getPackageName() {
    return packageName;
  }

  public void setOperationKey(Integer operationKey) {
    this.operationKey = operationKey;
  }

  public Integer getOperationKey() {
    return operationKey;
  }

  public void setOperationName(String operationName) {
    this.operationName = operationName;
  }

  public String getOperationName() {
    return operationName;
  }

  public void setRecordCount(Integer recordCount) {
    this.recordCount = recordCount;
  }

  public Integer getRecordCount() {
    return recordCount;
  }

  public void setHelpForm(ArrayList helpForm) {
  this.helpForm=helpForm;
  }

  public ArrayList getHelpForm() {
  return helpForm;
  }
}