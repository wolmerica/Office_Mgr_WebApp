package com.wolmerica.service.resource;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author  Richard Wolschlager
 * @date    August 3, 2010
 */
public interface ResourceService {

  public String getResourceName(Connection conn,
                                Integer resourceKey)
  throws Exception, SQLException;

}
