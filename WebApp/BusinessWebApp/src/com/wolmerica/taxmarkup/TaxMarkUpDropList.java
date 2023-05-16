/*
 * TaxMarkUpDropList.java
 *
 * Created on March 14, 2006, 6:36 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package com.wolmerica.taxmarkup;

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

public class TaxMarkUpDropList {

  Logger cat = Logger.getLogger("WOWAPP");

  public ArrayList getSalesTaxMarkUpList(Connection conn)
   throws Exception, SQLException {

    TaxMarkUpDO taxMarkUpRow = null;
    ArrayList<TaxMarkUpDO> taxMarkUpRows = new ArrayList<TaxMarkUpDO>();

    PreparedStatement ps = null;
    ResultSet rs = null;

    try {
//-----------------------------------------------------------------------------
// Retrieve the tax mark-up values available.
//-----------------------------------------------------------------------------
      String query = "SELECT thekey, name, description, "
                   + "value, percentage_id, "
                   + "precedence, active_id "
                   + "FROM taxmarkup "
                   + "WHERE name LIKE '%TAX%' "
                   + "ORDER BY precedence, description";

      ps = conn.prepareStatement(query);
      rs = ps.executeQuery();

      while ( rs.next() ) {
        taxMarkUpRow = new TaxMarkUpDO();

        taxMarkUpRow.setKey(rs.getByte("thekey"));
        taxMarkUpRow.setName(rs.getString("name"));
        taxMarkUpRow.setDescription(rs.getString("description"));
        taxMarkUpRow.setValue(rs.getBigDecimal("value"));
        taxMarkUpRow.setPercentageId(rs.getBoolean("percentage_id"));
        taxMarkUpRow.setPrecedence(rs.getByte("precedence"));
        taxMarkUpRow.setActiveId(rs.getBoolean("active_id"));
// Disabled do to validation errors.  Presumable from the date format of MySQL.

        taxMarkUpRows.add(taxMarkUpRow);
      }
      //===========================================================================
      // A formatter issues exists during the populatin of empty lists.
      // A work around is to populate one row when there is an emptyList.
      // The cooresponding jsp also needs to check for the phantom row.
      //===========================================================================
      if (taxMarkUpRows.isEmpty()) {
        taxMarkUpRows.add(new TaxMarkUpDO());
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
    return taxMarkUpRows;
  }
}