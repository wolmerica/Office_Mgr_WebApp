/*
 * PriceTypeDropList.java
 *
 * Created on January 14, 2008, 12:16 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package com.wolmerica.pricetype;

/**
 *
 * @author Richard
 */
import com.wolmerica.employee.EmployeesActionMapping;
import com.wolmerica.tools.formatter.FormattingException;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.ArrayList;

import org.apache.log4j.Logger;

public class PriceTypeDropList {

  Logger cat = Logger.getLogger("WOWAPP");

  public ArrayList getServicePriceTypeList(Connection conn)
   throws Exception, SQLException {

    PriceTypeDO priceTypeRow = null;
    ArrayList<PriceTypeDO> priceTypeRows = new ArrayList<PriceTypeDO>();

    PreparedStatement ps = null;
    ResultSet rs = null;

    try {
//-----------------------------------------------------------------------------
// Retrieve the service related price type values
//-----------------------------------------------------------------------------
      String query = "SELECT thekey, name, "
                   + "precedence, active_id "
                   + "FROM pricetype "
                   + "WHERE active_id "
                   + "AND domain_id = 1 "
                   + "ORDER BY precedence";

      ps = conn.prepareStatement(query);
      rs = ps.executeQuery();

      while ( rs.next() ) {
        priceTypeRow = new PriceTypeDO();

        priceTypeRow.setKey(rs.getByte("thekey"));
        priceTypeRow.setName(rs.getString("name"));
        priceTypeRow.setPrecedence(rs.getByte("precedence"));
        priceTypeRow.setActiveId(rs.getBoolean("active_id"));
        priceTypeRows.add(priceTypeRow);
      }
      //===========================================================================
      // A formatter issues exists during the populatin of empty lists.
      // A work around is to populate one row when there is an emptyList.
      // The cooresponding jsp also needs to check for the phantom row.
      //===========================================================================
      if (priceTypeRows.isEmpty()) {
        priceTypeRows.add(new PriceTypeDO());
      }
    }
    catch (SQLException e) {
      cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
    }
    finally {
      if (rs != null) {
        try {
          rs.close();
        }
        catch (SQLException sqle) {
          cat.error(this.getClass().getName() + ": SQLException : " + sqle.getMessage());
        }
        rs = null;
      }
      if (ps != null) {
        try {
          ps.close();
        }
        catch (SQLException sqle) {
            cat.error(this.getClass().getName() + ": SQLException : " + sqle.getMessage());
        }
        ps = null;
      }
    }
    return priceTypeRows;
  }
}