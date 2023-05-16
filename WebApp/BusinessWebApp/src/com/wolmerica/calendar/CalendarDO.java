/*
 * CalendarDO.java
 *
 * Created on January 15, 2007, 09:33 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 * 12/19/2005 Implement tools.formatter library.
 */

package com.wolmerica.calendar;

/**
 *
 * @author Richard
 */
import com.wolmerica.tools.formatter.AbstractDO;
import java.io.Serializable;
import java.util.Date;

public class CalendarDO extends AbstractDO implements Serializable {

  private Integer key = null;
  private Date theDate = null;      // 01/15/2007
  private Byte weekNumber = null;   // 1-first, 2-second, 3-third, etc.
  private Byte dayOfWeek = null;    // Sun, Mon, Tue, Wed, Thu, Fri, & Sat
  private Byte dayOfMonth = null;  
  private Boolean currentMonthId = null; 

  public void setKey(Integer key) {
    this.key = key;
  }

  public Integer getKey() {
    return key;
  }

  public void setTheDate(Date theDate) {
    this.theDate = theDate;
  }

  public Date getTheDate() {
    return theDate;
  }

  public void setWeekNumber(Byte weekNumber) {
    this.weekNumber = weekNumber;
  }

  public Byte getWeekNumber() {
    return weekNumber;
  }

  public void setDayOfWeek(Byte dayOfWeek) {
    this.dayOfWeek = dayOfWeek;
  }

  public Byte getDayOfWeek() {
    return dayOfWeek;
  }

  public void setDayOfMonth(Byte dayOfMonth) {
    this.dayOfMonth = dayOfMonth;
  }

  public Byte getDayOfMonth() {
    return dayOfMonth;
  }  
  
  public void setCurrentMonthId(Boolean currentMonthId) {
    this.currentMonthId = currentMonthId;
  }

  public Boolean getCurrentMonthId() {
    return currentMonthId;
  } 
}
