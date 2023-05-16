package com.wolmerica.service.accounting;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;

/**
 * @author  Richard Wolschlager
 * @date    July 31, 2010
 */
public interface AccountingService {

  public HashMap getCustomerLedgerBalance(Connection conn,
                                          Integer customerKey)
  throws Exception, SQLException;

  public HashMap getCustomerLastPayment(Connection conn,
                                        Integer customerKey)
  throws Exception, SQLException;

  public HashMap getCustomerBalance(Connection conn,
                                    Integer customerKey)
  throws Exception, SQLException;

  public HashMap getCustomerAcctTranDueAmount(Connection conn,
                                              Integer customerKey,
                                              Integer customerAcctKey)
  throws Exception, SQLException;

  public HashMap getVendorLastPayment(Connection conn,
                                   Integer vendorKey)
  throws Exception, SQLException;

  public HashMap getVendorBalance(Connection conn,
                                  Integer vendorKey)
  throws Exception, SQLException;

  public HashMap getVendorAcctTranDueAmount(Connection conn,
                                            Integer vendorKey,
                                            Integer vendorAcctKey)
  throws Exception, SQLException;
}
