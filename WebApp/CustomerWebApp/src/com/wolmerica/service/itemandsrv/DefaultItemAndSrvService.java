package com.wolmerica.service.itemandsrv;

import com.wolmerica.service.vendor.VendorService;
import com.wolmerica.service.vendor.DefaultVendorService;
import com.wolmerica.bundledetail.BundleDetailDO;
import com.wolmerica.itemdictionary.ItemDictionaryDO;
import com.wolmerica.itemdictionary.ItemDictionaryListDO;

import java.sql.Connection;
import java.sql.CallableStatement;
import java.sql.SQLException;

import org.apache.log4j.Logger;

/**
 * @author  Richard Wolschlager
 * @date    August 1, 2010
 */

public class DefaultItemAndSrvService implements ItemAndSrvService {

  Logger cat = Logger.getLogger("WOWAPP");

  private VendorService VendorService = new DefaultVendorService();

  public VendorService getVendorService() {
      return VendorService;
  }

  public void setVendorService(VendorService VendorService) {
      this.VendorService = VendorService;
  }

//--------------------------------------------------------------------------------
// getItemOrServiceKey(conn, sourceTypeKey, priceByKey)
//--------------------------------------------------------------------------------
  public Integer getItemOrServiceKey(Connection conn,
                                     Byte sourceTypeKey,
                                     Integer priceByKey)
   throws Exception, SQLException {

    CallableStatement cStmt = null;
    Integer sourceKey = null;

    try
    {
//--------------------------------------------------------------------------------
// Call a procedure with one IN parameter
//--------------------------------------------------------------------------------
      cStmt = conn.prepareCall("{call GetItemOrServiceKey(?,?,?,?,?,?,?)}");

//--------------------------------------------------------------------------------
// Set the value for the IN parameter
//--------------------------------------------------------------------------------
      cStmt.setByte(1, sourceTypeKey);
      cStmt.setInt(2, priceByKey);

//--------------------------------------------------------------------------------
// Execute the stored procedure
//--------------------------------------------------------------------------------
      cStmt.execute();

//--------------------------------------------------------------------------------
// Debug messages if necessary.
//--------------------------------------------------------------------------------
      cat.debug(this.getClass().getName() + ": sourceKey....: " + cStmt.getInt("sourceKey"));
      cat.debug(this.getClass().getName() + ": priceTypeKey.: " + cStmt.getByte("priceTypeKey"));
      cat.debug(this.getClass().getName() + ": releaseId....: " + cStmt.getByte("releaseId"));
      cat.debug(this.getClass().getName() + ": thirdPartyId.: " + cStmt.getByte("thirdPartyId"));
      cat.debug(this.getClass().getName() + ": vendorKey....: " + cStmt.getInt("vendorKey"));
//--------------------------------------------------------------------------------
// Retrieve the return values
//--------------------------------------------------------------------------------
      sourceKey = cStmt.getInt("sourceKey");
    }
    catch (SQLException e) {
      cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
      throw new Exception("getItemOrServiceKey() " + e.getMessage());
    }
    finally
    {
      try
      {
        cStmt.close();
      }
      catch (SQLException e) {
        cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
        throw new Exception("getItemOrServiceKey() " + e.getMessage());
      }
    }
    return sourceKey;
  }


//--------------------------------------------------------------------------------
// getItemOrServiceName(conn, bundleDetailDO)
//--------------------------------------------------------------------------------
  public BundleDetailDO getItemOrServiceName(Connection conn,
                                             BundleDetailDO formDO)
   throws Exception, SQLException {

    CallableStatement cStmt = null;

    try
    {
//--------------------------------------------------------------------------------
// Call a procedure with one IN parameter
//--------------------------------------------------------------------------------
      cStmt = conn.prepareCall("{call GetItemOrServiceName(?,?,?,?,?,?,?,?,?,?,?)}");

//--------------------------------------------------------------------------------
// Set the value for the IN parameter
//--------------------------------------------------------------------------------
      cStmt.setByte(1, formDO.getSourceTypeKey());
      cStmt.setInt(2, formDO.getSourceKey());

//--------------------------------------------------------------------------------
// Execute the stored procedure
//--------------------------------------------------------------------------------
      cStmt.execute();

//--------------------------------------------------------------------------------
// Debug messages if necessary.
//--------------------------------------------------------------------------------
      cat.debug(this.getClass().getName() + ": sourceName....: " + cStmt.getString("sourceName"));
      cat.debug(this.getClass().getName() + ": sourceNum.....: " + cStmt.getString("sourceNum"));
      cat.debug(this.getClass().getName() + ": categoryName..: " + cStmt.getString("categoryName"));
      cat.debug(this.getClass().getName() + ": size..........: " + cStmt.getBigDecimal("size"));
      cat.debug(this.getClass().getName() + ": sizeUnit......: " + cStmt.getString("sizeUnit"));
      cat.debug(this.getClass().getName() + ": priceTypeKey..: " + cStmt.getByte("priceTypeKey"));
      cat.debug(this.getClass().getName() + ": priceTypeName.: " + cStmt.getString("priceTypeName"));
      cat.debug(this.getClass().getName() + ": computedPrice.: " + cStmt.getBigDecimal("computedPrice"));
      cat.debug(this.getClass().getName() + ": releaseId.....: " + cStmt.getBoolean("releaseId"));

//--------------------------------------------------------------------------------
// Retrieve the return values
//--------------------------------------------------------------------------------
      formDO.setSourceName(cStmt.getString("sourceName"));
      formDO.setSourceNum(cStmt.getString("sourceNum"));
      formDO.setCategoryName(cStmt.getString("categoryName"));
      formDO.setSize(cStmt.getBigDecimal("size"));
      formDO.setSizeUnit(cStmt.getString("sizeUnit"));
      formDO.setPriceTypeKey(cStmt.getByte("priceTypeKey"));
      formDO.setPriceTypeName(cStmt.getString("priceTypeName"));
      formDO.setComputedPrice(cStmt.getBigDecimal("computedPrice"));
      formDO.setReleaseId(cStmt.getBoolean("releaseId"));
    }
    catch (SQLException e) {
      cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
      throw new Exception("getItemOrServiceName() " + e.getMessage());
    }
    finally
    {
      try
      {
        cStmt.close();
      }
      catch (SQLException e) {
        cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
        throw new Exception("getItemOrServiceName() " + e.getMessage());
      }
    }
    return formDO;
  }

//--------------------------------------------------------------------------------
// getItemInventory(conn, itemDictionaryDO)
//--------------------------------------------------------------------------------
  public ItemDictionaryDO getItemInventory(Connection conn,
                                           ItemDictionaryDO formDO,
                                           Integer idForecastDays)
   throws Exception, SQLException {

    CallableStatement cStmt = null;

    try
    {
//--------------------------------------------------------------------------------
// Call a procedure with one IN parameter
//--------------------------------------------------------------------------------
      cStmt = conn.prepareCall("{call GetItemInventory(?,?,?,?,?,?,?)}");

//--------------------------------------------------------------------------------
// Set the value for the IN parameter
//--------------------------------------------------------------------------------
      cStmt.setInt(1, formDO.getKey());
      cStmt.setInt(2, idForecastDays);
      cStmt.setBigDecimal(3, formDO.getSize());

//--------------------------------------------------------------------------------
// Execute the stored procedure
//--------------------------------------------------------------------------------
      cStmt.execute();

//--------------------------------------------------------------------------------
// Debug messages if necessary.
//--------------------------------------------------------------------------------
      cat.debug(this.getClass().getName() + ": thresholdQty.: " + cStmt.getBigDecimal("thresholdQty"));
      cat.debug(this.getClass().getName() + ": inventoryQty.: " + cStmt.getBigDecimal("inventoryQty"));
      cat.debug(this.getClass().getName() + ": orderedQty...: " + cStmt.getBigDecimal("orderedQty"));
      cat.debug(this.getClass().getName() + ": forecastQty..: " + cStmt.getBigDecimal("forecastQty"));

//--------------------------------------------------------------------------------
// Retrieve the return values
//--------------------------------------------------------------------------------
      formDO.setOrderThreshold(cStmt.getBigDecimal("thresholdQty"));
      formDO.setQtyOnHand(cStmt.getBigDecimal("inventoryQty"));
      formDO.setOrderedQty(cStmt.getBigDecimal("orderedQty"));
      formDO.setForecastQty(cStmt.getBigDecimal("forecastQty"));
    }
    catch (SQLException e) {
      cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
      throw new Exception("getItemInventory() " + e.getMessage());
    }
    finally
    {
      try
      {
        cStmt.close();
      }
      catch (SQLException e) {
        cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
        throw new Exception("getItemInventory() " + e.getMessage());
      }
    }
    return formDO;
  }


//--------------------------------------------------------------------------------
// getRecommendedPOQuantity()
//--------------------------------------------------------------------------------
  public int getRecommendedPOQuantity(Integer itemDictionaryKey,
                                      int onHandQty,
                                      int orderedQty,
                                      int forecastQty,
                                      int thresholdQty)
   throws Exception {

    int projectedQty = 0;

//--------------------------------------------------------------------------------
// Calculate the projected value of (QtyOnHand + OrderedQty - ForecastQty)
//--------------------------------------------------------------------------------
    projectedQty = onHandQty + orderedQty - forecastQty;

//--------------------------------------------------------------------------------
// The threshold is greater than the projected value we need to order the difference.
//--------------------------------------------------------------------------------
    if (thresholdQty > projectedQty)
      projectedQty = thresholdQty - projectedQty;
    else
      projectedQty = 0;

    return projectedQty;
  }


