/*
 * ScheduleListHeadDOForm.java
 *
 * Created on September 7, 2006, 10:00 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 * 01/05/2006 Implement tools.formatter library.
 */

package com.wolmerica.schedule;

import com.wolmerica.tools.formatter.AbstractDO;
import java.io.Serializable;
import java.util.ArrayList;
import java.math.BigDecimal;
import java.util.Date;
import java.sql.Timestamp;

public class ScheduleListHeadDO extends AbstractDO implements Serializable
{
  private Integer customerKeyFilter = null;   
  private String clientName = "";  
  private Date fromDate;
  private Date toDate;
  private String makeModelFilter = "";
  private Integer recordCount = new Integer("0");
  private Integer firstRecord = new Integer("0");
  private Integer lastRecord = new Integer("0");
  private Integer previousPage = new Integer("0");
  private Integer currentPage = new Integer("1");  
  private Integer nextPage = new Integer("0");
  private ArrayList scheduleForm;
  private ArrayList permissionListForm;

  public void setCustomerKeyFilter(Integer customerKeyFilter) {
    this.customerKeyFilter = customerKeyFilter;
  }

  public Integer getCustomerKeyFilter() {
    return customerKeyFilter;
  }
  
  public void setClientName(String clientName) {
    this.clientName = clientName;
  }

  public String getClientName() {
    return clientName;
  }  
  
  public void setFromDate(Date fromDate) {
    this.fromDate = fromDate;
  }

  public Date getFromDate() {
    return fromDate;
  }

  public void setToDate(Date toDate) {
    this.toDate = toDate;
  }

  public Date getToDate() {
    return toDate;
  }
    
  public void setMakeModelFilter(String makeModelFilter) {
    this.makeModelFilter = makeModelFilter;
  }

  public String getMakeModelFilter() {
    return makeModelFilter;
  }

  public void setRecordCount(Integer recordCount) {
    this.recordCount = recordCount;
  }

  public Integer getRecordCount() {
    return recordCount;
  }

  public void setFirstRecord(Integer firstRecord) {
    this.firstRecord = firstRecord;
  }

  public Integer getFirstRecord() {
    return firstRecord;
  }

  public void setLastRecord(Integer lastRecord) {
    this.lastRecord = lastRecord;
  }

  public Integer getLastRecord() {
    return lastRecord;
  }

  public void setPreviousPage(Integer previousPage ) {
    this.previousPage = previousPage;
  }

  public Integer getPreviousPage() {
    return previousPage;
  }
  
  public void setCurrentPage(Integer currentPage ) {
    this.currentPage = currentPage;
  }

  public Integer getCurrentPage() {
    return currentPage;
  }
  
  public void setNextPage(Integer nextPage ) {
    this.nextPage = nextPage;
  }

  public Integer getNextPage() {
    return nextPage;
  }

  public void setScheduleForm(ArrayList scheduleList){
  this.scheduleForm=scheduleList;
  }

  public ArrayList getScheduleForm(){
  return scheduleForm;
  }

  public void setPermissionListForm(ArrayList permissionList){
  this.permissionListForm=permissionList;
  }

  public ArrayList getPermissionListForm(){
  return permissionListForm;
  }
}