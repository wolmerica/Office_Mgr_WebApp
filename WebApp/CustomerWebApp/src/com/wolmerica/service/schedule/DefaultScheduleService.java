package com.wolmerica.service.schedule;

import com.wolmerica.service.itemandsrv.ItemAndSrvService;
import com.wolmerica.service.itemandsrv.DefaultItemAndSrvService;
import com.wolmerica.bundledetail.BundleDetailDO;
import com.wolmerica.schedule.ScheduleDO;
import com.wolmerica.workorder.WorkOrderDO;
import com.wolmerica.workorder.WorkOrderForm;
import com.wolmerica.tools.formatter.FormattingException;

import javax.servlet.http.HttpServletRequest;
import java.sql.Connection;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.apache.log4j.Logger;

/**
 * @author  Richard Wolschlager
 * @date    August 28, 2010
 */

public class DefaultScheduleService implements ScheduleService {

  Logger cat = Logger.getLogger("WOWAPP");

  private ItemAndSrvService itemAndSrvService = new DefaultItemAndSrvService();

  public ItemAndSrvService getItemAndSrvService() {
      return itemAndSrvService;
  }

  public void setItemAndSrvService(ItemAndSrvService itemAndSrvService) {
      this.itemAndSrvService = itemAndSrvService;
  }

//--------------------------------------------------------------------------------
// Author: Richard Wolschlager
// Date..: 09/13/2006
// Method: buildScheduleForm
// 1) Connection
// 2) Schedule Key
//--------------------------------------------------------------------------------
// Introduce a schedule kit in the tools package to provide a common code base
// for retrieving schedule records, one at a time.
// a) ScheduleGetAction.java
// b) ResourceInstanceGetAction.java  (multiple calls for arraylist)
//--------------------------------------------------------------------------------
  public ScheduleDO buildScheduleForm(Connection conn,
                                      Integer schKey)
   throws Exception, SQLException {

    Calendar rightNow = Calendar.getInstance();
    Calendar startOfEvent = Calendar.getInstance();

    CallableStatement cStmt = null;
    ScheduleDO formDO = new ScheduleDO();

    try
    {
//--------------------------------------------------------------------------------
// Call a procedure with one IN parameters and forty-four OUT parameters.
//--------------------------------------------------------------------------------
      cStmt = conn.prepareCall("{call GetSchedule(?,?,?,?,?,?,?,?,?,?,"
                                               + "?,?,?,?,?,?,?,?,?,?,"
                                               + "?,?,?,?,?,?,?,?,?,?,"
                                               + "?,?,?,?,?,?,?,?,?,?,"
                                               + "?,?,?,?,?,?,?)}");
      cStmt.setInt(1, schKey);
      cStmt.execute();

      cat.debug(this.getClass().getName() + ": sEventTypeKey : " + cStmt.getByte("sEventTypeKey"));
      cat.debug(this.getClass().getName() + ": sLocationId : " + cStmt.getBoolean("sLocationId"));
      cat.debug(this.getClass().getName() + ": sSubject : " + cStmt.getString("sSubject"));
      cat.debug(this.getClass().getName() + ": sCustomerKey : " + cStmt.getInt("sCustomerKey"));
      cat.debug(this.getClass().getName() + ": sClientName : " + cStmt.getString("sClientName"));
      cat.debug(this.getClass().getName() + ": sCustomerPhone : " + cStmt.getString("sCustomerPhone"));
      cat.debug(this.getClass().getName() + ": sReminderPrefKey : " + cStmt.getByte("sReminderPrefKey"));
      cat.debug(this.getClass().getName() + ": sCustomerTypeKey : " + cStmt.getByte("sCustomerTypeKey"));
      cat.debug(this.getClass().getName() + ": sAttributeToEntity : " + cStmt.getString("sAttributeToEntity"));
      cat.debug(this.getClass().getName() + ": sSourceTypeKey : " + cStmt.getByte("sSourceTypeKey"));
      cat.debug(this.getClass().getName() + ": sSourceKey : " + cStmt.getInt("sSourceKey"));
      cat.debug(this.getClass().getName() + ": sAttributeToName : " + cStmt.getString("sAttributeToName"));
      cat.debug(this.getClass().getName() + ": sAttributeToDate : " + cStmt.getDate("sAttributeToDate"));
      cat.debug(this.getClass().getName() + ": sStartDate : " + cStmt.getDate("sStartDate"));
      cat.debug(this.getClass().getName() + ": sStartHour : " + cStmt.getByte("sStartHour"));
      cat.debug(this.getClass().getName() + ": sStartMinute : " + cStmt.getByte("sStartMinute"));
      cat.debug(this.getClass().getName() + ": sEndDate : " + cStmt.getDate("sEndDate"));
      cat.debug(this.getClass().getName() + ": sEndHour : " + cStmt.getByte("sEndHour"));
      cat.debug(this.getClass().getName() + ": sEndMinute : " + cStmt.getByte("sEndMinute"));
      cat.debug(this.getClass().getName() + ": sAddressId : " + cStmt.getBoolean("sAddressId"));
      cat.debug(this.getClass().getName() + ": sAddress : " + cStmt.getString("sAddress"));
      cat.debug(this.getClass().getName() + ": sCity : " + cStmt.getString("sCity"));
      cat.debug(this.getClass().getName() + ": sState : " + cStmt.getString("sState"));
      cat.debug(this.getClass().getName() + ": sNote : " + cStmt.getString("sNote"));
      cat.debug(this.getClass().getName() + ": sCustomerInvoiceKey : " + cStmt.getString("sCustomerInvoiceKey"));
      cat.debug(this.getClass().getName() + ": sStatusKey : " + cStmt.getByte("sStatusKey"));
      cat.debug(this.getClass().getName() + ": sReleaseId : " + cStmt.getBoolean("sReleaseId"));
      cat.debug(this.getClass().getName() + ": sThirdPartyId : " + cStmt.getBoolean("sThirdPartyId"));
      cat.debug(this.getClass().getName() + ": sThirdPartyOrderId : " + cStmt.getBoolean("sThirdPartyOrderId"));
      cat.debug(this.getClass().getName() + ": sCategory1Id : " + cStmt.getBoolean("sCategory1Id"));
      cat.debug(this.getClass().getName() + ": sCategory2Id : " + cStmt.getBoolean("sCategory2Id"));
      cat.debug(this.getClass().getName() + ": sCategory3Id : " + cStmt.getBoolean("sCategory3Id"));
      cat.debug(this.getClass().getName() + ": sCategory4Id : " + cStmt.getBoolean("sCategory4Id"));
      cat.debug(this.getClass().getName() + ": sCategory5Id : " + cStmt.getBoolean("sCategory5Id"));
      cat.debug(this.getClass().getName() + ": sCategory6Id : " + cStmt.getBoolean("sCategory6Id"));
      cat.debug(this.getClass().getName() + ": sCategory1Key : " + cStmt.getInt("sCategory1Key"));
      cat.debug(this.getClass().getName() + ": sCategory2Key : " + cStmt.getInt("sCategory2Key"));
      cat.debug(this.getClass().getName() + ": sCategory3Key : " + cStmt.getInt("sCategory3Key"));
      cat.debug(this.getClass().getName() + ": sCategory4Key : " + cStmt.getInt("sCategory4Key"));
      cat.debug(this.getClass().getName() + ": sCategory5Key : " + cStmt.getInt("sCategory5Key"));
      cat.debug(this.getClass().getName() + ": sCategory6Key : " + cStmt.getInt("sCategory6Key"));
      cat.debug(this.getClass().getName() + ": sExamKey : " + cStmt.getInt("sExamKey"));
      cat.debug(this.getClass().getName() + ": sCreateUser : " + cStmt.getString("sCreateUser"));
      cat.debug(this.getClass().getName() + ": sCreateStamp : " + cStmt.getTimestamp("sCreateStamp"));
      cat.debug(this.getClass().getName() + ": sUpdateUser : " + cStmt.getString("sUpdateUser"));
      cat.debug(this.getClass().getName() + ": sUpdateStamp : " + cStmt.getTimestamp("sUpdateStamp"));

//--------------------------------------------------------------------------------
// Assign return values to the form.
//--------------------------------------------------------------------------------
      formDO.setKey(schKey);
      formDO.setEventTypeKey(cStmt.getByte("sEventTypeKey"));
      formDO.setLocationId(cStmt.getBoolean("sLocationId"));
      formDO.setSubject(cStmt.getString("sSubject"));
      formDO.setCustomerKey(cStmt.getInt("sCustomerKey"));
      formDO.setClientName(cStmt.getString("sClientName"));
      formDO.setCustomerPhone(cStmt.getString("sCustomerPhone"));
      formDO.setReminderPrefKey(cStmt.getByte("sReminderPrefKey"));
      formDO.setCustomerTypeKey(cStmt.getByte("sCustomerTypeKey"));
      formDO.setAttributeToEntity(cStmt.getString("sAttributeToEntity"));
      formDO.setSourceTypeKey(cStmt.getByte("sSourceTypeKey"));
      formDO.setSourceKey(cStmt.getInt("sSourceKey"));
      formDO.setAttributeToName(cStmt.getString("sAttributeToName"));
      formDO.setAttributeToDate(cStmt.getDate("sAttributeToDate"));
      formDO.setStartDate(cStmt.getDate("sStartDate"));
      formDO.setStartHour(cStmt.getByte("sStartHour"));
      formDO.setStartMinute(cStmt.getByte("sStartMinute"));
      formDO.setEndDate(cStmt.getDate("sEndDate"));
      formDO.setEndHour(cStmt.getByte("sEndHour"));
      formDO.setEndMinute(cStmt.getByte("sEndMinute"));
      formDO.setAddressId(cStmt.getBoolean("sAddressId"));
      formDO.setAddress(cStmt.getString("sAddress"));
      formDO.setCity(cStmt.getString("sCity"));
      formDO.setState(cStmt.getString("sState"));
      formDO.setNoteLine1(cStmt.getString("sNote"));
      formDO.setCustomerInvoiceKey(cStmt.getString("sCustomerInvoiceKey"));
      formDO.setStatusKey(cStmt.getByte("sStatusKey"));
      formDO.setReleaseId(cStmt.getBoolean("sReleaseId"));
      formDO.setThirdPartyId(cStmt.getBoolean("sThirdPartyId"));
      formDO.setThirdPartyOrderId(cStmt.getBoolean("sThirdPartyOrderId"));
      formDO.setCategory1Id(cStmt.getBoolean("sCategory1Id"));
      formDO.setCategory2Id(cStmt.getBoolean("sCategory2Id"));
      formDO.setCategory3Id(cStmt.getBoolean("sCategory3Id"));
      formDO.setCategory4Id(cStmt.getBoolean("sCategory4Id"));
      formDO.setCategory5Id(cStmt.getBoolean("sCategory5Id"));
      formDO.setCategory6Id(cStmt.getBoolean("sCategory6Id"));
      formDO.setCategory1Key(cStmt.getInt("sCategory1Key"));
      formDO.setCategory2Key(cStmt.getInt("sCategory2Key"));
      formDO.setCategory3Key(cStmt.getInt("sCategory3Key"));
      formDO.setCategory4Key(cStmt.getInt("sCategory4Key"));
      formDO.setCategory5Key(cStmt.getInt("sCategory5Key"));
      formDO.setCategory6Key(cStmt.getInt("sCategory6Key"));
      formDO.setExamKey(cStmt.getInt("sExamKey"));
      formDO.setCreateUser(cStmt.getString("sCreateUser"));
      formDO.setCreateStamp(cStmt.getTimestamp("sCreateStamp"));
      formDO.setUpdateUser(cStmt.getString("sUpdateUser"));
      formDO.setUpdateStamp(cStmt.getTimestamp("sUpdateStamp"));

//--------------------------------------------------------------------------------
// Get work order total for are only needed for the following status.
// 0 - Notify Customer
// 2 - Pending
// 3 - In Progress
// 8 - Complete
//--------------------------------------------------------------------------------
      if ((formDO.getStatusKey() <= 3) || (formDO.getStatusKey() >= 8)) {
        cStmt = conn.prepareCall("{call GetWorkOrderTotal(?,?,?,?,?)}");
        cStmt.setInt(1, schKey);
        cStmt.execute();

        cat.debug(this.getClass().getName() + ": woItemCount    : " + cStmt.getInt("woItemCount"));
        cat.debug(this.getClass().getName() + ": woItemTotal    : " + cStmt.getBigDecimal("woItemTotal"));
        cat.debug(this.getClass().getName() + ": woServiceCount : " + cStmt.getInt("woServiceCount"));
        cat.debug(this.getClass().getName() + ": woServiceTotal : " + cStmt.getBigDecimal("woServiceTotal"));

        formDO.setWorkOrderItemTotal(cStmt.getBigDecimal("woItemTotal"));
        formDO.setWorkOrderServiceTotal(cStmt.getBigDecimal("woServiceTotal"));
      }

//--------------------------------------------------------------------------------
// Get the rewind indicator for appointments that have been invoiced.
//--------------------------------------------------------------------------------
      if ((formDO.getEventTypeKey() == 0) && (formDO.getCustomerInvoiceKey() != null)) {
        formDO.setRewindId(setRewindIndicator(conn,
                                 new Integer(formDO.getCustomerInvoiceKey()),
                                 "wolmerica", false));
      }

//--------------------------------------------------------------------------------
// For all events that do not have a status value set to "Closed".
// Allow edits of events that are not aged more than 480 hours (20 days).
//--------------------------------------------------------------------------------
      if (formDO.getStatusKey() != 8) {
        rightNow = Calendar.getInstance();
        rightNow.add(Calendar.HOUR, - 480);

        startOfEvent.setTime(formDO.getStartDate());
        startOfEvent.set(startOfEvent.get(Calendar.YEAR),
                         startOfEvent.get(Calendar.MONTH),
                         startOfEvent.get(Calendar.DATE),
                         formDO.getStartHour().intValue(),
                         formDO.getStartMinute().intValue(),
                         0);

        if (rightNow.getTime().getTime() < startOfEvent.getTime().getTime())
          formDO.setActiveId(true);
      }
    }
    catch (SQLException e) {
      cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
      throw new Exception("getSchedule() " + e.getMessage());
    }
    finally
    {
      try
      {
        cStmt.close();
      }
      catch (SQLException e) {
        cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
        throw new Exception("getSchedule() " + e.getMessage());
      }
    }
    return formDO;
  }


