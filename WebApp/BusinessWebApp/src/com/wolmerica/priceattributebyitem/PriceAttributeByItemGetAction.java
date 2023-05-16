/*
 * PriceAttributeByItemGetAction.java
 *
 * Created on September 9, 2005, 8:14 AM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package com.wolmerica.priceattributebyitem;

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
import java.math.BigDecimal;

import org.apache.log4j.Logger;

public class PriceAttributeByItemGetAction extends Action {

  Logger cat = Logger.getLogger("WOWAPP");

  private UserStateService userStateService = new DefaultUserStateService();

  public UserStateService getUserStateService() {
      return userStateService;
  }

  public void setUserStateService(UserStateService userStateService) {
      this.userStateService = userStateService;
  }

  private PriceAttributeByItemHeadDO getPriceAttributeByItems(HttpServletRequest request,
                                                                ActionForm form,
                                                                Integer idKey,
                                                                Byte ptKey)
   throws Exception, IOException, SQLException, ServletException {

    PriceAttributeByItemHeadDO formHDO = new PriceAttributeByItemHeadDO();
    PriceAttributeByItemDO priceAttributeByItem = null;
    ArrayList<PriceAttributeByItemDO> priceAttributeByItems = new ArrayList<PriceAttributeByItemDO>();

    DataSource ds = null;
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;
    ResultSet rs2 = null;

    try {
      ds = getDataSource(request);
      conn = ds.getConnection();

      String query = "SELECT brand_name,"
                   + "size,"
                   + "size_unit "
                   + "FROM itemdictionary "
                   + "WHERE thekey=?";
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

      query = "SELECT thekey,name,"
            + "full_size_id,precedence "
            + "FROM pricetype "
            + "WHERE active_id "
            + "AND domain_id = 0 ";      // Item domain is zero
      if (ptKey != null )
        query = query + "AND thekey = ? ";
      else
        query = query + "ORDER BY precedence";
      ps = conn.prepareStatement(query);
      if (ptKey != null ) {
        cat.debug(this.getClass().getName() + ": pt = " + ptKey.toString());
        ps.setByte(1, ptKey);
      }
      rs = ps.executeQuery();
      
      cat.debug(this.getClass().getName() + ": before query #2");          
      query = "SELECT thekey,size "
            + "FROM priceattributebyitem "
            + "WHERE itemdictionary_key = ? "
            + "AND pricetype_key = ?";
      ps = conn.prepareStatement(query);

      Integer oiCount = new Integer("0");
      cat.debug(this.getClass().getName() + ": before while  ");    
      while ( rs.next() ) {
        cat.debug(this.getClass().getName() + ": inside while ");                     
        priceAttributeByItem = new PriceAttributeByItemDO();

        priceAttributeByItem.setPriceTypeKey(rs.getByte("thekey"));
        priceAttributeByItem.setPriceTypeName(rs.getString("name"));
        priceAttributeByItem.setFullSize(rs.getBoolean("full_size_id"));
        cat.debug(this.getClass().getName() + ": about to set ps values inside while ");           
        ps.setInt(1, idKey);
        ps.setByte(2, rs.getByte("thekey"));
        rs2 = ps.executeQuery();
      cat.debug(this.getClass().getName() + ": open rs2 ");        
        if (rs2.next()) {
          priceAttributeByItem.setKey(rs2.getInt("thekey"));
          priceAttributeByItem.setSize(rs2.getBigDecimal("size"));
        } else {
          priceAttributeByItem.setKey(new Integer("-1"));
          priceAttributeByItem.setSize(new BigDecimal("0"));
        }
        oiCount++;
        priceAttributeByItems.add(priceAttributeByItem);
      }

      formHDO.setPriceAttributeByItemForm(priceAttributeByItems);
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
      if (rs2 != null) {
        try {
          rs2.close();
        }
        catch (SQLException sqle) {
          cat.error(this.getClass().getName() + ": SQLException : " + sqle.getMessage());
        }
        rs2 = null;
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

      // Price adjustment - Passes row id via the request attribute.
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

      // Check if a customer type key via the request attribute.
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

      // Check if a price type key via the request attribute.
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

      // New nested object coding.
      try {
        // Instantiate a formHDO object to populate formHStr.
        PriceAttributeByItemHeadDO formHDO = getPriceAttributeByItems(request, form, theKey, ptKey);
        request.getSession().setAttribute("priceattributebyitemHDO", formHDO);

        ArrayList al = formHDO.getPriceAttributeByItemForm();
        cat.debug(this.getClass().getName() + ": size = " + al.size());
        PriceAttributeByItemDO fDO = (PriceAttributeByItemDO) al.get(0);
        cat.debug(this.getClass().getName() + ": size = " + fDO.getPriceTypeName());


        // Create the wrapper object for priceinvoiceitem.
        PriceAttributeByItemHeadForm formHStr = new PriceAttributeByItemHeadForm();
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