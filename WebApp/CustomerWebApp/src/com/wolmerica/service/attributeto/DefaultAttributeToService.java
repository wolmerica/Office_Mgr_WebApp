package com.wolmerica.service.attributeto;

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

public class DefaultAttributeToService implements AttributeToService {

  Logger cat = Logger.getLogger("WOWAPP");

//--------------------------------------------------------------------------------
// getAttributeToName(conn, sourceTypeKey, sourceKey)
//--------------------------------------------------------------------------------
  public HashMap<String,Object> getAttributeToName(Connection conn,
                                    Byte sourceTypeKey,
                                    Integer sourceKey)
   throws Exception, SQLException {

    CallableStatement cStmt = null;
    HashMap<String,Object> nameMap = new HashMap<String,Object>();

    try
    {
      cStmt = conn.prepareCall("{call GetAttributeToName(?,?,?,?,?)}");
      cStmt.setByte(1, sourceTypeKey);
      cStmt.setInt(2, sourceKey);
      cStmt.execute();

      cat.debug(this.getClass().getName() + ": sourceName.....: " + cStmt.getString("sourceName"));
      cat.debug(this.getClass().getName() + ": attributeToName: " + cStmt.getString("attributeToName"));
      cat.debug(this.getClass().getName() + ": customerKey....: " + cStmt.getString("customerKey"));

      nameMap.put("sourceName", cStmt.getString("sourceName"));
      nameMap.put("attributeToName", cStmt.getString("attributeToName"));
      nameMap.put("customerKey", cStmt.getInt("customerKey"));
    }
    catch (SQLException e) {
      cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
      throw new Exception("getAttributeToName() " + e.getMessage());
    }
    finally
    {
      try
      {
        cStmt.close();
      }
      catch (SQLException e) {
        cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
        throw new Exception("getAttributeToName() " + e.getMessage());
      }
    }
    return nameMap;
  }

//--------------------------------------------------------------------------------
// getLastServiceDate(conn, customerKey, sourceTypeKey, sourceKey)
//--------------------------------------------------------------------------------
 public Date getLastServiceDate(Connection conn,
                                 Integer cKey,
                                 Byte stKey,
                                 Integer sKey)
   throws Exception, SQLException {

    CallableStatement cStmt = null;
    Date serviceDate = null;

    try
    {
//--------------------------------------------------------------------------------
// Call a procedure with two IN parameters and one OUT parameter.
//--------------------------------------------------------------------------------
      cStmt = conn.prepareCall("{call GetAttributeToLastServiceDate(?,?,?,?)}");
//--------------------------------------------------------------------------------
// Set the value for the IN parameter
//--------------------------------------------------------------------------------
      cStmt.setInt(1, cKey);
      cStmt.setByte(2, stKey);
      cStmt.setInt(3, sKey);
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
      throw new Exception("getAttributeToLastServiceDate() " + e.getMessage());
    }
    finally
    {
      try
      {
        cStmt.close();
      }
      catch (SQLException e) {
        cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
        throw new Exception("getAttributeToLastServiceDate() " + e.getMessage());
      }
    }
    return serviceDate;
  }

//--------------------------------------------------------------------------------
// getAttributeToAgeCode(conn, sourceTypeKey, sourceKey)
//--------------------------------------------------------------------------------
  public String getAttributeToAgeCode(Connection conn,
                                      Byte sourceTypeKey,
                                      Integer sourceKey)
   throws Exception, SQLException {

    CallableStatement cStmt = null;
    String ageCode = "";

    try
    {
      cStmt = conn.prepareCall("{call GetAttributeToAgeCode(?,?,?)}");
      cStmt.setByte(1, sourceTypeKey);
      cStmt.setInt(2, sourceKey);
      cStmt.execute();

      cat.debug(this.getClass().getName() + ": ageCode....: " + cStmt.getString("ageCode"));
      ageCode = cStmt.getString("ageCode");
    }
    catch (SQLException e) {
      cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
      throw new Exception("getAttributeToAgeCode() " + e.getMessage());
    }
    finally
    {
      try
      {
        cStmt.close();
      }
      catch (SQLException e) {
        cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
        throw new Exception("getAttributeToAgeCode() " + e.getMessage());
      }
    }
    return ageCode;
  }
  
}