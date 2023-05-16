/*
 * AntechLabResultsGetAction.java
 *
 * Created on January 16, 2009, 9:36 AM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package com.wolmerica.vendorresult;

//--------------------------------------------------------------------------------
// @author Richard Wolschlager
//--------------------------------------------------------------------------------
import com.wolmerica.employee.EmployeesActionMapping;
import com.wolmerica.service.attachment.AttachmentService;
import com.wolmerica.service.attachment.DefaultAttachmentService;
import com.wolmerica.service.property.PropertyService;
import com.wolmerica.service.property.DefaultPropertyService;
import com.wolmerica.service.userstate.UserStateService;
import com.wolmerica.service.userstate.DefaultUserStateService;
import com.zoasis.service.zoasislab.ZoasisLabService;
import com.zoasis.service.zoasislab.DefaultZoasisLabService;

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
import java.sql.SQLException;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;

// Web Services Stuff
import javax.xml.rpc.ServiceException;
//import com.zoasis.www.service.services.FinderException;
import zoasis.ZoasisServicesPort;
import zoasis.ws.datamodel.general.LoginObject;

import zoasis.ws.datamodel.laborders.LabOrderCode;
import zoasis.ws.datamodel.labresult.LabAccessionIdObject;
import zoasis.ws.datamodel.labresult.LabResultObject;
import zoasis.ws.datamodel.labresult.LabResults;
import zoasis.ws.datamodel.general.Acknowlegement;
import zoasis.ws.datamodel.general.TypeOfService;

import org.apache.log4j.Logger;

public class AntechLabResultsGetAction extends Action {

  Logger cat = Logger.getLogger("WOWAPP");

  private AttachmentService attachmentService = new DefaultAttachmentService();
  private PropertyService propertyService = new DefaultPropertyService();
  private UserStateService userStateService = new DefaultUserStateService();
  private ZoasisLabService zoasisLabService = new DefaultZoasisLabService();

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

  public UserStateService getUserStateService() {
      return userStateService;
  }

  public void setUserStateService(UserStateService userStateService) {
      this.userStateService = userStateService;
  }

  public ZoasisLabService getZoasisLabService() {
      return zoasisLabService;
  }

  public void setZoasisLabService(ZoasisLabService zoasisLabService) {
      this.zoasisLabService = zoasisLabService;
  }
  

  private HashMap<String, String> getAllLabResults(HttpServletRequest request,
                                   Byte sourceTypeKey,
                                   Integer sourceKey)
   throws Exception {

    HashMap<String, String> labMap = new HashMap<String, String>();
    String xmlFileName = "";

    try {
      boolean isUpdate = false;
      File fileExist = null;
      int zoasisId = 0;
      String labResultXML = "";
      String xmlLine = getPropertyService().getCustomerProperties(request,"webservice.antech.xmlline");
      String pathSymbol = getPropertyService().getCustomerProperties(request, "fileupload.separator.symbol");
      String outputPath = getAttachmentService().getAttachmentPath(request, sourceTypeKey, sourceKey);

      cat.debug("xmlLine = " + xmlLine);
      cat.debug("outputPath = " + outputPath);
      cat.debug("Before call to getInitializedZoasisPort(request)");

      ZoasisServicesPort zsp = getZoasisLabService().getInitializedZoasisPort(request);
      LoginObject loginObj = getZoasisLabService().getLoginObject(request);

      cat.debug("After call to getInitializedZoasisPort(request)");
      LabResults labResult = new LabResults();
      LabResults labResults[] = null;
      labResults = (LabResults[]) zsp.getAllLabResults(loginObj);
      Acknowlegement acknowlege = new Acknowlegement();
      Acknowlegement acknowlegeResults[] = null;

      Integer attachmentKey = null;
      int ackSize = 0;
      int size = labResults.length;
      for (int i=0; i<size; i++)
      {
        labResult = labResults[i];
        cat.debug("ZoasisId..: " + labResult.getZoasisId());
        cat.debug("LabResults: " + labResult.getLabResults());

        zoasisId = labResult.getZoasisId();
        labResultXML = labResult.getLabResults();
        if (labResultXML.indexOf(xmlLine) != -1) { //true xml file
          xmlFileName = getXmlFilename(outputPath, pathSymbol, zoasisId);
          writeToFile(xmlFileName, labResultXML);
          fileExist = new File(xmlFileName);
          if (fileExist.exists()) {
            isUpdate = true;
            cat.debug("call saveAsAttachment()");
            attachmentKey = saveAsAttachment(request,
                                             sourceTypeKey,
                                             sourceKey,
                                             fileExist);
            cat.debug("saveAsAttachment() key.: " + attachmentKey);
          }

          if (isUpdate) {
            acknowlegeResults = labResult.getAcknowlegements();
            ackSize = acknowlegeResults.length;
            for (int j=0; j<size; j++) {
              acknowlege = acknowlegeResults[j];
              cat.debug("acknowleg Cnt: " + j);
              cat.debug("ExtraField1..: " + acknowlege.getExtraField1());
              cat.debug("ExtraField2..: " + acknowlege.getExtraField2());
              cat.debug("ServiceType..: " + acknowlege.getServiceType());
              cat.debug("TimeStamp....: " + acknowlege.getTimeStamp());
              cat.debug("ZoasisRowId..: " + acknowlege.getZoasisRowId());
//------------------------------------------------------------------------------
//              allUpdates.add(new Acknowlegement("", "", "labresult",
//                                                acknowlege.getTimeStamp(),
//                                                acknowlege.getZoasisRowId()));
//------------------------------------------------------------------------------
            }
            cat.debug("acknowledgeServices()..: " + acknowlegeResults.length);
            zsp.acknowledgeServices(loginObj, acknowlegeResults);
          }
        } else {
          xmlFileName = "NoLabResultsToDownload";
        }
      }
    }
    catch (Exception e) {
      String uploadStatus = getZoasisLabService().exceptionFilter(request,
                                                                  e.toString());
      cat.debug("uploadStatus..: " + uploadStatus);
      labMap.put("resultUploadMessage", uploadStatus);
      if (uploadStatus.length() == 0)
        throw new Exception(e);
    }
    labMap.put("xmlFileName", xmlFileName);

    return labMap;
  }

  private void getLabResults(HttpServletRequest request,
                             String labAccessionId)
   throws Exception, ServiceException {

    try {
      ZoasisServicesPort zsp = getZoasisLabService().getInitializedZoasisPort(request);

      LoginObject loginObj = getZoasisLabService().getLoginObject(request);
      LabAccessionIdObject labObj = new LabAccessionIdObject();
      labObj.setLabAccessionId(labAccessionId);

      LabResultObject labResultObj = new LabResultObject();
      labResultObj = zsp.getLabResults(loginObj, labObj);

      cat.debug("Acc Receive Date: " + labResultObj.getAccessionLatestReceivedDate());
      cat.debug("Acc Result Id...: " + labResultObj.getAccessionResultId());
      cat.debug("Antech Acc Id...: " + labResultObj.getAntechAccessionId());
      cat.debug("Error Message...: " + labResultObj.getErrorMessage());
      cat.debug("Lab Name........: " + labResultObj.getLabName());
      cat.debug("Lab Results.....: " + labResultObj.getLabResults());
      cat.debug("Order Status....: " + labResultObj.getOrderStatus());
      cat.debug("Result Reported.: " + labResultObj.getResultReportedDate());
    }
    catch (ServiceException se) {
      cat.error(this.getClass().getName() + ": Exception : " + se);
      throw new Exception(se.getMessage());
    }
  }

  private void getTypeOfServices(HttpServletRequest request)
   throws Exception, ServiceException {

    try {
      ZoasisServicesPort zsp = getZoasisLabService().getInitializedZoasisPort(request);

      LoginObject loginObj = getZoasisLabService().getLoginObject(request);

      TypeOfService typeOfService = new TypeOfService();
      TypeOfService typeOfServices[] = null;
      typeOfServices = (TypeOfService[])zsp.getTypeOfServices(loginObj);

      int size = typeOfServices.length;
      for (int i=0; i<size; i++)
      {
        typeOfService = typeOfServices[i];
        cat.debug("Service Type..:" + typeOfService.getServiceType());

      }
    }
    catch (ServiceException se) {
      cat.error(this.getClass().getName() + ": Exception : " + se);
      throw new Exception(se.getMessage());
    }
  }

  private void validateLabOrderCode(HttpServletRequest request)
   throws Exception, ServiceException {

    try {
      ZoasisServicesPort zsp = getZoasisLabService().getInitializedZoasisPort(request);
      LoginObject loginObj = getZoasisLabService().getLoginObject(request);

      LabOrderCode labOrderCode = new LabOrderCode();
      labOrderCode.setLabId("1");
      labOrderCode.setOrderCode("90200");
      String validateResponse = (String)zsp.validateLabOrderCode(loginObj, labOrderCode);

      cat.debug("validateLabOrderCode.: " + validateResponse);
    }
    catch (ServiceException se) {
      cat.error(this.getClass().getName() + ": Exception : " + se);
      throw new Exception(se.getMessage());
    }
  }

  private Integer saveAsAttachment(HttpServletRequest request,
                                   Byte sourceTypeKey,
                                   Integer sourceKey,
                                   File theFile)
   throws Exception, SQLException {
    cat.debug("Start saveAsAttachment() " + theFile.getAbsolutePath());
    DataSource ds = null;
    Connection conn = null;
    Integer attachmentKey = null;
    try {
      ds = getDataSource(request);
      conn = ds.getConnection();
      String fileName = theFile.getName();
      String contentType = "text/xml";
      Integer fileSize = Integer.valueOf((int)theFile.length());
      String userName = request.getSession().getAttribute("USERNAME").toString();

      attachmentKey = getAttachmentService().insertAttachmentRow(conn,
                                             sourceTypeKey,
                                             sourceKey,
                                             fileName,
                                             contentType,
                                             fileSize,
                                             userName);
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
    cat.debug("Finish saveAsAttachment() [" + attachmentKey + "]");
    return attachmentKey;
  }

  private void writeToFile(String xmlFileName,
                           String labResultXML)
   throws Exception, IOException {

    try {
      BufferedWriter out = new BufferedWriter(new FileWriter(xmlFileName));
      out.write(labResultXML);
      out.close();
    } catch (IOException e) {
      cat.error(this.getClass().getName() + ": Exception : " + e);
    }
  }

  private String getXmlFilename(String outputPath,
                                String pathSymbol,
                                int zoasisId)
   throws Exception, IOException {
    cat.debug("Start getXmlFilename() [" + outputPath + "] [" + pathSymbol + "]");
    Calendar calendar = new GregorianCalendar();
    Date date = calendar.getTime();
    DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
    dateFormat.setLenient(false);
    String dateString = dateFormat.format(date);
    String xmlFileName = outputPath + pathSymbol + "lab_" + zoasisId + "_" + dateString + ".xml";
    cat.debug("Finish getXmlFilename() [" + xmlFileName + "]");
    return xmlFileName;
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
// Forward to the appropriate View
//--------------------------------------------------------------------------------
        return (mapping.findForward(target));
      }
    }

    try {
//--------------------------------------------------------------------------------
// Requires the vendor key value for attachments.
//--------------------------------------------------------------------------------
      Integer vendorKey = null;
      if (!(request.getParameter("vendorKey") == null)) {
        vendorKey = new Integer(request.getParameter("vendorKey"));
      }
      else {
        throw new Exception("Request getParameter [vendorKey] not found!");
      }

//--------------------------------------------------------------------------------
// Requires the source type key value for attachments.
//--------------------------------------------------------------------------------
      Byte sourceTypeKey = null;
      if (!(request.getParameter("sourceTypeKey") == null)) {
        sourceTypeKey = new Byte(request.getParameter("sourceTypeKey"));
      }
      else {
        throw new Exception("Request getParameter [sourceTypeKey] not found!");
      }

//--------------------------------------------------------------------------------
// 07-23-06  Code to handle permissions and avoid simultaneous updates to records.
//--------------------------------------------------------------------------------
      
      String usToken = getUserStateService().getUserToken(request,
                                              this.getDataSource(request).getConnection(),
                                              this.getClass().getName(),
                                              vendorKey);
      if (usToken.equalsIgnoreCase(getUserStateService().getLocked()))
        request.setAttribute(getUserStateService().getDisableEdit(), false);
      else if (usToken.equalsIgnoreCase(getUserStateService().getProhibited()))
        throw new Exception(getUserStateService().getAccessDenied());

      HashMap labMap = getAllLabResults(request,
                                        sourceTypeKey,
                                        vendorKey);
//      String xmlFileName = "C:/antech/labresults/lab_98_20100208152950.xml";
//      getLabResults(request, "012010001");
//      getTypeOfServices(request);
//      validateLabOrderCode(request);
      if (labMap.containsKey("xmlFileName")) {
        cat.debug("AntechLabResultsGet file.: " + labMap.get("xmlFileName").toString());
        request.setAttribute("xmlFileName", labMap.get("xmlFileName").toString());
      }

      if (labMap.containsKey("resultUploadMessage")) {
        cat.debug("AntechLabResultsGet..: " + labMap.get("resultUploadMessage").toString());
        request.setAttribute("resultUploadMessage", labMap.get("resultUploadMessage").toString());
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