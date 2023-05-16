/*
 * ServiceDictionaryListHeadDO.java
 *
 * Created on June 20, 2006, 07:58 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 * 01/05/2006 Implement tools.formatter library.
 */

package com.wolmerica.servicedictionary;

import com.wolmerica.tools.formatter.AbstractDO;
import java.io.Serializable;
import java.util.ArrayList;

public class ServiceDictionaryListHeadDO extends AbstractDO implements Serializable
{
  private String serviceNameFilter = "";
  private String serviceNumFilter = "";
  private String categoryNameFilter = "";
  private Integer recordCount = new Integer("0");
  private Integer firstRecord = new Integer("0");
  private Integer lastRecord = new Integer("0");
  private Integer previousPage = new Integer("0");
  private Integer currentPage = new Integer("1");  
  private Integer nextPage = new Integer("0");
  private ArrayList serviceDictionaryListForm;
  private ArrayList permissionListForm;

  public void setServiceNameFilter(String serviceNameFilter) {
    this.serviceNameFilter = serviceNameFilter;
  }

  public String getServiceNameFilter() {
    return serviceNameFilter;
  }

  public void setServiceNumFilter(String serviceNumFilter) {
    this.serviceNumFilter = serviceNumFilter;
  }

  public String getServiceNumFilter() {
    return serviceNumFilter;
  }

  public void setCategoryNameFilter(String categoryNameFilter) {
    this.categoryNameFilter = categoryNameFilter;
  }

  public String getCategoryNameFilter() {
    return categoryNameFilter;
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

  public void setServiceDictionaryListForm(ArrayList serviceDictionaryList){
  this.serviceDictionaryListForm = serviceDictionaryList;
  }

  public ArrayList getServiceDictionaryListForm(){
  return serviceDictionaryListForm;
  }

  public void setPermissionListForm(ArrayList permissionList){
  this.permissionListForm=permissionList;
  }

  public ArrayList getPermissionListForm(){
  return permissionListForm;
  }
}