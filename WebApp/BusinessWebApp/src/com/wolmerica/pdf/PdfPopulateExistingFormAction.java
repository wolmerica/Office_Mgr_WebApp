/*
 * PdfPopulateExistingFormAction.java
 *
 * Created on August 31, 2007, 9:00 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 *
 */

package com.wolmerica.pdf;

//--------------------------------------------------------------------------------
// @author Richard Wolschlager
//--------------------------------------------------------------------------------
import com.wolmerica.employee.EmployeesActionMapping;
import com.wolmerica.service.attachment.AttachmentService;
import com.wolmerica.service.attachment.DefaultAttachmentService;
import com.wolmerica.service.property.PropertyService;
import com.wolmerica.service.property.DefaultPropertyService;
import com.wolmerica.service.purchaseorder.PurchaseOrderService;
import com.wolmerica.service.purchaseorder.DefaultPurchaseOrderService;
import com.wolmerica.service.userstate.UserStateService;
import com.wolmerica.service.userstate.DefaultUserStateService;

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

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PRAcroForm;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.AcroFields;

import java.util.Iterator;
import java.io.IOException;
import java.util.Map;
import java.util.HashMap;

import org.apache.log4j.Logger;


public class PdfPopulateExistingFormAction extends Action {

  Logger cat = Logger.getLogger("WOWAPP");

  private AttachmentService attachmentService = new DefaultAttachmentService();
  private PropertyService propertyService = new DefaultPropertyService();
  private PurchaseOrderService purchaseOrderService = new DefaultPurchaseOrderService();
  private UserStateService userStateService = new DefaultUserStateService();

  public AttachmentService getAttachmentService() {
      return attachmentService;
  }

  public void setAttachmentService(AttachmentService attachmentService) {
      this.attachmentService = attachmentService;
  }

  public PropertyService getPropertyService() {
      return propertyService;
  }

  public void setPropertyService(PropertyService propertyService) {
      this.propertyService = propertyService;
  }

  public PurchaseOrderService getPurchaseOrderService() {
      return purchaseOrderService;
  }

  public void setPurchaseOrderService(PurchaseOrderService purchaseOrderService) {
      this.purchaseOrderService = purchaseOrderService;
  }

  public UserStateService getUserStateService() {
      return userStateService;
  }

  public void setUserStateService(UserStateService userStateService) {
      this.userStateService = userStateService;
  }

