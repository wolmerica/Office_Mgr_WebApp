package com.wolmerica.service.purchaseorder;

import java.sql.Connection;
import java.sql.CallableStatement;
import java.sql.SQLException;
import java.util.HashMap;

import org.apache.log4j.Logger;

/**
 * @author  Richard Wolschlager
 * @date    July 31, 2010
 */

public class DefaultPurchaseOrderService implements PurchaseOrderService {

  Logger cat = Logger.getLogger("WOWAPP");

  public HashMap<String, Object> getPurchaseOrderKeys(Connection conn,
                                      Integer poKey)
   throws Exception, SQLException {

    CallableStatement cStmt = null;
    HashMap<String, Object> pokMap = new HashMap<String, Object>();

    try {
//--------------------------------------------------------------------------------
// Call a procedure with two IN parameters and one OUT parameter.
//--------------------------------------------------------------------------------
      cStmt = conn.prepareCall("{call GetPurchaseOrderKeys(?,?,?,?,?,?)}");
//--------------------------------------------------------------------------------
// Set the value for the IN parameter
//--------------------------------------------------------------------------------
      cStmt.setInt(1, poKey);
//--------------------------------------------------------------------------------
// Execute the stored procedure
//--------------------------------------------------------------------------------
      cStmt.execute();
//--------------------------------------------------------------------------------
// debug messages if necessary.
//--------------------------------------------------------------------------------
      cat.debug(this.getClass().getName() + ": purchaseOrderNum.....: " + cStmt.getString("purchaseOrderNum"));
      cat.debug(this.getClass().getName() + ": vendorKey............: " + cStmt.getInt("vendorKey"));
      cat.debug(this.getClass().getName() + ": customerKey..........: " + cStmt.getInt("customerKey"));
      cat.debug(this.getClass().getName() + ": attributeToEntityKey.: " + cStmt.getByte("attributeToEntityKey"));
      cat.debug(this.getClass().getName() + ": attributeToKey.......: " + cStmt.getByte("attributeToKey"));
//--------------------------------------------------------------------------------
// Retrieve the vendor order form value to be returned
//--------------------------------------------------------------------------------
      pokMap.put("purchaseOrderNum", cStmt.getString("purchaseOrderNum"));
      pokMap.put("vendorKey", cStmt.getInt("vendorKey"));
      pokMap.put("customerKey", cStmt.getInt("customerKey"));
      pokMap.put("attributeToEntityKey", cStmt.getByte("attributeToEntityKey"));
      pokMap.put("attributeToKey", cStmt.getInt("attributeToKey"));
    }
    catch (SQLException e) {
      cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
      throw new Exception("getPurchaseOrderKeys() " + e.getMessage());
    }
    finally
    {
      try
      {
        cStmt.close();
      }
      catch (SQLException e) {
        cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
        throw new Exception("getPurchaseOrderKeys() " + e.getMessage());
      }
    }
    return pokMap;
  }


//--------------------------------------------------------------------------------
// getVendorOrderFormDetail(conn, purchaseOrderKey, sourceTypeKey,
//                          sourceKey, lookupCode)
//--------------------------------------------------------------------------------
  public String getVendorOrderFormDetail(Connection conn,
                                         Integer poKey,
                                         Byte stKey,
                                         Integer sKey,
                                         String lookupCode)
   throws Exception, SQLException {

    CallableStatement cStmt = null;
    String vofValue = "";

    try
    {
//--------------------------------------------------------------------------------
// Call a procedure with two IN parameters and one OUT parameter.
//--------------------------------------------------------------------------------
      cStmt = conn.prepareCall("{call GetVendorOrderFormDetail(?,?,?,?,?)}");
//--------------------------------------------------------------------------------
// Set the value for the IN parameter
//--------------------------------------------------------------------------------
      cStmt.setInt(1, poKey);
      cStmt.setByte(2, stKey);
      cStmt.setInt(3, sKey);
      cStmt.setString(4, lookupCode);
//--------------------------------------------------------------------------------
// Execute the stored procedure
//--------------------------------------------------------------------------------
      cStmt.execute();
//--------------------------------------------------------------------------------
// debug messages if necessary.
//--------------------------------------------------------------------------------
      cat.debug(this.getClass().getName() + ": vendorOrderFormValue : " + cStmt.getString("vendorOrderFormValue"));
//--------------------------------------------------------------------------------
// Retrieve the vendor order form value to be returned
//--------------------------------------------------------------------------------
      vofValue = cStmt.getString("vendorOrderFormValue");
    }
    catch (SQLException e) {
      cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
      throw new Exception("getVendorOrderFormDetail() " + e.getMessage());
    }
    finally
    {
      try
      {
        cStmt.close();
      }
      catch (SQLException e) {
        cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
        throw new Exception("getVendorOrderFormDetail() " + e.getMessage());
      }
    }
    return vofValue;
  }

}