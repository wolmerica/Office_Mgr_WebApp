/*
 * PromotionListHeadDO.java
 *
 * Created on February 21, 2009, 7:07 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package com.wolmerica.promotion;

/**
 *
 * @author Richard
 */
import com.wolmerica.tools.formatter.AbstractDO;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class PromotionListHeadDO extends AbstractDO implements Serializable
{
  private String promoNameFilter = "";
  private String promoCategoryFilter = "";
  private Date promoFromDate = null;
  private Date promoToDate = null;  
  private Integer recordCount = new Integer("0");
  private Integer firstRecord = new Integer("0");
  private Integer lastRecord = new Integer("0");
  private Integer previousPage = new Integer("0");
  private Integer currentPage = new Integer("1");  
  private Integer nextPage = new Integer("0");
  private ArrayList promotionListForm;
  private ArrayList permissionListForm;

  public void setPromoNameFilter(String promoNameFilter) {
    this.promoNameFilter = promoNameFilter;
  }

  public String getPromoNameFilter() {
    return promoNameFilter;
  }

  public void setPromoCategoryFilter(String promoCategoryFilter) {
    this.promoCategoryFilter = promoCategoryFilter;
  }

  public String getPromoCategoryFilter() {
    return promoCategoryFilter;
  }  
  
  public void setPromoFromDate(Date promoFromDate) {
    this.promoFromDate = promoFromDate;
  }

  public Date getPromoFromDate() {
    return promoFromDate;
  }

  public void setPromoToDate(Date promoToDate) {
    this.promoToDate = promoToDate;
  }

  public Date getPromoToDate() {
    return promoToDate;
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

  public void setPromotionListForm(ArrayList promotionListForm){
    this.promotionListForm=promotionListForm;
  }

  public ArrayList getPromotionListForm(){
    return promotionListForm;
  }

  public void setPermissionListForm(ArrayList permissionList){
  this.permissionListForm=permissionList;
  }

  public ArrayList getPermissionListForm(){
  return permissionListForm;
  }
}

