package com.wolmerica.service.customer;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;

/**
 * @author  Richard Wolschlager
 * @date    August 1, 2010
 */

public interface CustomerService {

  public String getClientName(Connection conn,
                              Integer customerKey)
  throws Exception, SQLException;

  public Date getLastServiceDate(Connection conn,
                                Integer cKey)
  throws Exception, SQLException;

  public String getCustomerEmail(Connection conn,
                                 Byte reminderPrefKey,
                                 Integer customerKey)
  throws Exception, SQLException;

  public HashMap getCustomerAttributeToEntity(Connection conn,
                                              Integer customerKey)
  throws Exception, SQLException;

  public boolean setAdjustmentIndicator(Connection conn,
                                        Integer ciKey,
                                        String updateUser,
                                        boolean updateId)
  throws Exception, SQLException;

}