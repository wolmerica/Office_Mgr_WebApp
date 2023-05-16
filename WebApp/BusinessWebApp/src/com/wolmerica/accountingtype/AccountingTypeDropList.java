/*
 * AccountingTypeDropList.java
 *
 * Created on March 28, 2006, 10:36 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package com.wolmerica.accountingtype;

//--------------------------------------------------------------------------------
// @author Richard Wolschlager
//--------------------------------------------------------------------------------
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

public class AccountingTypeDropList {

  Logger cat = Logger.getLogger("WOWAPP");

  public ArrayList getAccountingTypeList(Connection conn)
   throws Exception, SQLException {

    AccountingTypeDO accountingTypeRow = null;
    ArrayList<AccountingTypeDO> accountingTypeRows = new ArrayList<AccountingTypeDO>();
     
    PreparedStatement ps = null;
    ResultSet rs = null;
    
    try {  
//-----------------------------------------------------------------------------      
// Retrieve the active accounting type values available.
//-----------------------------------------------------------------------------
      String query = "SELECT thekey, name, description, "
                   + "precedence, active_id, "
                   + "create_user, create_stamp,"
                   + "update_user, update_stamp "
                   + "FROM accountingtype "
                   + "WHERE active_id "
                   + "AND name != 'TRANSACTION' "
                   + "ORDER BY precedence";
      ps = conn.prepareStatement(query);
      rs = ps.executeQuery();

      while ( rs.next() ) {
        accountingTypeRow = new AccountingTypeDO();

        accountingTypeRow.setKey(rs.getByte("thekey"));
        accountingTypeRow.setName(rs.getString("name"));
        accountingTypeRow.setDescription(rs.getString("description"));
        accountingTypeRow.setPrecedence(rs.getByte("precedence"));
        accountingTypeRow.setActiveId(rs.getBoolean("active_id"));        
        accountingTypeRow.setCreateUser(rs.getString("create_user"));
        accountingTypeRow.setCreateStamp(rs.getTimestamp("create_stamp"));
        accountingTypeRow.setUpdateUser(rs.getString("update_user"));
        accountingTypeRow.setUpdateStamp(rs.getTimestamp("update_stamp"));
                  
        accountingTypeRows.add(accountingTypeRow);
      }
      //===========================================================================
      // A formatter issues exists during the populatin of empty lists.
      // A work around is to populate one row when there is an emptyList.
      // The cooresponding jsp also needs to check for the phantom row.
      //===========================================================================
      if (accountingTypeRows.isEmpty()) {
        accountingTypeRows.add(new AccountingTypeDO());
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
    return accountingTypeRows;
  }
}