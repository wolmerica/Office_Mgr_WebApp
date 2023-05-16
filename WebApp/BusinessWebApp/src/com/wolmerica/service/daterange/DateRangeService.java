package com.wolmerica.service.daterange;

import java.util.Date;

/**
 * @author  Richard Wolschlager
 * @date    August 8, 2010
 */
public interface DateRangeService {

  public String getDateToString(Date theDate)
  throws Exception;

  public Date getYearEndDate()
  throws Exception;

  public Date getYTDFromDate()
  throws Exception;

  public Date getYTDToDate()
  throws Exception;

  public Date getMTDFromDate()
  throws Exception;

  public Date getMTDToDate()
   throws Exception;

  public Date getBACKFromDate(int days)
  throws Exception;

  public Date getBACKToDate()
  throws Exception;

  public Date getFWDFromDate()
  throws Exception;

  public Date getFWDToDate(int days)
  throws Exception;

}
