/*
 * BreedListForm.java
 *
 * Created on March 05, 2006, 6:36 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 * 01/05/2006 Implement tools.formatter library.
 */

package com.wolmerica.breed;

//--------------------------------------------------------------------------------
// @author Richard Wolschlager
//--------------------------------------------------------------------------------
import com.wolmerica.tools.formatter.AbstractDO;
import java.io.Serializable;
import java.util.ArrayList;

public class BreedListHeadDO extends AbstractDO implements Serializable
{
  private Integer speciesKey = null;
  private String speciesName = "";
  private String breedNameFilter = "";
  private Integer recordCount = new Integer("0");
  private Integer firstRecord = new Integer("0");
  private Integer lastRecord = new Integer("0");
  private Integer previousPage = new Integer("0");
  private Integer nextPage = new Integer("0");
  private ArrayList breedListForm;
  private ArrayList permissionListForm;

  public void setSpeciesKey(Integer speciesKey) {
    this.speciesKey =speciesKey;
  }

  public Integer getSpeciesKey() {
    return speciesKey;
  }

  public void setSpeciesName(String speciesName) {
    this.speciesName = speciesName;
  }

  public String getSpeciesName() {
    return speciesName;
  }

  public void setBreedNameFilter(String breedNameFilter) {
    this.breedNameFilter =breedNameFilter;
  }

  public String getBreedNameFilter() {
    return breedNameFilter;
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

  public void setBreedListForm(ArrayList breedList){
  this.breedListForm=breedList;
  }

  public ArrayList getBreedListForm(){
  return breedListForm;
  }

  public void setPermissionListForm(ArrayList permissionList){
  this.permissionListForm=permissionList;
  }

  public ArrayList getPermissionListForm(){
  return permissionListForm;
  }
}