  public ItemDictionaryListDO getItemAvailability(Connection conn,
                                                  Integer itemDictionaryKey,
                                                  ItemDictionaryListDO formDO)
    throws Exception, SQLException {

    CallableStatement cStmt = null;

    try
    {
//--------------------------------------------------------------------------------
// Call a procedure with two IN parameter
//--------------------------------------------------------------------------------
      cStmt = conn.prepareCall("{call GetItemAvailability(?,?,?,?,?,?)}");

//--------------------------------------------------------------------------------
// Set the value for the IN parameter
//--------------------------------------------------------------------------------
      cStmt.setInt(1, itemDictionaryKey);
      cStmt.setBigDecimal(2, formDO.getSize());

//--------------------------------------------------------------------------------
// Execute the stored procedure
//--------------------------------------------------------------------------------
      cStmt.execute();

//--------------------------------------------------------------------------------
// Debug messages if necessary.
//--------------------------------------------------------------------------------
      cat.debug(this.getClass().getName() + ": availableQty....: " + cStmt.getBigDecimal("availableQty"));
      cat.debug(this.getClass().getName() + ": sellableItemId..: " + cStmt.getBoolean("sellableItemId"));
      cat.debug(this.getClass().getName() + ": sourceTypeKey...: " + cStmt.getByte("sourceTypeKey"));
      cat.debug(this.getClass().getName() + ": sourceKey.......: " + cStmt.getInt("sourceKey"));

//--------------------------------------------------------------------------------
// Retrieve the return values
//--------------------------------------------------------------------------------
      formDO.setQtyOnHand(cStmt.getBigDecimal("availableQty").intValue());
      formDO.setSellableItemId(cStmt.getBoolean("sellableItemId"));
      if (cStmt.getString("sourceTypeKey") != null)
        formDO.setSourceTypeKey(cStmt.getByte("sourceTypeKey"));
      if (cStmt.getString("sourceKey") != null)
        formDO.setSourceKey(cStmt.getInt("sourceKey"));
    }
    catch (SQLException e) {
      cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
      throw new Exception("getItemAvailability() " + e.getMessage());
    }
    finally
    {
      try
      {
        cStmt.close();
      }
      catch (SQLException e) {
        cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
        throw new Exception("getItemAvailability() " + e.getMessage());
      }
    }
    return formDO;
  }


