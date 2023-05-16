package com.wolmerica.service.rebate;

import java.sql.Connection;
import java.sql.CallableStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

import org.apache.log4j.Logger;

/**
 * @author  Richard Wolschlager
 * @date    August 3, 2010
 */
public class DefaultRebateService implements RebateService {

  Logger cat = Logger.getLogger("WOWAPP");
  
//--------------------------------------------------------------------------------
// getRebateCountByItem(conn, idKey)
//--------------------------------------------------------------------------------
  public Short getRebateCountByItem(Connection conn,
                                    Integer idKey)
   throws Exception, SQLException {

    CallableStatement cStmt = null;
    Short rebateCount = new Short("0");

    try
    {
      cStmt = conn.prepareCall("{call GetRebateCountByItem(?,?)}");
      cStmt.setInt(1, idKey);
      cStmt.execute();

      cat.debug(this.getClass().getName() + ": rebateCountByItem() : " + cStmt.getShort("rebateCount"));

      rebateCount = cStmt.getShort("rebateCount");
    }
    catch (SQLException e) {
      cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
      throw new Exception("getRebateCountByItem() " + e.getMessage());
    }
    finally
    {
      try
      {
        cStmt.close();
      }
      catch (SQLException e) {
        cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
        throw new Exception("getRebateCountByItem() " + e.getMessage());
      }
    }
    return rebateCount;
  }

//--------------------------------------------------------------------------------
// getRebateInstanceCountByItem(conn, idKey)
//--------------------------------------------------------------------------------
  public Short getRebateInstanceCountByItem(Connection conn,
                                            Integer idKey)
   throws Exception, SQLException {

    CallableStatement cStmt = null;
    Short rebateInstanceCount = new Short("0");

    try
    {
      cStmt = conn.prepareCall("{call getRebateInstanceCountByItem(?,?)}");
      cStmt.setInt(1, idKey);
      cStmt.execute();

      cat.debug(this.getClass().getName() + ": rebateInstanceCountByItem() : " + cStmt.getShort("rebateInstanceCount"));

      rebateInstanceCount = cStmt.getShort("rebateInstanceCount");
    }
    catch (SQLException e) {
      cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
      throw new Exception("getRebateInstanceCountByItem() " + e.getMessage());
    }
    finally
    {
      try
      {
        cStmt.close();
      }
      catch (SQLException e) {
        cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
        throw new Exception("getRebateInstanceCountByItem() " + e.getMessage());
      }
    }
    return rebateInstanceCount;
  }

//--------------------------------------------------------------------------------
// getRebateCountByPO(conn, poKey, createStamp)
//--------------------------------------------------------------------------------
  public Short getRebateCountByPO(Connection conn,
                                  Integer poKey,
                                  Timestamp poCreateStamp)
   throws Exception, SQLException {

    CallableStatement cStmt = null;
    Short rebateCount = new Short("0");

    try
    {
      cStmt = conn.prepareCall("{call GetRebateCountByPO(?,?,?)}");
      cStmt.setInt(1, poKey);
      cStmt.setTimestamp(2, poCreateStamp);
      cStmt.execute();

      cat.debug(this.getClass().getName() + ": rebateCountByPO() : " + cStmt.getShort("rebateCount"));

      rebateCount = cStmt.getShort("rebateCount");
    }
    catch (SQLException e) {
      cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
      throw new Exception("getRebateCountByPO() " + e.getMessage());
    }
    finally
    {
      try
      {
        cStmt.close();
      }
      catch (SQLException e) {
        cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
        throw new Exception("getRebateCountByPO() " + e.getMessage());
      }
    }
    return rebateCount;
  }

//--------------------------------------------------------------------------------
// getRebateInstanceCountByPO(conn, poKey)
//--------------------------------------------------------------------------------
  public Short getRebateInstanceCountByPO(Connection conn,
                                          Integer poKey)

   throws Exception, SQLException {

    CallableStatement cStmt = null;
    Short rebateInstanceCount = new Short("0");

    try
    {
      cStmt = conn.prepareCall("{call GetRebateInstanceCountByPO(?,?)}");
      cStmt.setInt(1, poKey);
      cStmt.execute();

      cat.debug(this.getClass().getName() + ": rebateInstanceCountByPO() : " + cStmt.getShort("rebateInstanceCount"));

      rebateInstanceCount = cStmt.getShort("rebateInstanceCount");
    }
    catch (SQLException e) {
      cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
      throw new Exception("getRebateInstanceCountByPO() " + e.getMessage());
    }
    finally
    {
      try
      {
        cStmt.close();
      }
      catch (SQLException e) {
        cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
        throw new Exception("getRebateInstanceCountByPO() " + e.getMessage());
      }
    }
    return rebateInstanceCount;
  }

//--------------------------------------------------------------------------------
// getRebateInstanceCountByRebate(conn, poKey)
//--------------------------------------------------------------------------------
  public Short getRebateInstanceCountByRebate(Connection conn,
                                              Integer rebateKey)

   throws Exception, SQLException {

    CallableStatement cStmt = null;
    Short rebateInstanceCount = new Short("0");

    try
    {
      cStmt = conn.prepareCall("{call GetRebateInstanceCountByRebate(?,?)}");
      cStmt.setInt(1, rebateKey);
      cStmt.execute();

      cat.debug(this.getClass().getName() + ": rebateInstanceCountByRebate() : " + cStmt.getShort("rebateInstanceCount"));

      rebateInstanceCount = cStmt.getShort("rebateInstanceCount");
    }
    catch (SQLException e) {
      cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
      throw new Exception("getRebateInstanceCountByRebate() " + e.getMessage());
    }
    finally
    {
      try
      {
        cStmt.close();
      }
      catch (SQLException e) {
        cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
        throw new Exception("getRebateInstanceCountByRebate() " + e.getMessage());
      }
    }
    return rebateInstanceCount;
  }

}
