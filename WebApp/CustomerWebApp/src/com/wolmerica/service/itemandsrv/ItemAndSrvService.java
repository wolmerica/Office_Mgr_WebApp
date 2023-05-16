package com.wolmerica.service.itemandsrv;

import com.wolmerica.bundledetail.BundleDetailDO;
import com.wolmerica.itemdictionary.ItemDictionaryDO;
import com.wolmerica.itemdictionary.ItemDictionaryListDO;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author  Richard Wolschlager
 * @date    August 1, 2010
 */

public interface ItemAndSrvService {

  public Integer getItemOrServiceKey(Connection conn,
                                     Byte sourceTypeKey,
                                     Integer priceByKey)
  throws Exception, SQLException;

  public BundleDetailDO getItemOrServiceName(Connection conn,
                                             BundleDetailDO formDO)
  throws Exception, SQLException;

  public ItemDictionaryDO getItemInventory(Connection conn,
                                           ItemDictionaryDO formDO,
                                           Integer idForecastDays)
  throws Exception, SQLException;

  public int getRecommendedPOQuantity(Integer itemDictionaryKey,
                                      int onHandQty,
                                      int orderedQty,
                                      int forecastQty,
                                      int thresholdQty)
  throws Exception;

  public ItemDictionaryListDO getItemAvailability(Connection conn,
                                                  Integer itemDictionaryKey,
                                                  ItemDictionaryListDO formDO)
  throws Exception, SQLException;

  public Boolean getItemBookedForEvent(Connection conn,
                                       Integer scheduleKey,
                                       Integer itemDictionaryKey)

  throws Exception, SQLException;

}