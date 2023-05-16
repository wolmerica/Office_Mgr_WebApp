/*
 * BundleLoadToInvoiceAction.java
 *
 * Created on April 4, 2007 7:35 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package com.wolmerica.bundle;

//--------------------------------------------------------------------------------
// @author Richard Wolschlager
//--------------------------------------------------------------------------------
import com.wolmerica.employee.EmployeesActionMapping;
import com.wolmerica.bundledetail.BundleDetailDO;
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

import org.apache.log4j.Logger;

public class BundleLoadToInvoiceAction extends Action {

  Logger cat = Logger.getLogger("WOWAPP");

  private UserStateService userStateService = new DefaultUserStateService();

  public UserStateService getUserStateService() {
      return userStateService;
  }

  public void setUserStateService(UserStateService userStateService) {
      this.userStateService = userStateService;
  }

  private BundleDetailDO getBundleDetail(HttpServletRequest request,
                                         Integer buKey,
                                         Integer budCnt)
    throws Exception, SQLException {

    BundleDetailDO bundleDetail = new BundleDetailDO();
    
    DataSource ds = null;
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;
    
    try {
      ds = getDataSource(request);
      conn = ds.getConnection();

//--------------------------------------------------------------------------------
// Retreive a bundle detail row from the designated bundle.
//--------------------------------------------------------------------------------
      String query = "SELECT thekey,"
                   + "sourcetype_key,"
                   + "source_key,"
                   + "order_qty "
                   + "FROM bundledetail "
                   + "WHERE bundle_key = ? "
                   + "ORDER by sourcetype_key DESC, thekey";
          cat.debug(this.getClass().getName() + ": query  : " + query);       
      ps = conn.prepareStatement(query);
      ps.setInt(1, buKey);
      rs = ps.executeQuery();

      Integer recordCount = 0;
      while ( rs.next() ) {
        ++recordCount;
        if (recordCount == budCnt) {
          cat.debug(this.getClass().getName() + ": loading bud  : " + budCnt);
          bundleDetail.setKey(rs.getInt("thekey"));
          bundleDetail.setBundleKey(buKey);
          bundleDetail.setSourceTypeKey(rs.getByte("sourcetype_key"));
          bundleDetail.setSourceKey(rs.getInt("source_key"));
          bundleDetail.setOrderQty(rs.getShort("order_qty"));
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
    return bundleDetail;
  }

    @Override
  public ActionForward execute(ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws IOException, ServletException {
//--------------------------------------------------------------------------------
// Default target to success
//--------------------------------------------------------------------------------
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
// Customer invoice key in which the bundle detail will populate.
//--------------------------------------------------------------------------------
      Integer theKey = null;
      if (request.getAttribute("key") != null) {
        theKey = new Integer(request.getAttribute("key").toString());
        request.getSession().setAttribute("CIKEY", theKey.toString());
      }
      else {
        if (request.getSession().getAttribute("CIKEY") != null) {
          theKey = new Integer(request.getSession().getAttribute("CIKEY").toString());
        }
        else {
          throw new Exception("Request getAttribute [key] not found!");
	    }
      }

//--------------------------------------------------------------------------------
// Bundle key that will be used to populate the customer invoice.
// The first call will include the request parameter value for buKey.
//--------------------------------------------------------------------------------
      Integer buKey = null;
      if (request.getParameter("buKey") != null) {
        buKey = new Integer(request.getParameter("buKey").toString());
        request.getSession().setAttribute("BUKEY", buKey.toString());
      }
      else {
        if (request.getSession().getAttribute("BUKEY") != null) {
          buKey = new Integer(request.getSession().getAttribute("BUKEY").toString());
        }
        else {
          throw new Exception("Request getParameter/getAttribute [buKey] not found!");
	    }
      }

//--------------------------------------------------------------------------------
// Bundle count will identify the record that will be added to the customer invoice.
// The first record will be identified by one and we increment from that point.
//--------------------------------------------------------------------------------
      Integer budCnt = 1;
      if (request.getSession().getAttribute("BUDCNT") == null) {
        request.getSession().setAttribute("BUDCNT", budCnt.toString());
      }
      else {
        budCnt = new Integer(request.getSession().getAttribute("BUDCNT").toString());
        budCnt = budCnt + 1;
        request.getSession().setAttribute("BUDCNT", budCnt.toString());
      }

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

//--------------------------------------------------------------------------------
// Retreive a bundle detail row from the designated bundle.
//--------------------------------------------------------------------------------
      cat.debug(this.getClass().getName() + ": key    : " + theKey.toString());
      cat.debug(this.getClass().getName() + ": buKey  : " + buKey.toString());
      cat.debug(this.getClass().getName() + ": budCnt : " + budCnt.toString());

      BundleDetailDO formDO = getBundleDetail(request,
                                              buKey,
                                              budCnt);

//--------------------------------------------------------------------------------
// Assign the foward target according to the existence of bundle detail data.
// The default target is set to "success" so we redirect as per logic below.
//--------------------------------------------------------------------------------
      cat.debug(this.getClass().getName() + ": " + target + " : " + formDO.getSourceTypeKey().toString());      
      if (formDO != null) {
        if (formDO.getSourceTypeKey() == 3) {
          target = new String("item");
          request.setAttribute("idKey", formDO.getSourceKey().toString());
          request.setAttribute("orderQty", formDO.getOrderQty().toString());
        }
        if (formDO.getSourceTypeKey() == 6) {
          target = new String("service");
          request.setAttribute("pbsKey", formDO.getSourceKey().toString());
          request.setAttribute("orderQty", formDO.getOrderQty().toString());          
        }
      }
      request.setAttribute("availableQty", "1000");
      request.setAttribute("key", theKey);

      cat.debug(this.getClass().getName() + ": " + target);
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
