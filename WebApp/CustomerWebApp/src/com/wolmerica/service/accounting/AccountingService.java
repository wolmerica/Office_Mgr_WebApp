package com.wolmerica.service.accounting;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;

/**
 * @author  Richard Wolschlager
 * @date    July 31, 2010
 */
public interface AccountingService {

  public HashMap<String, Object> getCustomerLedgerBalance(Connection conn,
                                          Integer customerKey)
  throws Exception, SQLException;

  public HashMap<String, Object> getCustomerLastPayment(Connection conn,
                                        Integer customerKey)
  throws Exception, SQLException;

  public HashMap<String, Object> getCustomerBalance(Connection conn,
                                    Integer customerKey)
  throws Exception, SQLException;

  public HashMap<String, Object> getCustomerAcctTranDueAmount(Connection conn,
                                              Integer customerKey,
                                              Integer customerAcctKey)
  throws Exception, SQLException;

  public HashMap<String, Object> getVendorLastPayment(Connection conn,
                                   Integer vendorKey)
  throws Exception, SQLException;

  public HashMap<String, Object> getVendorBalance(Connection conn,
                                  Integer vendorKey)
  throws Exception, SQLException;

  public HashMap<String, Object> getVendorAcctTranDueAmount(Connection conn,
                                            Integer vendorKey,
                                            Integer vendorAcctKey)
  throws Exception, SQLException;
}
