/*
 * CustomerAttributeByItemLoadAction.java
 *
 * Created on August 30, 2005, 4:58 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package com.wolmerica.customerattributebyitem;

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

public class CustomerAttributeByItemLoadAction extends Action {

  Logger cat = Logger.getLogger("WOWAPP");

  private UserStateService userStateService = new DefaultUserStateService();

  public UserStateService getUserStateService() {
      return userStateService;
  }

  public void setUserStateService(UserStateService userStateService) {
      this.userStateService = userStateService;
  }
  

  private void loadCustomerAttributeByItem(HttpServletRequest request)

    throws Exception, SQLException {

    DataSource ds = null;
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;
    ItemImportForm form = null;

    try {
      ds = getDataSource(request);
      conn = ds.getConnection();

      String query = "SELECT thekey,"
                   + "label_cost,"
                   + "label_beef,"
                   + "label_dairy,"
                   + "mubeef,"
                   + "mufarm,"
                   + "mudairy,"
                   + "musa,"
                   + "muadd,"
                   + "acfarm,"
                   + "acsa,"
                   + "perbeef,"
                   + "perfarm,"
                   + "perdairy,"
                   + "persa,"
                   + "discount_threshold "
                   + "FROM itemimport ";
      ps = conn.prepareStatement(query);
      rs = ps.executeQuery();

      query = "INSERT INTO customerattributebyitem "
            + "(thekey,"
            + "customertype_key,"
            + "itemdictionary_key,"
            + "label_cost,"
            + "admin_cost,"
            + "markup_rate,"
            + "add_markup_rate,"
            + "discount_rate,"
            + "discount_threshold) "
            + "VALUES (NULL,?,?,?,?,?,?,?,?)";
        cat.warn(this.getClass().getName() + ": Insert #1 " + query);
        ps = conn.prepareStatement(query);

      while ( rs.next() ) {

        form = new ItemImportForm();

        form.setKey(rs.getInt("thekey"));
        form.setLabelCost(rs.getBigDecimal("label_cost"));
        form.setLabelBeef(rs.getBigDecimal("label_beef"));
        form.setLabelDairy(rs.getBigDecimal("label_dairy"));
        form.setMuBeef(rs.getBigDecimal("mubeef"));
        form.setMuFarm(rs.getBigDecimal("mufarm"));
        form.setMuDairy(rs.getBigDecimal("mudairy"));
        form.setMuSmallAnimal(rs.getBigDecimal("musa"));
        form.setMuAdditional(new BigDecimal("0.0"));
        form.setAddFarm(rs.getBigDecimal("acfarm"));
        form.setAddSmallAnimal(rs.getBigDecimal("acsa"));
        form.setPerBeef(rs.getBigDecimal("perbeef"));
        form.setPerFarm(rs.getBigDecimal("perfarm"));
        form.setPerDairy(rs.getBigDecimal("perdairy"));
        form.setPerSmallAnimal(rs.getBigDecimal("persa"));
        form.setDiscount(rs.getBigDecimal("discount_threshold"));

        // Tech
        ps.setShort(1, new Short("1"));
        ps.setInt(2, form.getKey());
        ps.setBigDecimal(3, form.getLabelCost());
        ps.setBigDecimal(4, form.getAddFarm());
        ps.setBigDecimal(5, form.getMuFarm());
        ps.setBigDecimal(6, form.getMuAdditional());
        ps.setBigDecimal(7, form.getPerFarm());
        ps.setBigDecimal(8, form.getDiscount());
        ps.executeUpdate();

        // Farm
        ps.setShort(1, new Short("2"));
        ps.setInt(2, form.getKey());
        ps.setBigDecimal(3, form.getLabelCost());
        ps.setBigDecimal(4, form.getAddFarm());
        ps.setBigDecimal(5, form.getMuFarm());
        ps.setBigDecimal(6, form.getMuAdditional());
        ps.setBigDecimal(7, form.getPerFarm());
        ps.setBigDecimal(8, form.getDiscount());
        ps.executeUpdate();

        // Dairy
        ps.setShort(1, new Short("3"));
        ps.setInt(2, form.getKey());
        ps.setBigDecimal(3, form.getLabelDairy());
        ps.setBigDecimal(4, new BigDecimal("0.00"));
        ps.setBigDecimal(5, form.getMuDairy());
        ps.setBigDecimal(6, form.getMuAdditional());
        ps.setBigDecimal(7, form.getPerDairy());
        ps.setBigDecimal(8, form.getDiscount());
        ps.executeUpdate();

        // Small Animal
        ps.setShort(1, new Short("4"));
        ps.setInt(2, form.getKey());
        ps.setBigDecimal(3, form.getLabelCost());
        ps.setBigDecimal(4, form.getAddSmallAnimal());
        ps.setBigDecimal(5, form.getMuSmallAnimal());
        ps.setBigDecimal(6, form.getMuAdditional());
        ps.setBigDecimal(7, form.getPerSmallAnimal());
        ps.setBigDecimal(8, form.getDiscount());
        ps.executeUpdate();
      }

        // Beef
        ps.setShort(1, new Short("5"));
        ps.setInt(2, form.getKey());
        ps.setBigDecimal(3, form.getLabelBeef());
        ps.setBigDecimal(4, new BigDecimal("0.00"));
        ps.setBigDecimal(5, form.getMuBeef());
        ps.setBigDecimal(6, form.getMuAdditional());
        ps.setBigDecimal(7, form.getPerBeef());
        ps.setBigDecimal(8, form.getDiscount());
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

    cat.warn(this.getClass().getName() + ": EXECUTE ");
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

      cat.warn(this.getClass().getName() + ": START cabi Load ");
      loadCustomerAttributeByItem(request);
      cat.warn(this.getClass().getName() + ": FINISH cabi Load ");
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