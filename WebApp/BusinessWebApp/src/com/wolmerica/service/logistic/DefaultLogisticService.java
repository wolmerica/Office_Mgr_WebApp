package com.wolmerica.service.logistic;

import java.sql.Connection;
import java.sql.CallableStatement;
import java.sql.SQLException;

import org.apache.log4j.Logger;

/**
 * @author  Richard Wolschlager
 * @date    August 2, 2010
 */

public class DefaultLogisticService implements LogisticService {

  Logger cat = Logger.getLogger("WOWAPP");

  public Integer getLogisticsCount(Connection conn,
                                   Byte sourceTypeKey,
                                   Integer sourceKey)
   throws Exception, SQLException {

    CallableStatement cStmt = null;
    Integer logisticsCnt = null;

    try
    {
//--------------------------------------------------------------------------------
// Call a procedure with two IN parameters and seventeen OUT parameters.
//--------------------------------------------------------------------------------
      cStmt = conn.prepareCall("{call GetLogisticsCount(?,?,?)}");

//--------------------------------------------------------------------------------
// Set the value for the IN parameter
//--------------------------------------------------------------------------------
      cStmt.setByte(1, sourceTypeKey);
      cStmt.setInt(2, sourceKey);

//--------------------------------------------------------------------------------
// Execute the stored procedure
//--------------------------------------------------------------------------------
      cStmt.execute();

//--------------------------------------------------------------------------------
// debug messages if necessary.
//--------------------------------------------------------------------------------
      cat.debug(this.getClass().getName() + ": logisticsCnt : " + cStmt.getInt("logisticsCnt"));

//--------------------------------------------------------------------------------
// Retrieve the return values
//--------------------------------------------------------------------------------
      logisticsCnt = cStmt.getInt("logisticsCnt");
    }
    catch (SQLException e) {
      cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
      throw new Exception("getLogisticsCount() " + e.getMessage());
    }
    finally
    {
      try
      {
        cStmt.close();
      }
      catch (SQLException e) {
        cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
        throw new Exception("getLogisticsCount() " + e.getMessage());
      }
    }
    return logisticsCnt;
  }

}