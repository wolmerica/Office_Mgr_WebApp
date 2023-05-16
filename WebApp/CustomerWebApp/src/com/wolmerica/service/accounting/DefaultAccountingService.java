package com.wolmerica.service.accounting;

import java.sql.Connection;
import java.sql.CallableStatement;
import java.sql.SQLException;
import java.util.HashMap;

import org.apache.log4j.Logger;

/**
 * @author  Richard Wolschlager
 * @date    July 31, 2010
 */

public class DefaultAccountingService implements AccountingService {

  Logger cat = Logger.getLogger("WOWAPP");

//--------------------------------------------------------------------------------
// getCustomerLedgerBalance(conn, customerKey)
//--------------------------------------------------------------------------------
  public HashMap<String, Object> getCustomerLedgerBalance(Connection conn,
                                       Integer customerKey)
   throws Exception, SQLException {

    CallableStatement cStmt = null;
    HashMap<String, Object> acctMap = new HashMap<String, Object>();

    try
    {
      cStmt = conn.prepareCall("{call GetCustomerLedgerBalance(?,?)}");
      cStmt.setInt(1, customerKey);
      cStmt.execute();

      cat.debug(this.getClass().getName() + ": ledgerBalanceAmt : " + cStmt.getInt("ledgerBalanceAmt"));
      acctMap.put("accountAmount", cStmt.getBigDecimal("ledgerBalanceAmt"));
    }
    catch (SQLException e) {
      cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
      throw new Exception("getCustomerLedgerBalance() " + e.getMessage());
    }
    finally
    {
      try
      {
        cStmt.close();
      }
      catch (SQLException e) {
        cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
        throw new Exception("getCustomerLedgerBalance() " + e.getMessage());
      }
    }
    return acctMap;
  }

//--------------------------------------------------------------------------------
// getCustomerLastPayment(conn, customerKey)
//--------------------------------------------------------------------------------
  public HashMap<String,Object> getCustomerLastPayment(Connection conn,
                                     Integer customerKey)
   throws Exception, SQLException {

    CallableStatement cStmt = null;
    HashMap<String,Object> acctMap = new HashMap<String,Object>();

    try
    {
      cStmt = conn.prepareCall("{call GetCustomerLastPayment(?,?,?)}");
      cStmt.setInt(1, customerKey);
      cStmt.execute();

      cat.debug(this.getClass().getName() + ": lastPaymentAmt : " + cStmt.getInt("lastPaymentAmt"));
      cat.debug(this.getClass().getName() + ": lastPaymentDate : " + cStmt.getDate("lastPaymentDate"));

      acctMap.put("lastPaymentAmt", cStmt.getBigDecimal("lastPaymentAmt"));
      acctMap.put("lastPaymentDate", cStmt.getDate("lastPaymentDate"));
    }
    catch (SQLException e) {
      cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
      throw new Exception("getCustomerLastPayment() " + e.getMessage());
    }
    finally
    {
      try
      {
        cStmt.close();
      }
      catch (SQLException e) {
        cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
        throw new Exception("getCustomerLastPayment() " + e.getMessage());
      }
    }
    return acctMap;
  }


//--------------------------------------------------------------------------------
// getCustomerBalance(conn, customerKey)
//--------------------------------------------------------------------------------
  public HashMap<String, Object> getCustomerBalance(Connection conn,
                                    Integer customerKey)
   throws Exception, SQLException {

    CallableStatement cStmt = null;
    HashMap<String, Object> acctMap = new HashMap<String, Object>();

    try
    {
      cStmt = conn.prepareCall("{call GetCustomerBalance(?,?,?)}");
      cStmt.setInt(1, customerKey);
      cStmt.execute();

      cat.debug(this.getClass().getName() + ": balanceAmt : " + cStmt.getInt("balanceAmt"));
      cat.debug(this.getClass().getName() + ": balanceDate : " + cStmt.getDate("balanceDate"));

      acctMap.put("balanceAmt", cStmt.getBigDecimal("balanceAmt"));
      acctMap.put("balanceDate", cStmt.getDate("balanceDate"));
    }
    catch (SQLException e) {
      cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
      throw new Exception("getCustomerBalance() " + e.getMessage());
    }
    finally
    {
      try
      {
        cStmt.close();
      }
      catch (SQLException e) {
        cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
        throw new Exception("getCustomerBalance() " + e.getMessage());
      }
    }
    return acctMap;
  }

//--------------------------------------------------------------------------------
// getCustomerAcctTranDueAmount(conn, customerKey, customerAcctKey)
//--------------------------------------------------------------------------------
  public HashMap<String, Object> getCustomerAcctTranDueAmount(Connection conn,
                                              Integer customerKey,
                                              Integer customerAcctKey)
   throws Exception, SQLException {

    CallableStatement cStmt = null;
    HashMap<String, Object> acctMap = new HashMap<String, Object>();

    try
    {
      cStmt = conn.prepareCall("{call GetCustomerAcctTranDueAmount(?,?,?,?)}");
      cStmt.setInt(1, customerKey);
      cStmt.setInt(2, customerAcctKey);
      cStmt.execute();

      cat.debug(this.getClass().getName() + ": customerAcctDueAmt : " + cStmt.getInt("balanceDueAmt"));
      cat.debug(this.getClass().getName() + ": daysOverDue....... : " + cStmt.getInt("daysOverDue"));

      acctMap.put("balanceDueAmt", cStmt.getBigDecimal("balanceDueAmt"));
      acctMap.put("daysOverDue", cStmt.getInt("daysOverDue"));
    }
    catch (SQLException e) {
      cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
      throw new Exception("getCustomerAcctTranDueAmount() " + e.getMessage());
    }
    finally
    {
      try
      {
        cStmt.close();
      }
      catch (SQLException e) {
        cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
        throw new Exception("getCustomerAcctTranDueAmount() " + e.getMessage());
      }
    }
    return acctMap;
  }

//--------------------------------------------------------------------------------
// getVendorLastPayment(conn, vendorKey)
//--------------------------------------------------------------------------------
  public HashMap<String, Object> getVendorLastPayment(Connection conn,
                                   Integer vendorKey)
   throws Exception, SQLException {

    CallableStatement cStmt = null;
    HashMap<String, Object> acctMap = new HashMap<String, Object>();

    try
    {
      cStmt = conn.prepareCall("{call GetVendorLastPayment(?,?,?)}");
      cStmt.setInt(1, vendorKey);
      cStmt.execute();

      cat.debug(this.getClass().getName() + ": lastPaymentAmt : " + cStmt.getInt("lastPaymentAmt"));
      cat.debug(this.getClass().getName() + ": lastPaymentDate : " + cStmt.getDate("lastPaymentDate"));

      acctMap.put("lastPaymentAmt", cStmt.getBigDecimal("lastPaymentAmt"));
      acctMap.put("lastPaymentDate", cStmt.getDate("lastPaymentDate"));
    }
    catch (SQLException e) {
      cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
      throw new Exception("getVendorLastPayment() " + e.getMessage());
    }
    finally
    {
      try
      {
        cStmt.close();
      }
      catch (SQLException e) {
        cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
        throw new Exception("getVendorLastPayment() " + e.getMessage());
      }
    }
    return acctMap;
  }


//--------------------------------------------------------------------------------
// getVendorBalance(conn, vendorKey)
//--------------------------------------------------------------------------------
  public HashMap<String, Object> getVendorBalance(Connection conn,
                                  Integer vendorKey)
   throws Exception, SQLException {

    CallableStatement cStmt = null;
    HashMap<String, Object> acctMap = new HashMap<String, Object>();

    try
    {
      cStmt = conn.prepareCall("{call GetVendorBalance(?,?,?)}");
      cStmt.setInt(1, vendorKey);
      cStmt.execute();

      cat.debug(this.getClass().getName() + ": balanceAmt : " + cStmt.getInt("balanceAmt"));
      cat.debug(this.getClass().getName() + ": balanceDate : " + cStmt.getDate("balanceDate"));

      acctMap.put("balanceAmt", cStmt.getBigDecimal("balanceAmt"));
      acctMap.put("balanceDate", cStmt.getDate("balanceDate"));
    }
    catch (SQLException e) {
      cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
      throw new Exception("getVendorBalance() " + e.getMessage());
    }
    finally
    {
      try
      {
        cStmt.close();
      }
      catch (SQLException e) {
        cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
        throw new Exception("getVendorBalance() " + e.getMessage());
      }
    }
    return acctMap;
  }

//--------------------------------------------------------------------------------
// getVendorAcctTranDueAmount(conn, vendorKey, vendorAcctKey)
//--------------------------------------------------------------------------------
  public HashMap<String, Object> getVendorAcctTranDueAmount(Connection conn,
                                            Integer vendorKey,
                                            Integer vendorAcctKey)
   throws Exception, SQLException {

    CallableStatement cStmt = null;
    HashMap<String, Object> acctMap = new HashMap<String, Object>();

    try
    {
      cStmt = conn.prepareCall("{call GetVendorAcctTranDueAmount(?,?,?,?)}");
      cStmt.setInt(1, vendorKey);
      cStmt.setInt(2, vendorAcctKey);
      cStmt.execute();

      cat.debug(this.getClass().getName() + ": vendorAcctDueAmt : " + cStmt.getInt("balanceDueAmt"));
      cat.debug(this.getClass().getName() + ": daysOverDue....... : " + cStmt.getInt("daysOverDue"));

      acctMap.put("balanceDueAmt", cStmt.getBigDecimal("balanceDueAmt"));
      acctMap.put("daysOverDue", cStmt.getInt("daysOverDue"));
    }
    catch (SQLException e) {
      cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
      throw new Exception("getVendorAcctTranDueAmount() " + e.getMessage());
    }
    finally
    {
      try
      {
        cStmt.close();
      }
      catch (SQLException e) {
        cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
        throw new Exception("getVendorAcctTranDueAmount() " + e.getMessage());
      }
    }
    return acctMap;
  }
}