package com.wolmerica.service.speciesbreed;

import java.sql.Connection;
import java.sql.CallableStatement;
import java.sql.SQLException;
import java.util.HashMap;

import org.apache.log4j.Logger;

/**
 * @author  Richard Wolschlager
 * @date    July 31, 2010
 */

public class DefaultSpeciesBreedService implements SpeciesBreedService {

  Logger cat = Logger.getLogger("WOWAPP");
  
//--------------------------------------------------------------------------------
// getSpeciesName(conn, speciesKey)
//--------------------------------------------------------------------------------
  public String getSpeciesName(Connection conn,
                               Integer speciesKey)
   throws Exception, SQLException {

    CallableStatement cStmt = null;
    String speciesName = "";

    try
    {
      cStmt = conn.prepareCall("{call GetSpeciesName(?,?)}");
      cStmt.setInt(1, speciesKey);
      cStmt.execute();

      cat.debug(this.getClass().getName() + ": speciesName : " + cStmt.getString("speciesName"));
      speciesName = cStmt.getString("speciesName");
    }
    catch (SQLException e) {
      cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
      throw new Exception("getSpeciesName() " + e.getMessage());
    }
    finally
    {
      try
      {
        cStmt.close();
      }
      catch (SQLException e) {
        cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
        throw new Exception("getSpeciesName() " + e.getMessage());
      }
    }
    return speciesName;
  }

//--------------------------------------------------------------------------------
// getSpeciesKeyByName(conn, speciesName)
//--------------------------------------------------------------------------------
  public Integer getSpeciesKeyByName(Connection conn,
                                    String speciesName)
   throws Exception, SQLException {

    CallableStatement cStmt = null;
    Integer speciesKey = null;

    try
    {
      cStmt = conn.prepareCall("{call GetSpeciesKeyByName(?,?)}");
      cStmt.setString(1, speciesName);
      cStmt.execute();

      cat.debug(this.getClass().getName() + ": speciesKey : " + cStmt.getInt("speciesKey"));
      speciesKey = cStmt.getInt("speciesKey");
    }
    catch (SQLException e) {
      cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
      throw new Exception("getSpeciesKeyByName() " + e.getMessage());
    }
    finally
    {
      try
      {
        cStmt.close();
      }
      catch (SQLException e) {
        cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
        throw new Exception("getSpeciesKeyByName() " + e.getMessage());
      }
    }
    return speciesKey;
  }

//--------------------------------------------------------------------------------
// getBreedName(conn, breedKey)
//--------------------------------------------------------------------------------
  public String getBreedName(Connection conn,
                             Integer breedKey)
   throws Exception, SQLException {

    CallableStatement cStmt = null;
    String breedName = "";

    try
    {
      cStmt = conn.prepareCall("{call GetBreedName(?,?)}");
      cStmt.setInt(1, breedKey);
      cStmt.execute();

      cat.debug(this.getClass().getName() + ": breedName : " + cStmt.getString("breedName"));

      breedName = cStmt.getString("breedName");
    }
    catch (SQLException e) {
      cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
      throw new Exception("getBreedName() " + e.getMessage());
    }
    finally
    {
      try
      {
        cStmt.close();
      }
      catch (SQLException e) {
        cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
        throw new Exception("getBreedName() " + e.getMessage());
      }
    }
    return breedName;
  }

//--------------------------------------------------------------------------------
// getBreedKeyByName(conn, breedName)
//--------------------------------------------------------------------------------
  public Integer getBreedKeyByName(Connection conn,
                                    String breedName)
   throws Exception, SQLException {

    CallableStatement cStmt = null;
    Integer breedKey = null;

    try
    {
      cStmt = conn.prepareCall("{call GetBreedKeyByName(?,?)}");
      cStmt.setString(1, breedName);
      cStmt.execute();

      cat.debug(this.getClass().getName() + ": breedKey : " + cStmt.getInt("breedKey"));
      breedKey = cStmt.getInt("breedKey");
    }
    catch (SQLException e) {
      cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
      throw new Exception("getBreedKeyByName() " + e.getMessage());
    }
    finally
    {
      try
      {
        cStmt.close();
      }
      catch (SQLException e) {
        cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
        throw new Exception("getBreedKeyByName() " + e.getMessage());
      }
    }
    return breedKey;
  }

//--------------------------------------------------------------------------------
// GetPetSexAndNeuterIdByCode(conn, sexCode)
//--------------------------------------------------------------------------------
  public HashMap<String, String> GetSexAndNeuterIdByCode(Connection conn,
                                         String sexCode)
   throws Exception, SQLException {

    CallableStatement cStmt = null;
    HashMap<String, String> sexNeuteredMap = new HashMap<String, String>();

    try
    {
      cStmt = conn.prepareCall("{call GetSexAndNeuteredIdByCode(?,?,?)}");
      cStmt.setString(1, sexCode);
      cStmt.execute();

      cat.debug(this.getClass().getName() + ": sexId.....: " + cStmt.getString("sexId"));
      cat.debug(this.getClass().getName() + ": neuteredId..: " + cStmt.getString("neuteredId"));
      sexNeuteredMap.put("sexId", cStmt.getString("sexId"));
      sexNeuteredMap.put("neuteredId", cStmt.getString("neuteredId"));
    }
    catch (SQLException e) {
      cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
      throw new Exception("getSexAndNeuteredIdByCode() " + e.getMessage());
    }
    finally
    {
      try
      {
        cStmt.close();
      }
      catch (SQLException e) {
        cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
        throw new Exception("getSexAndNeuteredIdByCode() " + e.getMessage());
      }
    }
    return sexNeuteredMap;
  }

}