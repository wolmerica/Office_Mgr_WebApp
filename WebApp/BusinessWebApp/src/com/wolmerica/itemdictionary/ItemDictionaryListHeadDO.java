/*
 * ItemDictionaryListForm.java
 *
 * Created on March 05, 2006, 6:36 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 * 01/05/2006 Implement tools.formatter library.
 */

package com.wolmerica.itemdictionary;

import com.wolmerica.tools.formatter.AbstractDO;
import java.io.Serializable;
import java.util.ArrayList;

public class ItemDictionaryListHeadDO extends AbstractDO implements Serializable
{
  private String brandNameFilter = "";
  private String itemNumFilter = "";
  private String genericNameFilter = "";
  private Integer recordCount = new Integer("0");
  private Integer firstRecord = new Integer("0");
  private Integer lastRecord = new Integer("0");
  private Integer previousPage = new Integer("0");
  private Integer currentPage = new Integer("1");
  private Integer nextPage = new Integer("0");
  private ArrayList itemDictionaryListForm;
  private ArrayList permissionListForm;

  public void setBrandNameFilter(String brandNameFilter) {
    this.brandNameFilter = brandNameFilter;
  }

  public String getBrandNameFilter() {
    return brandNameFilter;
  }

  public void setItemNumFilter(String itemNumFilter) {
    this.itemNumFilter = itemNumFilter;
  }

  public String getItemNumFilter() {
    return itemNumFilter;
  }

  public void setGenericNameFilter(String genericNameFilter) {
    this.genericNameFilter = genericNameFilter;
  }

  public String getGenericNameFilter() {
    return genericNameFilter;
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

  public void setItemDictionaryListForm(ArrayList itemDictionaryList){
  this.itemDictionaryListForm=itemDictionaryList;
  }

  public ArrayList getItemDictionaryListForm(){
  return itemDictionaryListForm;
  }

  public void setPermissionListForm(ArrayList permissionList){
  this.permissionListForm=permissionList;
  }

  public ArrayList getPermissionListForm(){
  return permissionListForm;
  }
}