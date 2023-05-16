/*
 * VehicleListHeadDOForm.java
 *
 * Created on June 10, 2007, 11:01 AM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 * 01/05/2006 Implement tools.formatter library.
 */

package com.wolmerica.vehicle;

import com.wolmerica.tools.formatter.AbstractDO;
import java.io.Serializable;
import java.util.ArrayList;
import java.math.BigDecimal;
import java.util.Date;
import java.sql.Timestamp;

public class VehicleListHeadDO extends AbstractDO implements Serializable
{
  private Byte sourceTypeKey = null;
  private Integer customerKeyFilter = null;
  private String clientNameFilter = "";
  private Short yearFilter = null;
  private String makeFilter = "";
  private Integer recordCount = new Integer("0");
  private Integer firstRecord = new Integer("0");
  private Integer lastRecord = new Integer("0");
  private Integer previousPage = new Integer("0");
  private Integer nextPage = new Integer("0");
  private ArrayList vehicleListForm;
  private ArrayList permissionListForm;

  public void setSourceTypeKey(Byte sourceTypeKey) {
    this.sourceTypeKey = sourceTypeKey;
  }

  public Byte getSourceTypeKey() {
    return sourceTypeKey;
  }

  public void setCustomerKeyFilter(Integer customerKeyFilter) {
    this.customerKeyFilter = customerKeyFilter;
  }

  public Integer getCustomerKeyFilter() {
    return customerKeyFilter;
  }

  public void setClientNameFilter(String clientNameFilter) {
    this.clientNameFilter =clientNameFilter;
  }

  public String getClientNameFilter() {
    return clientNameFilter;
  }
  
  public void setYearFilter(Short yearFilter) {
    this.yearFilter = yearFilter;
  }

  public Short getYearFilter() {
    return yearFilter;
  }  

  public void setMakeFilter(String makeFilter) {
    this.makeFilter = makeFilter;
  }

  public String getMakeFilter() {
    return makeFilter;
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

  public void setNextPage(Integer nextPage ) {
    this.nextPage = nextPage;
  }

  public Integer getNextPage() {
    return nextPage;
  }

  public void setVehicleListForm(ArrayList vehicleList){
  this.vehicleListForm=vehicleList;
  }

  public ArrayList getVehicleListForm(){
  return vehicleListForm;
  }

  public void setPermissionListForm(ArrayList permissionList){
  this.permissionListForm=permissionList;
  }

  public ArrayList getPermissionListForm(){
  return permissionListForm;
  }
}