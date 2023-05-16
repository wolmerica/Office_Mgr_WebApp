/*
 * VendorResultGetAction.java
 *
 * Created on January 21, 2010, 9:25 AM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 * 12/21/2005 Implement tools.formatter library.
 */

package com.wolmerica.vendorresult;

//--------------------------------------------------------------------------------
// @author Richard Wolschlager
//--------------------------------------------------------------------------------
import com.wolmerica.employee.EmployeesActionMapping;
import com.wolmerica.service.attachment.AttachmentService;
import com.wolmerica.service.attachment.DefaultAttachmentService;
import com.wolmerica.service.attributeto.AttributeToService;
import com.wolmerica.service.attributeto.DefaultAttributeToService;
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
import java.util.HashMap;

import org.apache.log4j.Logger;

public class VendorResultGetAction extends Action {

  Logger cat = Logger.getLogger("WOWAPP");
  

  private AttachmentService attachmentService = new DefaultAttachmentService();
  private AttributeToService attributeToService = new DefaultAttributeToService();
  private UserStateService userStateService = new DefaultUserStateService();

  public AttachmentService getAttachmentService() {
      return attachmentService;
  }

  public void setAttachmentService(AttachmentService attachmentService) {
      this.attachmentService = attachmentService;
  }

  public AttributeToService getAttributeToService() {
      return attributeToService;
  }

  public void setAttributeToService(AttributeToService attributeToService) {
      this.attributeToService = attributeToService;
  }

  public UserStateService getUserStateService() {
      return userStateService;
  }

  public void setUserStateService(UserStateService userStateService) {
      this.userStateService = userStateService;
  }


  private VendorResultDO buildVendorResultForm(HttpServletRequest request,
                                               Integer vrKey)
   throws Exception, SQLException {

    DataSource ds = null;
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;
    VendorResultDO formDO = null;

    Byte sourceTypeKey = null;
    Integer sourceKey = null;

    try {
      ds = getDataSource(request);
      conn = ds.getConnection();
//--------------------------------------------------------------------------------
// Get the vendor, customer and source name values.
//--------------------------------------------------------------------------------
      String query = "SELECT vr.purchaseorder_key,"
                   + "vendor.name AS vendor_name,"
                   + "customer.client_name,"
                   + "customertype.attribute_to_entity,"
                   + "po.sourcetype_key,"
                   + "po.source_key,"
                   + "vr.status,"
                   + "vr.import_file,"
                   + "vr.order_status,"
                   + "vr.site_name,"
                   + "vr.receive_date,"
                   + "vr.receive_accession_id,"
                   + "vr.result_date,"
                   + "vr.result_accession_id,"
                   + "vr.profile_num,"
                   + "vr.unit_status,"
                   + "vr.unit_code,"
                   + "vr.unit_name,"
                   + "vr.abnormal_status,"
                   + "vr.test_status,"
                   + "vr.test_code,"
                   + "vr.test_name,"
                   + "vr.test_value,"
                   + "vr.test_units,"
                   + "vr.test_range,"
                   + "vr.test_comment,"
                   + "vr.error_message,"
                   + "vr.create_user,"
                   + "vr.create_stamp,"
                   + "vr.update_user,"
                   + "vr.update_stamp "
                   + "FROM vendorresult vr, purchaseorder po, "
                   + "vendor, customer, customertype "
                   + "WHERE vr.thekey = ? "
                   + "AND vr.purchaseorder_key = po.thekey "
                   + "AND po.vendor_key = vendor.thekey "
                   + "AND po.customer_key = customer.thekey "
                   + "AND customer.customertype_key = customertype.thekey ";
      ps = conn.prepareStatement(query);
      ps.setInt(1, vrKey);
      rs = ps.executeQuery();

      if ( rs.next() ) {
        formDO = new VendorResultDO();

        formDO.setKey(vrKey);
        formDO.setPurchaseOrderKey(rs.getInt("purchaseorder_key"));
        formDO.setVendorName(rs.getString("vendor_name"));
        formDO.setCustomerName(rs.getString("client_name"));
        formDO.setAttributeToEntityName(rs.getString("attribute_to_entity"));
        sourceTypeKey = rs.getByte("sourcetype_key");
        sourceKey = rs.getInt("source_key");
        formDO.setStatus(rs.getString("status"));
        formDO.setImportFilename(rs.getString("import_file"));
        formDO.setOrderStatus(rs.getString("order_status"));
        formDO.setSiteName(rs.getString("site_name"));
        formDO.setReceiveDate(rs.getDate("receive_date"));
        formDO.setReceiveAssessionId(rs.getString("receive_accession_id"));
        formDO.setResultDate(rs.getDate("result_date"));
        formDO.setResultAssessionId(rs.getString("result_accession_id"));
        formDO.setProfileNum(rs.getString("profile_num"));
        formDO.setUnitStatus(rs.getString("unit_status"));
        formDO.setUnitCode(rs.getString("unit_code"));
        formDO.setUnitName(rs.getString("unit_name"));
        formDO.setAbnormalStatus(rs.getString("abnormal_status"));
        formDO.setTestStatus(rs.getString("test_status"));
        formDO.setTestCode(rs.getString("test_code"));
        formDO.setTestName(rs.getString("test_name"));
        formDO.setTestValue(rs.getString("test_value"));
        formDO.setTestUnits(rs.getString("test_units"));
        formDO.setTestRange(rs.getString("test_range"));
        formDO.setTestComment(rs.getString("test_comment"));
        formDO.setErrorMessage(rs.getString("error_message"));
        formDO.setCreateUser(rs.getString("create_user"));
        formDO.setCreateStamp(rs.getTimestamp("create_stamp"));
        formDO.setUpdateUser(rs.getString("update_user"));
        formDO.setUpdateStamp(rs.getTimestamp("update_stamp"));

//--------------------------------------------------------------------------------
// Get the count of attachments associated with this VENDOR.
//--------------------------------------------------------------------------------
        formDO.setAttachmentCount(getAttachmentService().getAttachmentCount(conn,
                                                        getUserStateService().getFeatureKey().byteValue(),
                                                        formDO.getKey()));

//--------------------------------------------------------------------------------
// Look up the attribute to name with the source type and source key.
//--------------------------------------------------------------------------------
        if (sourceTypeKey != null) {
          HashMap nameMap = getAttributeToService().getAttributeToName(conn,
                                                    sourceTypeKey,
                                                    sourceKey);
          formDO.setAttributeToName(nameMap.get("attributeToName").toString());
        }
      }
      else {
        throw new Exception("VendorResult  " + vrKey.toString() + " not found!");
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
// Instantiate a formDO object to populate formStr.
//--------------------------------------------------------------------------------
        VendorResultDO formDO = buildVendorResultForm(request, theKey);
        formDO.setPermissionStatus(usToken);
        request.getSession().setAttribute("vendorResult", formDO);
        VendorResultForm formStr = new VendorResultForm();
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