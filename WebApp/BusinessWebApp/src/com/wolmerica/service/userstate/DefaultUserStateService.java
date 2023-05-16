package com.wolmerica.service.userstate;

import com.wolmerica.service.property.PropertyService;
import com.wolmerica.service.property.DefaultPropertyService;
import com.wolmerica.permission.PermissionListDO;
import javax.servlet.http.HttpServletRequest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Enumeration;
import java.sql.Timestamp;
import java.util.Date;

import org.apache.log4j.Logger;

/**
 * @author  Richard Wolschlager
 * @date    Sept 4, 2010
 */

public class DefaultUserStateService implements UserStateService {

  Logger cat = Logger.getLogger("WOWAPP");

  private PropertyService propertyService = new DefaultPropertyService();

  public PropertyService getPropertyService() {
      return propertyService;
  }

  public void setPropertyService(PropertyService propertyService) {
      this.propertyService = propertyService;
  }


  static final int noAction = 0;
  static final int listAction = 1;
  static final int readOnlyAction = 2;
  static final int editAction = 3;
  static final int addAction = 4;
  static final int deleteAction = 5;
  static final int maxAction = 5;
  static final int DIVISOR = 10;
  private final Integer NOKEY = new Integer(0);
  private final String LOGIN = "LOGIN";
  private final String LOGOUT = "LOGOUT";
  private final String USERNAME = "USERNAME";
  private final String ADMIN = "ADMIN";
  private final String USER = "USER";
  private final String TOKEN = "TOKEN";
  private final String LICENSE = "LICENSE";
  private final String EXPIRED = "EXPIRED";
  private final String DEFAULTINSTANCENAME = "DEFAULTINSTANCENAME";
  private final String INSTANCENAME = "INSTANCENAME";
  private final String PERMISSION = "PERMISSION";
  private final String LOCKED = "LOCKED";
  private final String READONLY = "READONLY";
  private final String PROHIBITED = "PROHIBITED";
  private final String DISABLEEDIT = "disableEdit";
  private final String ACCESSDENIED = "Access denied...";
  private Integer featureKey = null;

  public Integer getNoKey() {
    return NOKEY;
  }

  public String getLogin() {
    return LOGIN;
  }

  public String getUserName() {
    return USERNAME;
  }

  public String getAdmin() {
    return ADMIN;
  }

  public String getUser() {
    return USER;
  }

  public String getPermission() {
    return PERMISSION;
  }

  public String getToken() {
    return TOKEN;
  }

  public String getLicense() {
    return LICENSE;
  }

  public String getExpired() {
    return EXPIRED;
  }

  public String getDefaultInstanceName() {
    return DEFAULTINSTANCENAME;
  }

  public String getInstanceName() {
    return INSTANCENAME;
  }

  public String getLocked() {
    return LOCKED;
  }

  public String getReadOnly() {
    return READONLY;
  }

  public String getProhibited() {
    return PROHIBITED;
  }

  public String getDisableEdit() {
    return DISABLEEDIT;
  }

  public String getAccessDenied() {
    return ACCESSDENIED;
  }

  public Integer getFeatureKey() {
      return featureKey;
  }

