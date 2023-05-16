package com.wolmerica.service.logistic;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author  Richard Wolschlager
 * @date    August 2, 2010
 */
public interface LogisticService {

  public Integer getLogisticsCount(Connection conn,
                                   Byte sourceTypeKey,
                                   Integer sourceKey)
  throws Exception, SQLException;


}
