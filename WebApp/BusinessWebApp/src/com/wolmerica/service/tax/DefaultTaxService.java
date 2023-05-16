package com.wolmerica.service.tax;

import java.sql.Connection;
import java.sql.CallableStatement;
import java.sql.SQLException;
import java.math.BigDecimal;

import org.apache.log4j.Logger;

/**
 * @author  Richard Wolschlager
 * @date    July 31, 2010
 */

public class DefaultTaxService implements TaxService {

  Logger cat = Logger.getLogger("WOWAPP");


  public BigDecimal getTaxRate(Connection conn,
                               Byte taxMarkUpKey)
   throws Exception, SQLException {

    CallableStatement cStmt = null;
    BigDecimal taxRate = new BigDecimal("0");

    try
    {
//--------------------------------------------------------------------------------
// Call a procedure with one IN parameters and one OUT parameter.
//--------------------------------------------------------------------------------
      cStmt = conn.prepareCall("{call GetTaxRate(?,?)}");

//--------------------------------------------------------------------------------
// Set the value for the IN parameter
//--------------------------------------------------------------------------------
      cStmt.setByte(1, taxMarkUpKey);

//--------------------------------------------------------------------------------
// Execute the stored procedure
//--------------------------------------------------------------------------------
      cStmt.execute();

//--------------------------------------------------------------------------------
// debug messages if necessary.
//--------------------------------------------------------------------------------
      cat.debug(this.getClass().getName() + ": taxRate : " + cStmt.getBigDecimal("taxRate"));

//--------------------------------------------------------------------------------
// Retrieve the return values
//--------------------------------------------------------------------------------
      taxRate = cStmt.getBigDecimal("taxRate");
    }
    catch (SQLException e) {
      cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
      throw new Exception("getTaxRate() " + e.getMessage());
    }
    finally
    {
      try
      {
        cStmt.close();
      }
      catch (SQLException e) {
        cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
        throw new Exception("getTaxRate() " + e.getMessage());
      }
    }
    return taxRate;
  }
  
}