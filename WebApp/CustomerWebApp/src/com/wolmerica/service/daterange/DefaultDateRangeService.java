package com.wolmerica.service.daterange;

import com.wolmerica.tools.formatter.FormattingException;
import com.wolmerica.tools.formatter.DateFormatter;

import java.util.Calendar;
import java.util.Date;

import org.apache.log4j.Logger;

/**
 * @author  Richard Wolschlager
 * @date    August 8, 2010
 */

public class DefaultDateRangeService implements DateRangeService {

  Logger cat = Logger.getLogger("WOWAPP");

  public String getDateToString(Date theDate)
   throws Exception {
    DateFormatter df = new DateFormatter();
    String displayDate = "";

    try {
      displayDate = df.format(theDate);
    }
    catch (FormattingException fe) {
        fe.getMessage();
    }
    return displayDate;
  }

  public Date getYearEndDate()
   throws Exception {
    Calendar rightNow = Calendar.getInstance();
    rightNow.set(rightNow.get(Calendar.YEAR),0,1);
    rightNow.add(Calendar.DATE,-1);
    return new Date(rightNow.getTime().getTime());
  }

  public Date getYTDFromDate()
   throws Exception {
    Calendar rightNow = Calendar.getInstance();
    rightNow.set(rightNow.get(Calendar.YEAR),0,1);
    return new Date(rightNow.getTime().getTime());
  }

  public Date getYTDToDate()
   throws Exception {
    Calendar rightNow = Calendar.getInstance();
    return new Date(rightNow.getTime().getTime());
  }

  public Date getMTDFromDate()
   throws Exception {
    Calendar rightNow = Calendar.getInstance();
    rightNow.set(rightNow.get(Calendar.YEAR),rightNow.get(Calendar.MONTH),1);
    return new Date(rightNow.getTime().getTime());
  }

  public Date getMTDToDate()
   throws Exception {
    Calendar rightNow = Calendar.getInstance();
    return new Date(rightNow.getTime().getTime());
  }

  public Date getBACKFromDate(int days)
   throws Exception {
    Calendar rightNow = Calendar.getInstance();
    rightNow.add(Calendar.DATE,days*-1);
    return new Date(rightNow.getTime().getTime());
  }

  public Date getBACKToDate()
   throws Exception {
    Calendar rightNow = Calendar.getInstance();
    return new Date(rightNow.getTime().getTime());
  }

  public Date getFWDFromDate()
   throws Exception {
    Calendar rightNow = Calendar.getInstance();
    return new Date(rightNow.getTime().getTime());
  }

  public Date getFWDToDate(int days)
   throws Exception {
    Calendar rightNow = Calendar.getInstance();
    rightNow.add(Calendar.DATE,days);
    return new Date(rightNow.getTime().getTime());
  }

}