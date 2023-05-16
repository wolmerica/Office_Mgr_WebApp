/*
 * CustomerTypeDropList.java
 *
 * Created on March 14, 2006, 6:36 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package com.wolmerica.customertype;

/**
 *
 * @author Richard
 */
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

public class CustomerTypeDropList {

  Logger cat = Logger.getLogger("CUSTAPP");

  public ArrayList getActiveCustomerTypeList(Connection conn)
   throws Exception, SQLException {

    CustomerTypeDO customerTypeRow = null;
    ArrayList<CustomerTypeDO> customerTypeRows = new ArrayList<CustomerTypeDO>();
     
    PreparedStatement ps = null;
    ResultSet rs = null;

    try {  
//-----------------------------------------------------------------------------      
// Retrieve the active customer type values available to assign to a customer.
//-----------------------------------------------------------------------------
      String query = "SELECT thekey, name, "
                   + "precedence, active_id "
                   + "FROM customertype "
                   + "WHERE active_id "
                   + "ORDER BY precedence";

      ps = conn.prepareStatement(query);
      rs = ps.executeQuery();

      while ( rs.next() ) {
        customerTypeRow = new CustomerTypeDO();

        customerTypeRow.setKey(rs.getByte("thekey"));
        customerTypeRow.setName(rs.getString("name"));
        customerTypeRow.setPrecedence(rs.getByte("precedence"));
        customerTypeRow.setSoldByKey(rs.getInt("thekey"));
        customerTypeRow.setSoldByName(rs.getString("name"));
        customerTypeRow.setActiveId(rs.getBoolean("active_id"));        
        customerTypeRows.add(customerTypeRow);
      }
      //===========================================================================
      // A formatter issues exists during the populatin of empty lists.
      // A work around is to populate one row when there is an emptyList.
      // The cooresponding jsp also needs to check for the phantom row.
      //===========================================================================
      if (customerTypeRows.isEmpty()) {
        customerTypeRows.add(new CustomerTypeDO());
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
    return customerTypeRows;
  }
}