  public void doReportGenerator(HttpServletRequest request,
                                HttpServletResponse response,
                                Integer poKey)
   throws Exception, IOException, SQLException {

    String createUser = (String) request.getSession().getAttribute("USERNAME");
    cat.debug("doReportGenerator(): " + poKey);
//--------------------------------------------------------------------------------
// step 1 create a document object and open an database connection.
//--------------------------------------------------------------------------------
    DataSource ds = null;
    Connection conn = null;
    Document document = new Document();

    try {
      ds = getDataSource(request);
      conn = ds.getConnection();
//--------------------------------------------------------------------------------
// Make sure the user has permission to run this report.
//--------------------------------------------------------------------------------
      
      String usToken = getUserStateService().getUserToken(request,
                                              this.getDataSource(request).getConnection(),
                                              this.getClass().getName(),
                                              getUserStateService().getNoKey());
      if (usToken.equalsIgnoreCase(getUserStateService().getLocked()))
        cat.debug("No request attribute to set inside PDF reports.");
      else if (usToken.equalsIgnoreCase(getUserStateService().getProhibited()))
        throw new Exception(getUserStateService().getAccessDenied());

      Integer attKey = getAttachmentService().getAttachmentOrderFormKey(conn, poKey);
      cat.debug("getAttachmentOrderFormKey(): " + attKey);

      try {
//--------------------------------------------------------------------------------
// step 2: set the ContentType to pdf
//--------------------------------------------------------------------------------
        response.setContentType("application/pdf");
        
//--------------------------------------------------------------------------------
// step 3: Create a hash map and populate with purchase order details.
//--------------------------------------------------------------------------------
        Map<String, String> map = new HashMap<String, String>();    // hash table
        map = setFormMapping(conn,
                             poKey,
                             attKey,
                             map);

//--------------------------------------------------------------------------------
// step 4: Construct the path for the PDF file to be populated.
//--------------------------------------------------------------------------------
        String formName = getAttachmentService().getAttachmentName(conn,
                                                                   attKey);
        cat.debug("getAttachmentName(): " + formName);
        String filePath = getPropertyService().getCustomerProperties(request, "fileupload.physical.directory");
        String pathSymbol = getPropertyService().getCustomerProperties(request, "fileupload.separator.symbol");
        filePath = filePath + pathSymbol + "7" + pathSymbol + "2" + pathSymbol + formName;
        cat.debug(this.getClass().getName() + ": file name " + filePath);
//--------------------------------------------------------------------------------
// step 5: Traverse through all the fields in the PDF and set the values.
//--------------------------------------------------------------------------------
        PdfReader reader = new PdfReader(filePath);
        PdfStamper stamp = new PdfStamper(reader, response.getOutputStream());
        AcroFields form = stamp.getAcroFields();
        String key = "";
        String fieldType = "";

        for(Iterator i = reader.getAcroForm().getFields().iterator(); i.hasNext();) {
          PRAcroForm.FieldInformation field = (PRAcroForm.FieldInformation) i.next();
          field.getInfo();
          key = field.getName();

//        HashMap fields = form.getFields();
//        for (Iterator i = fields.keySet().iterator(); i.hasNext();) {
//          key = (String) i.next();

          switch(form.getFieldType(key)) {
            case AcroFields.FIELD_TYPE_CHECKBOX:
              fieldType = "Checkbox";
              break;
            case AcroFields.FIELD_TYPE_COMBO:
              fieldType = "Combobox";
              break;
            case AcroFields.FIELD_TYPE_LIST:
              fieldType = "List";
              break;
            case AcroFields.FIELD_TYPE_NONE:
              fieldType = "None";
              break;
            case AcroFields.FIELD_TYPE_PUSHBUTTON:
              fieldType = "Pushbotton";
              break;
            case AcroFields.FIELD_TYPE_RADIOBUTTON:
              fieldType = "Radiobutton";
              break;
            case AcroFields.FIELD_TYPE_SIGNATURE:
              fieldType = "Signature";
              break;
            case AcroFields.FIELD_TYPE_TEXT:
              fieldType = "Text";
              break;
            default:
              fieldType = "Undefined";
          }

          if (map.get(key) != null) {
            if (form.setField(key, map.get(key).toString())) {
              cat.debug("setField: "+ key + " value: " + map.get(key));
            }
          } else {
            throw new Exception("Unmapped PDF key: " + key + " Type: " + fieldType);
          }
        }
        stamp.close();          
                      
      }
      catch(DocumentException de) {
      de.printStackTrace();
      cat.debug(this.getClass().getName() + ": document: " + de.getMessage());
    }
//--------------------------------------------------------------------------------
// step 6: We close the document (the outputstream is also closed internally)
//--------------------------------------------------------------------------------
    document.close();
    }
    catch (SQLException e) {
      cat.error(this.getClass().getName() + ": SQLException : " + e.getMessage());
    }
    finally {
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

  private Map<String, String> setFormMapping(Connection conn,
                             Integer poKey,
                             Integer attKey,
                             Map<String, String> cbMap)
    throws Exception, SQLException {

    PreparedStatement ps = null;
    ResultSet rs = null;
    Byte sourceTypeKey = null;
    Integer sourceKey = null;
    String fieldName = "";
    String lookupCode = "";
    String vofValue = "";

    try {
      String query = "SELECT sourcetype_key, "
                   + "source_key, "
                   + "field_name, "
                   + "lookup_code "
                   + "FROM configvendororderform "
                   + "WHERE attachment_key = ?";
      ps = conn.prepareStatement(query);
      ps.setInt(1, attKey);
      rs = ps.executeQuery();
      while (rs.next()) {
        sourceTypeKey = rs.getByte("sourcetype_key");
        sourceKey = rs.getInt("source_key");
        fieldName = rs.getString("field_name");
        lookupCode = rs.getString("lookup_code");

        vofValue = getPurchaseOrderService().getVendorOrderFormDetail(conn,
                                                                      poKey,
                                                                      sourceTypeKey,
                                                                      sourceKey,
                                                                      lookupCode);
        if (vofValue.equalsIgnoreCase("Not Found")) {
          throw new Exception("Missing vendorOrderFormCode " + lookupCode + " in GetVendorOrderFormDetail() stored procedure.");
        } else {
//          if (vofValue.equalsIgnoreCase("On")) {
//            cat.debug(this.getClass().getName() + ": fieldName:" + fieldName + " value:" + vofValue);
//          }
          cbMap.put(fieldName, vofValue);
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
    }
    return cbMap;
  }

    @Override
  public ActionForward execute(ActionMapping mapping,
		               ActionForm form,
		               HttpServletRequest request,
		               HttpServletResponse response)
   throws Exception, IOException, SQLException {

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
// Purchase order key for the order.
//--------------------------------------------------------------------------------
      Integer theKey = null;
      if (request.getParameter("key") != null) {
        theKey = new Integer(request.getParameter("key"));
      }
      else {
        throw new Exception("Request getParameter [poKey] not found!");
      }

      doReportGenerator(request,
                        response,
                        theKey);
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
