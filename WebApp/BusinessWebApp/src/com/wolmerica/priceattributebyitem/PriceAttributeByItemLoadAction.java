/*
 * PriceAttributeByItemLoadAction.java
 *
 * Created on August 30, 2005, 4:58 PM
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
import com.wolmerica.tools.ItemImportForm;

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

public class PriceAttributeByItemLoadAction extends Action {

  Logger cat = Logger.getLogger("WOWAPP");

  private UserStateService userStateService = new DefaultUserStateService();

  public UserStateService getUserStateService() {
      return userStateService;
  }

  public void setUserStateService(UserStateService userStateService) {
      this.userStateService = userStateService;
  }
  

  private void loadPriceAttributeByItem(HttpServletRequest request)

    throws Exception, SQLException {

    DataSource ds = null;
    Connection conn = null;
    PreparedStatement ps = null;
    PreparedStatement ps2 = null;
    ResultSet rs = null;
    ItemImportForm form = null;

    try {
      String query = "SELECT thekey,"
                   + "pobeef,"
                   + "pofarm,"
                   + "podairy,"
                   + "posa,"
                   + "orpobeef,"
                   + "orpofarm,"
                   + "orpodairy,"
                   + "orposa,"
                   + "resale1,"
                   + "disbeef1,"
                   + "disfarm1,"
                   + "disdairy1,"
                   + "dissa1,"
                   + "ordisbeef1,"
                   + "ordisfarm1,"
                   + "ordisdairy1,"
                   + "ordissa1,"
                   + "resale2,"
                   + "disbeef2,"
                   + "disfarm2,"
                   + "disdairy2,"
                   + "dissa2,"
                   + "ordisbeef2,"
                   + "ordisfarm2,"
                   + "ordisdairy2,"
                   + "ordissa2,"
                   + "resale3,"
                   + "disbeef3,"
                   + "disfarm3,"
                   + "disdairy3,"
                   + "dissa3,"
                   + "ordisbeef3,"
                   + "ordisfarm3,"
                   + "ordisdairy3,"
                   + "ordissa3 "
                   + "FROM itemimport "
                   + "ORDER BY thekey";

      ds = getDataSource(request);
      conn = ds.getConnection();
      ps = conn.prepareStatement(query);

      rs = ps.executeQuery();

      query = "INSERT INTO priceattributebyitem"
            + "(thekey,"
            + "itemdictionary_key, "
            + "pricetype_key,"
            + "size,"
            + "update_user,"
            + "update_stamp) "
            + "VALUES (NULL,?,?,?,'MIGRATION', CURRENT_TIMESTAMP)";

      cat.warn(this.getClass().getName() + ": Insert #1 " + query);
      ps = conn.prepareStatement(query);

      while ( rs.next() ) {

        form = new ItemImportForm();

        form.setKey(rs.getInt("thekey"));
        form.setPoBeef(rs.getBigDecimal("pobeef"));
        form.setPoFarm(rs.getBigDecimal("pofarm"));
        form.setPoDairy(rs.getBigDecimal("podairy"));
        form.setPoSmallAnimal(rs.getBigDecimal("posa"));
        form.setOverPoBeef(rs.getBigDecimal("orpobeef"));
        form.setOverPoFarm(rs.getBigDecimal("orpofarm"));
        form.setOverPoDairy(rs.getBigDecimal("orpodairy"));
        form.setOverPoSmallAnimal(rs.getBigDecimal("orposa"));
        form.setResale1(rs.getBigDecimal("resale1"));
        form.setDisBeef1(rs.getBigDecimal("disbeef1"));
        form.setDisFarm1(rs.getBigDecimal("disfarm1"));
        form.setDisDairy1(rs.getBigDecimal("disdairy1"));
        form.setDisSmallAnimal1(rs.getBigDecimal("dissa1"));
        form.setOverBeef1(rs.getBigDecimal("ordisbeef1"));
        form.setOverFarm1(rs.getBigDecimal("ordisfarm1"));
        form.setOverDairy1(rs.getBigDecimal("ordisdairy1"));
        form.setOverSmallAnimal1(rs.getBigDecimal("ordissa1"));
        form.setResale2(rs.getBigDecimal("resale2"));
        form.setDisBeef2(rs.getBigDecimal("disbeef2"));
        form.setDisFarm2(rs.getBigDecimal("disfarm2"));
        form.setDisDairy2(rs.getBigDecimal("disdairy2"));
        form.setDisSmallAnimal2(rs.getBigDecimal("dissa2"));
        form.setOverBeef2(rs.getBigDecimal("ordisbeef2"));
        form.setOverFarm2(rs.getBigDecimal("ordisfarm2"));
        form.setOverDairy2(rs.getBigDecimal("ordisdairy2"));
        form.setOverSmallAnimal2(rs.getBigDecimal("ordissa2"));
        form.setResale3(rs.getBigDecimal("resale3"));
        form.setDisBeef3(rs.getBigDecimal("disbeef3"));
        form.setDisFarm3(rs.getBigDecimal("disfarm3"));
        form.setDisDairy3(rs.getBigDecimal("disdairy3"));
        form.setDisSmallAnimal3(rs.getBigDecimal("dissa3"));
        form.setOverBeef3(rs.getBigDecimal("ordisbeef3"));
        form.setOverFarm3(rs.getBigDecimal("ordisfarm3"));
        form.setOverDairy3(rs.getBigDecimal("ordisdairy3"));
        form.setOverSmallAnimal3(rs.getBigDecimal("ordissa3"));

        cat.debug(": Insert pabi = " + form.getKey().toString());
        // Purchase Order
        ps.setInt(1, form.getKey());
        ps.setInt(2, new Integer("0"));
        ps.setBigDecimal(3, form.getResale1());
        ps.executeUpdate();

        // Resale #1
        ps.setInt(1, form.getKey());
        ps.setInt(2, new Integer("1"));
        ps.setBigDecimal(3, form.getResale1());
        ps.executeUpdate();

        // Resale #2
        if (form.getResale2().compareTo(new BigDecimal("0")) > 0 ) {
          ps.setInt(1, form.getKey());
          ps.setInt(2, new Integer("2"));
          ps.setBigDecimal(3, form.getResale2());
          ps.executeUpdate();
        }

        // Resale #3
        if (form.getResale3().compareTo(new BigDecimal("0")) > 0 ) {
            ps.setInt(1, form.getKey());
            ps.setInt(2, new Integer("3"));
            ps.setBigDecimal(3, form.getResale3());
            ps.executeUpdate();
        }
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

      cat.warn(this.getClass().getName() + ": START pabi Load ");
      loadPriceAttributeByItem(request);
      cat.warn(this.getClass().getName() + ": FINISH pabi Load ");

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