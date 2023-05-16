package com.wolmerica.service.vendor;

import java.sql.Connection;
import java.sql.CallableStatement;
import java.sql.SQLException;
import java.util.HashMap;

import org.apache.log4j.Logger;

/**
 * @author  Richard Wolschlager
 * @date    August 1, 2010
 */

public class DefaultVendorService implements VendorService {

  Logger cat = Logger.getLogger("WOWAPP");

//--------------------------------------------------------------------------------
// getVendorName(conn, vendorKey)
//--------------------------------------------------------------------------------
  public String getVendorName(Connection conn,
                              Integer vendorKey)
   throws Exception, SQLException {

    CallableStatement cStmt = null;
    String vendorName = "";

    try
    {
      cStmt = conn.prepareCall("{call GetVendorName(?,?)}");
      cStmt.setInt(1, vendorKey);
      cStmt.execute();

      cat.debug(this.getClass().getName() + ": vendorName : " + cStmt.getString("vendorName"));

      vendorName = cStmt.getString("vendorName");
    }
    catch (SQLException e) {
      cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
      throw new Exception("getVendorName() " + e.getMessage());
    }
    finally
    {
      try
      {
        cStmt.close();
      }
      catch (SQLException e) {
        cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
        throw new Exception("getVendorName() " + e.getMessage());
      }
    }
    return vendorName;
  }

//--------------------------------------------------------------------------------
// getVendorNameForVI(conn, viKey)
//--------------------------------------------------------------------------------
  public String getVendorNameForVI(Connection conn,
                                   Integer viKey)
   throws Exception, SQLException {

    CallableStatement cStmt = null;
    String vendorName = "";

    try
    {
      cat.debug(this.getClass().getName() + ": viKey : " + viKey);
      cStmt = conn.prepareCall("{call GetVendorNameForVI(?,?)}");
      cStmt.setInt(1, viKey);
      cStmt.execute();

      cat.debug(this.getClass().getName() + ": vendorName : " + cStmt.getString("vendorName"));

      vendorName = cStmt.getString("vendorName");
    }
    catch (SQLException e) {
      cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
      throw new Exception("getVendorNameForVI() " + e.getMessage());

    }
    finally
    {
      try
      {
        cStmt.close();
      }
      catch (SQLException e) {
        cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
        throw new Exception("getVendorNameForVI() " + e.getMessage());
      }
    }
    return vendorName;
  }

  public Boolean isClinicAccount(Connection conn,
                                 Integer vendorKey)
   throws Exception, SQLException {

    CallableStatement cStmt = null;
    Boolean vendorClinicInd = false;

    try
    {
      cat.debug(this.getClass().getName() + ": vendorKey : " + vendorKey);
      cStmt = conn.prepareCall("{call GetVendorClinicInd(?,?)}");
      cStmt.setInt(1, vendorKey);
      cStmt.execute();

      cat.debug(this.getClass().getName() + ": vendorClinicInd : " + cStmt.getBoolean("vendorClinicInd"));

      vendorClinicInd = cStmt.getBoolean("vendorClinicInd");
    }
    catch (SQLException e) {
      cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
      throw new Exception("GetVendorClinicInd() " + e.getMessage());
    }
    finally
    {
      try
      {
        cStmt.close();
      }
      catch (SQLException e) {
        cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
        throw new Exception("GetVendorClinicInd() " + e.getMessage());
      }
    }
    return vendorClinicInd;
  }

  public Boolean isTrackResultSupported(Connection conn,
                                        Integer vendorKey)
   throws Exception, SQLException {

    CallableStatement cStmt = null;
    Boolean hasTrackResultSupport = false;

    try
    {
      cat.debug(this.getClass().getName() + ": vendorKey : " + vendorKey);
      cStmt = conn.prepareCall("{call GetVendorTrackResultInd(?,?)}");
      cStmt.setInt(1, vendorKey);
      cStmt.execute();

      cat.debug(this.getClass().getName() + ": trackResultInd : " + cStmt.getBoolean("trackResultInd"));

      hasTrackResultSupport = cStmt.getBoolean("trackResultInd");
    }
    catch (SQLException e) {
      cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
      throw new Exception("getVendorTrackResultInd() " + e.getMessage());
    }
    finally
    {
      try
      {
        cStmt.close();
      }
      catch (SQLException e) {
        cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
        throw new Exception("getVendorTrackResultInd() " + e.getMessage());
      }
    }
    return hasTrackResultSupport;
  }


  public Boolean isWebServiceSupported(Connection conn,
                                       Integer vendorKey)
   throws Exception, SQLException {

    CallableStatement cStmt = null;
    Boolean hasWebServiceSupport = false;

    try
    {
      cat.debug(this.getClass().getName() + ": vendorKey : " + vendorKey);
      cStmt = conn.prepareCall("{call GetVendorWebServiceInd(?,?)}");
      cStmt.setInt(1, vendorKey);
      cStmt.execute();

      cat.debug(this.getClass().getName() + ": webServiceInd : " + cStmt.getBoolean("webServiceInd"));

      hasWebServiceSupport = cStmt.getBoolean("webServiceInd");
    }
    catch (SQLException e) {
      cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
      throw new Exception("getVendorWebServiceInd() " + e.getMessage());
    }
    finally
    {
      try
      {
        cStmt.close();
      }
      catch (SQLException e) {
        cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
        throw new Exception("getVendorWebServiceInd() " + e.getMessage());
      }
    }
    return hasWebServiceSupport;
  }


