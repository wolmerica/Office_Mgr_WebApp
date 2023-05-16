/*
 * PriceByItemLoadAction.java
 *
 * Created on August 30, 2005, 4:58 PM
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

public class PriceByItemLoadAction extends Action {

  Logger cat = Logger.getLogger("WOWAPP");

  private UserStateService userStateService = new DefaultUserStateService();

  public UserStateService getUserStateService() {
      return userStateService;
  }

  public void setUserStateService(UserStateService userStateService) {
      this.userStateService = userStateService;
  }
  

  private void loadPriceByItem(HttpServletRequest request)

    throws Exception, SQLException {

    DataSource ds = null;
    Connection conn = null;
    PreparedStatement ps = null;
    PreparedStatement ps2 = null;
    PreparedStatement ps3 = null;
    ResultSet rs = null;
    ResultSet rs2 = null;
    ItemImportForm form = null;

    try {
      ds = getDataSource(request);
      conn = ds.getConnection();

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
                   + "ORDER BY thekey ";
      ps = conn.prepareStatement(query);
      rs = ps.executeQuery();

      query = "SELECT thekey "
            + "FROM priceattributebyitem "
            + "WHERE itemdictionary_key = ? "
            + "AND pricetype_key = ?";

      cat.warn(this.getClass().getName() + ": Query #2 " + query);
      ps3 = conn.prepareStatement(query);

      query = "INSERT INTO pricebyitem "
            + "(thekey,"
            + "customertype_key,"
            + "priceattributebyitem_key,"
            + "computed_price,"
            + "previous_price,"
            + "over_ride_price,"
            + "update_user,"
            + "update_stamp) "
            + "VALUES (NULL,?,?,?,0,?,'MIGRATION', CURRENT_TIMESTAMP)";

      cat.warn(this.getClass().getName() + ": Insert  " + query);
      ps2 = conn.prepareStatement(query);

      Integer ipaKey;
      Byte priceType = 0;

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

        priceType = 0;
        while (priceType <= 4 ) {
          ps3.setInt(1, form.getKey());
          ps3.setInt(2, priceType);
          rs2 = ps3.executeQuery();
          if (rs2.next()) {
           ipaKey = rs2.getInt("thekey");
            switch(priceType)
            {
              case 0:
                ps2.setInt(1, new Integer("1"));
                ps2.setInt(2, ipaKey);
                ps2.setBigDecimal(3, form.getPoBeef());
                ps2.setBigDecimal(4, form.getOverPoBeef());
                ps2.executeUpdate();

                ps2.setInt(1, new Integer("2"));
                ps2.setInt(2, ipaKey);
                ps2.setBigDecimal(3, form.getPoFarm());
                ps2.setBigDecimal(4, form.getOverPoFarm());
                ps2.executeUpdate();

                ps2.setInt(1, new Integer("3"));
                ps2.setInt(2, ipaKey);
                ps2.setBigDecimal(3, form.getPoDairy());
                ps2.setBigDecimal(4, form.getOverPoDairy());
                ps2.executeUpdate();

                ps2.setInt(1, new Integer("4"));
                ps2.setInt(2, ipaKey);
                ps2.setBigDecimal(3, form.getPoSmallAnimal());
                ps2.setBigDecimal(4, form.getOverPoSmallAnimal());
                ps2.executeUpdate();

                ps2.setInt(1, new Integer("5"));
                ps2.setInt(2, ipaKey);
                ps2.setBigDecimal(3, new BigDecimal("0"));
                ps2.setBigDecimal(4, new BigDecimal("0"));
                ps2.executeUpdate();
                break;
              case 1:
                ps2.setInt(1, new Integer("1"));
                ps2.setInt(2, ipaKey);
                ps2.setBigDecimal(3, form.getDisBeef1());
                ps2.setBigDecimal(4, form.getOverBeef1());
                ps2.executeUpdate();

                ps2.setInt(1, new Integer("2"));
                ps2.setInt(2, ipaKey);
                ps2.setBigDecimal(3, form.getDisFarm1());
                ps2.setBigDecimal(4, form.getOverFarm1());
                ps2.executeUpdate();

                ps2.setInt(1, new Integer("3"));
                ps2.setInt(2, ipaKey);
                ps2.setBigDecimal(3, form.getDisDairy1());
                ps2.setBigDecimal(4, form.getOverDairy1());
                ps2.executeUpdate();

                ps2.setInt(1, new Integer("4"));
                ps2.setInt(2, ipaKey);
                ps2.setBigDecimal(3, form.getDisSmallAnimal1());
                ps2.setBigDecimal(4, form.getOverSmallAnimal1());
                ps2.executeUpdate();

                ps2.setInt(1, new Integer("5"));
                ps2.setInt(2, ipaKey);
                ps2.setBigDecimal(3, new BigDecimal("0"));
                ps2.setBigDecimal(4, new BigDecimal("0"));
                ps2.executeUpdate();
              break;
              case 2:
                ps2.setInt(1, new Integer("1"));
                ps2.setInt(2, ipaKey);
                ps2.setBigDecimal(3, form.getDisBeef2());
                ps2.setBigDecimal(4, form.getOverBeef2());
                ps2.executeUpdate();

                ps2.setInt(1, new Integer("2"));
                ps2.setInt(2, ipaKey);
                ps2.setBigDecimal(3, form.getDisFarm2());
                ps2.setBigDecimal(4, form.getOverFarm2());
                ps2.executeUpdate();

                ps2.setInt(1, new Integer("3"));
                ps2.setInt(2, ipaKey);
                ps2.setBigDecimal(3, form.getDisDairy2());
                ps2.setBigDecimal(4, form.getOverDairy2());
                ps2.executeUpdate();

                ps2.setInt(1, new Integer("4"));
                ps2.setInt(2, ipaKey);
                ps2.setBigDecimal(3, form.getDisSmallAnimal2());
                ps2.setBigDecimal(4, form.getOverSmallAnimal2());
                ps2.executeUpdate();

                ps2.setInt(1, new Integer("5"));
                ps2.setInt(2, ipaKey);
                ps2.setBigDecimal(3, new BigDecimal("0"));
                ps2.setBigDecimal(4, new BigDecimal("0"));
                ps2.executeUpdate();
              break;
              case 3:
                ps2.setInt(1, new Integer("1"));
                ps2.setInt(2, ipaKey);
                ps2.setBigDecimal(3, form.getDisBeef3());
                ps2.setBigDecimal(4, form.getOverBeef3());
                ps2.executeUpdate();

                ps2.setInt(1, new Integer("2"));
                ps2.setInt(2, ipaKey);
                ps2.setBigDecimal(3, form.getDisFarm3());
                ps2.setBigDecimal(4, form.getOverFarm3());
                ps2.executeUpdate();

                ps2.setInt(1, new Integer("3"));
                ps2.setInt(2, ipaKey);
                ps2.setBigDecimal(3, form.getDisDairy3());
                ps2.setBigDecimal(4, form.getOverDairy3());
                ps2.executeUpdate();

                ps2.setInt(1, new Integer("4"));
                ps2.setInt(2, ipaKey);
                ps2.setBigDecimal(3, form.getDisSmallAnimal3());
                ps2.setBigDecimal(4, form.getOverSmallAnimal3());
                ps2.executeUpdate();

                ps2.setInt(1, new Integer("5"));
                ps2.setInt(2, ipaKey);
                ps2.setBigDecimal(3, new BigDecimal("0"));
                ps2.setBigDecimal(4, new BigDecimal("0"));
                ps2.executeUpdate();
                break;
            }
          }
          ++priceType;
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

      cat.warn(this.getClass().getName() + ": START pbi Load ");
      loadPriceByItem(request);
      cat.warn(this.getClass().getName() + ": FINISH pbi Load ");

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