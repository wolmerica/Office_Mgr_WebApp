/*
 * BundleDetailGetAction.java
 *
 * Created on September 9, 2005, 8:14 AM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 *
 */

package com.wolmerica.bundledetail;

//--------------------------------------------------------------------------------
// @author Richard Wolschlager
//--------------------------------------------------------------------------------
import com.wolmerica.employee.EmployeesActionMapping;
import com.wolmerica.service.itemandsrv.ItemAndSrvService;
import com.wolmerica.service.itemandsrv.DefaultItemAndSrvService;
import com.wolmerica.service.property.PropertyService;
import com.wolmerica.service.property.DefaultPropertyService;
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
import java.math.MathContext;

import org.apache.log4j.Logger;

public class BundleDetailGetAction extends Action {

  Logger cat = Logger.getLogger("WOWAPP");

  private ItemAndSrvService itemAndSrvService = new DefaultItemAndSrvService();
  private PropertyService propertyService = new DefaultPropertyService();
  private UserStateService userStateService = new DefaultUserStateService();

  public ItemAndSrvService getItemAndSrvService() {
      return itemAndSrvService;
  }

  public void setItemAndSrvService(ItemAndSrvService itemAndSrvService) {
      this.itemAndSrvService = itemAndSrvService;
  }

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


  private BundleDetailListHeadDO getBundleDetail(HttpServletRequest request,
                                                 Integer buKey,
                                                 Integer pageNo)
   throws Exception, SQLException {

    BundleDetailListHeadDO formHDO = new BundleDetailListHeadDO();
    BundleDetailDO bundleDetail = null;
    ArrayList<BundleDetailDO> bundleDetails = new ArrayList<BundleDetailDO>();

    DataSource ds = null;
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    boolean ciServiceTaxId = false;
    boolean ciiServiceTaxId = false;
    BigDecimal bdOrderQty = new BigDecimal("0");
    MathContext mc = new MathContext(5);

    try {
      ds = getDataSource(request);
      conn = ds.getConnection();

//--------------------------------------------------------------------------------
// Get the customer invoice service records for all the master records.
//--------------------------------------------------------------------------------
      String query = "SELECT thekey,"
                   + "bundle_key,"
                   + "sourcetype_key,"
                   + "source_key,"
                   + "order_qty,"
                   + "create_user,"
                   + "create_stamp,"
                   + "update_user,"
                   + "update_stamp "              
                   + "FROM bundledetail "
                   + "WHERE bundle_key = ? "
                   + "ORDER by sourcetype_key, thekey";
      ps = conn.prepareStatement(query);
      ps.setInt(1, buKey);
      rs = ps.executeQuery();

      Integer pageMax = new Integer(getPropertyService().getCustomerProperties(request,"bundledetail.list.size"));
      Integer firstRecord = ((pageNo.intValue() - 1) * pageMax) + 1;
      Integer lastRecord = firstRecord + (pageMax - 1);
      Short minQty = 0;
      Short maxQty = 0;

      Integer recordCount = 0;
      while ( rs.next() ) {
        ++recordCount;
        if ((recordCount >= firstRecord) && (recordCount <= lastRecord)) {
          bundleDetail = new BundleDetailDO();
          bundleDetail.setKey(rs.getInt("thekey"));
          bundleDetail.setBundleKey(buKey);
          bundleDetail.setSourceTypeKey(rs.getByte("sourcetype_key"));
          bundleDetail.setSourceKey(rs.getInt("source_key"));
          bundleDetail.setOrderQty(rs.getShort("order_qty"));
          bundleDetail.setCreateUser(rs.getString("create_user"));
          bundleDetail.setCreateStamp(rs.getTimestamp("create_stamp"));          
          bundleDetail.setUpdateUser(rs.getString("update_user"));
          bundleDetail.setUpdateStamp(rs.getTimestamp("update_stamp"));          

          bundleDetail = getItemAndSrvService().getItemOrServiceName(conn, bundleDetail);

          bundleDetails.add(bundleDetail);
        }
      }
//--------------------------------------------------------------------------------
// Pagination logic to figure out what the previous and next page
// values will be for the next screen to be displayed.
//--------------------------------------------------------------------------------
      Integer prevPage=0;
      Integer nextPage=0;
      if (recordCount > lastRecord)
        nextPage = pageNo + 1;
      else
        lastRecord = recordCount;
      if (firstRecord > 1)
        prevPage = pageNo - 1;
      if (recordCount == 0)
        firstRecord=0;
//--------------------------------------------------------------------------------
// Store the filter, row count, previous and next page number values.
//--------------------------------------------------------------------------------
      formHDO.setBundleKey(buKey);
      formHDO.setRecordCount(recordCount);
      formHDO.setFirstRecord(firstRecord);
      formHDO.setLastRecord(lastRecord);
      formHDO.setPreviousPage(prevPage);
      formHDO.setNextPage(nextPage);
//--------------------------------------------------------------------------------
// A formatter issues exists during the population of empty lists.
// A work around is to populate one row when there is an emptyList.
// The cooresponding jsp also needs to check for the phantom row.
//--------------------------------------------------------------------------------
      if (bundleDetails.isEmpty()) {
        bundleDetails.add(new BundleDetailDO());
      }
      formHDO.setBundleDetailForm(bundleDetails);
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
   throws Exception, IOException, SQLException, ServletException {

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

      Integer pageNo = new Integer("1");
      if (!(request.getParameter("pageNo") == null)) {
        pageNo = new Integer(request.getParameter("pageNo").toString());
        if (pageNo < 0)
          pageNo = new Integer("1");
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

      try {
//--------------------------------------------------------------------------------
// Instantiate a formHDO object to populate formHStr.
//--------------------------------------------------------------------------------
        BundleDetailListHeadDO formHDO = getBundleDetail(request,
                                                         theKey,
                                                         pageNo);

        formHDO.setPermissionStatus(usToken);

        request.getSession().setAttribute("bundledetailHDO", formHDO);
//--------------------------------------------------------------------------------
// Create the wrapper object for customerinvoiceitem.
//--------------------------------------------------------------------------------
        BundleDetailListHeadForm formHStr = new BundleDetailListHeadForm();
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
