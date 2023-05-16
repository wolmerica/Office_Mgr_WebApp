/*
 * CustomerTypeGetAction.java
 *
 * Created on March 14, 2006, 6:36 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 * 12/21/2005 Implement tools.formatter library.
 */

package com.wolmerica.customertype;

//--------------------------------------------------------------------------------
// @author Richard Wolschlager
//--------------------------------------------------------------------------------
import com.wolmerica.customer.CustomerActionMapping;
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
import java.sql.Timestamp;
import java.util.Date;
import java.math.BigDecimal;

import org.apache.log4j.Logger;

public class CustomerTypeGetAction extends Action {

  Logger cat = Logger.getLogger("CUSTAPP");

  private CustomerTypeDO buildCustomerTypeForm(HttpServletRequest request,
                                                 Byte ctKey)
   throws Exception, SQLException {

    String user = null;
    DataSource ds = null;
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;
    CustomerTypeDO formDO = null;

    try {
      ds = getDataSource(request);
      conn = ds.getConnection();

      String query = "SELECT thekey,name,"
                    + "precedence,bluebook_id,"
                    + "pricesheet_id,active_id,"
                    + "sold_by_key,"
                    + "create_user,create_stamp,"
                    + "update_user,update_stamp "
                    + "FROM customertype "
                    + "WHERE thekey=?";

      ps = conn.prepareStatement(query);

      ps.setByte(1, ctKey);
      rs = ps.executeQuery();

      if ( rs.next() ) {

        formDO = new CustomerTypeDO();

        formDO.setKey(rs.getByte("thekey"));
        formDO.setName(rs.getString("name"));
        formDO.setPrecedence(rs.getByte("precedence"));
        formDO.setBlueBookId(rs.getBoolean("bluebook_id"));
        formDO.setPriceSheetId(rs.getBoolean("pricesheet_id"));
        formDO.setSoldByKey(rs.getInt("sold_by_key"));
        formDO.setActiveId(rs.getBoolean("active_id"));
        formDO.setCreateUser(rs.getString("create_user"));
        formDO.setCreateStamp(rs.getTimestamp("create_stamp"));
        formDO.setUpdateUser(rs.getString("update_user"));
        formDO.setUpdateStamp(rs.getTimestamp("update_stamp"));

//--------------------------------------------------------------------------------
// Get the sold by name with a look-up to customer with sold by key.
//--------------------------------------------------------------------------------
        query = "SELECT client_name "
              + "FROM customer "
              + "WHERE thekey = ?";
        cat.debug(this.getClass().getName() + ": getSoldByKey() = " + formDO.getSoldByKey());
        ps = conn.prepareStatement(query);
        ps.setInt(1, formDO.getSoldByKey());
        rs = ps.executeQuery();
        if ( rs.next() ) {
          formDO.setSoldByName(rs.getString("client_name"));
        }
        else {
          throw new Exception("Customer SELECT sold by name not found!");
        }
      }
      else {
        throw new Exception("CustomerType " + ctKey.toString() + " not found!");
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
    return formDO;
  }

	public ActionForward execute(ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws IOException, ServletException {

		// Default target to success
    String target = "success";

    CustomerActionMapping customerMapping =
      (CustomerActionMapping)mapping;

//--------------------------------------------------------------------------------
// Does this action require the user to login.
//--------------------------------------------------------------------------------
    if ( customerMapping.isLoginRequired() ) {

      if ( request.getSession().getAttribute("ACCTKEY") == null ) {
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
      Byte theKey = null;
      if (!(request.getParameter("key") == null)) {
        theKey = new Byte(request.getParameter("key"));
      }
      else {
        if (!(request.getAttribute("key") == null)) {
          theKey = new Byte(request.getAttribute("key").toString());
        }
        else {
          throw new Exception("Request getParameter/getAttribute [key] not found!");
        }
      }
      cat.debug(this.getClass().getName() + ": get[key] = " + theKey.toString());

      try {
//--------------------------------------------------------------------------------
// Instantiate a formDO object to populate formStr.
//--------------------------------------------------------------------------------
        CustomerTypeDO formDO = buildCustomerTypeForm(request, theKey);
        request.getSession().setAttribute("customerTypeDO", formDO);
        CustomerTypeForm formStr = new CustomerTypeForm();
        formStr.populate(formDO);

        form = formStr;
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