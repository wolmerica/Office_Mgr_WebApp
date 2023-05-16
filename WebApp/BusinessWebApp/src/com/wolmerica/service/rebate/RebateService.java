package com.wolmerica.service.rebate;

import java.sql.Connection;
import java.sql.CallableStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

/**
 * @author  Richard Wolschlager
 * @date    August 3, 2010
 */
public interface RebateService {

  public Short getRebateCountByItem(Connection conn,
                                    Integer idKey)
  throws Exception, SQLException;

  public Short getRebateInstanceCountByItem(Connection conn,
                                            Integer idKey)
  throws Exception, SQLException;

  public Short getRebateCountByPO(Connection conn,
                                  Integer poKey,
                                  Timestamp poCreateStamp)
  throws Exception, SQLException;

  public Short getRebateInstanceCountByPO(Connection conn,
                                          Integer poKey)

  throws Exception, SQLException;

  public Short getRebateInstanceCountByRebate(Connection conn,
                                              Integer rebateKey)

  throws Exception, SQLException;

}
