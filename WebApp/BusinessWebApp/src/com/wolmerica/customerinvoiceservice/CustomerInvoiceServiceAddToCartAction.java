/*
 * CustomerInvoiceServiceAddToCartAction.java
 *
 * Created on August 16, 2006, 12:30 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package com.wolmerica.customerinvoiceservice;

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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.math.BigDecimal;

import org.apache.log4j.Logger;

public class CustomerInvoiceServiceAddToCartAction extends Action {

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
  
  
  private void insertCustomerInvoiceService(HttpServletRequest request,
                                            Integer ciKey,
                                            Integer pbsKey,
                                            Integer sAvailableQty,
                                            Integer orderQty,
                                            BigDecimal estimatedPrice)
    throws Exception, SQLException {

    DataSource ds = null;
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    Integer cisKey = new Integer("1");
    Byte ctKey = null;
    Byte ptKey = null;
    Integer sdKey = null;
    BigDecimal pbsComputedPrice = null;
    BigDecimal ptServiceRate = null;
    BigDecimal sdCostBasis = null;

    try {
      ds = getDataSource(request);
      conn = ds.getConnection();

//===========================================================================
// Retrieve all the details for the service to be added to the customer invoice.
//===========================================================================
      String query = "SELECT pricebyservice.pricetype_key,"
                   + "customerattributebyservice.customertype_key,"
                   + "customerattributebyservice.servicedictionary_key,"
                   + "servicedictionary.labor_cost,"
                   + "servicedictionary.duration_hours,"
                   + "servicedictionary.duration_minutes/60 AS hour_fraction,"
                   + "pricetype.full_size_id,"
                   + "pricetype.service_rate/100 AS service_rate,"
                   + getPropertyService().getCustomerProperties(request,"customerinvoiceservice.default.price") + " AS computed_price"
                   + " FROM pricebyservice,customerattributebyservice,"
                   + "servicedictionary,pricetype "
                   + "WHERE pricebyservice.thekey = ? "
                   + "AND pricebyservice.customerattributebyservice_key = customerattributebyservice.thekey "
                   + "AND customerattributebyservice.servicedictionary_key = servicedictionary.thekey "
                   + "AND pricebyservice.pricetype_key = pricetype.thekey";
      ps = conn.prepareStatement(query);
      ps.setInt(1, pbsKey);
      cat.debug(this.getClass().getName() + ": setInt(pbsKey) = " + pbsKey.toString());
      rs = ps.executeQuery();
      if ( rs.next() ) {
        ptKey = rs.getByte("pricetype_key");
        ctKey = rs.getByte("customertype_key");
        sdKey = rs.getInt("servicedictionary_key");
        pbsComputedPrice = rs.getBigDecimal("computed_price");
        ptServiceRate = rs.getBigDecimal("service_rate");
        if (rs.getBoolean("full_size_id")) {
          ptServiceRate = rs.getBigDecimal("duration_hours").add(rs.getBigDecimal("hour_fraction"));
        }
        sdCostBasis = ptServiceRate.multiply(rs.getBigDecimal("labor_cost"));
        cat.debug("Service Dictionary Key = " + sdKey + " Cost Basis = " + sdCostBasis);
      }
      else {
        throw new Exception("Price By Service  " + pbsKey.toString() + " not found!");
      }

//--------------------------------------------------------------------------------
// Check if we have enough available to sell at a minimum, one unit.
//--------------------------------------------------------------------------------
      if (sAvailableQty < orderQty) {
//        throw new Exception("Service quantity is less than " + orderQty + " unit...:" + sAvailableQty);
        orderQty = new Integer("0");
      }

//--------------------------------------------------------------------------------
// Preserve the work order estimated price for the invoice.
//--------------------------------------------------------------------------------
      if (estimatedPrice.compareTo(new BigDecimal("-270")) != 0) {
        pbsComputedPrice = estimatedPrice;
      }
      cat.debug(this.getClass().getName() + ": pbsComputedPrice = " + pbsComputedPrice.toString());
//===========================================================================
// Get the maximum key from the customer invoice service.
//===========================================================================
      query = "SELECT COUNT(*) AS cis_cnt, MAX(thekey)+1 AS cis_key "
            + "FROM customerinvoiceservice ";
      ps = conn.prepareStatement(query);

//===========================================================================
// Query to retrieve the maximum key value in the customer invoice
// service table.  We then increase the maximum by one before insertion.
//===========================================================================
      rs = ps.executeQuery();
      if ( rs.next() ) {
        if ( rs.getInt("cis_cnt") > 0 ) {
           cisKey = rs.getInt("cis_key");
        }
      }
      else {
        throw new Exception("CustomerInvoiceService  " + ciKey.toString() + " not found!");
      }
      cat.debug(this.getClass().getName() + ": cisKey = " + cisKey.toString());

//===========================================================================
// Preparation of a insert query for the customer invoice service.
//===========================================================================
      query = "INSERT INTO customerinvoiceservice "
            + "(thekey,customerinvoice_key,"
            + "servicedictionary_key,pricetype_key,"
            + "available_qty,order_qty,"
            + "service_price,cost_basis,"
            + "discount_rate,service_tax_id,"
            + "genesis_key,"
            + "master_key,"
            + "create_user,create_stamp,"
            + "update_user,update_stamp) "
            + "VALUES (?,?,?,?,?,?,?,?,?,?,"
            + "?,?,?,CURRENT_TIMESTAMP,?,CURRENT_TIMESTAMP)";
      ps = conn.prepareStatement(query);

//===========================================================================
// Insert a new customer invoice service row.
//===========================================================================
      ps.setInt(1, cisKey);
      ps.setInt(2, ciKey);
      ps.setInt(3, sdKey);
      ps.setByte(4, ptKey);
      ps.setInt(5, sAvailableQty);
      ps.setInt(6, orderQty);
      ps.setBigDecimal(7, pbsComputedPrice.setScale(2, BigDecimal.ROUND_HALF_UP));
      ps.setBigDecimal(8, sdCostBasis.setScale(3, BigDecimal.ROUND_HALF_UP));
      ps.setBigDecimal(9, new BigDecimal("0"));
      ps.setBoolean(10, false);
      ps.setInt(11, cisKey);
      ps.setInt(12, cisKey);      
      ps.setString(13, request.getSession().getAttribute("USERNAME").toString());
      ps.setString(14, request.getSession().getAttribute("USERNAME").toString());
      ps.executeUpdate();
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
// Customer Invoice key.
//--------------------------------------------------------------------------------
      Integer theKey = null;
      if (request.getAttribute("key") != null) {
        theKey = new Integer(request.getAttribute("key").toString());
      }
      else {
        if (request.getParameter("key") != null) {
          theKey = new Integer(request.getParameter("key"));
        }
        else {
          throw new Exception("Request getParameter/getAttribute [key] not found!");
        }
      }
      cat.debug(this.getClass().getName() + ": get[key] = " + theKey.toString());
//--------------------------------------------------------------------------------
// Price by service key
//--------------------------------------------------------------------------------
      Integer pbsKey = null;
      if (request.getParameter("pbsKey") != null) {
        pbsKey = new Integer(request.getParameter("pbsKey"));
      }
      else {
        if (request.getAttribute("pbsKey") != null) {
          pbsKey = new Integer(request.getAttribute("pbsKey").toString());
        }
        else {
          throw new Exception("Request getParameter/getAttribute [pbsKey] not found!");
        }
      }
      cat.debug(this.getClass().getName() + ": get[pbsKey] = " + pbsKey.toString());
//----------------------------------------------------------------------
// Get availableQty value which indicates maximum quantity available.
//----------------------------------------------------------------------
      Integer availableQty = 1000;
      if (request.getParameter("availableQty") != null) {
        availableQty = new Integer(request.getParameter("availableQty"));
      }
      else {
        if (request.getAttribute("availableQty") != null) {
          availableQty = new Integer(request.getAttribute("availableQty").toString());
        }
      }
      cat.debug(this.getClass().getName() + ": get[availableQty] = " + availableQty.toString());

//----------------------------------------------------------------------
// Get estimated price value if provided from the work order.
//----------------------------------------------------------------------
      BigDecimal estimatedPrice = new BigDecimal("-270");
      if (request.getParameter("estimatedPrice") != null) {
        estimatedPrice = new BigDecimal(request.getParameter("estimatedPrice"));
      }
      else {
        if (request.getAttribute("estimatedPrice") != null) {
          estimatedPrice = new BigDecimal(request.getAttribute("estimatedPrice").toString());
        }
      }
      cat.debug(this.getClass().getName() + ": get[estimatedPrice] = " + estimatedPrice.toString());

//----------------------------------------------------------------------
// Get orderQty value which indicates what is needed for the invoice.
//----------------------------------------------------------------------
      Integer orderQty = 1;
      if (request.getParameter("orderQty") != null) {
        orderQty = new Integer(request.getParameter("orderQty"));
      }
      else {
        if (request.getAttribute("orderQty") != null) {
          orderQty = new Integer(request.getAttribute("orderQty").toString());
        }
      }
      cat.debug(this.getClass().getName() + ": get[orderQty] = " + orderQty.toString());


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

      if (orderQty > 0){
        insertCustomerInvoiceService(request,
                                     theKey,
                                     pbsKey,
                                     availableQty,
                                     orderQty,
                                     estimatedPrice);
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