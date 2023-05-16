/*
 * PriceByItemGetAction.java
 *
 * Created on September 9, 2005, 8:14 AM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package com.wolmerica.pricebyitem;

//--------------------------------------------------------------------------------
// @author Richard Wolschlager
//--------------------------------------------------------------------------------
import com.wolmerica.employee.EmployeesActionMapping;
import com.wolmerica.service.userstate.UserStateService;
import com.wolmerica.service.userstate.DefaultUserStateService;
import com.wolmerica.tools.formatter.FormattingException;

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
import java.util.ArrayList;

import org.apache.log4j.Logger;

public class PriceByItemGetAction extends Action {

  Logger cat = Logger.getLogger("WOWAPP");

  private UserStateService userStateService = new DefaultUserStateService();

  public UserStateService getUserStateService() {
      return userStateService;
  }

  public void setUserStateService(UserStateService userStateService) {
      this.userStateService = userStateService;
  }
  

  private PriceByItemHeadDO getPriceByItems(HttpServletRequest request,
                                              ActionForm form,
                                              Integer idKey,
                                              Byte ctKey,
                                              Byte ptKey)
   throws Exception, IOException, SQLException, ServletException {

    PriceByItemHeadDO formHDO = new PriceByItemHeadDO();
    PriceByItemDO priceByItem = null;
    ArrayList<PriceByItemDO> priceByItems = new ArrayList<PriceByItemDO>();

    DataSource ds = null;
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    try {

      String query = "SELECT brand_name,"
                   + "size,"
                   + "size_unit "
                   + "FROM itemdictionary "
                   + "WHERE thekey=?";

      ds = getDataSource(request);
      conn = ds.getConnection();
      ps = conn.prepareStatement(query);

      ps.setInt(1, idKey);
      rs = ps.executeQuery();
      if ( rs.next() ) {
        formHDO.setItemDictionaryKey(idKey);
        formHDO.setBrandName(rs.getString("brand_name"));
        formHDO.setSize(rs.getBigDecimal("size"));
        formHDO.setSizeUnit(rs.getString("size_unit"));
      }
      else {
        throw new Exception("ItemDictionary " + idKey.toString() + " not found!");
      }

      query = "SELECT pricebyitem.thekey,"
            + "pricetype.precedence,"
            + "customertype_key,"
            + "pricetype_key,"
            + "priceattributebyitem_key,"
            + "computed_price,"
            + "previous_price,"
            + "over_ride_price "
            + "FROM priceattributebyitem, pricebyitem, pricetype, customertype "
            + "WHERE itemdictionary_key=? "
            + "AND priceattributebyitem.thekey = priceattributebyitem_key "
            + "AND pricetype_key = pricetype.thekey "
            + "AND pricetype.active_id "
            + "AND customertype_key = customertype.thekey "
            + "AND customertype.active_id ";
      if (ctKey != null )
        query = query + "AND customertype_key=? ";
      if (ptKey != null )
        query = query + "AND pricetype.thekey=? ";
      query = query + "ORDER BY pricetype.precedence, customertype_key ";

      cat.debug(this.getClass().getName() + ": Query = " + query);
      ps = conn.prepareStatement(query);
      ps.setInt(1, idKey);
      if (ctKey != null ) {
        cat.debug(this.getClass().getName() + ": ct = " + ctKey.toString());
        ps.setByte(2, ctKey);
      }
      if (ptKey != null ) {
        cat.debug(this.getClass().getName() + ": pt = " + ptKey.toString());
        ps.setByte(3, ptKey);
      }
      rs = ps.executeQuery();

      Integer oiCount = new Integer("0");
      while ( rs.next() ) {
        priceByItem = new PriceByItemDO();

        priceByItem.setKey(rs.getInt("thekey"));
        priceByItem.setCustomerTypeKey(rs.getByte("customertype_key"));
        priceByItem.setPriceTypeKey(rs.getByte("pricetype_key"));
        priceByItem.setPriceAttributeByItemKey(rs.getInt("priceattributebyitem_key"));
        priceByItem.setComputedPrice(rs.getBigDecimal("computed_price"));
        priceByItem.setPreviousPrice(rs.getBigDecimal("previous_price"));
        priceByItem.setOverRidePrice(rs.getBigDecimal("over_ride_price"));

        oiCount++;
        priceByItems.add(priceByItem);
      }
      formHDO.setPriceByItemForm(priceByItems);

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
    return formHDO;
  }

    @Override
  public ActionForward execute(ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
   throws Exception, SQLException {

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
      Integer theKey = null;
      if (!(request.getParameter("key") == null)) {
        theKey = new Integer(request.getParameter("key"));
      }
      else {
        if (!(request.getAttribute("key") == null)) {
          theKey = new Integer(request.getAttribute("key").toString());
        }
        else {
          throw new Exception("Request getParameter/getAttribute [key] not found!");
        }
      }
      cat.debug(this.getClass().getName() + ": get[key] = " + theKey.toString());
      request.setAttribute("key", theKey.toString());

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
      
//--------------------------------------------------------------------------------
// Price adjustment - Passes row id via the request attribute.
//--------------------------------------------------------------------------------
      Byte theRow = null;
      if (!(request.getParameter("row") == null)) {
        theRow = new Byte(request.getParameter("row"));
      }
      else {
        if (!(request.getAttribute("row") == null)) {
          theRow = new Byte(request.getAttribute("row").toString());
        }
      }
      if (theRow != null)
        request.setAttribute("row", theRow.toString());
//--------------------------------------------------------------------------------
// Price adjustment - Passes customer type key via the request attribute.
//--------------------------------------------------------------------------------
      Byte ctKey = null;
      if (!(request.getParameter("ctkey") == null)) {
        ctKey = new Byte(request.getParameter("ctkey"));
      }
      else {
        if (!(request.getAttribute("ctkey") == null)) {
          ctKey = new Byte(request.getAttribute("ctkey").toString());
        }
      }
      if (ctKey != null)
        request.setAttribute("ctkey", ctKey.toString());      
//--------------------------------------------------------------------------------
// Price adjustment - Passes price type key via the request attribute.
//--------------------------------------------------------------------------------
      Byte ptKey = null;
      if (!(request.getParameter("ptkey") == null)) {
        ptKey = new Byte(request.getParameter("ptkey"));
      }
      else {
        if (!(request.getAttribute("ptkey") == null)) {
          ptKey = new Byte(request.getAttribute("ptkey").toString());
        }
      }
      if (ptKey != null)
        request.setAttribute("ptkey", ptKey.toString());
//--------------------------------------------------------------------------------
// New nested object coding.
//--------------------------------------------------------------------------------
      try {
//--------------------------------------------------------------------------------          
// Instantiate a formHDO object to populate formHStr.
//--------------------------------------------------------------------------------
        PriceByItemHeadDO formHDO = getPriceByItems(request, form, theKey, ctKey, ptKey);
        request.getSession().setAttribute("pricebyitemHDO", formHDO);
//--------------------------------------------------------------------------------
// Create the wrapper object for priceinvoiceitem.
//--------------------------------------------------------------------------------        
        PriceByItemHeadForm formHStr = new PriceByItemHeadForm();
        formHStr.populate(formHDO);
        form = formHStr;
      }
      catch (FormattingException fe) {
            fe.getMessage();
      }
      if ( form == null ) {
        cat.debug(this.getClass().getName() + ":---->form is null<----");
      }
      if ("request".equals(mapping.getScope())) {
        cat.debug(this.getClass().getName() + ":---->request.setAttribute<----");
        request.setAttribute(mapping.getAttribute(), form);
      }
      else {
        cat.debug(this.getClass().getName() + ":---->session.setAttribute<----");
        request.getSession().setAttribute(mapping.getAttribute(), form);
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