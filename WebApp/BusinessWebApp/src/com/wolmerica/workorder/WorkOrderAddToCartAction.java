/*
 * WorkOrderAddToCartAction.java
 *
 * Created on October 13, 2007, 09:00 AM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package com.wolmerica.workorder;

//--------------------------------------------------------------------------------
// @author Richard Wolschlager
//--------------------------------------------------------------------------------
import com.wolmerica.employee.EmployeesActionMapping;
import com.wolmerica.service.property.PropertyService;
import com.wolmerica.service.property.DefaultPropertyService;
import com.wolmerica.service.userstate.UserStateService;
import com.wolmerica.service.userstate.DefaultUserStateService;

import java.io.IOException;
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
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.math.BigDecimal;
import java.sql.Timestamp;

import org.apache.log4j.Logger;

public class WorkOrderAddToCartAction extends Action {

  Logger cat = Logger.getLogger("WOWAPP");

  private PropertyService propertyService = new DefaultPropertyService();
  private UserStateService userStateService = new DefaultUserStateService();

  public PropertyService getPropertyService() {
      return propertyService;
  }

  public void setPropertyService(PropertyService propertyService) {
      this.propertyService = propertyService;
  }

  public UserStateService getUserStateService() {
      return userStateService;
  }

  public void setUserStateService(UserStateService userStateService) {
      this.userStateService = userStateService;
  }

  
  private void loadFromBundle(HttpServletRequest request,
                              Integer sKey,
                              Integer bdKey)
    throws Exception, SQLException {

    DataSource ds = null;
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;
    BigDecimal woPrice = null;

    try {
      ds = getDataSource(request);
      conn = ds.getConnection();

//===========================================================================
// Collect the bundle detail to be added to the work order.
//===========================================================================
      String query = "SELECT thekey,"
                   + "sourcetype_key,"
                   + "source_key,"
                   + "order_qty "
                   + "FROM bundledetail "
                   + "WHERE bundle_key = ?";
      ps = conn.prepareStatement(query);
      ps.setInt(1, bdKey);
      rs = ps.executeQuery();  
      while(rs.next()) {
        cat.debug(this.getClass().getName() + ": sourceKey = " + rs.getInt("source_key"));
        insertWorkOrder(request,
                        sKey,
                        rs.getByte("sourcetype_key"),
                        rs.getInt("source_key"),
                        rs.getInt("order_qty"));
        cat.debug(this.getClass().getName() + ": order_qty = " + rs.getInt("order_qty"));        
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
  }

  private void loadFromPreviousEvent(HttpServletRequest request,
                                     Integer sKey,
                                     Integer orgsKey)
   throws Exception, SQLException {

    DataSource ds = null;
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;
      
    try {
      ds = getDataSource(request);
      conn = ds.getConnection();

//--------------------------------------------------------------------------------
// Get the work order records for the orginal event.
//--------------------------------------------------------------------------------
      String query = "SELECT thekey,"
                   + "sourcetype_key,"
                   + "source_key,"                   
                   + "order_qty,"
                   + "the_price "
                   + "FROM workorder "
                   + "WHERE schedule_key = ? "
                   + "ORDER by thekey";
      ps = conn.prepareStatement(query);
      ps.setInt(1, orgsKey);
      rs = ps.executeQuery();
      while ( rs.next() ) {
        cat.debug(this.getClass().getName() + ": sourceKey = " + rs.getInt("source_key"));        
        insertWorkOrderWithPrice(request,
                                 conn,
                                 sKey,
                                 rs.getByte("sourcetype_key"),
                                 rs.getInt("source_key"),
                                 rs.getInt("order_qty"),
                                 rs.getBigDecimal("the_price"));
        cat.debug(this.getClass().getName() + ": order_qty = " + rs.getInt("order_qty"));        
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
  }

  private void insertWorkOrder(HttpServletRequest request,
                               Integer sKey,
                               Byte sourceTypeKey,
                               Integer pbKey,
                               Integer woQty)
      throws Exception, SQLException {

    DataSource ds = null;
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;
    BigDecimal woPrice = null;

    try {
      ds = getDataSource(request);
      conn = ds.getConnection();

      cat.debug(this.getClass().getName() + ": insertWorkOrder()  " + woQty);
      woPrice = getWorkOrderPrice(request,
                                  conn,
                                  sourceTypeKey,
                                  pbKey);

      cat.debug(this.getClass().getName() + ": order_qty = " + woQty);
      cat.debug(this.getClass().getName() + ": woPrice = " + woPrice);

      insertWorkOrderWithPrice(request,
                               conn,
                               sKey,
                               sourceTypeKey,
                               pbKey,
                               woQty,
                               woPrice);
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
  }

  private void insertWorkOrderWithPrice(HttpServletRequest request,
                                        Connection conn,
                                        Integer sKey,
                                        Byte sourceTypeKey,
                                        Integer pbKey,
                                        Integer woQty,
                                        BigDecimal woPrice)
    throws Exception, SQLException {

    PreparedStatement ps = null;
    ResultSet rs = null;    
    Integer woKey = new Integer("1");

    try { 
      cat.debug(this.getClass().getName() + ": sKey.........: " + sKey);
      cat.debug(this.getClass().getName() + ": sourceTypeKey: " + sourceTypeKey);
      cat.debug(this.getClass().getName() + ": pbKey........: " + pbKey);
      cat.debug(this.getClass().getName() + ": woQty........: " + woQty);
      cat.debug(this.getClass().getName() + ": woPrice......: " + woPrice);
//===========================================================================
// Get the maximum key from the work order.
//===========================================================================
      String query = "SELECT COUNT(*) AS wo_cnt, MAX(thekey)+1 AS wo_key "
                   + "FROM workorder";
      ps = conn.prepareStatement(query);
      rs = ps.executeQuery();
      if ( rs.next() ) {
        if ( rs.getInt("wo_cnt") > 0 ) {
           woKey = rs.getInt("wo_key");
        }
      }
      else {
        throw new Exception("WorkOrder " + woKey.toString() + " not found!");
      }
      cat.debug(this.getClass().getName() + ": woKey..........: " + woKey);
      
//===========================================================================
// Get the start_stamp value from the schedule.
//===========================================================================
      Timestamp startStamp = null;
      Timestamp endStamp = null;
      query = "SELECT start_stamp, start_stamp + INTERVAL 1 HOUR AS end_stamp "
            + "FROM schedule "
            + "WHERE thekey = ?";
      ps = conn.prepareStatement(query);
      ps.setInt(1, sKey);
      rs = ps.executeQuery();
      if ( rs.next() ) {
        startStamp = rs.getTimestamp("start_stamp");
        endStamp = rs.getTimestamp("end_stamp");
      }
      else
        throw new Exception("Schedule " + sKey.toString() + " not found!");
      cat.debug(this.getClass().getName() + ": startStamp.....: " + startStamp);

//===========================================================================
// Get the release indicator, third party indicator, and vendor key value.
//===========================================================================
      CallableStatement cStmt = null;
      Boolean releaseId = false;
      Boolean thirdPartyId = false;
      Integer vendorKey = null;

//--------------------------------------------------------------------------------
// Get item and service details associated with the workorder entry.
//--------------------------------------------------------------------------------      
      cStmt = conn.prepareCall("{call GetItemOrServiceKey(?,?,?,?,?,?,?)}");
      cStmt.setByte(1, sourceTypeKey);
      cStmt.setInt(2, pbKey);
      cStmt.execute();
      releaseId = cStmt.getBoolean("releaseId");
      thirdPartyId = cStmt.getBoolean("thirdPartyId");
      vendorKey = cStmt.getInt("vendorKey");
            
//===========================================================================
// Preparation of a insert query for work order.
//===========================================================================
      query = "INSERT INTO workorder "
            + "(thekey,"
            + "schedule_key,"
            + "sourcetype_key,"
            + "source_key,"
            + "release_id,"
            + "thirdparty_id,"
            + "vendor_key,"
            + "order_qty,"
            + "the_price,"
            + "start_stamp,"
            + "end_stamp,"
            + "create_user,"
            + "create_stamp,"
            + "update_user,"
            + "update_stamp) "
            + "VALUES (?,?,?,?,?,?,?,?,?,?,"
            + "?,?,CURRENT_TIMESTAMP,?,CURRENT_TIMESTAMP)";
      ps = conn.prepareStatement(query);
      cat.debug(this.getClass().getName() + ": insert query = " + query);

//===========================================================================
// Insert a work order with the price by key instead of the item dictionary key.
// Default the start_stamp and end_stamp value to the schedule start_stamp.
//===========================================================================
      ps.setInt(1, woKey);
      ps.setInt(2, sKey);
      ps.setByte(3, sourceTypeKey);
      ps.setInt(4, pbKey);
      ps.setBoolean(5, releaseId);
      ps.setBoolean(6, thirdPartyId);
      ps.setInt(7, vendorKey);
      ps.setInt(8, woQty);
      ps.setBigDecimal(9, woPrice);
      ps.setTimestamp(10, startStamp);
      ps.setTimestamp(11, endStamp);
      ps.setString(12, request.getSession().getAttribute("USERNAME").toString());
      ps.setString(13, request.getSession().getAttribute("USERNAME").toString());
      ps.executeUpdate();
    }
    catch (SQLException e) {
      cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
      throw new Exception("insertWorkOrderWithPrice() " + e.getMessage());
    }
    finally {
      if (rs != null) {
        try {
          rs.close();
        }
        catch (SQLException e) {
          cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
          throw new Exception("insertWorkOrderWithPrice() " + e.getMessage());
        }
        rs = null;
      }
      if (ps != null) {
        try {
          ps.close();
        }
        catch (SQLException e) {
          cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
          throw new Exception("insertWorkOrderWithPrice() " + e.getMessage());
        }
        ps = null;
      }
    }
  }

  private BigDecimal getWorkOrderPrice(HttpServletRequest request,
                                       Connection conn,
                                       Byte sourceTypeKey,
                                       Integer pbKey)
    throws Exception, SQLException {

    PreparedStatement ps = null;
    ResultSet rs = null;
    BigDecimal woPrice = new BigDecimal("0");

    cat.debug(this.getClass().getName() + ": getWorkOrderPrice() start: " + sourceTypeKey);
    cat.debug(this.getClass().getName() + ": getWorkOrderPrice() start: " + pbKey);

    try {
      String query = "";
      if (sourceTypeKey == 3) {
        query = "SELECT "
              + getPropertyService().getCustomerProperties(request,"customerinvoiceitem.default.price") + " AS computed_price"
              + " FROM pricebyitem "
              + "WHERE thekey = ? ";
      }
      if (sourceTypeKey == 6) {
        query = "SELECT "
                   + getPropertyService().getCustomerProperties(request,"customerinvoiceservice.default.price") + " AS computed_price"
                   + " FROM pricebyservice "
                   + "WHERE thekey = ? ";
      }
      cat.debug(this.getClass().getName() + ": getWorkOrderPrice() query: " + query);
      ps = conn.prepareStatement(query);
      ps.setInt(1, pbKey);
      rs = ps.executeQuery();
      if ( rs.next() ) {
        woPrice = rs.getBigDecimal("computed_price");
      }
      else {
        cat.error(this.getClass().getName() + ": Unable to retrieve the work order price for : " + pbKey);
      }
    cat.debug(this.getClass().getName() + ": getWorkOrderPrice() end: " + pbKey);
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
    cat.debug(this.getClass().getName() + ": getWorkOrderPrice() return: " + woPrice);
    return woPrice;
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
// The event in which the the goods and services are added to.
//--------------------------------------------------------------------------------
      Integer theKey = null;
      if (request.getParameter("key") != null) {
        theKey = new Integer(request.getParameter("key"));
      } 
      else {
        if (request.getAttribute("key") != null) {
          theKey = new Integer(request.getAttribute("key").toString());
        } 
        else {          
          throw new Exception("Request getParameter/getAttribute [key] not found!");
        }
      }

//--------------------------------------------------------------------------------
// Price by item/service key, bundleKey, or orginal schedule key.
//--------------------------------------------------------------------------------
      Integer pbKey = null;
      Integer bundleKey = null;
      Integer orgsKey = null;
      Byte sourceTypeKey = null;
      if (request.getParameter("pbiKey") != null) {
        pbKey = new Integer(request.getParameter("pbiKey"));
        sourceTypeKey = new Byte("3");
      }
      else {
        if (request.getParameter("pbsKey") != null) {
          pbKey = new Integer(request.getParameter("pbsKey"));
          sourceTypeKey = new Byte("6");
        }
        else {
//--------------------------------------------------------------------------------
// Bundle key.
//--------------------------------------------------------------------------------
          if (request.getParameter("bundleKey") != null) {
            bundleKey = new Integer(request.getParameter("bundleKey"));
          }
          else {
            if (request.getParameter("orgsKey") != null) {
              orgsKey = new Integer(request.getParameter("orgsKey"));
            }
            else {               
              throw new Exception("Request getParameter [pb*Key] not found!");
            }
          }
        }
      }
      cat.debug(this.getClass().getName() + ": bundleKey..: " + bundleKey);
      cat.debug(this.getClass().getName() + ": orgsKey....: " + orgsKey);      
      cat.debug(this.getClass().getName() + ": pbKey......: " + pbKey);      

//--------------------------------------------------------------------------------
// Order quantity value.
//--------------------------------------------------------------------------------
      Integer woQty = new Integer("1");
      if (request.getParameter("orderQty") != null) {
        woQty = new Integer(request.getParameter("orderQty"));
      }

//--------------------------------------------------------------------------------
// 07-23-06  Code to handle permissions and avoid simultaneous updates to records.
//--------------------------------------------------------------------------------
      
      String usToken = getUserStateService().getUserToken(request,
                                              this.getDataSource(request).getConnection(),
                                              this.getClass().getName(),
                                              theKey);
      if (usToken.equalsIgnoreCase(getUserStateService().getLocked()))
        request.setAttribute(getUserStateService().getDisableEdit(), false);
      else if (usToken.equalsIgnoreCase(getUserStateService().getProhibited()))
        throw new Exception(getUserStateService().getAccessDenied());
            
      if (bundleKey != null) {
        loadFromBundle(request, theKey, bundleKey);
      } 
      else {
        if (orgsKey != null) {
          loadFromPreviousEvent(request, theKey, orgsKey);            
        } 
        else {
          insertWorkOrder(request, theKey, sourceTypeKey, pbKey, woQty);
        }
      }      
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