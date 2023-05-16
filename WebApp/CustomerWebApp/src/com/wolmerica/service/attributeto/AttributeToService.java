package com.wolmerica.service.attributeto;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;

/**
 * @author  Richard Wolschlager
 * @date    August 1, 2010
 */

public interface AttributeToService {

  public HashMap getAttributeToName(Connection conn,
                                    Byte sourceTypeKey,
                                    Integer sourceKey)
  throws Exception, SQLException;

  public Date getLastServiceDate(Connection conn,
                                 Integer cKey,
                                 Byte stKey,
                                 Integer sKey)
  throws Exception, SQLException;

  public String getAttributeToAgeCode(Connection conn,
                                      Byte sourceTypeKey,
                                      Integer sourceKey)
  throws Exception, SQLException;


}