  public Boolean getItemBookedForEvent(Connection conn,
                                       Integer scheduleKey,
                                       Integer itemDictionaryKey)

    throws Exception, SQLException {

    CallableStatement cStmt = null;
    Boolean sellableItemId = false;

    try
    {
//--------------------------------------------------------------------------------
// Call a procedure with two IN parameter
//--------------------------------------------------------------------------------
      cStmt = conn.prepareCall("{call GetItemBookedForEvent(?,?,?)}");

//--------------------------------------------------------------------------------
// Set the value for the IN parameter
//--------------------------------------------------------------------------------
      cStmt.setInt(1, scheduleKey);
      cStmt.setInt(2, itemDictionaryKey);

//--------------------------------------------------------------------------------
// Execute the stored procedure
//--------------------------------------------------------------------------------
      cStmt.execute();

//--------------------------------------------------------------------------------
// Debug messages if necessary.
//--------------------------------------------------------------------------------
      cat.debug(this.getClass().getName() + ": dependencyCnt..: " + cStmt.getInt("dependencyCnt"));

//--------------------------------------------------------------------------------
// Retrieve the return values
//--------------------------------------------------------------------------------
      if (cStmt.getInt("dependencyCnt") == 0)
        sellableItemId = true;
    }
    catch (SQLException e) {
      cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
      throw new Exception("getItemBookedForEvent() " + e.getMessage());
    }
    finally
    {
      try
      {
        cStmt.close();
      }
      catch (SQLException e) {
        cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
        throw new Exception("getItemBookedForEvent() " + e.getMessage());
      }
    }
    return sellableItemId;
  }
  
}