  public boolean setRewindIndicator(Connection conn,
                                    Integer ciKey,
                                    String updateUser,
                                    boolean updateId)
   throws Exception, SQLException {


    CallableStatement cStmt = null;
    boolean allowRewindId = true;

    try
    {
//--------------------------------------------------------------------------------
// Call a procedure with three IN parameters and one OUT parameters.
//--------------------------------------------------------------------------------
      cStmt = conn.prepareCall("{call SetCustomerInvoiceRewind(?,?,?,?)}");

//--------------------------------------------------------------------------------
// Set the value for the IN parameter
//--------------------------------------------------------------------------------
      cStmt.setInt(1, ciKey);
      cStmt.setString(2, updateUser);
      cStmt.setBoolean(3, updateId);
//--------------------------------------------------------------------------------
// Execute the stored procedure
//--------------------------------------------------------------------------------
      cStmt.execute();

//--------------------------------------------------------------------------------
// debug messages if necessary.
//--------------------------------------------------------------------------------
      cat.debug(this.getClass().getName() + ": ciiCnt : " + cStmt.getByte("ciiCnt"));

//--------------------------------------------------------------------------------
// Retrieve the return values
//--------------------------------------------------------------------------------
      if (cStmt.getByte("ciiCnt") > 0)
        allowRewindId = false;
    }
    catch (SQLException e) {
      cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
      throw new Exception("setCustomerInvoiceRewind() " + e.getMessage());
    }
    finally
    {
      try
      {
        cStmt.close();
      }
      catch (SQLException e) {
        cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
        throw new Exception("setCustomerInvoiceRewind() " + e.getMessage());
      }
    }
    return allowRewindId;
  }


//--------------------------------------------------------------------------------
// Author: Richard Wolschlager
// Date..: 10/29/2007
// Method: buildWorkOrderForm
// 1) Connection
// 2) WorkOrder Key
//--------------------------------------------------------------------------------
// Introduce a schedule kit in the tools package to provide a common code base
// for retrieving schedule records, one at a time.
// a) WorkOrderGetAction.java
// b) WorkOrderResourceListAction.java  (multiple calls for arraylist)
//--------------------------------------------------------------------------------
  public WorkOrderDO buildWorkOrderForm(Connection conn,
                                        Integer woKey)
   throws Exception, SQLException {

    CallableStatement cStmt = null;
    WorkOrderDO formDO = new WorkOrderDO();

    try
    {
//--------------------------------------------------------------------------------
// Call a procedure with one IN parameters and twenty two OUT parameters.
//--------------------------------------------------------------------------------
      cStmt = conn.prepareCall("{call GetWorkOrder(?,?,?,?,?,?,?,?,?,?,"
                                                + "?,?,?,?,?,?,?,?)}");

//--------------------------------------------------------------------------------
// Set the value for the IN parameter
//--------------------------------------------------------------------------------
      cStmt.setInt(1, woKey);

//--------------------------------------------------------------------------------
// Execute the stored procedure
//--------------------------------------------------------------------------------
      cStmt.execute();

//--------------------------------------------------------------------------------
// debug messages if necessary.
//--------------------------------------------------------------------------------
      cat.debug(this.getClass().getName() + ": workorder key.: " + woKey);
      cat.debug(this.getClass().getName() + ": wScheduleKey..: " + cStmt.getInt("wScheduleKey"));
      cat.debug(this.getClass().getName() + ": wSourceTypeKey: " + cStmt.getByte("wSourceTypeKey"));
      cat.debug(this.getClass().getName() + ": wSourceKey....: " + cStmt.getInt("wSourceKey"));
      cat.debug(this.getClass().getName() + ": wReleaseId : " + cStmt.getBoolean("wReleaseId"));
      cat.debug(this.getClass().getName() + ": wThirdPartyId : " + cStmt.getBoolean("wThirdPartyId"));
      cat.debug(this.getClass().getName() + ": wOrderQty.....: " + cStmt.getShort("wOrderQty"));
      cat.debug(this.getClass().getName() + ": wEstimatedPrice: " + cStmt.getShort("wEstimatedPrice"));
      cat.debug(this.getClass().getName() + ": wStartDate....: " + cStmt.getDate("wStartDate"));
      cat.debug(this.getClass().getName() + ": wStartHour....: " + cStmt.getByte("wStartHour"));
      cat.debug(this.getClass().getName() + ": wStartMinute..: " + cStmt.getByte("wStartMinute"));
      cat.debug(this.getClass().getName() + ": wEndDate......: " + cStmt.getDate("wEndDate"));
      cat.debug(this.getClass().getName() + ": wEndHour......: " + cStmt.getByte("wEndHour"));
      cat.debug(this.getClass().getName() + ": wEndMinute....: " + cStmt.getByte("wEndMinute"));
      cat.debug(this.getClass().getName() + ": wCreateUser...: " + cStmt.getString("wCreateUser"));
      cat.debug(this.getClass().getName() + ": wCreateStamp..: " + cStmt.getTimestamp("wCreateStamp"));
      cat.debug(this.getClass().getName() + ": wUpdateUser...: " + cStmt.getString("wUpdateUser"));
      cat.debug(this.getClass().getName() + ": wUpdateStamp..: " + cStmt.getTimestamp("wUpdateStamp"));

//--------------------------------------------------------------------------------
// Retrieve the return values
//--------------------------------------------------------------------------------
      formDO.setKey(woKey);
      formDO.setScheduleKey(cStmt.getInt("wScheduleKey"));
      formDO.setSourceTypeKey(cStmt.getByte("wSourceTypeKey"));
      formDO.setSourceKey(cStmt.getInt("wSourceKey"));
      formDO.setReleaseId(cStmt.getBoolean("wReleaseId"));
      formDO.setThirdPartyId(cStmt.getBoolean("wThirdPartyId"));
      formDO.setOrderQty(cStmt.getShort("wOrderQty"));
      formDO.setEstimatedPrice(cStmt.getBigDecimal("wEstimatedPrice"));
      formDO.setStartDate(cStmt.getDate("wStartDate"));
      formDO.setStartHour(cStmt.getByte("wStartHour"));
      formDO.setStartMinute(cStmt.getByte("wStartMinute"));
      formDO.setEndDate(cStmt.getDate("wEndDate"));
      formDO.setEndHour(cStmt.getByte("wEndHour"));
      formDO.setEndMinute(cStmt.getByte("wEndMinute"));
      formDO.setCreateUser(cStmt.getString("wCreateUser"));
      formDO.setCreateStamp(cStmt.getTimestamp("wCreateStamp"));
      formDO.setUpdateUser(cStmt.getString("wUpdateUser"));
      formDO.setUpdateStamp(cStmt.getTimestamp("wUpdateStamp"));
//--------------------------------------------------------------------------------
// Retrieve additional details from the getItemOrServiceName() stored procedure.
//--------------------------------------------------------------------------------
      BundleDetailDO bundleDetail = new BundleDetailDO();
      bundleDetail.setKey(cStmt.getInt("wScheduleKey"));
      bundleDetail.setSourceTypeKey(cStmt.getByte("wSourceTypeKey"));
      bundleDetail.setSourceKey(cStmt.getInt("wSourceKey"));
      bundleDetail.setOrderQty(cStmt.getShort("wOrderQty"));
      bundleDetail = getItemAndSrvService().getItemOrServiceName(conn, bundleDetail);
      formDO.setSourceName(bundleDetail.getSourceName());
      formDO.setCategoryName(bundleDetail.getCategoryName());
      formDO.setPriceTypeKey(bundleDetail.getPriceTypeKey());
      formDO.setPriceTypeName(bundleDetail.getPriceTypeName());
      formDO.setSize(bundleDetail.getSize());
      formDO.setSizeUnit(bundleDetail.getSizeUnit());
      formDO.setEstimatedPrice(bundleDetail.getComputedPrice());
      formDO.setExtendedPrice(formDO.getEstimatedPrice().multiply(cStmt.getBigDecimal("wOrderQty")));
//--------------------------------------------------------------------------------
// Retrieve schedule details with buildScheduleForm() stored procedure.
//--------------------------------------------------------------------------------
      ScheduleDO schedule = buildScheduleForm(conn, formDO.getScheduleKey());
      formDO.setLocationId(schedule.getLocationId());
      formDO.setSubject(schedule.getSubject());
      formDO.setClientName(schedule.getClientName());
      formDO.setAttributeToEntity(schedule.getAttributeToEntity());
      formDO.setAttributeToName(schedule.getAttributeToName());
    }
    catch (SQLException e) {
      cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
      throw new Exception("buildWorkOrderForm() " + e.getMessage());
    }
    finally
    {
      try
      {
        cStmt.close();
      }
      catch (SQLException e) {
        cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
        throw new Exception("buildWorkOrderForm() " + e.getMessage());
      }
    }
    return formDO;
  }


//--------------------------------------------------------------------------------
// Author: Richard Wolschlager
// Date..: 10/29/2007
// Method: getResourceSchedule
// 1) HttpServletRequest
// 2) Connection
// 3) Work Order Key
// 4) Resource Key
// 5) Start TimeStamp
// 6) End TimeStamp
//--------------------------------------------------------------------------------
  public ArrayList getResourceSchedule(HttpServletRequest request,
                                       Connection conn,
                                       Integer woKey,
                                       Integer resourceKey,
                                       Timestamp startStamp,
                                       Timestamp endStamp)
   throws Exception, SQLException {

    ArrayList workOrderRows = new ArrayList();
    PreparedStatement ps = null;
    ResultSet rs = null;

    try {
//--------------------------------------------------------------------------------
// Prepare an SQL query to retrieve the resource tasks for the work order time frame.
//--------------------------------------------------------------------------------
      String query = "SELECT DISTINCT workorder_key,"
                   + "HOUR(start_stamp) AS start_hour,"
                   + "MINUTE(start_stamp) AS start_minute,"
                   + "start_stamp "
                   + "FROM resourceinstance, workorder "
                   + "WHERE resourceinstance.workorder_key = workorder.thekey "
                   + "AND resourceinstance.resource_key = ? "
                   + "AND workorder.thekey != ? "
                   + "AND ((? BETWEEN start_stamp AND end_stamp "
                   + "OR ? BETWEEN start_stamp AND end_stamp) "
                   + "OR (start_stamp BETWEEN ? AND ? "
                   + "OR end_stamp BETWEEN ? AND ?)) "
                   + "ORDER BY start_stamp";

      cat.debug(this.getClass().getName() + ": query = " + query);
//--------------------------------------------------------------------------------
// Prepare the SQL statement an populate the parameters.
//--------------------------------------------------------------------------------
      ps = conn.prepareStatement(query);
      ps.setInt(1, resourceKey);
      ps.setInt(2, woKey);
      ps.setTimestamp(3, startStamp);
      ps.setTimestamp(4, endStamp);
      ps.setTimestamp(5, startStamp);
      ps.setTimestamp(6, endStamp);
      ps.setTimestamp(7, startStamp);
      ps.setTimestamp(8, endStamp);
      rs = ps.executeQuery();

      while ( rs.next() ) {
        WorkOrderDO formDO = this.buildWorkOrderForm(conn, rs.getInt("workorder_key"));

        try {
//--------------------------------------------------------------------------------
// Instantiate a formDO object to populate formStr.  Leverage the existing
// formatter library to convert the nested objects regarding a resource.
//--------------------------------------------------------------------------------
          request.getSession().setAttribute("riworkorder", formDO);
          WorkOrderForm formStr = new WorkOrderForm();
          formStr.populate(formDO);

          cat.debug(this.getClass().getName() + ": resourceKey="+resourceKey.toString()+"subject="+formStr.getSubject());
          workOrderRows.add(formStr);
        }
        catch (FormattingException fe) {
              fe.getMessage();
        }
      }
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
      if (rs != null) {
        try {
          rs.close();
        }
        catch (SQLException sqle) {
          cat.error(this.getClass().getName() + ": SQLException : " + sqle.getMessage());
        }
        rs = null;
      }
    }
    return workOrderRows;
  }

//--------------------------------------------------------------------------------
// Author: Richard Wolschlager
// Date..: 11/01/2007
// Method: getCustomerSchedule
// 1) HttpServletRequest
// 2) Connection
// 3) Customer Key
// 4) Event Date
//--------------------------------------------------------------------------------
  public ArrayList<WorkOrderForm> getCustomerSchedule(HttpServletRequest request,
                                       Connection conn,
                                       Integer customerKey,
                                       Date startDate)
   throws Exception, SQLException {

    WorkOrderForm formStr = null;
    ArrayList<WorkOrderForm> workOrderRows = new ArrayList<WorkOrderForm>();

    PreparedStatement ps = null;
    ResultSet rs = null;

    try {
//--------------------------------------------------------------------------------
// Prepare an SQL query to retrieve the tasks associated with a customer for a day.
// The source type limit will only include the service related entries.
//--------------------------------------------------------------------------------
      String query = "SELECT workorder.thekey AS workorder_key,"
                   + "HOUR(workorder.start_stamp) AS start_hour,"
                   + "MINUTE(workorder.start_stamp) AS start_minute "
                   + "FROM schedule, workorder "
                   + "WHERE schedule.customer_key = ? "
                   + "AND schedule.eventtype_key = 0 "
                   + "AND schedule.thekey = workorder.schedule_key "
                   + "AND ? BETWEEN DATE(workorder.start_stamp) AND DATE(workorder.end_stamp) "
                   + "AND workorder.sourcetype_key = 6 "
                   + "ORDER BY schedule_key,start_hour,start_minute";
      ps = conn.prepareStatement(query);
      ps.setInt(1, customerKey);
      ps.setDate(2, new java.sql.Date(startDate.getTime()));
      rs = ps.executeQuery();

      while ( rs.next() ) {
        WorkOrderDO formDO = this.buildWorkOrderForm(conn, rs.getInt("workorder_key"));

        try {
//--------------------------------------------------------------------------------
// Instantiate a formDO object to populate formStr.  Leverage the existing
// formatter library to convert the nested objects regarding a resource.
//--------------------------------------------------------------------------------
        request.getSession().setAttribute("workorder", formDO);
        formStr = new WorkOrderForm();
        formStr.populate(formDO);

        }
        catch (FormattingException fe) {
              fe.getMessage();
        }
        cat.debug(this.getClass().getName() + ": customerKey...:" + customerKey.toString());
        cat.debug(this.getClass().getName() + ": subject.......:" + formStr.getSubject());

        workOrderRows.add(formStr);
      }
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
      if (rs != null) {
        try {
          rs.close();
        }
        catch (SQLException sqle) {
          cat.error(this.getClass().getName() + ": SQLException : " + sqle.getMessage());
        }
        rs = null;
      }
    }
    return workOrderRows;
  }


//--------------------------------------------------------------------------------
// Author: Richard Wolschlager
// Date..: 11/01/2007
// Method: getAttributeToSchedule
// 1) HttpServletRequest
// 2) Connection
// 3) Source Type Key
// 4) Source Key
// 5) Event Date
//--------------------------------------------------------------------------------
  public ArrayList getAttributeToSchedule(HttpServletRequest request,
                                          Connection conn,
                                          Byte sourceTypeKey,
                                          Integer sourceKey,
                                          Date startDate)
   throws Exception, SQLException {

    WorkOrderForm formStr = null;
    ArrayList workOrderRows = new ArrayList();

    PreparedStatement ps = null;
    ResultSet rs = null;

    try {
//--------------------------------------------------------------------------------
// Prepare an SQL query to retrieve the attribute to appointments for a day.
// The source type limit will only include the service related entries.
//--------------------------------------------------------------------------------
      String query = "SELECT workorder.thekey AS workorder_key,"
                   + "HOUR(workorder.start_stamp) AS start_hour,"
                   + "MINUTE(workorder.start_stamp) AS start_minute "
                   + "FROM schedule, workorder "
                   + "WHERE schedule.sourcetype_key = ? "
                   + "AND schedule.source_key = ? "
                   + "AND schedule.eventtype_key = 0 "
                   + "AND schedule.thekey = workorder.schedule_key "
                   + "AND ? BETWEEN DATE(workorder.start_stamp) AND DATE(workorder.end_stamp) "
                   + "AND workorder.sourcetype_key = 6 "
                   + "ORDER BY start_hour,start_minute";
      ps = conn.prepareStatement(query);
      ps.setByte(1, sourceTypeKey);
      ps.setInt(2, sourceKey);
      ps.setDate(3, new java.sql.Date(startDate.getTime()));
      rs = ps.executeQuery();

      while ( rs.next() ) {
        WorkOrderDO formDO = this.buildWorkOrderForm(conn, rs.getInt("workorder_key"));

        try {
//--------------------------------------------------------------------------------
// Instantiate a formDO object to populate formStr.  Leverage the existing
// formatter library to convert the nested objects regarding a resource.
//--------------------------------------------------------------------------------
        request.getSession().setAttribute("riworkorder", formDO);
        formStr = new WorkOrderForm();
        formStr.populate(formDO);

        }
        catch (FormattingException fe) {
              fe.getMessage();
        }
        cat.debug(this.getClass().getName() + ": sourceTypeKey.:" + sourceTypeKey.toString());
        cat.debug(this.getClass().getName() + ": sourceKey.....:" + sourceKey.toString());
        cat.debug(this.getClass().getName() + ": subject.......:" + formStr.getSubject());

        workOrderRows.add(formStr);
      }
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
      if (rs != null) {
        try {
          rs.close();
        }
        catch (SQLException sqle) {
          cat.error(this.getClass().getName() + ": SQLException : " + sqle.getMessage());
        }
        rs = null;
      }
    }
    return workOrderRows;
  }


//--------------------------------------------------------------------------------
// Author: Richard Wolschlager
// Date..: 02/29/2008
// Method: setScheduleStatus
// 1) Connection
// 2) Schedule Key
//--------------------------------------------------------------------------------
  public void setScheduleStatus(Connection conn,
                                Integer scheduleKey)
   throws Exception, SQLException {

    PreparedStatement ps = null;

    try {
//--------------------------------------------------------------------------------
// Prepare an SQL to update the schedule status to the "pending" state.
//--------------------------------------------------------------------------------
      String query = "UPDATE schedule "
                   + "SET status_key = 2 "
                   + "WHERE thekey = ?";
      ps = conn.prepareStatement(query);
      ps.setInt(1, scheduleKey);
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
    }
  }


}