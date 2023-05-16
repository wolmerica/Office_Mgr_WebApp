package com.wolmerica.service.customerinvoice;

import com.wolmerica.customerinvoice.CustomerInvoiceDO;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author  Richard Wolschlager
 * @date    February 18, 2011
 */

public interface CustomerInvoiceService {

  public CustomerInvoiceDO getCustomerInvoiceTotalsByCiKey(Connection conn,
                                                           CustomerInvoiceDO formDO,
                                                           Integer ciKey)
   throws Exception, SQLException;

  public CustomerInvoiceDO getCustomerInvoiceAllowCredit(Connection conn,
                                                         CustomerInvoiceDO formDO)
   throws Exception, SQLException;

  public CustomerInvoiceDO creditCustomerInvoiceForm(CustomerInvoiceDO formDO)
   throws Exception;

}