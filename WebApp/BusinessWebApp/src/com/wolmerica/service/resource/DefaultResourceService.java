package com.wolmerica.service.resource;

import java.sql.Connection;
import java.sql.CallableStatement;
import java.sql.SQLException;

import org.apache.log4j.Logger;

/**
 * @author  Richard Wolschlager
 * @date    August 3, 2010
 */
public class DefaultResourceService implements ResourceService {

  Logger cat = Logger.getLogger("WOWAPP");

//--------------------------------------------------------------------------------
// getResourceName(conn, resourceKey)
//--------------------------------------------------------------------------------
  public String getResourceName(Connection conn,
                                Integer resourceKey)
   throws Exception, SQLException {

    CallableStatement cStmt = null;
    String resourceName = "";

    try
    {
      cStmt = conn.prepareCall("{call GetResourceName(?,?)}");
      cStmt.setInt(1, resourceKey);
      cStmt.execute();

      cat.debug(this.getClass().getName() + ": resourceName : " + cStmt.getString("resourceName"));

      resourceName = cStmt.getString("resourceName");
    }
    catch (SQLException e) {
      cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
      throw new Exception("getResourceName() " + e.getMessage());
    }
    finally
    {
      try
      {
        cStmt.close();
      }
      catch (SQLException e) {
        cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
        throw new Exception("getResourceName() " + e.getMessage());
      }
    }
    return resourceName;
  }

}
