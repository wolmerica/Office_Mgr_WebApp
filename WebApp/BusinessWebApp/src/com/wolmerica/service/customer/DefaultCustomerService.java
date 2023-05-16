package com.wolmerica.service.customer;

import java.sql.Connection;
import java.sql.CallableStatement;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;

import org.apache.log4j.Logger;

/**
 * @author  Richard Wolschlager
 * @date    August 1, 2010
 */

public class DefaultCustomerService implements CustomerService {

  Logger cat = Logger.getLogger("WOWAPP");

//--------------------------------------------------------------------------------
// getClientName(conn, customerKey)
//--------------------------------------------------------------------------------
  public String getClientName(Connection conn,
                              Integer customerKey)
   throws Exception, SQLException {

    CallableStatement cStmt = null;
    String clientName = "";

    try
    {
      cStmt = conn.prepareCall("{call GetClientName(?,?)}");
      cStmt.setInt(1, customerKey);
      cStmt.execute();

      cat.debug(this.getClass().getName() + ": clientName : " + cStmt.getString("clientName"));

      clientName = cStmt.getString("clientName");
    }
    catch (SQLException e) {
      cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
      throw new Exception("getClientName() " + e.getMessage());
    }
    finally
    {
      try
      {
        cStmt.close();
      }
      catch (SQLException e) {
        cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
        throw new Exception("getClientName() " + e.getMessage());
      }
    }
    return clientName;
  }


//--------------------------------------------------------------------------------
// getLastServiceDate(conn, customerKey)
//--------------------------------------------------------------------------------
 public Date getLastServiceDate(Connection conn,
                                Integer cKey)
   throws Exception, SQLException {

    CallableStatement cStmt = null;
    Date serviceDate = null;

    try
    {
//--------------------------------------------------------------------------------
// Call a procedure with two IN parameters and one OUT parameter.
//--------------------------------------------------------------------------------
      cStmt = conn.prepareCall("{call GetCustomerLastServiceDate(?,?)}");
//--------------------------------------------------------------------------------
// Set the value for the IN parameter
//--------------------------------------------------------------------------------
      cStmt.setInt(1, cKey);
//--------------------------------------------------------------------------------
// Execute the stored procedure
//--------------------------------------------------------------------------------
      cStmt.execute();
//--------------------------------------------------------------------------------
// debug messages if necessary.
//--------------------------------------------------------------------------------
      serviceDate = cStmt.getDate("serviceDate");
      cat.debug(this.getClass().getName() + ": serviceDate : " + serviceDate.toString());

    }
    catch (SQLException e) {
      cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
      throw new Exception("getCustomerLastServiceDate() " + e.getMessage());
    }
    finally
    {
      try
      {
        cStmt.close();
      }
      catch (SQLException e) {
        cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
        throw new Exception("getCustomerLastServiceDate() " + e.getMessage());
      }
    }
    return serviceDate;
  }

//--------------------------------------------------------------------------------
// getCustomerEmail(conn, customerKey)
//--------------------------------------------------------------------------------
  public String getCustomerEmail(Connection conn,
                                 Byte reminderPrefKey,
                                 Integer customerKey)
   throws Exception, SQLException {

    CallableStatement cStmt = null;
    String customerEmail = "";

    try
    {
      cStmt = conn.prepareCall("{call GetCustomerEmail(?,?,?)}");
      cStmt.setInt(1, customerKey);
      cStmt.execute();

      cat.debug(this.getClass().getName() + ": customerEmail  : " + cStmt.getString("customerEmail"));
      cat.debug(this.getClass().getName() + ": customerEmail2 : " + cStmt.getString("customerEmail2"));

      if (reminderPrefKey == 2)
        customerEmail = cStmt.getString("customerEmail");
      if (reminderPrefKey == 3)
        customerEmail = cStmt.getString("customerEmail2");
    }
    catch (SQLException e) {
      cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
      throw new Exception("getCustomerEmail() " + e.getMessage());
    }
    finally
    {
      try
      {
        cStmt.close();
      }
      catch (SQLException e) {
        cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
        throw new Exception("getCustomerEmail() " + e.getMessage());
      }
    }
    return customerEmail;
  }

//--------------------------------------------------------------------------------
// getCustomerAttributeToEntity(conn, attributeToEntityKey)
//--------------------------------------------------------------------------------
  public HashMap<String, Object> getCustomerAttributeToEntity(Connection conn,
                                              Integer customerKey)
   throws Exception, SQLException {

    CallableStatement cStmt = null;
    HashMap<String, Object> attributeToEntityMap = new HashMap<String, Object>();

    try
    {
      cStmt = conn.prepareCall("{call GetCustomerAttributeToEntity(?,?,?)}");
      cStmt.setInt(1, customerKey);
      cStmt.execute();

      cat.debug(this.getClass().getName() + ": attributeToEntityKey  : " + cStmt.getByte("attributeToEntityKey"));
      cat.debug(this.getClass().getName() + ": attributeToEntityName : " + cStmt.getString("attributeToEntityName"));
      attributeToEntityMap.put("key", cStmt.getByte("attributeToEntityKey"));
      attributeToEntityMap.put("name", cStmt.getString("attributeToEntityName"));
    }
    catch (SQLException e) {
      cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
      throw new Exception("getCustomerAttributeToEntity() " + e.getMessage());
    }
    finally
    {
      try
      {
        cStmt.close();
      }
      catch (SQLException e) {
        cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
        throw new Exception("getCustomerAttributeToEntity() " + e.getMessage());
      }
    }
    return attributeToEntityMap;
  }
  
  public boolean setAdjustmentIndicator(Connection conn,
                                        Integer ciKey,
                                        String updateUser,
                                        boolean updateId)
   throws Exception, SQLException {


    CallableStatement cStmt = null;
    boolean allowAdjustmentId = true;

    try
    {
//--------------------------------------------------------------------------------
// Call a procedure with three IN parameters and one OUT parameters.
//--------------------------------------------------------------------------------
      cStmt = conn.prepareCall("{call SetCustomerInvoiceAdjustment(?,?,?,?)}");

//--------------------------------------------------------------------------------
// Set the value for the IN parameter
//--------------------------------------------------------------------------------
      cStmt.setInt(1, ciKey);
      cStmt.setString(2, updateUser);
      cStmt.setBoolean(3, updateId);
//--------------------------------------------------------------------------------
// Execute the stored procedure
//--------------------------------------------------------------------------------
      cStmt.execute();

//--------------------------------------------------------------------------------
// debug messages if necessary.
//--------------------------------------------------------------------------------
      cat.debug(this.getClass().getName() + ": adjCnt : " + cStmt.getByte("adjCnt"));

//--------------------------------------------------------------------------------
// Retrieve the return values
//--------------------------------------------------------------------------------
      if (cStmt.getByte("adjCnt") < 1)
        allowAdjustmentId = false;
    }
    catch (SQLException e) {
      cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
      throw new Exception("setAdjustmentIndicator() " + e.getMessage());
    }
    finally
    {
      try
      {
        cStmt.close();
      }
      catch (SQLException e) {
        cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
        throw new Exception("setAdjustmentIndicator() " + e.getMessage());
      }
    }
    return allowAdjustmentId;
  }
  
}