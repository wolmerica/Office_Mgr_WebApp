package com.wolmerica.service.vendor;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;

/**
 * @author  Richard Wolschlager
 * @date    August 1, 2010
 */

public interface VendorService {

  public String getVendorName(Connection conn,
                              Integer vendorKey)
  throws Exception, SQLException;

  public String getVendorNameForVI(Connection conn,
                                   Integer viKey)
  throws Exception, SQLException;

  public Boolean isClinicAccount(Connection conn,
                                 Integer vendorKey)
  throws Exception, SQLException;

  public Boolean isTrackResultSupported(Connection conn,
                                        Integer vendorKey)
  throws Exception, SQLException;

  public Boolean isWebServiceSupported(Connection conn,
                                       Integer vendorKey)
  throws Exception, SQLException;

  public Integer GetVendorKeyForTrackResult(Connection conn)
  throws Exception, SQLException;

  public Integer getItemCountByVendor(Connection conn,
                                      Integer vendorKey)
  throws Exception, SQLException;

  public Integer getServiceCountByVendor(Connection conn,
                                         Integer vendorKey)
  throws Exception, SQLException;

  public Integer getLicenseCount(Connection conn,
                                 Byte sourceTypeKey,
                                 Integer sourceKey)
  throws Exception, SQLException;

  public HashMap getLicenseAssignedCount(Connection conn,
                                         Byte invoiceTypeKey,
                                         Integer invoiceKey)
  throws Exception, SQLException;
}