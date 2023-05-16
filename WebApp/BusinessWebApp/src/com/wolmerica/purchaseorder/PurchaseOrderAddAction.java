/*
 * PurchaseOrderAddAction.java
 *
 * Created on August 24, 2005, 2:17 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 * 12/23/2005 Implement tools.formatter library.
 */

package com.wolmerica.purchaseorder;

//--------------------------------------------------------------------------------
// @author Richard Wolschlager
//--------------------------------------------------------------------------------
import com.wolmerica.employee.EmployeesActionMapping;
import com.wolmerica.service.userstate.UserStateService;
import com.wolmerica.service.userstate.DefaultUserStateService;
import com.wolmerica.util.common.EnumPurchaseOrderStatus;

import java.io.IOException;
import java.text.DecimalFormat;
import javax.servlet.ServletException;
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
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

import org.apache.log4j.Logger;

public class PurchaseOrderAddAction extends Action {

  Logger cat = Logger.getLogger("WOWAPP");

  private UserStateService userStateService = new DefaultUserStateService();

  public UserStateService getUserStateService() {
      return userStateService;
  }

  public void setUserStateService(UserStateService userStateService) {
      this.userStateService = userStateService;
  }


  private Integer insertPurchaseOrder(HttpServletRequest request,
                                      ActionForm form,
                                      String posNumber)
   throws Exception, SQLException {

    cat.debug(this.getClass().getName() + "posNumber = " + posNumber);
       
    DataSource ds = null;
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    Integer poKey = 1;

    try {
      ds = getDataSource(request);
      conn = ds.getConnection();        
        
      PurchaseOrderDO formDO = (PurchaseOrderDO) request.getSession().getAttribute("purchaseorder");
      formDO.setPurchaseOrderNumber(posNumber);

//-------------------------------------------------------------------------------- 
// Construct the insert statement for purchaseorder.
//--------------------------------------------------------------------------------      
      String query = "INSERT INTO purchaseorder "
                   + "(thekey,"
                   + "purchase_order_num,"
                   + "order_status,"
                   + "priority_key,"
                   + "vendor_key,"
                   + "customer_key,"
                   + "sourcetype_key,"
                   + "source_key,"  
                   + "submit_order_stamp,"
                   + "note_line1,"
                   + "create_user,"
                   + "create_stamp,"
                   + "update_user,"
                   + "update_stamp";
      if (formDO.getScheduleKey() != null)
        query = query + ",schedule_key";
      query = query + " ) "                   
                    + "VALUES (NULL,?,?,?,?,?,?,?,?,?,"
                    + "?,CURRENT_TIMESTAMP,?,CURRENT_TIMESTAMP";
      if (formDO.getScheduleKey() != null)
        query = query + ",?";
      query = query + ")";
      cat.debug(this.getClass().getName() + "query = " + query);      
//-------------------------------------------------------------------------------- 
// Prepare purchaseorder insert statement.
//--------------------------------------------------------------------------------      
      ps = conn.prepareStatement(query);
      formDO.setOrderStatus(EnumPurchaseOrderStatus.N.getValue());
      ps.setString(1, formDO.getPurchaseOrderNumber());
      ps.setString(2, formDO.getOrderStatus());
      ps.setByte(3, formDO.getPriorityKey());
      ps.setInt(4, formDO.getVendorKey());
      ps.setInt(5, formDO.getCustomerKey());
      ps.setByte(6, formDO.getSourceTypeKey());
      ps.setInt(7, formDO.getSourceKey());
      ps.setTimestamp(8, formDO.getSubmitOrderStamp());
      ps.setString(9, formDO.getNoteLine1());
      ps.setString(10, request.getSession().getAttribute("USERNAME").toString());
      ps.setString(11, request.getSession().getAttribute("USERNAME").toString());
      if (formDO.getScheduleKey() != null)
        ps.setInt(12, formDO.getScheduleKey());
      ps.executeUpdate();
      cat.debug(this.getClass().getName() + "insert into purchaseorder");
      
//-------------------------------------------------------------------------------- 
// Retrieve the purchase order key for the newly added record.
//--------------------------------------------------------------------------------      
      query = "SELECT thekey, order_status, submit_order_stamp "
            + "FROM purchaseorder "
            + "WHERE purchase_order_num = ? "
            + "ORDER BY thekey DESC";
      ps = conn.prepareStatement(query);
      ps.setString(1, formDO.getPurchaseOrderNumber());
      rs = ps.executeQuery();

      if ( rs.next() ) {
        formDO.setKey(rs.getInt("thekey"));
        poKey = formDO.getKey();
        formDO.setOrderStatus(rs.getString("order_status"));
        cat.debug(this.getClass().getName() + " Assign to key=" + formDO.getKey().toString());
      }
      else {
        throw new Exception("No purchaseorder with PO#: "+ formDO.getPurchaseOrderNumber());
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
      if (conn != null) {
        try {
          conn.close();
        }
        catch (SQLException sqle) {
            cat.error(this.getClass().getName() + ": SQLException : " + sqle.getMessage());
        }
        conn = null;
      }
    }
    return poKey;
  }

  private String getPurchaseOrderNumber(HttpServletRequest request)
    throws Exception, SQLException {
    DataSource ds = null;
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;
    ResultSetMetaData rsmd = null;
    Integer returnKey = null;
    Integer poCount = null;
    Timestamp poTimestamp = null;
    String posNumber = null;

    poTimestamp = new Timestamp(new Date().getTime());

    try {
      ds = getDataSource(request);
      conn = ds.getConnection();

      String query = "SELECT order_count "
                    + "FROM purchaseordernumber "
                    + "WHERE order_date = CURRENT_DATE";

      ps = conn.prepareStatement(query);
      rs = ps.executeQuery();
      if ( rs.next() ) {
        cat.debug(this.getClass().getName() + "rs.next is TRUE");
        poCount = rs.getInt("order_count");
        cat.debug(this.getClass().getName() + "Assign to orderCount");
//--------------------------------------------------------------------------------
// Increment the order_count value by one in ordernumber 
//--------------------------------------------------------------------------------        
        query =  "UPDATE purchaseordernumber "
              + "SET order_count = order_count + 1";
        ps = conn.prepareStatement(query);
        ps.executeUpdate();
        }
      else {
//--------------------------------------------------------------------------------
// Updating the 'ordernumber' table for a new day. 
//--------------------------------------------------------------------------------          
        poCount = 1;
        cat.debug(this.getClass().getName() + "rs.next is FALSE");
        query = null;
        query = "UPDATE purchaseordernumber "
              + "SET order_count = ?, "
              + "order_date = CURRENT_DATE, "
              + "update_user=?,"
              + "update_stamp=?"
              + "WHERE order_date != CURRENT_DATE";
        ps = conn.prepareStatement(query);
        ps.setInt(1, new Integer("2"));
        ps.setString(2, request.getSession().getAttribute("USERNAME").toString());
        ps.setTimestamp(3, poTimestamp);
        ps.executeUpdate();
     }
//--------------------------------------------------------------------------------
// Generate a P.O. number add a record to the 'purchaseorder' table. 
//--------------------------------------------------------------------------------      
      query = null;
      Calendar now = Calendar.getInstance();
      
      Integer pomth = now.get(Calendar.MONTH) + 1;
      Integer poday = now.get(Calendar.DATE);
      Integer poyear = now.get(Calendar.YEAR);
      String posYear = poyear.toString();

      DecimalFormat myFormat = new java.text.DecimalFormat("00");
      posNumber = myFormat.format(pomth);
      posNumber = posNumber + myFormat.format(poday);
      posNumber = posNumber + posYear.substring(2);
      myFormat = new java.text.DecimalFormat("000");
      posNumber = posNumber + myFormat.format(poCount);
      if (poCount > 99)
        throw new Exception("Purchase order count for the day exceeds the 99 maximum.");
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
      if (conn != null) {
        try {
          conn.close();
        }
        catch (SQLException sqle) {
            cat.error(this.getClass().getName() + ": SQLException : " + sqle.getMessage());
        }
        conn = null;
      }
    }
    return posNumber;
  }

    @Override
  public ActionForward execute(ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws IOException, ServletException {

		// Default target to success
    String target = "success";

    EmployeesActionMapping employeesMapping =
      (EmployeesActionMapping)mapping;

//--------------------------------------------------------------------------------
// Does this action require the user to login.
//--------------------------------------------------------------------------------
    if ( employeesMapping.isLoginRequired() ) {

      if ( request.getSession().getAttribute("USER") == null ) {
//--------------------------------------------------------------------------------
// The user is not logged in.
//--------------------------------------------------------------------------------
        target = "login";
        ActionMessages actionMessages = new ActionMessages();

        actionMessages.add(ActionMessages.GLOBAL_MESSAGE,
          new ActionMessage("errors.login.required"));
//--------------------------------------------------------------------------------
// Report any ActionMessages we have discovered back to the original form.
//--------------------------------------------------------------------------------
        if (!actionMessages.isEmpty()) {
          saveMessages(request, actionMessages);
        }
//--------------------------------------------------------------------------------
// Forward to the request to the login screen.
//--------------------------------------------------------------------------------
        return (mapping.findForward(target));      
      }    
    }

    try {
//--------------------------------------------------------------------------------
// 07-23-06  Code to handle permissions and avoid simultaneous updates to records.
//--------------------------------------------------------------------------------
      
      String usToken = getUserStateService().getUserToken(request,
                                              this.getDataSource(request).getConnection(),
                                              this.getClass().getName(),
                                              getUserStateService().getNoKey());
      if (usToken.equalsIgnoreCase(getUserStateService().getLocked()))
        request.setAttribute(getUserStateService().getDisableEdit(), false);
      else if (usToken.equalsIgnoreCase(getUserStateService().getProhibited()))
        throw new Exception(getUserStateService().getAccessDenied());

      cat.debug(this.getClass().getName() + "getPONumber");
      String posNumber = getPurchaseOrderNumber(request);
      Integer theKey = null;
      cat.debug(this.getClass().getName() + "1st posNumber = " + posNumber);      
      theKey = insertPurchaseOrder(request, form, posNumber);
      request.setAttribute("key", theKey.toString());
    }
    catch (Exception e) {
      cat.error(this.getClass().getName() + ": Exception : " + e.getMessage());
      target = "error";
      ActionMessages actionMessages = new ActionMessages();
      actionMessages.add(ActionMessages.GLOBAL_MESSAGE,
        new ActionMessage("errors.database.error", e.getMessage()));
//--------------------------------------------------------------------------------
// Report any ActionMessages
//--------------------------------------------------------------------------------
      if (!actionMessages.isEmpty()) {
        saveMessages(request, actionMessages);
      }
    }
//--------------------------------------------------------------------------------
// Forward to the appropriate View
//--------------------------------------------------------------------------------
    return (mapping.findForward(target));
  }
}