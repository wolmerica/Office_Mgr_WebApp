/*
 * CalendarListHeadDOForm.java
 *
 * Created on January 15, 2007, 09:33 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 * 01/05/2006 Implement tools.formatter library.
 */

package com.wolmerica.calendar;

import com.wolmerica.tools.formatter.AbstractDO;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class CalendarListHeadDO extends AbstractDO implements Serializable
{
  private Integer currentDayOfYear = null;
  private Integer currentMonth = null;
  private Integer currentYear = null;
  private Date currentFromDate = null;
  private Date currentToDate = null;
  private Byte dayOfWeek = null;    // Sun, Mon, Tue, Wed, Thu, Fri, & Sat  
  private Date previousFromDate = null;
  private Date previousToDate = null;
  private Date nextFromDate = null;
  private Date nextToDate = null;
  private Integer recordCount = null;
  private ArrayList calendarForm;

  public void setCurrentDayOfYear(Integer currentDayOfYear) {
    this.currentDayOfYear = currentDayOfYear;
  }

  public Integer getCurrentDayOfYear() {
    return currentDayOfYear;
  }

  public void setCurrentMonth(Integer currentMonth) {
    this.currentMonth = currentMonth;
  }

  public Integer getCurrentMonth() {
    return currentMonth;
  }

  public void setCurrentYear(Integer currentYear) {
    this.currentYear = currentYear;
  }

  public Integer getCurrentYear() {
    return currentYear;
  }

  public void setCurrentFromDate(Date currentFromDate) {
    this.currentFromDate = currentFromDate;
  }

  public Date getCurrentFromDate() {
    return currentFromDate;
  }

  public void setCurrentToDate(Date currentToDate) {
    this.currentToDate = currentToDate;
  }

  public Date getCurrentToDate() {
    return currentToDate;
  }

  public void setDayOfWeek(Byte dayOfWeek) {
    this.dayOfWeek = dayOfWeek;
  }

  public Byte getDayOfWeek() {
    return dayOfWeek;
  }
    
  public void setPreviousFromDate(Date previousFromDate) {
    this.previousFromDate = previousFromDate;
  }

  public Date getPreviousFromDate() {
    return previousFromDate;
  }

  public void setPreviousToDate(Date previousToDate) {
    this.previousToDate = previousToDate;
  }

  public Date getPreviousToDate() {
    return previousToDate;
  }

  public void setNextFromDate(Date nextFromDate) {
    this.nextFromDate = nextFromDate;
  }

  public Date getNextFromDate() {
    return nextFromDate;
  }

  public void setNextToDate(Date nextToDate) {
    this.nextToDate = nextToDate;
  }

  public Date getNextToDate() {
    return nextToDate;
  }

  public void setRecordCount(Integer recordCount) {
    this.recordCount = recordCount;
  }

  public Integer getRecordCount() {
    return recordCount;
  }

  public void setCalendarForm(ArrayList calendarList){
  this.calendarForm=calendarList;
  }

  public ArrayList getCalendarForm(){
  return calendarForm;
  }
}