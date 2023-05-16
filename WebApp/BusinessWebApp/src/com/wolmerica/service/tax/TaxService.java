package com.wolmerica.service.tax;

import java.sql.Connection;
import java.sql.SQLException;
import java.math.BigDecimal;

/**
 * @author  Richard Wolschlager
 * @date    July 31, 2010
 */
public interface TaxService {

  public BigDecimal getTaxRate(Connection conn,
                               Byte taxMarkUpKey)
  throws Exception, SQLException;
  
}
