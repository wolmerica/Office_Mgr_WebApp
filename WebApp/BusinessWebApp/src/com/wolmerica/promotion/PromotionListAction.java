/*
 * PromotionListAction.java
 *
 * Created on February 21, 2009, 6:57 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 *
 * 03/07/2006 - Add pagination to employee list display.
 */

package com.wolmerica.promotion;

//--------------------------------------------------------------------------------
// @author Richard Wolschlager
//--------------------------------------------------------------------------------
import com.wolmerica.employee.EmployeesActionMapping;
import com.wolmerica.permission.PermissionListDO;
import com.wolmerica.service.daterange.DateRangeService;
import com.wolmerica.service.daterange.DefaultDateRangeService;
import com.wolmerica.service.property.PropertyService; 
import com.wolmerica.service.property.DefaultPropertyService; 
import com.wolmerica.service.userstate.UserStateService;
import com.wolmerica.service.userstate.DefaultUserStateService;
import com.wolmerica.tools.formatter.FormattingException;
import com.wolmerica.tools.formatter.DateFormatter;

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
import java.util.Date;
import org.json.JSONArray;
import org.json.JSONObject;

import org.apache.log4j.Logger;

public class PromotionListAction extends Action {

  Logger cat = Logger.getLogger("WOWAPP");

  private DateRangeService dateRangeService = new DefaultDateRangeService();
  private PropertyService propertyService = new DefaultPropertyService();
  private UserStateService userStateService = new DefaultUserStateService();

  public DateRangeService getDateRangeService() {
      return dateRangeService;
  }

