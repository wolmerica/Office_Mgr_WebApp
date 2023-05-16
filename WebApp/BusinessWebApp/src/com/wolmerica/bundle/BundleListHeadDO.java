/*
 * BundleListHeadDO.java
 *
 * Created on July 02, 2006, 3:31 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package com.wolmerica.bundle;

/**
 *
 * @author Richard
 */
import com.wolmerica.tools.formatter.AbstractDO;
import java.io.Serializable;
import java.util.ArrayList;
import java.math.BigDecimal;
import java.util.Date;
import java.sql.Timestamp;

public class BundleListHeadDO extends AbstractDO implements Serializable
{
  private String bundleNameFilter = "";
  private String bundleCategoryFilter = "";
  private Integer recordCount = new Integer("0");
  private Integer firstRecord = new Integer("0");
  private Integer lastRecord = new Integer("0");
  private Integer previousPage = new Integer("0");
  private Integer currentPage = new Integer("1");  
  private Integer nextPage = new Integer("0");
  private ArrayList bundleListForm;
  private ArrayList permissionListForm;

  public void setBundleNameFilter(String bundleNameFilter) {
    this.bundleNameFilter = bundleNameFilter;
  }

  public String getBundleNameFilter() {
    return bundleNameFilter;
  }

  public void setBundleCategoryFilter(String bundleCategoryFilter) {
    this.bundleCategoryFilter = bundleCategoryFilter;
  }

  public String getBundleCategoryFilter() {
    return bundleCategoryFilter;
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

  public void setBundleListForm(ArrayList bundleListForm){
    this.bundleListForm=bundleListForm;
  }

  public ArrayList getBundleListForm(){
    return bundleListForm;
  }

  public void setPermissionListForm(ArrayList permissionList){
  this.permissionListForm=permissionList;
  }

  public ArrayList getPermissionListForm(){
  return permissionListForm;
  }
}

