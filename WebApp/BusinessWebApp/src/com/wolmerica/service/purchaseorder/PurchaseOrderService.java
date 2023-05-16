package com.wolmerica.service.purchaseorder;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;

/**
 * @author  Richard Wolschlager
 * @date    July 31, 2010
 */
public interface PurchaseOrderService {

  public HashMap getPurchaseOrderKeys(Connection conn,
                                      Integer poKey)
  throws Exception, SQLException;

  public String getVendorOrderFormDetail(Connection conn,
                                         Integer poKey,
                                         Byte stKey,
                                         Integer sKey,
                                         String lookupCode)
  throws Exception, SQLException;
}