  public void setDateRangeService(DateRangeService dateRangeService) {
      this.dateRangeService = dateRangeService;
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

  
  private PromotionListHeadDO getPromotionList(HttpServletRequest request,
                                         String promoNameFilter,
                                         String promoCategoryFilter,
                                         String promoFromDate,
                                         String promoToDate,                                         
                                         Integer pageNo,
                                         Byte sourceTypeKey,
                                         Boolean jsonId)
    throws Exception, SQLException {

    PromotionListHeadDO formHDO = new PromotionListHeadDO();
    PromotionListDO promoRow = null;
    ArrayList<PromotionListDO> promoRows = new ArrayList<PromotionListDO>();
    
    ArrayList<PermissionListDO> permissionRows = new ArrayList<PermissionListDO>();

    DataSource ds = null;
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;
    ResultSet rs2 = null;

    try {
      ds = getDataSource(request);
      conn = ds.getConnection();
      
//--------------------------------------------------------------------------------
// Call the DateFormatter to convert the Date to a String like YYYY-MM-DD.
//--------------------------------------------------------------------------------
      DateFormatter dateFormatter = new DateFormatter();
      Date myDate = (Date) dateFormatter.unformat(promoFromDate);
      formHDO.setPromoFromDate(myDate);
      myDate = (Date) dateFormatter.unformat(promoToDate);
      formHDO.setPromoToDate(myDate);      

      String query = "SELECT thekey,"
                   + "name,"
                   + "category,"
                   + "start_date,"
                   + "end_date,"
                   + "discount_rate "
                   + "FROM promotion "
                   + "WHERE name LIKE ? "
                   + "AND category LIKE ? "
                   + "ORDER BY name, category";
      ps = conn.prepareStatement(query);
      ps.setString(1, "%" + promoNameFilter.toUpperCase().trim() + "%");
      ps.setString(2, "%" + promoCategoryFilter.toUpperCase().trim() + "%");
      rs = ps.executeQuery();

      Integer pageMax;
      if (!jsonId)
        pageMax = new Integer(getPropertyService().getCustomerProperties(request,"promotion.list.size"));
      else 
        pageMax = new Integer(getPropertyService().getCustomerProperties(request,"json.list.size"));
      
      Integer firstRecord = ((pageNo.intValue() - 1) * pageMax) + 1;
      Integer lastRecord = firstRecord + (pageMax - 1);

      PermissionListDO permissionRow = null;      
      String prevName = "";
      Boolean releaseId = false;
      Integer recordCount = 0;
      while ( rs.next() ) {
        ++recordCount;
        if ((recordCount >= firstRecord) && (recordCount <= lastRecord)) {
          promoRow = new PromotionListDO();

          promoRow.setKey(rs.getInt("thekey"));
          promoRow.setName(rs.getString("name"));
          promoRow.setCategory(rs.getString("category"));
          promoRow.setStartDate(rs.getDate("start_date"));
          promoRow.setEndDate(rs.getDate("end_date"));
          promoRow.setDiscountRate(rs.getBigDecimal("discount_rate"));

          if (!jsonId) {
//--------------------------------------------------------------------------------
// 07-31-06  Define the permission information in separate class for re-usability.
//--------------------------------------------------------------------------------
            permissionRow = getUserStateService().getUserListToken(request,conn,
                               this.getClass().getName(),promoRow.getKey());
            permissionRows.add(permissionRow);
            
            promoRow.setAllowDeleteId(true);
          }
          promoRows.add(promoRow);
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
      formHDO.setPromoNameFilter(promoNameFilter);
      formHDO.setPromoCategoryFilter(promoCategoryFilter);
      formHDO.setRecordCount(recordCount);
      formHDO.setFirstRecord(firstRecord);
      formHDO.setLastRecord(lastRecord);
      formHDO.setPreviousPage(prevPage);
      formHDO.setCurrentPage(prevPage + 1);
      formHDO.setNextPage(nextPage);
//--------------------------------------------------------------------------------
// A formatter issues exists during the populatin of empty lists.
// A work around is to populate one row when there is an emptyList.
// The cooresponding jsp also needs to check for the phantom row.
//--------------------------------------------------------------------------------
      if (promoRows.isEmpty()) {
        promoRows.add(new PromotionListDO());
        permissionRows.add(getUserStateService().getUserListToken(request,conn,
                           this.getClass().getName(),getUserStateService().getNoKey()));
      }
      formHDO.setPromotionListForm(promoRows);
      formHDO.setPermissionListForm(permissionRows);
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
      String promoNameFilter = "";
      if (!(request.getParameter("promoNameFilter") == null)) {
        promoNameFilter = request.getParameter("promoNameFilter");
      }

      String promoCategoryFilter = "";
      if (!(request.getParameter("promoCategoryFilter") == null)) {
        promoCategoryFilter = request.getParameter("promoCategoryFilter");
      }

      Integer pageNo = new Integer("1");
      if (!(request.getParameter("pageNo") == null)) {
        pageNo = new Integer(request.getParameter("pageNo").toString());
        if (pageNo < 0)
          pageNo = new Integer("1");
      }
      
//--------------------------------------------------------------------------------
// The user can only select the the day.  The hour, minute, second, etc
// values are set to zero for the "promoFromDate" and set to the end of the
// day for the "promoToDate".
//--------------------------------------------------------------------------------
      String promoFromDate = "";
      String promoToDate = "";

      if (request.getParameter("promoFromDate") != null) {
        if (request.getParameter("promoFromDate").length() > 0 ) {
          promoFromDate = request.getParameter("promoFromDate");
        }
      } else {
        promoFromDate = getDateRangeService().getDateToString(getDateRangeService().getYTDFromDate());
      }

      if (request.getParameter("promoToDate") != null) {
        if (request.getParameter("promoToDate").length() > 0 ) {
          promoToDate = request.getParameter("promoToDate");
        }
      } else {
        promoToDate = getDateRangeService().getDateToString(getDateRangeService().getYTDToDate());
      }      
      
      Boolean jsonId = false;
      if (request.getParameter("json") != null) {
        jsonId = new Boolean(request.getParameter("json").toString());
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

      try {
//--------------------------------------------------------------------------------
// Instantiate a formHDO object to populate formHStr.
//--------------------------------------------------------------------------------
        PromotionListHeadDO formHDO = getPromotionList(request,
                                                 promoNameFilter,
                                                 promoCategoryFilter,
                                                 promoFromDate,
                                                 promoToDate,                                                 
                                                 pageNo,
                                                 getUserStateService().getFeatureKey().byteValue(),
                                                 jsonId);        
        if (jsonId) {
          JSONArray jsonItems = new JSONArray();
          JSONObject obj = null;

          if (formHDO.getRecordCount() > 0) {
            ArrayList myArray = formHDO.getPromotionListForm();            
            PromotionListDO myDO = new PromotionListDO();
            for (int i = 0; i < myArray.size(); i++) {
              myDO = (PromotionListDO) myArray.get(i);

              obj = new JSONObject();
              obj.put("id", myDO.getKey());
              obj.put("value", myDO.getName());
              obj.put("info", myDO.getCategory());
              jsonItems.put(obj);
            }
          }
          JSONObject json = new JSONObject();
          json.put("results", jsonItems);

          response.setContentType("application/json");
          response.setHeader("Cache-Control", "no-cache");
          response.setDateHeader("Expires", 0);
          response.setHeader("Pragma", "no-cache");
          response.setStatus(HttpServletResponse.SC_OK);
          response.setContentLength(json.toString().length());
          response.getWriter().write(json.toString());
          response.getWriter().flush();
        }
        else {                
        
          request.getSession().setAttribute("promotionListHDO", formHDO);

//--------------------------------------------------------------------------------
// Create the wrapper object for promotion list.
//--------------------------------------------------------------------------------
          PromotionListHeadForm formHStr = new PromotionListHeadForm();
          formHStr.populate(formHDO);
          form = formHStr;
        }
      }
      catch (FormattingException fe) {
            fe.getMessage();
      }

      if (!jsonId) {
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