  public Integer GetVendorKeyForTrackResult(Connection conn)
   throws Exception, SQLException {

    CallableStatement cStmt = null;
    Integer vendorKey = null;

    try
    {
      cStmt = conn.prepareCall("{call GetVendorKeyForTrackResult(?)}");
      cStmt.execute();

      cat.debug(this.getClass().getName() + ": vendorKey : " + cStmt.getInt("vendorKey"));
      vendorKey = cStmt.getInt("vendorKey");
    }
    catch (SQLException e) {
      cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
      throw new Exception("getVendorKeyForTrackResult() " + e.getMessage());
    }
    finally
    {
      try
      {
        cStmt.close();
      }
      catch (SQLException e) {
        cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
        throw new Exception("getVendorKeyForTrackResult() " + e.getMessage());
      }
    }
    return vendorKey;
  }


//--------------------------------------------------------------------------------
// getItemCountByVendor(conn, vendorKey)
//--------------------------------------------------------------------------------
  public Integer getItemCountByVendor(Connection conn,
                                      Integer vendorKey)
   throws Exception, SQLException {

    CallableStatement cStmt = null;
    Integer vendorItemCount = 0;

    try
    {
      cStmt = conn.prepareCall("{call GetItemCountByVendor(?,?)}");
      cStmt.setInt(1, vendorKey);
      cStmt.execute();

      cat.debug(this.getClass().getName() + ": vendorItemCount : " + cStmt.getInt("vendorItemCount"));
      vendorItemCount = cStmt.getInt("vendorItemCount");
    }
    catch (SQLException e) {
      cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
      throw new Exception("getItemCountByVendor() " + e.getMessage());
    }
    finally
    {
      try
      {
        cStmt.close();
      }
      catch (SQLException e) {
        cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
        throw new Exception("getItemCountByVendor() " + e.getMessage());
      }
    }
    return vendorItemCount;
  }
//--------------------------------------------------------------------------------
// getServiceCountByVendor(conn, vendorKey)
//--------------------------------------------------------------------------------
  public Integer getServiceCountByVendor(Connection conn,
                                         Integer vendorKey)
   throws Exception, SQLException {

    CallableStatement cStmt = null;
    Integer vendorServiceCount = 0;

    try
    {
      cStmt = conn.prepareCall("{call GetServiceCountByVendor(?,?)}");
      cStmt.setInt(1, vendorKey);
      cStmt.execute();

      cat.debug(this.getClass().getName() + ": vendorServiceCount : " + cStmt.getInt("vendorServiceCount"));
      vendorServiceCount = cStmt.getInt("vendorServiceCount");
    }
    catch (SQLException e) {
      cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
      throw new Exception("getServiceCountByVendor() " + e.getMessage());
    }
    finally
    {
      try
      {
        cStmt.close();
      }
      catch (SQLException e) {
        cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
        throw new Exception("getServiceCountByVendor() " + e.getMessage());
      }
    }
    return vendorServiceCount;
  }

  public Integer getLicenseCount(Connection conn,
                                 Byte sourceTypeKey,
                                 Integer sourceKey)
   throws Exception, SQLException {

    CallableStatement cStmt = null;
    Integer licenseCnt = null;

    try
    {
//--------------------------------------------------------------------------------
// Call a procedure with two IN parameters and one OUT parameter.
//--------------------------------------------------------------------------------
      cStmt = conn.prepareCall("{call GetLicenseCount(?,?,?)}");

//--------------------------------------------------------------------------------
// Set the value for the IN parameter
//--------------------------------------------------------------------------------
      cStmt.setByte(1, sourceTypeKey);
      cStmt.setInt(2, sourceKey);
      cStmt.execute();

      cat.debug(this.getClass().getName() + ": licenseCnt : " + cStmt.getInt("licenseCnt"));
      licenseCnt = cStmt.getInt("licenseCnt");
    }
    catch (SQLException e) {
      cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
      throw new Exception("getLicenseCount() " + e.getMessage());
    }
    finally
    {
      try
      {
        cStmt.close();
      }
      catch (SQLException e) {
        cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
        throw new Exception("getLicenseCount() " + e.getMessage());
      }
    }
    return licenseCnt;
  }


  public HashMap getLicenseAssignedCount(Connection conn,
                                         Byte invoiceTypeKey,
                                         Integer invoiceKey)
   throws Exception, SQLException {

    CallableStatement cStmt = null;
    HashMap licenseMap = new HashMap();

    try
    {
//--------------------------------------------------------------------------------
// Call a procedure with two IN parameters and 1 OUT parameter.
//--------------------------------------------------------------------------------
      cStmt = conn.prepareCall("{call GetLicenseAssignedCount(?,?,?,?)}");

//--------------------------------------------------------------------------------
// Set the value for the IN parameter
//--------------------------------------------------------------------------------
      cStmt.setByte(1, invoiceTypeKey);
      cStmt.setInt(2, invoiceKey);
      cStmt.execute();

      cat.debug(this.getClass().getName() + ": licenseCnt.: " + cStmt.getInt("licenseCnt"));
      cat.debug(this.getClass().getName() + ": orderQty...: " + cStmt.getInt("orderQty"));
      licenseMap.put("licenseCnt", cStmt.getInt("licenseCnt"));
      licenseMap.put("orderQty", cStmt.getInt("orderQty"));
    }
    catch (SQLException e) {
      cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
      throw new Exception("getLicenseAssignedCount() " + e.getMessage());
    }
    finally
    {
      try
      {
        cStmt.close();
      }
      catch (SQLException e) {
        cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
        throw new Exception("getLicenseAssignedCount() " + e.getMessage());
      }
    }
    return licenseMap;
  }
  
}