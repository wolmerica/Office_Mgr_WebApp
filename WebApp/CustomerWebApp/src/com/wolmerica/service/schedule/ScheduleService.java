package com.wolmerica.service.schedule;

import com.wolmerica.schedule.ScheduleDO;
import com.wolmerica.workorder.WorkOrderDO;

import javax.servlet.http.HttpServletRequest;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.sql.Timestamp;
import java.util.Date;

/**
 * @author  Richard Wolschlager
 * @date    August 28, 2010
 */
public interface ScheduleService {

  public ScheduleDO buildScheduleForm(Connection conn,
                                      Integer schKey)
  throws Exception, SQLException;

  public boolean setRewindIndicator(Connection conn,
                                    Integer ciKey,
                                    String updateUser,
                                    boolean updateId)
  throws Exception, SQLException;

  public WorkOrderDO buildWorkOrderForm(Connection conn,
                                        Integer woKey)
  throws Exception, SQLException;

  public ArrayList getResourceSchedule(HttpServletRequest request,
                                       Connection conn,
                                       Integer woKey,
                                       Integer resourceKey,
                                       Timestamp startStamp,
                                       Timestamp endStamp)
  throws Exception, SQLException;

  public ArrayList getCustomerSchedule(HttpServletRequest request,
                                       Connection conn,
                                       Integer customerKey,
                                       Date startDate)
  throws Exception, SQLException;

  public ArrayList getAttributeToSchedule(HttpServletRequest request,
                                          Connection conn,
                                          Byte sourceTypeKey,
                                          Integer sourceKey,
                                          Date startDate)
  throws Exception, SQLException;

  public void setScheduleStatus(Connection conn,
                                Integer scheduleKey)
  throws Exception, SQLException;


}