  public void setFeatureKey(Integer featureKey) {
      this.featureKey = featureKey;
  }

//--------------------------------------------------------------------------------
// Author: Richard Wolschlager
// Date..: 07/22/2005
// Method: CreateUserToken
// 1) Connection
// 2) User Name
// 3) User IP Address
//--------------------------------------------------------------------------------
  public Integer createUserToken(Connection conn,
                                 String userName,
                                 String userIpAddress)
   throws Exception, SQLException {

    PreparedStatement ps = null;
    ResultSet rs = null;

    Integer usKey = 1;

    try {
//--------------------------------------------------------------------------------
// Prepare SQL statement to get the maximum key from the user state table.
//--------------------------------------------------------------------------------
      String query = "SELECT COUNT(*) AS us_cnt, MAX(thekey)+1 AS us_key "
                   + "FROM userstate";
      ps = conn.prepareStatement(query);
//--------------------------------------------------------------------------------
// Query to retrieve the maximum key value in the user state table.
//--------------------------------------------------------------------------------
      rs = ps.executeQuery();
      if ( rs.next() ) {
        usKey = rs.getInt("us_cnt");
        if ( usKey > 0 ) {
          usKey = rs.getInt("us_key");
        }
      }
      else {
        throw new Exception("UserState MAX() not found!");
      }
//--------------------------------------------------------------------------------
// Prepare SQL statement to insert a row into the user state table.
// Exclude Columns: feature_type and feature_key
//--------------------------------------------------------------------------------
      query = "INSERT INTO userstate "
            + "(thekey,"
            + "user_name,"
            + "ip_address,"
            + "create_stamp,"
            + "update_stamp) "
            + "VALUES (?,?,?,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP)";
      ps = conn.prepareStatement(query);
      ps.setInt(1, usKey);
      ps.setString(2, userName);
      ps.setString(3, userIpAddress);
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
    }
    return usKey;
  }


//--------------------------------------------------------------------------------
// Author: Richard Wolschlager
// Date..: 07/22/2005
// Method: GetUserToken
// 1) HttpServletRequest
// 2) Connection
// 3) Action name of feature
// 4) Record key accessed by feature
//--------------------------------------------------------------------------------
  public String getUserToken(HttpServletRequest request,
                             Connection conn,
                             String fAction,
                             Integer fKey)
   throws Exception, SQLException {

    PreparedStatement ps = null;
    ResultSet rs = null;
    String usName = "";

    cat.debug(this.getClass().getName() + ": fAction.: " + fAction);
    cat.debug(this.getClass().getName() + ": fKey....: " + fKey);

    try {
      Integer usKey = (Integer) request.getSession().getAttribute(TOKEN);
      String permSlip = (String) request.getSession().getAttribute(PERMISSION);
//--------------------------------------------------------------------------------
// Map the fAction string into the appropriate fType numeric value.
// Mapping example..: com.wolmerica.menu.MenuEntryAction=1
//--------------------------------------------------------------------------------
      Integer fType = new Integer(getPropertyService().getFeatureProperties(fAction));
//--------------------------------------------------------------------------------
// The ten's digit represents the feature identity.   20 represents employee
// The one's digit represents the feature fuctionality
// Employee Permission			Action fType
// ===================			============
// 1 - list                                 11
// 2 - edit - readonly                      12
// 3 - edit                                 13
// 4 - add                                  14
// 5 - delete                               15
//--------------------------------------------------------------------------------
      featureKey = fType / DIVISOR;
      int fId = fType % DIVISOR;
      cat.debug(this.getClass().getName() + ": featureKey=" + featureKey.toString());
//--------------------------------------------------------------------------------
// An expired license will still have "Readonly" access to the application.
//--------------------------------------------------------------------------------
      int usPerm = DefaultUserStateService.readOnlyAction;
      if (!(request.getSession().getAttribute(this.LICENSE).toString().equalsIgnoreCase(this.EXPIRED)))
        usPerm = new Integer(permSlip.substring(featureKey.intValue(), featureKey.intValue()+1));
      cat.debug(this.getClass().getName() + ": usPerm=" + usPerm);
      boolean dbUpdate = false;
//--------------------------------------------------------------------------------
// The readonly option for edit is driven by the permission setting.  The action
// class passes the highest edit privilege.  We adjust the value to correspond
// the the permission setting for the user.
//--------------------------------------------------------------------------------
      if ((fId == editAction) && (usPerm == readOnlyAction))
         fId--;

      String query = "";
      if (usPerm >= fId) {
        if ((fId == editAction) || (fId == deleteAction)) {
//--------------------------------------------------------------------------------
// Prepare SQL statement to select a specific feature record from the user state.
//--------------------------------------------------------------------------------
          query = "SELECT thekey, user_name "
                + "FROM userstate "
                + "WHERE feature_type = ? "
                + "AND feature_key = ? "
                + "AND token_name = ?";
          ps = conn.prepareStatement(query);
          ps.setInt(1, fType);
          ps.setInt(2,fKey);
          ps.setString(3, this.LOCKED);
          rs = ps.executeQuery();
//--------------------------------------------------------------------------------
// Someone already has a token for the record we are pursuing.
//--------------------------------------------------------------------------------
          if ( rs.next() ) {
//--------------------------------------------------------------------------------
// Check to find out if it is our own lock that we see.
//--------------------------------------------------------------------------------
            if (rs.getInt("thekey") != usKey) {
              usName = rs.getString("user_name");
            }
            else
              usName = this.LOCKED;
          }
          else {
//--------------------------------------------------------------------------------
// No one has a lock so we will take one for edit and delete operations.
//--------------------------------------------------------------------------------
            usName = this.LOCKED;
          }
        }
        else {
//--------------------------------------------------------------------------------
// The readonly permission returns a unique value compared to list and add.
//--------------------------------------------------------------------------------
          if (usPerm == readOnlyAction) {
            usName = this.READONLY;
            fType--;
          }
          else {
            usName = this.LOCKED;
          }
        }
      }
      else {
//--------------------------------------------------------------------------------
// User access is prohibited for the requested feature.
//--------------------------------------------------------------------------------
        usName = this.PROHIBITED;
      }

      query = "UPDATE userstate SET "
            + "token_name = ?,"
            + "feature_type = ?,"
            + "feature_key = ?, "
            + "update_stamp = CURRENT_TIMESTAMP "
            + "WHERE thekey = ?";
      ps = conn.prepareStatement(query);
      ps.setString(1, usName);
      ps.setInt(2, fType);
      ps.setInt(3, fKey);
      ps.setInt(4, usKey);
      ps.executeUpdate();

//--------------------------------------------------------------------------------
// 2007-09-07 Perform session validation to prohibit crossing application instances.
//--------------------------------------------------------------------------------
      this.sessionInstanceValidate(request);

//--------------------------------------------------------------------------------
// 2007-09-02 Show the session attributes.
//--------------------------------------------------------------------------------
      this.sessionAttributeCleanUp(request);

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
    cat.debug(this.getClass().getName() + ": getUserToken=" + usName);
    return usName;
  }


//--------------------------------------------------------------------------------
// Author: Richard Wolschlager
// Date..: 07/31/2005
// Method: GetUserToken
// 1) HttpServletRequest
// 2) Connection
// 3) Action name of feature
// 4) Record key accessed by feature
//--------------------------------------------------------------------------------
  public PermissionListDO getUserListToken(HttpServletRequest request,
                                           Connection conn,
                                           String fAction,
                                           Integer fKey)
   throws Exception, SQLException {

    PermissionListDO permissionSlip = new PermissionListDO();
    PreparedStatement ps = null;
    ResultSet rs = null;
    String usName = "";

    try {
      Integer usKey = (Integer) request.getSession().getAttribute(TOKEN);
      String permSlip = (String) request.getSession().getAttribute(PERMISSION);
      Integer fType = new Integer(getPropertyService().getFeatureProperties(fAction));

      int fStart = fType / DIVISOR;
      int fId = fType % DIVISOR;
      int usPerm = new Integer(permSlip.substring(fStart, fStart+1));
//--------------------------------------------------------------------------------
// Prepare SQL statement to search for a feature record in the user state table.
//--------------------------------------------------------------------------------
      fStart = (fStart * DIVISOR) + editAction;
      int fEnd = fStart + maxAction;
      String query = "SELECT thekey, user_name "
                   + "FROM userstate "
                   + "WHERE feature_type between ? AND ? "
                   + "AND feature_key = ?";
      ps = conn.prepareStatement(query);
      ps.setInt(1, fStart);
      ps.setInt(2,fEnd);
      ps.setInt(3,fKey);
      rs = ps.executeQuery();
//--------------------------------------------------------------------------------
// A found record indicates someone already has the row locked.  Find out who.
//--------------------------------------------------------------------------------
      if ( rs.next() ) {
        permissionSlip.setLockAvailableId(false);
        permissionSlip.setLockedBy(rs.getString("user_name"));
        if (usKey == rs.getInt("thekey")) {
          permissionSlip.setMyLockId(true);
        }
      }
//--------------------------------------------------------------------------------
// Set the view and add permission options based only on the permission slip.
//--------------------------------------------------------------------------------
      if (usPerm > noAction)
        permissionSlip.setListId(true);
      if (usPerm > listAction)
        permissionSlip.setViewId(true);
      if (usPerm > editAction)
        permissionSlip.setAddId(true);

      if (permissionSlip.getLockAvailableId()) {
        if (usPerm > readOnlyAction) {
	  permissionSlip.setViewId(false);
          permissionSlip.setEditId(true);
        }
        if (usPerm > addAction) {
          permissionSlip.setDeleteId(true);
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
      /*      
      if (conn != null) {
        try {
          conn.close();
        }
        catch (SQLException sqle) {
          cat.error(this.getClass().getName() + ": SQLException : " + sqle.getMessage());
        }
        conn = null;
      }      
      */
    }
    return permissionSlip;
  }


//--------------------------------------------------------------------------------
// Author: Richard Wolschlager
// Date..: 07/22/2005
// Method: ReleaseExpiredUserToken
// 1) Connection
//--------------------------------------------------------------------------------
  public void releaseExpiredUserToken(Connection conn)
   throws Exception, SQLException {

    PreparedStatement ps = null;

    try {
      Integer usExpirationMinute = new Integer(getPropertyService().getFeatureProperties("userstate.expiration.minute"));
      cat.debug("ExpirationMinute = " + usExpirationMinute.toString());
//--------------------------------------------------------------------------------
// Prepare SQL statement to delete a record from the user state table.
//--------------------------------------------------------------------------------
      String query = "DELETE FROM userstate "
                   + "WHERE (CURRENT_TIMESTAMP - update_stamp) / 60 > ?";
      ps = conn.prepareStatement(query);
      ps.setInt(1, usExpirationMinute);
      ps.executeUpdate();
    }
    catch (SQLException e) {
      cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
    }
    finally {
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

//--------------------------------------------------------------------------------
// Author: Richard Wolschlager
// Date..: 07/22/2005
// Method: DeleteUserToken
// 1) Connection
// 2) Integer
//--------------------------------------------------------------------------------
  public void deleteUserToken(Connection conn,
                              Integer usKey)
   throws Exception, SQLException {

    PreparedStatement ps = null;

    try {
//--------------------------------------------------------------------------------
// Prepare SQL statement to delete a record from the user state table.
//--------------------------------------------------------------------------------
      String query = "DELETE FROM userstate "
                   + "WHERE thekey = ?";
      ps = conn.prepareStatement(query);
      ps.setInt(1, usKey);
      ps.executeUpdate();
    }
    catch (SQLException e) {
      cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
    }
    finally {
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

//--------------------------------------------------------------------------------
// Author: Richard Wolschlager
// Date..: 09/02/2007
// Method: SessionAttributeList
// 1) HttpServletRequest
//--------------------------------------------------------------------------------
  public void sessionAttributeCleanUp(HttpServletRequest request)
   throws Exception {

   cat.debug(request.getSession().getAttribute(this.INSTANCENAME) + "|"
           + request.getRequestURI().substring(request.getRequestURI().lastIndexOf("/")+1) + "|"
           + request.getSession().getAttribute(this.USER) + "|"
           + "|" + new Timestamp(new Date().getTime()).toString());
//--------------------------------------------------------------------------------
// Only the "MenuEntry" and "List" requests will be purged of session attributes.
//--------------------------------------------------------------------------------
    if ((request.getRequestURI().contains("/MenuEntry.do")) ||
      (request.getRequestURI().contains("/AttachmentList.do")) ||
      (request.getRequestURI().contains("/EventCalendarList.do")) ||
      (request.getRequestURI().contains("/ScheduleList.do")) ||
      (request.getRequestURI().contains("/ResourceList.do")) ||
      (request.getRequestURI().contains("/EmployeeList.do")) ||
      (request.getRequestURI().contains("/ProspectList.do")) ||
      (request.getRequestURI().contains("/CustomerList.do")) ||
      (request.getRequestURI().contains("/VendorList.do")) ||
      (request.getRequestURI().contains("/ItemDictionaryList.do")) ||
      (request.getRequestURI().contains("/ServiceDictionaryList.do")) ||
      (request.getRequestURI().contains("/BundleList.do")) ||
      (request.getRequestURI().contains("/PetList.do")) ||
      (request.getRequestURI().contains("/PetBoardList")) ||
      (request.getRequestURI().contains("/PetExamList")) ||
      (request.getRequestURI().contains("/PetVacList")) ||
      (request.getRequestURI().contains("/SystemList.do")) ||
      (request.getRequestURI().contains("/VehicleList.do")) ||
      (request.getRequestURI().contains("/RebateList.do")) ||
      (request.getRequestURI().contains("/CustomerTypeList.do")) ||
      (request.getRequestURI().contains("/PriceTypeList.do")) ||
      (request.getRequestURI().contains("/TaxMarkUpList.do")) ||
      (request.getRequestURI().contains("/PurchaseOrderList.do")) ||
      (request.getRequestURI().contains("/RebateInstanceList.do")) ||
      (request.getRequestURI().contains("/VendorInvoiceList.do")) ||
      (request.getRequestURI().contains("/CustomerInvoiceList.do")) ||
// Exclude due to summary and detail information from two different actions.
//      (request.getRequestURI().contains("/VendorInvoiceReport")) ||
//      (request.getRequestURI().contains("/CustomerInvoiceReport")) ||
      (request.getRequestURI().contains("/PriceSheetList.do")) ||
      (request.getRequestURI().contains("/LedgerList")) ||
      (request.getRequestURI().contains("/CustomerAccountingList")) ||
      (request.getRequestURI().contains("/VendorInvoiceDueEntry.do")) ||
      (request.getRequestURI().contains("/VendorAccountingList")) ||
      (request.getRequestURI().contains("/ExpenseList.do")) ||
      (request.getRequestURI().contains("/ItemPurchase")) ||
      (request.getRequestURI().contains("/ItemSale")) ||
      (request.getRequestURI().contains("/ServiceLabor")) ||
      (request.getRequestURI().contains("/ServiceSale")) ||
      (request.getRequestURI().contains("/ServiceSale")))
    {
//--------------------------------------------------------------------------------
// Traverse through the session attributes
//--------------------------------------------------------------------------------
      Enumeration attNames = request.getSession().getAttributeNames();
      String attName = "";
      while (attNames.hasMoreElements()) {
        attName = (String) attNames.nextElement();
        if (attName.equalsIgnoreCase(this.ADMIN) ||
           attName.equalsIgnoreCase(this.PERMISSION) ||
           attName.equalsIgnoreCase(this.TOKEN) ||
           attName.equalsIgnoreCase(this.USER) ||
           attName.equalsIgnoreCase(this.USERNAME) ||
           attName.equalsIgnoreCase(this.DEFAULTINSTANCENAME) ||
           attName.equalsIgnoreCase(this.INSTANCENAME) ||
           attName.equalsIgnoreCase(this.LICENSE) ||
           attName.contains("org")) {
           cat.debug(this.getClass().getName() + " Keep attribute...: " + attName);
        } else {
           cat.debug(this.getClass().getName() + " Remove attribute...: " + attName);
           request.getSession().removeAttribute(attName);
        }
      }
    }
  }

//--------------------------------------------------------------------------------
// Author: Richard Wolschlager
// Date..: 09/07/2007
// Method: SessionInstanceValidate
// 1) HttpServletRequest
// Prohibit the user from crossing instances with their user session.  For example
// a user who logged in to the "lindaw" instance should not be able to access the
// "wolmerica" instance.  The user will only be able to access the instance from
// which the login had taken place.  An error message will be displayed otherwise.
//--------------------------------------------------------------------------------
  public void sessionInstanceValidate(HttpServletRequest request)
   throws Exception {

    String instanceName = request.getRequestURI();
    cat.debug(this.getClass().getName() + ": requestURI...: " + instanceName);
    int sp = instanceName.indexOf("WebApp/")+7;
    int ep = instanceName.length();
    instanceName = instanceName.substring(sp,ep);

    ep = instanceName.indexOf("/");
    if (ep > 0)
      instanceName = instanceName.substring(0,ep);
    else
      instanceName = request.getSession().getAttribute(this.DEFAULTINSTANCENAME).toString();

    String sesInstanceName = request.getSession().getAttribute(this.INSTANCENAME).toString();
    if (sesInstanceName.compareTo(instanceName) != 0)
      throw new Exception("Invalid session instance - click the browser back button to continue...");
  }

}